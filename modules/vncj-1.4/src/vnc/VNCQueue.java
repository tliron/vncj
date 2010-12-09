package gnu.vnc;

import gnu.rfb.*;
import gnu.rfb.server.*;
import gnu.awt.*;

import java.io.*;
import java.util.*;

/**
* Smart rectangle queue that converts inserted rectangles to as small a number of encoded RFB
* rectangles as possible within a specified clipping area.
**/

public class VNCQueue
{
	//
	// Construction
	//
	
	public VNCQueue( RFBClients clients )
	{
		this.clients = clients;
	}
	
	//
	// Operations
	//
	
	public void frameBufferUpdate( RFBClient client, boolean incremental, int x, int y, int w, int h, PixelsOwner pixelsOwner ) throws IOException
	{
		int[] pixels = pixelsOwner.getPixels();
		int scanline = pixelsOwner.getPixelWidth();
		
		Rect[] rects;
		if( incremental )
		{
			// Encode queued regions
			rects = popEncoded( client, x, y, w, h, pixels, scanline );
		}
		else
		{
			// Encode specified region
			rects = new Rect[1];
			rects[0] = Rect.encode( client.getPreferredEncoding(), pixels, client.getPixelFormat(), scanline, x, y, w, h );
		}
		
		client.writeFrameBufferUpdate( rects );
	}
	
	public Rect[] popEncoded( RFBClient client, int x, int y, int w, int h, int[] pixels, int scanline ) throws IOException
	{
		return popEncoded( client, new Rectangle( x, y, w, h ), pixels, scanline );
	}
	
	//public Graphics g = null;
	public Rect[] popEncoded( RFBClient client, Rectangle clip, int[] pixels, int scanline ) throws IOException
	{
		// Pop
		Rectangle[] rectangles = pop( client, clip );
		
		// Encode rectangles
		PixelFormat pixelFormat = client.getPixelFormat();
		int encoding = client.getPreferredEncoding();
		Rect[] rects = new Rect[ rectangles.length ];
		for( int i = 0; i < rectangles.length; i++ )
		{
			rects[i] = Rect.encode( encoding, pixels, pixelFormat, scanline, rectangles[i].x, rectangles[i].y, rectangles[i].width, rectangles[i].height );
		}
		
		//rects = new Rect[1];
		//rects[0] = Rect.encode( encoding, pixels, pixelFormat, scanline, clip.x, clip.y, clip.width, clip.height );
		
		if( rects.length == 0 )
		{
			// Encode empty raw rect
			rects = new Rect[1];
			rects[0] = Rect.encode( rfb.EncodingRaw, pixels, pixelFormat, scanline, 0, 0, 0, 0 );
		}
		
		return rects;
	}
	
	public void addRectangle( int x, int y, int w, int h, PixelsOwner pixelsOwner )
	{
		addRectangle( new Rectangle( x, y, w, h ), pixelsOwner );
	}
	
	public void addRectangle( Rectangle addition, PixelsOwner pixelsOwner )
	{
		// Clip addition
		addition = new Rectangle( 0, 0, pixelsOwner.getPixelWidth(), pixelsOwner.getPixelHeight() ).intersection( addition );
		
		// Add to all queues
		Vector queue;
		for( Enumeration e = clients.elements(); e.hasMoreElements(); )
		{
			queue = getQueue( (RFBClient) e.nextElement() );
			addRectangle( queue, addition );
		}
	}
	
	public void addRectangle( PixelsOwner pixelsOwner )
	{
		// Entire area
		Rectangle addition = new Rectangle( 0, 0, pixelsOwner.getPixelWidth(), pixelsOwner.getPixelHeight() );
		
		// Set all queues
		Vector queue;
		for( Enumeration e = clients.elements(); e.hasMoreElements(); )
		{
			queue = getQueue( (RFBClient) e.nextElement() );
			synchronized( queue )
			{
				queue.removeAllElements();
				queue.addElement( addition );
			}
		}
	}
	
	public Rectangle[] pop( RFBClient client, int x, int y, int w, int h )
	{
		return pop( client, new Rectangle( x, y, w, h ) );
	}

