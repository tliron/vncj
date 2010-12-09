package gnu.rfb;

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;
import java.util.*;

/**
* Library of {@link gnu.rfb.Rect RFB rectangles} created from an AWT font.
**/

public class RectFont extends RectLibrary
{
	//
	// Construction
	//
	
	public RectFont( Font font, Dimension charSize, char[] chars, PixelFormat pixelFormat, int encoding, DirectColorModel colorModel )
	{
		init( font, charSize, chars, pixelFormat, encoding, colorModel );
	}
	
	public RectFont( Font font, Dimension charSize, PixelFormat pixelFormat, int encoding, DirectColorModel colorModel )
	{
		char[] chars = new char[256];
		for( int c = 0; c < chars.length; c++ )
			chars[c] = (char) c;
			
		init( font, charSize, chars, pixelFormat, encoding, colorModel );
	}
	
	public RectFont( Font font, Dimension charSize, PixelFormat pixelFormat, int encoding )
	{
		this( font, charSize, pixelFormat, encoding, new DirectColorModel( ColorSpace.getInstance( ColorSpace.CS_sRGB ), 24, 0xFF0000, 0xFF00, 0xFF, 0, true, DataBuffer.TYPE_INT ) );
	}
	
	public RectFont( Dimension charSize, PixelFormat pixelFormat, int encoding )
	{
		this( new Font( "monospaced", Font.PLAIN, charSize.height ), charSize, pixelFormat, encoding );
	}
	
	//
	// Operations
	//
	
	public DirectColorModel getDirectColorModel()
	{
		return colorModel;
	}
	
	public Rect getRect( char c )
	{
		return super.getRect( c );
	}

	public Rect getRect( char c, int originX, int originY )
	{
		return super.getRect( c, originX, originY );
	}
	
	public Rect[] getRects( String string, int originX, int originY )
	{
		int length = string.length();
		Rect[] rects = new Rect[ length ];
		for( int i = 0; i < length; i++ )
		{
			rects[i] = getRect( string.charAt( i ), originX, originY );
			originX += charSize.width;
		}
		return rects;
	}
	
	public Rect[] getRects( char[] chars, int offset, int length, int originX, int originY, int xLimit )
	{
		Rect[] rects = new Rect[ length ];
		int x = originX;
		int y = originY;
		for( int i = offset; i < offset + length; i++ )
		{
			rects[i] = getRect( chars[ i ], x, y );
			x += charSize.width;
			if( x >= xLimit )
			{
				x = originX;
				y += charSize.height;
			}
		}
		return rects;
	}
	
	public Rect[] getRects( char[] chars, boolean[] valid, int offset, int length, int originX, int originY, int xLimit )
	{
		Vector rects = new Vector();
		int x = originX;
		int y = originY;
		Rect rect;
		for( int i = offset; i < offset + length; i++ )
		{
			if( !valid[i] )
			{
				rect = getRect( chars[i], x, y );
				if( rect != null )
					rects.addElement( rect );
				
				valid[i] = true;
			}
			
			x += charSize.width;
			if( x >= xLimit )
			{
				x = originX;
				y += charSize.height;
			}
		}

		// Convert to array
		Rect[] array = new Rect[ rects.size() ];
		rects.toArray( array );
		return array;
	}
	
	public void putRect( char c, Rect rect )
	{
		super.putRect( c, rect );
	}
	
	public Dimension getCharSize()
	{
		return charSize;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private Dimension charSize = new Dimension();
	private PixelFormat pixelFormat;
	private DirectColorModel colorModel;
	
	private void init( Font font, Dimension charSize, char[] chars, PixelFormat pixelFormat, int encoding, DirectColorModel colorModel )
	{
		this.charSize = charSize;
		this.pixelFormat = pixelFormat;
		this.colorModel = colorModel;
		
		// Pixel data
		int[] pixels = new int[ charSize.width * charSize.height ];
		DataBuffer dataBuffer = new DataBufferInt( pixels, pixels.length );
		
		// Sample model
		SampleModel sampleModel = new SinglePixelPackedSampleModel( DataBuffer.TYPE_INT, charSize.width, charSize.height, colorModel.getMasks() );

		// Raster				
		WritableRaster raster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
		
		// Image
		BufferedImage image = new BufferedImage( colorModel, raster, true, null );
		Graphics g = image.getGraphics();
		g.setFont( font );
		g.setColor( Color.yellow );
		FontMetrics fontMetrics = g.getFontMetrics();
		int baseline = charSize.height - fontMetrics.getDescent();
		
		Rect rect;
		int ii;
		for( int i = 0; i < chars.length; i++ )
		{
			if( font.canDisplay( chars[i] ) )
			{
				// Clean
				for( ii = 0; ii < pixels.length; ii++ )
					pixels[ii] = 0;
				
				// Draw
				g.drawChars( chars, i, 1, 0, baseline );
				
				// Encode
				rect = Rect.encode( encoding, pixels, pixelFormat, charSize.width, 0, 0, charSize.width, charSize.height );
				putRect( chars[i], rect );
			}
		}

		// Null rect
		putRect( 0, getRect( ' ' ) );
		
		// Default
		putDefaultRect( getRect( '?' ) );
	}
}
