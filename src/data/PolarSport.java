package data;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 01.03.2005
 * Time: 11:18:26
 * To change this template use File | Settings | File Templates.
 */
public class PolarSport {
    private String myShortName = null;
    private String myName = null;
    private int myNumber = -1;

    public PolarSport(int newNumber, String newShortName, String newName)
    {
        myNumber = newNumber;
        myShortName = newShortName;
        myName = newName;
    }

    public int getNumber()
    {
        return myNumber;
    }

    public String getShortName()
    {
        return myShortName;
    }

    public String getName()
    {
        return myName;
    }
}
