package graph;


import javax.swing.*;
import javax.swing.event.EventListenerList;

import data.ErgoData;
import data.ErgoDatastore;

import tools.ErgoTools;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.util.Enumeration;
import java.util.BitSet;
import java.util.Date;
import java.util.Hashtable;
import java.text.SimpleDateFormat;


public class ErgoLineGraph extends JPanel implements MouseListener, MouseMotionListener {
	static final long serialVersionUID = 0;
	
    private ErgoDatastore  myErgoDatastore = null;

    protected EventListenerList myListenerList = new EventListenerList();

    public static final int POWER = 1;
    public static final int PULS = 2;
    public static final int RPM = 3;
    public static final int SPEED = 4;
    public static final int GRADE = 5;

    private BitSet myAttribute = new BitSet();
    private boolean myIsClickSensitive = false;
    private boolean myIsDrawing = false;
//    private int myMarker = -1;
    private int myDistMarker = -1;
    private int myTimeMarker = -1;
    
    Hashtable<String,ErgoData> myMarkers = null;

    private int lastX = -1;
//    private int lastY = -1;
    
    private double myMaxHeight = 0;
    private double myMinHeight = 0;
    private double myStepHeight = 0;
    
    private int x2Points[] = null;
    private int y2PointsRPM[] = null;
    private int y2PointsPuls[] = null;
    private int y2PointsPower[] = null;
    private int y2PointsSpeed[] = null;
    private int y2PointsHeight[] = null;
    private GeneralPath pathRPM = null;
    private GeneralPath pathPuls = null;
    private GeneralPath pathPower = null;
    private GeneralPath pathSpeed = null;
    private GeneralPath pathHeight = null;
    int distance = 0;
    
