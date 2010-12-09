package vncjdemo;

import gnu.vnc.awt.*;
import gnu.awt.*;

import java.awt.*;

public class AWTModelTest extends VNCDesktop
{
	//
	// Construction
	//
	
	public AWTModelTest( int display, String displayName, int width, int height )
	{
		super( displayName, width, height );

		addFrame();
		addFrame();
		addFrame();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private int openFrameCount = 0;
	private int xOffset = 30, yOffset = 30;
	
	private void addFrame()
	{
		// Create frame
		Frame frame = new ToolkitFrame( this, "Editor [" + openFrameCount + "]" );
		frame.setLocation( xOffset * openFrameCount, yOffset * openFrameCount++ );
		frame.setSize( 300, 200 );
		
		// Populate it
		TextArea text = new TextArea( "This is an AWT Frame\ncontaining an AWT TextArea." );
		frame.add( text );
		
		// Show it
		frame.show();
	}
}

