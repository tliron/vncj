package gnu.rfb.server;

import gnu.rfb.*;

import java.io.*;
import java.net.*;

/**
* To be implemented by RFB client models.
**/

public interface RFBClient
{
	// Attributes
	
	public PixelFormat getPixelFormat();
	public String getProtocolVersionMsg();
	public boolean getShared();
	public int getPreferredEncoding();
	public void setPreferredEncoding( int encoding );
	public int[] getEncodings();
	public InetAddress getInetAddress();
	
	// Messages from server to client
	
	public void writeFrameBufferUpdate( Rect rects[] ) throws IOException;
	public void writeSetColourMapEntries( int firstColour, Colour colours[] ) throws IOException;
	public void writeBell() throws IOException;
	public void writeServerCutText( String text ) throws IOException;
	public void writeConnectionFailed( String text ) throws IOException;
	
	// Operations
	
	public void write( int integer ) throws IOException;
	public void write( byte bytes[] ) throws IOException;
	public void flush() throws IOException;
	public void read( byte bytes[] ) throws IOException;
	public void close() throws IOException;
}
