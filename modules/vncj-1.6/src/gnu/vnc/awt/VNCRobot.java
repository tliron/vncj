package gnu.vnc.awt;

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
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class VNCRobot extends Component implements RFBServer
{
	//
	// Construction
	//
	
	public VNCRobot( int display, String displayName )
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
		//return 200;
		return device.getDefaultConfiguration().getBounds().width;
	}
	
	public int getFrameBufferHeight( RFBClient client )
	{
		//return 200;
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
		int vk = keysym.toVKall( key );
		if( vk != 0 )
		{
			if( down )
				robot.keyPress( vk );
			else	
				robot.keyRelease( vk );
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

        public boolean isUpdateAvailable(RFBClient client){
            return false;
        }
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private String displayName;
	private GraphicsDevice device;
	private Robot robot;
	private int mouseModifiers = 0;
}

