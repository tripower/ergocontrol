package data;

import java.util.ArrayList;
import java.util.Properties;

public class ErgoBikeDefinition 
{
	private String myName = "New Bikeset";
	
//	private int myRing = 0;
	private int myGear = 0;
	
	private ArrayList<Double> gears = new ArrayList<Double>();
//	private ArrayList chainRings = new ArrayList();	
	
	private double riderHeight = 1.8;
	private double riderWeight = 60;

	private int transI = 0;
	private int bikeI = 5;
	private int vrI = i_tireF[bikeI];
	private int hrI = i_tireR[bikeI];
	private double bikeWeight = def_mr[bikeI];
	
	private double CrV = Cr[vrI];
	private double ATireV = ATire[vrI];
	private double CrH = Cr[hrI];
	private double ATireH = ATire[hrI];
	private int circumference = Circ[bikeI]; //in mm
	
	private static String transArr[] = { "rohloff", "xtr (11-32)", "xtr (12-34)",  "dura ace (11-21)", "dura ace (12-27)", "time trial" };
	private static int smallestChainRing[] = { 24,   22,            22,             30,                 30,                 46};
	private static int smallestGear[] = {      34,   32,            34,             21,                 27,                 21};
	private static int greatestChaingRing[] = {44,   44,            44,             53,                 53,                 58};
	private static int greatestGear [] = {     12,   11,            12,             11,                 12,                 11};
	
	private static String tireArr[] = { "narrow racing tire (high pressure)", "medium-wide high pressure slick", "wide high pressure slick", "robust wide touring tire (thread)", "Rinkowsky radial ply tire (wide slick)", "offroad tire 1.75''", "low pressure offroad tire 2.1''" };
	private static double Cr[] = { .006, 0.0055, 0.005, 0.0075, 0.003, 0.007, 0.008 };
	private static double ATire[] = { .021, 0.031, 0.042, 0.048, 0.042, 0.055, 0.065 };
	
	private static String bikeArr[] = { "roadster", "mtb", "tandem", "racetops", "racedrops", "tria", "superman", "lwbuss", "swbuss", "swbass", "ko4", "ko4tailbox", "whitehawk", "questclosed", "handtrike" };
	private static double Cw[]      = {  0.9,        0.75,  0.33,     0.78,       0.57,        0.505,  0.45,       0.79,     0.63,     0.55,     0.48,  0.405,        0,           0,             0.59 };
	private static double sin[]     = {  0.95,       0.85,  0.7,      0.89,       0.67,        0.64,   0.55,       0.64,     0.51,     0.44,     0.37,  0.37,         0,           0,             0.55 };
	private static double CwBike[]  = {  1.6,        1.23,  1.4,      1.2,        1.2,         1,      0.75,       1.4,      1.3,      1 ,       0.8,   0.7,          0.03,        0.066,         1.2 };
	private static double aFrame[]  = {  0.06,       0.052, 0.06,     0.048,      0.048,       0.048,  0.044,      0.039,    0.036,    0.031,    0.023, 0.026,        1,           1,             0.046 };
	private static double CATireV[] = {  1.1,        1.1,   1.1,      1.1,        1.1,         1.1,    0.9,        0.66,     0.8,      0.85,     0.77,  0.77,         0.1,         0.26,          0.9 };
	private static double CATireH[] = {  0.9,        0.9,   0.9,      0.9,        0.9,         0.7,    0.7,        0.9,      0.80,     0.84,     0.49,  0.3,          0.13,        0.16,          2 };
	private static double FV[]      = {  0.33,       0.45,  0.5,      0.37,       0.45,        0.47,   0.48,       0.34,     0.65,     0.65,     0.78,  0.78,         0.715,       0.88,          0.5 };
	private static double FH[]      = {  0.67,       0.55,  0.5,      0.63,       0.55,        0.53,   0.52,       0.72,     0.5,      0.5,      0.4,   0.4,          0.45,        0.4,           0.55 };
	private static double ks[]      = {  1.04,       1.035, 1.06,     1.03,       1.03,        1.03,   1.03,       1.05,     1.05,     1.05,     1.06,  1.06,         1.07,        1.09,          1.04 };
	private static double def_mr[]  = { 18,         12,    17.8,      6,          6,           8,      8,         18,       15.5,      11.5,    11.8,  13.5,         18,          32,            18 };
	private static int i_tireF[] = {     3,          5,     1,        0,          0,           0,      0,          1,        2,        0,        0,     0,            0,           0,             0 };
	private static int i_tireR[] = {     3,          5,     1,        0,          0,           0,      0,          3,        3,        0,        0,     0,            0,           0,             0 };
	private static int Circ[] = {     2120,       2120,  2070,     2070,       2070,        1950,   2070,       1470,     1470,     1470,     1470,  1470,         1470,        1470,          1470 };
	
