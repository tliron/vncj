package gnu.rfb;

/**
* RFB constants and utilities.
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
