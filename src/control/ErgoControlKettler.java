package control;

import javax.comm.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Timer;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import net.ErgoControlClient;

import org.apache.log4j.Logger;

import tools.ErgoPhysics;

import com.centralnexus.input.Joystick;

import data.ErgoBikeDefinition;
import data.ErgoData;
import data.ErgoDatastore;
import data.GPSPosition;



/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 13.02.2005
 * Time: 16:20:09
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlKettler extends ErgoControlInterface {
    protected static Logger log = Logger.getLogger(ErgoControlKettler.class);

    protected BufferedReader myReader = null;
    protected BufferedWriter myWriter = null;
    protected String myLastCommand = "";
//    protected boolean isGT = false;

   public ErgoControlKettler(CommPortIdentifier portId)
    {
        super(portId);
    }

    public void init(ErgoDatastore newProgram, ErgoBikeDefinition ergoBike, int refreshRate, int sampleRate, ErgoControlClient newClient)
    {
//		isGT = false;

		try
        {
        	super.init(newProgram, ergoBike, refreshRate, sampleRate, newClient);

            log.debug("try to start thread");
            //if (myErgoControlThread == null)
            if (myErgoControlTimer == null)
            {
            	if(myProgram != null && (myProgram.getRefDataIndex() == ErgoDatastore.GRADE || myProgram.getRefDataIndex() == ErgoDatastore.SPEED))
            		myPhysics = new ErgoPhysics(ergoBike, true);
            	else
            		myPhysics = new ErgoPhysics(ergoBike, false);

            	/*log.debug("create new thread");
                myErgoControlThread = new Thread(this);*/
            	myErgoControlTimer = new Timer();

                if(myPortId != null)
                {
	                myPort = (SerialPort)myPortId.open("ergo", 1000);
	                if(myPort == null)
	                {
	                    log.debug("failed to open port " + myPortId.getName());
	                    stop(true);
	                    return;
	                }
	                myPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	                //myPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT );
	                //myPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN | SerialPort.FLOWCONTROL_XONXOFF_OUT);
	                myPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
	                myPort.enableReceiveTimeout(80);

	                log.debug("open writer");
	                myWriter = new BufferedWriter(new OutputStreamWriter(myPort.getOutputStream()));
	                log.debug("open reader");
	                myReader = new BufferedReader(new InputStreamReader(myPort.getInputStream()));
                }

                //myErgoControlThread.start();
                myErgoControlTimer.schedule(this, 0, myRefreshRate);

//				write("KI");
                write("RS");
                write("CM");
/*                if(isGT)
                	write("BR38400");
*/                write("PW 25");
            }
            else log.debug("thread already started");
        } catch(Exception ex)
        {
            stop(true);

            log.error(ex);
        }
    }

    public void start()
    {
        if(myProgram != null)
        	write("PW " + myPower);
        else
            write("ST");
    }

    public void stop(boolean bFailure)
    {
        log.debug("call stop from super class");
        super.stop(bFailure);

        log.debug("release reader");
        try
        {
            if(myReader != null)
            {
                myReader.close();
                myReader = null;
            }
        } catch(Exception e)
        {
            log.error(e);
        }
        log.debug("release writer");
        try
        {
            if(myWriter != null)
            {
                myWriter.close();
                myWriter = null;
            }
        } catch(Exception e)
        {
            log.error(e);
        }
    }

    public synchronized void run()
    {
    	myNowSample += myRefreshRate;
    	SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        try
        {
        	double lastGrade = 0;
            //String nowHours = "00";
            //myLastSample = 0;
            //myLastTime = 0;
            //myLastRecoveryTime = 0;

            //while(myErgoControlThread != null)
            {
            	log.debug("check if something has written");
            	if(myHasWritten)
                {
            		log.debug("process data");
                	myHasWritten = false;
                    String input = null;

                    if(myPortId != null)
                    {
		                try
		                {
		                    input = myReader.readLine();
		                } catch(Exception e)
		                {
		                    log.error("communication failed! resend command");
		                    write(null);
		                }
                    }
                    else
                    	input = mySimInput;

                    if(input != null)
                    {
                        if(input.length() == 0)
                        {
                            log.error("communication failed! (empty) resend command");
                            write(null);
                        }
                        else
                        {
                            myHasReceived = true;
                            log.debug("received: " + input);
/*                            if(input.startsWith("SD2SErgoRacer GT") && myLastCommand.equals("KI"))
                            {
								log.debug("ErgoRacer GT found!");
								isGT = true;
							}
							else if(myLastCommand.equals("KI"))
							{
								log.debug("ErgoRacer GT not found! returned " + input);
								isGT = false;
							}
							else if(myLastCommand.equals("BR38400") || myLastCommand.equals("SI4711"))
							{
								log.debug("ignore input: " + input);
							}
							else*/ if(input.toUpperCase().startsWith("ACK"))
                                log.debug("communication ok!");
                            else if(input.toUpperCase().startsWith("ERROR"))
                            {
                                log.error("communication failed!");
                                stop(true);
                            }
                            else if(myIsInitialized == true)
                            {
                                int nowPuls = 0;
                                int nowRPM = 0;
                                double nowSpeed = 0;
                                double nowDistance = 0;
                                int nowPower = 0;
                                int nowKilojoule = 0;
                                double nowHeight = 0;
                                double nowBearing = 0;
                                double nowGrade = lastGrade;
                                String nowTime = null;
                                String nowFullTime = null;
                                int nowPower2 = 0;
                                boolean bIsRecovery = false;
                                int tokenPos = 0;
                                long time = 0;
                                GPSPosition nowPos = null;

                                if(myStartSample == 0)
                                	myStartSample = myNowSample;

                                /*if(myLastRefresh != 0)
                                {
                                    long nowRefresh = 0;
                                    do
                                    {
                                        nowRefresh = System.currentTimeMillis();
                                    }
                                    while((nowRefresh - myLastRefresh) < myRefreshRate);
                                    myLastRefresh = System.currentTimeMillis();
                                }
                                else
                                    myLastRefresh = System.currentTimeMillis();*/

                                log.debug("receive status");
                                StringTokenizer tokenizer = new StringTokenizer(input, "\t", false);
                                while(tokenizer.hasMoreTokens())
                                {
                                    String token = tokenizer.nextToken();
                                    String display = "unknown";
                                    int cut = token.indexOf("\r\n");

                                    if(cut >= 0)
                                    	token = token.substring(0,cut);

                                    switch(tokenPos)
                                    {
                                        case 0: //Puls
                                            display = "Puls";
                                            nowPuls = Integer.parseInt(token);
                                            break;
                                        case 1: //RPM
                                            display = "RPM";
                                            nowRPM = Integer.parseInt(token);
                                            break;
                                        case 2: //Speed
                                            display = "Speed";
                                            nowSpeed = Double.parseDouble(token) / 10;
                                            break;
                                        case 3: //Distance
                                            display = "Distance";
                                            nowDistance = Double.parseDouble(token) / 10;
                                            break;
                                        case 4: //Power
                                            display = "Power";
                                            nowPower = Integer.parseInt(token);
                                            break;
                                        case 5: //Kilojoule
                                            display = "Kilojoule";
                                            nowKilojoule = Integer.parseInt(token);
                                            break;
                                        case 6: //Time
                                            display = "Time";

                                            /*if((nowSample - myLastSample) > -3599000)
                                            {
                                                int hours = Integer.parseInt(nowHours) + 1;
                                                if(hours < 10)
                                                    nowHours = "0" + Integer.toString(hours);
                                                else
                                                    nowHours = Integer.toString(hours);
                                            }*/

                                            //myLastTime = nowSample - myStartSample - 3599000;
                                            time = myNowSample - myStartSample;;
                                            nowTime = timeFormat.format(new Date(time - 3599000));
                                            nowFullTime = (new SimpleDateFormat("HH")).format(new Date(time - 3599000)) + ":" + nowTime;


                                            if(token.indexOf(';') > -1)
                                            {
                                            	bIsRecovery = true;

                                            	if(token.equals("00;00"))
                                            	{
                                                    stop(false);
                                                    return;
                                            	}

                                                //long now = (new SimpleDateFormat("mm;ss")).parse(token).getTime();


                                                //token.replace(';', ':');

                                                //nowTime = (new SimpleDateFormat("mm:ss")).format((new Date(myLastTime + Math.abs(now) - 3540000)));
                                                //nowFullTime = nowHours + ":" + nowTime;*/

                                                /*if(myLastRecoveryTime == 60 && now == 0)
                                                    return;

                                                myLastRecoveryTime = (new SimpleDateFormat("mm:ss")).parse(nowTime).getTime();*/
                                            }
                                            break;
                                        case 7: //last Power ?!?
                                            display = "Real Power";
                                            nowPower2 = Integer.parseInt(token);
                                            break;
                                    }
                                    log.debug(display + ": " + token);
                                    tokenPos++;
                                }

                                /*try
                                {
                                    if(nowTime != null)
                                        nowSample = (new SimpleDateFormat("mm:ss")).parse(nowTime).getTime();
                                } catch(Exception e)
                                {
                                    log.error(e);
                                }*/

                                if(myProgram != null)
                                {
                                    if(!bIsRecovery)
                                    {
                                        //if(myProgramEnum.hasMoreElements())
                                    	if(myProgramPos + 1 < myProgram.size())
                                        {
                                        	ErgoData data = null;

                                            //if(myErgoDatastore != null)
                                            //{
                                            if(!myProgram.getUseDistance())
                                            {
                                                if(Math.abs(myNowSample - myLastSample) >= mySampleRate)
                                                	myProgramPos++;
                                                	//data = (ErgoData)myProgramEnum.nextElement();
                                                data = myProgram.get(myProgramPos);
                                            }
                                            else
                                            {
                                            	do
                                            	{
                                            		data = myProgram.get(myProgramPos);
                                            		if(data.getDistance() < myPhysics.getDistance())
                                            		{
                                            			log.debug("Dist1 " + myPhysics.getDistance() + " Dist2 " + data.getDistance());
                                            			myProgramPos++;
                                            		}
                                            	}
                                            	while(data.getDistance() < myPhysics.getDistance());
                                            }
                                            /*}
                                            else
                                                bSet = true;*/

                                            if(data != null)
                                            {
//TODO: Configuration for threshold has to be made
/////////////////////////////////////////////////////////////////////////////////
                                                if(data.getSpeed() != 0 && myProgram.getRefDataIndex() == ErgoDatastore.SPEED)
                                                {
													myPhysics.calc(time, myPower, 0, nowRPM);
													nowSpeed = myPhysics.getVelocity();

                                                    if(data.getSpeed()-nowSpeed > 1)
                                                        myPower = nowPower - 5;
                                                    else if(data.getSpeed()-nowSpeed < -1)
                                                        myPower = nowPower + 5;
                                                }
                                                if(data.getRPM() != 0 && myProgram.getRefDataIndex() == ErgoDatastore.RPM)
                                                {
                                                    if(data.getRPM()-nowRPM > 5)
                                                        myPower = nowPower - 5;
                                                    else if(data.getRPM()-nowRPM < -5)
                                                        myPower = nowPower + 5;
                                                }
                                                if(data.getPuls() != 0 && myProgram.getRefDataIndex() == ErgoDatastore.PULS)
                                                {
													int sampleBuffer = (int)(Math.pow(1.0/Math.abs((data.getPuls()-nowPuls)*0.5),2) * 1000000);
													System.out.println("sb2: " + sampleBuffer);
													if(data.getPuls()-nowPuls >= 5 && (myNowSample - myLastAdjustSample) > sampleBuffer)
                                                    {
                                                        myPower = nowPower + 5;
                                                        myLastAdjustSample = myNowSample;
													}
                                                    else if(data.getPuls()-nowPuls <= -5 && (myNowSample - myLastAdjustSample) > sampleBuffer)
                                                    {
                                                        myPower = nowPower - 5;
                                                        myLastAdjustSample = myNowSample;
													}

                                                }
                                                if(myProgram.getRefDataIndex() == ErgoDatastore.GRADE)
                                                {
                                                	nowGrade = data.getGrade();
                                                	nowHeight = data.getHeight();
                                                	nowBearing = data.getBearing();
                                                }
                                                if(data.getPower() != 0 && myProgram.getRefDataIndex() == ErgoDatastore.POWER)
                                                    myPower = data.getPower();

                                                nowPos = data.getPosition();
                                            }
                                        }
                                        else
                                            stop(false);
                                    }
                                    else
                                    {
                                        myPower = 25;
                                        myProgram = null;
                                    }

                                    if(myPower < 25)
                                    	myPower = 25;

                                    if(myProgram != null && myProgram.getRefDataIndex() == ErgoDatastore.GRADE)
                                    {
                                    	/*if((nowPower - 5) == myLastPower)
                                    		myPhysics.getBike().shiftGearUp();
                                    	if((nowPower + 5) == myLastPower || nowPower == 600 && myLastPower == 25)
                                    		myPhysics.getBike().shiftGearDown();

                                    	if((nowPower - 10) >= myLastPower)
                                    	{
                                    		log.debug("ring up");
                                    		myPhysics.getBike().shiftRingUp();
                                    	}
                                    	if((nowPower + 10) <= myLastPower)
                                    	{
                                    		log.debug("ring down");
                                    		myPhysics.getBike().shiftRingDown();
                                    	}*/
                                    	myPower = nowPower = myLastPower;

                                    	myPhysics.calc(time, nowPower2, nowGrade, nowRPM);
                                    }
                                    else
                                    	myPhysics.calc(time, myPower, 0, nowRPM);
                                }
                                else
                                	myPhysics.calc(time, nowPower2, 0, nowRPM);

                                if(myProgram != null && (myProgram.getRefDataIndex() != ErgoDatastore.SPEED && myProgram.getRefDataIndex() != ErgoDatastore.RPM && myProgram.getRefDataIndex() != ErgoDatastore.PULS))
                                	myPower = myPhysics.getPower();
                            	nowDistance = myPhysics.getDistance();
                                nowSpeed = myPhysics.getVelocity();

                                myErgoData = new ErgoData(nowPos, nowPuls, nowRPM, nowSpeed, nowDistance, nowPower2, nowKilojoule, nowHeight, nowGrade, nowBearing, nowFullTime, bIsRecovery, myPort == null);

                                if(myErgoDatastore != null)
                                {
                                	if(Math.abs(myNowSample - myLastSample) >= mySampleRate)
                                	/*int recSample = (int)Math.floor(time/1000.0);
                                	log.debug("RecSample " + recSample + " LastRecSample " + myLastRecSample);
                                	log.debug("SampleRate " + Math.floor(mySampleRate/1000.0));
                                	log.debug("Modulo " + recSample % Math.floor(mySampleRate/1000.0));
                                	if(recSample % Math.floor(mySampleRate/1000.0) == 0 && recSample != myLastRecSample)*/
                                	{
                                		log.debug("rec...");
                                        myErgoDatastore.add(myErgoData);

                                       	myLastSample = myNowSample;
                                       	//myLastRecSample = recSample;
                                    }
                                }

                                Hashtable<String,ErgoData> dataArray = null;
                                if(myClient != null)
                                {
                                	log.debug("dispatch net data");
                                	dataArray = myClient.send(myErgoData);

                                	if(dataArray != null)
                                	{
                                		log.debug("process other client data");
	                                	Enumeration<String> enumDataArray = dataArray.keys();
	                                	while(enumDataArray.hasMoreElements())
	                                	{
	                                		String key = enumDataArray.nextElement();
	                                		ErgoData data = dataArray.get(key);
	                                		log.debug("key: " + key + " data: " + data.getDistance());
	                                	}
                                	}
                                }

                                log.debug("fireDataAvailable...");
                                fireDataAvailable(new ErgoControlEvent(this), myErgoData, myPhysics.getBike(), dataArray);

                                if(myPower == 0)
                                	myPower = myLastPower;

                                if(myProgram != null)
                                {
                                	log.debug("write PW " + myPower);
                                    write("PW " + myPower);
                                }
                                else
                                {
                                    log.debug("write ST");
                                    write("ST");
                                }

                                lastGrade = nowGrade;
                            }
                            else if(input.length() >= 34)
                            {
                                log.error("input length " + input.length());
                                myIsInitialized = true;
                            }
                            else
                            {
                                log.error("communication failed! (crippled)  resend command");
                                write(null);
                            }
                        }
                    }
                }
                //wait(10);
            }
        } catch(Exception ex)
        {
            myHasReceived = true;
            stop(true);
            log.error(ex);
        }
    }

    protected void write(String command)
    {
    	if(command == null)
    		log.debug("wait to communicate with ergoracer: " + myLastCommand);
    	else
    		log.debug("wait to communicate with ergoracer: " + command);
        while(!myHasReceived && command != null)
        {
        	try
        	{
        		Thread.sleep(50);
        	}
        	catch(Exception ex)
        	{

        	}
        }
        log.debug("waited");
        myHasReceived = false;
        try
        {
            //if(myErgoControlThread != null)
        	if(myErgoControlTimer != null)
            {
                if(command == null)
                    command = myLastCommand;
                else
                    myLastCommand = command;

                if(myPortId != null)
                {
                	if(command.startsWith("PW"))
            			myLastPower = Integer.parseInt(command.substring(3,command.length()));
	                myWriter.write(command + "\r\n");
	                myWriter.flush();
                }
                else
                {
                	if(command.startsWith("RS") || command.startsWith("CM"))
                	{
                		mySimInput = "ACK";
                		mySimTime = 0;
                		mySimDistance = 0;
                	}
                	else
                	{
                		if(command.startsWith("PW"))
                			mySimPower = Integer.parseInt(command.substring(3,command.length()));

                		SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                		DecimalFormat numFormat = new DecimalFormat("000");
                		mySimInput = numFormat.format(mySimHR)+ "\t" + numFormat.format(mySimRPM) + "\t300\t" + numFormat.format(mySimDistance) + "\t" + numFormat.format(mySimPower) + "\t0020\t" + timeFormat.format(new Date(mySimTime)) + "\t" + numFormat.format(mySimPower) + "\r\n";
                		mySimDistance += 0.08333;
                		mySimTime += 1000;
                	}
                }
                myHasWritten = true;
/*                if(command.startsWith("PW") && isGT)
                	write("SI4711");
*/            }
        } catch(Exception ex)
        {
            stop(true);
            log.error(ex);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent e)
    {
    	if( e.getKeyCode() == KeyEvent.VK_SHIFT) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		myPhysics.getBike().shiftGearUp();
            	    break;
            }
        }

    	if( e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		myPhysics.getBike().shiftGearDown();
            	    break;
            }
        }

    	if( e.getKeyCode() == KeyEvent.VK_CONTROL) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		mySimRPM -= 5;
            	    break;
            }
        }

    	if( e.getKeyCode() == KeyEvent.VK_ALT) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		mySimRPM += 5;
            	    break;
            }
        }

    	if( e.getKeyCode() == KeyEvent.VK_Y) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		mySimPower -= 5;
            	    break;
            }
        }

    	if( e.getKeyCode() == KeyEvent.VK_A) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		mySimPower += 5;
            	    break;
            }
        }
    	if( e.getKeyCode() == KeyEvent.VK_X) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		mySimHR -= 5;
            	    break;
            }
        }

    	if( e.getKeyCode() == KeyEvent.VK_S) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		mySimHR += 5;
            	    break;
            }
        }

        return false;
    }

    public void joystickButtonChanged(Joystick evt)
    {
    	if((evt.getButtons() & Joystick.BUTTON1) != 0)
    		myPhysics.getBike().shiftGearDown();

    	if((evt.getButtons() & Joystick.BUTTON2) != 0)
    		myPhysics.getBike().shiftGearUp();
	}
}
