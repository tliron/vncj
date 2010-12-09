package gnu.rfb;

/**
* CoRRE - Compact RRE Encoding.
* <p>
* We have an RRE header structure giving
* the number of subrectangles following. Finally the data follows in the form
* [<bgpixel><subrect><subrect>...] where each <subrect> is
* [<pixel><rfbCoRRERectangle>]. This means that
* the whole rectangle must be at most 255x255 pixels.
* <p>
*
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
import java.util.*;

public class CoRRE extends RRE
{
	//
	// Construction
	//

	public CoRRE( int[] pixels, PixelFormat pixelFormat, int offsetX, int offsetY, int scanline, int x, int y, int w, int h )
	{
		super( pixels, pixelFormat, offsetX, offsetY, scanline, x, y, w, h );
	}
	
	public CoRRE( int x, int y, int w, int h, PixelFormat pixelFormat, int bgpixel, SubRect[] subrects )
	{
		super( x, y, w, h, pixelFormat, bgpixel, subrects );
	}
	
	//
	// Rect
	//

	public void writeData( DataOutput output ) throws IOException
	{
		output.writeShort( x );
		output.writeShort( y );
		output.writeShort( w );
		output.writeShort( h );
		output.writeInt( rfb.EncodingCoRRE );
		output.writeInt( subrects.length );
		writePixel( output, pixelFormat, bgpixel );
		for( int i = 0; i < subrects.length; i++ )
		{
			writePixel( output, pixelFormat, subrects[i].pixel );
			output.writeByte( subrects[i].x );
			output.writeByte( subrects[i].y );
			output.writeByte( subrects[i].w );
			output.writeByte( subrects[i].h );
		}
	}

	//
	// Object
	//
	
	public Object clone() throws CloneNotSupportedException
	{
		SubRect[] subrectsClone = new SubRect[ subrects.length ];
		for( int i = 0; i < subrects.length; i++ )
		{
			subrectsClone[i] = new SubRect();
			subrectsClone[i].pixel = subrects[i].pixel;
			subrectsClone[i].x = subrects[i].x;
			subrectsClone[i].y = subrects[i].y;
			subrectsClone[i].w = subrects[i].w;
			subrectsClone[i].h = subrects[i].h;
		}
		
		return new CoRRE( x, y, w, h, pixelFormat, bgpixel, subrectsClone );
	}
}
