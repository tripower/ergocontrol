package forms;


import graph.ErgoLineGraph;
import graph.ErgoLineGraphEvent;
import graph.ErgoLineGraphListener;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import data.ErgoData;
import data.ErgoDatastore;

import tools.ErgoTools;

import java.util.Properties;
import java.util.Vector;
import java.util.Date;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 03.03.2005
 * Time: 07:46:38
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlProgram extends JDialog implements ActionListener, WindowListener, ListSelectionListener, ErgoLineGraphListener {
	static final long serialVersionUID = 0;
	
//    private static Properties myProperties = null;
    private static ErgoControlConfig myErgoControlConfig = null;

    private final static String DIALOGSTRING = "ErgoControl Program";
    private final static String BUTTON_CONFIRM = "Ok";
    private final static String BUTTON_ABORT = "Cancel";

    private final static String PROGRAM_CONCONI = "Conconi Test";
    private final static String PROGRAM_GRADATION = "Gradation Test";
    private final static String PROGRAM_SEPARATOR = "----------------------";
    private final static String PROGRAM_CLIMB = "Climbing";
    private final static String PROGRAM_INT = "Intervall";
    private final static String PROGRAM_RAF = "Rise and Fall";
    private final static String PROGRAM_POWER = "Power";
    private final static String PROGRAM_PULS = "Puls";
    private final static String PROGRAM_SPEED = "Speed";
    private final static String PROGRAM_RPM = "RPM";
    private final static String PROGRAM_CUSTOMIZED_POWER = "Custom Power";
    private final static String PROGRAM_CUSTOMIZED_PULS = "Custom Puls";
    private final static String PROGRAM_CUSTOMIZED_SPEED = "Custom Speed";
    private final static String PROGRAM_CUSTOMIZED_RPM = "Custom RPM";

    private final static String PROGRAM_LABEL_DURATION = "Duration (Overall)";
    private final static String PROGRAM_LABEL_DURATION_STEP = "Duration (Step)";
    private final static String PROGRAM_LABEL_DURATION_MIN = "Duration (Min)";
    private final static String PROGRAM_LABEL_DURATION_MAX = "Duration (Max)";
    private final static String PROGRAM_LABEL_DISTANCE = "Distance (Overall)";
    private final static String PROGRAM_LABEL_DISTANCE_STEP = "Distance (Step)";
    private final static String PROGRAM_LABEL_DISTANCE_MIN = "Distance (Min)";
    private final static String PROGRAM_LABEL_DISTANCE_MAX = "Distance (Max)";
    private final static String PROGRAM_LABEL_POWER_START = "Power Start";
    private final static String PROGRAM_LABEL_POWER_END = "Power End";
    private final static String PROGRAM_LABEL_POWER_STEP = "Power Step";
    private final static String PROGRAM_LABEL_POWER_MIN = "Power Min";
    private final static String PROGRAM_LABEL_POWER_MAX = "Power Max";

    private final static String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private ErgoDatastore myErgoDatastore = null;

    private final static int WINDOW_WIDTH = 600;
    private final static int WINDOW_HEIGHT = 400;

    private final static int DEFAULT_SAMPLERATE = 1000; //1 sec

    private JPanel mySouthPanel = new JPanel();
    private JButton myConfirm = new JButton(BUTTON_CONFIRM);
    private JButton myAbort = new JButton(BUTTON_ABORT);
    private JPanel myWestPanel = new JPanel();
    private JList myProgramType = new JList();
    private JPanel myDataPanel = new JPanel();
    private JLabel myTime = new JLabel();
    private JLabel myValue = new JLabel();
    private ErgoLineGraph myProgram = new ErgoLineGraph();
    private JScrollPane myProgramScroll = new JScrollPane();

    private Vector<String> myListData = new Vector<String>();

    private int mySampleRate = DEFAULT_SAMPLERATE;
    private boolean myUseDistance = false;

    public ErgoControlProgram(JFrame owner, Properties properties, ErgoControlConfig config, boolean bUseDistance)
    {
        super(owner, DIALOGSTRING, true);
        myUseDistance = bUseDistance;

//        myProperties = properties;
        myErgoControlConfig = config;

        getContentPane().setLayout(new BorderLayout());
        mySouthPanel.setLayout(new GridLayout(1,2));
        myWestPanel.setLayout(new BorderLayout());
        setSize(WINDOW_WIDTH,WINDOW_HEIGHT);

        setResizable(false);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        if(!myUseDistance)
            myListData.add(PROGRAM_CONCONI);

        myListData.add(PROGRAM_GRADATION);
	    myListData.add(PROGRAM_SEPARATOR);
        myListData.add(PROGRAM_CLIMB);
        myListData.add(PROGRAM_INT);
        myListData.add(PROGRAM_RAF);
        myListData.add(PROGRAM_SEPARATOR);
        /*myListData.add(PROGRAM_PULS);
        myListData.add(PROGRAM_SPEED);
        myListData.add(PROGRAM_RPM);
        myListData.add(PROGRAM_SEPARATOR);*/
        myListData.add(PROGRAM_CUSTOMIZED_POWER);
        myListData.add(PROGRAM_CUSTOMIZED_PULS);
        myListData.add(PROGRAM_CUSTOMIZED_SPEED);
        myListData.add(PROGRAM_CUSTOMIZED_RPM);
        myProgramType.setListData(myListData);

        mySouthPanel.add(myConfirm);
        mySouthPanel.add(myAbort);

        myProgramScroll.setViewportView(myProgram);

        myDataPanel.setLayout(new GridLayout(2,1));
        myDataPanel.add(myValue);
        myDataPanel.add(myTime);

        myWestPanel.add(myProgramType, BorderLayout.CENTER);
        myWestPanel.add(myDataPanel, BorderLayout.SOUTH);

        getContentPane().add(myWestPanel, BorderLayout.WEST);

        getContentPane().add(myProgramScroll, BorderLayout.CENTER);
        getContentPane().add(mySouthPanel, BorderLayout.SOUTH);

        myProgramType.addListSelectionListener(this);
        myProgram.addErgoLineGraphListener(this);

        myConfirm.addActionListener(this);
        myAbort.addActionListener(this);

        addWindowListener(this);

        init();
    }

    private void init()
    {
        mySampleRate = myErgoControlConfig.getSampleRate();

        myTime.setText("");
        myValue.setText("");

        repaint();
    }

    public ErgoDatastore getDatastore()
    {
        return myErgoDatastore;
    }

    public void actionPerformed(ActionEvent evt)
    {
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

    public void valueChanged(ListSelectionEvent evt)
    {
        if(evt.getSource().equals(myProgramType))
        {
            String program = (String)myProgramType.getSelectedValue();
            boolean isDrawing = false;

            if(program.equals(PROGRAM_SEPARATOR))
                return;

            myProgram.unsetAllAttributes();

            if(program.equals(PROGRAM_CONCONI))
            {
                myErgoDatastore =  new ErgoDatastore(PROGRAM_CONCONI, mySampleRate);
                myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
                int dur = 120;
                int power = 100;
                int joule = 12000;

                for(int i=0; power <= ErgoTools.MAX_POWER; i++)
                {
                    if(i == dur)
                    {
                        power += 20;
                        dur = dur + Math.round((float)joule / power);
                    }
                    if(power <= ErgoTools.MAX_POWER)
                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, 0, power, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
                }

                myProgram.setAttribute(ErgoLineGraph.POWER);
            }
            else if(program.equals(PROGRAM_GRADATION))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                config.add(PROGRAM_LABEL_POWER_START, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, PROGRAM_LABEL_POWER_END);
                config.add(PROGRAM_LABEL_POWER_END, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, PROGRAM_LABEL_POWER_START, null);
                config.add(PROGRAM_LABEL_POWER_STEP, ErgoTools.MIN_POWER_STEP, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, null);
                if(myUseDistance)
                {
                	config.add(PROGRAM_LABEL_DISTANCE_STEP, 0.1, 999, 0.1, null, null);
                	config.showDialog();
                	
                	if(!config.wasCanceled())
                	{
                		double power = ((Double)config.getValue(PROGRAM_LABEL_POWER_START)).doubleValue();
	                    double power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_END)).doubleValue();
	                    double step = ((Double)config.getValue(PROGRAM_LABEL_POWER_STEP)).doubleValue();
	                    double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE_STEP)).doubleValue();
	                    double distanceAll = distance * (power_max - power) / step;
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_GRADATION, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(double i=0; i <= distanceAll; i+=distance)
	                    {
	                        int newPower = 0;
	                        
	                        newPower = (int)(Math.floor(power/ErgoTools.MIN_POWER_STEP)*ErgoTools.MIN_POWER_STEP);
	                        if(power <= power_max)
	                        {
	                        	for(double j=i; j < (i+distance); j+=0.01)
	                        		myErgoDatastore.add(new ErgoData(null, 0, 0, 0, j, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/distance)*mySampleRate - 3600000))), false));
	                        }
	                        power += step;
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
                	}
                	else
	                    myErgoDatastore = null;
                }
                else
                {
                	config.add(PROGRAM_LABEL_DURATION_STEP, -3600000, -3600000, 0, null, null);
	                config.showDialog();
	                
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION_STEP)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && !config.wasCanceled())
	                {
	                    double power = ((Double)config.getValue(PROGRAM_LABEL_POWER_START)).doubleValue();
	                    double power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_END)).doubleValue();
	                    double step = ((Double)config.getValue(PROGRAM_LABEL_POWER_STEP)).doubleValue();
	                    long dur2 = dur;
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_GRADATION, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    for(long i=0; i <= dur2; i++)
	                    {
	                        int newPower = 0;
	                        if(i == dur2)
	                        {
	                            power += step;
	                            dur2 += dur;
	                        }
	                        newPower = (int)(Math.floor(power/ErgoTools.MIN_POWER_STEP)*ErgoTools.MIN_POWER_STEP);
	                        if(power <= power_max)
	                            myErgoDatastore.add(new ErgoData(null, 0, 0, 0, 0, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	                        else
	                            dur2 = 0;
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
	                }
	                else
	                    myErgoDatastore = null;
                }
            }
            else if(program.equals(PROGRAM_CLIMB))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                if(myUseDistance)
                	config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
                else
                	config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_LABEL_POWER_START, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, PROGRAM_LABEL_POWER_END);
                config.add(PROGRAM_LABEL_POWER_END, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, PROGRAM_LABEL_POWER_START, null);
                config.add(PROGRAM_LABEL_POWER_STEP, ErgoTools.MIN_POWER_STEP, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	if(!config.wasCanceled())
                	{
                		double power = ((Double)config.getValue(PROGRAM_LABEL_POWER_START)).doubleValue();
	                    double power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_END)).doubleValue();
	                    double step = ((Double)config.getValue(PROGRAM_LABEL_POWER_STEP)).doubleValue();
	                    double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                    double dist_step = (distance/(((power_max-power)/step)+1));	                    
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_GRADATION, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(double i=0; i <= distance; i+=dist_step)
	                    {
	                        int newPower = 0;
	                        
	                        newPower = (int)(Math.floor(power/ErgoTools.MIN_POWER_STEP)*ErgoTools.MIN_POWER_STEP);
	                        if(power <= power_max)
	                        {
	                        	for(double j=i; j < (i+dist_step); j+=0.01)
	                        		myErgoDatastore.add(new ErgoData(null, 0, 0, 0, j, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/dist_step)*mySampleRate - 3600000))), false));
	                        }
	                        power += step;
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
                	}
                	else
	                    myErgoDatastore = null;
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && !config.wasCanceled())
	                {
	                    double power = ((Double)config.getValue(PROGRAM_LABEL_POWER_START)).doubleValue();
	                    double power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_END)).doubleValue();
	                    double step = ((Double)config.getValue(PROGRAM_LABEL_POWER_STEP)).doubleValue();
	                    long dur_step = (long)(dur/(((power_max-power)/step)+1));
	                    long dur2 = dur_step;
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CLIMB, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(long i=0; i <= dur; i++)
	                    {
	                        int newPower = 0;
	                        if(i == dur2)
	                        {
	                            power += step;
	                            dur2 += dur_step;
	                        }
	                        newPower = (int)(Math.floor(power/ErgoTools.MIN_POWER_STEP)*ErgoTools.MIN_POWER_STEP);
	                        if(power <= power_max)
	                            myErgoDatastore.add(new ErgoData(null, 0, 0, 0, 0, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
	                }
	                else
	                    myErgoDatastore = null;
                }
            }
            else if(program.equals(PROGRAM_INT))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                if(myUseDistance)
                {
                	config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
	                config.add(PROGRAM_LABEL_DISTANCE_MIN, 0.1, 999, 0.1, null, null);
	                config.add(PROGRAM_LABEL_POWER_MIN, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, PROGRAM_LABEL_POWER_MAX);
	                config.add(PROGRAM_LABEL_DISTANCE_MAX, 0.1, 999, 0.1, null, null);
                }
                else
                {
	                config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
	                config.add(PROGRAM_LABEL_DURATION_MIN, -3600000, -3600000, 0, null, null);
	                config.add(PROGRAM_LABEL_POWER_MIN, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, PROGRAM_LABEL_POWER_MAX);
	                config.add(PROGRAM_LABEL_DURATION_MAX, -3600000, -3600000, 0, null, null);
                }
                config.add(PROGRAM_LABEL_POWER_MAX, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, PROGRAM_LABEL_POWER_MIN, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	if(!config.wasCanceled())
                	{
                		double power_min = ((Double)config.getValue(PROGRAM_LABEL_POWER_MIN)).doubleValue();
	                    double power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_MAX)).doubleValue();
	                    double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                    double distance_min = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE_MIN)).doubleValue();
	                    double distance_max = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE_MAX)).doubleValue();
	                    double dist_min = 0;
	                    double dist_max = 0;
	                    
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_GRADATION, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(double i=0; i <= distance; i+=0.01)
	                    {
	                        int newPower = 0;
	
	                        if(dist_max > 0)
	                            newPower = (int)power_max;
	                        else
	                            newPower = (int)power_min;
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, i, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/0.01)*mySampleRate - 3600000))), false));
	                        
	                        if(dist_max == 0)
	                        {
	                        	if(dist_min > distance_min)
	                            {
	                                dist_max+=0.01;
	                                dist_min = 0;
	                            }
	                        	else
	                        		dist_min+=0.01;	                            
	                        }
	                        else if(dist_min == 0)
	                        {
	                            if(dist_max > distance_max)
	                            {
	                                dist_min+=0.01;
	                                dist_max = 0;
	                            }
	                            else
	                            	dist_max+=0.01;
	                        }
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
                	}
                	else
	                    myErgoDatastore = null;
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                long dur_min = (((Date)config.getValue(PROGRAM_LABEL_DURATION_MIN)).getTime()+3600000)/mySampleRate;
	                long dur_max = (((Date)config.getValue(PROGRAM_LABEL_DURATION_MAX)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && dur_min > 0 && dur_max > 0 && !config.wasCanceled())
	                {
	                    int power_min = ((Double)config.getValue(PROGRAM_LABEL_POWER_MIN)).intValue();
	                    int power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_MAX)).intValue();
	                    long time_min = 0;
	                    long time_max = 0;
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_INT, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    for(long i=0; i <= dur; i++)
	                    {
	                        int newPower = 0;
	
	                        if(time_max == 0)
	                        {
	                            time_min++;
	                            if(time_min > dur_min)
	                            {
	                                time_max++;
	                                time_min = 0;
	                            }
	                        }
	                        else if(time_min == 0)
	                        {
	                            time_max++;
	                            if(time_max > dur_max)
	                            {
	                                time_min++;
	                                time_max = 0;
	                            }
	                        }
	
	                        if(time_max > 0)
	                            newPower = power_max;
	                        else
	                            newPower = power_min;
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, 0, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
	                }
	                else
	                    myErgoDatastore = null;
                }
            }
            else if(program.equals(PROGRAM_RAF))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                
                if(myUseDistance)
                {
	                config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
	                config.add(PROGRAM_LABEL_DISTANCE_MIN, 0.1, 999, 0.1, null, null);
	                config.add(PROGRAM_LABEL_POWER_MIN, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, PROGRAM_LABEL_POWER_MAX);
	                config.add(PROGRAM_LABEL_DISTANCE_MAX, 0.1, 999, 0.1, null, null);
                }
                else
                {
                	config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                    config.add(PROGRAM_LABEL_DURATION_MIN, -3600000, -3600000, 0, null, null);
                    config.add(PROGRAM_LABEL_POWER_MIN, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, PROGRAM_LABEL_POWER_MAX);
                    config.add(PROGRAM_LABEL_DURATION_MAX, -3600000, -3600000, 0, null, null);                	
                }
                config.add(PROGRAM_LABEL_POWER_MAX, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, PROGRAM_LABEL_POWER_MIN, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	if(!config.wasCanceled())
                	{
                		double power = ((Double)config.getValue(PROGRAM_LABEL_POWER_MIN)).doubleValue();
	                    double power_max = ((Double)config.getValue(PROGRAM_LABEL_POWER_MAX)).doubleValue();
	                    double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                    double distance_min = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE_MIN)).doubleValue();
	                    double distance_max = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE_MAX)).doubleValue();
	                    double step_up = (power_max - power) / (double)distance_min * 0.01;
	                    double step_down = (power_max - power) / (double)distance_max * 0.01;
	                    
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_GRADATION, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(double i=0; i <= distance; i+=0.01)
	                    {
	                        int newPower = (int)(Math.floor(power/ErgoTools.MIN_POWER_STEP)*ErgoTools.MIN_POWER_STEP);
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, i, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/0.01)*mySampleRate - 3600000))), false));
	                        if(i <= distance_min)
	                            power += step_up;
	                        if(i > distance_min && i < (distance_min+distance_max))
	                            power -= step_down;
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
                	}
                	else
	                    myErgoDatastore = null;
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                long dur_min = (((Date)config.getValue(PROGRAM_LABEL_DURATION_MIN)).getTime()+3600000)/mySampleRate;
	                long dur_max = (((Date)config.getValue(PROGRAM_LABEL_DURATION_MAX)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && dur_min > 0 && dur_max > 0 && !config.wasCanceled())
	                {
	                    double power = ((Double)config.getValue(PROGRAM_LABEL_POWER_MIN)).intValue();
	                    double step_up = (((Double)config.getValue(PROGRAM_LABEL_POWER_MAX)).intValue() - ((Double)config.getValue(PROGRAM_LABEL_POWER_MIN)).intValue()) / (double)dur_min;
	                    double step_down = (((Double)config.getValue(PROGRAM_LABEL_POWER_MAX)).intValue() - ((Double)config.getValue(PROGRAM_LABEL_POWER_MIN)).intValue()) / (double)dur_max;
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_RAF, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(long i=0; i <= dur; i++)
	                    {
	                        int newPower = (int)(Math.floor(power/ErgoTools.MIN_POWER_STEP)*ErgoTools.MIN_POWER_STEP);
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, 0, newPower, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	                        if(i <= dur_min)
	                            power += step_up;
	                        if(i > dur_min && i < (dur_min+dur_max))
	                            power -= step_down;
	                    }
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
	                }
	                else
	                    myErgoDatastore = null;
                }
            }
            /*else if(program.equals(PROGRAM_PULS))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_PULS, ErgoTools.MIN_PULS, ErgoTools.MAX_PULS, ErgoTools.MIN_PULS_STEP, null, null);
                config.showDialog();
                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
                if(dur > 0 && !config.wasCanceled())
                {
                    int puls = ((Double)config.getValue(PROGRAM_PULS)).intValue();

                    myErgoDatastore =  new ErgoDatastore(PROGRAM_PULS, mySampleRate);
                    for(long i=0; i <= dur; i++)
                        myErgoDatastore.add(new ErgoData(puls, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));

                    myProgram.setAttribute(ErgoLineGraph.PULS);
                }
                else
                    myErgoDatastore = null;
            }
            else if(program.equals(PROGRAM_SPEED))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_SPEED, ErgoTools.MIN_SPEED, ErgoTools.MAX_SPEED, ErgoTools.MIN_SPEED_STEP, null, null);
                config.showDialog();
                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
                if(dur > 0 && !config.wasCanceled())
                {
                    int speed = ((Double)config.getValue(PROGRAM_SPEED)).intValue();

                    myErgoDatastore =  new ErgoDatastore(PROGRAM_SPEED, mySampleRate);
                    for(long i=0; i <= dur; i++)
                        myErgoDatastore.add(new ErgoData(null, 0, 0, speed, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));

                    myProgram.setAttribute(ErgoLineGraph.SPEED);
                }
                else
                    myErgoDatastore = null;
            }
            else if(program.equals(PROGRAM_RPM))
            {
                ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
                config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_RPM, ErgoTools.MIN_RPM, ErgoTools.MAX_SPEED, ErgoTools.MIN_RPM_STEP, null, null);
                config.showDialog();
                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
                if(dur > 0 && !config.wasCanceled())
                {
                    int rpm = ((Double)config.getValue(PROGRAM_RPM)).intValue();

                    myErgoDatastore =  new ErgoDatastore(PROGRAM_RPM, mySampleRate);
                    for(long i=0; i <= dur; i++)
                        myErgoDatastore.add(new ErgoData(null, 0, rpm, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));

                    myProgram.setAttribute(ErgoLineGraph.RPM);
                }
                else
                    myErgoDatastore = null;
            }*/
            //else JOptionPane.showMessageDialog(this, "Currently not implemented!");

            else if(program.equals(PROGRAM_CUSTOMIZED_POWER))
            {
            	ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
            	if(myUseDistance)
            		config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
            	else
            		config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_POWER, ErgoTools.MIN_POWER, ErgoTools.MAX_POWER, ErgoTools.MIN_POWER_STEP, null, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                if(!config.wasCanceled())
	                {
	                    int power = ((Double)config.getValue(PROGRAM_POWER)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_POWER, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(double i=0; i <= distance; i+=0.01)
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, i, power, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/0.01)*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
	                }
	                else
	                    myErgoDatastore = null;                	
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && !config.wasCanceled())
	                {
	                    int power = ((Double)config.getValue(PROGRAM_POWER)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_POWER, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.POWER);
	                    
	                    for(long i=0; i <= dur; i++)
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, 0, 0, power, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.POWER);
	                }
	                else
	                    myErgoDatastore = null;
                }
                
            	/*myErgoDatastore = new ErgoDatastore(PROGRAM_CUSTOMIZED_POWER, mySampleRate);
                myProgram.setAttribute(ErgoLineGraph.POWER);*/
                isDrawing = true;
            }
            else if(program.equals(PROGRAM_CUSTOMIZED_PULS))
            {
            	ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
            	if(myUseDistance)
            		config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
            	else
            		config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_PULS, ErgoTools.MIN_PULS, ErgoTools.MAX_PULS, ErgoTools.MIN_PULS_STEP, null, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                if(!config.wasCanceled())
	                {
	                    int puls = ((Double)config.getValue(PROGRAM_PULS)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_PULS, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.PULS);
	                    
	                    for(double i=0; i <= distance; i+=0.01)
	                        myErgoDatastore.add(new ErgoData(null, puls, 0, 0, i, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/0.01)*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.PULS);
	                }
	                else
	                    myErgoDatastore = null;                	
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && !config.wasCanceled())
	                {
	                    int puls = ((Double)config.getValue(PROGRAM_PULS)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_PULS, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.PULS);
	                    
	                    for(long i=0; i <= dur; i++)
	                        myErgoDatastore.add(new ErgoData(null, puls, 0, 0, 0, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.PULS);
	                }
	                else
	                    myErgoDatastore = null;
                }
                
                /*myErgoDatastore = new ErgoDatastore(PROGRAM_CUSTOMIZED_PULS, mySampleRate);
                myProgram.setAttribute(ErgoLineGraph.PULS);*/
                isDrawing = true;
            }
            else if(program.equals(PROGRAM_CUSTOMIZED_RPM))
            {
            	ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
            	if(myUseDistance)
            		config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
            	else
            		config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_RPM, ErgoTools.MIN_RPM, ErgoTools.MAX_RPM, ErgoTools.MIN_RPM_STEP, null, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                if(!config.wasCanceled())
	                {
	                    int rpm = ((Double)config.getValue(PROGRAM_RPM)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_RPM, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.RPM);
	                    
	                    for(double i=0; i <= distance; i+=0.01)
	                        myErgoDatastore.add(new ErgoData(null, 0, rpm, 0, i, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/0.01)*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.RPM);
	                }
	                else
	                    myErgoDatastore = null;                	
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && !config.wasCanceled())
	                {
	                    int rpm = ((Double)config.getValue(PROGRAM_RPM)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_RPM, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.RPM);
	                    
	                    for(long i=0; i <= dur; i++)
	                        myErgoDatastore.add(new ErgoData(null, 0, rpm, 0, 0, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.RPM);
	                }
	                else
	                    myErgoDatastore = null;
                }
                
                /*myErgoDatastore = new ErgoDatastore(PROGRAM_CUSTOMIZED_RPM, mySampleRate);
                myProgram.setAttribute(ErgoLineGraph.RPM);*/
                isDrawing = true;
            }
            else if(program.equals(PROGRAM_CUSTOMIZED_SPEED))
            {
            	ErgoControlProgramConfig config = new ErgoControlProgramConfig((JFrame)this.getOwner());
            	if(myUseDistance)
            		config.add(PROGRAM_LABEL_DISTANCE, 0.1, 999, 0.1, null, null);
            	else
            		config.add(PROGRAM_LABEL_DURATION, -3600000, -3600000, 0, null, null);
                config.add(PROGRAM_SPEED, ErgoTools.MIN_SPEED, ErgoTools.MAX_SPEED, ErgoTools.MIN_SPEED_STEP, null, null);
                config.showDialog();
                
                if(myUseDistance)
                {
                	double distance = ((Double)config.getValue(PROGRAM_LABEL_DISTANCE)).doubleValue();
	                if(!config.wasCanceled())
	                {
	                    int speed = ((Double)config.getValue(PROGRAM_SPEED)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_SPEED, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.SPEED);
	                    
	                    for(double i=0; i <= distance; i+=0.01)
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, speed, i, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date((int)(i/0.01)*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.SPEED);
	                }
	                else
	                    myErgoDatastore = null;                	
                }
                else
                {
	                long dur = (((Date)config.getValue(PROGRAM_LABEL_DURATION)).getTime()+3600000)/mySampleRate;
	                if(dur > 0 && !config.wasCanceled())
	                {
	                    int speed = ((Double)config.getValue(PROGRAM_SPEED)).intValue();
	
	                    myErgoDatastore =  new ErgoDatastore(PROGRAM_CUSTOMIZED_SPEED, mySampleRate);
	                    myErgoDatastore.setUseDistance(myUseDistance);
	                    myErgoDatastore.setRefDataIndex(ErgoDatastore.SPEED);
	                    
	                    for(long i=0; i <= dur; i++)
	                        myErgoDatastore.add(new ErgoData(null, 0, 0, speed, 0, 0, 0, 0, 0, 0, (new SimpleDateFormat(DEFAULT_TIME_FORMAT)).format((new Date(i*mySampleRate - 3600000))), false));
	
	                    myProgram.setAttribute(ErgoLineGraph.SPEED);
	                }
	                else
	                    myErgoDatastore = null;
                }
                /*
                myErgoDatastore = new ErgoDatastore(PROGRAM_CUSTOMIZED_SPEED, mySampleRate);
                myProgram.setAttribute(ErgoLineGraph.SPEED);*/
                isDrawing = true;
            }
            myProgram.setDatastore(myErgoDatastore, !isDrawing, isDrawing);

            if(myErgoDatastore != null)
            {
                myProgram.setPreferredSize(new Dimension(myErgoDatastore.size(), myProgram.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
                myProgram.setSize(new Dimension(myErgoDatastore.size(), myProgram.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
            }
            else
            {
                myProgram.setPreferredSize(new Dimension(0, myProgram.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
                myProgram.setSize(new Dimension(0, myProgram.getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
            }

            myProgramScroll.getHorizontalScrollBar().setValue(0);

            init();
        }
    }

    public void dataAvailable(ErgoLineGraphEvent evt, ErgoData data)
    {
    	if(myUseDistance)
    	{
    		DecimalFormat decFormat = new DecimalFormat("0.00");
    		myTime.setText(decFormat.format(data.getDistance()));
    	}
    	else
    		myTime.setText(data.getTime());
        if(myProgram.getAttribute(ErgoLineGraph.POWER))
            myValue.setText(Integer.toString(data.getPower()));
        if(myProgram.getAttribute(ErgoLineGraph.PULS))
            myValue.setText(Integer.toString(data.getPuls()));
        if(myProgram.getAttribute(ErgoLineGraph.RPM))
            myValue.setText(Integer.toString(data.getRPM()));
        if(myProgram.getAttribute(ErgoLineGraph.SPEED))
            myValue.setText(Double.toString(data.getSpeed()));
        repaint();
    }

    private void abort()
    {
        myErgoDatastore = null;
        this.setVisible(false);
    }

    private void confirm()
    {
    	this.setVisible(false);
    }
}
