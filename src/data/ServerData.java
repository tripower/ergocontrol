package data;

public class ServerData 
{
	private String myIp = null;
	private int myPort = 0;

	public ServerData(String ip, int port)
	{
		myIp = ip;
		myPort = port;
	}
	
	public int getPort()
	{
		return myPort;
	}
	
	public String getIp()
	{
		return myIp;
	}
}
