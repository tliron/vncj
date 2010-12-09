package gnu.rfb;

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

public abstract class rfb
{
	// Handshaking
	public static final String ProtocolVersionMsg = "RFB 003.003\n";
	
	// Authentication
	public static final int ConnFailed = 0;
	public static final int NoAuth = 1;
	public static final int VncAuth = 2;
	public static final int VncAuthOK = 0;
	public static final int VncAuthFailed = 1;
	public static final int VncAuthTooMany = 2;

	// Messages from server to client
	public static final int FrameBufferUpdate = 0;
	public static final int SetColourMapEntries = 1;
	public static final int Bell = 2;
	public static final int ServerCutText = 3;
	
	// Messages from client to server
	public static final int SetPixelFormat = 0;
	public static final int FixColourMapEntries = 1;
	public static final int SetEncodings = 2;
	public static final int FrameBufferUpdateRequest = 3;
	public static final int KeyEvent = 4;
	public static final int PointerEvent = 5;
	public static final int ClientCutText = 6;
	
	// Pointer button masks
	public static final int Button1Mask = 1;
	public static final int Button2Mask = 2;
	public static final int Button3Mask = 4;
	public static final int Button4Mask = 8;
	public static final int Button5Mask = 16;
	
	// Encodings
	public static final int EncodingRaw = 0;
	public static final int EncodingCopyRect = 1;
	public static final int EncodingRRE = 2;
	public static final int EncodingCoRRE = 4;
	public static final int EncodingHextile = 5;

	// Hextile
	public static final int HextileRaw = (1 << 0);
	public static final int HextileBackgroundSpecified = (1 << 1);
	public static final int HextileForegroundSpecified = (1 << 2);
	public static final int HextileAnySubrects = (1 << 3);
	public static final int HextileSubrectsColoured = (1 << 4);

	// Swapping
	
	public static int swapShort( int v )
	{
		return
			( ( v & 0xff ) << 8 ) |
			( ( v >> 8 ) & 0xff );
	}
	
	public static int swapInt( int v )
	{
		return
			( ( v & 0xff000000 ) >> 24 ) |
			( ( v & 0x00ff0000 ) >> 8 ) |
			( ( v & 0x0000ff00 ) << 8 ) |
	 		( ( v & 0x000000ff ) << 24 );
	 }
}
