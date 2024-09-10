package graph;


import javax.swing.*;

import org.apache.log4j.Logger;

import data.ErgoData;
import data.ErgoDatastore;
import data.GPSPosition;
import data.XYPosition;

import tools.ErgoTools;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 17.02.2005
 * Time: 19:59:09
 * To change this template use File | Settings | File Templates.
 */
public class ErgoGraph extends JPanel implements KeyEventDispatcher, Runnable {
	static final long serialVersionUID = 0;
	
	private static Logger log = Logger.getLogger(ErgoGraph.class);
	
	private ErgoData myData = null;
    private ErgoDatastore myDatastore = null;

    public final static int MODE_NONE = -1;
    public final static int MODE_BARS = 0;
    public final static int MODE_DIALS = 1;
    public final static int MODE_MAP = 2;

    private int mode = 0;
    
    private ArrayList<Image> myMaps = null;
    private GeneralPath myPath = null;
    private ArrayList<XYPosition> myPositions = null;
    private double myScale = -1;
    private int myDisplaceX = 0;
    private int myDisplaceY = 0;
    private int myWidth = 0;
    private int myHeight = 0;
    
    private int myWindowWidth = 0;
    private int myWindowHeight = 0;
    
    private int leftExtent = 0;
    private int rightExtent = 0;
    private int upperExtent = 0;
    private int lowerExtent = 0;
    private double leftCoord = 0;
    private double rightCoord = 0;
    private double upperCoord = 0;
    private double lowerCoord = 0;
    
    private int myMinGoogleZoom = 0;
    private int myGoogleZoom = -1;
    private double myZoom = 1;
    
    private boolean drawMaps = true;
    
    private String myPreLoadingImages = null;
    private Thread myThread = null;
    private Hashtable<Double, Integer> myResolutions = new Hashtable<Double,Integer>();
    
    Hashtable<String,ErgoData> myMarkers = null;
    
    public ErgoGraph()
    {
        super();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }
    
    private void mapReset()
    {
    	myMaps = null;
    	myPath = null;
    	myPositions = null;
    }
    
    public void resetAll()
    {
    	myResolutions = new Hashtable<Double,Integer>();
    	myZoom = 1;
    	myGoogleZoom = -1;
    	myMinGoogleZoom = 0;
    	drawMaps = true;
    	
    	reset();
    }
    
    public void reset()
    {	
    	myThread = null;
    	while(myPreLoadingImages != null)
    	{
    		try
    		{
    			Thread.sleep(3);
    		}
    		catch(Exception ex)
    		{
    			
    		}
    	}
    	
    	myMaps = null;
    	myPath = null;
    	myPositions = null;
    	myScale = -1;    	    	
    	
    	if(myWindowWidth == 0 && myWindowHeight == 0)
    	{
    		myWindowWidth = this.getWidth();
	        myWindowHeight = this.getHeight();
    	}
    	
    	Dimension dimension = new Dimension(myWindowWidth, myWindowHeight);
		this.setSize(dimension);
		this.setPreferredSize(dimension);
    }

    public void setData(ErgoData newData)
    {
    	myData = newData;
    }
    
    public void setDatastore(ErgoDatastore newDatastore)
    {
        myDatastore = newDatastore;
    }

