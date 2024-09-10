package net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import tools.ErgoTools;

import data.ErgoData;
import data.ErgoDatastore;

public class ErgoControlServerThread extends Thread
{
	private static Logger log = Logger.getLogger(ErgoControlServerThread.class);
	protected EventListenerList myListenerList = new EventListenerList();
	ErgoDatastore myErgoDatastoreProgram = null;
	
	private Socket myClient = null;
	private boolean bRun = true;
	
	private static Hashtable<String,ErgoData> clients = new Hashtable<String,ErgoData>();
	
	public ErgoControlServerThread(Socket client, ErgoDatastore dataStore)
	{
		myClient = client;
		myErgoDatastoreProgram = dataStore;
	}
	
	public static void reset()
	{
		clients = new Hashtable<String,ErgoData>();
	}
	
	public void cancel()
	{
		bRun = false;
	}

	public synchronized void run() 
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(myClient.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(myClient.getOutputStream());
			
			while(bRun)
			{
				log.debug("Client request processing initiated...");
				
				String id = null;
				
				log.debug("get request...");
				int request = ois.read();
				switch(request)
				{
					case 1: //connect
						log.debug("connect requested");
						id = ois.readUTF();
						String version = ois.readUTF();
						if(ErgoTools.VERSION.equals(version))
						{
							if(id == null)
								id = Long.toString(System.currentTimeMillis());
							else
								if(clients.containsKey(id))
								{
									log.debug("Client already connected with id " + id);
									id += Long.toString(System.currentTimeMillis());
								}
	
							clients.put(id, new ErgoData());
							log.debug("Client connected with id " + id);
						}
						else 
						{
							log.debug("Invalid version " + version);
							id = "";
						}
						oos.writeUTF(id);													
						break;
					case 2: //disconnect
						id = ois.readUTF();
						log.debug("disconnect requested for id " + id);
						clients.remove(id);
						oos.writeBoolean(true);
						bRun = false;
						log.debug("Client disconnected with id " + id);					
						break;
					case 3: //send data
						id = ois.readUTF();
						log.debug("send requested for id " + id);
						ErgoData data = (ErgoData)ois.readObject();
						clients.put(id, data);
						log.debug("send data of " + clients.size() + " other client(s)");
						Hashtable<String,ErgoData> dataArray = new Hashtable<String,ErgoData>();
						Enumeration<String> enumClients = clients.keys();
						while(enumClients.hasMoreElements())
						{
							String key = enumClients.nextElement();
						
							log.debug("check if id " + key + " equals id " + id);
							if(!key.equals(id))
								dataArray.put(key,clients.get(key));
						}
						if(dataArray.size() == 0)
							dataArray = null;
							
						oos.writeObject(dataArray);
						break;
					case 4: //request program
						id = ois.readUTF();
						log.debug("program requested for id " + id);
						oos.writeObject(myErgoDatastoreProgram);
						break;
					default:
						log.debug("unknown request " + request);
						bRun = false;
						break;
				}
				oos.flush();
				log.debug("Client request processed");
			}
			
			myClient.close();
		}
		catch(Exception ex)
		{
			log.error(ex);
		}
		
		fireFinished(this.getId());
	}
	
	public void addErgoControlServerListener(ErgoControlServerListener listener)
    {
        myListenerList.add(ErgoControlServerListener.class,  listener);
    }

    public void removeErgoControlServerListener(ErgoControlServerListener listener)
    {
        myListenerList.remove(ErgoControlServerListener.class,  listener);
    }
	
	protected void fireFinished(long threadId)
    {
        Object[] listeners = myListenerList.getListenerList();

        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==ErgoControlServerListener.class) {
                ((ErgoControlServerListener)listeners[i+1]).finished(threadId);
            }
        }

    }
}
