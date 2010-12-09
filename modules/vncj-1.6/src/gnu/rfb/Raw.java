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
import java.io.*;

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
