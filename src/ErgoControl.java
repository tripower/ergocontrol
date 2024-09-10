

import forms.ErgoControlAbout;
import forms.ErgoControlBikeset;
import forms.ErgoControlConfig;
import forms.ErgoControlProgram;
import forms.ErgoControlProgramSelect;
import forms.ErgoControlUser;
import graph.ErgoGraph;
import graph.ErgoLineGraph;
import graph.ErgoLineGraphEvent;
import graph.ErgoLineGraphListener;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Pattern;
import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import net.ErgoControlClient;
import net.ErgoControlServer;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import control.*;

import data.ErgoBikeDefinition;
import data.ErgoData;
import data.ErgoDatastore;
import data.ErgoUserData;
import data.ServerData;

import tools.ErgoBikeAdministration;
import tools.ErgoFileFilter;
import tools.ErgoTools;
import tools.ErgoUserAdministration;

import javax.swing.*;
import javax.swing.Timer;


/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 13.02.2005
 * Time: 15:48:20
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControl extends JFrame implements ActionListener, WindowListener, PropertyChangeListener, ErgoControlListener, ErgoLineGraphListener {
	static final long serialVersionUID = 0;

    private static Properties myProperties = new Properties();
    private static Logger log = null;

    private final static int DEFAULT_SAMPLERATE = 1000; //1 sec

    private final static String PROPERTY_FILE = "ErgoControl.cnf";
    private final static String PROPERTY_LOGGER = "loggerConfFile";
    private final static String PROPERTY_ACTIVEUSER = "ActiveUser";
    private final static String PROPERTY_ACTIVEBIKE = "ActiveBike";

    private final static String FORBIDDEN_CHARS = "/\\:*?\"<>|";

    private final static String DEFAULT_SAVE_FILENAME_EXTENSION = ".csv";
    private final static String DEFAULT_GPX_FILENAME_EXTENSION = ".gpx";
    private final static String DEFAULT_HRM_FILENAME_EXTENSION = ".hrm";

    private final static String TITLE_APP = "ErgoControl";
    private final static String TITLE_OPEN = TITLE_APP + " - Open File...";
    private final static String TITLE_IMPORT = TITLE_APP + " - Import File...";
    private final static String TITLE_SAVE = TITLE_APP + " - Save File...";
    private final static String TITLE_EXPORT = TITLE_APP + " - Export File...";
    private final static String TITLE_CONVERT = TITLE_APP + " - Convert File...";
    private final static String DIALOG_EXPORT_SAVE = "Save and/or Export";
    private final static String LABEL_NOTE = "Please enter Note for training (default is " + ErgoTools.DEFAULT_NAME + ")";
    private final static String FILEMENU = "File";
    private final static String FILEMENU_NEW = "New";
    private final static String FILEMENU_NEW_TOOLTIP = "Creates a new program";
    private final static String FILEMENU_OPEN = "Open";
    private final static String FILEMENU_OPEN_TOOLTIP = "Opens a program";
    private final static String FILEMENU_IMPORT = "Import";
    private final static String FILEMENU_IMPORT_TOOLTIP = "Imports a program";
    private final static String FILEMENU_SERVER_START = "Start Server";
    private final static String FILEMENU_SERVER_START_TOOLTIP = "Starts a server";
    private final static String FILEMENU_SERVER_STOP = "Stop Server";
    private final static String FILEMENU_SERVER_STOP_TOOLTIP = "Stops a server";
    private final static String FILEMENU_CONNECT = "Connect";
    private final static String FILEMENU_CONNECT_TOOLTIP = "Connects to a server";
    private final static String FILEMENU_CLOSE = "Close";
    private final static String FILEMENU_CLOSE_TOOLTIP = "Close current program";
    private final static String FILEMENU_SAVE = "Save";
    private final static String FILEMENU_SAVE_TOOLTIP = "Save recording";
    private final static String FILEMENU_EXPORT_PPP = "Export > PPP";
    private final static String FILEMENU_EXPORT_GPX = "Export > GPX";
    private final static String FILEMENU_EXPORT_PPP_TOOLTIP = "Export into Polar Precision Performance";
    private final static String FILEMENU_EXPORT_GPX_TOOLTIP = "Export into GPX";
    private final static String FILEMENU_CONVERT_PPP = "Convert > PPP";
    private final static String FILEMENU_CONVERT_PPP_TOOLTIP = "Convert Recording into Polar Precision Performance";
    private final static String FILEMENU_CONVERT_GPX = "Convert > GPX";
    private final static String FILEMENU_CONVERT_GPX_TOOLTIP = "Convert Recording into GPX";
    private final static String FILEMENU_CONFIG = "Config";
    private final static String FILEMENU_CONFIG_TOOLTIP = "Open configuration";
    private final static String FILEMENU_EXIT = "Exit";
    private final static String FILEMENU_EXIT_TOOLTIP = "Exit ErgoControl";
    private final static String CONTROLMENU = "Control";
    private final static String CONTROLMENU_START = "Start";
    private final static String CONTROLMENU_START_TOOLTIP = "Start a new training";
    private final static String CONTROLMENU_STOP = "Stop";
    private final static String CONTROLMENU_STOP_TOOLTIP = "Stop current training";
    private final static String DISPLAYMENU = "Display";
    private final static String DISPLAYMENU_PROGRAM = "Show Program";
    private final static String DISPLAYMENU_PROGRAM_TOOLTIP = "Show Program as curve";
    private final static String DISPLAYMENU_RECORDING = "Show Recording";
    private final static String DISPLAYMENU_RECORDING_TOOLTIP = "Show Recording as curve";
    private final static String DISPLAYMENU_BARS = "Show Bars";
    private final static String DISPLAYMENU_BARS_TOOLTIP = "Show Data as bars";
    private final static String DISPLAYMENU_DIALS= "Show Dials";
    private final static String DISPLAYMENU_DIALS_TOOLTIP = "Show Recording as dials";
    private final static String DISPLAYMENU_MAP= "Show Map";
    private final static String DISPLAYMENU_MAP_TOOLTIP = "Show Recording as map";
    private final static String DISPLAYMENU_NONE= "Show Graph only";
    private final static String DISPLAYMENU_NONE_TOOLTIP = "Show Recording graph only";
    private final static String USERMENU = "User";
    private final static String USERMENU_CREATE = "Create";
    private final static String USERMENU_CREATE_TOOLTIP = "Create a new user";
    private final static String USERMENU_REMOVE = "Remove";
    private final static String USERMENU_REMOVE_TOOLTIP = "Remove current user";
    private final static String USERMENU_LOGOUT = "Logout";
    private final static String USERMENU_LOGOUT_TOOLTIP = "Logout current user";
    private final static String BIKEMENU = "Bike";
    private final static String BIKEMENU_CREATE = "Create";
    private final static String BIKEMENU_CREATE_TOOLTIP = "Create a new bikeset";
    private final static String BIKEMENU_EDIT = "Edit";
    private final static String BIKEMENU_EDIT_TOOLTIP = "Edits actual bikeset";
    private final static String BIKEMENU_REMOVE = "Remove";
    private final static String BIKEMENU_REMOVE_TOOLTIP = "Remove current bikeset";
    private final static String HELPMENU = "Help";
    private final static String HELPMENU_ABOUT = "About";
    private final static String HELPMENU_ABOUT_TOOLTIP = "About this software";

    private final static String MESSAGE_SAVED = "Data saved!";
    private final static String MESSAGE_EXPORTED = "Data exported!";
    private final static String MESSAGE_CONVERTED = "Data converted!";

    private final int MESSAGE_TIMEOUT = 5; //sec

    private final static String ERROR_LOGDETAIL = "For further Information check log entries.";
    private final static String ERROR_START_LOG = "Failed to start logging!";
    private final static String ERROR_READ_CONFIG = "Failed to read configuration file: " + PROPERTY_FILE + "!";
    private final static String ERROR_WRITE_CONFIG = "Failed to write configuration file: " + PROPERTY_FILE + "!\n" + ERROR_LOGDETAIL;
    private final static String ERROR_UNDEFINED = "An unhandeld error occured!\n" + ERROR_LOGDETAIL;
    private final static String ERROR_POLAR_NOT_INITIALIZED = "Polar Userdata is not initialized!\nPlease select correct User.ppd file, automatic export disabled!";
    private final static String ERROR_ZONE_NOT_EXISTS = "HR-Zone is not set!\nPlease select HR-Zone, automatic export disabled!";
    private final static String ERROR_SPORT_NOT_EXISTS = "Sport is not set!\nPlease select sport, automatic export disabled!";
    private final static String ERROR_SAVEDIR_NOT_EXISTS = "Save Directory is not set or does not exist!\nPlease select Directory or disable automatic save!";
    private final static String ERROR_FORBIDDEN_CHARS = "Following characters are disallowed: " + FORBIDDEN_CHARS;
    private final static String ERROR_NO_DEVICE = "No device selected!\nPlease select device and start training again!";
    private final static String ERROR_CONVERSION_FAILED = "Conversion failed!";
    private final static String ERROR_ON_IMPORT = "Import failed! Check log for details.";
    private final static String ERROR_ON_LOAD = "Load failed! Check log for details.";
    private final static String ERROR_FILE_FORMAT = "Unsupported file format!";
    private final static String ERROR_CONNECT = "Connection failed!";
    private final static String ERROR_GETPROGRAM = "Failed to download program!";

    private final static String PROPERTY_GRAPH = "GraphMode";

    private final static int WINDOW_WIDTH = 1000;
    private final static int WINDOW_HEIGHT = 700;
    private final static int CENTER_WIDTH = 1000;
    private final static int CENTER_HEIGHT = 680;
    private final static int SOUTH_WIDTH = 1000;
    private final static int SOUTH_HEIGHT = 20;
    private final static int DATA_EAST_WIDTH = 500;
    private final static int DATA_EAST_HEIGHT = 680;
    private final static int DATA_CENTER_WIDTH = 500;
    private final static int DATA_CENTER_HEIGHT = 680;
    private final static int DATA_SOUTH_WIDTH = 1000;
    private final static int DATA_SOUTH_HEIGHT = 0;

    private JMenuBar myMenuBar = new JMenuBar();
    private JMenu myFileMenu = new JMenu(FILEMENU);
    private JMenuItem myFileMenu_New = new JMenuItem(FILEMENU_NEW);
    private JMenuItem myFileMenu_Open = new JMenuItem(FILEMENU_OPEN);
    private JMenuItem myFileMenu_Import = new JMenuItem(FILEMENU_IMPORT);
    private JMenuItem myFileMenu_Server_Start = new JMenuItem(FILEMENU_SERVER_START);
    private JMenuItem myFileMenu_Server_Stop = new JMenuItem(FILEMENU_SERVER_STOP);
    private JMenuItem myFileMenu_Connect = new JMenuItem(FILEMENU_CONNECT);
    private JMenuItem myFileMenu_Save = new JMenuItem(FILEMENU_SAVE);
    private JMenuItem myFileMenu_Export_PPP = new JMenuItem(FILEMENU_EXPORT_PPP);
    private JMenuItem myFileMenu_Export_GPX = new JMenuItem(FILEMENU_EXPORT_GPX);
    private JMenuItem myFileMenu_Convert_PPP = new JMenuItem(FILEMENU_CONVERT_PPP);
    private JMenuItem myFileMenu_Convert_GPX = new JMenuItem(FILEMENU_CONVERT_GPX);
    private JMenuItem myFileMenu_Close = new JMenuItem(FILEMENU_CLOSE);
    private JMenuItem myFileMenu_Config = new JMenuItem(FILEMENU_CONFIG);
    private JMenuItem myFileMenu_Exit = new JMenuItem(FILEMENU_EXIT);
    private JMenu myControlMenu = new JMenu(CONTROLMENU);
    private JMenuItem myControlMenu_Start = new JMenuItem(CONTROLMENU_START);
    private JMenuItem myControlMenu_Stop = new JMenuItem(CONTROLMENU_STOP);
    private JMenu myDisplayMenu = new JMenu(DISPLAYMENU);
    private ButtonGroup myDisplayMenu_GroupLineGraph = new ButtonGroup();
    private ButtonGroup myDisplayMenu_GroupGraph = new ButtonGroup();
    private JRadioButtonMenuItem myDisplayMenu_Program = new JRadioButtonMenuItem(DISPLAYMENU_PROGRAM);
    private JRadioButtonMenuItem myDisplayMenu_Recording = new JRadioButtonMenuItem(DISPLAYMENU_RECORDING);
    private JRadioButtonMenuItem myDisplayMenu_Bars = new JRadioButtonMenuItem(DISPLAYMENU_BARS);
    private JRadioButtonMenuItem myDisplayMenu_Dials = new JRadioButtonMenuItem(DISPLAYMENU_DIALS);
    private JRadioButtonMenuItem myDisplayMenu_Map = new JRadioButtonMenuItem(DISPLAYMENU_MAP);
    private JRadioButtonMenuItem myDisplayMenu_None = new JRadioButtonMenuItem(DISPLAYMENU_NONE);
    private JMenu myUserMenu = new JMenu(USERMENU);
    private JMenuItem myUserMenu_Create = new JMenuItem(USERMENU_CREATE);
    private JMenuItem myUserMenu_Remove = new JMenuItem(USERMENU_REMOVE);
    private JMenuItem myUserMenu_Logout = new JMenuItem(USERMENU_LOGOUT);
    private JMenu myBikeMenu = new JMenu(BIKEMENU);
    private JMenuItem myBikeMenu_Create = new JMenuItem(BIKEMENU_CREATE);
    private JMenuItem myBikeMenu_Edit = new JMenuItem(BIKEMENU_EDIT);
    private JMenuItem myBikeMenu_Remove = new JMenuItem(BIKEMENU_REMOVE);
    private JMenu myHelpMenu = new JMenu(HELPMENU);
    private JMenuItem myHelpMenu_About = new JMenuItem(HELPMENU_ABOUT);

    private JPanel myInfoWindow = new JPanel();
    private JLabel myInfo = new JLabel();
    private JLabel myInfoTime = new JLabel();
    private Vector<String> myAdditionalInfo = new Vector<String>();
    private Hashtable<String,Integer> myAdditionalInfoTimeout = new Hashtable<String,Integer>();

    private JLabel myPuls = new JLabel();
    private JLabel myPulsLabel = new JLabel(ErgoTools.LABEL_PULS);
    private JLabel myRPM = new JLabel();
    private JLabel myRPMLabel = new JLabel(ErgoTools.LABEL_RPM);
    private JLabel mySpeed = new JLabel();
    private JLabel mySpeedLabel = new JLabel(ErgoTools.LABEL_SPEED);
    private JLabel myDistance = new JLabel();
    private JLabel myDistanceLabel = new JLabel(ErgoTools.LABEL_DISTANCE);
    private JLabel myToGo = new JLabel();
    private JLabel myToGoLabel = new JLabel(ErgoTools.LABEL_TOGO);
    private JLabel myGrade = new JLabel();
    private JLabel myGradeLabel = new JLabel(ErgoTools.LABEL_GRADE);
    private JLabel myGear = new JLabel();
    private JLabel myGearLabel = new JLabel(ErgoTools.LABEL_GEAR);
//    private JLabel myRing = new JLabel();
//    private JLabel myRingLabel = new JLabel(ErgoTools.LABEL_RING);
    private JLabel myPower = new JLabel();
    private JLabel myPowerLabel = new JLabel(ErgoTools.LABEL_POWER);
    //private JLabel myKilojoule = new JLabel();
    //private JLabel myKilojouleLabel = new JLabel(ErgoTools.LABEL_KILOJOULE);
    private JLabel myTime = new JLabel();
    private JLabel myTimeLabel = new JLabel(ErgoTools.LABEL_TIME);
    private JLabel myRealTime = new JLabel();
    private JLabel myRealTimeLabel = new JLabel(ErgoTools.LABEL_REALTIME);

    private JPanel myDataWindow = new JPanel();
    private JPanel myDataValuesWindow = new JPanel();
    private ErgoGraph myDataGraphWindow = new ErgoGraph();
    private ErgoLineGraph myProgramWindow = new ErgoLineGraph();
    private JScrollPane myProgramWindowScroll = new JScrollPane();

    private ErgoControlInterface myErgoControl = null;
    private ErgoControlConfig myErgoControlConfig = null;

    private ErgoDatastore myErgoDatastore = null;
    private ErgoDatastore myErgoDatastoreProgram = null;

    private ErgoUserAdministration myErgoUserAdministration = null;
    private ErgoBikeAdministration myErgoBikeAdministration = null;
    private ErgoUserData myActiveUser = null;
    private ErgoBikeDefinition myActiveBike = null;

    private Timer myTimer = new Timer(1000, this);

    static ErgoData myLastData = null;
    static Hashtable<String,ErgoData> myLastDataArray = null;
    static ErgoBikeDefinition myLastBike = null;

    private boolean myScreenInitialized = false;

    private static ErgoControlServer myServer = null;
    private static ErgoControlClient myClient = null;

    public ErgoControl()
    {
        super();

        setResizable(false);
        setTitle(TITLE_APP);

        myFileMenu_New.addActionListener(this);
        myFileMenu_Open.addActionListener(this);
        myFileMenu_Import.addActionListener(this);
        myFileMenu_Server_Start.addActionListener(this);
        myFileMenu_Server_Stop.addActionListener(this);
        myFileMenu_Connect.addActionListener(this);
        myFileMenu_Close.addActionListener(this);
        myFileMenu_Save.addActionListener(this);
        myFileMenu_Export_PPP.addActionListener(this);
        myFileMenu_Export_GPX.addActionListener(this);
        myFileMenu_Convert_PPP.addActionListener(this);
        myFileMenu_Convert_GPX.addActionListener(this);
        myFileMenu_Config.addActionListener(this);
        myFileMenu_Exit.addActionListener(this);

        myFileMenu_New.setToolTipText(FILEMENU_NEW_TOOLTIP);
        myFileMenu_Open.setToolTipText(FILEMENU_OPEN_TOOLTIP);
        myFileMenu_Import.setToolTipText(FILEMENU_IMPORT_TOOLTIP);
        myFileMenu_Server_Start.setToolTipText(FILEMENU_SERVER_START_TOOLTIP);
        myFileMenu_Server_Stop.setToolTipText(FILEMENU_SERVER_STOP_TOOLTIP);
        myFileMenu_Connect.setToolTipText(FILEMENU_CONNECT_TOOLTIP);
        myFileMenu_Close.setToolTipText(FILEMENU_CLOSE_TOOLTIP);
        myFileMenu_Save.setToolTipText(FILEMENU_SAVE_TOOLTIP);
        myFileMenu_Export_PPP.setToolTipText(FILEMENU_EXPORT_PPP_TOOLTIP);
        myFileMenu_Export_GPX.setToolTipText(FILEMENU_EXPORT_GPX_TOOLTIP);
        myFileMenu_Convert_PPP.setToolTipText(FILEMENU_CONVERT_PPP_TOOLTIP);
        myFileMenu_Convert_GPX.setToolTipText(FILEMENU_CONVERT_GPX_TOOLTIP);
        myFileMenu_Config.setToolTipText(FILEMENU_CONFIG_TOOLTIP);
        myFileMenu_Exit.setToolTipText(FILEMENU_EXIT_TOOLTIP);

        myFileMenu.add(myFileMenu_New);
        myFileMenu.add(myFileMenu_Open);
        myFileMenu.add(myFileMenu_Import);
        myFileMenu.add(myFileMenu_Server_Start);
        myFileMenu_Server_Stop.setEnabled(false);
        myFileMenu.add(myFileMenu_Server_Stop);
        myFileMenu.add(myFileMenu_Connect);
        myFileMenu.add(myFileMenu_Close);
        myFileMenu.addSeparator();
        myFileMenu.add(myFileMenu_Save);
        myFileMenu.add(myFileMenu_Export_PPP);
        myFileMenu.add(myFileMenu_Export_GPX);
        myFileMenu.addSeparator();
        myFileMenu.add(myFileMenu_Convert_PPP);
        myFileMenu.add(myFileMenu_Convert_GPX);
        myFileMenu.addSeparator();
        myFileMenu.add(myFileMenu_Config);
        myFileMenu.addSeparator();
        myFileMenu.add(myFileMenu_Exit);
        myMenuBar.add(myFileMenu);

        myControlMenu_Start.addActionListener(this);
        myControlMenu_Stop.addActionListener(this);

        myDisplayMenu_Program.addActionListener(this);
        myDisplayMenu_Recording.addActionListener(this);
        myDisplayMenu_Bars.addActionListener(this);
        myDisplayMenu_Dials.addActionListener(this);
        myDisplayMenu_Map.addActionListener(this);
        myDisplayMenu_None.addActionListener(this);

        myControlMenu_Start.setToolTipText(CONTROLMENU_START_TOOLTIP);
        myControlMenu_Stop.setToolTipText(CONTROLMENU_STOP_TOOLTIP);

        myDisplayMenu_Program.setToolTipText(DISPLAYMENU_PROGRAM_TOOLTIP);
        myDisplayMenu_Recording.setToolTipText(DISPLAYMENU_RECORDING_TOOLTIP);
        myDisplayMenu_Bars.setToolTipText(DISPLAYMENU_BARS_TOOLTIP);
        myDisplayMenu_Dials.setToolTipText(DISPLAYMENU_DIALS_TOOLTIP);
        myDisplayMenu_Map.setToolTipText(DISPLAYMENU_MAP_TOOLTIP);
        myDisplayMenu_None.setToolTipText(DISPLAYMENU_NONE_TOOLTIP);

        myControlMenu_Stop.setEnabled(false);
        myControlMenu.add(myControlMenu_Start);
        myControlMenu.add(myControlMenu_Stop);
        myMenuBar.add(myControlMenu);

        myDisplayMenu.add(myDisplayMenu_Program);
        myDisplayMenu.add(myDisplayMenu_Recording);
        myDisplayMenu.addSeparator();
        myDisplayMenu.add(myDisplayMenu_Bars);
        myDisplayMenu.add(myDisplayMenu_Dials);
        myDisplayMenu.add(myDisplayMenu_Map);
        myDisplayMenu.add(myDisplayMenu_None);
        myMenuBar.add(myDisplayMenu);

        myDisplayMenu_GroupLineGraph.add(myDisplayMenu_Program);
        myDisplayMenu_GroupLineGraph.add(myDisplayMenu_Recording);

        myDisplayMenu_GroupGraph.add(myDisplayMenu_Bars);
        myDisplayMenu_GroupGraph.add(myDisplayMenu_Dials);
        myDisplayMenu_GroupGraph.add(myDisplayMenu_Map);
        myDisplayMenu_GroupGraph.add(myDisplayMenu_None);

        myUserMenu_Create.addActionListener(this);
        myUserMenu_Remove.addActionListener(this);
        myUserMenu_Logout.addActionListener(this);

        myUserMenu_Create.setToolTipText(USERMENU_CREATE_TOOLTIP);
        myUserMenu_Remove.setToolTipText(USERMENU_REMOVE_TOOLTIP);
        myUserMenu_Logout.setToolTipText(USERMENU_LOGOUT_TOOLTIP);
        myMenuBar.add(myUserMenu);

        myBikeMenu_Create.addActionListener(this);
        myBikeMenu_Edit.addActionListener(this);
        myBikeMenu_Remove.addActionListener(this);

        myBikeMenu_Create.setToolTipText(BIKEMENU_CREATE_TOOLTIP);
        myBikeMenu_Edit.setToolTipText(BIKEMENU_EDIT_TOOLTIP);
        myBikeMenu_Remove.setToolTipText(BIKEMENU_REMOVE_TOOLTIP);
        myMenuBar.add(myBikeMenu);

        myHelpMenu.add(myHelpMenu_About);

        myHelpMenu_About.addActionListener(this);
        myHelpMenu_About.setToolTipText(HELPMENU_ABOUT_TOOLTIP);
        myMenuBar.add(myHelpMenu);

        setJMenuBar(myMenuBar);

        myPulsLabel.setForeground(ErgoTools.COLOR_PULS);
        myRPMLabel.setForeground(ErgoTools.COLOR_RPM);
        mySpeedLabel.setForeground(ErgoTools.COLOR_SPEED);
        myDistanceLabel.setForeground(ErgoTools.COLOR_DISTANCE);
        myToGoLabel.setForeground(ErgoTools.COLOR_DISTANCE);
        myPowerLabel.setForeground(ErgoTools.COLOR_POWER);
        //myKilojouleLabel.setForeground(ErgoTools.COLOR_KILOJOULE);
        myTimeLabel.setForeground(ErgoTools.COLOR_TIME);
        myRealTimeLabel.setForeground(ErgoTools.COLOR_REALTIME);
        myGradeLabel.setForeground(ErgoTools.COLOR_GRADE);
        myGearLabel.setForeground(ErgoTools.COLOR_GEAR);
//        myRingLabel.setForeground(ErgoTools.COLOR_RING);

        myPuls.setForeground(ErgoTools.COLOR_PULS);
        myRPM.setForeground(ErgoTools.COLOR_RPM);
        mySpeed.setForeground(ErgoTools.COLOR_SPEED);
        myDistance.setForeground(ErgoTools.COLOR_DISTANCE);
        myToGoLabel.setForeground(ErgoTools.COLOR_DISTANCE);
        myPower.setForeground(ErgoTools.COLOR_POWER);
        //myKilojoule.setForeground(ErgoTools.COLOR_KILOJOULE);
        myTime.setForeground(ErgoTools.COLOR_TIME);
        myRealTime.setForeground(ErgoTools.COLOR_REALTIME);
        myGrade.setForeground(ErgoTools.COLOR_GRADE);
        myGear.setForeground(ErgoTools.COLOR_GEAR);
//        myRing.setForeground(ErgoTools.COLOR_RING);

        myInfoWindow.setLayout(new BorderLayout());
        myInfoWindow.add(myInfo, BorderLayout.CENTER);
        myInfoWindow.add(myInfoTime, BorderLayout.EAST);

        myDataValuesWindow.setLayout(new GridLayout(4,6));
        myDataValuesWindow.setBackground(Color.WHITE);
        myDataValuesWindow.add(myPulsLabel);
        myDataValuesWindow.add(myPuls);
        myDataValuesWindow.add(myRPMLabel);
        myDataValuesWindow.add(myRPM);
        myDataValuesWindow.add(mySpeedLabel);
        myDataValuesWindow.add(mySpeed);
        myDataValuesWindow.add(myPowerLabel);
        myDataValuesWindow.add(myPower);
        myDataValuesWindow.add(myDistanceLabel);
        myDataValuesWindow.add(myDistance);
        myDataValuesWindow.add(myToGoLabel);
        myDataValuesWindow.add(myToGo);
        //myDataValuesWindow.add(myKilojouleLabel);
        //myDataValuesWindow.add(myKilojoule);
        myDataValuesWindow.add(myGradeLabel);
        myDataValuesWindow.add(myGrade);
        myDataValuesWindow.add(myTimeLabel);
        myDataValuesWindow.add(myTime);
        myDataValuesWindow.add(myRealTimeLabel);
        myDataValuesWindow.add(myRealTime);
        myDataValuesWindow.add(myGearLabel);
        myDataValuesWindow.add(myGear);
        myDataValuesWindow.add(new JLabel());
        myDataValuesWindow.add(new JLabel());
        myDataValuesWindow.add(new JLabel());
        myDataValuesWindow.add(new JLabel());
//        myDataValuesWindow.add(myRingLabel);
//        myDataValuesWindow.add(myRing);

        myDataWindow.setLayout(new BorderLayout());

        myProgramWindow.addErgoLineGraphListener(this);
        myProgramWindowScroll.setViewportView(myProgramWindow);
        myDataWindow.add(myProgramWindowScroll, BorderLayout.CENTER);

        myDataWindow.add(myDataValuesWindow, BorderLayout.SOUTH);

        myDataWindow.add(myDataGraphWindow, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(myDataWindow, BorderLayout.CENTER);

        getContentPane().add(myInfoWindow, BorderLayout.SOUTH);

        addWindowListener(this);

        myErgoControlConfig = new ErgoControlConfig(this, myProperties, myActiveUser);
        refresh(false, true);

        myTimer.start();
    }

    public static void main(String[] args)
    {
    	try
        {
            try
            {
                File confFile = new File(PROPERTY_FILE);
                FileInputStream fileInput = new FileInputStream(confFile);

                //wir lesen die angegebene Datei als Properties ein.
                myProperties.load(fileInput);
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, ERROR_READ_CONFIG + "\n" + e.getMessage());
                e.printStackTrace();
            }

            //Wir versuchen den Logger mit Hilfe seines Konfigurationsfiles zu starten
            try
            {
                log = Logger.getLogger(ErgoControl.class);
                PropertyConfigurator.configure(myProperties.getProperty(PROPERTY_LOGGER));  // set the Logger CNF file
                log.debug("Logger started successfully");
            }
            catch (Exception e)
            {
                log = null;
                JOptionPane.showMessageDialog(null, ERROR_START_LOG);
                e.printStackTrace();
            }

            if(log != null)
            {
            	ErgoControl me = new ErgoControl();
                me.setVisible(true);
            }

        } catch(Exception e)
        {
            String msg = ERROR_UNDEFINED;

            if(log != null)
            {
                msg += "\n" + ERROR_LOGDETAIL;
                log.error(e);
            }
            else
            {
                msg += "\n" + e.getMessage();
                e.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, msg);
        }

        return;
    }

    /*private void resize()
    {
    	resize(myErgoControlConfig.getWindowSize(), false);
    }*/

    private void resize(double factor, boolean renew)
    {
        int width = (int)(WINDOW_WIDTH * factor);
        int height = (int)(WINDOW_HEIGHT * factor);

        ErgoTools.resizeFont(factor);
        Font font = ErgoTools.DEFAULT_FONT;
        myPulsLabel.setFont(font);
        myRPMLabel.setFont(font);
        mySpeedLabel.setFont(font);
        myDistanceLabel.setFont(font);
        myToGoLabel.setFont(font);
        myPowerLabel.setFont(font);
        //myKilojouleLabel.setFont(font);
        myTimeLabel.setFont(font);
        myRealTimeLabel.setFont(font);
        myGradeLabel.setFont(font);
        myGearLabel.setFont(font);
//        myRingLabel.setFont(font);

        myPuls.setFont(font);
        myRPM.setFont(font);
        mySpeed.setFont(font);
        myDistance.setFont(font);
        myToGo.setFont(font);
        myPower.setFont(font);
        //myKilojoule.setFont(font);
        myTime.setFont(font);
        myRealTime.setFont(font);
        myGrade.setFont(font);
        myGear.setFont(font);
//        myRing.setFont(font);

        if(width > java.awt.Toolkit.getDefaultToolkit().getScreenSize().width)
        {
            width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            factor = (double)width / WINDOW_WIDTH;
            height = (int)(WINDOW_HEIGHT * factor);
        }
        if(height > java.awt.Toolkit.getDefaultToolkit().getScreenSize().height)
        {
            height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
            factor = (double)height / WINDOW_HEIGHT;
            width = (int)(WINDOW_WIDTH * factor);
        }
        setSize(width,height);
        int posX = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width/2)-(width/2);
        int posY = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height/2)-(height/2);
        if(posX < 0) posX = 0;
        if(posY < 0) posY = 0;
        setLocation(posX,posY);

        if(!myScreenInitialized)
        {
	        width = (int)(DATA_SOUTH_WIDTH * factor);
	        height = (int)((DATA_SOUTH_HEIGHT+ErgoTools.DEFAULT_FONT_SIZE_FULL*4/factor) * factor);
        }
        else
        {
        	width = (int)(myDataValuesWindow.getWidth() * factor);
        	height = (int)(myDataValuesWindow.getHeight() * factor);
        }
        Dimension d = new Dimension(width, height);
        myDataValuesWindow.setSize(d);
        myDataValuesWindow.setPreferredSize(d);

        if(!myScreenInitialized)
        {
	        width = (int)(SOUTH_WIDTH * factor);
	        height = (int)(SOUTH_HEIGHT * factor);
        }
        else
        {
        	width = (int)(myInfoWindow.getWidth() * factor);
        	height = (int)(myInfoWindow.getHeight() * factor);
        }
        d = new Dimension(width, height);
        myInfoWindow.setSize(d);
        myInfoWindow.setPreferredSize(d);

        if(!myScreenInitialized)
        {
	        width = (int)(DATA_EAST_WIDTH * factor);
	        height = (int)((DATA_EAST_HEIGHT-ErgoTools.DEFAULT_FONT_SIZE_FULL*4/factor) * factor);
        }
        else
        {
        	width = (int)(myDataGraphWindow.getWidth() * factor);
        	height = (int)(myDataGraphWindow.getHeight() * factor);
        }
        d = new Dimension(width, height);
        myDataGraphWindow.setSize(d);
        myDataGraphWindow.setPreferredSize(d);

        if(!myScreenInitialized)
        {
	        width = (int)(DATA_CENTER_WIDTH * factor);
	        height = (int)((DATA_CENTER_HEIGHT-ErgoTools.DEFAULT_FONT_SIZE_FULL*4/factor) * factor);
        }
        else
        {
        	width = (int)(myProgramWindowScroll.getWidth() * factor);
        	height = (int)(myProgramWindowScroll.getHeight() * factor);
        }
        d = new Dimension(width, height);
        myProgramWindowScroll.setSize(d);
        myProgramWindowScroll.setPreferredSize(d);

        if(!myScreenInitialized)
        {
	        width = (int)(CENTER_WIDTH * factor);
	        height = (int)(CENTER_HEIGHT * factor);
        }
        else
        {
        	width = (int)(myDataWindow.getWidth() * factor);
        	height = (int)(myDataWindow.getHeight() * factor);
        }
        d = new Dimension(width, height);
        myDataWindow.setSize(d);
        myDataWindow.setPreferredSize(d);

        if(renew)
        {
            myErgoControlConfig = new ErgoControlConfig(this, myProperties, myActiveUser);
            myErgoControlConfig.addPropertyChangeListener2(this);
            myErgoControlConfig.setWindowSize(factor);
            myErgoControlConfig.save();
        }

        this.setVisible(true);
        refreshData(null, null, null);//repaint();

        //myScreenInitialized = true;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
    	if(evt.getPropertyName().equals(ErgoControlConfig.PROPERTY_DAMPINGRATE))
    	{
    		if(myErgoDatastoreProgram != null)
    		{
    			myErgoDatastoreProgram.calculateGradiation(((Integer)evt.getNewValue()).intValue());
    			this.myProgramWindow.calcLines();
    			myDataGraphWindow.repaint();
    		}

    	}
        if(evt.getPropertyName().equals(ErgoControlConfig.PROPERTY_WINDOW_SIZE))
        {
            resize(((Double)evt.getNewValue()).doubleValue(), false);
            if(myErgoDatastoreProgram != null)
            {
	            setProgram(myErgoDatastoreProgram, false);
	            refreshData(myErgoDatastoreProgram.get(0), null, null);//repaint();
            }
        }
    }

    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getSource() instanceof Timer)
        {
            Enumeration enumInfo = myAdditionalInfo.elements();
            while(enumInfo.hasMoreElements())
            {
                String text = (String)enumInfo.nextElement();
                int timeout = ((Integer)myAdditionalInfoTimeout.get(text)).intValue();

                timeout--;
                if(timeout == 0)
                {
                    myAdditionalInfo.remove(text);
                    myAdditionalInfoTimeout.remove(text);
                }
                else
                    myAdditionalInfoTimeout.put(text, new Integer(timeout));
            }

            refreshData(null, null, null);//repaint();
        }
        else if (evt.getSource() instanceof JMenuItem)
        {
            if (evt.getSource().equals(myFileMenu_New))
            {
                ErgoControlProgramSelect newProgramSelect = new ErgoControlProgramSelect(this);
                ErgoData start = null;

                newProgramSelect.setDistance(true);
                newProgramSelect.setTime(true);
                newProgramSelect.showDialog();

                ErgoControlProgram newProgram = new ErgoControlProgram(this, myProperties, myErgoControlConfig, newProgramSelect.getUseDistance());
                newProgram.setVisible(true);
                if(newProgram.getDatastore() != null)
                {
                	myErgoDatastoreProgram = newProgram.getDatastore();
                	this.disconnect();
                    setProgram(myErgoDatastoreProgram, true);
                    start = myErgoDatastoreProgram.get(0);
                }
                refreshData(start, null, null);//repaint();
                return;

            }
            if (evt.getSource().equals(myFileMenu_Open))
            {
            	ErgoData start = null;
                JFileChooser fileOpen= new JFileChooser();
                fileOpen.setDialogTitle(TITLE_OPEN);
                ErgoFileFilter fileFilter = new ErgoFileFilter();
                fileFilter.addExtension(DEFAULT_SAVE_FILENAME_EXTENSION);
                fileFilter.setDescription("*" + DEFAULT_SAVE_FILENAME_EXTENSION);
                fileOpen.setFileFilter(fileFilter);

                if(myActiveUser != null && myActiveUser.getSaveDir() != null)
                    fileOpen.setCurrentDirectory(new File(myActiveUser.getSaveDir()));
                if(fileOpen.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                {
                    if(myActiveUser != null)
                    {
                        myActiveUser.setSaveDir(fileOpen.getSelectedFile().getPath().substring(0, fileOpen.getSelectedFile().getPath().lastIndexOf('\\')));
                        myActiveUser.save(myProperties);
                    }
                    myErgoDatastoreProgram = new ErgoDatastore(fileOpen.getSelectedFile().getName(), myErgoControlConfig.getSampleRate());
                    if(!myErgoDatastoreProgram.loadFromFile(this, fileOpen.getSelectedFile().getAbsolutePath(), null))
                    {
                    	JOptionPane.showMessageDialog(null, ERROR_ON_LOAD);
                        myErgoDatastoreProgram = null;
                    }
                    else
                		start = myErgoDatastoreProgram.get(0);

                    this.disconnect();
                    setProgram(myErgoDatastoreProgram, true);
                }
                refresh(false, false);
                refreshData(start, null, null);//repaint();
                return;
            }
            if (evt.getSource().equals(myFileMenu_Import))
            {
            	ErgoData start = null;
                JFileChooser fileImport = new JFileChooser();
                fileImport.setDialogTitle(TITLE_IMPORT);
                ErgoFileFilter fileFilter = new ErgoFileFilter();
                fileFilter.addExtension(DEFAULT_HRM_FILENAME_EXTENSION);
                fileFilter.addExtension(DEFAULT_GPX_FILENAME_EXTENSION);
                fileFilter.setDescription("*" + DEFAULT_HRM_FILENAME_EXTENSION + ";*" + DEFAULT_GPX_FILENAME_EXTENSION);
                fileImport.setFileFilter(fileFilter);

                if(myActiveUser != null && myActiveUser.getImportDir() != null)
                    fileImport.setCurrentDirectory(new File(myActiveUser.getImportDir()));
                if(fileImport.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                {
                	String fileName = fileImport.getSelectedFile().getAbsolutePath().toLowerCase();
                	String name = fileImport.getSelectedFile().getName();
                	name = name.substring(0, name.length()-4);
                    if(myActiveUser != null)
                    {
                        myActiveUser.setImportDir(fileImport.getSelectedFile().getPath().substring(0, fileImport.getSelectedFile().getPath().lastIndexOf('\\')));
                        myActiveUser.save(myProperties);
                    }
                    myErgoDatastoreProgram = new ErgoDatastore(name, myErgoControlConfig.getSampleRate());
                    if(fileName.endsWith(DEFAULT_HRM_FILENAME_EXTENSION))
                    {
                    	if(!myErgoDatastoreProgram.importHRM(this, fileName, myErgoControlConfig.getDampingRate()) )
                    	{
                    		JOptionPane.showMessageDialog(null, ERROR_ON_IMPORT);
                    		myErgoDatastoreProgram = null;
                    	}
                    	else
                    		start = myErgoDatastoreProgram.get(0);
                    }
                    else if(fileName.endsWith(DEFAULT_GPX_FILENAME_EXTENSION))
                    {
                    	if(!myErgoDatastoreProgram.importGPX(this, fileName, myErgoControlConfig.getDampingRate()) )
                    	{
                    		JOptionPane.showMessageDialog(null, ERROR_ON_IMPORT);
                    		myErgoDatastoreProgram = null;
                    	}
                    	else
                    		start = myErgoDatastoreProgram.get(0);
                    }
                    else
                    {
                    	JOptionPane.showMessageDialog(null, ERROR_FILE_FORMAT);
                    	myErgoDatastoreProgram = null;
                    }

                    this.disconnect();
                    setProgram(myErgoDatastoreProgram, true);
                }
                refresh(false, false);
                refreshData(start, null, null);//repaint();
                return;
            }
            if (evt.getSource().equals(myFileMenu_Server_Start))
            {
            	serve(true);
            	refresh(false, false);
            	return;
            }
            if (evt.getSource().equals(myFileMenu_Server_Stop))
            {
            	serve(false);
            	refresh(false, false);
            	return;
            }
            if (evt.getSource().equals(myFileMenu_Connect))
            {
            	if(JOptionPane.showConfirmDialog(null, "Use Local Network?", "Choose Network Type", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            	{
	            	String host = "localhost";
	            	host = JOptionPane.showInputDialog("Please Enter Server Address:", host);
	            	String portString = Integer.toString(ErgoTools.PORT);
	            	int port = 0;
	            	/*if(host != null)
	            		portString = JOptionPane.showInputDialog("Please Enter Server Port:", portString);*/
	            	if(portString != null)
	            		port = Integer.parseInt(portString);
	            	if(host != null && portString != null)
	            		this.connect(host, port);
            	}
            	else
            	{
            		try
            		{
            			Hashtable<String,ServerData> servers = new Hashtable<String,ServerData>();
	            		URL ecURL = new URL(ErgoTools.getHTTPUrl(myProperties) + "getservers.php?version=" + ErgoTools.VERSION);
			    		URLConnection ecURLCon = ecURL.openConnection();

			    		ecURLCon.setConnectTimeout(2000);
			    		ecURLCon.connect();
						InputStream is = ecURLCon.getInputStream();

						ByteArrayOutputStream os = new ByteArrayOutputStream();

						byte[] buffer = new byte[ 0xFFFF ];
					    for ( int len; (len = is.read(buffer)) != -1; )
					    	os.write( buffer, 0, len );

					    String answer = os.toString();
					    if(answer.charAt(0) == '0')
					    {
					    	Pattern pat=Pattern.compile("<br>");

					    	String flds[]=pat.split(answer);
					    	for(int i=1;i <flds.length;i++)
					    	{
					    		if(flds[i].length() > 0 && flds[i].charAt(0) != '\n')
					    		{
					    			StringTokenizer tokenizer = new StringTokenizer(flds[i], "\t");
					    			String ip = tokenizer.nextToken();
					    			int port = Integer.parseInt(tokenizer.nextToken());
					    			String host = tokenizer.nextToken();

					    			host += " (" + ip + ")";

					    			servers.put(host, new ServerData(ip, port));
					    		}
					    	}
					    }

					    if(servers.size() > 0)
					    {
					    	ArrayList<String> keys = new ArrayList<String>();
					    	Enumeration<String> enumKeys = servers.keys();
					    	while(enumKeys.hasMoreElements())
					    		keys.add(enumKeys.nextElement());

					    	Object[] keyArray = keys.toArray();
					    	String selectedValue = (String)JOptionPane.showInputDialog(null, "Choose Server:", "Server selection", JOptionPane.INFORMATION_MESSAGE, null, keyArray, keyArray[0]);

					    	if(selectedValue != null)
					    	{
						    	ServerData data = servers.get(selectedValue);
						    	this.connect(data.getIp(), data.getPort());
					    	}
					    }
					    else
					    	JOptionPane.showConfirmDialog(null, "No servers online!", "Servers", JOptionPane.DEFAULT_OPTION);
            		}
            		catch(Exception ex)
            		{
            			String msg = "";
            			if(ex.getMessage() != null)
            				msg = ex.getMessage();
            			JOptionPane.showConfirmDialog(null, "Server connection failed!\n" + msg, "Servers", JOptionPane.DEFAULT_OPTION);
            			log.error(ex);
            		}
            	}
            	return;
            }
            if (evt.getSource().equals(myFileMenu_Save))
            {
                String note = myErgoDatastore.getName();
                boolean bRetry = false;

                if(myActiveUser != null)
                {
                    do
                    {
                        bRetry = false;
                        note = (String)JOptionPane.showInputDialog(this, LABEL_NOTE, DIALOG_EXPORT_SAVE, JOptionPane.PLAIN_MESSAGE, null, null, note);
                        if(note.length() == 0)
                            note = myErgoDatastore.getName();
                        else
                        {
                            if(!ErgoTools.checkCharacters(note, FORBIDDEN_CHARS))
                            {
                                JOptionPane.showMessageDialog(this, ERROR_FORBIDDEN_CHARS);
                                bRetry = true;
                            }
                        }
                    }
                    while(bRetry);
                }

                if(note != null)
                {
                    JFileChooser fileSave = new JFileChooser();
                    fileSave.setDialogTitle(TITLE_SAVE);
                    ErgoFileFilter fileFilter = new ErgoFileFilter();
                    fileFilter.addExtension(DEFAULT_SAVE_FILENAME_EXTENSION);
                    fileFilter.setDescription("*" + DEFAULT_SAVE_FILENAME_EXTENSION);
                    fileSave.setFileFilter(fileFilter);

                    if(myActiveUser != null && myActiveUser.getSaveDir() != null)
                        fileSave.setSelectedFile(new File(myActiveUser.getSaveDir() + "\\" + (new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(myErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_SAVE_FILENAME_EXTENSION));
                    else
                        fileSave.setSelectedFile(new File((new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(myErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_SAVE_FILENAME_EXTENSION));

                    if(myActiveUser == null || myActiveUser.getSaveDir() == null)
                    {
                        if(fileSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                        {
                            if(myActiveUser != null)
                            {
                                myActiveUser.setSaveDir(fileSave.getSelectedFile().getPath().substring(0, fileSave.getSelectedFile().getPath().lastIndexOf('\\')));
                                myActiveUser.save(myProperties);
                            }
                            myErgoDatastore.saveToFile(fileSave.getSelectedFile().getAbsolutePath(), myErgoControlConfig.getSampleRate());
                            addAdditionalInfo(MESSAGE_SAVED);
                        }
                    }
                    if(myActiveUser != null && myActiveUser.getSaveDir() != null)
                    {
                        myErgoDatastore.saveToFile(fileSave.getSelectedFile().getAbsolutePath(), myErgoControlConfig.getSampleRate());
                        addAdditionalInfo(MESSAGE_SAVED);
                    }
                }

                refresh(false, false);
                refreshData(null, null, null);//repaint();
                return;
            }
            if (evt.getSource().equals(myFileMenu_Export_PPP))
            {
                String note = myErgoDatastore.getName();
                boolean bRetry = false;

                if(myActiveUser.getActiveZone() == null)
                {
                    JOptionPane.showMessageDialog(this, ERROR_ZONE_NOT_EXISTS);
                    showConfig();
                    return;
                }
                if(myActiveUser.getActiveSport() == null)
                {
                    JOptionPane.showMessageDialog(this, ERROR_SPORT_NOT_EXISTS);
                    showConfig();
                    return;
                }

                do
                {
                    bRetry = false;
                    note = (String)JOptionPane.showInputDialog(this, LABEL_NOTE, DIALOG_EXPORT_SAVE, JOptionPane.PLAIN_MESSAGE, null, null, note);
                    if(note.length() == 0)
                        note = myErgoDatastore.getName();
                    else
                    {
                        if(!ErgoTools.checkCharacters(note, FORBIDDEN_CHARS))
                        {
                            JOptionPane.showMessageDialog(this, ERROR_FORBIDDEN_CHARS);
                            bRetry = true;
                        }
                    }
                }
                while(bRetry);

                if(note != null)
                {
                    myErgoDatastore.exportPPP(myActiveUser, note);
                    addAdditionalInfo(MESSAGE_EXPORTED);
                }

                refresh(false, false);
                return;
            }
            if (evt.getSource().equals(myFileMenu_Export_GPX))
            {
            	String note = myErgoDatastore.getName();
                boolean bRetry = false;

                if(myActiveUser != null)
                {
                    do
                    {
                        bRetry = false;
                        note = (String)JOptionPane.showInputDialog(this, LABEL_NOTE, DIALOG_EXPORT_SAVE, JOptionPane.PLAIN_MESSAGE, null, null, note);
                        if(note.length() == 0)
                            note = myErgoDatastore.getName();
                        else
                        {
                            if(!ErgoTools.checkCharacters(note, FORBIDDEN_CHARS))
                            {
                                JOptionPane.showMessageDialog(this, ERROR_FORBIDDEN_CHARS);
                                bRetry = true;
                            }
                        }
                    }
                    while(bRetry);
                }

                if(note != null)
                {
                	double lat = 0;
                	double lon = 0;
                    JFileChooser fileSave = new JFileChooser();
                    fileSave.setDialogTitle(TITLE_EXPORT);
                    ErgoFileFilter fileFilter = new ErgoFileFilter();
                    fileFilter.addExtension(DEFAULT_GPX_FILENAME_EXTENSION);
                    fileFilter.setDescription("*" + DEFAULT_GPX_FILENAME_EXTENSION);
                    fileSave.setFileFilter(fileFilter);

                    if(myErgoDatastoreProgram != null && myErgoDatastoreProgram.size() > 0 && myErgoDatastoreProgram.getHasPositionData())
                    {
                    	lat = myErgoDatastoreProgram.get(0).getPosition().getLat();
                    	lon = myErgoDatastoreProgram.get(0).getPosition().getLon();
                    }

                    if(myActiveUser != null && myActiveUser.getSaveDir() != null)
                        fileSave.setSelectedFile(new File(myActiveUser.getSaveDir() + "\\" + (new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(myErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_GPX_FILENAME_EXTENSION));
                    else
                        fileSave.setSelectedFile(new File((new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(myErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_GPX_FILENAME_EXTENSION));

                    if(myActiveUser == null || myActiveUser.getSaveDir() == null)
                    {
                        if(fileSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                        {
                            if(myActiveUser != null)
                            {
                                myActiveUser.setSaveDir(fileSave.getSelectedFile().getPath().substring(0, fileSave.getSelectedFile().getPath().lastIndexOf('\\')));
                                myActiveUser.save(myProperties);
                            }

                            myErgoDatastore.exportGPX(fileSave.getSelectedFile().getAbsolutePath(), note, lat, lon);
                            addAdditionalInfo(MESSAGE_EXPORTED);
                        }
                    }
                    if(myActiveUser != null && myActiveUser.getSaveDir() != null)
                    {
                        myErgoDatastore.exportGPX(fileSave.getSelectedFile().getAbsolutePath(), note, lat, lon);
                        addAdditionalInfo(MESSAGE_EXPORTED);
                    }
                }

                refresh(false, false);
                return;
            }
            if (evt.getSource().equals(myFileMenu_Close))
            {
            	this.disconnect();
            	setProgram(null, true);
            	refresh(false, false);
                return;
            }
            if (evt.getSource().equals(myFileMenu_Convert_PPP))
            {
                String note = null;
                JFileChooser fileOpen= new JFileChooser();
                fileOpen.setDialogTitle(TITLE_CONVERT);
                ErgoDatastore ErgoDatastore = null;
                ErgoFileFilter fileFilter = new ErgoFileFilter();
                fileFilter.addExtension(DEFAULT_SAVE_FILENAME_EXTENSION);
                fileFilter.setDescription("*" + DEFAULT_SAVE_FILENAME_EXTENSION);
                fileOpen.setFileFilter(fileFilter);

                fileOpen.setMultiSelectionEnabled(true);
                if(myActiveUser != null && myActiveUser.getSaveDir() != null)
                    fileOpen.setSelectedFile(new File(myActiveUser.getSaveDir() + "\\*.*"));
                else
                    fileOpen.setSelectedFile(new File(ErgoTools.DEFAULT_NAME + DEFAULT_SAVE_FILENAME_EXTENSION));

                if(fileOpen.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                {
                    File[] files = fileOpen.getSelectedFiles();
                    for(int i=0; i < files.length; i++)
                    {
                        String fileName = files[i].getName();
                        String date = null;
                        try
                        {
                            int pos = fileName.indexOf(" --- ");
                            date = fileName.substring(0,pos);
                            note = fileName.substring(pos+5,fileName.lastIndexOf('.'));
                        }
                        catch(Exception e)
                        {
                            note = fileName;
                        }
                        if(myActiveUser != null)
                        {
                            myActiveUser.setSaveDir(fileOpen.getSelectedFile().getPath().substring(0, fileOpen.getSelectedFile().getPath().lastIndexOf('\\')));
                            myActiveUser.save(myProperties);
                        }

                        ErgoDatastore = new ErgoDatastore(note, myErgoControlConfig.getSampleRate());
                        if(!ErgoDatastore.loadFromFile(this, fileOpen.getSelectedFile().getAbsolutePath(), myActiveBike))
                        {
                        	JOptionPane.showMessageDialog(null, ERROR_ON_LOAD);
                            ErgoDatastore = null;
                        }
                        else
                        {
                            try
                            {
                                ErgoDatastore.setStartTime((new SimpleDateFormat("dd.MM.yyyy HH-mm")).parse(date).getTime());
                            }
                            catch(Exception e)
                            {
                                date = null;
                            }
                            if(date == null)
                            {
                                ErgoDatastore = null;
                            }
                        }

                        if(ErgoDatastore == null)
                            JOptionPane.showMessageDialog(this, ERROR_CONVERSION_FAILED);
                        else
                        {
                            if(myActiveUser.getActiveZone() == null)
                            {
                                JOptionPane.showMessageDialog(this, ERROR_ZONE_NOT_EXISTS);
                                showConfig();
                                return;
                            }
                            if(myActiveUser.getActiveSport() == null)
                            {
                                JOptionPane.showMessageDialog(this, ERROR_SPORT_NOT_EXISTS);
                                showConfig();
                                return;
                            }

                            ErgoDatastore.exportPPP(myActiveUser, note);
                            addAdditionalInfo(MESSAGE_CONVERTED);

                            refresh(false, false);
                        }
                    }
                }


                return;
            }
            if (evt.getSource().equals(myFileMenu_Convert_GPX))
			{
				double lat = 0;
				double lon = 0;
				String note = null;
				ErgoDatastore ErgoDatastore = null;
				ErgoFileFilter fileOFilter = new ErgoFileFilter();
				ErgoFileFilter fileSFilter = new ErgoFileFilter();
				JFileChooser fileSave = new JFileChooser();
				JFileChooser fileOpen= new JFileChooser();

				fileSave.setDialogTitle(TITLE_EXPORT);
				fileSFilter.addExtension(DEFAULT_GPX_FILENAME_EXTENSION);
				fileSFilter.setDescription("*" + DEFAULT_GPX_FILENAME_EXTENSION);
				fileSave.setFileFilter(fileSFilter);

				fileOpen.setDialogTitle(TITLE_CONVERT);
				fileOFilter.addExtension(DEFAULT_SAVE_FILENAME_EXTENSION);
				fileOFilter.setDescription("*" + DEFAULT_SAVE_FILENAME_EXTENSION);
				fileOpen.setFileFilter(fileOFilter);

				fileOpen.setMultiSelectionEnabled(true);
				if(myActiveUser != null && myActiveUser.getSaveDir() != null)
					fileOpen.setSelectedFile(new File(myActiveUser.getSaveDir() + "\\*.*"));
				else
					fileOpen.setSelectedFile(new File(ErgoTools.DEFAULT_NAME + DEFAULT_SAVE_FILENAME_EXTENSION));

				if(fileOpen.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				{
					File[] files = fileOpen.getSelectedFiles();
					for(int i=0; i < files.length; i++)
					{
						String fileName = files[i].getName();
						String date = null;
						try
						{
							int pos = fileName.indexOf(" --- ");
							date = fileName.substring(0,pos);
							note = fileName.substring(pos+5,fileName.lastIndexOf('.'));
						}
						catch(Exception e)
						{
							note = fileName;
						}
						if(myActiveUser != null)
						{
							myActiveUser.setSaveDir(fileOpen.getSelectedFile().getPath().substring(0, fileOpen.getSelectedFile().getPath().lastIndexOf('\\')));
							myActiveUser.save(myProperties);
						}

						ErgoDatastore = new ErgoDatastore(note, myErgoControlConfig.getSampleRate());
						if(!ErgoDatastore.loadFromFile(this, fileOpen.getSelectedFile().getAbsolutePath(), myActiveBike, true))
						{
							JOptionPane.showMessageDialog(null, ERROR_ON_LOAD);
							ErgoDatastore = null;
						}
						else
						{
							try
							{
								ErgoDatastore.setStartTime((new SimpleDateFormat("dd.MM.yyyy HH-mm")).parse(date).getTime());
							}
							catch(Exception e)
							{
								date = null;
							}
							if(date == null)
							{
								ErgoDatastore = null;
							}
						}

						if(ErgoDatastore == null)
							JOptionPane.showMessageDialog(this, ERROR_CONVERSION_FAILED);
						else
						{
							if(myErgoDatastoreProgram != null && myErgoDatastoreProgram.size() > 0 && myErgoDatastoreProgram.getHasPositionData())
							{
								lat = myErgoDatastoreProgram.get(0).getPosition().getLat();
								lon = myErgoDatastoreProgram.get(0).getPosition().getLon();
							}

							if(myActiveUser != null && myActiveUser.getGPXDir() != null)
								fileSave.setSelectedFile(new File(myActiveUser.getGPXDir() + "\\" + (new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(ErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_GPX_FILENAME_EXTENSION));
							else
								fileSave.setSelectedFile(new File((new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(ErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_GPX_FILENAME_EXTENSION));

							if(myActiveUser == null || myActiveUser.getGPXDir() == null)
							{
								if(fileSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
								{
									if(myActiveUser != null)
									{
										myActiveUser.setSaveDir(fileSave.getSelectedFile().getPath().substring(0, fileSave.getSelectedFile().getPath().lastIndexOf('\\')));
										myActiveUser.save(myProperties);
									}

									ErgoDatastore.exportGPX(fileSave.getSelectedFile().getAbsolutePath(), note, lat, lon);
									addAdditionalInfo(MESSAGE_CONVERTED);
								}
							}
							if(myActiveUser != null && myActiveUser.getSaveDir() != null)
							{
								ErgoDatastore.exportGPX(fileSave.getSelectedFile().getAbsolutePath(), note, lat, lon);
								addAdditionalInfo(MESSAGE_CONVERTED);
                    		}

							refresh(false, false);
						}
					}
				}


				return;
            }
            if (evt.getSource().equals(myFileMenu_Config))
            {
                showConfig();
                return;
            }
            if (evt.getSource().equals(myFileMenu_Exit))
                closeApp();

            if (evt.getSource().equals(myControlMenu_Start))
            {
                start();
                return;
            }
            if (evt.getSource().equals(myControlMenu_Stop))
            {
                stop();
                return;
            }
            if (evt.getSource().equals(myDisplayMenu_Program))
            {
                showProgram(true, myDisplayMenu_Program.isEnabled());
                return;
            }
            if (evt.getSource().equals(myDisplayMenu_Bars) || evt.getSource().equals(myDisplayMenu_Dials) ||
            		evt.getSource().equals(myDisplayMenu_Map) || evt.getSource().equals(myDisplayMenu_None))
            {
                refreshGraph();

                return;
            }
            if (evt.getSource().equals(myDisplayMenu_Recording))
            {
                showProgram(false, myDisplayMenu_Recording.isEnabled());
                return;
            }
            if (evt.getSource().equals(myUserMenu_Create))
            {
                ErgoControlUser newUser = new ErgoControlUser(this, myProperties);
                newUser.setVisible(true);
                myActiveUser = newUser.getData();
                saveProperties();
                refresh(false, true);
                return;
            }
            if (evt.getSource().equals(myUserMenu_Remove))
            {
                if(myActiveUser != null)
                {
                    myActiveUser.remove(myProperties);
                    saveProperties();
                    myActiveUser=null;
                    myErgoControlConfig = new ErgoControlConfig(this, myProperties, myActiveUser);
                    refresh(false, true);
                    return;
                }
            }
            if (evt.getSource().equals(myBikeMenu_Create))
            {
                ErgoControlBikeset newBike = new ErgoControlBikeset(this, myActiveUser, myProperties);
                newBike.showDialog();
                myActiveBike = newBike.getData();
                saveProperties();
                refresh(false, true);
                return;
            }
            if (evt.getSource().equals(myBikeMenu_Edit))
            {
                ErgoControlBikeset newBike = new ErgoControlBikeset(this, myActiveBike, myActiveUser, myProperties);
                newBike.showDialog();
                myActiveBike = newBike.getData();
                saveProperties();
                refresh(false, true);
                return;
            }
            if (evt.getSource().equals(myBikeMenu_Remove))
            {
                if(myActiveBike != null)
                {
                	myActiveBike.remove(myActiveUser, myProperties);
                    saveProperties();
                    myActiveBike=null;
                    refresh(false, true);
                    return;
                }
            }
            if (evt.getSource().equals(myUserMenu_Logout))
            {
                if(myActiveUser != null)
                {
                    refresh(true, true);
                    return;
                }
            }
            if (evt.getSource().equals(myHelpMenu_About))
            {
                ErgoControlAbout newAbout = new ErgoControlAbout(this);
                newAbout.setVisible(true);
                return;
            }

            Object obj = myErgoUserAdministration.get(evt.getActionCommand());
            if(obj != null)
            	myActiveUser = (ErgoUserData)obj;

            obj = myErgoBikeAdministration.get(evt.getActionCommand());
            if(obj != null)
            	myActiveBike = (ErgoBikeDefinition)obj;

            refresh(false, true);
        }
    }

    public void windowClosing(WindowEvent evt)
    {
        closeApp();
    }

    public void windowOpened(WindowEvent evt){}
    public void windowIconified(WindowEvent evt){}
    public void windowDeiconified(WindowEvent evt){}
    public void windowClosed(WindowEvent evt){}
    public void windowActivated(WindowEvent evt){}
    public void windowDeactivated(WindowEvent evt){}

    public void dataAvailable(ErgoControlEvent evt, ErgoData data, ErgoBikeDefinition bike, Hashtable<String,ErgoData> dataArray)
    {
        refreshData(data, bike, dataArray);
    }

    public void dataAvailable(ErgoLineGraphEvent evt, ErgoData data)
    {
        refreshData(data, null, null);
    }

    public void finished(ErgoControlEvent evt, boolean bFailure)
    {
        if(bFailure)
            JOptionPane.showMessageDialog(this, ERROR_UNDEFINED);

        myErgoControl.removeErgoControlListener(this);
        myErgoDatastore = myErgoControl.getDatastore();
        myErgoControl = null;

        if(myActiveUser != null)
        {
            String note = myErgoDatastore.getName();
            boolean bRetry = false;

            if((myErgoControlConfig.getAutoExportPPP() || myErgoControlConfig.getAutoExportGPX() || myErgoControlConfig.getAutoSave()) && myErgoDatastore.size() > 1)
            {
            	//myErgoDatastore = myErgoDatastoreProgram;
                do
                {
                    bRetry = false;
                    note = (String)JOptionPane.showInputDialog(this, LABEL_NOTE, DIALOG_EXPORT_SAVE, JOptionPane.PLAIN_MESSAGE, null, null, note);
                    if(note != null)
                    {
	                    if(note.length() > 0)
	                    {
	                        if(!ErgoTools.checkCharacters(note, FORBIDDEN_CHARS))
	                        {
	                            JOptionPane.showMessageDialog(this, ERROR_FORBIDDEN_CHARS);
	                            bRetry = true;
	                        }
	                    }
	                    else
	                    	note = myErgoDatastore.getName();
                    }
                }
                while(bRetry);

                if(note != null)
                {
                    if(myErgoControlConfig.getAutoExportPPP())
                    {
                        myErgoDatastore.exportPPP(myActiveUser, note);
                        addAdditionalInfo(MESSAGE_EXPORTED);
                    }
                    if(myErgoControlConfig.getAutoExportGPX())
                    {
                    	String fileName = myActiveUser.getGPXDir() + "\\" + (new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(myErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_GPX_FILENAME_EXTENSION;
                    	double lat = 0;
                    	double lon = 0;

                    	if(myErgoDatastoreProgram != null && myErgoDatastoreProgram.size() > 0 && myErgoDatastoreProgram.getHasPositionData())
                        {
                        	lat = myErgoDatastoreProgram.get(0).getPosition().getLat();
                        	lon = myErgoDatastoreProgram.get(0).getPosition().getLon();
                        }

                    	myErgoDatastore.exportGPX(fileName, note, lat, lon);
                        addAdditionalInfo(MESSAGE_EXPORTED);
                    }
                    if(myErgoControlConfig.getAutoSave())
                    {
                        String fileName = myActiveUser.getSaveDir() + "\\" + (new SimpleDateFormat("dd.MM.yyyy HH-mm")).format((new Date(myErgoDatastore.getStartTime()))) + " --- " + note + DEFAULT_SAVE_FILENAME_EXTENSION;

                        myErgoDatastore.saveToFile(fileName, myErgoControlConfig.getSampleRate());
                        addAdditionalInfo(MESSAGE_SAVED);
                    }
                }
            }
        }

        myLastData = null;
        myLastDataArray = null;
        myLastBike = null;
        refreshData(null, null, null);
        refresh(false, false);
    }

    public void refreshData(ErgoData data, ErgoBikeDefinition bike, Hashtable<String,ErgoData> dataArray)
    {
    	if(data != null)
    		myLastData = data;
    	if(bike != null)
    		myLastBike = bike;
    	if(dataArray != null)
    		myLastDataArray = dataArray;

        if(myDisplayMenu_Recording.isSelected())
        {
            ErgoDatastore datastore = null;
            if(myErgoControl != null)
                datastore = myErgoControl.getDatastore();

            myProgramWindow.setDatastore(datastore, false, false);
            myProgramWindowScroll.getHorizontalScrollBar().setValue(myProgramWindowScroll.getHorizontalScrollBar().getMaximum());
        }
        else
        {
            ErgoDatastore datastore = null;
            if(myErgoControl != null)
            {
            	datastore = myErgoControl.getDatastore();

                int val = 0;
                int val2 = datastore.size() - 1;

                if(val2 < 0)
                	val2 = 0;
                if(val2 >= myErgoDatastoreProgram.size())
                	val2 = myErgoDatastoreProgram.size() - 1;

                if(!myErgoDatastoreProgram.getUseDistance())
                	val = datastore.size();
                else
                {
                	if(datastore.size() > 0)
                		val = (int)Math.floor((datastore.get(datastore.size()-1).getDistance()*100)+0.5);
                }

                //if(val < myProgramWindowScroll.getHorizontalScrollBar().getMaximum())
                {
                	int width = myProgramWindowScroll.getWidth()/2;
                	if(val > width)
                	{
	                	myProgramWindowScroll.getHorizontalScrollBar().setValueIsAdjusting(false);
	                    myProgramWindowScroll.getHorizontalScrollBar().setValue(val - width);
                	}
                    /*int val2 = myErgoDatastoreProgram.size();
                    int val3 = (int)((myProgramWindowScroll.getHorizontalScrollBar().getMaximum()-(myProgramWindowScroll.getWidth()+5))/(double)val2*10);
                    //if(val > myProgramWindowScroll.getWidth()/2)
                    {
                        if(val%10 == 0)
                            myProgramWindowScroll.getHorizontalScrollBar().setValue(myProgramWindowScroll.getHorizontalScrollBar().getValue()+val3);
                    } */
                    //myProgramWindow.setMarker(val);
                	myProgramWindow.setMarker(val, true);
                	myProgramWindow.setMarker(val2, false);

                	myProgramWindow.setMarker(myLastDataArray);
                }
            }
        }

        if(myLastData != null)
        {
        	DecimalFormat decFormat = new DecimalFormat("0.00");
        	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        	String togo = "";
            String grade = "";

            if(myErgoDatastoreProgram != null)
            {
	            if(myErgoDatastoreProgram.getUseDistance())
	            {
	            	togo = decFormat.format(myErgoDatastoreProgram.get(myErgoDatastoreProgram.size()-1).getDistance() - myLastData.getDistance()) + " " + ErgoTools.LABEL_DISTANCE_UNIT;
	            	grade = decFormat.format(Math.tan(Math.toRadians(myLastData.getGrade())) * 100) + " " + ErgoTools.LABEL_GRADE_UNIT;
	            }
	            else if(myErgoControl != null && myErgoControl.getDatastore() != null)
	            	togo = timeFormat.format(new Date((myErgoDatastoreProgram.size() - myErgoControl.getDatastore().size()) * 1000 -  3599000));
            }

        	myPuls.setText(Integer.toString(myLastData.getPuls()) + " " + ErgoTools.LABEL_PULS_UNIT);
            myRPM.setText(Integer.toString(myLastData.getRPM()) + " " + ErgoTools.LABEL_RPM_UNIT);
            mySpeed.setText(decFormat.format(myLastData.getSpeed()) + " " + ErgoTools.LABEL_SPEED_UNIT);
            myDistance.setText(decFormat.format(myLastData.getDistance()) + " " + ErgoTools.LABEL_DISTANCE_UNIT);
            myToGo.setText(togo);

            myPower.setText(Integer.toString(myLastData.getPower()) + " " + ErgoTools.LABEL_POWER_UNIT);
            //myKilojoule.setText(Integer.toString(myLastData.getKilojoule()) + " " + ErgoTools.LABEL_KILOJOULE_UNIT);
            myTime.setText(myLastData.getTime());
            if(myLastData.isRecovery())
                myTime.setVisible(!myTime.isVisible());
            else
                myTime.setVisible(true);

            myGrade.setText(grade);

            if(myLastBike != null && myErgoDatastoreProgram != null && myErgoDatastoreProgram.getUseDistance())
            {
            	myGear.setText(Integer.toString(myLastBike.getGear()));
//            	myRing.setText(Integer.toString(myLastBike.getRing()));
            }
        }
        else
        {
            myPuls.setText("");
            myRPM.setText("");
            mySpeed.setText("");
            myDistance.setText("");
            myToGo.setText("");
            myPower.setText("");
            //myKilojoule.setText("");
            myTime.setText("");
            myGrade.setText("");
            myGear.setText("");
//            myRing.setText("");
        }
        myRealTime.setText((new SimpleDateFormat("HH:mm:ss")).format(new Date()));
        if(myErgoDatastoreProgram != null && myLastData != null && myDataGraphWindow.getMode() == ErgoGraph.MODE_MAP)
        {
        	Enumeration enumErgoDatastoreProgram =  myErgoDatastoreProgram.elements();
        	ErgoData displayData = null;

        	while(enumErgoDatastoreProgram.hasMoreElements())
        	{
        		displayData = (ErgoData)enumErgoDatastoreProgram.nextElement();
        		if(displayData.getDistance() >= myLastData.getDistance())
        			break;
        		else
        			displayData = null;
        	}

        	myDataGraphWindow.setData(displayData);
        }
        else
        	myDataGraphWindow.setData(myLastData);

        myDataGraphWindow.setMarker(myLastDataArray);

        myDataGraphWindow.setDatastore(myErgoDatastoreProgram);
        repaint();
    }

    private void start()
    {
        try
        {
            myErgoDatastore = null;

            if(myActiveUser != null)
            {
                if(myErgoControlConfig.getAutoExportPPP())
                {
                    if(!myActiveUser.isInitialized())
                    {
                        JOptionPane.showMessageDialog(this, ERROR_POLAR_NOT_INITIALIZED);
                        myActiveUser.setAutoExportPPP(false);
                        showConfig();
                        return;
                    }
                    if(myActiveUser.getActiveZone() == null)
                    {
                        JOptionPane.showMessageDialog(this, ERROR_ZONE_NOT_EXISTS);
                        myActiveUser.setAutoExportPPP(false);
                        showConfig();
                        return;
                    }
                    if(myActiveUser.getActiveSport() == null)
                    {
                        JOptionPane.showMessageDialog(this, ERROR_SPORT_NOT_EXISTS);
                        myActiveUser.setAutoExportPPP(false);
                        showConfig();
                        return;
                    }
                }

                if(myErgoControlConfig.getAutoSave())
                {
                    boolean saveDirError = false;

                    if(myActiveUser.getSaveDir() == null)
                        saveDirError = true;
                    else
                    {
                        File f = new File(myActiveUser.getSaveDir());

                        if(myActiveUser.getSaveDir().length() == 0 || !f.exists())
                            saveDirError = true;
                    }

                    if(saveDirError)
                    {
                        JOptionPane.showMessageDialog(this, ERROR_SAVEDIR_NOT_EXISTS);
                        showConfig();
                        return;
                    }
                }
            }

            log.debug("create new thread");
            if(myErgoControlConfig.getIsKettler())
                myErgoControl = (ErgoControlInterface)new ErgoControlKettler(myErgoControlConfig.getPort());
            /*else if(myErgoControlConfig.getIsDaum())
                myErgoControl = (ErgoControlInterface)new ErgoControlDaum(myErgoControlConfig.getPort());*/
            else
            {
                JOptionPane.showMessageDialog(this, ERROR_NO_DEVICE);
                showConfig();
                return;
            }

            myControlMenu_Start.setEnabled(false);
            myControlMenu_Stop.setEnabled(true);

            log.debug("start thread");
            myErgoControl.init(myErgoDatastoreProgram, myActiveBike, myErgoControlConfig.getRefreshRate(), DEFAULT_SAMPLERATE, myClient);
            if(!myErgoControl.isRunning())
                stop();
            while(!myErgoControl.hasInitalized() && myErgoControl.isRunning());
            if(myErgoControl.hasInitalized())
            {
                myFileMenu_Save.setEnabled(false);
                myFileMenu_Export_PPP.setEnabled(false);
                myFileMenu_Export_GPX.setEnabled(false);

                myErgoControl.addErgoControlListener(this);

                myErgoControl.start();
            }
            else
                stop();
        } catch(Exception e)
        {
            stop();

            JOptionPane.showMessageDialog(this, ERROR_UNDEFINED);
            log.error(e);
        }
    }

    public void stop()
    {
        if(myErgoControl != null)
        {
            myErgoDatastore = myErgoControl.getDatastore();

            log.debug("stop thread");
            myErgoControl.stop(false);
            myErgoControl = null;
        }

        myLastData = null;
        myLastDataArray = null;
        refreshData(null, null, null);
        refresh(false, false);
    }

    public void showConfig()
    {
        myErgoControlConfig.setVisible(true);
        saveProperties();


        resize(myErgoControlConfig.getWindowSize(), true);

        if(myErgoDatastoreProgram != null)
        {
        	myErgoDatastoreProgram.calculateGradiation(myErgoControlConfig.getDampingRate());
	        setProgram(myErgoDatastoreProgram, false);
	        refreshData(myErgoDatastoreProgram.get(0), null, null);//repaint();
        }
    }

    private void closeApp()
    {
        stop();

        if(myDisplayMenu_Bars.isSelected())
            myProperties.put(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH, Integer.toString(ErgoGraph.MODE_BARS));
        else if(myDisplayMenu_Dials.isSelected())
            myProperties.put(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH, Integer.toString(ErgoGraph.MODE_DIALS));
        else if(myDisplayMenu_Map.isSelected())
            myProperties.put(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH, Integer.toString(ErgoGraph.MODE_MAP));
        else if(myDisplayMenu_None.isSelected())
            myProperties.put(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH, Integer.toString(ErgoGraph.MODE_NONE));
        else
            myProperties.remove(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH);

        saveProperties();

        setProgram(null, true);

        System.exit(0);
    }

    private void saveProperties()
    {
        try
        {
            File confFile = new File(PROPERTY_FILE);
            FileOutputStream fileOutput = new FileOutputStream(confFile);

            //wir speichern in die angegebene Datei die Properties.
            myProperties.store(fileOutput, "");

            fileOutput.close();
        } catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, ERROR_WRITE_CONFIG);
            log.error(e);
        }
    }

    private void setProgram(ErgoDatastore newErgoDatastore, boolean removeServer)
    {
    	myLastData = null;
    	myLastDataArray = null;
        myErgoDatastoreProgram = newErgoDatastore;

        if(myServer == null && myErgoDatastoreProgram != null && myErgoDatastoreProgram.getHasPositionData() && myErgoDatastoreProgram.getUseDistance())
        	myFileMenu_Server_Start.setEnabled(true);
        else
        	myFileMenu_Server_Start.setEnabled(false);

        if(removeServer == true)
        	serve(false);

        myDataGraphWindow.resetAll();
        showProgram(newErgoDatastore!=null, newErgoDatastore!=null);
    }

    private void showProgram(boolean visible, boolean enabled)
    {
    	if(!visible)
        {
            ErgoDatastore datastore = null;
            if(myErgoControl != null)
                datastore = myErgoControl.getDatastore();

            myProgramWindow.setDatastore(datastore, false, false);

            myFileMenu_Close.setEnabled(enabled);
            myDisplayMenu_Program.setEnabled(enabled);

            myDisplayMenu_Recording.setSelected(true);

            myProgramWindow.setAttribute(ErgoLineGraph.POWER);
            myProgramWindow.setAttribute(ErgoLineGraph.RPM);
            myProgramWindow.setAttribute(ErgoLineGraph.PULS);
            myProgramWindow.setAttribute(ErgoLineGraph.SPEED);

            myProgramWindow.setPreferredSize(new Dimension(0, myProgramWindow.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
            myProgramWindow.setSize(new Dimension(0, myProgramWindow.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
        }
        else
        {
            myProgramWindow.setDatastore(myErgoDatastoreProgram, true, false);

            myFileMenu_Close.setEnabled(true);
            myDisplayMenu_Program.setEnabled(true);

            myDisplayMenu_Program.setSelected(true);

            if(myErgoDatastoreProgram.getRefDataIndex() == ErgoDatastore.POWER)
            	myProgramWindow.setAttribute(ErgoLineGraph.POWER);
            else
            	myProgramWindow.unsetAttribute(ErgoLineGraph.POWER);
                        if(myErgoDatastoreProgram.getRefDataIndex() == ErgoDatastore.RPM)
            	myProgramWindow.setAttribute(ErgoLineGraph.RPM);
            else
            	myProgramWindow.unsetAttribute(ErgoLineGraph.RPM);

            if(myErgoDatastoreProgram.getRefDataIndex() == ErgoDatastore.PULS)
            	myProgramWindow.setAttribute(ErgoLineGraph.PULS);
            else
            	myProgramWindow.unsetAttribute(ErgoLineGraph.PULS);

            if(myErgoDatastoreProgram.getRefDataIndex() == ErgoDatastore.SPEED)
            	myProgramWindow.setAttribute(ErgoLineGraph.SPEED);
            else
            	myProgramWindow.unsetAttribute(ErgoLineGraph.SPEED);

            if(myErgoDatastoreProgram.getRefDataIndex() == ErgoDatastore.GRADE)
            	myProgramWindow.setAttribute(ErgoLineGraph.GRADE);
            else
            	myProgramWindow.unsetAttribute(ErgoLineGraph.GRADE);

            /*myProgramWindow.unsetAttribute(ErgoLineGraph.RPM);
            myProgramWindow.unsetAttribute(ErgoLineGraph.PULS);
            myProgramWindow.unsetAttribute(ErgoLineGraph.SPEED);*/

            if(!myErgoDatastoreProgram.getUseDistance())
            {
	            myProgramWindow.setPreferredSize(new Dimension(myErgoDatastoreProgram.size(), myProgramWindow.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
	            myProgramWindow.setSize(new Dimension(myErgoDatastoreProgram.size(), myProgramWindow.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
            }
            else
            {
            	int distance = (int)Math.floor(((ErgoData)myErgoDatastoreProgram.get(myErgoDatastoreProgram.size()-1)).getDistance()*100+0.5);
	            myProgramWindow.setPreferredSize(new Dimension(distance, myProgramWindow.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
	            myProgramWindow.setSize(new Dimension(distance, myProgramWindow.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
            }
            //myDisplayMenu_Map.setEnabled(true);
        }

    	/*TODO check if map should be disabled
    	 if(myErgoDatastoreProgram == null)
    		bDisableMap = true;

    	if(bDisableMap)
    	{
	    	myDisplayMenu_Map.setEnabled(false);
	    	if(myDisplayMenu_Map.isSelected())
	    		myDisplayMenu_None.setSelected(true);
    	}*/

        refreshData(null, null, null);//repaint();
    }

    private void initUserMenu(boolean doLogout, boolean reload)
    {
        String lastActiveUser = myProperties.getProperty(PROPERTY_ACTIVEUSER);
        Enumeration enumUsers = myErgoUserAdministration.elements();

        if(doLogout)
        {
            myActiveUser = null;
            lastActiveUser = null;
            myActiveBike = null;
        }

        myUserMenu_Remove.setEnabled(false);
        myUserMenu_Logout.setEnabled(false);

        myUserMenu.removeAll();
        myUserMenu.add(myUserMenu_Create);
        myUserMenu.add(myUserMenu_Remove);

        myUserMenu.add(myUserMenu_Logout);

        if(myErgoUserAdministration.size() > 0)
        {
            ButtonGroup bg = new ButtonGroup();
            myUserMenu.add(new JSeparator());
            while(enumUsers.hasMoreElements())
            {
                ErgoUserData data = (ErgoUserData)enumUsers.nextElement();

                JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(data.getUserName());
                menuItem.removeActionListener(this);
                menuItem.addActionListener(this);
                if(myActiveUser != null)
                {
                    if(myActiveUser.getUserName().equals(data.getUserName()) )
                    {
                        myUserMenu_Remove.setEnabled(true);
                        myUserMenu_Logout.setEnabled(true);
                        menuItem.setSelected(true);
                    }
                }
                else if(lastActiveUser != null)
                {
                    if(lastActiveUser.equals(data.getUserName()) )
                    {
                        myUserMenu_Remove.setEnabled(true);
                        myUserMenu_Logout.setEnabled(true);
                        menuItem.setSelected(true);
                        myActiveUser = data;
                    }
                }
                myUserMenu.add(menuItem);
                bg.add(menuItem);
            }
        }

        myErgoControlConfig = new ErgoControlConfig(this, myProperties, myActiveUser);
        if(myActiveUser != null)
            myProperties.setProperty(PROPERTY_ACTIVEUSER, myActiveUser.getUserName());
        else
            myProperties.remove(PROPERTY_ACTIVEUSER);
        saveProperties();

        if(reload)
        {
            if(myProperties.get(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH) != null)
            {
                try
                {
                    if(myProperties.get(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH).equals(Integer.toString(ErgoGraph.MODE_BARS)))
                        myDisplayMenu_Bars.setSelected(true);
                    else if(myProperties.get(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH).equals(Integer.toString(ErgoGraph.MODE_DIALS)))
                        myDisplayMenu_Dials.setSelected(true);
                    else if(myProperties.get(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH).equals(Integer.toString(ErgoGraph.MODE_MAP)))
                        myDisplayMenu_Map.setSelected(true);
                    else if(myProperties.get(myErgoControlConfig.getConfigurationPrefix() + PROPERTY_GRAPH).equals(Integer.toString(ErgoGraph.MODE_NONE)))
                        myDisplayMenu_None.setSelected(true);
                } catch(Exception e)
                {
                    log.error(e);
                }
            }
        }

        resize(myErgoControlConfig.getWindowSize(), true);
    }

    private void initBikeMenu()
    {
        String lastActiveBike = myProperties.getProperty(PROPERTY_ACTIVEBIKE);
        Enumeration enumBikes = myErgoBikeAdministration.elements();

        myBikeMenu_Remove.setEnabled(false);
        myBikeMenu_Edit.setEnabled(false);

        myBikeMenu.removeAll();
        myBikeMenu.add(myBikeMenu_Create);
        myBikeMenu.add(myBikeMenu_Edit);
        myBikeMenu.add(myBikeMenu_Remove);

        if(myErgoBikeAdministration.size() > 0)
        {
            ButtonGroup bg = new ButtonGroup();
            myBikeMenu.add(new JSeparator());
            while(enumBikes.hasMoreElements())
            {
                ErgoBikeDefinition data = (ErgoBikeDefinition)enumBikes.nextElement();

                JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(data.getName());
                menuItem.removeActionListener(this);
                menuItem.addActionListener(this);
                if(myActiveBike != null)
                {
                    if(myActiveBike.getName().equals(data.getName()) )
                    {
                        myBikeMenu_Remove.setEnabled(true);
                        myBikeMenu_Edit.setEnabled(true);
                        menuItem.setSelected(true);
                    }
                }
                else if(lastActiveBike != null)
                {
                    if(lastActiveBike.equals(data.getName()) )
                    {
                        myBikeMenu_Remove.setEnabled(true);
                        myBikeMenu_Edit.setEnabled(true);
                        menuItem.setSelected(true);
                        myActiveBike = data;
                    }
                }
                myBikeMenu.add(menuItem);
                bg.add(menuItem);
            }
        }

        if(myActiveBike != null)
            myProperties.setProperty(PROPERTY_ACTIVEBIKE, myActiveBike.getName());
        else
            myProperties.remove(PROPERTY_ACTIVEBIKE);
        saveProperties();
    }

    private void refresh(boolean doLogout, boolean reload)
    {
    	myErgoUserAdministration = new ErgoUserAdministration(myProperties);

        if(myErgoControl == null)
        {
            myControlMenu_Start.setEnabled(true);
            myControlMenu_Stop.setEnabled(false);
        }
        else
        {
            myControlMenu_Start.setEnabled(false);
            myControlMenu_Stop.setEnabled(true);
        }

        if(myErgoDatastore != null && myErgoDatastore.size() > 0)
        {
            myFileMenu_Save.setEnabled(true);
            if(myActiveUser == null)
                myFileMenu_Export_PPP.setEnabled(false);
            else
            {
                if(!myErgoDatastore.getExportedPPP())
                    myFileMenu_Export_PPP.setEnabled(true);
                else
                    myFileMenu_Export_PPP.setEnabled(false);
            }
            myFileMenu_Export_GPX.setEnabled(true);
        }
        else
        {
            myFileMenu_Save.setEnabled(false);
            myFileMenu_Export_PPP.setEnabled(false);
            myFileMenu_Export_GPX.setEnabled(false);
        }

        setProgram(myErgoDatastoreProgram, false);
        initUserMenu(doLogout, reload);

        myErgoBikeAdministration = new ErgoBikeAdministration(myActiveUser, myProperties);
        initBikeMenu();

        if(myActiveUser != null && myActiveUser.getPolarDir() != null)
        	myFileMenu_Convert_PPP.setEnabled(true);
        else
        	myFileMenu_Convert_PPP.setEnabled(false);

        myFileMenu_Convert_GPX.setEnabled(true);

        refreshGraph();

        if(myServer != null || myClient != null)
        	myFileMenu_Server_Start.setEnabled(false);

        if(myClient != null)
        	myFileMenu_Connect.setEnabled(false);
        else
        	myFileMenu_Connect.setEnabled(true);

        String title = TITLE_APP;
        if(myActiveUser != null)
        	title += " - "  + myActiveUser.getUserName();
        if(myActiveBike != null)
        	title += " (" + myActiveBike.getName() + ")";
        if(myServer != null)
        	title += " - Server is active (" + myServer.getHost() + ":" + myServer.getPort() + ")";

        setTitle(title);
    }

    private void refreshGraph()
    {
    	if(myDisplayMenu_Bars.isSelected())
        {
            myDataWindow.remove(myDataGraphWindow);
            myDataWindow.add(myDataGraphWindow, BorderLayout.EAST);
            myDataGraphWindow.setMode(ErgoGraph.MODE_BARS);
        }
        else if(myDisplayMenu_Dials.isSelected())
        {
            myDataWindow.remove(myDataGraphWindow);
            myDataWindow.add(myDataGraphWindow, BorderLayout.EAST);
            myDataGraphWindow.setMode(ErgoGraph.MODE_DIALS);
        }
        else if(myDisplayMenu_Map.isSelected())
        {
            myDataWindow.remove(myDataGraphWindow);
            myDataWindow.add(myDataGraphWindow, BorderLayout.EAST);
            myDataGraphWindow.setMode(ErgoGraph.MODE_MAP);
        }
        else if(myDisplayMenu_None.isSelected())
        {
            myDataWindow.remove(myDataGraphWindow);
            myDataGraphWindow.setMode(ErgoGraph.MODE_NONE);
        }
        else
        {
            myDisplayMenu_None.setSelected(true);
            myDataGraphWindow.setMode(ErgoGraph.MODE_NONE);
        }
        repaint();
    }

    public void paint(Graphics g)
    {
    	//long start = System.currentTimeMillis();
        String info = " ";
        super.paint(g);

        if(myActiveUser != null)
            info += "User: " + myActiveUser.getUserName();

        if(myErgoDatastoreProgram != null)
        {
            if(info.length() > 0)
                info += " | ";
            info += "Program: " + myErgoDatastoreProgram.getName();
        }

        Enumeration enumInfo = myAdditionalInfo.elements();
        while(enumInfo.hasMoreElements())
        {
            String text = (String)enumInfo.nextElement();

            if(info.length() > 0)
                info += " | ";
            info += text;
        }

        myInfo.setText(info);
        myInfoTime.setText((new SimpleDateFormat(" HH:mm:ss dd.MM.yyyy ")).format(new Date()));
        //long stop = System.currentTimeMillis();
        //log.debug("paint dur = " + (stop - start));
    }

    private void addAdditionalInfo(String text)
    {
        if(myAdditionalInfoTimeout.get(text) == null)
        {
            myAdditionalInfo.add(text);
            myAdditionalInfoTimeout.put(text, new Integer(MESSAGE_TIMEOUT));
        }
    }

    private void serve(boolean startServer)
    {
    	if(myServer != null)
        {
	       	try
			{
	       		if(myClient != null)
	       		{
	       			myClient.disconnect();
	       			myClient = null;
	       		}
	       		if(!myServer.getIsLocal())
	       		{
		       		try
		       		{
			       		URL ecURL = new URL(ErgoTools.getHTTPUrl(myProperties) + "removeserver.php?ip=" + myServer.getHost());
			    		URLConnection ecURLCon = ecURL.openConnection();

			    		ecURLCon.setConnectTimeout(2000);
			    		ecURLCon.connect();
						InputStream is = ecURLCon.getInputStream();

						ByteArrayOutputStream os = new ByteArrayOutputStream();

						byte[] buffer = new byte[ 0xFFFF ];
					    for ( int len; (len = is.read(buffer)) != -1; )
					    	os.write( buffer, 0, len );

					    String answer = os.toString();
					    if(answer.charAt(0) == '0')
					    {
					    	log.debug("Server removed from web list");
					    }
		       		}
		       		catch(Exception ex)
		       		{
		       			log.error(ex);
		       		}
	       		}

	       		myServer.cancel();
	       		myServer = null;
	       		myFileMenu_Connect.setEnabled(true);
	       		if(myErgoDatastoreProgram != null && myErgoDatastoreProgram.getHasPositionData() && myErgoDatastoreProgram.getUseDistance())
	            	myFileMenu_Server_Start.setEnabled(true);
	       		myFileMenu_Server_Stop.setEnabled(false);
			}
			catch(Exception ex)
			{
				log.error(ex);
			}
        }

    	if(!startServer)
    		return;

    	if(myErgoDatastoreProgram != null && myErgoDatastoreProgram.getHasPositionData() && myErgoDatastoreProgram.getUseDistance())
    	{
    		try
    		{
    			String ip = InetAddress.getLocalHost().getHostAddress();
    			String portString = Integer.toString(ErgoTools.PORT);
    			int port = 0;
    			String host = "default";

    			/*ip = JOptionPane.showInputDialog("Enter IP Address of Server:", ip);
    			if(ip != null)
    				portString = JOptionPane.showInputDialog("Enter Port of Server:", portString);*/
    			if(portString != null)
    				port = Integer.parseInt(portString);
    			/*if(ip != null && portString != null)
    				host = JOptionPane.showInputDialog("Enter Name of Server:", host);*/

    			if(ip != null && portString != null && host != null)
    			{
    				boolean bOk = true;
    				boolean bLocal = false;

    				if(JOptionPane.showConfirmDialog(null, "Use Local Network?", "Choose Network Type", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
    				{
    					URL ecURL = new URL(ErgoTools.getHTTPUrl(myProperties) + "getip.php");
			    		URLConnection ecURLCon = ecURL.openConnection();

			    		ecURLCon.setConnectTimeout(2000);
			    		ecURLCon.connect();
						InputStream is = ecURLCon.getInputStream();

						ByteArrayOutputStream os = new ByteArrayOutputStream();

						byte[] buffer = new byte[ 0xFFFF ];
					    for ( int len; (len = is.read(buffer)) != -1; )
					    	os.write( buffer, 0, len );

					    ip = os.toString();

					    host = JOptionPane.showInputDialog("Enter Name of Server:", host);

					    if(ip != null && host != null)
					    {
					    	ecURL = new URL(ErgoTools.getHTTPUrl(myProperties) + "addserver.php?ip=" + ip + "&port=" + port + "&host=" + host + "&version=" + ErgoTools.VERSION);
				    		ecURLCon = ecURL.openConnection();

				    		ecURLCon.setConnectTimeout(2000);
				    		ecURLCon.connect();
							is = ecURLCon.getInputStream();

							os = new ByteArrayOutputStream();

							buffer = new byte[ 0xFFFF ];
						    for ( int len; (len = is.read(buffer)) != -1; )
						    	os.write( buffer, 0, len );

						    String answer = os.toString();
						    if(answer.charAt(0) != '0')
						    	bOk = false;
					    }
					    else
					    	bOk = false;
    				}
    				else
    					bLocal = true;

    				if(bOk)
    				{
				    	myServer = new ErgoControlServer(ip, host, myErgoDatastoreProgram, port, bLocal);
				    	myServer.start();
				    	this.connect(null, port);
				    	myFileMenu_Connect.setEnabled(false);
				    	myFileMenu_Server_Start.setEnabled(false);
				    	myFileMenu_Server_Stop.setEnabled(true);
    				}
    			}
    		}
    		catch(Exception ex)
    		{
    			String msg = "";
    			if(ex.getMessage() != null)
    				msg = ex.getMessage();
    			JOptionPane.showConfirmDialog(null, "Server creation failed!\n" + msg, "Server", JOptionPane.DEFAULT_OPTION);
    			log.error(ex);
    		}
    	}
    	else
    		JOptionPane.showConfirmDialog(null, "Program has no server support!", "Server", JOptionPane.DEFAULT_OPTION);
    }

    private void connect(String host, int port)
    {
    	boolean bLocal = false;

    	if(host == null)
    	{
    		host = "localhost";
    		bLocal = true;
    	}

    	if(host != null)
        {
    		String username = "user";

            if(this.myActiveUser != null)
            	username = this.myActiveUser.getUserName();

            myClient = new ErgoControlClient();

            if(myClient.connect(host, port, username))
            {
	            if(!bLocal)
	            {
	            	ErgoDatastore program = myClient.getProgram();
	            	if(program != null)
	            	{
		            	this.setProgram(program, false);
		            	refresh(false, false);
		                refreshData(program.get(0), null, null);//repaint();
	            	}
	            	else
	            	{
	            		JOptionPane.showMessageDialog(null, ERROR_GETPROGRAM + "\n" + myClient.getErrorMessage());
	            		disconnect();
	            	}
	            }
            }
            else
            {
            	JOptionPane.showMessageDialog(null, ERROR_CONNECT + "\n" + myClient.getErrorMessage());
            	disconnect();
            }
        }
    }

    private void disconnect()
    {
    	if(myClient != null)
        {
	       	try
			{
	       		myClient.disconnect();
			}
			catch(Exception ex)
			{
				log.error(ex);
			}
			myClient = null;
        }
    }
}