    public static final String PROPERTY_RIDERWEIGHT = "RiderWeight";
    public static final String PROPERTY_RIDERHEIGHT = "RiderHeight";
    public static final String PROPERTY_BIKETYPE = "BikeType";
    public static final String PROPERTY_TRANSTYPE = "TransmissionType";
    public static final String PROPERTY_BIKEFT = "BikeFT";
    public static final String PROPERTY_BIKERT = "BikeRT";
    public static final String PROPERTY_BIKEWEIGHT = "BikeWeight";
    public static final String PROPERTY_BIKECIRCUMFERENCE = "BikeCircumference";
	
	public ErgoBikeDefinition()
	{
		calcGears();
		/*gears.add(new Integer(21));
		gears.add(new Integer(19));
		gears.add(new Integer(17));
		gears.add(new Integer(16));
		gears.add(new Integer(15));
		gears.add(new Integer(14));
		gears.add(new Integer(13));
		gears.add(new Integer(12));
		gears.add(new Integer(11));		
		
		chainRings.add(new Integer(44));
		chainRings.add(new Integer(54));*/;
	}
		
	public ErgoBikeDefinition(String name, ErgoUserData user, Properties properties)
	{
		String prefix = "";
		
		if(user != null)
			prefix = user.getUserName() + "_";
		
		prefix += name + "_";
		
		myName = name;
		riderWeight = Double.parseDouble(properties.getProperty(prefix + PROPERTY_RIDERWEIGHT));
		riderHeight = Double.parseDouble(properties.getProperty(prefix + PROPERTY_RIDERHEIGHT));
		bikeWeight = Double.parseDouble(properties.getProperty(prefix + PROPERTY_BIKEWEIGHT));			
		
		bikeI = Integer.parseInt(properties.getProperty(prefix + PROPERTY_BIKETYPE));
		transI = Integer.parseInt(properties.getProperty(prefix + PROPERTY_TRANSTYPE));
		vrI = Integer.parseInt(properties.getProperty(prefix + PROPERTY_BIKEFT));
		hrI = Integer.parseInt(properties.getProperty(prefix + PROPERTY_BIKERT));
		circumference = Integer.parseInt(properties.getProperty(prefix + PROPERTY_BIKECIRCUMFERENCE));
		
		calcGears();
	}
	
	private void calcGears()
	{
		double startGear = smallestChainRing[transI]/(double)smallestGear[transI];
		double endGear = greatestChaingRing[transI]/(double)greatestGear[transI];
		double step = (endGear - startGear) / 14.0;
		
		for(int i=0; i < 14; i++)
		{
			gears.add(new Double(startGear));
			startGear += step; 
		}
	}
	
	public static String[] getBikeTypes()
	{
		return bikeArr;
	}
	
	public static String[] getTransmissionTypes()
	{
		return transArr;
	}	
	
	public static String[] getTireTypes()
	{
		return tireArr;
	}
	
	public int getBikeType()
	{
		return bikeI;
	}
	
	public void setBikeType(int index)
	{
		bikeI = index;
	}
	
	public int getTransmissionType()
	{
		return transI;
	}
	
	public void setTransmissionType(int index)
	{
		transI = index;
	}
	
	public int getRearTireType()
	{
		return hrI;
	}
	
	public void setRearTireType(int index)
	{
		hrI = index;
	}
	
	public int getFrontTireType()
	{
		return vrI;
	}
	
	public void setFrontTireType(int index)
	{
		vrI = index;
	}
		
	public int getCircumference()
	{
		return circumference;
	}
	
	public void setCircumference(int c)
	{
		circumference = c;
	}
	
	public int getGear()
	{
		//return ((Integer)gears.get(myGear)).integerValue();
		return myGear + 1;
	}
	
	private double getGear(int gear)
	{
		return ((Double)gears.get(gear)).doubleValue();
	}
	
/*	public int getRing()
	{
		return ((Integer)chainRings.get(myRing)).intValue();
	}
	
	private double getChainRing(int ring)
	{
		return ((Integer)chainRings.get(ring)).doubleValue();
	}*/
	
	public double getRiderHeight()
	{
		return riderHeight;
	}
	
	public void setRiderHeight(double val)
	{
		riderHeight = val;
	}
	
