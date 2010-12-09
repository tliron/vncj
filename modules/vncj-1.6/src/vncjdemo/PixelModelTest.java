package vncjdemo;

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

import gnu.vnc.awt.VNCEvents;
import gnu.vnc.pixels.*;
import gnu.awt.virtual.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.util.*;

public class PixelModelTest extends VNCPixels
{
	//
	// Construction
	//
	
	public PixelModelTest( int display, String displayName )
	{
		super( displayName, marginX * 2 + 57 * scale, marginY * 2 + 15 * scale );
		colon( true );
	}
	
	//
	// RFBServer
	//
	
	int b = 0;
	
	public void frameBufferUpdateRequest( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar);
		int hours = calendar.get( Calendar.HOUR_OF_DAY );
		int minutes = calendar.get( Calendar.MINUTE );
		int seconds = calendar.get( Calendar.SECOND );
		digit( 0, hours / 10 );
		digit( 1, hours % 10 );
		digit( 2, minutes / 10 );
		digit( 3, minutes % 10 );
		digit( 4, seconds / 10 );
		digit( 5, seconds % 10 );
		
		super.frameBufferUpdateRequest( client, incremental, x, y, w, h );
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private final static int[] sectionX = { 1, 0, 7, 1, 0, 7, 1 };
	private final static int[] sectionY = { 0, 1, 1, 7, 8, 8, 14 };
	private final static int[] sectionW = { 6, 1, 1, 6, 1, 1, 6 };
	private final static int[] sectionH = { 1, 6, 6, 1, 6, 6, 1 };
	private final static int[] sections = { 119, 36, 93, 109, 46, 107, 123, 37, 127, 111, 0 };
	private final static int marginX = 10;
	private final static int marginY = 10;
	private final static int scale = 5;
	
	private int oldDigit[] = { 10, 10, 10, 10, 10, 10 };
	private int pixel = 255;
	
	private void colon( boolean on )
	{
		bar( marginX + 18 * scale, marginY + scale * 2,  scale, 2 * scale, on ? pixel: 0 );
		bar( marginX + 18 * scale, marginY + scale * 10, scale, 2 * scale, on ? pixel: 0 );
		bar( marginX + 38 * scale, marginY + scale * 2,  scale, 2 * scale, on ? pixel: 0 );
		bar( marginX + 38 * scale, marginY + scale * 10, scale, 2 * scale, on ? pixel: 0 );
	}
	
	private void digit( int digit, int number )
	{
		if( oldDigit[ digit ] != number )
		{
			int bits = sections[ number ];
			int oldBits = sections[ oldDigit[ digit ] ];
			int oldBit;
			for( int i = 0; i < 7; i++ )
			{
				// Flip state of section if necessary
				oldBit = oldBits & 1;
				if( oldBit != ( bits & 1 ) )
					section( digit, i, oldBit == 0 );
					
				bits >>= 1;
				oldBits >>= 1;
			}
			
			oldDigit[ digit ] = number;
		}
	}
	
	private void section( int digit, int section, boolean on )
	{
		int x = 8 * digit * scale + ( digit > 0 ? scale * digit : 0 ) + ( digit > 1 ? scale * 2 : 0 ) + ( digit > 3 ? scale * 2 : 0 );
		bar( marginX + x + sectionX[section] * scale, marginY + sectionY[section] * scale, sectionW[section] * scale, sectionH[section] * scale, on ? pixel: 0 );
	}
	
	private void bar( int x, int y, int w, int h, int pixel )
	{
		int pixels[] = getPixels();
		int scanline = getPixelWidth();
		int jump = scanline - w;
		
		int size = w * h;
		int p = y * scanline + x;
		int s = 0;
		for( int i = 0; i < size; i++ )
		{
			pixels[ p++ ] = pixel;
			if( ++s == w )
			{
				p += jump;
				s = 0;
			}
		}
		
		p = y * scanline + x;
		pixels[ p ] = 0;
		p += w - 1;
		pixels[ p ] = 0;
		p += scanline * ( h - 1 );
		pixels[ p ] = 0;
		p -= w - 1;
		pixels[ p ] = 0;

		queue.addRectangle( x, y, w, h, this );
	}
}
