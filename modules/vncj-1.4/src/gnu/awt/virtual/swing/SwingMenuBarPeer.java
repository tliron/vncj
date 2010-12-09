package gnu.awt.virtual.swing;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT menu bar peer implemented as a {@link javax.swing.JMenuBar}.
**/

class SwingMenuBarPeer extends JMenuBar implements MenuBarPeer
{
	//
	// Construction
	//
	
	public SwingMenuBarPeer( MenuBar menuBar )
	{
		super();
	}
	
	//
	// MenuBarPeer
	//
	
	public void addMenu( Menu m )
	{
	}
	
	public void delMenu( int index )
	{
	}
	
	public void addHelpMenu( Menu m )
	{
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
