package gnu.awt.virtual.swing;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT button peer implemented as a {@link javax.swing.JButton}.
**/

class SwingButtonPeer extends JButton implements ButtonPeer
{
	//
	// Construction
	//
	
	public SwingButtonPeer( Button button )
	{
		super();
		SwingFramePeer.add( button, this );
	}
	
	//
	// ComponentPeer
	//
	
	// Events
	
	public void handleEvent( AWTEvent e )
	{
		//System.err.println(e);
	}
	
	public void coalescePaintEvent( PaintEvent e )
	{
		System.err.println(e);
	}
	
	public boolean handlesWheelScrolling()
	{
		return false;
	}
	
	// Obscurity
	
	public boolean isObscured()
	{
		return false;
	}
	
	public boolean canDetermineObscurity()
	{
		return false;
	}
	
	// Focus
	
	public boolean requestFocus( Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time )
	{
		return true;
	}
	
	// Buffer
	
	public void createBuffers( int x, BufferCapabilities bufferCapabilities )
	{
	}
	
	public void destroyBuffers()
	{
	}
	
	public void flip( BufferCapabilities.FlipContents flipContents )
	{
	}
	
	public Image getBackBuffer()
	{
		return null;
	}
	
	// Cursor
	
	public void updateCursorImmediately()
	{
	}
	
	// Misc
	
	public void dispose()
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
}
