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
import java.io.*;

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
//         System.err.println("DEBUG[Rect] encode("+w+" x "+h+") pixels[0]="+pixels[0]);
         if(w==0)
         if(h==0)
           {
            Exception e = new Exception("w==h==0");
            e.printStackTrace();
           }
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