    public ErgoLineGraph()
    {
        super();

        unsetAllAttributes();
        myAttribute.set(POWER, true);


        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setDatastore(ErgoDatastore newErgoDatastore, boolean isClickSensitive, boolean isDrawing)
    {
        myErgoDatastore = newErgoDatastore;
        myIsClickSensitive = isClickSensitive;
        myIsDrawing = isDrawing;
//        myMarker = -1;
        myDistMarker = -1;
        myTimeMarker = -1;
        
        if(!myIsDrawing && myErgoDatastore != null && myErgoDatastore.size() > 0)
        {
        	Enumeration enumDatastore = myErgoDatastore.elements();
        	myMaxHeight = 0;
        	myMinHeight = 0;
        	while(enumDatastore.hasMoreElements())
        	{
        		ErgoData data = (ErgoData)enumDatastore.nextElement();
        		if(myMaxHeight == 0 && myMinHeight == 0)
        		{
        			myMinHeight = data.getHeight();
        			myMaxHeight = data.getHeight();
        		}
        		if(myMaxHeight < data.getHeight())
        			myMaxHeight = data.getHeight();
        		if(myMinHeight > data.getHeight())
        			myMinHeight = data.getHeight();
        	}
        	myMaxHeight = Math.floor(myMaxHeight/100+1) * 100;
        	myMinHeight = Math.floor(myMinHeight/100) * 100;
        	myStepHeight = (myMaxHeight - myMinHeight) / 10;
        	myMaxHeight += myStepHeight;
        	myMinHeight -= myStepHeight;
        	myMaxHeight = Math.floor(myMaxHeight/10+1) * 10;
        	myMinHeight = Math.floor(myMinHeight/10) * 10;
        	myStepHeight = (myMaxHeight - myMinHeight) / 10;
        }
        else
        {
        	myMaxHeight = ErgoTools.MAX_HEIGHT;
        	myMinHeight = ErgoTools.MIN_HEIGHT;
        	myStepHeight = ErgoTools.HEIGHT_STEP;
        }        	
        
        x2Points = null;
        y2PointsRPM = null;
        y2PointsPuls = null;
        y2PointsPower = null;
        y2PointsSpeed = null;
        y2PointsHeight = null;
        
        distance = 0;

        calcLines();
    }
    
    public void calcLines()
    {
    	if(myErgoDatastore != null)
    	{
	//    	 draw GeneralPath (polyline)             
	        x2Points = new int[myErgoDatastore.size()];
	        y2PointsRPM = new int[myErgoDatastore.size()];
	        y2PointsPuls = new int[myErgoDatastore.size()];
	        y2PointsPower = new int[myErgoDatastore.size()];
	        y2PointsSpeed = new int[myErgoDatastore.size()];
	        y2PointsHeight = new int[myErgoDatastore.size()];
	        
	        int i = 0;
	        Dimension d = getSize();        
	        int gridHeight = d.height;
	        int gridWidth = d.width;
	        int xStartPos = 0;
	        
	        if((!myIsClickSensitive && !myIsDrawing) || (!myIsClickSensitive && (myIsDrawing && gridWidth-myErgoDatastore.size()<0)))
                xStartPos=gridWidth-myErgoDatastore.size();
	        
	        Enumeration enumDatastore = myErgoDatastore.elements();
	        while(enumDatastore.hasMoreElements() && x2Points.length > i && y2PointsPower.length > i)
	        {
	            ErgoData data = (ErgoData)enumDatastore.nextElement();
	            // set x
	            distance = (int)Math.floor((data.getDistance()*100) + 0.5);
	            if(!myErgoDatastore.getUseDistance())
	            	x2Points[i] = i + xStartPos;
	            else
	            	x2Points[i] = distance;
	
	            // set y
	            //if(myAttribute.get(POWER))
	                y2PointsPower[i] = gridHeight - ((gridHeight * data.getPower())/ErgoTools.MAX_POWER);
	            //if(myAttribute.get(PULS))
	                y2PointsPuls[i] = gridHeight - ((gridHeight * data.getPuls())/ErgoTools.MAX_PULS);
	            //if(myAttribute.get(RPM))
	                y2PointsRPM[i] = gridHeight - ((gridHeight * data.getRPM())/ErgoTools.MAX_RPM);
	            //if(myAttribute.get(SPEED))
	                y2PointsSpeed[i] = (int)(gridHeight - ((gridHeight * data.getSpeed())/ErgoTools.MAX_SPEED));
	            //if(myAttribute.get(GRADE))
	                y2PointsHeight[i] = (int)(gridHeight - ((gridHeight * (data.getHeight() - myMinHeight))/(myMaxHeight-myMinHeight)));
	            
	            i++;                    
	        }
	        
	        pathPower = createFill(x2Points, y2PointsPower, gridHeight);
	        pathPuls = createLine(x2Points, y2PointsPuls);
	        pathRPM = createLine(x2Points, y2PointsRPM);
	        pathSpeed = createLine(x2Points, y2PointsSpeed);
	        pathHeight = createLine(x2Points, y2PointsHeight);
    	}
    }

    public void setAttribute(int attribute)
    {
        myAttribute.set(attribute, true);
    }

    public void unsetAttribute(int attribute)
    {
        myAttribute.set(attribute, false);
    }

    public void setAllAttributes()
    {
        unsetAllAttributes();
        myAttribute.flip(0, myAttribute.size()-1);
    }

    public void unsetAllAttributes()
    {
        myAttribute.clear();
    }

    public boolean getAttribute(int attribute)
    {
        return myAttribute.get(attribute);
    }

    public void paint(Graphics g)
    {
    	Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        Dimension d = getSize();
        
        int gridWidth = d.width;
        int gridHeight = d.height;
        //int clipX = g.getClipBounds().x;
        //int clipWidth = g.getClipBounds().width;
                
        setBackground(Color.WHITE);
        super.paintComponent(g);

        if(myErgoDatastore != null)
        {
            if(myErgoDatastore.size() > 0)
            {   
            	int xStartPos=0;
            	int i=0;
            	
            	if((!myIsClickSensitive && !myIsDrawing) || (!myIsClickSensitive && (myIsDrawing && gridWidth-myErgoDatastore.size()<0)))
                    xStartPos=gridWidth-myErgoDatastore.size();

                g2.setColor(ErgoTools.COLOR_POWER);
                if(myAttribute.get(POWER))
                    g2.draw(pathPower);
                
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                if(myAttribute.get(PULS))
                    g2.draw(pathPuls);
                if(myAttribute.get(RPM))
                    g2.draw(pathRPM);
                if(myAttribute.get(SPEED))
                    g2.draw(pathSpeed);
                if(myAttribute.get(GRADE))
                    g2.draw(pathHeight);

                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                g2.setColor(ErgoTools.COLOR_POWER);
                if(myAttribute.get(POWER))
                    g2.draw(pathPower);
                g2.setColor(ErgoTools.COLOR_PULS);
                if(myAttribute.get(PULS))
                    g2.draw(pathPuls);
                g2.setPaintMode();
                g2.setColor(ErgoTools.COLOR_RPM);
                if(myAttribute.get(RPM))
                    g2.draw(pathRPM);
                g2.setColor(ErgoTools.COLOR_SPEED);
                if(myAttribute.get(SPEED))
                    g2.draw(pathSpeed);
                g2.setColor(ErgoTools.COLOR_HEIGHT);
                if(myAttribute.get(GRADE))
                    g2.draw(pathHeight);

                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                g2.setColor(Color.BLACK);
                if(!myErgoDatastore.getUseDistance())
                {
	                i=0;
	                Enumeration enumDatastore2 = myErgoDatastore.elements();
	                while(enumDatastore2.hasMoreElements() && x2Points.length > i)
	                {
	                    ErgoData data = (ErgoData)enumDatastore2.nextElement();
	
	                    if(data.getTime().endsWith("00"))
	                    {
	                        g2.drawLine(x2Points[i], gridHeight, x2Points[i], gridHeight - (gridHeight/15));
	                        g2.drawString(data.getTime(), x2Points[i], gridHeight - (gridHeight/15));
	                    }
	                    else if(data.getTime().endsWith("30"))
	                        g2.drawLine(x2Points[i], gridHeight, x2Points[i], gridHeight - (gridHeight/25));
	                    else if(data.getTime().endsWith("0") || data.getTime().endsWith("5"))
	                        g2.drawLine(x2Points[i], gridHeight, x2Points[i], gridHeight - (gridHeight/50));                                       	
	                    
	                    i++;
	                }
                }
                else
                {
                	for(i=0; i < distance; i++)
                	{
                		if((i%100) == 0)
                		{
                			g2.drawLine(i, gridHeight, i, gridHeight - (gridHeight/15));
                			g2.drawString("" + i/100, i, gridHeight - (gridHeight/15));
                		}
                		else if((i%10) == 0)
                		{
                			g2.drawLine(i, gridHeight, i, gridHeight - (gridHeight/25));
                		}
                	}
                }

                if(!myAttribute.get(POWER) && myAttribute.get(PULS) && !myAttribute.get(RPM) && !myAttribute.get(SPEED) && !myAttribute.get(GRADE)) //PULS
                {
                    g2.setColor(ErgoTools.COLOR_PULS.darker());
                    for(i = ErgoTools.PULS_STEP; i < ErgoTools.MAX_PULS; i+=ErgoTools.PULS_STEP)
                    {
                        int val = gridHeight - (gridHeight * i)/ErgoTools.MAX_PULS;
                        if(!myErgoDatastore.getUseDistance())
                        	g2.drawLine(xStartPos, val, xStartPos+myErgoDatastore.size(), val);
                        else
                        	g2.drawLine(xStartPos, val, xStartPos+distance, val);
                        g2.drawString(Integer.toString(i) + " " + ErgoTools.LABEL_PULS_UNIT, xStartPos, val);
                    }
                }
                else if(!myAttribute.get(POWER) && !myAttribute.get(PULS) && myAttribute.get(RPM) && !myAttribute.get(SPEED) && !myAttribute.get(GRADE)) //RPM
                {
                    g2.setColor(ErgoTools.COLOR_RPM.darker());
                    for(i = ErgoTools.RPM_STEP; i < ErgoTools.MAX_RPM; i+=ErgoTools.RPM_STEP)
                    {
                        int val = gridHeight - (gridHeight * i)/ErgoTools.MAX_RPM;
                        if(!myErgoDatastore.getUseDistance())
                        	g2.drawLine(xStartPos, val, xStartPos+myErgoDatastore.size(), val);
                        else
                        	g2.drawLine(xStartPos, val, xStartPos+distance, val);
                        g2.drawString(Integer.toString(i) + " " + ErgoTools.LABEL_RPM_UNIT, xStartPos, val);
                    }
                }
                else if(!myAttribute.get(POWER) && !myAttribute.get(PULS) && !myAttribute.get(RPM) && myAttribute.get(SPEED) && !myAttribute.get(GRADE)) //SPEED
                {
                    g2.setColor(ErgoTools.COLOR_SPEED.darker());
                    for(i = ErgoTools.SPEED_STEP; i < ErgoTools.MAX_SPEED; i+=ErgoTools.SPEED_STEP)
                    {
                        int val = gridHeight - (gridHeight * i)/ErgoTools.MAX_SPEED;
                        if(!myErgoDatastore.getUseDistance())
                        	g2.drawLine(xStartPos, val, xStartPos+myErgoDatastore.size(), val);
                        else
                        	g2.drawLine(xStartPos, val, xStartPos+distance, val);
                        g2.drawString(Integer.toString(i) + " " + ErgoTools.LABEL_SPEED_UNIT, xStartPos, val);
                    }
                }
                else if(!myAttribute.get(POWER) && !myAttribute.get(PULS) && !myAttribute.get(RPM) && !myAttribute.get(SPEED) && myAttribute.get(GRADE)) //HEIGHT
                {
                    g2.setColor(ErgoTools.COLOR_HEIGHT.darker());
                    for(i = (int)myStepHeight; i < myMaxHeight; i+=myStepHeight)
                    {
                        int val = (int)(gridHeight - (gridHeight * i)/(myMaxHeight - myMinHeight));
                        if(!myErgoDatastore.getUseDistance())
                        	g2.drawLine(xStartPos, val, xStartPos+myErgoDatastore.size(), val);
                        else
                        	g2.drawLine(xStartPos, val, xStartPos+distance, val);
                        g2.drawString(Integer.toString(i+(int)myMinHeight) + " " + ErgoTools.LABEL_HEIGHT_UNIT, xStartPos, val);
                    }
                }
                else
                {
                    g2.setColor(ErgoTools.COLOR_POWER.darker());
                    for(i = ErgoTools.POWER_STEP; i < ErgoTools.MAX_POWER; i+=ErgoTools.POWER_STEP)
                    {
                        int val = gridHeight - (gridHeight * i)/ErgoTools.MAX_POWER;
                        if(!myErgoDatastore.getUseDistance())
                        	g2.drawLine(xStartPos, val, xStartPos+myErgoDatastore.size(), val);
                        else
                        	g2.drawLine(xStartPos, val, xStartPos+distance, val);
                        g2.drawString(Integer.toString(i) + " " + ErgoTools.LABEL_POWER_UNIT, xStartPos, val);
                    }
                }

/*                if(myMarker > 0 )
                {
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(ErgoTools.COLOR_MARKER);
                    if(!myErgoDatastore.getUseDistance())
                    	g2.drawLine(xStartPos+myMarker,0,xStartPos+myMarker,gridHeight);
                    else
                    {
                    	ErgoData data = myErgoDatastore.get(myMarker);
                    	int mark = (int)Math.floor((data.getDistance()*100)+0.5);
                    	g2.drawLine(xStartPos+mark,0,xStartPos+mark,gridHeight);                    	
                    }
                }*/
                if(myMarkers != null)
                {
                	Enumeration<String> enumKeys = myMarkers.keys();
                	while(enumKeys.hasMoreElements())
                	{
                		String key = enumKeys.nextElement();
                		ErgoData data = myMarkers.get(key);
                		
                		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                		if(data.isSimulation())
                			g2.setColor(ErgoTools.COLOR_DISTMARKER2SIM);
                		else
                			g2.setColor(ErgoTools.COLOR_DISTMARKER2);                			
                        int mark = (int)Math.floor((data.getDistance()*100)+0.5);
                        g2.drawLine(xStartPos+mark,0,xStartPos+mark,gridHeight);
                        g2.drawString(key, xStartPos+mark, 12);
                	}
                }
                
                if(myTimeMarker > 0 )
                {
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(ErgoTools.COLOR_TIMEMARKER);
                    if(!myErgoDatastore.getUseDistance())
                    	g2.drawLine(xStartPos+myTimeMarker,0,xStartPos+myTimeMarker,gridHeight);
                    else
                    {
                    	ErgoData data = myErgoDatastore.get(myTimeMarker);
                    	int mark = (int)Math.floor((data.getDistance()*100)+0.5);
                    	g2.drawLine(xStartPos+mark,0,xStartPos+mark,gridHeight);                    	
                    }
                }
                if(myDistMarker > 0 )
                {
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                    g2.setColor(ErgoTools.COLOR_DISTMARKER);
                    if(!myErgoDatastore.getUseDistance())
                    	g2.drawLine(xStartPos+myDistMarker,0,xStartPos+myDistMarker,gridHeight);
                    else
                    {
                    	ErgoData data = myErgoDatastore.get(myDistMarker);
                    	int mark = (int)Math.floor((data.getDistance()*100)+0.5);
                    	g2.drawLine(xStartPos+mark,0,xStartPos+mark,gridHeight);                    	
                    }
                }
            }
        }
    }

    private GeneralPath createLine(int x2Points[], int y2Points[])
    {
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);

        if(x2Points.length > 0 && y2Points.length > 0)
        {
	        polyline.moveTo (x2Points[0], y2Points[0]);
	
	        for (int index = 0; index < x2Points.length ; index++)
	        {
	            polyline.lineTo(x2Points[index],
	            y2Points[index]);
	        }
        }

        return polyline;
    }

