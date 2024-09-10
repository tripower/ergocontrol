package forms;

import javax.comm.CommPortIdentifier;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import data.ErgoUserData;
import data.PolarSport;
import data.PolarZones;

import tools.PolarUserFileFilter;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.io.File;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 17.02.2005
 * Time: 10:07:46
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlConfig extends JDialog implements ActionListener, WindowListener, ChangeListener  {
	static final long serialVersionUID = 0;

//    private static Logger log = Logger.getLogger(ErgoControlConfig.class);
    private static Properties myProperties = null;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final static String DIALOGSTRING = "ErgoControl Configuration";
    private final static String LABEL_SIZE = "Window Size";
    private final static String LABEL_PORT = "Select Port";
    private final static String LABEL_DEVICE = "Select Device";
//    private final static String LABEL_REFRESHRATE = "Refreshrate";
    private final static String LABEL_DAMPINGRATE = "Dampingrate";
//    private final static String LABEL_SAMPLERATE = "Samplerate";
    private final static String LABEL_ZONESET = "HR Zone Set";
    private final static String LABEL_SPORTSET = "Sport Set";
    private final static String FILEDIALOGSTRING_POLAR = "Select Polar User File";
    private final static String LABEL_POLARDIR = "Select Polar Directory >>>";
    private final static String CHECKBOX_EXPORT_PPP = "Automatic PPP Export";
    private final static String CHECKBOX_EXPORT_GPX = "Automatic GPX Export";
    private final static String FILEDIALOGSTRING_SAVE = "Select Save Directory";
    private final static String FILEDIALOGSTRING_GPX = "Select GPX Directory";
    private final static String LABEL_SAVEDIR = "Select Save Directory >>>";
    private final static String LABEL_GPXDIR = "Select GPX Directory >>>";
    private final static String CHECKBOX_SAVE = "Automatic Save";
    //private final static String BUTTON_SELECT = "...";
    private final static String BUTTON_GPX = "GPX...";
    private final static String BUTTON_POLAR = "Polar Home...";
    private final static String BUTTON_SAVE = "Save...";
    private final static String BUTTON_CONFIRM = "Ok";
    private final static String BUTTON_ABORT = "Cancel";
    private final static String LABEL_SIMULATION = "Simulation";    

    private final static String ERROR_POLAR_NOT_INITIALIZED = "Polar Userdata is not initialized!\nPlease select correct User.ppd file, automatic export disabled!";
    private final static String ERROR_SAVEDIR_NOT_EXISTS = "Save Directory is not set!\nPlease select Directory, automatic save disabled!";
    private final static String ERROR_GPXDIR_NOT_EXISTS = "GPX Directory is not set!\nPlease select Directory, automatic save disabled!";

    private final static int DEFAULT_SIZE = 100;
    private final static int MINIMUM_WINDOW_SIZE = 50;
    private final static int MAXIMUM_WINDOW_SIZE = 150;

    private final static String DEVICE_KETTLER = "Kettler";
    private final static String DEVICE_DAUM = "Daum";

    /*private final static String REFRESH_UNIT = "ms";
    private final static int REFRESH_MAX = 800;
    private final static int REFRESH_MIN = 200;*/

    private final static String DAMPING_UNIT = "%";
	    private final static int DAMPING_MAX = 100;
    private final static int DAMPING_MIN = 0;

    /*private final static String SAMPLE_UNIT = "sec";
    private final static int SAMPLE_MAX = 30;
    private final static int SAMPLE_MIN = 1;*/

    public final static String PROPERTY_WINDOW_SIZE = "WindowSize";
    private final static String PROPERTY_PORT = "ComPort";
    private final static String PROPERTY_DEVICE = "Device";
//    private final static String PROPERTY_REFRESHRATE = "RefreshRate";
    public final static String PROPERTY_DAMPINGRATE = "DampingRate";
//    private final static String PROPERTY_SAMPLERATE = "SampleRate";

    private final static int WINDOW_WIDTH = 350;
    private final static int WINDOW_HEIGHT = 125;
    private final static int WINDOW_HEIGHT_USER = 300;

    /*private JPanel myRefreshPanel = new JPanel();
    private JSlider myRefreshRate = new JSlider();
    private JLabel myRefreshValue = new JLabel();*/
    private JPanel myDampingPanel = new JPanel();
	private JSlider myDampingRate = new JSlider();
    private JLabel myDampingValue = new JLabel();
/*    private JPanel mySamplePanel = new JPanel();
    private JSlider mySampleRate = new JSlider();
    private JLabel mySampleValue = new JLabel();
*/

    private JSlider myWindowSize = new JSlider();
    private JComboBox myPortChoice = new JComboBox();
    private JComboBox myDeviceChoice = new JComboBox();

    private JLabel myPolarUserFile= new JLabel();
    private JButton myPolarUserFileSelect = new JButton(BUTTON_POLAR);
    private JCheckBox myExportPPP = new JCheckBox(CHECKBOX_EXPORT_PPP);
    private JLabel myGPXDir= new JLabel();
    private JButton myGPXDirSelect = new JButton(BUTTON_GPX);
    private JCheckBox myExportGPX = new JCheckBox(CHECKBOX_EXPORT_GPX);
    private JLabel mySaveDir = new JLabel();
    private JButton mySaveDirSelect = new JButton(BUTTON_SAVE);
    private JCheckBox mySave = new JCheckBox(CHECKBOX_SAVE);
    private JComboBox myZoneSetChoice = new JComboBox();
    private JComboBox mySportSetChoice = new JComboBox();

    private JButton myConfirm = new JButton(BUTTON_CONFIRM);
    private JButton myAbort = new JButton(BUTTON_ABORT);

    private Hashtable<String,CommPortIdentifier> myPorts = new Hashtable<String,CommPortIdentifier>();
    private Hashtable<String,PolarZones> myZoneSet = new Hashtable<String,PolarZones>();
    private Hashtable<String,PolarSport> mySportSet = new Hashtable<String,PolarSport>();

    private ErgoUserData myUser = null;
    private String myConfigurationPrefix = "";

    public ErgoControlConfig(JFrame owner, Properties properties, ErgoUserData user)
    {
        super(owner, DIALOGSTRING, true);

        myProperties = properties;
        myUser = user;
        if(myUser != null)
        {
            getContentPane().setLayout(new GridLayout(12,2));
            setSize(WINDOW_WIDTH,WINDOW_HEIGHT_USER);
            myConfigurationPrefix = myUser.getUserName() + "_";
        }
        else
        {
            getContentPane().setLayout(new GridLayout(5,2));
            setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
            myConfigurationPrefix = "";
        }

        setResizable(false);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        getContentPane().add(new JLabel(LABEL_SIZE));
        myWindowSize.setMinimum(MINIMUM_WINDOW_SIZE);
        myWindowSize.setMaximum(MAXIMUM_WINDOW_SIZE);
        myWindowSize.setValue(DEFAULT_SIZE);
        myWindowSize.addChangeListener(this);
        getContentPane().add(myWindowSize);

        getContentPane().add(new JLabel(LABEL_PORT));
        myPortChoice.addItem(LABEL_SIMULATION);
        Enumeration commEnum = CommPortIdentifier.getPortIdentifiers();
        while(commEnum.hasMoreElements())
        {
            CommPortIdentifier commId = (CommPortIdentifier)commEnum.nextElement();
            if(commId.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                myPorts.put(commId.getName(), commId);
                myPortChoice.addItem(commId.getName());
            }
        }
        getContentPane().add(myPortChoice);

        getContentPane().add(new JLabel(LABEL_DEVICE));
        //myDeviceChoice.addItem(DEVICE_DAUM);
        myDeviceChoice.addItem(DEVICE_KETTLER);
        getContentPane().add(myDeviceChoice);

        /*myRefreshPanel.setLayout(new GridLayout(1, 2));
        myRefreshPanel.add(new JLabel(LABEL_REFRESHRATE));
        myRefreshPanel.add(myRefreshValue);
        getContentPane().add(myRefreshPanel);
        getContentPane().add(myRefreshRate);*/
        
        myDampingPanel.setLayout(new GridLayout(1, 2));
		myDampingPanel.add(new JLabel(LABEL_DAMPINGRATE));
		myDampingPanel.add(myDampingValue);
		getContentPane().add(myDampingPanel);
        getContentPane().add(myDampingRate);

/*        mySamplePanel.setLayout(new GridLayout(1, 2));
        mySamplePanel.add(new JLabel(LABEL_SAMPLERATE));
        mySamplePanel.add(mySampleValue);
        getContentPane().add(mySamplePanel);
        getContentPane().add(mySampleRate);
*/
        if(myUser != null)
        {
            getContentPane().add(myPolarUserFile);
            getContentPane().add(myPolarUserFileSelect);
            myPolarUserFileSelect.addActionListener(this);
        
		    getContentPane().add(myGPXDir);
		    getContentPane().add(myGPXDirSelect);
		    myGPXDirSelect.addActionListener(this);
		
		    getContentPane().add(mySaveDir);
		    getContentPane().add(mySaveDirSelect);
		    mySaveDirSelect.addActionListener(this);

            getContentPane().add(new JLabel(LABEL_ZONESET));
            getContentPane().add(myZoneSetChoice);

            getContentPane().add(new JLabel(LABEL_SPORTSET));
            getContentPane().add(mySportSetChoice);
        
            getContentPane().add(myExportPPP);        
	        getContentPane().add(myExportGPX);
	        getContentPane().add(mySave);
            getContentPane().add(new JLabel()); //fill empty space
        }
        
        getContentPane().add(myConfirm);
        getContentPane().add(myAbort);

        myConfirm.addActionListener(this);
        myAbort.addActionListener(this);
//        myRefreshRate.addChangeListener(this);
        myDampingRate.addChangeListener(this);
//        mySampleRate.addChangeListener(this);

        addWindowListener(this);

        init();
    }

    private void init()
    {
//        int refreshRate = 0;
        int dampingRate = 0;
//        int sampleRate = 0;

        /*try
        {
            if(myProperties.contains(myConfigurationPrefix + PROPERTY_REFRESHRATE))
                refreshRate = Integer.parseInt(myProperties.getProperty(myConfigurationPrefix + PROPERTY_REFRESHRATE));
        } catch(Exception e)
        {
            log.error(e);
        }
        
        try
        {
            sampleRate = Integer.parseInt(myProperties.getProperty(myConfigurationPrefix + PROPERTY_SAMPLERATE))/1000;
        } catch(Exception e)
        {
            log.error(e);
        }
*/
        if(myProperties.getProperty(myConfigurationPrefix + PROPERTY_DAMPINGRATE) != null)
    		dampingRate = Integer.parseInt(myProperties.getProperty(myConfigurationPrefix + PROPERTY_DAMPINGRATE));
        
        if(myProperties.getProperty(myConfigurationPrefix + PROPERTY_WINDOW_SIZE) != null)
            myWindowSize.setValue(Integer.parseInt(myProperties.getProperty(myConfigurationPrefix + PROPERTY_WINDOW_SIZE)));

        myPortChoice.setSelectedItem(myProperties.getProperty(myConfigurationPrefix + PROPERTY_PORT));
        myDeviceChoice.setSelectedItem(myProperties.getProperty(myConfigurationPrefix + PROPERTY_DEVICE));

        /*myRefreshRate.setMaximum(REFRESH_MAX);
        if(refreshRate > REFRESH_MAX) refreshRate = REFRESH_MAX;
        myRefreshRate.setMinimum(REFRESH_MIN);
        if(refreshRate < REFRESH_MIN) refreshRate = REFRESH_MIN;
        myRefreshRate.setValue(refreshRate);
        myRefreshValue.setText(refreshRate + REFRESH_UNIT);*/
        myDampingRate.setMaximum(DAMPING_MAX);
		if(dampingRate > DAMPING_MAX) dampingRate = DAMPING_MAX;
		myDampingRate.setMinimum(DAMPING_MIN);
		if(dampingRate < DAMPING_MIN) dampingRate = DAMPING_MIN;
		myDampingRate.setValue(dampingRate);
        myDampingValue.setText(dampingRate + DAMPING_UNIT);
/*        mySampleRate.setMaximum(SAMPLE_MAX);
        if(sampleRate > SAMPLE_MAX) sampleRate = SAMPLE_MAX;
        mySampleRate.setMinimum(SAMPLE_MIN);
        if(sampleRate < SAMPLE_MIN) sampleRate = SAMPLE_MIN;
        mySampleRate.setValue(sampleRate);
        mySampleValue.setText(sampleRate + SAMPLE_UNIT);
*/
        if(myUser != null)
        {
            String polarDir = LABEL_POLARDIR;
            String saveDir = LABEL_SAVEDIR;
            String gpxDir = LABEL_GPXDIR;

            if(myUser.getPolarUserFile() != null && myUser.getPolarUserFile().length() > 0)
                polarDir = myUser.getPolarUserFile();
            myPolarUserFile.setText(polarDir);
            myPolarUserFile.setToolTipText(polarDir);

            if(myUser.getGPXDir() != null && myUser.getGPXDir().length() > 0)
                gpxDir = myUser.getGPXDir();
            myGPXDir.setText(gpxDir);
            myGPXDir.setToolTipText(gpxDir);

            if(myUser.getSaveDir() != null && myUser.getSaveDir().length() > 0)
                saveDir = myUser.getSaveDir();
            mySaveDir.setText(saveDir);
            mySaveDir.setToolTipText(saveDir);

            myZoneSet.clear();
            myZoneSetChoice.removeAllItems();
            if(myUser.getZones() != null)
            {
                Enumeration zoneEnum = myUser.getZones();
                while(zoneEnum.hasMoreElements())
                {
                    PolarZones zoneSet = (PolarZones)zoneEnum.nextElement();
                    myZoneSet.put(zoneSet.getName(), zoneSet);
                    myZoneSetChoice.addItem(zoneSet.getName());
                }
            }
            if(myUser.getSports() != null)
            {
                Enumeration sportEnum = myUser.getSports();
                while(sportEnum.hasMoreElements())
                {
                    PolarSport zone = (PolarSport)sportEnum.nextElement();
                    mySportSet.put(zone.getName(), zone);
                    mySportSetChoice.addItem(zone.getName());
                }
            }

            if(myUser.getActiveZone() != null)
                myZoneSetChoice.setSelectedItem(myUser.getActiveZone().getName());
            if(myUser.getActiveSport() != null)
                mySportSetChoice.setSelectedItem(myUser.getActiveSport().getName());

            myExportPPP.setSelected(myUser.getAutoExportPPP());
            myExportGPX.setSelected(myUser.getAutoExportGPX());
            mySave.setSelected(myUser.getAutoSave());
        }
    }

    public void actionPerformed(ActionEvent evt)
    {
        if(evt.getSource().equals(myPolarUserFileSelect))
        {
            JFileChooser polarFile = new JFileChooser();
            polarFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
            polarFile.setDialogTitle(FILEDIALOGSTRING_POLAR);
            polarFile.setFileFilter(new PolarUserFileFilter());
            if(myUser.getPolarUserFile() != null && myUser.getPolarUserFile().length() > 0)
                polarFile.setSelectedFile(new File(myUser.getPolarUserFile()));
            if(polarFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                myUser.setPolarUserFile(polarFile.getSelectedFile().getAbsolutePath());

            init();
        }
        if(evt.getSource().equals(myGPXDirSelect))
        {
            JFileChooser gpxDir = new JFileChooser();
            gpxDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
            gpxDir.setDialogTitle(FILEDIALOGSTRING_GPX);
            if(myUser.getGPXDir() != null && myUser.getGPXDir().length() > 0)
            	gpxDir.setSelectedFile(new File(myUser.getGPXDir()));
            if(gpxDir.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                myUser.setGPXDir(gpxDir.getSelectedFile().getAbsolutePath());

            init();
        }
        if(evt.getSource().equals(mySaveDirSelect))
        {
            JFileChooser saveDir = new JFileChooser();
            saveDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
            saveDir.setDialogTitle(FILEDIALOGSTRING_SAVE);
            if(myUser.getSaveDir() != null && myUser.getSaveDir().length() > 0)
                saveDir.setSelectedFile(new File(myUser.getSaveDir()));
            if(saveDir.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                myUser.setSaveDir(saveDir.getSelectedFile().getAbsolutePath());

            init();
        }
        if(evt.getSource().equals(myConfirm))
            confirm();
        if(evt.getSource().equals(myAbort))
            abort();
    }

    public void windowClosing(WindowEvent evt)
    {
        abort();
    }

    public void windowOpened(WindowEvent evt){}
    public void windowIconified(WindowEvent evt){}
    public void windowDeiconified(WindowEvent evt){}
    public void windowClosed(WindowEvent evt){}
    public void windowActivated(WindowEvent evt){}
    public void windowDeactivated(WindowEvent evt){}

    public void stateChanged(ChangeEvent evt)
    {
/*        if(evt.getSource().equals(myRefreshRate) )
            myRefreshValue.setText(Integer.toString(myRefreshRate.getValue()) + REFRESH_UNIT);
*/      if(evt.getSource().equals(myDampingRate) )
		{
            myDampingValue.setText(Integer.toString(myDampingRate.getValue()) + DAMPING_UNIT);
            this.firePropertyChange2(PROPERTY_DAMPINGRATE, null, new Integer(myDampingRate.getValue()));
		}
        if(evt.getSource().equals(myWindowSize) )
            this.firePropertyChange2(PROPERTY_WINDOW_SIZE, null, new Double(myWindowSize.getValue()/100.0));
/*        if(evt.getSource().equals(mySampleRate) )
            mySampleValue.setText(Integer.toString(mySampleRate.getValue()) + SAMPLE_UNIT);
*/
    }

    private void abort()
    {
        init();
        this.setVisible(false);
    }

    private void confirm()
    {
        save();
        this.setVisible(false);
    }

    public void save()
    {
        myProperties.setProperty(myConfigurationPrefix + PROPERTY_WINDOW_SIZE, Integer.toString(myWindowSize.getValue()));
        if(myPortChoice.getSelectedItem()!= null)
            myProperties.setProperty(myConfigurationPrefix + PROPERTY_PORT, (String)myPortChoice.getSelectedItem());
        if(myDeviceChoice.getSelectedItem()!= null)
            myProperties.setProperty(myConfigurationPrefix + PROPERTY_DEVICE, (String)myDeviceChoice.getSelectedItem());
//        myProperties.setProperty(myConfigurationPrefix + PROPERTY_REFRESHRATE, Integer.toString(myRefreshRate.getValue()));
        myProperties.setProperty(myConfigurationPrefix + PROPERTY_DAMPINGRATE, Integer.toString(myDampingRate.getValue()));
//        myProperties.setProperty(myConfigurationPrefix + PROPERTY_SAMPLERATE, Integer.toString(mySampleRate.getValue()*1000));
        if(myUser != null)
        {
            myUser.setActiveZone(getZone());
            myUser.setActiveSport(getSport());

            if(myExportPPP.isSelected() && !myUser.isInitialized())
            {
                JOptionPane.showMessageDialog(this, ERROR_POLAR_NOT_INITIALIZED);
                myExportPPP.setSelected(false);
                return;
            }
            myUser.setAutoExportPPP(myExportPPP.isSelected());
            if(myExportGPX.isSelected() && (myUser.getGPXDir() == null || myUser.getGPXDir().length() == 0))
            {
                JOptionPane.showMessageDialog(this, ERROR_GPXDIR_NOT_EXISTS);
                myExportGPX.setSelected(false);
                return;
            }
            myUser.setAutoExportGPX(myExportGPX.isSelected());
            if(mySave.isSelected() && (myUser.getSaveDir() == null || myUser.getSaveDir().length() == 0))
            {
                JOptionPane.showMessageDialog(this, ERROR_SAVEDIR_NOT_EXISTS);
                mySave.setSelected(false);
                return;
            }
            myUser.setAutoSave(mySave.isSelected());

            myUser.save(myProperties);
        }
    }

    public CommPortIdentifier getPort()
    {
    	if(myPortChoice.getSelectedItem() != null)
    	{
    		if(myPortChoice.getSelectedItem().equals(LABEL_SIMULATION))
    			return null;
    		else
    			return (CommPortIdentifier)myPorts.get(myPortChoice.getSelectedItem());
    	}
        else
            return null;
    }

    public PolarZones getZone()
    {
        if(myZoneSetChoice.getSelectedItem() != null)
            return (PolarZones)myZoneSet.get(myZoneSetChoice.getSelectedItem());
        else
            return null;
    }

    public PolarSport getSport()
    {
        if(mySportSetChoice.getSelectedItem() != null)
            return (PolarSport)mySportSet.get(mySportSetChoice.getSelectedItem());
        else
            return null;
    }

    public int getRefreshRate()
    {
        return 250;//myRefreshRate.getValue();
    }
    
    public int getDampingRate()
    {
        return myDampingRate.getValue();
    }

    public int getSampleRate()
    {
        //return mySampleRate.getValue()*1000;
        return 1000;
    }

    public boolean getAutoSave()
    {
        return mySave.isSelected();
    }

    public boolean getAutoExportPPP()
    {
        return myExportPPP.isSelected();
    }

    public boolean getAutoExportGPX()
    {
        return myExportGPX.isSelected();
    }

    public boolean getIsKettler()
    {
        if(myDeviceChoice.getSelectedItem() != null)
            return myDeviceChoice.getSelectedItem().equals(DEVICE_KETTLER);
        else
            return false;
    }

    public boolean getIsDaum()
    {
        if(myDeviceChoice.getSelectedItem() != null)
            return myDeviceChoice.getSelectedItem().equals(DEVICE_DAUM);
        else
            return false;
    }

    public String getConfigurationPrefix()
    {
        return myConfigurationPrefix;
    }

    public double getWindowSize()
    {
        return myWindowSize.getValue()/100.0;
    }

    public void setWindowSize(double factor)
    {
        myWindowSize.setValue((int)(factor*100));
    }

    private void firePropertyChange2(String propertyName, Object oldValue, Object newValue) {
        if (oldValue == null && newValue != null ||
           !oldValue.equals(newValue)) {
            this.pcs.firePropertyChange(propertyName, oldValue, newValue);
        }
    }


    public void addPropertyChangeListener2(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener2(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener2(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener2(String propertyName,
                                             PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

}
