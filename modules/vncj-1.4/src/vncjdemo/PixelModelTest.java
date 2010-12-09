package vncjdemo;

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
	
	public PixelModelTest( int display, String displayName, int width, int height )
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
		int hours = calendar.get( Calendar.HOUR_OF_DAY );
		int minutes = calendar.get( Calendar.MINUTE );
		int seconds = calendar.get( Calendar.SECOND );
		digit( 0, hours / 10 );
		digit( 1, hours % 10 );
		digit( 2, minutes / 10 );
		digit( 3, minutes % 10 );
		digit( 4, seconds / 10 );
		digit( 5, seconds % 10 );
		colon( !colonOn );
		
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
	private boolean colonOn = false;
	
	private void colon( boolean on )
	{
		bar( marginX + 18 * scale, marginY + scale * 2,  scale, 2 * scale, on ? pixel: 0 );
		bar( marginX + 18 * scale, marginY + scale * 10, scale, 2 * scale, on ? pixel: 0 );
		bar( marginX + 38 * scale, marginY + scale * 2,  scale, 2 * scale, on ? pixel: 0 );
		bar( marginX + 38 * scale, marginY + scale * 10, scale, 2 * scale, on ? pixel: 0 );
		colonOn = on;
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