    private GeneralPath createFill(int x2Points[], int y2Points[], int height)
    {
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);

        for (int index = 0; index < x2Points.length ; index++)
        {
            polyline.moveTo(x2Points[index], height);
            polyline.lineTo(x2Points[index], y2Points[index]);
        }

        return polyline;
    }

    private void drawMarker(int xMarker, int yMarker, boolean bDist)
    {
        Dimension d = getSize();
        int gridWidth = d.width;
        int gridHeight = d.height;
        int xStartPos = 0;
        int xEndPos = xStartPos+myErgoDatastore.size();
        if((((myIsDrawing && gridWidth-myErgoDatastore.size()<0) && !myIsClickSensitive) || (!myIsDrawing && !myIsClickSensitive)) && myErgoDatastore != null)
            xStartPos=gridWidth-myErgoDatastore.size();
        int marker = 0;
        
        //myMarker = xMarker-xStartPos;
        if(bDist)
        	myDistMarker = xMarker-xStartPos;
        else
        	myTimeMarker = xMarker-xStartPos;
        
        //if(myErgoDatastore.getUseDistance() && myMarker > 0)
        if(bDist && myErgoDatastore.getUseDistance() && myDistMarker > 0)
        {
        	int newMarker = myDistMarker;
        	xEndPos = xStartPos + (int)Math.floor((myErgoDatastore.get(myErgoDatastore.size()-1).getDistance()+1)*100); 
        	
        	if(myDistMarker >= myErgoDatastore.size())
        		newMarker = myErgoDatastore.size()-1;
        	
        	ErgoData data = myErgoDatastore.get(newMarker);
        	if(Math.floor((data.getDistance()*100)+0.5) < myDistMarker)
        	{
        		do
        		{
        			if((newMarker + 1) < myErgoDatastore.size())
        			{
        				newMarker++;
        				data = myErgoDatastore.get(newMarker);
        			}
        		}
        		while(Math.floor((data.getDistance()*100)+0.5) < myDistMarker && (newMarker + 1) < myErgoDatastore.size());
        	}
        	else if(Math.floor((data.getDistance()*100)+0.5) > myDistMarker)
        	{
        		do
        		{
        			if((newMarker - 1) > 0)
        			{
        				newMarker--;
        				data = myErgoDatastore.get(newMarker);
        			}
        		}
        		while(Math.floor((data.getDistance()*100)+0.5) > myDistMarker && (newMarker - 1) > 0);        		
        	}
        	//myMarker = newMarker;
           	myDistMarker = newMarker;
           	marker = myDistMarker;
        }
        else
        	marker = myTimeMarker;
        
        if(myErgoDatastore != null && marker > 0)
        {
            if(myIsClickSensitive && xMarker >= xStartPos && xMarker < xEndPos )
            {
                ErgoData data = myErgoDatastore.get(marker);
                fireDataAvailable(new ErgoLineGraphEvent(this), data);
            }
            if(myIsDrawing)
            {
            	int xPos = xStartPos + xMarker;
                int yPos = 0;
                int startpos = xEndPos;

                if(xPos > 0)
                {
                    if(myAttribute.get(POWER))
                        yPos = ((- yMarker + gridHeight) * ErgoTools.MAX_POWER) / gridHeight;
                    if(myAttribute.get(PULS))
                        yPos = ((- yMarker + gridHeight) * ErgoTools.MAX_PULS) / gridHeight;
                    if(myAttribute.get(RPM))
                        yPos = ((- yMarker + gridHeight) * ErgoTools.MAX_RPM) / gridHeight;
                    if(myAttribute.get(SPEED))
                        yPos = ((- yMarker + gridHeight) * ErgoTools.MAX_SPEED) / gridHeight;
                    if(myAttribute.get(GRADE))
                        yPos = (int)(((- yMarker + gridHeight) * myMaxHeight) / gridHeight);

                    if(startpos > xPos)
                    {
                        int start;
                        int stop;

                        if(lastX == -1)
                            lastX = xPos;

                        if(lastX>xPos)
                        {
                            stop=lastX;
                            start=xPos-xStartPos;
                        }
                        else
                        {
                            start=lastX;
                            stop=xPos;
                        }
                        for(int i=start; i < stop;i++)
                        {
                        	double dist = 0;
                        	
                        	if(myErgoDatastore.getUseDistance())
                        		dist = i * 0.01;
                        	
                            if(myAttribute.get(POWER))
                                myErgoDatastore.set(i, new ErgoData(null, 0, 0, 0, dist, yPos, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(PULS))
                                myErgoDatastore.set(i, new ErgoData(null, yPos, 0, 0, dist, 0, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(RPM))
                                myErgoDatastore.set(i, new ErgoData(null, 0, yPos, 0, dist, 0, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(SPEED))
                                myErgoDatastore.set(i, new ErgoData(null, 0, 0, yPos, dist, 0, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(GRADE))
                                myErgoDatastore.set(i, new ErgoData(null, 0, 0, 0, dist, 0, 0, yPos, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                        }
                    }
                    else
                    {
                        for(int i=myErgoDatastore.size(); i < xPos; i++)
                        {
                        	double dist = 0;
                        	
                        	if(myErgoDatastore.getUseDistance())
                        		dist = i * 0.01;
                        	
                            if(myAttribute.get(POWER))
                                myErgoDatastore.add(new ErgoData(null, 0, 0, 0, dist, yPos, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(PULS))
                                myErgoDatastore.add(new ErgoData(null, yPos, 0, 0, dist, 0, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(RPM))
                                myErgoDatastore.add(new ErgoData(null, 0, yPos, 0, dist, 0, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(SPEED))
                                myErgoDatastore.add(new ErgoData(null, 0, 0, yPos, dist, 0, 0, 0, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                            if(myAttribute.get(GRADE))
                                myErgoDatastore.add(new ErgoData(null, 0, 0, 0, dist, 0, 0, yPos, 0, 0, (new SimpleDateFormat("HH:mm:ss")).format((new Date(i*myErgoDatastore.getSampleRate() - 3600000))), false));
                        }
                    }
                    calcLines();
                    this.repaint();
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e)
    {
    	drawMarker((int)e.getPoint().getX(), (int)e.getPoint().getY(), myErgoDatastore.getUseDistance());
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        lastX = -1;
//        lastY = -1;

        if(myErgoDatastore != null)
        {
        	if(!myErgoDatastore.getUseDistance())
        	{
	            setPreferredSize(new Dimension(myErgoDatastore.size(), getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
	            setSize(new Dimension(myErgoDatastore.size(), getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
        	}
        	else
        	{
        		int distance = (int)Math.floor(((ErgoData)myErgoDatastore.get(myErgoDatastore.size()-1)).getDistance()*100+0.5);
        		setPreferredSize(new Dimension(distance, getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
	            setSize(new Dimension(distance, getHeight() - ErgoTools.HEIGHT_SCROLLBAR));        		
        	}
        }
        else
        {
            setPreferredSize(new Dimension(0, getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
            setSize(new Dimension(0, getHeight() - ErgoTools.HEIGHT_SCROLLBAR));
        }
    }

    public void mouseDragged(MouseEvent e)
    {
        drawMarker((int)e.getPoint().getX(), (int)e.getPoint().getY(), myErgoDatastore.getUseDistance());
        lastX = (int)e.getPoint().getX();
//        lastY = (int)e.getPoint().getY();
    }

    public void mouseMoved(MouseEvent e) {}

    public void addErgoLineGraphListener(ErgoLineGraphListener listener)
    {
        myListenerList.add(ErgoLineGraphListener.class,  listener);
    }

    public void removeErgoLineGraphListener(ErgoLineGraphListener listener)
    {
        myListenerList.remove(ErgoLineGraphListener.class,  listener);
    }

    private void fireDataAvailable(ErgoLineGraphEvent evt, ErgoData data)
    {
        Object[] listeners = myListenerList.getListenerList();

        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==ErgoLineGraphListener.class) {
                ((ErgoLineGraphListener)listeners[i+1]).dataAvailable(evt, data);
            }
        }
    }

    /*public int getMarker()
    {
        return myMarker;
    }*/
    
    public void setMarker(Hashtable<String,ErgoData> dataArray)
    {
    	myMarkers = dataArray;
    }

    //public void setMarker(int newMark)
    public void setMarker(int newMark, boolean bDist)
    {
    	//myMarker = newMark;
    	if(bDist)
    		myDistMarker = newMark;
    	else
    		myTimeMarker = newMark;
    	
        //if(myErgoDatastore.getUseDistance() && myMarker > 0)
    	if(bDist && myErgoDatastore.getUseDistance() && myDistMarker > 0)
        {
        	int newMarker = myDistMarker;
        	
        	if(myDistMarker >= myErgoDatastore.size())
        		newMarker = myErgoDatastore.size()-1;
        	
        	ErgoData data = myErgoDatastore.get(newMarker);
        	if(Math.floor((data.getDistance()*100)+0.5) < myDistMarker)
        	{
        		do
        		{
        			newMarker++;
        			data = myErgoDatastore.get(newMarker);
        		}
        		while(Math.floor((data.getDistance()*100)+0.5) < myDistMarker);
        	}
        	else if(Math.floor((data.getDistance()*100)+0.5) > myDistMarker)
        	{
        		do
        		{
        			newMarker--;
        			data = myErgoDatastore.get(newMarker);
        		}
        		while(Math.floor((data.getDistance()*100)+0.5) > myDistMarker);        		
        	}
        	myDistMarker = newMarker;
        }
    }
}