    public void paint(Graphics g)
    {
    	DecimalFormat decFormat = new DecimalFormat("0.00");
        Graphics2D graph = (Graphics2D) g;
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graph.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        Dimension d = getSize();
        int gridWidth = d.width;
        int gridHeight = d.height;
        
        if(mode == MODE_MAP && myData != null)
        	setBackground(Color.BLACK);
        else
        	setBackground(Color.WHITE);
        super.paintComponent(g);

        if(myData != null)
        {
            if(mode == MODE_BARS)
            {
                int xOffset = (gridWidth)/4;
                int pulsHeight = (gridHeight*myData.getPuls()/ErgoTools.MAX_PULS);
                int rpmHeight = (gridHeight*myData.getRPM()/ErgoTools.MAX_RPM);
                int speedHeight = ((int)(gridHeight*myData.getSpeed()/ErgoTools.MAX_SPEED));
                int powerHeight = ((int)(gridHeight*myData.getPower()/ErgoTools.MAX_POWER));
                int xSpacer = xOffset/10;
                int xPos = 0;

                xOffset -= xSpacer + xSpacer/4;

                xPos = xSpacer;
                //graph.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                drawDataBar(graph, xPos, gridHeight, xOffset, pulsHeight, Integer.toString(myData.getPuls()) + "\n" + ErgoTools.LABEL_PULS_UNIT, ErgoTools.COLOR_PULS);

                xPos = xOffset + xSpacer*2;
                drawDataBar(graph, xPos, gridHeight, xOffset, powerHeight, Integer.toString(myData.getPower()) + "\n" + ErgoTools.LABEL_POWER_UNIT, ErgoTools.COLOR_POWER);

                xPos = xOffset*2 + xSpacer*3;
                drawDataBar(graph, xPos, gridHeight, xOffset, speedHeight, decFormat.format(myData.getSpeed()) + "\n" + ErgoTools.LABEL_SPEED_UNIT, ErgoTools.COLOR_SPEED);

                xPos = xOffset*3 + xSpacer*4;
                drawDataBar(graph, xPos, gridHeight, xOffset, rpmHeight, Integer.toString(myData.getRPM()) + "\n" + ErgoTools.LABEL_RPM_UNIT, ErgoTools.COLOR_RPM);

            }
            else if(mode == MODE_DIALS)
            {
            	int xSpacer = gridWidth/25;
                int ySpacer = gridHeight/25;
                int dia = (int)(gridWidth-xSpacer*3)/2;
                int dia2 = (int)(gridHeight-ySpacer*3)/2;
                int xCenter = 0;
                int yCenter = 0;
                double rad = dia/2;
                double default_size = rad/10;

                if(dia > dia2)
                    dia = dia2;

                xCenter = xSpacer+dia/2;
                yCenter = ySpacer+dia/2;
                drawDial(graph, xCenter, yCenter,  rad, default_size, 10, 20, ErgoTools.MAX_PULS);
                drawDataArrow(graph, myData.getPuls(), ErgoTools.MAX_PULS, xCenter, yCenter, rad, Integer.toString(myData.getPuls()) + "\n" + ErgoTools.LABEL_PULS_UNIT, ErgoTools.COLOR_PULS);

                xCenter = (int)(xSpacer*2+dia*1.5);
                yCenter = ySpacer+dia/2;
                drawDial(graph, xCenter, yCenter,  rad, default_size, 25, 50, ErgoTools.MAX_POWER);
                drawDataArrow(graph, myData.getPower(), ErgoTools.MAX_POWER, xCenter, yCenter, rad, Integer.toString(myData.getPower()) + "\n" + ErgoTools.LABEL_POWER_UNIT, ErgoTools.COLOR_POWER);

                xCenter = xSpacer+dia/2;
                yCenter = (int)(ySpacer*2+dia*1.5);
                drawDial(graph, xCenter, yCenter,  rad, default_size, 5, 10, ErgoTools.MAX_SPEED);
                drawDataArrow(graph, myData.getSpeed(), ErgoTools.MAX_SPEED, xCenter, yCenter, rad, decFormat.format(myData.getSpeed()) + "\n" + ErgoTools.LABEL_SPEED_UNIT, ErgoTools.COLOR_SPEED);

                xCenter = (int)(xSpacer*2+dia*1.5);
                yCenter = (int)(ySpacer*2+dia*1.5);
                drawDial(graph, xCenter, yCenter,  rad, default_size, 5, 10, ErgoTools.MAX_RPM);
                drawDataArrow(graph, myData.getRPM(), ErgoTools.MAX_RPM, xCenter, yCenter, rad, Integer.toString(myData.getRPM()) + "\n" + ErgoTools.LABEL_RPM_UNIT, ErgoTools.COLOR_RPM);
            }
            else if(mode == MODE_MAP)
            {
            	drawMap(graph);
            }
        }
    }

