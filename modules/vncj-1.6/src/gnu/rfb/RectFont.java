package gnu.rfb;

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

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;
import java.util.*;

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
