package gnu.vnc.awt;

import gnu.swing.virtual.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import javax.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
* A {@link gnu.swing.virtual.VirtualJFrame virtual JFC frame} that supports multiple RFB clients.
**/

public class VNCJFrame extends VirtualJFrame implements RFBServer
{
	//
	// Construction
	//
	
	public VNCJFrame( Toolkit toolkit, String name, int width, int height )
	{
		super( toolkit, name );
		
		events = new VNCEvents( this, clients );
		VNCRepaintManager.currentManager().manage( this, clients );
		
		// VNC frames cannot change size
		setSize( width, height );
		setResizable( false );
		
		show();
	}
	
	//
	// JFrame
	//
	
	public void dispose()
	{
		VNCRepaintManager.currentManager().unmanage( this );
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
		return getTitle();
	}
	
	public int getFrameBufferWidth( RFBClient client )
	{
		Insets insets = getInsets();
		return getWidth() - insets.left - insets.right;
	}
	
	public int getFrameBufferHeight( RFBClient client )
	{
		Insets insets = getInsets();
		return getHeight() - insets.top - insets.bottom;
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
		pixelFormat.setDirectColorModel( (DirectColorModel) getToolkit().getColorModel() );
	}
	
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}
	
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}
	
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		VNCRepaintManager.currentManager().frameBufferUpdate( this, client, incremental, x, y, w, h );
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
}

