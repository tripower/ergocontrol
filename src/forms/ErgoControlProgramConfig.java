package forms;


import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import data.ErgoControlProgramConfigEntry;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 07.03.2005
 * Time: 08:40:14
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlProgramConfig extends JDialog implements WindowListener, ActionListener, ChangeListener {
	static final long serialVersionUID = 0;
	
    private final static String DIALOGSTRING = "ErgoControl Program Configuration";
    private final static String BUTTON_CONFIRM = "Ok";
    private final static String BUTTON_ABORT = "Cancel";

    private final static int WINDOW_WIDTH = 200;
    private final static int WINDOW_HEIGHT = 25;

    private JButton myConfirm = new JButton(BUTTON_CONFIRM);
    private JButton myAbort = new JButton(BUTTON_ABORT);

    private boolean myCanceled = false;
    private  boolean myInitialized = false;

    private Vector<String> components = new Vector<String>();
    private Hashtable<String,ErgoControlProgramConfigEntry> componentObjectsByName = new Hashtable<String,ErgoControlProgramConfigEntry>();
    private Hashtable<Component,ErgoControlProgramConfigEntry> componentObjectsByComponent = new Hashtable<Component,ErgoControlProgramConfigEntry>();

    public ErgoControlProgramConfig(JFrame owner)
    {
        super(owner, DIALOGSTRING, true);

        setResizable(false);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        myConfirm.addActionListener(this);
        myAbort.addActionListener(this);

        addWindowListener(this);

        init();
    }

    public void init()
    {
        int size = components.size() + 1;

        myCanceled = false;

        getContentPane().setLayout(new GridLayout(size,2));
        setSize(WINDOW_WIDTH,WINDOW_HEIGHT * (size+1));

        getContentPane().removeAll();

        Enumeration compEnum = components.elements();
        while(compEnum.hasMoreElements())
        {
            String componentName = (String)compEnum.nextElement();
            ErgoControlProgramConfigEntry entry = (ErgoControlProgramConfigEntry)componentObjectsByName.get(componentName);

            getContentPane().add(entry.getLabel());
            getContentPane().add(entry.getComponent());

            initDependency((JComponent)entry.getComponent());
        }

        getContentPane().add(myConfirm);
        getContentPane().add(myAbort);

        myInitialized = true;
    }

    public void showDialog()
    {
        if(!myInitialized)
            init();

        super.setVisible(true);
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

    public void stateChanged(ChangeEvent evt)
    {
        if(evt.getSource().getClass().equals(JSpinner.class))
            initDependency((JComponent)evt.getSource());
    }

    public void initDependency(JComponent comp)
    {
        ErgoControlProgramConfigEntry entry = (ErgoControlProgramConfigEntry)componentObjectsByComponent.get((JSpinner)comp);
        String dep = entry.getMinDependency();

        if(dep != null)
        {
            ErgoControlProgramConfigEntry entry2 = (ErgoControlProgramConfigEntry)componentObjectsByName.get(dep);

            entry2.setMaxVal(entry);
        }

        dep = entry.getMaxDependency();
        if(dep != null)
        {
            ErgoControlProgramConfigEntry entry2 = (ErgoControlProgramConfigEntry)componentObjectsByName.get(dep);

            entry2.setMinVal(entry);
        }
    }


    public ErgoControlProgramConfigEntry add(String labelText, double minVal, double maxVal, double step, String minDep, String maxDep)
    {
        JLabel label = new JLabel(labelText);
        Component comp = null;
        ErgoControlProgramConfigEntry entry = null;

        components.add(labelText);
        entry = new ErgoControlProgramConfigEntry(label, minVal, maxVal, step, minDep, maxDep);
        comp = entry.getComponent();

        componentObjectsByName.put(labelText, entry);
        componentObjectsByComponent.put(comp, entry);

        ((JSpinner)comp).addChangeListener(this);
        myInitialized = false;

        return entry;
    }

    public Object getValue(String labelText)
    {
        ErgoControlProgramConfigEntry entry = (ErgoControlProgramConfigEntry)componentObjectsByName.get(labelText);
        return ((JSpinner)entry.getComponent()).getValue();
    }

    private void abort()
    {
        myCanceled = true;
        this.setVisible(false);
    }

    private void confirm()
    {
    	this.setVisible(false);
    }

    public boolean wasCanceled()
    {
        return myCanceled;
    }
}
