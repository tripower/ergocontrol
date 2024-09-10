package tools;


import java.util.Properties;
import java.util.Enumeration;
import java.util.Hashtable;

import data.ErgoUserData;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 21.02.2005
 * Time: 13:25:32
 * To change this template use File | Settings | File Templates.
 */
public class ErgoUserAdministration {
    Properties myProperties = null;
    Hashtable<String,ErgoUserData> myUsers = new Hashtable<String,ErgoUserData>();

    public ErgoUserAdministration(Properties newProperties)
    {
        myProperties = newProperties;
        String users = myProperties.getProperty("Users");

        if(users != null)
        {
            String nextUser = null;
            int pos = 0;
            do
            {
                nextUser = ErgoTools.subCharGet(users, pos, ';');
                if(nextUser != null && nextUser.length() > 0)
                {
                    nextUser = nextUser.trim();
                    ErgoUserData data = new ErgoUserData(nextUser, null, null, null, null, myProperties);

                    if(data != null)
                        myUsers.put(nextUser, data);
                }
                pos++;
            }
            while(nextUser != null && nextUser.length() > 0);
        }
    }

    public int size()
    {
        return myUsers.size();
    }

    public Enumeration elements()
    {
        return myUsers.elements();
    }

    public ErgoUserData get(String userName)
    {
        return (ErgoUserData)myUsers.get(userName);
    }
}
