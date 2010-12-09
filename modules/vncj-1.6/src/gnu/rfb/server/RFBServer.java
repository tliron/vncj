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

import java.io.*;
import java.awt.event.*;

public interface RFBServer
{
	// Clients
	
	public void addClient( RFBClient client );
	public void removeClient( RFBClient client );
	
	// Attributes
	
	public String getDesktopName( RFBClient client );
	public int getFrameBufferWidth( RFBClient client );
	public int getFrameBufferHeight( RFBClient client );
	public PixelFormat getPreferredPixelFormat( RFBClient client );
	public boolean allowShared();

	// Messages from client to server

	public void setClientProtocolVersionMsg( RFBClient client, String protocolVersionMsg ) throws IOException;
	public void setShared( RFBClient client, boolean shared ) throws IOException;
	public void setPixelFormat( RFBClient client, PixelFormat pixelFormat ) throws IOException;
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException; // not supported
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException;
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException;
	public void keyEvent( RFBClient client, boolean down, int key ) throws IOException;
	public void pointerEvent( RFBClient client, int buttonMask, int x, int y ) throws IOException;
	public void clientCutText( RFBClient client, String text ) throws IOException;
}
