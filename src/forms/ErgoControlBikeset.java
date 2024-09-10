package forms;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import data.ErgoBikeDefinition;
import data.ErgoUserData;


public class ErgoControlBikeset extends JDialog implements
	ActionListener, WindowListener
{
	static final long serialVersionUID = 0;
	
	private static Properties myProperties = null;
	
	private final static String BUTTON_CONFIRM = "Ok";
	private final static String BUTTON_ABORT = "Cancel";
	private final static String LABEL_NAME = "Name";
	private final static String LABEL_BIKE = "Bike";
	private final static String LABEL_WEIGHT = "Weight";
	private final static String LABEL_HEIGHT = "Height";
	private final static String LABEL_CIRCUMFERENCE = "Circumference";
	private final static String LABEL_BIKETYPE = "Bike Type";
	private final static String LABEL_TRANSTYPE = "Transmission Type";
	private final static String LABEL_FRONTTIRETYPE = "Front Tire Type";
	private final static String LABEL_REARTIRETYPE = "Rear Tire Type";
	private final static String LABEL_RIDER = "Rider";
	
	private final static String DIALOGSTRING = "ErgoControl Bike Definition";
	
	private final static int WINDOW_WIDTH = 300;
    private final static int WINDOW_HEIGHT = 375;
    
    private JButton myConfirm = new JButton(BUTTON_CONFIRM);
    private JButton myAbort = new JButton(BUTTON_ABORT);
    private JLabel myLabelName = new JLabel(LABEL_NAME);
    private JTextField myName = new JTextField();
    private JLabel myLabelBike = new JLabel(LABEL_BIKE);
    private JLabel myLabelWeight = new JLabel(LABEL_WEIGHT);
    private JSpinner myBikeWeight = new JSpinner(new SpinnerNumberModel(0, 0, 200, 0.1));
    private JLabel myLabelRiderHeight = new JLabel(LABEL_HEIGHT);
    private JSpinner myRiderHeight = new JSpinner(new SpinnerNumberModel(0, 0, 3, 0.01));
    private JLabel myLabelRiderWeight = new JLabel(LABEL_WEIGHT);
    private JSpinner myRiderWeight = new JSpinner(new SpinnerNumberModel(0, 0, 200, 0.1));
    private JLabel myLabelCircumference = new JLabel(LABEL_CIRCUMFERENCE);
    private JSpinner myCircumference = new JSpinner();
    private JLabel myLabelBikeType = new JLabel(LABEL_BIKETYPE);
    private JComboBox myBikeType = new JComboBox();
    private JLabel myLabelTransType = new JLabel(LABEL_TRANSTYPE);
    private JComboBox myTransType = new JComboBox();
    private JLabel myLabelFrontTireType = new JLabel(LABEL_FRONTTIRETYPE);
    private JComboBox myFrontTireType = new JComboBox();
    private JLabel myLabelRearTireType = new JLabel(LABEL_REARTIRETYPE);
    private JComboBox myRearTireType = new JComboBox();
    private JLabel myLabelRider = new JLabel(LABEL_RIDER);
    
    private ErgoBikeDefinition myDefinition = null;
    private ErgoUserData myUser = null;
    
	public ErgoControlBikeset(JFrame owner, ErgoUserData user, Properties properties)
    {
		super(owner, DIALOGSTRING, true);
		
		initClass(owner, user, properties);
    }
	
	public ErgoControlBikeset(JFrame owner, ErgoBikeDefinition ergoBike, ErgoUserData user, Properties properties)
    {
		super(owner, DIALOGSTRING, true);
		
		initClass(owner, user, properties);
		
		myDefinition = ergoBike;
    }
	
	private void initClass(JFrame owner, ErgoUserData user, Properties properties)
	{
		myProperties = properties;
		myUser = user;
        
        setResizable(false);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        addWindowListener(this);
	}
	
	private void init()
	{          
        getContentPane().setLayout(new GridLayout(16,1));
        setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        
        if(myDefinition == null)
        	myDefinition = new ErgoBikeDefinition();
        
        getContentPane().add(myLabelName);
        getContentPane().add(myName);
        
        getContentPane().add(new JLabel());
        getContentPane().add(new JLabel());
        
        getContentPane().add(myLabelRider);
        getContentPane().add(new JLabel());
        getContentPane().add(myLabelRiderWeight);
        getContentPane().add(myRiderWeight);
        getContentPane().add(myLabelRiderHeight);
        getContentPane().add(myRiderHeight);
        
        getContentPane().add(new JLabel());
        getContentPane().add(new JLabel());
        getContentPane().add(new JSeparator());
        getContentPane().add(new JSeparator());
        
        getContentPane().add(myLabelBike);
        getContentPane().add(new JLabel());
        getContentPane().add(myLabelWeight);
        getContentPane().add(myBikeWeight);
        getContentPane().add(myLabelBikeType);
        getContentPane().add(myBikeType);
        getContentPane().add(myLabelTransType);
        getContentPane().add(myTransType);
        getContentPane().add(myLabelFrontTireType);
        getContentPane().add(myFrontTireType);
        getContentPane().add(myLabelRearTireType);
        getContentPane().add(myRearTireType);
        getContentPane().add(myLabelCircumference);
        getContentPane().add(myCircumference);
        
        getContentPane().add(new JLabel());
        getContentPane().add(new JLabel());
       
        getContentPane().add(myConfirm);
        getContentPane().add(myAbort);
        
        myConfirm.addActionListener(this);
        myAbort.addActionListener(this);
        
        String bikeTypes[] = ErgoBikeDefinition.getBikeTypes();
        for(int i = 0; i < bikeTypes.length; i++)
        	myBikeType.addItem(bikeTypes[i]);
        
        String transTypes[] = ErgoBikeDefinition.getTransmissionTypes();
        for(int i = 0; i < transTypes.length; i++)
        	myTransType.addItem(transTypes[i]);
        
        String tireTypes[] = ErgoBikeDefinition.getTireTypes();
        for(int i = 0; i < tireTypes.length; i++)
        {
        	myFrontTireType.addItem(tireTypes[i]);
        	myRearTireType.addItem(tireTypes[i]);
        }	
        
        if(myDefinition != null)
        {
        	myRiderWeight.setValue(new Double(myDefinition.getRiderWeight()));
        	myRiderHeight.setValue(new Double(myDefinition.getRiderHeight()));
        	myBikeWeight.setValue(new Double(myDefinition.getBikeWeight()));
        	myCircumference.setValue(new Integer(myDefinition.getCircumference()));
        	
        	myBikeType.setSelectedIndex(myDefinition.getBikeType());
        	myTransType.setSelectedIndex(myDefinition.getTransmissionType());
        	myRearTireType.setSelectedIndex(myDefinition.getRearTireType());
        	myFrontTireType.setSelectedIndex(myDefinition.getFrontTireType());
        	
        	myName.setText(myDefinition.getName());
        }
    }
	
	public void showDialog()
	{
		init();
		super.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource().equals(myConfirm))
		{
			myDefinition.setName(myName.getText());
			
			myDefinition.setRiderWeight(((Double)myRiderWeight.getValue()).doubleValue());
			myDefinition.setRiderHeight(((Double)myRiderHeight.getValue()).doubleValue());
			myDefinition.setBikeWeight(((Double)myBikeWeight.getValue()).doubleValue());
			myDefinition.setCircumference(((Integer)myCircumference.getValue()).intValue());
        	
        	myDefinition.setBikeType(myBikeType.getSelectedIndex());
        	myDefinition.setTransmissionType(myTransType.getSelectedIndex());
        	myDefinition.setRearTireType(myRearTireType.getSelectedIndex());
        	myDefinition.setFrontTireType(myFrontTireType.getSelectedIndex());
			
			myDefinition.save(myUser, myProperties);
						
			this.setVisible(false);
		}
		if(evt.getSource().equals(myAbort))
			this.setVisible(false);
	}

	public void windowActivated(WindowEvent arg0) {


	}

	public void windowClosed(WindowEvent arg0) {


	}

	public void windowClosing(WindowEvent arg0) {


	}

	public void windowDeactivated(WindowEvent arg0) {


	}

	public void windowDeiconified(WindowEvent arg0) {


	}

	public void windowIconified(WindowEvent arg0) {


	}

	public void windowOpened(WindowEvent arg0) {


	}

	public ErgoBikeDefinition getData()
	{
		return myDefinition;
	}
}
