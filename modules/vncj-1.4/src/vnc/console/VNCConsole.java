package gnu.vnc.console;

import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
* Base class for console emulators supporting multiple RFB clients.
**/

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
		new Thread( this, "VNCConsole-" + displayName ).start();
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
		int[] mask = new int[2];
		keysym.toMask( key, mask );
		if( mask[0] != KeyEvent.VK_UNDEFINED )
			return;
		
		if( down )
		{
			int[] vk = new int[2];
			keysym.toVK( key, vk );
			if( vk[0] == KeyEvent.VK_UNDEFINED )
				// Standard key
				buffer.input( key );
			else
				// Virtual key
				buffer.inputVK( vk[0] );	
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