	public Rectangle[] pop( RFBClient client, Rectangle clip )
	{
		Vector queue = getQueue( client );
		
		// Collect rectangles in area
		Vector v = new Vector();
		Rectangle r;
		synchronized( queue )
		{
			for( Enumeration e = queue.elements(); e.hasMoreElements(); )
			{
				r = (Rectangle) e.nextElement();
				if( clip.contains( r.getLocation() ) ||
					clip.contains( new Point( r.x + r.width, r.y + r.height ) ) ||
					clip.contains( new Point( r.x, r.y + r.height ) ) ||
					clip.contains( new Point( r.x + r.width, r.y ) ) )
					// TODO: if the rectangle is only partially contained,
					// should we return the untouched area to the queue?
				{
					queue.removeElement( r );
					v.addElement( r );
					e = queue.elements();
				}
			}
		}
		
		// Convert to array
		Rectangle[] array = new Rectangle[ v.size() ];
		v.toArray( array );
		return array;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private RFBClients clients;
	
	private Vector getQueue( RFBClient client )
	{
		Vector queue = (Vector) clients.getProperty( client, "queue" );
		if( queue == null )
		{
			queue = new Vector();
			clients.setProperty( client, "queue", queue );
		}
		
		return queue;
	}
	
	private void addRectangle( Vector queue, Rectangle addition )
	{
		// Ignore linear regions
		if( ( addition.width <= 0 ) || ( addition.height <= 0 ) )
			return;
			
		Enumeration e;
		Rectangle r;
		
		synchronized( queue )
		{
			// Are we already contained?
			for( e = queue.elements(); e.hasMoreElements(); )
			{
				r = (Rectangle) e.nextElement();
				if( r.contains( addition ) )
					return;
			}

			// Do we contain others?
			for( e = queue.elements(); e.hasMoreElements(); )
			{
				r = (Rectangle) e.nextElement();
				if( addition.contains( r ) )
				{
					// We contain a previous rect
					queue.removeElement( r );
					e = queue.elements();
				}
			}
			
			// Do we overlap others?
			Rectangle[] union = null;
			for( e = queue.elements(); e.hasMoreElements(); )
			{
				r = (Rectangle) e.nextElement();
				if( addition.contains( r.getLocation() ) )
				{
					// We overlap previous rect, so add union
					union = nonOverlappedUnion( addition, r );
				}
				else if( r.contains( addition.getLocation() ) )
				{
					// A previous rect overlaps us, so add union
					union = nonOverlappedUnion( r, addition );
				}
				
				if( union != null )
				{
					// Add union
					queue.removeElement( r );
					for( int i = 0; i < union.length; i++ )
						addRectangle( queue, union[ i ] );
					return;
					//addition = addition.union( r );
				}
			}
			
			queue.addElement( addition );
		}
	}

	private static Rectangle[] nonOverlappedUnion( Rectangle r1, Rectangle r2 )
	{
		Rectangle s[] = null;
		if( ( r2.y + r2.height ) <= ( r1.y + r1.height ) )
		{
			// +---+     +---+
			// | +-+--+  |   +--+
			// | +-+--+  |   +--+
			// +---+     +---+
			
			s = new Rectangle[2];
			s[0] = r1;
			s[1] = new Rectangle( r1.x + r1.width, r2.y, r2.x + r2.width - r1.x - r1.width, r2.height );
		}
		else if( ( r2.x + r2.width ) <= ( r1.x + r1.width ) )
		{
			// +-----+  +-----+
			// | +-+ |  |     |
			// +-+-+-+  +-+-+-+
			//   | |      | |
			//   +-+      +-+
			
			s = new Rectangle[2];
			s[0] = r1;
			s[1] = new Rectangle( r2.x, r1.y + r1.height, r2.width, r2.y + r2.height - r1.y - r1.height );
		}
		else
		{
			// +-----+    +-----+
			// | +---+-+  +-----+-+
			// | |   | |  |       |
			// +-+---+ |  +-+-----+
			//   +-----+    +-----+
			
			s = new Rectangle[3];
			s[0] = new Rectangle( r1.x, r1.y, r1.width, r2.y - r1.y );
			s[1] = new Rectangle( r1.x, r2.y, r2.x + r2.width - r1.x, r1.y + r1.height - r2.y );
			s[2] = new Rectangle( r2.x, r1.y + r1.height, r2.width, r2.y + r2.height - r1.y - r1.height );
		}
		
		return s;
	}
}
