package gnu.rfb.server;

import gnu.rfb.*;

import java.util.*;
import java.io.*;

/**
* Manages a group of RFB clients with individual properties.
**/

public class RFBClients
{
	//
	// Construction
	//
	
	public RFBClients()
	{
	}
	
	//
	// Operations
	//
	
	public boolean isEmpty()
	{
		return clients.isEmpty();
	}
	
	public void addClient( RFBClient client )
	{
		clients.put( client, new Hashtable() );
	}
	
	public void removeClient( RFBClient client )
	{
		clients.remove( client );
	}
	
	public void closeAll()
	{
		RFBClient client;
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			client = (RFBClient) e.nextElement();
			try
			{
				client.close();
			}
			catch( IOException x )
			{
			}
		}
		
		clients.clear();
	}
	
	public Enumeration elements()
	{
		return clients.keys();
	}
	
	public void setProperty( RFBClient client, String key, Object value )
	{
		Hashtable properties = (Hashtable) clients.get( client );
		if( properties == null )
			return;
		
		properties.put( key, value );	
	}
	
	public Object getProperty( RFBClient client, String key )
	{
		Hashtable properties = (Hashtable) clients.get( client );
		if( properties == null )
			return null;
		
		return properties.get( key );	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	Hashtable clients = new Hashtable();
}
