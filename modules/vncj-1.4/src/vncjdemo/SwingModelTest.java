package vncjdemo;

import gnu.vnc.awt.*;
import gnu.awt.virtual.*;

import java.awt.*;

import javax.swing.*;

public class SwingModelTest extends VNCJFrame
{
	//
	// Construction
	//
	
	public SwingModelTest( int display, String displayName, int width, int height )
	{
		super( new VirtualToolkit(), displayName, width, height );
		
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