	public double getRiderWeight()
	{
		return riderWeight;
	}
	
	public void setRiderWeight(double val)
	{
		riderWeight = val;
	}
	
	public double getBikeWeight()
	{
		return bikeWeight;
	}
	
	public void setBikeWeight(double val)
	{
		bikeWeight = val;
	}
	
	public double getCrEff()
	{
		return FV[bikeI] * CrV + FH[bikeI] * CrH;
	}
	
	public double getCwaBike()
	{
		return CwBike[bikeI] * (CATireV[bikeI] * ATireV + CATireH[bikeI] * ATireH + aFrame[bikeI]);
	}
	
	public double getCw()
	{
		return Cw[bikeI];
	}
	
	public double getSin()
	{
		return sin[bikeI];
	}
	
	public double getKs()
	{
		return ks[bikeI];
	}
	
	public double getSpeed(int rpm)
	{
		//double mmPerMinute = getChainRing(myRing)/getGear(myGear) * circumference * rpm;
		double mmPerMinute = getGear(myGear) * circumference * rpm;
		return mmPerMinute*60 / 1000000;
	}
	
	public void shiftGearUp()
	{
		if(myGear + 1 < gears.size())
			myGear++;
	}
	
	public void shiftGearDown()
	{
		if(myGear - 1 >= 0)
			myGear--;
	}
	
/*	public void shiftRingUp()
	{
		if(myRing + 1 < chainRings.size())
			myRing++;
	}
	
	public void shiftRingDown()
	{
		if(myRing - 1 >= 0)
			myRing--;
	}
*/	
	public void save(ErgoUserData user, Properties properties)
    {
		boolean bAlreadyContained = false;
		String prefix = "";
		
		if(user != null)
			prefix = user.getUserName() + "_";
		
		String bikes = properties.getProperty(prefix + "Bikes");
        
        if(bikes != null)
        {
            if(bikes.indexOf(myName + ";") != -1)
                bAlreadyContained = true;
        }
        else bikes = "";

        if(!bAlreadyContained)
        {
            bikes += myName + ";";
            properties.setProperty(prefix + "Bikes", bikes);
        }
        
        prefix += myName + "_";

        properties.setProperty(prefix + PROPERTY_RIDERWEIGHT, Double.toString(riderWeight));
        properties.setProperty(prefix + PROPERTY_RIDERHEIGHT, Double.toString(riderHeight));
        properties.setProperty(prefix + PROPERTY_BIKEWEIGHT, Double.toString(bikeWeight));
        
        properties.setProperty(prefix + PROPERTY_BIKETYPE, Integer.toString(bikeI));
        properties.setProperty(prefix + PROPERTY_TRANSTYPE, Integer.toString(transI));
		properties.setProperty(prefix + PROPERTY_BIKEFT, Integer.toString(vrI));
		properties.setProperty(prefix + PROPERTY_BIKERT, Integer.toString(hrI));
		properties.setProperty(prefix + PROPERTY_BIKECIRCUMFERENCE, Integer.toString(circumference));
    }
	
	public void remove(ErgoUserData user, Properties properties)
    {
		String prefix = "";
		
		if(user != null)
			prefix = user.getUserName() + "_";
		
		String bikes = properties.getProperty(prefix + "Bikes");
        String newBikes = null;
        
        if(bikes != null)
        {
            int pos = bikes.indexOf(myName + ";");
            if(pos != -1)
            {
                if(pos != 0)
                	newBikes = bikes.substring(0, pos);
                else
                	newBikes = "";
                if((pos + myName.length() + 1) < bikes.length())
                	newBikes += bikes.substring(pos + myName.length() + 1, bikes.length());
            }
            properties.setProperty(prefix + "Bikes", newBikes);
        }
        
        prefix += myName + "_";

        properties.remove(prefix + PROPERTY_RIDERWEIGHT);
        properties.remove(prefix + PROPERTY_RIDERHEIGHT);
        properties.remove(prefix + PROPERTY_BIKEWEIGHT);
        
        properties.remove(prefix + PROPERTY_BIKETYPE);
        properties.remove(prefix + PROPERTY_TRANSTYPE);
		properties.remove(prefix + PROPERTY_BIKEFT);
		properties.remove(prefix + PROPERTY_BIKERT);
		properties.remove(prefix + PROPERTY_BIKECIRCUMFERENCE);
    }
	
	public String getName()
	{
		return myName;
	}
	
	public void setName(String name)
	{
		myName = name;
	}
}
