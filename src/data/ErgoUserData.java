package data;

import org.apache.log4j.Logger;

import tools.ErgoTools;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 21.02.2005
 * Time: 13:30:24
 * To change this template use File | Settings | File Templates.
 */
public class ErgoUserData {
    private static Logger log = Logger.getLogger(ErgoUserData.class);

    private final static String PROPERTY_POLARDIR = "_Polar";
    private final static String PROPERTY_GPXDIR = "_GPX";
    private final static String PROPERTY_SAVEDIR = "_Save";
    private final static String PROPERTY_IMPORTDIR = "_Import";
    private final static String PROPERTY_WEIGHT = "_Weight";
    private final static String PROPERTY_SPORT = "_Sport";
    private final static String PROPERTY_ZONE = "_Zone";
    private final static String PROPERTY_SAVE = "_AutoSave";
    private final static String PROPERTY_EXPORT_PPP = "_AutoExportPPP";
    private final static String PROPERTY_EXPORT_GPX = "_AutoExportGPX";

    private String myUserName = null;
    private String myPolarUserFile = null;
    private String myGPXDirectory = null;
    private boolean myAutoExportPPP = false;
    private boolean myAutoExportGPX = false;
    private String mySaveDirectory = null;
    private String myImportDirectory = null;
    private boolean myAutoSave = false;

    private int myMaxHR = 0;
    private int myRestHR = 0;
    private int myVO2Max = 0;
    private int myWeight = 0;

    private Hashtable<String,PolarZones> myZoneSets = null;
    private PolarZones myActiveZone = null;
    private Hashtable<String,PolarSport> mySportSets = null;
    private PolarSport myActiveSport = null;

    private final static int AREA_NOAREA = -1;
    private final static int AREA_INFO = 1;
    private final static int AREA_SPORTS = 2;
    private final static int AREA_ZONES = 3;

    private boolean myIsInitialized = false;

    public ErgoUserData(String newUserName, String newPolarUserFile, String newGPXFile,  String newSaveDirectory, String newImportDirectory, Properties properties)
    {
        myUserName = newUserName;

        myAutoExportPPP = new Boolean(properties.getProperty(myUserName + PROPERTY_EXPORT_PPP)).booleanValue();

        if(newPolarUserFile != null)
            myPolarUserFile = newPolarUserFile;
        else
            myPolarUserFile = properties.getProperty(myUserName + PROPERTY_POLARDIR);
        
        myAutoExportGPX = new Boolean(properties.getProperty(myUserName + PROPERTY_EXPORT_GPX)).booleanValue();
        
        if(newGPXFile != null)
            myGPXDirectory = newPolarUserFile;
        else
            myGPXDirectory = properties.getProperty(myUserName + PROPERTY_GPXDIR);

        myAutoSave = new Boolean(properties.getProperty(myUserName + PROPERTY_SAVE)).booleanValue();

        if(newSaveDirectory != null)
            mySaveDirectory = newSaveDirectory;
        else
            mySaveDirectory = properties.getProperty(myUserName + PROPERTY_SAVEDIR);

        if(newImportDirectory != null)
            myImportDirectory = newImportDirectory;
        else
            myImportDirectory = properties.getProperty(myUserName + PROPERTY_IMPORTDIR);

        if(properties.getProperty(myUserName + PROPERTY_WEIGHT) != null)
            myWeight = Integer.parseInt(properties.getProperty(myUserName + PROPERTY_WEIGHT));

        if(myPolarUserFile != null)
            initPolarData();

        if (properties.getProperty(myUserName + PROPERTY_ZONE) != null && myZoneSets != null)
            myActiveZone = (PolarZones)myZoneSets.get(properties.getProperty(myUserName + PROPERTY_ZONE));

        if (properties.getProperty(myUserName + PROPERTY_SPORT) != null && mySportSets != null)
            myActiveSport = (PolarSport)mySportSets.get(properties.getProperty(myUserName + PROPERTY_SPORT));
    }

