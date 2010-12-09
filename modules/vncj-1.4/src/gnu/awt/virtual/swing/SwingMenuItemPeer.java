package gnu.awt.virtual.swing;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT menu item peer implemented as a {@link javax.swing.JMenuItem}.
**/

class SwingMenuItemPeer extends JMenuItem implements MenuItemPeer
{
	//
	// Construction
	//
	
	public SwingMenuItemPeer( MenuItem menuItem )
	{
		super();
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
	
	// Misc
	
	public void dispose()
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
}
