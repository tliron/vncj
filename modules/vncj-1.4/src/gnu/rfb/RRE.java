package gnu.rfb;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
* RRE - Rise-and-Run-length Encoding.
* <p>
* We have an RRE header structure
* giving the number of subrectangles following. Finally the data follows in
* the form ([bgpixel][subrect][subrect]...) where each [subrect] is
* ([pixel][rfbRectangle]).
**/

public class RRE extends Rect
{
	//
	// Attributes
	//
	
	public PixelFormat pixelFormat;
	public int bgpixel;
	public SubRect subrects[];
	
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

	public RRE( int[] pixels, PixelFormat pixelFormat, int offsetX, int offsetY, int scanline, int x, int y, int w, int h )
	{
		super( x, y, w, h );
		this.pixelFormat = pixelFormat;
		
		pixels = copyPixels( pixels, scanline, x - offsetX, y - offsetY, w, h );
		
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
		bgpixel = getBackground( pixels, w, 0, 0, w, h );
	
		for( currentY = 0; currentY < h; currentY++ )
		{
			line = currentY * w;
			for( currentX = 0; currentX < w; currentX++ )
			{
				if( pixels[line + currentX] != bgpixel )
				{
					currentPixel = pixels[line + currentX];
					firstY = currentY - 1;
					firstYflag = true;
					for( runningY = currentY; runningY < h; runningY++ )
					{
						segment = runningY * w;
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
							pixels[ runningY * w + runningX ] = bgpixel;
				}
			}
		}
		
		subrects = new SubRect[ vector.size() ];
		vector.toArray( (Object[]) subrects );
	}

	public RRE( int x, int y, int w, int h, PixelFormat pixelFormat, int bgpixel, SubRect[] subrects )
	{
		super( x, y, w, h );
		this.pixelFormat = pixelFormat;
		this.bgpixel = bgpixel;
		this.subrects = subrects;
	}
	
	//
	// Rect
	//
	
	public void writeData( DataOutput output ) throws IOException
	{
		super.writeData( output );
		output.writeInt( rfb.EncodingRRE );
		output.writeInt( subrects.length );
		writePixel( output, pixelFormat, bgpixel );
		for( int i = 0; i < subrects.length; i++ )
		{
			writePixel( output, pixelFormat, subrects[i].pixel );
			output.writeShort( subrects[i].x );
			output.writeShort( subrects[i].y );
			output.writeShort( subrects[i].w );
			output.writeShort( subrects[i].h );
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
		
		return new RRE( x, y, w, h, pixelFormat, bgpixel, subrectsClone );
	}
}
