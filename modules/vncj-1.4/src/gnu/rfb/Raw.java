package gnu.rfb;

import java.awt.*;
import java.io.*;

/**
* Raw encoding.
**/

public class Raw extends Rect
{
	//
	// Attributes
	//
	
	public PixelFormat pixelFormat;
	public byte[] bytes;
	
	//
	// Construction
	//
	
	public Raw( int[] pixels, PixelFormat pixelFormat, int offsetX, int offsetY, int scanline, int x, int y, int w, int h )
	{
		super( x, y, w, h );
		this.pixelFormat = pixelFormat;
		
		// Encode as bytes
		int b = 0;
		int i = 0;
		int s = 0;
		int pixel;
		int size = w * h;
		int jump = scanline - w;
		int p = ( y - offsetY ) * scanline + x - offsetX;
		switch( pixelFormat.bitsPerPixel )
		{
		case 32:
			bytes = new byte[ size << 2 ];
			for( ; i < size; i++, s++, p++ )
			{
				if( s == w )
				{
					s = 0;
					p += jump;
				}
				pixel = pixelFormat.translatePixel( pixels[p] );
				bytes[b++] = (byte)( pixel & 0xFF );
				bytes[b++] = (byte)( ( pixel >> 8 ) & 0xFF );
				bytes[b++] = (byte)( ( pixel >> 16 ) & 0xFF );
				bytes[b++] = (byte)( ( pixel >> 24 ) & 0xFF );
			}
			break;
		case 16:
			bytes = new byte[ size << 1 ];
			for( ; i < size; i++, s++, p++ )
			{
				if( s == w )
				{
					s = 0;
					p += jump;
				}
				pixel = pixelFormat.translatePixel( pixels[p] );
				bytes[b++] = (byte)( pixel & 0xFF );
				bytes[b++] = (byte)( ( pixel >> 8 ) & 0xFF );
			}
			break;
		case 8:
			bytes = new byte[ size ];
			for( ; i < size; i++, s++, p++ )
			{
				if( s == w )
				{
					s = 0;
					p += jump;
				}
				bytes[i] = (byte) pixelFormat.translatePixel( pixels[p] );
			}
			break;
		}
	}
	
	public Raw( int x, int y, int w, int h, PixelFormat pixelFormat, byte[] bytes )
	{
		super( x, y, w, h );
		this.pixelFormat = pixelFormat;
		this.bytes = bytes;
	}
	
	//
	// Rect
	//

	public void writeData( DataOutput output ) throws IOException
	{
		super.writeData( output );
		output.writeInt( rfb.EncodingRaw );
		output.write( bytes );
	}

	//
	// Object
	//
	
	public Object clone() throws CloneNotSupportedException
	{
		return new Raw( x, y, w, h, pixelFormat, (byte[]) bytes.clone() );
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
}
