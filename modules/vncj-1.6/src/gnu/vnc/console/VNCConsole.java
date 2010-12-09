package gnu.vnc.console;

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
import gnu.rfb.server.*;

import java.io.*;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public abstract class VNCConsole implements RFBServer, Runnable
{
	//
	// Construction
	//
	
	public VNCConsole( String displayName, int columns, int rows, int charW, int charH )
	{
		this.displayName = displayName;
		this.columns = columns;
		this.rows = rows;
		charSize = new Dimension( charW, charH );

		buffer = new VNCConsoleBuffer( columns, rows, charSize, clients );
		
		in = buffer.inputStream();
		out = buffer.printStream();

		// Start thread
		new Thread( this ).start();
	}
	
	//
	// Operations
	//
	
	public abstract void main();
	
	public void dispose()
	{
		clients.closeAll();
	}
	
	//
	// RFBServer
	//

	// Clients
	
	public void addClient( RFBClient client )
	{
		clients.addClient( client );
	}
	
	public void removeClient( RFBClient client )
	{
		clients.removeClient( client );
		if( clients.isEmpty() && !shared )
			dispose();
	}
	
	// Attributes
	
	public String getDesktopName( RFBClient client )
	{
		return displayName;
	}
	
	public int getFrameBufferWidth( RFBClient client )
	{
		return columns * charSize.width;
	}
	
	public int getFrameBufferHeight( RFBClient client )
	{
		return rows * charSize.height;
	}
	
	public PixelFormat getPreferredPixelFormat( RFBClient client )
	{
		return PixelFormat.BGR233;
	}
	
	public boolean allowShared()
	{
		return true;
	}
	
	// Messages from client to server

	public void setClientProtocolVersionMsg( RFBClient client, String protocolVersionMsg ) throws IOException
	{
	}
	
	public void setShared( RFBClient client, boolean shared ) throws IOException
	{
		if( shared )
			this.shared = true;
	}
	
	public void setPixelFormat( RFBClient client, PixelFormat pixelFormat ) throws IOException
	{
		pixelFormat.setDirectColorModel( buffer.getFont( client ).getDirectColorModel() );
	}
	
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}
	
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}
	
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		client.writeFrameBufferUpdate( buffer.getRects( client, incremental ) );
	}
	
	public void keyEvent( RFBClient client, boolean down, int key ) throws IOException
	{
		// Ignore modifiers
		if( keysym.toMask( key ) != 0 )
			return;
		
		if( down )
		{
			int vk = keysym.toVK( key );
			if( vk == 0 )
				// Standard key
				buffer.input( key );
			else
				// Virtual key
				buffer.inputVK( vk );	
		}
	}
	
	public void pointerEvent( RFBClient client, int buttonMask, int x, int y ) throws IOException
	{
	}
	
	public void clientCutText( RFBClient client, String text ) throws IOException
	{
	}
	

	//
	// Runnable
	//
	
	public void run()
	{
		main();
		dispose();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private String displayName;
	private int columns;
	private int rows;
	private Dimension charSize;
	private VNCConsoleBuffer buffer;
	private RFBClients clients = new RFBClients();
	private boolean shared = false;
	
	protected InputStream in;
	protected PrintStream out;
}

