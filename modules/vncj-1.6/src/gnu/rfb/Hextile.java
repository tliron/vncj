package gnu.rfb;

/**
* Hextile Encoding.
* <p>
* The rectangle is divided up into 'tiles' of 16x16 pixels,
* starting at the top left going in left-to-right, top-to-bottom order. If
* the width of the rectangle is not an exact multiple of 16 then the width of
* the last tile in each row will be correspondingly smaller.  Similarly if the
* height is not an exact multiple of 16 then the height of each tile in the
* final row will also be smaller.  Each tile begins with a "subencoding" type
* byte, which is a mask made up of a number of bits.  If the Raw bit is set
* then the other bits are irrelevant; w*h pixel values follow (where w and h
* are the width and height of the tile).  Otherwise the tile is encoded in a
* similar way to RRE, except that the position and size of each subrectangle
* can be specified in just two bytes.  The other bits in the mask are as
* follows:
* <p>
* BackgroundSpecified - if set, a pixel value follows which specifies
*  the background colour for this tile.  The first non-raw tile in a
*  rectangle must have this bit set.  If this bit isn't set then the
*  background is the same as the last tile.
* <p>
* ForegroundSpecified - if set, a pixel value follows which specifies
*  the foreground colour to be used for all subrectangles in this tile.
*  If this bit is set then the SubrectsColoured bit must be zero.
* <p>
* AnySubrects - if set, a single byte follows giving the number of
*  subrectangles following.  If not set, there are no subrectangles (i.e.
*  the whole tile is just solid background colour).
* <p>
* SubrectsColoured - if set then each subrectangle is preceded by a pixel
*  value giving the colour of that subrectangle.  If not set, all
*  subrectangles are the same colour, the foreground colour;  if the
*  ForegroundSpecified bit wasn't set then the foreground is the same as
*  the last tile.
* <p>
* The position and size of each subrectangle is specified in two bytes.
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

public class Hextile extends Rect
{
	//
	// Attributes
	//
	
	public PixelFormat pixelFormat;
	public Object tiles[]; // each element either Tile or byte[]
	
	public static class Tile
	{
		public int bgpixel;
		public SubRect[] subrects;
	}
	
	public static class SubRect
	{
		public int pixel;
		public int x;
		public int y;
		public int w;
		public int h;
	}
	
	//
	// Construction
	//
	
	public Hextile( int[] pixels, PixelFormat pixelFormat, int offsetX, int offsetY, int scanline, int x, int y, int w, int h )
	{
		super( x, y, w, h );
		this.pixelFormat = pixelFormat;
		
		int workPixels[] = copyPixels( pixels, scanline, x - offsetX, y - offsetY, w, h );

		Vector vector = new Vector();
		int currentX, currentY;
		int tileW, tileH;
		Tile tile;
		int tileMaxSize;
		
		int pixelSize = pixelFormat.bitsPerPixel >> 3; // div 8
		
		// Maximum size of raw tile
		int rawMaxSize = pixelSize << 8; // * 16 * 16
		
		for( currentY = 0; currentY < h; currentY += 16 )
		{
			for( currentX = 0; currentX < w; currentX += 16 )
			{
				// Tile size
				tileW = w - currentX;
				if( tileW > 16 )
					tileW = 16;
				tileH = h - currentY;
				if( tileH > 16 )
					tileH = 16;
				
				tile = tile( workPixels, w, currentX, currentY, tileW, tileH );
				tileMaxSize = tile.subrects.length * ( 2 + pixelSize ) + ( 2 * pixelSize ) + 1;
				if( tileMaxSize < rawMaxSize )
				{
					vector.addElement( tile );
				}
				else
				{
					// Tile may be too large to be efficient, better use raw instead
					vector.addElement( raw( pixels, scanline, currentX + x, currentY + y, tileW, tileH ) );
					//System.err.print("!");
				}
			}
		}

		tiles = new Object[ vector.size() ];
		vector.toArray( (Object[]) tiles );
	}
	
	public Hextile( int x, int y, int w, int h, PixelFormat pixelFormat, Object[] tiles )
	{
		super( x, y, w, h );
		this.pixelFormat = pixelFormat;
		this.tiles = tiles;
	}
	
	//
	// Rect
	//
	
	public void writeData( DataOutput output ) throws IOException
	{
		super.writeData( output );
		output.writeInt( rfb.EncodingHextile );
		
		Tile tile;
		int mask;
		int oldBgpixel = 0x10000000;
		int fgpixel = 0x10000000;
		int j;
		for( int i = 0; i < tiles.length; i++ )
		{
			if( tiles[i] instanceof Tile )
			{
				tile = (Tile) tiles[i];
				mask = 0;

				// Do we have subrects?				
				if( tile.subrects.length > 0 )
				{
					// We have subrects
					mask |= rfb.HextileAnySubrects;
					
					// Do all subrects have the same pixel?
					fgpixel = tile.subrects[0].pixel;
					for( j = 1; j < tile.subrects.length; j++ )
					{
						if( tile.subrects[j].pixel != fgpixel )
						{
							// Subrects are of varying colours
							mask |= rfb.HextileSubrectsColoured;
							break;
						}
					}
					
					if( ( mask & rfb.HextileSubrectsColoured ) == 0 )
					{
						// All subrects have the same pixel
						mask |= rfb.HextileForegroundSpecified;
					}
				}
					
				// Has the background changed?
				if( tile.bgpixel != oldBgpixel )
				{
					oldBgpixel = tile.bgpixel;
					mask |= rfb.HextileBackgroundSpecified;
				}
	
				output.writeByte( mask );
	
				// Background pixel
				if( ( mask & rfb.HextileBackgroundSpecified ) != 0 )
				{
					writePixel( output, pixelFormat, tile.bgpixel );
				}
				
				// Foreground pixel
				if( ( mask & rfb.HextileForegroundSpecified ) != 0 )
				{
					writePixel( output, pixelFormat, fgpixel );
				}
				
				// Subrects
				if( ( mask & rfb.HextileAnySubrects ) != 0 )
				{
					output.writeByte( tile.subrects.length );
					
					if( ( mask & rfb.HextileSubrectsColoured ) != 0 )
					{
						// Subrects coloured
						for( j = 0; j < tile.subrects.length; j++ )
						{
							writePixel( output, pixelFormat, tile.subrects[j].pixel );
							output.writeByte( ( tile.subrects[j].x << 4 ) | tile.subrects[j].y );
							output.writeByte( ( ( tile.subrects[j].w - 1 ) << 4 ) | ( tile.subrects[j].h - 1 ) );
						}
					}
					else
					{
						for( j = 0; j < tile.subrects.length; j++ )
						{
							output.writeByte( ( tile.subrects[j].x << 4 ) | tile.subrects[j].y );
							output.writeByte( ( ( tile.subrects[j].w - 1 ) << 4 ) | ( tile.subrects[j].h - 1 ) );
						}
					}
				}
			}
			else
			{
				output.writeByte( rfb.HextileRaw );
				output.write( (byte[]) tiles[i] );
			}
		}
	}

	//
	// Object
	//
	
	public Object clone() throws CloneNotSupportedException
	{
		Object[] tilesClone = new Object[ tiles.length ];
		int j;
		Tile tileClone, tile;
		for( int i = 0; i < tiles.length; i++ )
		{
			if( tiles[i] instanceof Tile )
			{
				tile = (Tile) tiles[i];
				tileClone = new Tile();
				tilesClone[i] = tileClone;
				tileClone.bgpixel = tile.bgpixel;
				tileClone.subrects = new SubRect[ tile.subrects.length ];
				for( j = 0; j < tile.subrects.length; j++ )
				{
					tileClone.subrects[j] = new SubRect();
					tileClone.subrects[j].pixel = tile.subrects[j].pixel;
					tileClone.subrects[j].x = tile.subrects[j].x;
					tileClone.subrects[j].y = tile.subrects[j].y;
					tileClone.subrects[j].w = tile.subrects[j].w;
					tileClone.subrects[j].h = tile.subrects[j].h;
				}
			}
			else
			{
				tilesClone[i] = ((byte[]) tiles[i]).clone();
			}
		}
		
		return new Hextile( x, y, w, h, pixelFormat, tilesClone );
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private Tile tile( int[] pixels, int scanline, int x, int y, int w, int h )
	{
		Tile tile = new Tile();

		SubRect subrect;
		Vector vector = new Vector();
		
		int currentPixel;
		int currentX, currentY;
		int runningX, runningY;
		int firstX = 0, firstY, firstW, firstH;
		int secondX = 0, secondY, secondW, secondH;
		boolean firstYflag;
		int segment;
		int line;
		tile.bgpixel = getBackground( pixels, scanline, x, y, w, h );
	
		for( currentY = 0; currentY < h; currentY++ )
		{
			line = ( currentY + y ) * scanline + x;
			for( currentX = 0; currentX < w; currentX++ )
			{
				if( pixels[line + currentX] != tile.bgpixel )
				{
					currentPixel = pixels[line + currentX];
					firstY = currentY - 1;
					firstYflag = true;
					for( runningY = currentY; runningY < h; runningY++ )
					{
						segment = ( runningY + y ) * scanline + x;
						if( pixels[segment + currentX] != currentPixel )
							break;
						runningX = currentX;
						while( ( runningX < w ) && ( pixels[segment + runningX] == currentPixel ) )
							runningX++;
						runningX--;
						if( runningY == currentY )
							secondX = firstX = runningX;
						if( runningX < secondX )
							secondX = runningX;
						if( firstYflag && ( runningX >= firstX ) )
							firstY++;
						else
							firstYflag = false;	
					}
					secondY = runningY - 1;
					
					firstW = firstX - currentX + 1;
					firstH = firstY - currentY + 1;
					secondW = secondX - currentX + 1;
					secondH = secondY - currentY + 1;
					
					subrect = new SubRect();
					vector.addElement( subrect );
					subrect.pixel = currentPixel;
					subrect.x = currentX;
					subrect.y = currentY;
						
					if( ( firstW * firstH ) > ( secondW * secondH ) )
					{
						subrect.w = firstW;
						subrect.h = firstH;
					}
					else
					{
						subrect.w = secondW;
						subrect.h = secondH;
					}
				
					for( runningY = subrect.y; runningY < ( subrect.y + subrect.h ); runningY++ )
						for( runningX = subrect.x; runningX < ( subrect.x + subrect.w ); runningX++ )
							pixels[ ( runningY + y ) * scanline + x + runningX ] = tile.bgpixel;
				}
			}
		}
		
		tile.subrects = new SubRect[ vector.size() ];
		vector.toArray( (Object[]) tile.subrects );

		return tile;
	}

	private byte[] raw( int[] pixels, int scanline, int x, int y, int w, int h )
	{
		byte[] bytes = null;
		int b = 0;
		int i = 0;
		int s = 0;
		int pixel;
		int size = w * h;
		int jump = scanline - w;
		int p = y * scanline + x;
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
		
		return bytes;
	}
}
