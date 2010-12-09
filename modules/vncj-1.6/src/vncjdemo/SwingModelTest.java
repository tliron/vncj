package vncjdemo;

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

import gnu.vnc.awt.swing.*;
import gnu.awt.virtual.*;

import java.awt.*;

import javax.swing.*;

public class SwingModelTest extends VNCJFrame
{
	//
	// Construction
	//
	
	public SwingModelTest( int display, String displayName )
	{
		super( displayName, 500, 400 );

		desktopPane = new JDesktopPane();
		getContentPane().add( desktopPane );
		
		addFrame();
		addFrame();
		addFrame();
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private JDesktopPane desktopPane;
	private int openFrameCount = 0;
	private int xOffset = 30, yOffset = 30;
	
	private void addFrame()
	{
		// Create frame
		JInternalFrame frame = new JInternalFrame( "Editor [" + openFrameCount + "]", true, false, true, true );
		desktopPane.add( frame );
		
		// Populate it
		JTextArea text = new JTextArea( "This is a Swing JInternalFrame\ncontaining a Swing JTextArea." );
		frame.getContentPane().add( new JScrollPane( text ) );
		
		// Show it
		frame.setLocation( xOffset * openFrameCount, yOffset * openFrameCount++ );
		frame.setSize( 300, 200 );
		frame.setVisible( true );
	}
}
