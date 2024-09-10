package tools;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import data.ErgoBikeDefinition;
import data.ErgoUserData;


public class ErgoBikeAdministration 
{
	Properties myProperties = null;
    Hashtable<String,ErgoBikeDefinition> myBikes = new Hashtable<String,ErgoBikeDefinition>();

    public ErgoBikeAdministration(ErgoUserData user, Properties newProperties)
    {
        myProperties = newProperties;

        String prefix = "";
		
		if(user != null)
			prefix = user.getUserName() + "_";
		
        String bikes = myProperties.getProperty(prefix + "Bikes");
                
        if(bikes != null)
        {
            String nextBike = null;
            int pos = 0;
            do
            {
            	nextBike = ErgoTools.subCharGet(bikes, pos, ';');
                if(nextBike != null && nextBike.length() > 0)
                {
                	nextBike = nextBike.trim();
                	ErgoBikeDefinition data = new ErgoBikeDefinition(nextBike, user, newProperties);

                    if(data != null)
                        myBikes.put(nextBike, data);
                }
                pos++;
            }
            while(nextBike != null && nextBike.length() > 0);
        }
    }

    public int size()
    {
        return myBikes.size();
    }

    public Enumeration elements()
    {
        return myBikes.elements();
    }

    public ErgoBikeDefinition get(String bikeName)
    {
        return (ErgoBikeDefinition)myBikes.get(bikeName);
    }
}
