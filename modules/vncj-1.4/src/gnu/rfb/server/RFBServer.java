package gnu.rfb.server;

import gnu.rfb.*;

import java.io.*;
import java.awt.event.*;

/**
* To be implemented by RFB servers, which must also define a constructor that accepts
* an integer (for the display number), a string (for the display name), and then two
* integers (suggested width and height).
**/

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
