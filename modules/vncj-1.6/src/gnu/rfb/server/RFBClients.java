package gnu.rfb.server;

/**
* <br><br><center><table border="1" width="80%"><hr>
* <strong><a href="http://www.amherst.edu/~tliron/vncj">VNCj</a></strong>
* <p>
* Copyright (C) 2000-2002 by Tal Liron
* <p>
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public License
* as published by the Free Software Foundation; either version 2.1
* of the License, or (at your option) any later version.
* <p>
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* <a href="http://www.gnu.org/copyleft/lesser.html">GNU Lesser General Public License</a>
* for more details.
* <p>
* You should have received a copy of the <a href="http://www.gnu.org/copyleft/lesser.html">
* GNU Lesser General Public License</a> along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
* <hr></table></center>
**/

import gnu.rfb.*;

import java.util.*;
import java.io.*;

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
