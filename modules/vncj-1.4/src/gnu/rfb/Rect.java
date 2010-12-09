package gnu.rfb;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
* Rectangle of pixels that can be written to a stream. Base class for RFB encodings.
**/

public abstract class Rect implements Cloneable
{
	//
	// Static operations
	//
	
	public static int bestEncoding( int[] encodings )
	{
		for( int i = 0; i < encodings.length; i++ )
		{
			switch( encodings[i] )
			{
			case rfb.EncodingRaw:
			case rfb.EncodingRRE:
			case rfb.EncodingCoRRE:
			case rfb.EncodingHextile:
				return encodings[i];
			}
		}
		
		// List does not include a supported encoding
		return rfb.EncodingHextile;
	}
	
	public static Rect encode( int encoding, PixelFormat pixelFormat, Image image, int x, int y, int w, int h )
	{
		// Grab pixels
		int pixels[] = new int[ w * h ];
		PixelGrabber grabber = new PixelGrabber( image, x, y, w, h, pixels, 0, w );
		try
		{
			grabber.grabPixels();
		}
		catch( InterruptedException e )
		{
		}
		
		return encode( encoding, pixels, pixelFormat, x, y, w, x, y, w, h );
	}
	
	public static Rect encode( int encoding, int[] pixels, PixelFormat pixelFormat, int scanline, int x, int y, int w, int h )
	{
		return encode( encoding, pixels, pixelFormat, 0, 0, scanline, x, y, w, h );
	}
	
	public static Rect encode( int encoding, int[] pixels, PixelFormat pixelFormat, int offsetX, int offsetY, int scanline, int x, int y, int w, int h )
	{
		switch( encoding )
		{
		case rfb.EncodingRaw:
			return new Raw( pixels, pixelFormat, offsetX, offsetY, scanline, x, y, w, h );
		case rfb.EncodingCopyRect:
			return null;
		case rfb.EncodingRRE:
			return new RRE( pixels, pixelFormat, offsetX, offsetY, scanline, x, y, w, h );
		case rfb.EncodingCoRRE:
			return new CoRREStub( pixels, pixelFormat, offsetX, offsetY, scanline, x, y, w, h );
		case rfb.EncodingHextile:
			return new Hextile( pixels, pixelFormat, offsetX, offsetY, scanline, x, y, w, h );
		default:
			return null;
		}
	}

	//
	// Attributes
	//
	
	public int x;
	public int y;
	public int w;
	public int h;
	public int count = 1;
	
	//
	// Construction
	//
	
	public Rect( int x, int y, int w, int h )
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	//
	// Operations
	//
	
	public void writeData( DataOutput output ) throws IOException
	{
		output.writeShort( x );
		output.writeShort( y );
		output.writeShort( w );
		output.writeShort( h );
	}
	
	public void transform( int transformX, int transformY )
	{
		x += transformX;
		y += transformY;
	}
	
	//
	// Object
	//
	
	public String toString()
	{
		return String.valueOf( x ) + "," + y + "," + w + "," + h;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException( "Rect not cloneable" );
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	protected static int[] copyPixels( int[] pixels, int scanline, int x, int y, int w, int h )
	{
		int size = w * h;
		int[] ourPixels = new int[ size ];
		int jump = scanline - w;
		int s = 0;
		int p = y * scanline + x;
		for( int i = 0; i < size; i++, s++, p++ )
		{
			if( s == w )
			{
				s = 0;
				p += jump;
			}
			ourPixels[i] = pixels[p];
		}
		
		return ourPixels;
	}
	
	protected static void writePixel( DataOutput output, PixelFormat pixelFormat, int pixel ) throws IOException
	{
		pixel = pixelFormat.translatePixel( pixel );
		
		switch( pixelFormat.bitsPerPixel )
		{
		case 32:
			output.writeByte( pixel & 0xFF );
			output.writeByte( ( pixel >> 8 ) & 0xFF );
			output.writeByte( ( pixel >> 16 ) & 0xFF );
			output.writeByte( ( pixel >> 24 ) & 0xFF );
			break;
		case 16:
			output.writeByte( pixel & 0xFF );
			output.writeByte( ( pixel >> 8 ) & 0xFF );
			break;
		case 8:
			output.writeByte( pixel & 0xFF );
			break;
		}
	}

	protected static int getBackground( int pixels[], int scanline, int x, int y, int w, int h )
	{
		return pixels[ y * scanline + x ];
	
		/*int runningX, runningY, k;
		int counts[] = new int[256];
		
		int maxcount = 0;
		int maxclr = 0;
		
		if( bitsPerPixel == 16 )
			return pixels[0];
		else if( bitsPerPixel == 32 )
			return pixels[0];
		
		// For 8-bit	
		return pixels[0];
		
		for( runningX = 0; runningX < 256; runningX++ )
			counts[runningX] = 0;
		
		for( runningY = 0; runningY < pixels.length; runningY++ )
		{
			k = pixels[runningY];
			if( k >= counts.length )
			{
				return 0;
			}
			counts[k]++;
			if( counts[k] > maxcount )
			{
				maxcount = counts[k];
				maxclr = pixels[runningY];
			}
		}
		
		return maxclr;*/
	}
}
