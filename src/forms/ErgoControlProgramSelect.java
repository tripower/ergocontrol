package forms;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import data.ErgoDatastore;


public class ErgoControlProgramSelect extends JDialog implements
		ActionListener, WindowListener
{
	static final long serialVersionUID = 0;
	
	private final static String BUTTON_CONFIRM = "Ok";
	
	private final static String DIALOGSTRING = "ErgoControl Program Selection";
	
	private final static int WINDOW_WIDTH = 200;
    private final static int WINDOW_HEIGHT = 25;
    
    private JButton myConfirm = new JButton(BUTTON_CONFIRM);
    
    private JRadioButton myPulsButton = new JRadioButton("Puls");
    private JRadioButton myRPMButton = new JRadioButton("RPM");
    private JRadioButton mySpeedButton = new JRadioButton("Speed");
    private JRadioButton myGradeButton = new JRadioButton("Grade");
    private JRadioButton myPowerButton = new JRadioButton("Power");
    private ButtonGroup myTypeGroup = new ButtonGroup();
    
    private JRadioButton myTimeButton = new JRadioButton("Time");
    private JRadioButton myDistanceButton = new JRadioButton("Distance");
    private ButtonGroup myUnitGroup = new ButtonGroup();
    
    private boolean myPuls = false;
    private boolean myRPM = false;
    private boolean mySpeed = false;
    private boolean myGrade = false;
    private boolean myPower = false;
    
    private boolean myDistance = false;
    private boolean myTime = false;
    
    private int myType = 0;	
	public ErgoControlProgramSelect(JFrame owner)
    {
		super(owner, DIALOGSTRING, true);
        
        setResizable(false);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        myConfirm.addActionListener(this);

        addWindowListener(this);
    }
	
	private void init()
	{   
        int elements = 3;
        boolean bSelected1 = false;
        boolean bSelected2 = false;
        
        getContentPane().add(new JLabel());
        
        if(myDistance == true)
        {
        	elements++;
        	getContentPane().add(myDistanceButton);
        	myUnitGroup.add(myDistanceButton);
        	if(!bSelected1)
	        {
        		myDistanceButton.setSelected(true);
	        	bSelected1 = true;
	        }
        }
        
        if(myTime == true)
        {
        	elements++;
        	getContentPane().add(myTimeButton);
            myUnitGroup.add(myTimeButton);
	        if(!bSelected1)
	        {
	        	myTimeButton.setSelected(true);
	        	bSelected1 = true;
	        }
        }
        
        if(myGrade == true)
        {
        	if(bSelected2 == false)
        	{
        		elements+=2;
        		getContentPane().add(new JLabel());
        		getContentPane().add(new JSeparator());
        		myGradeButton.setSelected(true);
        		bSelected2 = true;
        	}
        	elements++;
        	getContentPane().add(myGradeButton);
        	myTypeGroup.add(myGradeButton);        	
        }
        if(myPower == true)
        {
        	if(bSelected2 == false)
        	{
        		elements+=2;
        		getContentPane().add(new JLabel());
        		getContentPane().add(new JSeparator());
        		myPowerButton.setSelected(true);
        		bSelected2 = true;
        	}
        	elements++;
        	getContentPane().add(myPowerButton);
        	myTypeGroup.add(myPowerButton);        	
        }
        if(myPuls == true)
        {
        	if(bSelected2 == false)
        	{
        		elements+=2;
        		getContentPane().add(new JLabel());
        		getContentPane().add(new JSeparator());
        		myPulsButton.setSelected(true);
        		bSelected2 = true;
        	}
        	elements++;
        	getContentPane().add(myPulsButton);
        	myTypeGroup.add(myPulsButton);        	
        }
        if(myRPM == true)
        {
        	if(bSelected2 == false)
        	{
        		elements+=2;
        		getContentPane().add(new JLabel());
        		getContentPane().add(new JSeparator());
        		myRPMButton.setSelected(true);
        		bSelected2 = true;
        	}
        	elements++;
        	getContentPane().add(myRPMButton);
        	myTypeGroup.add(myRPMButton);        	
        }
        if(mySpeed == true)
        {
        	if(bSelected2 == false)
        	{
        		elements+=2;
        		getContentPane().add(new JLabel());
        		getContentPane().add(new JSeparator());
        		mySpeedButton.setSelected(true);
        		bSelected2 = true;
        	}
        	elements++;
        	getContentPane().add(mySpeedButton);
        	myTypeGroup.add(mySpeedButton);        	
        }
        
        getContentPane().setLayout(new GridLayout(elements,1));
        setSize(WINDOW_WIDTH,WINDOW_HEIGHT * (elements+1));        
       
        getContentPane().add(new JLabel());
        getContentPane().add(myConfirm);
    }
	
	public void showDialog()
	{
		init();
		super.setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource().equals(myConfirm))
		{
			if(myPulsButton.isSelected())
				myType = ErgoDatastore.PULS;
			if(myRPMButton.isSelected())
				myType = ErgoDatastore.RPM;
			if(mySpeedButton.isSelected())
				myType = ErgoDatastore.SPEED;
			if(myGradeButton.isSelected())
				myType = ErgoDatastore.GRADE;
			if(myPowerButton.isSelected())
				myType = ErgoDatastore.POWER;
			
			this.setVisible(false);
		}
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

	public int getType()
	{
		return myType;
	}
	
	public boolean getUseDistance()
	{
		return myDistanceButton.isSelected();
	}
	
	public void setPuls(boolean bValue)
	{
		myPuls = bValue;
	}
	
    public void setRPM(boolean bValue)
	{
    	myRPM = bValue;
	}
    
    public void setSpeed(boolean bValue)
	{
    	mySpeed = bValue;
	}
    
    public void setGrade(boolean bValue)
	{
    	myGrade = bValue;
	}
    
    public void setPower(boolean bValue)
	{
		myPower = bValue;
	}  
    
    public void setDistance(boolean bValue)
	{
		myDistance = bValue;
	}  
    
    public void setTime(boolean bValue)
	{
		myTime = bValue;
	}  
}
