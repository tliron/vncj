package gnu.vnc.awt;

import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/**
* A very limited implementation of a {@link java.awt.Robot} that supports RFB clients.
**/

public class VNCRobot extends Component implements RFBServer
{
	//
	// Construction
	//
	
	public VNCRobot( int display, String displayName, int width, int height )
	{
		this.displayName = displayName;
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		try
		{
			robot = new Robot( device );
		}
		catch( AWTException x )
		{
		}
	}
	
	//
	// RFBServer
	//
	
	// Clients
	
	public void addClient( RFBClient client )
	{
	}
	
	public void removeClient( RFBClient client )
	{
	}
	
	// Attributes
	
	public String getDesktopName( RFBClient client )
	{
		return displayName;
	}
	
	public int getFrameBufferWidth( RFBClient client )
	{
		return device.getDefaultConfiguration().getBounds().width;
	}
	
	public int getFrameBufferHeight( RFBClient client )
	{
		return device.getDefaultConfiguration().getBounds().height;
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
	}
	
	public void setPixelFormat( RFBClient client, PixelFormat pixelFormat ) throws IOException
	{
		pixelFormat.setDirectColorModel( (DirectColorModel) Toolkit.getDefaultToolkit().getColorModel() );
	}
	
	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
	}
	
	public void fixColourMapEntries( RFBClient client, int firstColour, Colour[] colourMap ) throws IOException
	{
	}
	
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		// If you really really want automatic refreshes, comment out the following two lines.
		// BEWARE, it will send the entire screen, and probably be unusably slow. For now,
		// you must "request screen refresh" manually from your VNC viewer.
		if( incremental )
			return;
		
		// Create image
		BufferedImage image = robot.createScreenCapture( new Rectangle( x, y, w, h ) );
		
		// Encode image
		Rect r = Rect.encode( client.getPreferredEncoding(), client.getPixelFormat(), image, x, y, w, h );
		
		// Write to client
		Rect[] rects = { r };
		try
		{
			client.writeFrameBufferUpdate( rects );
		}
		catch( IOException xx )
		{
			xx.printStackTrace();
		}
	}
	
	public void keyEvent( RFBClient client, boolean down, int key ) throws IOException
	{
		int[] vk = new int[2];
		keysym.toVKall( key, vk );
		if( vk[0] != KeyEvent.VK_UNDEFINED )
		{
			if( down )
				robot.keyPress( vk[0] );
			else	
				robot.keyRelease( vk[0] );
		}	
	}
	
	public void pointerEvent( RFBClient client, int buttonMask, int x, int y ) throws IOException
	{
		// Modifiers		
		int newMouseModifiers = 0;
		if( ( buttonMask & rfb.Button1Mask ) != 0 )
			newMouseModifiers |= MouseEvent.BUTTON1_MASK;
		if( ( buttonMask & rfb.Button2Mask ) != 0 )
			newMouseModifiers |= MouseEvent.BUTTON2_MASK;
		if( ( buttonMask & rfb.Button3Mask ) != 0 )
			newMouseModifiers |= MouseEvent.BUTTON3_MASK;
		
		if( newMouseModifiers != mouseModifiers )
		{
			// Change of button state
			if( mouseModifiers == 0 )
			{
				robot.keyPress( newMouseModifiers );
			}
			else
			{
				robot.keyRelease( newMouseModifiers );
			}
			
			mouseModifiers = newMouseModifiers;
		}
		
		robot.mouseMove( x, y );
	}
	
	public void clientCutText( RFBClient client, String text ) throws IOException
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private String displayName;
	private GraphicsDevice device;
	private Robot robot;
	private int mouseModifiers = 0;
}

