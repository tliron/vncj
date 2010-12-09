package gnu.vnc.awt.swing;

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

import gnu.vnc.awt.*;
import gnu.awt.virtual.*;
import gnu.awt.virtual.swing.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class VNCJFrame extends VirtualJFrame implements RFBServer
{
	//
	// Construction
	//
	
	public VNCJFrame( String name, int width, int height )
	{
		super( name );

		events = new VNCEvents( this, clients );
		repaintManager = new VNCRepaintManager( this, clients );
		VNCRepaintManager.setCurrentManager( repaintManager );
		
		// VNC frames cannot change size
		setSize( width, height );
		super.setResizable( false );
		
		setVisible(true);
	}
	
	//
	// JFrame
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
	//	if( clients.isEmpty() && !shared )
	//		dispose();
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
	
	public void setShared( boolean shared ) throws IOException
	{
		if( shared )
			this.shared = true;
	}
	
	public void setPixelFormat( RFBClient client, PixelFormat pixelFormat ) throws IOException
	{
		pixelFormat.setDirectColorModel( (DirectColorModel) 
				getToolkit().getColorModel() );
	}
	
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}
	
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}
	
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		repaintManager.frameBufferUpdate( client, incremental, x, y, w, h );
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

        public void setShared(RFBClient client, boolean shared) throws IOException {
        }
        
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private RFBClients clients = new RFBClients();
	private VNCEvents events;
	private boolean shared = false;
	private VNCRepaintManager repaintManager;
}

