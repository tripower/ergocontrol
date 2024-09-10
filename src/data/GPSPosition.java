package data;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class GPSPosition implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GPSPosition.class);
	
	private double myLon = 0;
	private double myLat = 0;
	
	private final static double equLength = 40075.004;
	private final static double polLength = 39940.638;
	
	private String myCode = null;
	private int myZoom = -1;
	
	public GPSPosition(GPSPosition newPos)
	{
		myLon = newPos.getLon();
		myLat = newPos.getLat();
		myCode = newPos.myCode;
		myZoom = newPos.myZoom;
	}
	
	public GPSPosition(double newLat, double newLon)
	{
		myLon = newLon;
		myLat = newLat;
		myCode = null;
		myZoom = -1;
	}
	
	public GPSPosition(String googleCode)
	{
		calcLatLonFromGoogleCode(googleCode); 
	}
	
	public double getLon()
	{
		return myLon;
	}
	
	public double getLat()
	{
		return myLat;
	}
	
	public int zoomFactor(double distance)
	{
		int zoom = 17;
		double checkDist = equLength / (180.0/(180-myLat));
		
		while(checkDist > distance && zoom >= 0)
		{
			checkDist /= 2;
			zoom--;			
		}
				
		return zoom;
	}
	
	public static double scaleFactor(int zoom)
	{
		return (256 * Math.pow(2, 17 - zoom)) / (256 * Math.pow(2, 17));
	}
	
	public int getXpixel(int zoom)
	{
		return getXpixel(myLon,zoom);
	}
	
	private int getXpixel(double lon, int zoom)
    {
        // Instead of -180 to +180, we want 0 to 360
        double dlng = lon + 180.0;

        // 256 = tile Width
        double dxpixel = dlng / 360.0 * 256 * Math.pow(2, 17 - zoom);
        int xpixel = (int)Math.floor(dxpixel);
        return xpixel;
    }

	public int getYpixel(int zoom)
	{
		return getYpixel(myLat, zoom);
	}
	
	private int getYpixel(double lat, int zoom)
    {
        // The 25 comes from 17 + (256=>2^8=>8) 17+8 = 25
        // ypixelcenter = the middle y pixel (the equator) at this zoom level 
        double ypixelcenter = Math.pow(2, 25 - zoom - 1);

        // PI/360 == degrees -> radians
        // The trig functions are done with radians
        double dypixel = ypixelcenter - Math.log(Math.tan(lat * Math.PI / 360 + Math.PI / 4)) * ypixelcenter / Math.PI;
        int ypixel = (int)(Math.floor(dypixel));
        return ypixel;
    }
    
    /*TODO get Street Maps
    private String tileURL(int zoom) 
    {
    	return "http://mt.google.com/mt?v=w2.5&x=" + getXpixel(zoom) + "&y=" + getYpixel(zoom) + "&zoom=" + zoom;
    }*/
    
    private String satURL(int zoom)
    {	
    	String url = "http://kh.google.com/kh?v=3&t=" + googleCode(zoom);
    	return url;
    }
    
    public String googleCode(int zoom)
    {
    	if(myCode == null || myZoom != zoom)
    	{
	    	int x = getXpixel(zoom);
	    	int y = getYpixel(zoom);
	    	
	    	int e = (int)(Math.pow(2, 17 - zoom))*256;
	    	String f = "t";
	        for (int h = 16; h >= zoom; h--) 
	        {
	        	e /= 2;
	            if (y < e) 
	            {
	                if (x < e) 
	                {
	                    f += "q";
	                } 
	                else 
	                {
	                    f += "r";
	                    x -= e;
	                }
	            } else 
	            {
	                if (x < e) 
	                {
	                    f += "t";
	                    y -= e;
	                } else {
	                    f += "s";
	                    x -= e;
	                    y -= e;
	                }
	            }
	        } 	
	        
	        myCode = f;
	        myZoom = zoom;
    	}
    	return myCode;
    }
    
    private void calcLatLonFromGoogleCode(String googleCode)
    {
    	int zoom = 18-googleCode.length();
		int zoomBuf = zoom;
		double x = 0;
		double y = 0;
		
		for (int h = googleCode.length() - 1; h > 1; h--) 
        {
			int e = (int)(Math.pow(2, zoomBuf++))*256;
        	char c = googleCode.charAt(h);
        	
        	switch(c)
        	{
        		case 'q':
        			x += e;
        			break;
        		case 'r':
        			x += e*2;
        			break;
        		case 't':
        			x += e;
        			y += e;
        			break;
        		case 's':
        			x += e*2;
        			y += e;
        			break;
        	}
        }
        
		x += 256 * Math.pow(2, zoom);
		zoom = 0;
		myLon = (x * 360.0 / 256 / Math.pow(2, 17 - zoom)) - 180;
		
        double ypixelcenter = Math.pow(2, 25 - zoom - 1);
        myLat = (Math.atan(Math.exp((ypixelcenter - y) / ypixelcenter * Math.PI)) - Math.PI / 4) / Math.PI * 360;
    }
    
    public void googleCorrection(int zoom)
    {
    	/*int xOld = getXpixel(myLon, zoom);
    	int yOld = getYpixel(myLat, zoom);*/
    	
    	calcLatLonFromGoogleCode(googleCode(zoom));
    	
    	/*int xNew = getXpixel(zoom);
    	int yNew = getYpixel(zoom);
    	
    	int size = (int)(256 * Math.pow(2, zoom)); */   	
    }
    
    public double getTileHeight(int zoom)
    {
    	return polLength * scaleFactor(zoom); 
    }
    
    public double getTileWidth(int zoom)
    {
    	return (equLength * scaleFactor(zoom)) / (180.0/(180-myLat)); 
    }

	public void move(double distance, double bearing)
	{
		if(distance != 0)
		{
			double er = 6366.707;
			double d = distance/er;  // d = angular distance covered on earth's surface
			double brng = Math.toRadians(bearing);
			double oldLat = Math.toRadians(myLat);
			double oldLon = Math.toRadians(myLon);
			double newLat = 0;
			double newLon = 0;
	
			newLat = oldLat + d*Math.cos(brng);
			double dPhi = Math.log(Math.tan(newLat/2+Math.PI/4)/Math.tan(oldLat/2+Math.PI/4));
			double q = (newLat-oldLat)/dPhi;
			if (Double.isNaN(q) || Double.isInfinite(q))
				q = Math.cos(oldLat);
	
			double dLon = d*Math.sin(brng)/q;
			// check for some daft bugger going past the pole
			if (Math.abs(newLat) > Math.PI/2) 
				newLat = newLat>0 ? Math.PI-newLat : -Math.PI-newLat;
			newLon = (oldLon+dLon+Math.PI)%(2*Math.PI) - Math.PI;
			 
			myLat = Math.toDegrees(newLat);
			myLon = Math.toDegrees(newLon);
			myCode = null;
			myZoom = -1;
		}
	}
	
	public void moveTileRight(int zoom)
	{
		String code = googleCode(zoom);
		
		code = shiftRight(code);
		
		calcLatLonFromGoogleCode(code);
		myCode = code;
	}
	
	public void moveTileDown(int zoom)
	{
		String code = googleCode(zoom);
		
		code = shiftDown(code);
		
		calcLatLonFromGoogleCode(code);
		myCode = code;
	}
	
	private String shiftRight(String code)
	{
		String newCode = "";
		
		if(code.length() > 0)
		{
			if(code.charAt(code.length()-1) == 'q')
				newCode = code.substring(0,code.length()-1) + 'r';
			if(code.charAt(code.length()-1) == 't')
				newCode = code.substring(0,code.length()-1) + 's';
			
			if(code.charAt(code.length()-1) == 'r')
				newCode = shiftRight(code.substring(0,code.length()-1)) + 'q';
			if(code.charAt(code.length()-1) == 's')
				newCode = shiftRight(code.substring(0,code.length()-1)) + 't';
		}
		
		return newCode;
	}
	
	private String shiftDown(String code)
	{
		String newCode = "";
		
		if(code.length() > 0)
		{
			if(code.charAt(code.length()-1) == 'q')
				newCode = code.substring(0,code.length()-1) + 't';
			if(code.charAt(code.length()-1) == 'r')
				newCode = code.substring(0,code.length()-1) + 's';
			
			if(code.charAt(code.length()-1) == 't')
				newCode = shiftDown(code.substring(0,code.length()-1)) + 'q';
			if(code.charAt(code.length()-1) == 's')
				newCode = shiftDown(code.substring(0,code.length()-1)) + 'r';
		}
		
		return newCode;
	}
	
	public double calcDistance(GPSPosition newPos) 
	{
		return calcDistance(newPos.myLat, newPos.myLon);
	}

    public double calcDistance(double newLat, double newLon) 
	{
		double er = 6366.707;
//		ave. radius = 6371.315 (someone said more accurate is 6366.707)
//		equatorial radius = 6378.388
//		nautical mile = 1.15078
		double radlat1 = Math.toRadians(myLat);//Math.PI * myLat;//(td[nq] + tm[nq]/60 + ts[nq]/3600)/180
		double radlat2 = Math.toRadians(newLat);//Math.PI * newLat;//(td[nj] + tm[nj]/60 + ts[nj]/3600)/180
//		now long.
		double radlong1 = Math.toRadians(myLon);//Math.PI * myLon;//(gd[nq] + gm[nq]/60 + gs[nq]/3600)/180
		double radlong2 = Math.toRadians(newLon);//Math.PI * newLon;//(gd[nj] + gm[nj]/60 + gs[nj]/3600)/180
//		spherical coordinates x=r*cos(ag)sin(at), y=r*sin(ag)*sin(at), z=r*cos(at)
//		zero ag is up so reverse lat
		if (myLat > 0) radlat1=Math.PI/2-radlat1;
		if (myLat < 0) radlat1=Math.PI/2+radlat1;
		if (myLon < 0) radlong1=Math.PI*2-radlong1;

		if (myLat > 0) radlat2=Math.PI/2-radlat2;
		if (myLat < 0) radlat2=Math.PI/2+radlat2;
		if (myLon < 0) radlong2=Math.PI*2-radlong2;

		double x1 = er * Math.cos(radlong1)*Math.sin(radlat1);
		double y1 = er * Math.sin(radlong1)*Math.sin(radlat1);
		double z1 = er * Math.cos(radlat1);

		double x2 = er * Math.cos(radlong2)*Math.sin(radlat2);
		double y2 = er * Math.sin(radlong2)*Math.sin(radlat2);
		double z2 = er * Math.cos(radlat2);

		double d = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2));

