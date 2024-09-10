package data;


import java.util.Vector;
import java.util.Enumeration;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import tools.ErgoPhysics;
import tools.ErgoTools;

import forms.ErgoControlProgramSelect;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 17.02.2005
 * Time: 20:32:07
 * To change this template use File | Settings | File Templates.
 */
public class ErgoDatastore implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ErgoDatastore.class);
    private Vector<ErgoData> myErgoDatas = new Vector<ErgoData>();
    private int mySampleRate = 0;
    private String myName = "";

    private long myStartTime = 0;

    private static String HEADER_TIME = "Zeit";
    private static String HEADER_POWER = "P [Watt]";
    private static String HEADER_PULS = "Puls [1/min]";
    private static String HEADER_RPM = "RPM [1/min]";
    private static String HEADER_SPEED = "Tempo [1/min]";
    private static String HEADER_DISTANCE = "Strecke [km]";
    private static String HEADER_KILOJOULE = "K [Kilojoule]";

    private static String HEADER2_TIME = "Zeit min";
	private static String HEADER2_POWER = "Leistung W";
	private static String HEADER2_PULS = "Puls 1/min";
	private static String HEADER2_RPM = "Drehzahl 1/min";
	private static String HEADER2_SPEED = "Geschwindigkeit km/h";
	private static String HEADER2_DISTANCE = "Weg km";
    private static String HEADER2_KILOJOULE = "Energie kCal";

    //additional
    private static String HEADER_HEIGHT = "Hoehe [m]";
    private static String HEADER_GRADE = "Steigung [%]";
    private static String HEADER_BEARING = "Richtung [Grad]";

    private static String FILE_HEADER = HEADER_TIME + ";" + HEADER_POWER + ";" + HEADER_PULS + ";" +
            HEADER_RPM + ";" + HEADER_SPEED + ";" + HEADER_KILOJOULE + ";" + HEADER_HEIGHT + ";" + HEADER_GRADE + ";" + HEADER_BEARING;

    private boolean myExportedPPP = false;

    public static final int PULS = 1;
    public static final int RPM = 2;
    public static final int SPEED = 3;
    public static final int GRADE = 4;
    public static final int POWER = 5;

    private int myRefDataIndex = 0;
    private boolean myUseDistance = false;
    private boolean myHasPositionData = false;

    public ErgoDatastore(String newName, int newSampleRate)
    {
        myName = newName;
        mySampleRate = newSampleRate;
        myStartTime = System.currentTimeMillis();

        myExportedPPP = false;
    }

    public void setStartTime(long startTime)
    {
        myStartTime = startTime;
    }

    public boolean saveToFile(String fileName, int sampleRate)
    {
        File outputFile = new File(fileName);

        if(!outputFile.canWrite())
        {
            File outputDir = new File(fileName.substring(0,fileName.lastIndexOf('\\')));
            outputDir.mkdirs();
        }

        try
        {
            FileWriter fw = new FileWriter(outputFile, false);
            String item = null;
            int headers = 0;
            int timeIndex = -1;
            int pulsIndex = -1;
            int powerIndex = -1;
            int rpmIndex = -1;
            int speedIndex = -1;
            int distanceIndex = -1;
            int kilojouleIndex = -1;

            fw.write(FILE_HEADER + "\r\n");

            do
            {
                item = ErgoTools.subCharGet(FILE_HEADER.toLowerCase(), headers, ';');
                if(item != null)
                {
                    if(item.equals(HEADER_TIME.toLowerCase()) )
                        timeIndex = headers;
                    if(item.equals(HEADER_PULS.toLowerCase()) )
                        pulsIndex = headers;
                    if(item.equals(HEADER_POWER.toLowerCase()) )
                        powerIndex = headers;
                    if(item.equals(HEADER_RPM.toLowerCase()) )
                        rpmIndex = headers;
                    if(item.equals(HEADER_SPEED.toLowerCase()) )
                        speedIndex = headers;
                    if(item.equals(HEADER_DISTANCE.toLowerCase()) )
                        distanceIndex = headers;
                    if(item.equals(HEADER_KILOJOULE.toLowerCase()) )
                        kilojouleIndex = headers;
                }
                headers++;
            }
            while(item != null);

            Enumeration enumDatas = myErgoDatas.elements();
            while(enumDatas.hasMoreElements())
            {
                ErgoData data = (ErgoData)enumDatas.nextElement();

                long nowSample = 0;
                long myLastSample = 0;
                boolean bWrite = false;

                try
                {
                    nowSample = (new SimpleDateFormat("HH:mm:ss")).parse(data.getTime()).getTime();
                } catch(Exception e)
                {
                    log.error(e);
                }

                if(myLastSample != 0)
                {
                    if((nowSample - myLastSample) >= sampleRate)
                        bWrite = true;
                    else
                        bWrite = false;
                }
                else bWrite = true;

                if(bWrite)
                {
                    String line = "";
                    for(int i = 0; i < headers; i++)
                    {
                        if(line.length() > 0)
                            line += ";";
                        if(i == timeIndex)
                            line += data.getTime();
                        if(i == pulsIndex)
                            line += data.getPuls();
                        if(i == powerIndex)
                            line += data.getPower();
                        if(i == rpmIndex)
                            line += data.getRPM();
                        if(i == speedIndex)
                            line += data.getSpeed();
                        if(i == distanceIndex)
                            line += data.getDistance();
                        if(i == kilojouleIndex)
                            line += data.getKilojoule();
                    }
                    fw.write(line + "\r\n");
                }

                myLastSample = nowSample;
            }
            fw.close();
            return true;
        } catch(Exception e)
        {
            log.error(e);
        }
        return false;
    }

    public boolean exportGPX(String fileName, String note, double lat, double lon)
    {
    	File outputFile = new File(fileName);

        if(!outputFile.canWrite())
        {
            File outputDir = new File(fileName.substring(0,fileName.lastIndexOf('\\')));
            outputDir.mkdirs();
        }

        try
        {
        	Date now = new Date();
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        	DecimalFormat doubleFormat = new DecimalFormat("0.0000000000000000");
        	FileWriter fw = new FileWriter(outputFile, false);
            double oDistance = 0;
            int kalorien = 0;
            ErgoData data = null;

            DecimalFormatSymbols sym = doubleFormat.getDecimalFormatSymbols();
            sym.setDecimalSeparator('.');
            doubleFormat.setDecimalFormatSymbols(sym);

            StringBuffer bufferCadence = new StringBuffer();
            StringBuffer bufferPower = new StringBuffer();
            StringBuffer bufferHR = new StringBuffer();
            StringBuffer bufferTrack = new StringBuffer();

            log.debug("Write to File " + outputFile.getAbsolutePath());

            Enumeration enumDatas = myErgoDatas.elements();
            GPSPosition pos = new GPSPosition(lat,lon);
            double lastDistance = 0;
            for(int i = 0; enumDatas.hasMoreElements(); i++)
            {
            	String time = dateFormat.format(new Date(myStartTime + (i*1000)));
            	double distance = 0;

            	data = (ErgoData)enumDatas.nextElement();
            	distance = data.getDistance();

                bufferCadence.append("<st:cadence time=\"" + time + "\" rpm=\"" + data.getRPM() + "\" />\r\n");
                bufferPower.append("<st:power time=\"" + time + "\" watts=\"" + data.getPower() + "\" />\r\n");
                bufferHR.append("<st:heartRate time=\"" + time + "\" bpm=\"" + data.getPuls() + "\" />\r\n");

                bufferTrack.append("<trkpt lat=\"" + doubleFormat.format(pos.getLat()) + "\" lon=\"" + doubleFormat.format(pos.getLon()) + "\">\r\n");
                bufferTrack.append("  <ele>" + doubleFormat.format(data.getHeight()) + "</ele>\r\n");
                bufferTrack.append("  <time>" + time + "</time>\r\n");
                bufferTrack.append("</trkpt>\r\n");

                if(distance != 0)
                	oDistance = data.getDistance();
               	else
               	{
               		distance = (data.getRPM()/60.0)*5.917/1000;
               		oDistance += distance;
				}

				if(data.getKilojoule() != 0)
                	kalorien = (int)(data.getKilojoule() * 0.23885);

				pos.move(distance - lastDistance, data.getBearing());

				lastDistance = data.getDistance();
            }

            fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
            fw.write("<gpx version=\"1.1\" creator=\"ErgoControl\" xmlns:st=\"urn:uuid:D0EB2ED5-49B6-44e3-B13C-CF15BE7DD7DD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/1\" schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\r\n");
            fw.write("  <extensions>\r\n");
            fw.write("    <st:activity id=\"1\" dataSource=\"ErgoControl\" created=\"" + dateFormat.format(now) + "\"");
            fw.write(" useGPSData=\"true\" startTime=\"" + dateFormat.format(new Date(this.myStartTime)) + "\"");
            fw.write(" hasStartTime=\"true\" distanceEntered=\"" + oDistance + "\"");
            fw.write(" timeEntered=\"" + timeFormat.format(new Date(myErgoDatas.size() * 1000)) + "\"");
            fw.write(" calories=\"" + kalorien + "\" location=\"Ergometer\"");
            fw.write(" notes=\"" + note + "\" intensity=\"0\" weather=\"\">\r\n");
            fw.write("      <st:cadenceTrack>\r\n" + bufferCadence.toString() + "      </st:cadenceTrack>\r\n");
            fw.write("      <st:powerTrack>\r\n" + bufferPower.toString() + "      </st:powerTrack>\r\n");
            fw.write("      <st:heartRateTrack>\r\n" + bufferHR.toString() + "      </st:heartRateTrack>\r\n");
            fw.write("    </st:activity>\r\n");
            fw.write("  </extensions>\r\n");
            fw.write("  <trk>\r\n");
            fw.write("    <number>1</number>\r\n");
            fw.write("    <trkseg>\r\n" + bufferTrack.toString() + "    </trkseg>\r\n");
            fw.write("  </trk>\r\n");
            fw.write("</gpx>");
            fw.close();
            return true;
        } catch(Exception e)
        {
            log.error(e);
        }
        return false;
    }

    public boolean exportPPP(ErgoUserData user, String note)
    {
        String fileDate = (new SimpleDateFormat("yyMMdd")).format((new Date(myStartTime)));
        int fileCounter = 1;
        File outputFile = null;
        String fileName = null;

        if(note == null)
            note = "";

        do
        {
            String fileCounterString = "";

            if(fileCounter < 10)
                fileCounterString = "0" + fileCounter;
            else
                fileCounterString = "" + fileCounter;

            fileName = user.getPolarDir() + "\\" + (new SimpleDateFormat("yyyy")).format((new Date(myStartTime))) + "\\" +
                    fileDate + fileCounterString + ".hrm";

            outputFile = new File(fileName);

            if(!outputFile.canWrite())
            {
                File outputDir = new File(user.getPolarDir() + "\\" + (new SimpleDateFormat("yyyy")).format((new Date(myStartTime))));
                outputDir.mkdirs();
            }

            fileCounter++;
        }
        while(outputFile.exists());

        try
        {
            FileWriter fw = new FileWriter(outputFile, false);
            String duration = (new SimpleDateFormat("HH:mm:ss.S")).format((new Date((myErgoDatas.size()*1000)-3600000)));
            double distance = 0;
            int kalorien = 0;
            double maxSpeed = 0;
            double avgSpeed = 0;
            int maxCad = 0;
            int avgCad = 0;
            int maxPower = 0;
            int avgPower = 0;
            int minHR = 200;
            int maxHR = 0;
            int avgHR = 0;
            int exerciseDuration = 0;
            int minHRRecovery = 200;
            int maxHRRecovery = 0;
            int avgHRRecovery = 0;
            int recoveryDuration = 0;
            int limitHR[] = new int[10];
            boolean calculateDistance = false;
            PolarZones zones = null;

            ErgoData data = (ErgoData)myErgoDatas.get(myErgoDatas.size()-1);
            if(data.getDistance() != 0)
                distance = data.getDistance();
            else
                calculateDistance = true;

            if(data.getKilojoule() != 0)
                kalorien = (int)(data.getKilojoule() * 0.23885);

            if(user.getActiveZone() != null)
                zones = user.getActiveZone();

            Enumeration dataEnum = myErgoDatas.elements();
            while(dataEnum.hasMoreElements())
            {
                data = (ErgoData)dataEnum.nextElement();
                double speed = 0;
                if(data.getSpeed() == 0)
                    speed = (double)((data.getRPM()/60.0)*5.917*3.65);
                else
                    speed = (double)data.getSpeed();

                avgPower += data.getPower();

                if(data.getPower() > maxPower)
                    maxPower = data.getPower();

                avgPower += data.getRPM();

                if(data.getRPM() > maxCad)
                    maxCad = data.getRPM();

                if(calculateDistance)
                    distance += (data.getRPM()/60.0)*5.917/1000;

                if(speed > maxSpeed)
                    maxSpeed = speed;

                if(data.isRecovery())
                {
                    if(data.getPuls() > maxHRRecovery)
                        maxHRRecovery = data.getPuls();

                    if(data.getPuls() < minHRRecovery)
                        minHRRecovery = data.getPuls();

                    avgHRRecovery += data.getPuls();

                    recoveryDuration++;
                }
                else
                {
                    avgSpeed += speed;

                    if(data.getPuls() > maxHR)
                        maxHR = data.getPuls();

                    if(data.getPuls() < minHR)
                        minHR = data.getPuls();

                    avgHR += data.getPuls();

                    exerciseDuration++;
                }

                if(zones != null)
                    limitHR[zones.getIndex(data.getPuls())]++;
            }

			avgCad /= exerciseDuration;
            avgSpeed /= exerciseDuration;
            avgPower /= exerciseDuration;
            avgHR /= exerciseDuration;
            if(recoveryDuration != 0)
                avgHRRecovery /= recoveryDuration;

            fw.write("[Params]\r\n");
            fw.write("Version=107\r\n");
            fw.write("Monitor=12\r\n");
            fw.write("SMode=110111100\r\n");
            fw.write("Date=" + (new SimpleDateFormat("yyyyMMdd")).format((new Date(myStartTime))) + "\r\n");
            fw.write("StartTime=" + (new SimpleDateFormat("HH:mm:ss.S")).format((new Date(myStartTime))) + "\r\n");
            fw.write("Length=" + duration + "\r\n");
            fw.write("Interval=5\r\n");
            fw.write("Upper1=0\r\n");
            fw.write("Lower1=0\r\n");
            fw.write("Upper2=0\r\n");
            fw.write("Lower2=0\r\n");
            fw.write("Upper3=0\r\n");
            fw.write("Lower3=0\r\n");
            fw.write("Timer1=00:00\r\n");
            fw.write("Timer2=00:00\r\n");
            fw.write("Timer3=00:00\r\n");
            fw.write("ActiveLimit=0\r\n");
            fw.write("MaxHR=" + user.getMaxHR() + "\r\n");
            fw.write("RestHR=" + user.getRestHR() + "\r\n");
            fw.write("StartDelay=0\r\n");
            fw.write("VO2max=" + user.getVO2Max() + "\r\n");
            fw.write("Weight=" + user.getWeight()/100 +  "\r\n\r\n");

            fw.write("[Note]\r\n" + note  + "\r\n\r\n");

            fw.write("[IntTimes]\r\n");
            fw.write("00:00:00.0\t0\t" + minHR + "\t" + maxHR + "\t" + avgHR + "\r\n");
            fw.write("0\t0\t0\t0\t0\t0\r\n");
            fw.write("0\t0\t0\t0\t0\r\n");
            fw.write("2\t" + (int)distance*10 + "\t0\t0\t0\t0\r\n");
            fw.write("0\t0\t0\t0\t0\r\n");
            //recovery lap time
            fw.write((new SimpleDateFormat("HH:mm:ss.S")).format((new Date((exerciseDuration*1000)-3600000))) + "\t0\t" + minHRRecovery + "\t" + maxHRRecovery + "\t" + avgHRRecovery + "\r\n");
            fw.write("3\t" + recoveryDuration + "\t" + minHRRecovery + "\t0\t0\t0\r\n");
            fw.write("0\t0\t0\t0\t0\r\n");
            fw.write("8192\t0\t0\t0\t0\t0\r\n");
            fw.write("0\t0\t0\t0\t0\r\n\r\n");

            fw.write("[IntNotes]\r\n");
            fw.write("1\tStart of Exercise\r\n");
            fw.write("2\tRecovery\r\n\r\n");

            fw.write("[ExtraData]\r\n\r\n");

            fw.write("[Summary-123]\r\n");
            fw.write(myErgoDatas.size() + "\t0\t" + myErgoDatas.size() + "\t0\t0\t0\r\n");
            fw.write(maxHR + "\t0\t0\t" + user.getRestHR() + "\r\n");
            fw.write(myErgoDatas.size() + "\t0\t" + myErgoDatas.size() + "\t0\t0\t0\r\n");
            fw.write(maxHR + "\t0\t0\t" + user.getRestHR() + "\r\n");
            fw.write("0\t0\t0\t0\t0\t0\r\n");
            fw.write(maxHR + "\t0\t0\t" + user.getRestHR() + "\r\n");
            fw.write("0\t"+ myErgoDatas.size()/5 + "\r\n\r\n");

            fw.write("[Summary-TH]\r\n");
            fw.write(myErgoDatas.size() + "\t0\t" + myErgoDatas.size() + "\t0\t0\t0\r\n");
            fw.write(maxHR + "\t0\t0\t" + user.getRestHR() + "\r\n");
            fw.write("0\t" + myErgoDatas.size()/5 + "\r\n\r\n");

            if(zones != null)
                fw.write("[HRZones]\r\n" + user.getMaxHR() + "\r\n" + zones.get(0).getMinValue() + "\r\n" + zones.get(1).getMinValue() + "\r\n" +
                        zones.get(2).getMinValue() + "\r\n" + zones.get(3).getMinValue() + "\r\n" + zones.get(4).getMinValue() + "\r\n" +
                        zones.get(5).getMinValue() + "\r\n" + zones.get(6).getMinValue() + "\r\n" + zones.get(7).getMinValue() + "\r\n" +
                        zones.get(8).getMinValue() + "\r\n" + zones.get(9).getMinValue() + "\r\n\r\n");
            else
                fw.write("[HRZones]\r\n190\r\n180\r\n170\r\n160\r\n150\r\n140\r\n0\r\n0\r\n0\r\n0\r\n0\r\n\r\n");

            fw.write("[SwapTimes]\r\n\r\n");

            fw.write("[Trip]\r\n");
            fw.write((int)distance*10 + "\r\n");
            fw.write("0\r\n");
            fw.write(myErgoDatas.size() + "\r\n");
            fw.write("0\r\n");
            fw.write("0\r\n");
            fw.write(avgSpeed*128 + "\r\n");
            fw.write((int)(maxSpeed*128) + "\r\n");
            fw.write((int)(distance*10) + "\r\n\r\n");

            fw.write("[HRData]\r\n");

            dataEnum = myErgoDatas.elements();
            while(dataEnum.hasMoreElements())
            {
                data = (ErgoData)dataEnum.nextElement();

                if(data.getTime().endsWith("0") || data.getTime().endsWith("5"))
                {
                    int speed = 0;
                    if(data.getSpeed() == 0)
                        speed = (int)((data.getRPM()/60.0)*5.917*3.65)*10;
                    else
                        speed = (int)data.getSpeed()*10;

                    fw.write(data.getPuls() + "\t" + speed + "\t" + data.getRPM() + "\t" + data.getPower() + "\r\n");
                }
            }

            fw.close();

            String fileName2 = null;
            File outputFile2 = null;
            fileName2 = user.getPolarDir() + "\\" + (new SimpleDateFormat("yyyy")).format((new Date(myStartTime))) + "\\" +
                    (new SimpleDateFormat("yyyyMMdd")).format((new Date(myStartTime))) + ".pdd";
            int time = Integer.parseInt((new SimpleDateFormat("HH")).format((new Date(myStartTime)))) * 3600;
            time +=  Integer.parseInt((new SimpleDateFormat("mm")).format((new Date(myStartTime)))) * 60;

            outputFile2 = new File(fileName2);
            if(outputFile2.exists())
            {
                BufferedReader br = new BufferedReader(new FileReader(outputFile2));
                FileWriter fw2 = null;
                String oldFileContent = "";
                String newFileContent = "";
                String line = null;
                String exerciseInfo = null;
                int startPos = 0;
                int endPos = 0;
                int newExercisePos = 0;
                int index = 0;
                boolean bAlreadyExists = false;

                do
                {
                    line = br.readLine();
                    if(line != null)
                    {
                        if(oldFileContent.length() > 0 )
                            oldFileContent += "\r\n";
                        oldFileContent += line;
                    }
                }
                while(line != null);
                oldFileContent += "\r\n";
                br.close();

                startPos = 0;
                endPos = 0;
                index = 0;
                do
                {
                    exerciseInfo = null;
                    startPos = oldFileContent.toLowerCase().indexOf("[exerciseinfo", endPos);
                    if(startPos != -1)
                    {
                        endPos = oldFileContent.toLowerCase().indexOf("[exerciseinfo", startPos+1);
                        if(endPos == -1)
                            endPos = oldFileContent.length()-1;
                        exerciseInfo = oldFileContent.substring(startPos, endPos);
                        int exerciseTime = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(exerciseInfo, 2, "\r\n"),4));

                        if(exerciseTime == time)
                            bAlreadyExists = true;
                        else
                            if(exerciseTime > time)
                                newExercisePos = index;
                            else
                                newExercisePos = index+1;
                    }

                    index++;
                }
                while(exerciseInfo != null && bAlreadyExists == false);

                if(!bAlreadyExists)
                {
					int infoLines = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(oldFileContent, 1, "\r\n"),1));
					int lineCount = infoLines +
									Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(oldFileContent, 1, "\r\n"),2)) +
									Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(oldFileContent, 1, "\r\n"),4));
                    int exerciseCount = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(oldFileContent, infoLines + 1, "\r\n"),1)) + 1;
                    int cor = 0;

                    newFileContent = ErgoTools.getLines(oldFileContent, 0, infoLines) + "\r\n";
                    line = ErgoTools.subCharGet(oldFileContent, infoLines + 1, "\r\n");
                    newFileContent += ErgoTools.subTabGet(line,0) + "\t" + exerciseCount + "\t" + ErgoTools.subTabGet(line,2) + "\t" + ErgoTools.subTabGet(line,3) + "\t" + ErgoTools.subTabGet(line,4) + "\t" + ErgoTools.subTabGet(line,5) + "\r\n";
                    newFileContent += ErgoTools.getLines(oldFileContent, infoLines + 2, lineCount) + "\r\n";

                    startPos = 0;
                    endPos = 0;
                    index = 0;
                    do
                    {
                        if(newExercisePos == index)
                        {
                            /*newFileContent += "[ExerciseInfo" + (index+1) + "]\r\n100\t1\t12\t6\t5\t256\r\n";
                            newFileContent += "0\t0\t0\t0\t" + time + "\t" + myErgoDatas.size() + "\r\n";
                            newFileContent += user.getActiveSport().getNumber() + "\t" + (int)distance*10 + "\t0\t0\t0\t" + kalorien + "\r\n";
                            newFileContent += (int)(distance*1000) + "\t0\t0\t0\t0\t0\r\n";
                            newFileContent += "0\t0\t0\t0\t0\t0\r\n";
                            newFileContent += limitHR[0] + "\t" + limitHR[1] + "\t" + limitHR[2] + "\t" + limitHR[3] + "\t" + limitHR[4] + "\t" + limitHR[5] + "\r\n";
                            newFileContent += limitHR[6] + "\t" + limitHR[7] + "\t" + limitHR[8] + "\t" + limitHR[9] + "\t0\t" + (int)(distance*1000) + "\r\n";
                            newFileContent += "0\t0\t0\t0\t0\t0\r\n";
                            newFileContent += "0\t0\t0\t0\t0\t0\r\n";
                            newFileContent += avgHR + "\t0\t0\t0\t0\t0\r\n";
                            newFileContent += "0\t0\t" + avgPower + "\t" + maxPower + "\t0\t0\r\n";
                            newFileContent += "0\t0\t0\t0\t0\t0\r\n";
                            newFileContent += "0\t0\t0\t0\t0\t0\r\n";
                            newFileContent += "\r\n" + note + "\r\n" + fileName + "\r\n\r\n\r\n\r\n";*/
                            newFileContent += "[ExerciseInfo1]\r\n100\t1\t12\t6\t5\t256\r\n";
							newFileContent += "0\t0\t0\t0\t" + time + "\t" + myErgoDatas.size() + "\r\n";
							newFileContent += user.getActiveSport().getNumber() + "\t" + (int)distance*10 + "\t0\t0\t0\t" + kalorien + "\r\n";
							newFileContent += (int)(distance*1000) + "\t0\t0\t0\t0\t0\r\n";
							newFileContent += "0\t0\t0\t0\t0\t0\r\n";
							newFileContent += limitHR[0] + "\t" + limitHR[1] + "\t" + limitHR[2] + "\t" + limitHR[3] + "\t" + limitHR[4] + "\t" + limitHR[5] + "\r\n";
							newFileContent += limitHR[6] + "\t" + limitHR[7] + "\t" + limitHR[8] + "\t" + limitHR[9] + "\t0\t0\r\n";
							newFileContent += "0\t0\t0\t0\t0\t0\r\n";
							newFileContent += "0\t0\t0\t0\t1\t0\r\n";
							newFileContent += avgHR + "\t" + maxHR + "\t" + (int)(avgSpeed*10) + "\t" + (int)(maxSpeed*10) + "\t" + avgCad+ "\t" + maxCad + "\r\n";
							newFileContent += "0\t0\t" + avgPower + "\t" + maxPower + "\t0\t0\r\n";
							newFileContent += "0\t0\t0\t0\t0\t0\r\n";
							newFileContent += "0\t0\t0\t0\t0\t0\r\n";
							newFileContent += "\r\n" + note + "\r\n" + fileName + "\r\n\r\n\r\n";

                            cor++;
                        }
                        else
                        {
                            startPos = oldFileContent.toLowerCase().indexOf("[exerciseinfo", endPos);
                            endPos = oldFileContent.toLowerCase().indexOf("[exerciseinfo", startPos+1);
                            if(endPos == -1)
                                endPos = oldFileContent.length();
                            newFileContent += "[ExerciseInfo" + (index + cor + 1) + "]" + oldFileContent.substring(oldFileContent.indexOf("\r\n", startPos), endPos);
                        }

                        index++;
                    }
                    while(index < exerciseCount);

                    fw2 = new FileWriter(outputFile2, false);
                    fw2.write(newFileContent);
                    fw2.close();
                }
                else
                    outputFile.delete();
            }
            else
            {
                FileWriter fw2 = new FileWriter(outputFile2, false);

                fw2.write("[DayInfo]\r\n100\t1\t7\t6\t1\t256\r\n");
				fw2.write((new SimpleDateFormat("yyyyMMdd")).format((new Date(myStartTime))) + "\t1\t" + user.getRestHR() + "\t0\t" + user.getWeight() + "\t28800\r\n");
				//fw2.write("0\t0\t" + user.getMaxHR() + "\t0\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n");
				fw2.write("0\t0\t" + user.getMaxHR() + "\t0\t0\t0\r\n");
				fw2.write("0\t0\t" + user.getVO2Max() + "\t1\t200\t0\r\n");
				fw2.write("0\t0\t1\t0\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n\r\n");


				fw2.write("[ExerciseInfo1]\r\n100\t1\t12\t6\t5\t256\r\n");
                fw2.write("0\t0\t0\t0\t" + time + "\t" + myErgoDatas.size() + "\r\n");
				fw2.write(user.getActiveSport().getNumber() + "\t" + (int)distance*10 + "\t0\t0\t0\t" + kalorien + "\r\n");
				fw2.write((int)(distance*1000) + "\t0\t0\t0\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n");
				fw2.write(limitHR[0] + "\t" + limitHR[1] + "\t" + limitHR[2] + "\t" + limitHR[3] + "\t" + limitHR[4] + "\t" + limitHR[5] + "\r\n");
				//fw2.write(limitHR[6] + "\t" + limitHR[7] + "\t" + limitHR[8] + "\t" + limitHR[9] + "\t0\t" + (int)(distance*1000) + "\r\n");
				fw2.write(limitHR[6] + "\t" + limitHR[7] + "\t" + limitHR[8] + "\t" + limitHR[9] + "\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t1\t0\r\n");
				fw2.write(avgHR + "\t" + maxHR + "\t" + (int)(avgSpeed*10) + "\t" + (int)(maxSpeed*10) + "\t" + avgCad+ "\t" + maxCad + "\r\n");
				fw2.write("0\t0\t" + avgPower + "\t" + maxPower + "\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n");
				fw2.write("0\t0\t0\t0\t0\t0\r\n");
				fw2.write("\r\n" + note + "\r\n" + fileName + "\r\n\r\n\r\n");
                fw2.close();
			}

            myExportedPPP = true;
            return true;
        } catch(Exception e)
        {
            log.error(e);
        }
        return false;
    }

    public boolean loadFromFile(JFrame owner, String fileName, ErgoBikeDefinition ergoBike)
    {
		return loadFromFile(owner, fileName, ergoBike, false);
	}

    public boolean loadFromFile(JFrame owner, String fileName, ErgoBikeDefinition ergoBike, boolean bgdLoad)
    {
        File inputFile = new File(fileName);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String lastTime = "00:00:00";
        ErgoPhysics myPhysics = null;

        if(ergoBike != null)
        	myPhysics = new ErgoPhysics(ergoBike, false);

        myStartTime = inputFile.lastModified();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = null;
            boolean bFirst = true;
            String item = null;
            int headers = 0;
            int foundHeaders = 0;
            int timeIndex = -1;
            int pulsIndex = -1;
            int powerIndex = -1;
            int rpmIndex = -1;
            int speedIndex = -1;
            int distanceIndex = -1;
            int kilojouleIndex = -1;
            int heightIndex = -1;
            int gradeIndex = -1;
            int bearingIndex = -1;
            boolean bDistance = false;
            boolean bGrade = false;
            boolean bPower = false;

            while((line = br.readLine()) != null)
            {
                if(line != null)
                {
                    if(bFirst)
                    {
                    	headers = 0;
                    	foundHeaders = 0;
                        do
                        {
                            item = ErgoTools.subCharGet(line.toLowerCase(), headers, ';');
                            if(item != null)
                            {
                                if(item.equals(HEADER_TIME.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    timeIndex = headers;
                                }
                                if(item.equals(HEADER_PULS.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    pulsIndex = headers;
                                }
                                if(item.equals(HEADER_POWER.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    powerIndex = headers;
                                }
                                if(item.equals(HEADER_RPM.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    rpmIndex = headers;
                                }
                                if(item.equals(HEADER_SPEED.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    speedIndex = headers;
                                }
                                if(item.equals(HEADER_DISTANCE.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    distanceIndex = headers;
                                }
                                if(item.equals(HEADER_KILOJOULE.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    kilojouleIndex = headers;
                                }
                                if(item.equals(HEADER_HEIGHT.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    heightIndex = headers;
                                }
                                if(item.equals(HEADER_GRADE.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    gradeIndex = headers;
                                }
                                if(item.equals(HEADER_BEARING.toLowerCase()) )
                                {
                                	foundHeaders++;
                                    bearingIndex = headers;
                                }

                                if(item.equals(HEADER2_TIME.toLowerCase()) )
                                {
                                	foundHeaders++;
									timeIndex = headers;
                                }
								if(item.equals(HEADER2_PULS.toLowerCase()) )
								{
                                	foundHeaders++;
									pulsIndex = headers;
								}
								if(item.equals(HEADER2_POWER.toLowerCase()) )
								{
                                	foundHeaders++;
									powerIndex = headers;
								}
								if(item.equals(HEADER2_RPM.toLowerCase()) )
								{
                                	foundHeaders++;
									rpmIndex = headers;
								}
								if(item.equals(HEADER2_SPEED.toLowerCase()) )
								{
                                	foundHeaders++;
									speedIndex = headers;
								}
								if(item.equals(HEADER2_DISTANCE.toLowerCase()) )
								{
                                	foundHeaders++;
									distanceIndex = headers;
								}
								if(item.equals(HEADER2_KILOJOULE.toLowerCase()) )
								{
                                	foundHeaders++;
                                    kilojouleIndex = headers;
								}
                            }
                            headers++;
                        }
                        while(item != null);
                        if(foundHeaders > 1)
                        	bFirst = false;
                    }
                    else
                    {
                        int newPuls = 0;
                        int newRPM = 0;
                        double newSpeed = 0;
                        double newDistance = 0;
                        int newKilojoule = 0;
                        int newPower = 0;
                        double newGrade = 0;
                        double newBearing = 0;
                        double newHeight = 0;
                        String newTime = "00:00:00";

                        if(pulsIndex != -1 && ErgoTools.subCharGet(line, pulsIndex, ';').length() > 0)
                            newPuls = Integer.parseInt(ErgoTools.subCharGet(line, pulsIndex, ';'));
                        if(rpmIndex != -1 && ErgoTools.subCharGet(line, rpmIndex, ';').length() > 0)
                            newRPM = Integer.parseInt(ErgoTools.subCharGet(line, rpmIndex, ';'));
                        if(speedIndex != -1 && ErgoTools.subCharGet(line, speedIndex, ';').length() > 0)
                            newSpeed = Double.parseDouble(ErgoTools.subCharGet(line, speedIndex, ';').replace(',','.'));
                        if(distanceIndex != -1 && ErgoTools.subCharGet(line, distanceIndex, ';').length() > 0)
                            newDistance = Double.parseDouble(ErgoTools.subCharGet(line, distanceIndex, ';').replace(',','.'));
                        if(kilojouleIndex != -1 && ErgoTools.subCharGet(line, kilojouleIndex, ';').length() > 0)
                            newKilojoule = Integer.parseInt(ErgoTools.subCharGet(line, kilojouleIndex, ';'));
                        if(powerIndex != -1 && ErgoTools.subCharGet(line, powerIndex, ';').length() > 0)
                            newPower = Integer.parseInt(ErgoTools.subCharGet(line, powerIndex, ';'));
                        if(timeIndex != -1 && ErgoTools.subCharGet(line, timeIndex, ';').length() > 0)
                            newTime = ErgoTools.subCharGet(line, timeIndex, ';');
                        if(gradeIndex != -1 && ErgoTools.subCharGet(line, gradeIndex, ';').length() > 0)
                        	newGrade = Double.parseDouble(ErgoTools.subCharGet(line, gradeIndex, ';'));
                        if(bearingIndex != -1 && ErgoTools.subCharGet(line, bearingIndex, ';').length() > 0)
                        	newBearing = Double.parseDouble(ErgoTools.subCharGet(line, bearingIndex, ';'));
                        if(heightIndex != -1 && ErgoTools.subCharGet(line, heightIndex, ';').length() > 0)
                        	newHeight = Double.parseDouble(ErgoTools.subCharGet(line, heightIndex, ';'));

                        if(newDistance > 0)
                        	bDistance = true;
                        if(newGrade > 0)
                        	bGrade = true;
                        if(newPower > 0)
                        	bPower = true;

                        Date now = df.parse(newTime);
                        Date before = df.parse(lastTime);
                        long count = (now.getTime() - before.getTime()) / 1000;

                        while(count > 0)
                        {
                        	before = new Date(before.getTime() + 1000);
                        	String nowTime = df.format(before);

                        	if(myPhysics != null)
                        	{
	                        	long time = before.getTime();

	                        	myPhysics.calc(time, newPower, newGrade, newRPM);
	                        	newSpeed = myPhysics.getVelocity();
                        	}

	                        ErgoData data = new ErgoData(null, newPuls, newRPM, newSpeed, newDistance, newPower, newKilojoule, newHeight, newGrade, newBearing, nowTime, false);
	                        add(data);

	                        count--;
                        }
                        lastTime = newTime;
                    }
                }
            }
            br.close();

			if(!bgdLoad)
			{
				ErgoControlProgramSelect pSelect = new ErgoControlProgramSelect(owner);
				pSelect.setDistance(bDistance);
				pSelect.setPower(bPower);
				/*if(rpmIndex >= 0)
					pSelect.setRPM(true);*/
				pSelect.setGrade(bGrade);
				/*if(pulsIndex >= 0)
					pSelect.setPuls(true);*/

				pSelect.setTime(true);
				pSelect.showDialog();

				myRefDataIndex = pSelect.getType();
				myUseDistance = pSelect.getUseDistance();
			}

            return true;
        } catch(Exception e)
        {
            log.error(e);
        }
        return false;
    }

    public boolean importGPX(JFrame owner, String fileName, double dampingRate)
    {
    	//boolean hasPuls = false;
        boolean hasPower = false;
        //boolean hasRpm = false;
        boolean hasSpeed = false;
        boolean hasHeight = false;
    	int interval = 1;
        long time = -3600000;
        long gpsTime = 0;
        long lastGPSTime = 0;
        double lastDistance = 0;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        //SimpleDateFormat gpsTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat gpsTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        GPSPosition lastPos = null;
        double lastHeight = 0;

        try
		{
			FileInputStream fis = new FileInputStream(fileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] buf = new byte[65535];
	        int cnt = 0;
	        while (cnt != -1) {
	            cnt = fis.read(buf,0,65534);
	            if(cnt != -1)
	                bos.write(buf,0,cnt);
	        }
	        byte[] myContent = bos.toByteArray();

	        ByteArrayInputStream bis = new ByteArrayInputStream(myContent);

	        DOMParser myParser = new DOMParser();
	        myParser.parse(new InputSource(bis));
	        DocumentImpl myDocumentImpl = (DocumentImpl) myParser.getDocument();

	        //process first level (root)
	        NodeList nodes = myDocumentImpl.getChildNodes();
	        for(int i = 0; i < nodes.getLength(); i++)
	        {
	        	Node node = nodes.item(i);
	        	if(node.getNodeName().equals("gpx"))
	        	{
	        		nodes = node.getChildNodes();
	        		break;
	        	}
	        }
	        if(nodes == null) return false;
	        //process second level (tracks)
	        for(int i = 0; i < nodes.getLength(); i++)
	        {
	        	Node node = nodes.item(i);
	        	if(node.getNodeName().equals("trk"))
	        	{
	        		nodes = node.getChildNodes();
	        		break;
	        	}
	        }
	        if(nodes == null) return false;
	        // process third level (track segments)
	        for(int i = 0; i < nodes.getLength(); i++)
	        {
	        	Node node = nodes.item(i);
	        	if(node.getNodeName().equals("trkseg"))
	        	{
	        		nodes = node.getChildNodes();
	        		break;
	        	}
	        }
	        if(nodes == null) return false;
	        // process third level (track points)
	        for(int i = 0; i < nodes.getLength(); i++)
	        {
	        	Node node = nodes.item(i);
	        	if(node.getNodeName().equals("trkpt"))
	        	{
	        		Node ele = null;
	        		Node tme = null;
	        		NodeList nodes2 = node.getChildNodes();
	        		for(int j = 0; j < nodes2.getLength(); j++)
	    	        {
	    	        	ele = nodes2.item(j);

	    	        	if(ele.getNodeName().equals("ele"))
	    	        		break;
	    	        	else
	    	        		ele = null;
	    	        }
	        		for(int j = 0; j < nodes2.getLength(); j++)
	    	        {
	    	        	tme = nodes2.item(j);

	    	        	if(tme.getNodeName().equals("time"))
	    	        		break;
	    	        	else
	    	        		tme = null;
	    	        }

	        		double lat = Double.parseDouble(node.getAttributes().getNamedItem("lat").getNodeValue());
	        		double lon = Double.parseDouble(node.getAttributes().getNamedItem("lon").getNodeValue());
	        		double h = 0;
	        		if(ele != null)
	        		{
	        			h = Double.parseDouble(ele.getFirstChild().getNodeValue());
	        			hasHeight = true;
	        		}

	        		hasSpeed = true;

	        		if(tme != null)
	        			gpsTime = gpsTimeFormat.parse(tme.getFirstChild().getNodeValue()).getTime();
	        		else
	        		{
	        			if(lastGPSTime != 0)
	        				gpsTime += 1000;
	        			else
	        				gpsTime = System.currentTimeMillis();
	        		}

	        		GPSPosition pos = new GPSPosition(lat, lon);
	        		int curPuls = 0;
                    int curRPM = 0;
                    double curSpeed = 0;
                    double curDistance = 0;
                    int curKilojoule = 0;
                    int curPower = 0;
                    double curHeight = h;
                    double curGrade = 0;
                    double curBearing = 0;

                    if(lastGPSTime != 0)
                    {
	        			interval = (int)((gpsTime - lastGPSTime)/1000);

	        			curDistance = lastPos.calcDistance(pos);
	        			curSpeed = curDistance / interval * 3600;
	        			curBearing = lastPos.calcBearing(pos);
	        			curGrade = Math.toDegrees(Math.asin((curHeight - lastHeight) / (curDistance * 1000)));
	        			//curGrade = 100 * Math.tan(Math.asin(Math.toRadians((curHeight - lastHeight) / (curDistance * 1000))));
                    }

                    if(interval <= 0 && tme != null)
                    {
                    	log.error("Bad time data in gpx file: " + tme.getFirstChild().getNodeValue());
                    	continue;
                    }

                    double stepDistance = curDistance/interval;
                    double stepHeight = (curHeight-lastHeight)/interval;
                    do
                    {
                    	lastDistance += stepDistance;
                    	lastHeight += stepHeight;

                    	int newPuls = curPuls;
                        int newRPM = curRPM;
                        double newSpeed = curSpeed;
                        double newDistance = lastDistance;
                        int newKilojoule = curKilojoule;
                        int newPower = curPower;
                        double newHeight = lastHeight;
                        double newGrade = curGrade;
                        double newBearing = curBearing;
                    	String newTime = timeFormat.format(new Date(time));

		        		ErgoData data = new ErgoData(pos, newPuls, newRPM, newSpeed, newDistance, newPower, newKilojoule, newHeight, newGrade, newBearing, newTime, false);

	                    add(data);
	                    time += 1000;
	                    interval--;
	                }
	                while(interval > 0);
                    lastGPSTime = gpsTime;
                    lastPos = pos;
                    lastHeight = h;
	        	}
	        }

	        ErgoControlProgramSelect pSelect = new ErgoControlProgramSelect(owner);
            if(hasSpeed == true)
            {
            	//pSelect.setSpeed(true);
            	pSelect.setDistance(true);
            	myHasPositionData = true;
            }
            if(hasPower == true)
            	pSelect.setPower(true);
            /*if(hasRpm == true)
            	pSelect.setRPM(true);*/
            if(hasHeight == true)
            {
            	pSelect.setGrade(true);
            	calculateGradiation(dampingRate);
			}
            /*if(hasPuls == true)
            	pSelect.setPuls(true);*/

            pSelect.setTime(true);
            pSelect.showDialog();

            myRefDataIndex = pSelect.getType();
            myUseDistance = pSelect.getUseDistance();

	    	return true;
		}
		catch(Exception ex)
		{
			log.error(ex);
			ex.printStackTrace();
		}
    	return false;
    }

    public boolean importHRM(JFrame owner, String fileName, double dampingRate)
    {
        File inputFile = new File(fileName);

        myStartTime = inputFile.lastModified();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = null;
            boolean configMode = false;
            boolean configInt = false;
            boolean dataMode = false;
            int pulsIndex = 0;
            int powerIndex = 4;
            int rpmIndex = 2;
            int speedIndex = 1;
            int heightIndex = 3;
            int corrFactor = 0;
            int interval = 5000;
            long time = -3600000;
            double lastDistance = 0;

            while((line = br.readLine()) != null)
            {
                if(line != null)
                {
                     if(dataMode)
                    {
                        do
                        {
                            int newPuls = 0;
                            int newRPM = 0;
                            double newSpeed = 0;
                            double newDistance = 0;
                            int newKilojoule = 0;
                            int newPower = 0;
                            double newHeight = 0;
                            double newGrade = 0;
                            double newBearing = 0;
                            String newTime = (new SimpleDateFormat("HH:mm:ss")).format(new Date(time)) ;

                            if(pulsIndex != -1)
                                newPuls = Integer.parseInt(ErgoTools.subCharGet(line, pulsIndex, '\t'));
                            if(rpmIndex != -1)
                                newRPM = Integer.parseInt(ErgoTools.subCharGet(line, rpmIndex, '\t'));
                            if(speedIndex != -1)
                                newSpeed = Double.parseDouble(ErgoTools.subCharGet(line, speedIndex, '\t'))/10;
                            if(powerIndex != -1)
                                newPower = Integer.parseInt(ErgoTools.subCharGet(line, powerIndex, '\t'));
                            if(heightIndex != -1)
                                newHeight = Integer.parseInt(ErgoTools.subCharGet(line, heightIndex, '\t'));

                            newDistance = lastDistance + newSpeed/3600.0;
                            lastDistance = newDistance;

                            ErgoData data = new ErgoData(null, newPuls, newRPM, newSpeed, newDistance, newPower, newKilojoule, newHeight, newGrade, newBearing, newTime, false);

                            add(data);
                            time += 1000;
                        }
                        while(time%interval != 0);
                    }
                    if(!configMode && line.length() > 6 && line.substring(0,5).equalsIgnoreCase("SMode"))
                    {
                        //Speed
                        if(line.charAt(6) == '0')
                        {
                            speedIndex = -1;
                            corrFactor--;
                        }
                        //Cadence
                        if(line.charAt(7) == '0')
                        {
                            rpmIndex = -1;
                            corrFactor--;
                        }
                        else
                            rpmIndex += corrFactor;
                        //Altitude
                        if(line.charAt(8) == '0')
                        {
                        	heightIndex = -1;
                            corrFactor--;
                        }
                        else
                            heightIndex += corrFactor;
                        //Power
                        if(line.charAt(9) == '0')
                        {
                            powerIndex = -1;
                            corrFactor--;
                        }
                        else
                            powerIndex += corrFactor;

                        configMode = true;
                    }
                    if(!configInt && line.length() > 9 && line.substring(0,8).equalsIgnoreCase("Interval"))
                    {
                        interval = Integer.parseInt(line.substring(9,10))*1000;
                        configInt = true;
                    }
                    if(configMode && configInt && line.equalsIgnoreCase("[HRData]"))
                        dataMode = true;
                }
            }
            br.close();

            ErgoControlProgramSelect pSelect = new ErgoControlProgramSelect(owner);
            if(speedIndex >= 0)
            {
            	//pSelect.setSpeed(true);
            	pSelect.setDistance(true);
            }
            if(powerIndex >= 0)
            	pSelect.setPower(true);
            /*if(rpmIndex >= 0)
            	pSelect.setRPM(true);*/
            if(heightIndex >= 0)
            {
            	pSelect.setGrade(true);
            	calculateGradiation(dampingRate);
			}
            /*if(pulsIndex >= 0)
            	pSelect.setPuls(true);*/

            pSelect.setTime(true);
            pSelect.showDialog();

            myRefDataIndex = pSelect.getType();
            myUseDistance = pSelect.getUseDistance();

            return true;
        } catch(Exception e)
        {
            log.error(e);
        }
        return false;
    }

    public void calculateGradiation(double dampingRate)
    {
    	double dampingDistance = dampingRate/100;
		double lastDist = 0;

		for(int i=0; i < myErgoDatas.size(); i++)
		{
			ErgoData data = (ErgoData)myErgoDatas.get(i);
			double grade = data.getGrade();
			double height = data.getOrigHeight();
			double dist = data.getDistance() - lastDist;
			lastDist = data.getDistance();

			if(dist < dampingDistance)
			{
				double startHeight = data.getOrigHeight();
				double endHeight = data.getOrigHeight() * dist;
				int j = i + 1;
				double lastDist2 = data.getDistance();

				while(dist < dampingDistance && j < myErgoDatas.size())
				{
					ErgoData data2 = (ErgoData)myErgoDatas.get(j++);
					double dist2 = data2.getDistance() - lastDist2;

					endHeight += data2.getOrigHeight() * dist2;
					dist += dist2;
					lastDist2 = data2.getDistance();
				}
				if(dist < dampingDistance)
				{
					double dist2 = dampingDistance-dist;
					endHeight += (endHeight / dist) * dist2;
					dist += dist2;
				}
				endHeight /= dist;
				height = endHeight;
				grade = Math.toDegrees(Math.asin((endHeight - startHeight) / (dist * 1000)));
			}

			data.setHeight(height);
			data.setGrade(grade);
		}
    }

    public int getSampleRate()
    {
        return mySampleRate;
    }

    public Enumeration elements()
    {
        return myErgoDatas.elements();
    }

    public void add(ErgoData newData)
    {
        myErgoDatas.add(newData);
    }

    public void set(int pos, ErgoData newData)
    {
        myErgoDatas.set(pos, newData);
    }

    public int size()
    {
        return myErgoDatas.size();
    }

    public ErgoData get(int index)
    {
        return (ErgoData)myErgoDatas.get(index);
    }

    public long getStartTime()
    {
        return myStartTime;
    }

    public boolean getExportedPPP()
    {
        return myExportedPPP;
    }

    public String getName()
    {
        return myName;
    }

    public int getRefDataIndex()
    {
    	return myRefDataIndex;
    }

    public void setRefDataIndex(int newRefDataIndex)
    {
    	myRefDataIndex = newRefDataIndex;
    }

    public void setUseDistance(boolean bNew)
    {
    	myUseDistance = bNew;
    }

    public boolean getUseDistance()
    {
    	return myUseDistance;
    }

    public void getHasPositionData(boolean bNew)
    {
    	myHasPositionData = bNew;
    }

    public boolean getHasPositionData()
    {
    	return myHasPositionData;
    }
}
