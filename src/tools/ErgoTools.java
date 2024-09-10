package tools;

import java.awt.*;
import java.util.Properties;
import java.util.Vector;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 21.02.2005
 * Time: 09:20:07
 * To change this template use File | Settings | File Templates.
 */
public class ErgoTools {
	public final static String VERSION = "V2.28";
	public final static String BUILD_DATE = "November 03, 2007";

    public final static String DEFAULT_NAME = "recording";

    public final static int PORT = 13746;

    public final static Color COLOR_PULS = Color.RED;
    public final static Color COLOR_RPM = Color.ORANGE;
    public final static Color COLOR_SPEED = Color.BLUE;
    public final static Color COLOR_DISTANCE = Color.BLACK;
    public final static Color COLOR_POWER = Color.GRAY;
    public final static Color COLOR_KILOJOULE = Color.BLACK;
    public final static Color COLOR_TIME = Color.BLACK;
    public final static Color COLOR_HEIGHT = Color.GREEN;
    public final static Color COLOR_REALTIME = Color.BLACK;
    public final static Color COLOR_GRADE = Color.BLACK;
    public final static Color COLOR_GEAR = Color.BLACK;
    public final static Color COLOR_RING = Color.BLACK;

    public final static Color COLOR_MARKER = Color.YELLOW;
    public final static Color COLOR_MARKER2 = Color.YELLOW.brighter();
    public final static Color COLOR_MARKER2SIM = Color.YELLOW.darker();
    public final static Color COLOR_MAP = Color.YELLOW;

    public final static Color COLOR_DISTMARKER = Color.BLACK;
    public final static Color COLOR_DISTMARKER2 = Color.GREEN;
    public final static Color COLOR_DISTMARKER2SIM = Color.RED;
    public final static Color COLOR_TIMEMARKER = Color.LIGHT_GRAY;

    public final static String LABEL_PULS = "Puls";
    public final static String LABEL_RPM = "Cadence";
    public final static String LABEL_SPEED = "Speed";
    public final static String LABEL_DISTANCE = "Distance";
    public final static String LABEL_TOGO = "To Go";
    public final static String LABEL_POWER = "Power";
    public final static String LABEL_KILOJOULE = "Kilojoule";
    public final static String LABEL_TIME = "Time";
    public final static String LABEL_HEIGHT = "Height";
    public final static String LABEL_REALTIME = "RealTime";
    public final static String LABEL_GRADE = "Grade";
    public final static String LABEL_GEAR = "Gear";
    public final static String LABEL_RING = "Ring";

    public final static String LABEL_PULS_UNIT = "bpm";
    public final static String LABEL_RPM_UNIT = "rpm";
    public final static String LABEL_SPEED_UNIT = "km/h";
    public final static String LABEL_DISTANCE_UNIT = "km";
    public final static String LABEL_POWER_UNIT = "Watt";
    public final static String LABEL_HEIGHT_UNIT = "m";
    public final static String LABEL_KILOJOULE_UNIT = "kJ";
    public final static String LABEL_GRADE_UNIT = "%";

    public final static int MAX_PULS = 250;
    public final static int MIN_PULS = 60;
    public final static int MIN_PULS_STEP = 5;
    public final static int PULS_STEP = 25;

    public final static int MAX_RPM = 180;
    public final static int MIN_RPM = 1;
    public final static int MIN_RPM_STEP = 5;
    public final static int RPM_STEP = 20;

    public final static int MAX_SPEED = 100;
    public final static int MIN_SPEED = 1;
    public final static int MIN_SPEED_STEP = 1;
    public final static int SPEED_STEP = 10;

    public final static int MAX_POWER = 600;
    public final static int MIN_POWER = 25;
    public final static int MIN_POWER_STEP = 5;
    public final static int POWER_STEP = 50;

    public final static int MAX_HEIGHT = 1000;
    public final static int MIN_HEIGHT = 0;
    public final static int MIN_HEIGHT_STEP = 1;
    public final static int HEIGHT_STEP = 100;

    public final static int HEIGHT_SCROLLBAR = 15;

    public final static int TIMEOUT = 1000;

    public final static int INITIAL_FONT_SIZE = 24;
    public final static int INITIAL_FONT_LOWER_DIVIDER = 4;
    public static int DEFAULT_FONT_SIZE = INITIAL_FONT_SIZE;
    public static int DEFAULT_FONT_SIZE_LOWER = DEFAULT_FONT_SIZE / INITIAL_FONT_LOWER_DIVIDER;
    public static int DEFAULT_FONT_SIZE_FULL = DEFAULT_FONT_SIZE + DEFAULT_FONT_SIZE_LOWER;
    public static Font DEFAULT_FONT = new Font("Comic", Font.BOLD, DEFAULT_FONT_SIZE);

    public final static int SHADOW_OFFSET = 2;

    public static String myHTTPUrl = null;

    public static void resizeFont(double factor)
    {
        DEFAULT_FONT_SIZE = (int)(INITIAL_FONT_SIZE * factor);
        DEFAULT_FONT = new Font("Comic", Font.BOLD, DEFAULT_FONT_SIZE);
        DEFAULT_FONT_SIZE_LOWER = (int)(DEFAULT_FONT.getSize2D() / INITIAL_FONT_LOWER_DIVIDER);
        DEFAULT_FONT_SIZE_FULL = (int)(DEFAULT_FONT.getSize2D() + DEFAULT_FONT_SIZE_LOWER);
    }

    public static String subTabGet(String inString, int pos)
    {
        return subCharGet(inString, pos, "\t");
    }

    public static String subCharGet(String inString, int pos, char delim)
    {
        return subCharGet(inString, pos, "" + delim);
    }

    public static String subCharGet(String inString, int pos, String delim)
    {
        String newString = null;
        int curPos = 0;
        int lastIndex = 0;

        while(curPos <= pos)
        {
            int index = inString.indexOf(delim, lastIndex);
            if(curPos == pos)
            {
                if(index == -1)
                    index = inString.length();

                newString = inString.substring(lastIndex, index);
            }
            else if(index == -1)
                curPos = pos+1;

            lastIndex = index+delim.length();
            curPos++;
        }

        return newString;
    }

    public static String getLines(String inString, int startLine, int endLine)
    {
        String newString = null;
        String line = null;
        String delim = "\r\n";
        int linePos = 0;

        if(inString.indexOf(delim) == -1)
            delim = "\n";
        if(inString.indexOf(delim) == -1)
            return inString;

        do
        {
            line = subCharGet(inString, linePos, delim);
            if(linePos >= startLine && linePos <= endLine)
            {
                if(newString == null)
                    newString = line;
                else
                    newString += delim + line;
            }
            linePos++;
        }
        while(line != null && linePos <= endLine);

        return newString;
    }

    public static boolean checkCharacters(String inString, String characters)
    {
        int pos = 0;
        int length = characters.length();
        do
        {
            if(pos < length)
            {
                String nextChar = characters.substring(pos, pos+1);
                if(inString.indexOf(nextChar) >= 0 )
                    return false;
            }
            pos++;
        }
        while(pos < length);
        return true;
    }

    public static Vector sort(Vector v) {
        Object[] arr = v.toArray();
        Arrays.sort(arr);
        Vector result=new Vector();
        for(int i=0; i<arr.length; i++) {
            result.addElement(arr[i]);
        }
        return result;
    }

    public static String getHTTPUrl(Properties prop)
    {
    	if(myHTTPUrl == null)
    		myHTTPUrl = prop.getProperty("ServerURL");

    	return myHTTPUrl;
    }
}
