package gnu.vnc.awt;

import gnu.awt.virtual.*;
import gnu.awt.virtual.swing.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
* AWT toolkit implemented entirely with JFC peers supporting multiple RFB client, thus allowing
* a lightweight remote simulation of the operating system desktop.
**/

public class VNCDesktop extends VirtualDesktop implements RFBServer
{
	//
	// Construction
	//
	
	public VNCDesktop( int bitsPerPixel, int rMask, int gMask, int bMask, String title, int width, int height )
	{
		super( bitsPerPixel, rMask, gMask, bMask, title, width, height );
		init();
	}
	
	public VNCDesktop( String title, int width, int height )
	{
		super( title, width, height );
		init();
	}
	
	//
	// VirtualDesktop
	//
	
	public void dispose()
	{
		clients.closeAll();
		super.dispose();
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
		return desktopFrame.getTitle();
	}
	
	public int getFrameBufferWidth( RFBClient client )
	{
		Insets insets = desktopFrame.getInsets();
		return desktopFrame.getWidth() - insets.left - insets.right;
	}
	
	public int getFrameBufferHeight( RFBClient client )
	{
		Insets insets = desktopFrame.getInsets();
		return desktopFrame.getHeight() - insets.top - insets.bottom;
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
		pixelFormat.setDirectColorModel( (DirectColorModel) getColorModel() );
	}
	
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}
	
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}

	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		VNCRepaintManager.currentManager().frameBufferUpdate( desktopFrame, client, incremental, x, y, w, h );
	}
	
	public void keyEvent( RFBClient client, boolean down, int key ) throws IOException
	{
		events.translateKeyEvent( client, down, key );
	}
	
	public void pointerEvent( RFBClient client, int buttonMask, int x, int y ) throws IOException
	{
		events.translatePointerEvent( client, buttonMask, x, y );
	}
	
	public void clientCutText( RFBClient client, String text ) throws IOException
	{
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private RFBClients clients = new RFBClients();
	private VNCEvents events;
	private boolean shared = false;
	
	private void init()
	{
		events = new VNCEvents( desktopFrame, clients );
		VNCRepaintManager.currentManager().manage( desktopFrame, clients );
		
		// VNC frames cannot change size
		desktopFrame.setResizable( false );
		
		show();
	}
}

