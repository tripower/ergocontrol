package data;

public class XYPosition 
{
	int myX = 0;
	int myY = 0;
	
	public XYPosition(int newX, int newY)
	{
		myX = newX;
		myY = newY;
	}

	public int getX()
	{
		return myX;
	}
	
	public int getY()
	{
		return myY;
	}
	
	public void setX(int newX)
	{
		myX = newX;
	}
	
	public void setY(int newY)
	{
		myY = newY;
	}
}
