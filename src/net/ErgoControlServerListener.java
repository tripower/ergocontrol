package net;

import java.util.EventListener;

public interface ErgoControlServerListener extends EventListener 
{
	public void finished(long threadId);
}
