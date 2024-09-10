package tools;

import data.ErgoBikeDefinition;

public class ErgoPhysics 
{
	private ErgoBikeDefinition myErgoBike = null;
	
	private double cCad = 0.002;
	
	private double myVelocity = 0;
	private double myDistance = 0;
	private int myPower = 0;
	private long myLastTime = 0;
//	private double myLastVelocity = 0;
	
	private boolean myCalcSpeed = false;
	
	public ErgoPhysics(ErgoBikeDefinition newErgoBike, boolean inCalcSpeedFromRPM)
    {
		if(newErgoBike != null)
			myErgoBike = newErgoBike;
		else
			myErgoBike = new ErgoBikeDefinition();
    	myCalcSpeed = inCalcSpeedFromRPM;
    }
	
	public void calc(long time, int P, double grade, int rpm)
	{
		if(myCalcSpeed)
		{
			myVelocity = myErgoBike.getSpeed(rpm);
			calc(time, 0, myVelocity, rpm, 0, 0, 0, grade);
		}
		else
			calc(time, P, 0, rpm, 0, 0, 0, grade);
	}
	
	private void calc(long time, double inPower, double inVelocity, double inCadence, double inWindSpeed, double inTemp, double inHeight, double inGrade)
	{
		double hRider = myErgoBike.getRiderHeight();
		double M = myErgoBike.getRiderWeight();
		double mBik = myErgoBike.getBikeWeight();
		double T = inTemp;
		double Hn = inHeight;
		double stg = Math.toRadians(inGrade);
		double W = inWindSpeed * 0.27778;
		double P = inPower;
		double V = inVelocity * 0.27778;
		double cad = inCadence;
		double prefCad = cad;
		
		hRider /= ((hRider<10) ? 1 : ((10<=hRider&&hRider<400)? 100 : 1000));
		cad = Math.abs(cad);
		//if(cad > 10) prefCad = cad;
		
		double CrEff = myErgoBike.getCrEff();
		
		double adipos = Math.sqrt(M/(hRider*750));
		double CwaBike = myErgoBike.getCwaBike();
		double Cstg = 9.81 * (mBik + M) * (CrEff * Math.cos(stg) + Math.sin(stg));
		
		if(inVelocity != 0)
		{
			double y = 7979;
		    while (y == 7979 || Math.round(y) <= 0 && cad > 0 || Math.round(y) > 0 && cad == 0)
		    {
		    	if (y != 7979 && Math.round(y) <= 0 && cad > 0)
		        	cad = 0;
		        if (y != 7979 && Math.round(y) > 0 && cad == 0)
		        	cad = prefCad;

		        double CwaRider = (1 + cad * cCad) * myErgoBike.getCw() * adipos * (((hRider - adipos) * myErgoBike.getSin()) + adipos);
		        double Kw = 176.5 * Math.exp(-Hn * .0001253) * (CwaRider + CwaBike) / (273 + T);
		        double vw=V+W;
		        y = (myErgoBike.getKs() * V * (Kw * (vw * ((vw<0)? -vw : vw)) + Cstg));		        
		    }
		    myPower = ((int)y/5)*5;
		    if(myPower < 25)
		    	myPower = 25;
		    if(myPower > 600)
		    	myPower = 600;
		}
		else if(inPower != 0) 
		{
			if (P > 0 && cad == 0)
				cad = prefCad;
		    if (P <= 0 && cad > 0)
		    	cad = 0;
		    double CwaRider = (1 + cad * cCad) * myErgoBike.getCw() * adipos * (((hRider - adipos) * myErgoBike.getSin()) + adipos);
		    double Kw = 176.5 * Math.exp(-Hn * .0001253) * (CwaRider + CwaBike) / (273 + T);
		    double expA = W*W/9-Cstg/(3*Kw);
		    double expB=W*W*W/27+W*Cstg/(3*Kw)+P/(2*Kw*myErgoBike.getKs());
		    double iwurz=expB*expB-expA*expA*expA;
		    double ire=expB-Math.sqrt(iwurz);
		    double Vms = 0;
		    if (iwurz >= 0) 
		    	Vms = Math.pow(expB+Math.sqrt(iwurz),1/3.0)+((ire<0)?-Math.pow(-ire,1/3.0):Math.pow(ire,1/3.0));
		    else 
		    	Vms = 2*Math.sqrt(expA)*Math.cos(Math.acos(expB/Math.sqrt(Math.pow(expA,3.0)))/3.0);
		    
		    myVelocity = 3.6*(Vms-2*W/3.0);
		    myPower = (((int)P)/5)*5;
		    //myVelocity = myLastVelocity + myVelocity / 12960000.0;
		}
		else 
			myVelocity = 0;
		
		myDistance += (myVelocity /  3600.0) * ((time - myLastTime) / 1000.0); 
		
		myLastTime = time;
//		myLastVelocity = myVelocity;
	}
	
	public int getPower()
	{
		return myPower;
	}
	
	public double getDistance()
	{
		return myDistance;
	}
	
	public double getVelocity()
	{
		return myVelocity;
	}
	
	public ErgoBikeDefinition getBike()
	{
		return myErgoBike;
	}
}