//		side, side, side, law of cosines and arccos
		double theta = Math.acos((er*er+er*er-d*d)/(2*er*er));
		double distance = theta*er;
		return distance;
	}
    
    public double calcBearing(GPSPosition newPos)
	{
    	return calcBearing(newPos.myLat, newPos.myLon);    	
	}
	
	public double calcBearing(double newLat, double newLon)
	{
		double oldLat = Math.toRadians(myLat);
		newLat = Math.toRadians(newLat);
		double oldLon = Math.toRadians(myLon);
		newLon = Math.toRadians(newLon);
		double dLon = newLon-oldLon;
		double dPhi = Math.log(Math.tan(newLat/2+Math.PI/4)/Math.tan(oldLat/2+Math.PI/4));
		if (Math.abs(dLon) > Math.PI) dLon = dLon>0 ? -(2*Math.PI-dLon) : (2*Math.PI+dLon);
		double rad = Math.atan2(dLon, dPhi);
		
		return Math.toDegrees((rad+2*Math.PI) % (2*Math.PI));		
	}
	
	public Image getSatTile(int zoom)
	{
		Image map = null;
		File image = null;
		boolean bCreated = false;
		try
		{
			Toolkit tk = Toolkit.getDefaultToolkit();
			image = new File("images/"+googleCode(zoom));
			if(image.exists())
				map = tk.getImage(image.getAbsolutePath());
			else
			{
				File imageFolder = new File("images");
				bCreated = true;
				if(!imageFolder.exists())
					imageFolder.mkdirs();
				image.createNewFile();
				
				String urlString = satURL(zoom);
				
				log.debug("try download of image " + urlString);
				
				URL url = new URL(urlString);
				URLConnection urlc = url.openConnection();
				urlc.setConnectTimeout(2000);
				urlc.connect();
				//InputStream is = url.openStream();
				InputStream is = urlc.getInputStream();
				FileOutputStream os = new FileOutputStream(image);
				/*int b = 0;
				
				while((b = is.read()) != -1)
				{
					os.write(b);					
				}*/
				byte[] buffer = new byte[ 0xFFFF ];
			    for ( int len; (len = is.read(buffer)) != -1; )
			    	os.write( buffer, 0, len );
			    
				map = tk.getImage(image.getAbsolutePath());								
			}
		} catch(Exception ex)
		{
			log.error(ex);
			if(image != null && bCreated)
				image.delete();
		}
		return map;
	}
}
