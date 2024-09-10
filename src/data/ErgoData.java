package data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 17.02.2005
 * Time: 17:26:51
 * To change this template use File | Settings | File Templates.
 */
public class ErgoData implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private int myPuls;
    private int myRPM;
    private double mySpeed;
    private double myDistance;
    private int myPower;
    private int myKilojoule;
    private String myTime;
    private boolean myIsRecovery;
    private double myHeight;
    private double myOrigHeight;
    private double myGrade;
    private double myBearing;
    private GPSPosition myPosition = null;
    private boolean mySimulation = false;
    
    public ErgoData()
    {
    	myPuls = 0;
        myRPM = 0;
        mySpeed = 0;
        myDistance = 0;
        myPower = 0;
        myKilojoule = 0;
        myTime = null;
        myIsRecovery = false;
        myHeight = 0;
        myOrigHeight = 0;
        myGrade = 0;
        myBearing = 0;
        myPosition = null;
        mySimulation = false;
    }
    
    public ErgoData(GPSPosition newPosition, int newPuls, int newRPM, double newSpeed, double newDistance, int newPower, int newKilojoule, double newHeight, double newGrade, double newBearing, String newTime, boolean isRecovery)
    {
    	init(newPosition, newPuls, newRPM, newSpeed, newDistance, newPower, newKilojoule, newHeight, newGrade, newBearing, newTime, isRecovery, false);
    }
    
    public ErgoData(GPSPosition newPosition, int newPuls, int newRPM, double newSpeed, double newDistance, int newPower, int newKilojoule, double newHeight, double newGrade, double newBearing, String newTime, boolean isRecovery, boolean newSimulation)
    {
    	init(newPosition, newPuls, newRPM, newSpeed, newDistance, newPower, newKilojoule, newHeight, newGrade, newBearing, newTime, isRecovery, newSimulation);
    }
    
    private void init(GPSPosition newPosition, int newPuls, int newRPM, double newSpeed, double newDistance, int newPower, int newKilojoule, double newHeight, double newGrade, double newBearing, String newTime, boolean isRecovery, boolean newSimulation)
    {
        myPuls = newPuls;
        myRPM = newRPM;
        mySpeed = newSpeed;
        myDistance = newDistance;
        myPower = newPower;
        myKilojoule = newKilojoule;
        myTime = newTime;
        myIsRecovery = isRecovery;
        myHeight = newHeight;
        myOrigHeight = newHeight;
        myGrade = newGrade;
        myBearing = newBearing;
        myPosition = newPosition;
        mySimulation = newSimulation;
    }
    
    public int getPuls()
    {
        return myPuls;
    }

    public int getRPM()
    {
        return myRPM;
    }

    public double getSpeed()
    {
        return mySpeed;
    }

    public double getDistance()
    {
        return myDistance;
    }

    public int getPower()
    {
        return myPower;
    }

    public int getKilojoule()
    {
        return myKilojoule;
    }
    
    public double getHeight()
    {
        return myHeight;
    }
    
    public double getOrigHeight()
    {
        return myOrigHeight;
    }
    
    public double getGrade()
    {
    	return myGrade;
    }
    
    public double getBearing()
    {
        return myBearing;
    }

    public String getTime()
    {
        return myTime;
    }

    public boolean isRecovery()
    {
        return myIsRecovery;
    }
    
    public GPSPosition getPosition()
    {
    	return myPosition;
    }
    
    public void setGrade(double inGrade)
    {
    	myGrade = inGrade;
    }
    
    public void setHeight(double inHeight)
    {
    	myHeight = inHeight;
    }
    
    public boolean isSimulation()
    {
    	return mySimulation;
    }
}
