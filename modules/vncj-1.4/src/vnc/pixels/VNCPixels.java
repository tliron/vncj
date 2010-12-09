package gnu.vnc.pixels;

import gnu.vnc.*;
import gnu.rfb.*;
import gnu.rfb.server.*;
import gnu.awt.*;

import java.io.*;
import java.util.*;

/**
* A raw pixel raster supporting multiple RFB clients.
**/

public class VNCPixels implements RFBServer, PixelsOwner
{
	//
	// Construction
	//
	
	public VNCPixels( String name, int width, int height )
	{
		this( name, width, height, 0xFF0000, 0xFF00, 0xFF );
	}
	
	public VNCPixels( String name, int width, int height, int redMask, int greenMask, int blueMask )
	{
		this.name = name;
		this.width = width;
		this.height = height;
		this.redMask = redMask;
		this.greenMask = greenMask;
		this.blueMask = blueMask;
		
		queue = new VNCQueue( clients );
		
		pixelArray = new int[ width * height ];
	}
	
	//
	// Operations
	//
	
	public void dispose()
	{
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
		{
			clients.closeAll();
			dispose();
		}
	}
	
	// Attributes
	
	public String getDesktopName( RFBClient client )
	{
		return name;
	}
	
	public int getFrameBufferWidth( RFBClient client )
	{
		return width;
	}
	
	public int getFrameBufferHeight( RFBClient client )
	{
		return height;
	}
	
	public PixelFormat getPreferredPixelFormat( RFBClient client )
	{
		return PixelFormat.RGB888;
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
		pixelFormat.setMasks( redMask, greenMask, blueMask );
	}
	
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}
	
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}
	
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		queue.frameBufferUpdate( client, incremental, x, y, w, h, this );
	}
	
	public void keyEvent( RFBClient client, boolean down, int key ) throws IOException
	{
	}
	
	public void pointerEvent( RFBClient client, int buttonMask, int x, int y ) throws IOException
	{
	}
	
	public void clientCutText( RFBClient client, String text ) throws IOException
	{
	}
	
	//
	// PixelsOwner
	//
	
	public int[] getPixels()
	{
		return pixelArray;
	}
	
	public void setPixelArray( int[] pixelArray, int pixelWidth, int pixelHeight )
	{
		this.pixelArray = pixelArray;
		this.width = pixelWidth;
		this.height = pixelHeight;
	}
	
	public int getPixelWidth()
	{
		return width;
	}
	
	public int getPixelHeight()
	{
		return height;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private String name;
	private int width;
	private int height;
	private int redMask;
	private int greenMask;
	private int blueMask;
	private RFBClients clients = new RFBClients();
	private boolean shared = false;
	private int[] pixelArray = null;
	protected VNCQueue queue;
}

