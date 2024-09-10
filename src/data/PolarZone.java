package data;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 23.02.2005
 * Time: 11:47:38
 * To change this template use File | Settings | File Templates.
 */
public class PolarZone {
    private String myShortName = null;
    private String myName = null;
    private int myMinValue = 0;
    private int myMaxValue = 0;

    public PolarZone(String newShortName, String newName, int newMinValue, int newMaxValue)
    {
        myShortName = newShortName;
        myName = newName;
        myMinValue = newMinValue;
        myMaxValue = newMaxValue;
    }

    public String getShortName()
    {
        return myShortName;
    }

    public String getName()
    {
        return myName;
    }

    public int getMinValue()
    {
        return myMinValue;
    }

    public int getMaxValue()
    {
        return myMaxValue;
    }
}
