package gnu.rfb;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
* CoRRE - Compact RRE Encoding.
* <p>
* We have an RRE header structure giving
* the number of subrectangles following. Finally the data follows in the form
* [bgpixel][subrect][subrect]... where each [subrect] is
* [pixel][rfbCoRRERectangle]. This means that
* the whole rectangle must be at most 255x255 pixels.
**/

public class CoRREStub extends Rect
{
	//
	// Attributes
	//
	
	public CoRRE rects[];
	
	//
	// Construction
	//

	public CoRREStub( int[] pixels, PixelFormat pixelFormat, int offsetX, int offsetY, int scanline, int x, int y, int w, int h )
	{
		super( x, y, w, h );
		
		CoRRE rect;
		Vector vector = new Vector();
		
		if( ( w <= 0xFF ) && ( h <= 0xFF ) )
		{
			rect = new CoRRE( pixels, pixelFormat, offsetX, offsetY, scanline, x, y, w, h );
			vector.addElement( rect );
		}
		else
		{
			int currentX, currentY, currentW, currentH;
			for( currentY = 0; currentY < h; currentY += 0xFF )
			{
				for( currentX = 0; currentX < w; currentX += 0xFF )
				{
					currentW = w - currentX;
					currentH = h - currentY;
					
					if( currentW > 0xFF )
						currentW = 0xFF;
					if( currentH > 0xFF )
						currentH = 0xFF;
						
					rect = new CoRRE( pixels, pixelFormat, offsetX, offsetY, scanline, x + currentX, y + currentY, currentW, currentH );
					vector.addElement( rect );
				}
			}
		}

		rects = new CoRRE[ vector.size() ];
		vector.toArray( (Object[]) rects );
		count = rects.length;
	}
	
	public CoRREStub( int x, int y, int w, int h, CoRRE[] rects )
	{
		super( x, y, w, h );
		this.rects = rects;
	}
	
	//
	// Rect
	//

	public void writeData( DataOutput output ) throws IOException
	{
		for( int i = 0; i < rects.length; i++ )
			rects[i].writeData( output );
	}

	public void transform( int transformX, int transformY )
	{
		for( int i = 0; i < rects.length; i++ )
			rects[i].transform( transformX, transformY );
	}
	
	//
	// Object
	//
	
	public Object clone() throws CloneNotSupportedException
	{
		CoRRE[] rectsClone = new CoRRE[ rects.length ];
		for( int i = 0; i < rects.length; i++ )
			rectsClone[i] = (CoRRE) rects[i].clone();
		
		return new CoRREStub( x, y, w, h, rectsClone );
	}
}
