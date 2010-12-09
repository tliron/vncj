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

public interface RFBClient
{
	// Attributes
	
	public PixelFormat getPixelFormat();
	public String getProtocolVersionMsg();
	public boolean getShared();
	public int getPreferredEncoding();
	public void setPreferredEncoding( int encoding );
	public int[] getEncodings();
	
	// Messages from server to client
	
	public void writeFrameBufferUpdate( Rect rects[] ) throws IOException;
	public void writeSetColourMapEntries( int firstColour, Colour colours[] ) throws IOException;
	public void writeBell() throws IOException;
	public void writeServerCutText( String text ) throws IOException;
        public void setUpdateIsAvailable(boolean value);
	
	// Operations

	public void close() throws IOException;
}
