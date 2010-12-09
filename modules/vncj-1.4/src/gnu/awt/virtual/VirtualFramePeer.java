package gnu.awt.virtual;

import gnu.awt.*;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

/**
* Virtual frame peer. Satisfies the requirements for AWT frame peers without actually displaying
* anything (hence, it is virtual). It manages its own {@link java.awt.Graphics} by creating
* a {@link java.awt.image.Raster} (single pixel packed sample model) that can be accessed by
* a {@link gnu.awt.PixelsOwner}. Virtual frame peers are created by a {@link gnu.awt.virtual.VirtualToolkit}
* for frames that implement {@link gnu.awt.PixelsOwner}.
**/

public class VirtualFramePeer extends VirtualComponentPeer implements FramePeer
{
	//
	// Construction
	//
	
	public VirtualFramePeer( Frame frame, PixelsOwner pixelsOwner )
	{
		super( frame.getToolkit(), frame );
		this.pixelsOwner = pixelsOwner;
	}
	
	//
	// FramePeer
	//

	public void setTitle( String title )
	{
	}
	
	public void setIconImage( Image im )
	{
	}
	
	public void setMenuBar( MenuBar mb )
	{
		// TODO
	}
	
	public void setResizable( boolean resizeable )
	{
	}
	
	public void setState( int state )
	{
		// Always Frame.NORMAL
	}
	
	public int getState()
	{
		return Frame.NORMAL;
	}
	
	public void setMaximizedBounds( java.awt.Rectangle bounds )
	{
	}
	
	//
	// WindowPeer
	//

	public void toFront()
	{
		// Always "in front"
	}
	
	public void toBack()
	{
		// Always "in front"
	}
	
	public int handleFocusTraversalEvent( KeyEvent e )
	{
		return -1;
	}
	
	//
	// ContainerPeer
	//

	public Insets getInsets()
	{
		return insets;
	}

	public void beginValidate()
	{
	}
	
	public void endValidate()
	{
	}

	public void beginLayout()
	{
	}
	
	public void endLayout()
	{
	}
	
	public boolean isPaintPending()
	{
		return false;
	}
	
	public Insets insets()
	{
		return getInsets();
	}
	
	//
	// ComponentPeer
	//
	
	public void setVisible( boolean b )
	{
		if( b == true )
		{
			// First paint
			component.paint( getGraphics() );
		}
	}
	
	public Graphics getGraphics()
	{
		// We will always be writing to the same image
		if( image == null )
		{
			//Thread.dumpStack();
			/*if( size.width == 0 )
				size.width = 100;
			if( size.height == 0 )
				size.height = 100;*/
				
			if( ( size.width > 0 ) && ( size.height > 0 ) )
			{
				// Color model
				DirectColorModel colorModel = (DirectColorModel) getColorModel();
			
				// Pixel data
				int[] pixels = new int[ size.width * size.height ];
				DataBuffer dataBuffer = new DataBufferInt( pixels, pixels.length );
				
				// Sample model
				SampleModel sampleModel = new SinglePixelPackedSampleModel( DataBuffer.TYPE_INT, size.width, size.height, colorModel.getMasks() );

				// Raster				
				WritableRaster raster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
				
				// Image
				image = new BufferedImage( colorModel, raster, true, null );
				
				// Set pixel owner
				pixelsOwner.setPixelArray( pixels, size.width, size.height );
			}
		}
		
		return image.getGraphics();
	}
	
	public Image createImage( int width, int height )
	{
		//System.err.println( "createImage("+width+","+height+")" );
		
		// Color model
		DirectColorModel colorModel = (DirectColorModel) getColorModel();
		
		// Sample model
		SampleModel sampleModel = new SinglePixelPackedSampleModel( DataBuffer.TYPE_INT, width, height, colorModel.getMasks() );
		
		// Raster				
		WritableRaster raster = Raster.createWritableRaster( sampleModel, null );
		
		// Image
		return new BufferedImage( colorModel, raster, true, null );
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private PixelsOwner pixelsOwner;
	private Insets insets = new Insets( 0, 0, 0, 0 );
	private BufferedImage image = null;
}