    private void drawDataBar(Graphics2D graph, int x, int y, int width, int height, String text, Color color)
    {
        String displayText = null;
        int xCenter = x+width/2;
        int yCenter = y - graph.getFont().getSize()*4;

        draw3DBar(graph, x, y, width, height, color);
        Font alt = graph.getFont();
        graph.setColor(color.darker().darker().darker());
        graph.setFont(ErgoTools.DEFAULT_FONT);
        displayText = ErgoTools.getLines(text, 0,0);
        graph.drawString(displayText, xCenter - graph.getFontMetrics().stringWidth(displayText)/2, (int)(yCenter + graph.getFont().getSize()/2));
        displayText = ErgoTools.getLines(text, 1,1);
        graph.drawString(displayText, xCenter - graph.getFontMetrics().stringWidth(displayText)/2, (int)(yCenter + graph.getFont().getSize()*1.5));
        graph.setFont(alt);
    }

    private void draw3DBar(Graphics2D graph, int x, int y, int width, int height, Color color)
    {
        Polygon p = null;
        int offset = width/8;

        graph.setColor(color.darker());
        p = new Polygon();
        p.addPoint(x, y-height);
        p.addPoint(x+width-offset, y-height);
        p.addPoint(x+width, y-height-offset);
        p.addPoint(x+offset, y-height-offset);
        p.addPoint(x, y-height);
        graph.fillPolygon(p);
        graph.setColor(color.darker().darker());
        p = new Polygon();
        p.addPoint(x+width-offset, y);
        p.addPoint(x+width, y-offset);
        p.addPoint(x+width, y-height-offset);
        p.addPoint(x+width-offset, y-height);
        p.addPoint(x+width-offset, y);
        graph.fillPolygon(p);
        graph.setColor(color);
        p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x+width-offset, y);
        p.addPoint(x+width-offset, y-height);
        p.addPoint(x, y-height);
        p.addPoint(x, y);
        graph.fillPolygon(p);
    }

    private void drawDataArrow(Graphics2D graph, double data, int maxData, int xCenter, int yCenter, double rad, String text, Color color)
    {
    	drawArrow(graph, data, maxData, xCenter+ErgoTools.SHADOW_OFFSET, yCenter+ErgoTools.SHADOW_OFFSET, rad, color.darker());
        drawArrow(graph, data, maxData, xCenter, yCenter, rad, color);

        drawText(graph, xCenter+ErgoTools.SHADOW_OFFSET, yCenter+(int)rad/2+ErgoTools.SHADOW_OFFSET, text, color.darker());
        drawText(graph, xCenter, yCenter+(int)rad/2, text, color);
    }

    private void drawArrow(Graphics2D graph, double data, int maxData, int xCenter, int yCenter, double rad, Color color)
    {
        double val = Math.toRadians(-45-data/maxData*270);
        double valRight = Math.toRadians(45-data/maxData*270);
        double valLeft = Math.toRadians(-135-data/maxData*270);
        int xVal = xCenter;
        int yVal = yCenter;
        int xVal2 = xCenter+(int)(Math.sin(val) * rad);
        int yVal2 = yCenter+(int)(Math.cos(val) * rad);

        graph.setColor(color.darker());
        Polygon p = new Polygon();
        p.addPoint(xVal, yVal);
        p.addPoint(xCenter+(int)(Math.sin(valRight) * rad/20), yCenter+(int)(Math.cos(valRight) * rad/20));
        p.addPoint(xVal2, yVal2);
        p.addPoint(xCenter+(int)(Math.sin(valLeft) * rad/20), yCenter+(int)(Math.cos(valLeft) * rad/20));
        p.addPoint(xVal, yVal);
        graph.fillPolygon(p);

        graph.fillOval((int)(xCenter-rad/10), (int)(yCenter-rad/10), (int)((rad/10)*2), (int)((rad/10)*2));
    }

    private void drawText(Graphics2D graph, int xCenter, int yCenter, String text, Color color)
    {
        String displayText = null;

        Font alt = graph.getFont();
        graph.setColor(color);
        graph.setFont(ErgoTools.DEFAULT_FONT);
        displayText = ErgoTools.getLines(text, 0,0);
        graph.drawString(displayText, xCenter - graph.getFontMetrics().stringWidth(displayText)/2, (int)(yCenter + graph.getFont().getSize()/2));
        displayText = ErgoTools.getLines(text, 1,1);
        graph.drawString(displayText, xCenter - graph.getFontMetrics().stringWidth(displayText)/2, (int)(yCenter + graph.getFont().getSize()*1.5));
        graph.setFont(alt);
    }

    private void drawDial(Graphics2D graph, int xCenter, int yCenter, double rad, double default_size, double marker, double marker_text, double max_value)
    {
        graph.setColor(Color.GRAY);
        graph.fillOval((int)(xCenter-rad), (int)(yCenter-rad), (int)rad*2+ErgoTools.SHADOW_OFFSET, (int)rad*2+ErgoTools.SHADOW_OFFSET);
        graph.setColor(Color.BLACK);
        graph.fillOval((int)(xCenter-rad), (int)(yCenter-rad), (int)rad*2, (int)rad*2);
        graph.setColor(Color.WHITE);
        for(double i = 0; i <= max_value; i += 5)
        {
            double size = default_size/2;

            if(i%marker == 0)
                size = default_size;

            double val = Math.toRadians(-45-i/max_value*270);
            int xVal = xCenter+(int)(Math.sin(val) * (rad-size));
            int yVal = yCenter+(int)(Math.cos(val) * (rad-size));
            int xVal2 = xCenter+(int)(Math.sin(val) * rad);
            int yVal2 = yCenter+(int)(Math.cos(val) * rad);

            graph.drawLine(xVal, yVal, xVal2, yVal2);
            if(i%marker_text == 0)
            {
                String text = Integer.toString((int)i);
                size = default_size*2;
                xVal = xCenter+(int)(Math.sin(val) * (rad-size));
                yVal = yCenter+(int)(Math.cos(val) * (rad-size));
                xVal = xVal - graph.getFontMetrics().stringWidth(text)/2;
                graph.drawString(text, xVal, yVal + graph.getFont().getSize()/2);
            }
        }
    }
    
    private void drawMap(Graphics2D graph)
    {
    	Dimension d = getSize();
    	int spaceX = 20;
    	int spaceY = 20;
    	int pointerSize = 20;
    	if(myZoom < 1)
    		pointerSize *= myZoom;
        int width = (d.width-spaceX);
        int height = (d.height-spaceY);
        
        int displaceX = 0;
		double xFactor = 0;		
		int displaceY = 0;
		double yFactor = 0;
		
		if(myDatastore == null || !myDatastore.getHasPositionData())
    	{	
			graph.setColor(Color.WHITE);
    		graph.drawLine(0, 0, d.width, d.height);
    		graph.drawLine(0, d.height, d.width, 0);
    	}
    	else
    	{	
    		if(myPreLoadingImages != null)
    		{
    			graph.setColor(Color.WHITE);
    			int corr = myPreLoadingImages.indexOf('.');
    			if(corr == -1)
    				corr = 0;
    			else
    				corr = myPreLoadingImages.length() - corr;
    			graph.drawString(myPreLoadingImages, width/2 - ((myPreLoadingImages.length()-corr)*3), height/2);
    			return;
    		}
    		
    		if(myPositions == null || myMaps == null || myPath == null)
    		{
    			myThread = new Thread(this);
    			myThread.start();
    			return;
    		}
    		
    		displaceX = -leftExtent;
    		int widthOld = rightExtent - leftExtent;
    		xFactor = (double)width/widthOld; 
    		
    		displaceY = -upperExtent;
    		int heightOld = lowerExtent - upperExtent;
    		yFactor = (double)height/heightOld;
    		
    		if(myScale == -1)
    		{
        		if(Math.abs(xFactor) < Math.abs(yFactor))
        			yFactor = -xFactor;
        		else
        			xFactor = -yFactor;
    		}
    		else
    		{
    			xFactor = myScale;
    			yFactor = myScale;
    			displaceX = myDisplaceX;
    			displaceY = myDisplaceY;
    			spaceX = 0;
    			spaceY = 0;
    		}
    		
    		graph.setColor(ErgoTools.COLOR_MAP);
    		
    		int i = 0;
        	boolean bFound = false;
        	Enumeration enumDatastore = myDatastore.elements();
    		while(enumDatastore.hasMoreElements() && !bFound)
    		{
    			ErgoData data = (ErgoData)enumDatastore.nextElement();
    			
    			if(myData.equals(data))
    				bFound = true;
    			else
    				i++;
    		}

    		XYPosition pos = (XYPosition)myPositions.get(i);
    		    		
    		int markerPosX = pos.getX();
    		int markerPosY = pos.getY();
    		
    		GeneralPath path = new GeneralPath(myPath); 
    		if(myZoom > 1)
    		{
    			displaceX = (this.getWidth() / 2) - markerPosX; 
    			displaceY = (this.getWidth() / 2) - markerPosY;
    			markerPosX += displaceX;
    			markerPosY += displaceY;
    			
    			AffineTransform tx = new AffineTransform();
    			tx.translate(displaceX, displaceY);
    			path.transform(tx);
    		}
    		else
    		{
    			displaceX = 0;
    			displaceY = 0;
    		}
    		
    		if(myMaps != null && myMaps.size() > 0)
    		{
    			int h = d.height / myHeight;
    			int w = d.width / myWidth;
    			
    			if(myZoom > 1)
    			{
	    			h = (int)(d.height / myHeight * myZoom);
	    			w = (int)(d.width / myWidth * myZoom);    				
    			}
	    			
    			if(h < w)
    				w = h;
    			else
    				h = w;
    			
    			Integer googleZoom = myResolutions.get(new Double(myZoom));
    			if(googleZoom != null)
    			{
    				if(myGoogleZoom != googleZoom)
    				{
    					mapReset();
    					myGoogleZoom = googleZoom;
	    				return;
    				}
    			}
    			else
    			{
	    			if((h > d.height || w > d.width) && myMinGoogleZoom <= myGoogleZoom - 1)
	    			{
	    				log.debug("higher resolution " + myMinGoogleZoom + " <= " + myGoogleZoom);
	    				mapReset();
	    				myGoogleZoom--;
	    				return;
	    			}
    			}
    			
    			Iterator itMyMaps = myMaps.iterator();
    			for(int x=0,y=0; itMyMaps.hasNext(); )
    			{
    				Image map = (Image)itMyMaps.next();
    				
    				if(map != null)
    					graph.drawImage(map, displaceX+x*w, displaceY+y*h, w, h, this);
    				
    				x++;
    				if(x >= myWidth)
    				{
    					x = 0;
    					y++;
    				}
    			}    			
    		}
    		    		
    		graph.draw(path);
    		
    		graph.setColor(ErgoTools.COLOR_MARKER);
    		
    		int strikeLength = (int)(pointerSize/2*0.7);    		
    		
    		graph.drawOval(markerPosX - pointerSize/2, markerPosY - pointerSize/2, pointerSize, pointerSize);
    		graph.drawLine(markerPosX - strikeLength, markerPosY - strikeLength, markerPosX + strikeLength, markerPosY + strikeLength);
    		graph.drawLine(markerPosX - strikeLength, markerPosY + strikeLength, markerPosX + strikeLength, markerPosY - strikeLength);
    		
    		displaceX = -leftExtent;
    		xFactor = (double)width/widthOld; 
    		
    		displaceY = -upperExtent;
    		yFactor = (double)height/heightOld;
    		
    		if(myScale == -1)
    		{
        		if(Math.abs(xFactor) < Math.abs(yFactor))
        			yFactor = -xFactor;
        		else
        			xFactor = -yFactor;
    		}
    		else
    		{
    			xFactor = myScale;
    			yFactor = myScale;
    			displaceX = myDisplaceX;
    			displaceY = myDisplaceY;
    			spaceX = 0;
    			spaceY = 0;
    		}
    		
    		pointerSize /= 2;
    		strikeLength = (int)(pointerSize/2*0.7);
    		
    		if(myMarkers != null)
            {
    			Enumeration<String> enumKeys = myMarkers.keys();
            	while(enumKeys.hasMoreElements())
            	{
            		String key = enumKeys.nextElement();
            		ErgoData data = myMarkers.get(key);
            		GPSPosition pos2 = data.getPosition();
            		
            		if(data.isSimulation())
            			graph.setColor(ErgoTools.COLOR_MARKER2SIM);
            		else
            			graph.setColor(ErgoTools.COLOR_MARKER2);
            		
            		if(pos2 != null)
            		{
	            		int markerPosX2 = (int)((pos2.getXpixel(0) + displaceX) * xFactor) + spaceX/2;
	            		int markerPosY2 = (int)((pos2.getYpixel(0) + displaceY) * yFactor) + spaceY/2;
	            		
	            		graph.drawOval(markerPosX2 - pointerSize/2, markerPosY2 - pointerSize/2, pointerSize, pointerSize);
	            		graph.drawLine(markerPosX2 - strikeLength, markerPosY2 - strikeLength, markerPosX2 + strikeLength, markerPosY2 + strikeLength);
	            		graph.drawLine(markerPosX2 - strikeLength, markerPosY2 + strikeLength, markerPosX2 + strikeLength, markerPosY2 - strikeLength);
	            		graph.drawString(key, markerPosX2 + pointerSize, markerPosY2 + pointerSize/2);
            		}
            	}
            }

    		try
    		{
    			Thread.sleep(3);
    		}
    		catch(Exception ex)
    		{
    			
    		}
    	}  	
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int newMode)
    {
        mode = newMode;
    }
    
    public boolean dispatchKeyEvent(KeyEvent e) 
    {
    	double zoom = myZoom;
    	int googleZoom = myGoogleZoom;
    	
    	if( e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		myZoom /= 2;
            		if(myZoom < 1)
                		myZoom = 1;
            	    break;
            }
        }
    	
    	if( e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
            switch( e.getID() ) {
            	case KeyEvent.KEY_PRESSED:
            	    break;
            	case KeyEvent.KEY_RELEASED:
            		if(!myResolutions.contains(new Double(myZoom)))
    					myResolutions.put(new Double(myZoom), new Integer(myGoogleZoom));
            		myZoom *= 2;
            	    break;
            }
        }
    	
    	if(zoom != myZoom || googleZoom != myGoogleZoom)
    	{
    		zoom = myZoom;
    		googleZoom = myGoogleZoom;
    		reset();
    		myZoom = zoom;
    		myGoogleZoom = googleZoom;
    	}
    	
        return false;
    }
    
    public void loadImages()
    {
    	Dimension d = getSize();
    	long sleep = 2;
    	
    	int spaceX = 20;
    	int spaceY = 20;
    	int width = (d.width-spaceX);
        int height = (d.height-spaceY);
        
        int displaceX = 0;
		double xFactor = 0;		
		int displaceY = 0;
		double yFactor = 0;
		
		String dots = ".";
    	
    	if(myPositions == null)
		{
			XYPosition lastPos = null;
    		leftExtent = 0;
    		rightExtent = 0;
    		upperExtent = 0;
    		lowerExtent = 0;
    		leftCoord = 0;
    		rightCoord = 0;
    		upperCoord = 0;
    		lowerCoord = 0;
    		
    		dots = ".";
    		
    		myPositions = new ArrayList<XYPosition>();
    		Enumeration enumDatastore = myDatastore.elements();
    		while(enumDatastore.hasMoreElements())
    		{
    			ErgoData data = (ErgoData)enumDatastore.nextElement();
    			
    			if(myThread == null)
    				return;
    			myPreLoadingImages = "Loading Position Data" + dots;    			
    			if(dots.length() > 3)
    				dots = ".";
    			else
    				dots += ".";
    			try
        		{
        			Thread.sleep(sleep);
        		}
        		catch(Exception ex)
        		{
        			
        		}
    			
    			GPSPosition curPosGPS = data.getPosition();
    			XYPosition curPos = new XYPosition(curPosGPS.getXpixel(0), curPosGPS.getYpixel(0));
    			myPositions.add(curPos);    			
    			
    			if(lastPos == null)
    			{
    				leftExtent = curPos.getX();
    				rightExtent = curPos.getX();
    				upperExtent = curPos.getY();
    				lowerExtent = curPos.getY();
    				
    				leftCoord = curPosGPS.getLon();
    	    		rightCoord = curPosGPS.getLon();
    	    		upperCoord = curPosGPS.getLat();
    	    		lowerCoord = curPosGPS.getLat();
    			}
    			else
    			{
    				if(curPos.getX() < leftExtent)
    					leftExtent = curPos.getX();
    				if(curPos.getX() > rightExtent)
    					rightExtent = curPos.getX();
    				
    				if(curPos.getY() > upperExtent)
    					upperExtent = curPos.getY();
    				if(curPos.getY() < lowerExtent)
    					lowerExtent = curPos.getY();
    				
    				if(curPosGPS.getLon() < leftCoord)
    					leftCoord = curPosGPS.getLon();
    				if(curPosGPS.getLon() > rightCoord)
    					rightCoord = curPosGPS.getLon();
    				
    				if(curPosGPS.getLat() > upperCoord)
    					upperCoord = curPosGPS.getLat();
    				if(curPosGPS.getLat() < lowerCoord)
    					lowerCoord = curPosGPS.getLat();
    			}
    			
    			lastPos = curPos;
    		}
		}
		
		if(myMaps == null)
		{    			
			GPSPosition leftUpper = new GPSPosition(upperCoord,leftCoord);
			GPSPosition rightUpper = new GPSPosition(upperCoord,rightCoord);
			GPSPosition leftLower = new GPSPosition(lowerCoord,leftCoord);    			
			
			if(myGoogleZoom == -1)
			{
    			double distanceWidth = leftUpper.calcDistance(rightUpper) / (180.0/(180-upperCoord));
    			double distanceHeight = leftUpper.calcDistance(leftLower);
    			
    			if(distanceHeight < distanceWidth)
    				myGoogleZoom = leftUpper.zoomFactor(distanceWidth);
    			else
    				myGoogleZoom = leftUpper.zoomFactor(distanceHeight);
			}
			
			myMaps = new ArrayList<Image>();
			
			GPSPosition start = new GPSPosition(upperCoord, leftCoord);
			start.googleCorrection(myGoogleZoom);
			GPSPosition pos = null;
			
			if(drawMaps)
			{
				int x = 1;
				int y = 1;
				boolean bBreakX = false;
				boolean bBreakY = false;
				int stepSize = (int)(Math.pow(2,myGoogleZoom) * 256);
				
				dots = ".";
				 
				do
				{    				
					pos = new GPSPosition(start);
					
					if(myThread == null)
	    				return;
					myPreLoadingImages = "Loading Map Data" + dots;
	    			if(dots.length() > 3)
	    				dots = ".";
	    			else
	    				dots += ".";
	    			try
	        		{
	        			Thread.sleep(sleep);
	        		}
	        		catch(Exception ex)
	        		{
	        			
	        		}
					
					for(int i = 1; i < y; i++)
						pos.moveTileDown(myGoogleZoom);
					
					//if(pos.getLat() < lowerCoord)
					if((pos.getYpixel(0) + stepSize) > upperExtent)
					{
						bBreakY = true;
						myHeight = y;
					}
					//else
					{
	    				do
		    			{
	    					if(myThread == null)
	    	    				return;
	    					myPreLoadingImages = "Loading Map Data" + dots;
	    					if(dots.length() > 3)
	    	    				dots = ".";
	    	    			else
	    	    				dots += ".";
	    					try
	    		    		{
	    		    			Thread.sleep(sleep);
	    		    		}
	    		    		catch(Exception ex)
	    		    		{
	    		    			
	    		    		}
	    					
	    					//if(pos.getLon() > rightCoord)
	    					if((pos.getXpixel(0) + stepSize) > rightExtent)
	    					{
	    						bBreakX = true;
	    						myWidth = x;//-1;
	    					}
	    					//else
	    					{
	    						Image img = pos.getSatTile(myGoogleZoom);
	    						if(img == null)
	    						{
	    							if(myZoom == 1)
	    								drawMaps = false;
	    							else
	    							{
		    							mapReset();
		    							myGoogleZoom++;
		    							myMinGoogleZoom = myGoogleZoom;
		    							log.debug("lower resolution " + myMinGoogleZoom + " <= " + myGoogleZoom);	    							
		    							return;
	    							}
	    						}
	    						if(img != null)
	    							myMaps.add(img);
	
	    						pos.moveTileRight(myGoogleZoom);
			    				x++;	    						
	    					}
		    			}
	    				while(!bBreakX);
					}
	    			y++;
	    			x = 1;
	    			bBreakX = false;
				}
				while(!bBreakY);
			}
			
			myScale = GPSPosition.scaleFactor(myGoogleZoom);
			myDisplaceX = -leftExtent + (leftUpper.getXpixel(0) - start.getXpixel(0));
			myDisplaceY = -lowerExtent + (leftUpper.getYpixel(0) - start.getYpixel(0));
			
			double zoom = myZoom;
			if(zoom < 1)
				zoom = 1;
			int h = (int)(d.height / myHeight * zoom);
			int w = (int)(d.width / myWidth * zoom);
			
			int newWidth = 0;
			if(h < w)
			{
				newWidth = h*myWidth;
				myScale *= (h/256.0);
			}
			else
			{
				newWidth = w*myWidth;
				myScale *= (w/256.0);
			}
						
			if(newWidth < d.width)
			{
    			Dimension dimension = new Dimension(newWidth, myWindowHeight);
    			this.setSize(dimension);
				this.setPreferredSize(dimension);
			}
		}
		
		displaceX = -leftExtent;
		int widthOld = rightExtent - leftExtent;
		xFactor = (double)width/widthOld; 
		
		displaceY = -upperExtent;
		int heightOld = lowerExtent - upperExtent;
		yFactor = (double)height/heightOld;
		
		if(myScale == -1)
		{
    		if(Math.abs(xFactor) < Math.abs(yFactor))
    			yFactor = -xFactor;
    		else
    			xFactor = -yFactor;
		}
		else
		{
			xFactor = myScale;
			yFactor = myScale;
			displaceX = myDisplaceX;
			displaceY = myDisplaceY;
			spaceX = 0;
			spaceY = 0;
		}
		
		if(myPath == null)
		{   
			myPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD, myPositions.size());
    		int lastX = 0;
    		int lastY = 0;
    		boolean bFirst = true;
    		
    		dots = ".";
    		
    		Iterator itPositions = myPositions.iterator();
    		while(itPositions.hasNext())
    		{
    			XYPosition pos = (XYPosition)itPositions.next();
    			
    			if(myThread == null)
    				return;
    			myPreLoadingImages = "Loading Path Data" + dots;
				if(dots.length() > 3)
    				dots = ".";
    			else
    				dots += ".";
				try
	    		{
	    			Thread.sleep(sleep);
	    		}
	    		catch(Exception ex)
	    		{
	    			
	    		}
    	
    			pos.setX((int)((pos.getX() + displaceX) * xFactor) + spaceX/2);
    			pos.setY((int)((pos.getY() + displaceY) * yFactor) + spaceY/2);
    			
    			if(!bFirst)
    			{
    				//graph.drawLine(lastX, lastY, pos.getX(), pos.getY());    			

	    	        myPath.moveTo (lastX, lastY);
	    	        myPath.lineTo(pos.getX(),pos.getY());
    	        }
    			
    			lastX = pos.getX();
    			lastY = pos.getY();
    			bFirst = false;
    		}
		}
    }

	public synchronized void run() 
	{
		myPreLoadingImages = "Loading";
		loadImages();
		myThread = null;
		myPreLoadingImages = null;		
	}
	
	public void setMarker(Hashtable<String,ErgoData> dataArray)
    {
    	myMarkers = dataArray;
    }
}