    public void initPolarData()
    {
        myIsInitialized = false;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(myPolarUserFile)));
            String line = null;
            int area = AREA_NOAREA;
            String areaInfo = "";
            String areaSports = "";
            String areaZones = "";

            do
            {
                line = br.readLine();
                if(line != null)
                {
                    String lline = line.toLowerCase();
                    if(lline.startsWith("["))
                    {
                        log.debug("Found Area: " + line);
                        area = AREA_NOAREA;
                    }
                    if(lline.startsWith("[personinfo]"))
                    {
                        area = AREA_INFO;
                        line = "";
                    }
                    if(lline.startsWith("[personsports]"))
                    {
                        area = AREA_SPORTS;
                        line = "";
                    }
                    if(lline.startsWith("[personhrzones]"))
                    {
                        area = AREA_ZONES;
                        line = "";
                    }

                    switch(area)
                    {
                        case AREA_INFO:
                            if(areaInfo.length() > 0)
                                areaInfo += "\r\n";
                            areaInfo += line;
                            break;
                        case AREA_SPORTS:
                            if(areaSports.length() > 0)
                                areaSports += "\r\n";
                            areaSports += line;
                            break;
                        case AREA_ZONES:
                            if(areaZones.length() > 0)
                                areaZones += "\r\n";
                            areaZones += line;
                            break;
                    }
                }
            }
            while(line != null);

            processInfo(areaInfo);
            processSports(areaSports);
            processZones(areaZones);
            
            br.close();

            myIsInitialized = true;
        } catch(FileNotFoundException e)
        {
            log.error(e);
            myPolarUserFile = null;
        } catch(IOException e)
        {
            log.error(e);
            myPolarUserFile = null;
        }
    }

    private void processInfo(String areaInfo)
    {
        //log.debug("INFO: " + areaInfo);
        String line = ErgoTools.subCharGet(areaInfo, 4, "\r\n");
        myMaxHR = Integer.parseInt(ErgoTools.subTabGet(line, 0));
        log.debug("Max HR = " + myMaxHR);
        myRestHR = Integer.parseInt(ErgoTools.subTabGet(line, 3));
        log.debug("Rest HR = " + myRestHR);
        myVO2Max = Integer.parseInt(ErgoTools.subTabGet(line, 4)) / 10;
        log.debug("VO2 Max = " + myVO2Max);
    }

    private void processSports(String areaSports)
    {
        //log.debug("SPORTS: " + areaSports);
        String sportArea = areaSports;
        int sportSetPos = 0;
        int startLine = 3;
        int endLine = 0;
        int sports = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(areaSports, 1, "\r\n"), 0));

        mySportSets = null;

        do
        {
            endLine = startLine + 5;
            sportArea = ErgoTools.getLines(areaSports, startLine, endLine);
            if(sportArea != null)
            {
                int sportNumber = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(sportArea, 0, "\r\n"), 0));
                boolean isActive = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(sportArea, 0, "\r\n"), 2))!=1;
                String sportShortName = ErgoTools.subCharGet(sportArea, 3, "\r\n");
                String sportName = ErgoTools.subCharGet(sportArea, 2, "\r\n");
                PolarSport sport = new PolarSport(sportNumber, sportShortName, sportName);

                if(isActive)
                {
                    if(mySportSets == null)
                        mySportSets = new Hashtable<String,PolarSport>();

                    mySportSets.put(sport.getName(), sport);
                }
            }
            startLine=endLine+1;
            sportSetPos++;
        }
        while(sportSetPos < sports);
    }

    private void processZones(String areaZones)
    {
        //log.debug("ZONES: " + areaZones);
        int defaultMaxVal = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(areaZones, 2, "\r\n"), 3));
        int zoneSetPos = 0;
        int startLine = 2;
        int endLine = 0;
        String zoneArea = areaZones;

        myZoneSets = null;

        do
        {
            int maxVal = defaultMaxVal;
            int zones = 0;

            zones = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(areaZones, startLine, "\r\n"), 1));
            endLine = startLine + 2 + zones*4;
            zoneArea = ErgoTools.getLines(areaZones, startLine, endLine);
            if(zoneArea != null)
            {
                String zoneSetName = null;
                PolarZones zoneSet = null;

                zoneSetName = ErgoTools.subCharGet(zoneArea, 2, "\r\n");

                zoneSet = new PolarZones(zoneSetName);

                for(int i = 0; i < zones; i++)
                {
                    int minVal = Integer.parseInt(ErgoTools.subTabGet(ErgoTools.subCharGet(zoneArea, 3+i*4, "\r\n"), 0));
                    String name = ErgoTools.subCharGet(zoneArea, 5+i*4, "\r\n");
                    String shortName = ErgoTools.subCharGet(zoneArea, 6+i*4, "\r\n");

                    zoneSet.add(new PolarZone(shortName, name, minVal, maxVal));
                    maxVal = minVal - 1;
                }
                for(int i = zones; i <= 9; i++)
                    zoneSet.add(new PolarZone("", "", 0, 0));
                
                if(myZoneSets == null)
                    myZoneSets = new Hashtable<String,PolarZones>();

                myZoneSets.put(zoneSet.getName(), zoneSet);
            }
            startLine=endLine+1;
            zoneSetPos++;
        }
        while(zoneSetPos < 4);
    }

    public void save(Properties properties)
    {
        String users = properties.getProperty("Users");
        boolean bAlreadyContained = false;

        if(users != null)
        {
            if(users.indexOf(myUserName + ";") != -1)
                bAlreadyContained = true;
        }
        else users = "";

        if(!bAlreadyContained)
        {
            users += myUserName + ";";
            properties.setProperty("Users", users);
        }

        properties.setProperty(myUserName + PROPERTY_EXPORT_PPP, Boolean.toString(myAutoExportPPP));
        if(myPolarUserFile != null)
            properties.setProperty(myUserName + PROPERTY_POLARDIR, myPolarUserFile);
        else
        	properties.remove(myUserName + PROPERTY_POLARDIR);
        properties.setProperty(myUserName + PROPERTY_EXPORT_GPX, Boolean.toString(myAutoExportGPX));
        if(myGPXDirectory != null)
            properties.setProperty(myUserName + PROPERTY_GPXDIR, myGPXDirectory);
        else
        	properties.remove(myUserName + PROPERTY_GPXDIR);
        properties.setProperty(myUserName + PROPERTY_SAVE, Boolean.toString(myAutoSave));
        if(mySaveDirectory != null)
            properties.setProperty(myUserName + PROPERTY_SAVEDIR, mySaveDirectory);
        else
        	properties.remove(myUserName + PROPERTY_SAVEDIR);
        if(myImportDirectory != null)
            properties.setProperty(myUserName + PROPERTY_IMPORTDIR, myImportDirectory);
        else
        	properties.remove(myUserName + PROPERTY_IMPORTDIR);

        properties.setProperty(myUserName + PROPERTY_WEIGHT, Integer.toString(myWeight));

        if(myActiveZone != null)
            properties.setProperty(myUserName + PROPERTY_ZONE, myActiveZone.getName());
        else
            properties.remove(myUserName + PROPERTY_ZONE);
        if(myActiveSport != null)
            properties.setProperty(myUserName + PROPERTY_SPORT, myActiveSport.getName());
        else
            properties.remove(myUserName + PROPERTY_SPORT);
    }

    public void remove(Properties properties)
    {
        String users = properties.getProperty("Users");
        String newUsers = null;

        if(users != null)
        {
            int pos = users.indexOf(myUserName + ";");
            if(pos != -1)
            {
                if(pos != 0)
                    newUsers = users.substring(0, pos);
                else
                    newUsers = "";
                if((pos + myUserName.length() + 1) < users.length())
                    newUsers += users.substring(pos + myUserName.length() + 1, users.length());
            }
            properties.setProperty("Users", newUsers);
        }

        properties.remove(myUserName + PROPERTY_EXPORT_PPP);
        properties.remove(myUserName + PROPERTY_POLARDIR);
        properties.remove(myUserName + PROPERTY_EXPORT_GPX);
        properties.remove(myUserName + PROPERTY_GPXDIR);
        properties.remove(myUserName + PROPERTY_SAVE);
        properties.remove(myUserName + PROPERTY_SAVEDIR);

        properties.remove(myUserName + PROPERTY_WEIGHT);

        properties.remove(myUserName + PROPERTY_ZONE);
        properties.remove(myUserName + PROPERTY_SPORT);
    }

    public String getUserName()
    {
        return myUserName;
    }

    public boolean getAutoExportGPX()
    {
        return myAutoExportGPX;
    }

    public void setAutoExportGPX(boolean newAutoExportGPX)
    {
        myAutoExportGPX = newAutoExportGPX;
    }
    
    public boolean getAutoExportPPP()
    {
        return myAutoExportPPP;
    }

    public void setAutoExportPPP(boolean newAutoExportPPP)
    {
        myAutoExportPPP = newAutoExportPPP;
    }

    public void setPolarUserFile(String newPolarUserFile)
    {
        myPolarUserFile = newPolarUserFile;

        initPolarData();
    }

    public String getPolarUserFile()
    {
        return myPolarUserFile;
    }

    public String getPolarDir()
    {
    	if(myPolarUserFile != null)
    		return myPolarUserFile.substring(0, myPolarUserFile.lastIndexOf('\\'));
    	else 
    		return null;
    }
    
    public String getGPXDir()
    {
    	return myGPXDirectory;
    }

    public boolean getAutoSave()
    {
        return myAutoSave;
    }

    public void setAutoSave(boolean newAutoSave)
    {
        myAutoSave = newAutoSave;
    }

    public void setSaveDir(String newSaveDir)
    {
        mySaveDirectory = newSaveDir;
    }
    
    public void setGPXDir(String newGPXDir)
    {
        myGPXDirectory = newGPXDir;
    }

    public String getSaveDir()
    {
        return mySaveDirectory;
    }

    public void setImportDir(String newImportDir)
    {
        myImportDirectory = newImportDir;
    }

    public String getImportDir()
    {
        return myImportDirectory;
    }

    public boolean isInitialized()
    {
        return myIsInitialized;
    }

    public int getMaxHR()
    {
        return myMaxHR;
    }

    public int getRestHR()
    {
        return myRestHR;
    }

    public int getVO2Max()
    {
        return myVO2Max;
    }

    public int getWeight()
    {
        return myWeight;
    }

    public void setWeight(int newWeight)
    {
        myWeight = newWeight;
    }

    public Enumeration getZones()
    {
        if(myZoneSets != null)
        {
            Enumeration e = myZoneSets.keys();
            Vector<String> v = new Vector<String>();
            while (e.hasMoreElements()) {
                v.addElement((String)e.nextElement());
            }
            e = ErgoTools.sort(v).elements();
            
            Vector<PolarZones> v2 = new Vector<PolarZones>();
            while (e.hasMoreElements()) {
                v2.addElement(myZoneSets.get(e.nextElement()));
            }
            return v2.elements();
        }
        else
            return null;
    }

    public void setActiveZone(PolarZones newZone)
    {
        myActiveZone = newZone;
    }

    public PolarZones getActiveZone()
    {
        return myActiveZone;
    }

    public Enumeration getSports()
    {
        if(mySportSets != null)
        {
            Enumeration e = mySportSets.keys();
            Vector<String> v = new Vector<String>();
            while (e.hasMoreElements()) {
                v.addElement((String)e.nextElement());
            }
            e = ErgoTools.sort(v).elements();

            Vector<PolarSport> v2 = new Vector<PolarSport>();
            
            while (e.hasMoreElements()) {
                v2.addElement((PolarSport)mySportSets.get(e.nextElement()));
            }
            return v2.elements();
        }
        else
            return null;
    }

    public void setActiveSport(PolarSport newSport)
    {
        myActiveSport = newSport;
    }

    public PolarSport getActiveSport()
    {
        return myActiveSport;
    }
}