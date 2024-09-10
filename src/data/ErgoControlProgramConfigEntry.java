package data;

import javax.swing.*;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 14.03.2005
 * Time: 14:10:18
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlProgramConfigEntry {
    private JLabel label = null;
    private JComponent component = null;
    private String minDependency = null;
    private String maxDependency = null;
    private double minVal = 0;
    private double maxVal = 0;
    private double step = 0;

    public ErgoControlProgramConfigEntry(JLabel newLabel, double newMinVal, double newMaxVal, double newStep, String newMinDependency, String newMaxDependency)
    {
        label = newLabel;
        minVal = newMinVal;
        maxVal = newMaxVal;
        step = newStep;
        minDependency = newMinDependency;
        maxDependency = newMaxDependency;

        if(step == 0)
        {
            Date d = null;
            if(maxVal > minVal)
                d = new Date((int)maxVal);
            component = new JSpinner(new SpinnerDateModel(new Date((int)minVal), new Date((int)minVal), d, Calendar.SECOND));
            ((JSpinner)component).setEditor(new JSpinner.DateEditor((JSpinner)component, "HH:mm:ss"));
        }
        else
            component = new JSpinner(new SpinnerNumberModel(minVal, minVal, maxVal, step));
    }

    public JLabel getLabel()
    {
        return label;
    }

    public JComponent getComponent()
    {
        return component;
    }

    public String getMinDependency()
    {
        return minDependency;
    }

    public String getMaxDependency()
    {
        return maxDependency;
    }

    public void setMaxVal(ErgoControlProgramConfigEntry newMaxVal)
    {
        Object newMax = ((JSpinner)newMaxVal.getComponent()).getValue();
        Object value = ((JSpinner)component).getValue();

        if(step == 0)
        {
            component = new JSpinner(new SpinnerDateModel((Date)value, new Date((int)minVal), (Date)newMax, Calendar.SECOND));
        }
        else
        {
            ((JSpinner)component).setModel(new SpinnerNumberModel(((Double)value).doubleValue(), (double)minVal, ((Double)newMax).doubleValue(), step));
        }
    }

    public void setMinVal(ErgoControlProgramConfigEntry newMinVal)
    {
        Object newMin = ((JSpinner)newMinVal.getComponent()).getValue();
        Object value = ((JSpinner)component).getValue();

        if(step == 0)
        {
            component = new JSpinner(new SpinnerDateModel((Date)value, (Date)newMin, new Date((int)maxVal), Calendar.SECOND));
        }
        else
        {
        	((JSpinner)component).setModel(new SpinnerNumberModel(((Double)value).doubleValue(), ((Double)newMin).doubleValue(), (double)maxVal, step));
        }
    }
}
