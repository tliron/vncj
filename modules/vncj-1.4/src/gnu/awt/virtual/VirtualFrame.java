package gnu.awt.virtual;

import gnu.awt.*;

import java.awt.*;

/**
* AWT frame that is a {@link gnu.awt.PixelsOwner}. The actual pixel array would probably be managed
* by an underlying {@link gnu.awt.virtual.VirtualFramePeer} created by a
* {@link gnu.awt.virtual.VirtualToolkit}.
**/

public class VirtualFrame extends ToolkitFrame implements PixelsOwner
{
	//
	// Construction
	//
	
	public VirtualFrame( Toolkit toolkit, String name )
	{
		super( toolkit, name );
	}
	
	public VirtualFrame( Toolkit toolkit )
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

