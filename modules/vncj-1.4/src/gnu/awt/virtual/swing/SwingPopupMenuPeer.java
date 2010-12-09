package gnu.awt.virtual.swing;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT popup menu peer implemented as a {@link javax.swing.JPopupMenu}.
**/

class SwingPopupMenuPeer extends JPopupMenu implements PopupMenuPeer
{
	//
	// Construction
	//
	
	public SwingPopupMenuPeer( PopupMenu popupMenu )
	{
		super();
	}
	
	//
	// PopupMenuPeer
	//
	
	public void show( Event e )
	{
	}
	
	//
	// PopupMenuPeer
	//
	
	public void addItem( MenuItem item )
	{
	}
	
	public void delItem( int index )
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
