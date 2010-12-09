package gnu.swing.virtual;

import gnu.awt.*;
import gnu.swing.*;

import java.awt.*;

import javax.swing.*;

/**
* JFC frame that is a {@link gnu.awt.PixelsOwner}. The actual pixel array would probably be managed
* by an underlying {@link gnu.awt.virtual.VirtualFramePeer} created by a
* {@link gnu.awt.virtual.VirtualToolkit}.
**/

public class VirtualJFrame extends ToolkitJFrame implements PixelsOwner
{
	//
	// Construction
	//
	
	public VirtualJFrame( Toolkit toolkit, String name )
	{
		super( toolkit, name );
	}
	
	public VirtualJFrame( Toolkit toolkit )
	{
		super( toolkit );
	}
	
	//
	// PixelsOwner
	//
	
	public int[] getPixels()
	{
		return pixelArray;
	}
	
	public void setPixelArray( int[] pixelArray, int pixelWidth, int pixelHeight )
	{
		this.pixelArray = pixelArray;
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
	}
	
	public int getPixelWidth()
	{
		return pixelWidth;
	}
	
	public int getPixelHeight()
	{
		return pixelHeight;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private int[] pixelArray = null;
	private int pixelWidth = -1;
	private int pixelHeight = -1;
}
