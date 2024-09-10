package control;

import net.ErgoControlClient;

import org.apache.log4j.Logger;

import tools.ErgoPhysics;
import tools.ErgoTools;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;

import data.ErgoBikeDefinition;
import data.ErgoData;
import data.ErgoDatastore;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.event.EventListenerList;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 02.03.2005
 * Time: 12:34:07
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlInterface extends TimerTask implements Runnable, KeyEventDispatcher, JoystickListener  {
    protected static Logger log = Logger.getLogger(ErgoControlInterface.class);

    protected EventListenerList myListenerList = new EventListenerList();

    protected CommPortIdentifier myPortId = null;
    protected SerialPort myPort = null;

    protected boolean myHasReceived = true;
    protected boolean myHasWritten = false;
    protected boolean myIsInitialized = false;

    //protected Thread myErgoControlThread = null;
    protected Timer myErgoControlTimer = null;

    protected int myPower = 0;
    protected int myLastPower = 0;
    protected ErgoData myErgoData = null;
    //protected long myLastRefresh = 0;
    protected int myRefreshRate = 0;

    protected ErgoDatastore myErgoDatastore = null;
    protected long myNowSample = 0;
    protected long myStartSample = 0;
    protected long myLastSample = 0;
    protected long myLastRecSample = 0;
    protected long myLastAdjustSample = 0;
    //protected long myLastTime = 0;
    protected long myLastRecoveryTime = 0;
    protected int mySampleRate = 0;

    //protected Enumeration myProgramEnum = null;
    protected ErgoDatastore myProgram = null;
    protected int myProgramPos = 0;

    protected String mySimInput = null;
    protected int mySimTime = 0;
    protected double mySimDistance = 0;
    protected int mySimPower = 25;
    protected int mySimRPM = 90;
    protected int mySimHR = 120;

    protected ErgoPhysics myPhysics = null;

    protected Joystick myJoystick = null;

    protected ErgoControlClient myClient = null;

    public ErgoControlInterface(CommPortIdentifier portId)
    {
    	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);

    	try
    	{
    		myJoystick = Joystick.createInstance();
    		myJoystick.addJoystickListener(this);
    	}
    	catch(Exception ex)
    	{
    		log.error(ex);
    	}


    	if(portId != null)
    		log.debug("set port to " + portId.getName());
    	else
    		log.debug("set port to simulation");
        myPortId = portId;
    }

    public void init(ErgoDatastore newProgram, ErgoBikeDefinition ergoBike, int refreshRate, int sampleRate, ErgoControlClient newClient)
    {
        myPower = 25;
        myLastPower = 25;
        myRefreshRate = refreshRate;
        mySampleRate = sampleRate;

        myProgram = null;
        //myProgramEnum = null;
        myProgramPos = 0;

        myClient = newClient;

        if(newProgram != null)
        {
            if(newProgram.getSampleRate() != 0)
                mySampleRate = newProgram.getSampleRate();
            myProgram = newProgram;
            //myProgramEnum = newProgram.elements();

            myErgoDatastore = new ErgoDatastore(newProgram.getName(), mySampleRate);
            myErgoDatastore.setUseDistance(newProgram.getUseDistance());
        }
        else
            myErgoDatastore = new ErgoDatastore(ErgoTools.DEFAULT_NAME, mySampleRate);
    }

    public void start()
    {

    }

    public void stop(boolean bFailure)
    {
    	if(myErgoControlTimer != null)
    	{
	    	log.debug("release Timer");
	    	myErgoControlTimer.cancel();
	    	myErgoControlTimer = null;
    	}

        //log.debug("release Thread");
        //myErgoControlThread = null;

        if(myPort != null)
        {
            log.debug("release port " + myPort.getName());
            myPort.close();
        }

        myProgram = null;

        log.debug("fireFinished...");
        fireFinished(new ErgoControlEvent(this), bFailure);
    }

    public synchronized void run()
    {
        log.error("no run method implemented!!!");
        stop(false);
    }

    public boolean isRunning()
    {
        //return (myErgoControlThread!=null);
    	return (myErgoControlTimer!=null);
    }

    public boolean hasReceived()
    {
        return myHasReceived;
    }

    public boolean hasInitalized()
    {
        return myIsInitialized;
    }

    public void setPower(int newPower)
    {
    	myPower = newPower;
    }

    public ErgoData getData()
    {
        return myErgoData;
    }

    public ErgoDatastore getDatastore()
    {
        return myErgoDatastore;
    }

    public ErgoDatastore getProgram()
    {
        return myProgram;
    }

    public void addErgoControlListener(ErgoControlListener listener)
    {
        myListenerList.add(ErgoControlListener.class,  listener);
    }

    public void removeErgoControlListener(ErgoControlListener listener)
    {
        myListenerList.remove(ErgoControlListener.class,  listener);
    }

    protected void fireDataAvailable(ErgoControlEvent evt, ErgoData data, ErgoBikeDefinition bike, Hashtable<String,ErgoData> dataArray)
    {
        Object[] listeners = myListenerList.getListenerList();

        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==ErgoControlListener.class) {
                ((ErgoControlListener)listeners[i+1]).dataAvailable(evt, data, bike, dataArray);
            }
        }
    }

    protected void fireFinished(ErgoControlEvent evt, boolean bFailure)
    {
        Object[] listeners = myListenerList.getListenerList();

        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==ErgoControlListener.class) {
                ((ErgoControlListener)listeners[i+1]).finished(evt, bFailure);
            }
        }

    }

    public boolean dispatchKeyEvent(KeyEvent e)
    {
        return false;
    }

	public void joystickAxisChanged(Joystick arg0) {
	}

	public void joystickButtonChanged(Joystick arg0) {
	}
}
