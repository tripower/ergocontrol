package data;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 23.02.2005
 * Time: 11:40:55
 * To change this template use File | Settings | File Templates.
 */
public class PolarZones {
    private String myName = null;
    private Vector<PolarZone> myZones = new Vector<PolarZone>();

    public PolarZones(String newName)
    {
        myName = newName;
    }

    public String getName()
    {
        return myName;
    }

    public Enumeration elements()
    {
        return myZones.elements();
    }

    public int size()
    {
        return myZones.size();
    }

    public void add(PolarZone zone)
    {
        myZones.add(zone);
    }
    
    public PolarZone get(int index)
    {
        return (PolarZone)myZones.get(index);
    }

    public int getIndex(int puls)
    {
        Enumeration zoneEnum = myZones.elements();
        int index = 0;

        while(zoneEnum.hasMoreElements())
        {
            PolarZone zone = (PolarZone)zoneEnum.nextElement();

            if(puls >= zone.getMinValue() && puls <= zone.getMaxValue())
                return index;
            else
                index++;
        }

        return 0;
    }
}
