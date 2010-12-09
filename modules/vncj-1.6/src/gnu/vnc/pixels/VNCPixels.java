package gnu.vnc.pixels;

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

import gnu.vnc.*;
import gnu.rfb.*;
import gnu.rfb.server.*;
import gnu.awt.*;

import java.io.*;
import java.util.*;

public class VNCPixels implements RFBServer, PixelsOwner
{

 /**
  * VNCEvents to forward events to
  */
 private gnu.vnc.awt.VNCEvents events = null;

 public void setVNCEventsHandler(gnu.vnc.awt.VNCEvents events)
 {
  this.events = events;
 }

 public gnu.vnc.awt.VNCEvents getVNCEventsHandler()
 {
  return events;
 }



	//
	// Construction
	//

	public VNCPixels( String name, int width, int height )
	{
		this.name = name;
		this.width = width;
		this.height = height;

		queue = new VNCQueue( clients );

		pixelArray = new int[ width * height ];
	}

        /**
         * externalize queue to manually add entries
         * @return
         */
        public VNCQueue getQueue()
        {
         return(queue);
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

        public RFBClients getClients()
        {
         return(clients);
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
	}

	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}

	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}

	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
            queue.takeSnapshot(this);
	    queue.frameBufferUpdate( client, incremental, x, y, w, h);
	}

	public void keyEvent( RFBClient client, boolean down, int key ) throws IOException
	{
//         System.err.println("DEBUG[VNCPixels] keyEvent");
        if( events!=null)
        {
            events.translateKeyEvent(client, down, key);
        }
        else
        {
        	updateAll();
        }
	}

	public void pointerEvent( RFBClient client, int buttonMask, int x, int y ) throws IOException
	{
//         System.err.println("DEBUG[VNCPixels] pointerEvent");
        if( events!=null) {
            events.translatePointerEvent(client, buttonMask, x, y);
        }
        else
        {
        	updateAll();
        }
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
	public void updateAll() throws IOException{
		for( Enumeration e = clients.elements(); e.hasMoreElements(); ){
			RFBClient client = (RFBClient)e.nextElement();
			frameBufferUpdateRequest( client, false, 0, 0, width, height );
		}
		
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private String name;
	private int width;
	private int height;
	private RFBClients clients = new RFBClients();
	private boolean shared = false;
	private int[] pixelArray = null;
	protected VNCQueue queue;
}

