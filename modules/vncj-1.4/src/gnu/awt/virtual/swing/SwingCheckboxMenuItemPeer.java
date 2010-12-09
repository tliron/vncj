package gnu.awt.virtual.swing;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT checkbox menu item peer implemented as a {@link javax.swing.JCheckBoxMenuItem}.
**/

class SwingCheckboxMenuItemPeer extends JCheckBoxMenuItem implements CheckboxMenuItemPeer
{
	//
	// Construction
	//
	
	public SwingCheckboxMenuItemPeer( CheckboxMenuItem checkBoxMenuItem )
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
