package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import data.ErgoDatastore;

import tools.ErgoTools;

public class ErgoControlServer extends Thread implements ErgoControlServerListener
{
	private static Logger log = Logger.getLogger(ErgoControlServer.class);
	private ServerSocket serverSocket = null;
	private Hashtable<Long,ErgoControlServerThread> threads = new Hashtable<Long,ErgoControlServerThread>();
	private ErgoDatastore myErgoDatastoreProgram = null;
	private int myPort = ErgoTools.PORT;
	private String myName = "default";
	private String myHost = "127.0.0.1";
	private boolean myIsLocal = false;
	
	private boolean bRun = true;
	
	public ErgoControlServer(String host, String name, ErgoDatastore dataStore, int inPort, boolean bLocal)
	{
		myHost = host;
		myName = name;
		myErgoDatastoreProgram = dataStore;
		if(inPort != 0)
			myPort = inPort;
		myIsLocal = bLocal;
	}
	
	public synchronized void run() 
	{
		log.debug("Server started...");
		ErgoControlServerThread.reset();
		try
		{
			serverSocket = new ServerSocket(myPort);
			serverSocket.setSoTimeout(ErgoTools.TIMEOUT);
			
			while(bRun)
			{
				Socket client = null;
				try
				{
					client = serverSocket.accept();
				}
				catch(Exception ex)
				{
					//log.error(ex);
				}
	
				if(client != null)
				{
					log.debug("Process client request...");
					ErgoControlServerThread serverThread = new ErgoControlServerThread(client, myErgoDatastoreProgram);
					threads.put(new Long(serverThread.getId()), serverThread);
					serverThread.addErgoControlServerListener(this);
					serverThread.start();
				}
				else
					Thread.sleep(10);
			}
			serverSocket.close();
			serverSocket = null;
		}
		catch(Exception ex)
		{
			log.debug(ex);
		}
		log.debug("Server stopped");
	}
	
	public void cancel() throws IOException
	{
		bRun = false;
	}
		
	public boolean getIsRunning()
	{
		return bRun;
	}

	public void stopThreads()
	{
		Enumeration<ErgoControlServerThread> enumThreads = threads.elements();
		while(enumThreads.hasMoreElements())
		{
			ErgoControlServerThread serverThread = enumThreads.nextElement();
			
			serverThread.cancel();
		}
		threads.clear();
	}

	public void finished(long threadId) 
	{
		threads.remove(new Long(threadId));
	}
	
	public int getPort()
	{
		if(serverSocket != null)
			return serverSocket.getLocalPort();
		else
			return 0;
	}
	
	public String getHost()
	{
		return myHost;
	}
	
	public String getServerName()
	{
		return myName;
	}
	
	public boolean getIsLocal()
	{
		return myIsLocal;
	}
}
