package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import data.ErgoData;
import data.ErgoDatastore;

import tools.ErgoTools;

public class ErgoControlClient 
{
	private static Logger log = Logger.getLogger(ErgoControlClient.class);
	private Socket server = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	
	private String id = null;
	private String errMsg = "";
	
	private static Hashtable<String,ErgoData> myObjects = null;
	
	public boolean connect(String host, int port, String name)
	{
		try
		{
			InetAddress address = InetAddress.getByName(host); 
			
			if(server == null || !server.getInetAddress().getAddress().equals(address.getAddress()))
			{
				log.debug("Try to connect to server <" + address.getHostName() + "/" + address.getHostAddress() + ">...");
				id = null;
				
				server = new Socket(address, port);
				server.setSoTimeout(ErgoTools.TIMEOUT);
				
				oos = new ObjectOutputStream(server.getOutputStream());
				ois = new ObjectInputStream(server.getInputStream());
				
				log.debug("Send connect");
				oos.write(1);
				oos.writeUTF(name);
				oos.writeUTF(ErgoTools.VERSION);
				oos.flush();
				log.debug("Wait for answer...");
				id = ois.readUTF();
				
				if(id.length() == 0)
				{
					errMsg += "\nInvalid Version!";
					id = null;
				}
				
				log.debug("Connection status = " + isConnected());
			}
		}
		catch(Exception ex)
        {
			if(ex.getMessage() != null)
        		errMsg += "\n" + ex.getMessage();
        	log.error(ex);
        }
		
		return isConnected();
	}

	public boolean disconnect() throws IOException
	{
		boolean b = true;
		
		log.debug("Try to disconnect from server...");
		
		if(isConnected())
		{
			log.debug("Send disconnect");
			oos.write(2);
			oos.writeUTF(id);
			oos.flush();
			log.debug("Wait for answer...");
			b = ois.readBoolean();			
		}
		
		if(oos != null)
		{
			oos.close();
			oos = null;
		}
		if(ois != null)
		{
			ois.close();
			ois = null;
		}
		if(server != null)
		{
			server.close();
			server = null;
		}
		
		log.debug("Status = " + b);
		return b;
	}
	
	public Hashtable<String,ErgoData> send(ErgoData sendData)
	{
		try
		{
			log.debug("Try to send data to server...");
			
			if(isConnected())
			{
				log.debug("Send data");
				oos.write(3);
				oos.writeUTF(id);
				oos.writeObject(sendData);
				oos.flush();
				log.debug("Wait for answer...");
				myObjects = (Hashtable<String,ErgoData>)ois.readObject();
			}
			
			if(myObjects != null)
				log.debug("Got " + myObjects.size() + " Objects" );
			else
				log.debug("Got no Objects" );
		}
		catch(Exception ex)
		{
			log.error(ex);
		}		
		
		return myObjects;
	}
	
	public ErgoDatastore getProgram()
	{
		ErgoDatastore program = null;
		try
		{
			log.debug("Try to get program from server...");
			
			if(isConnected())
			{
				log.debug("Send request");
				oos.write(4);
				oos.writeUTF(id);
				oos.flush();
				log.debug("Wait for answer...");
				program = (ErgoDatastore)ois.readObject();
			}
		}
		catch(Exception ex)
        {
        	if(ex.getMessage() != null)
        		errMsg += "\n" + ex.getMessage();
        	log.error(ex);
        }
		
		return program;
	}
	
	public boolean isConnected()
	{
		boolean bConnect = (id != null);
		
		return bConnect;
	}
	
	public String getErrorMessage()
	{
		return errMsg;
	}
}
