package gnu.rfb;

import java.util.*;

/**
* Manages a library of {@link gnu.rfb.Rect RFB rectangles}.
**/

public class RectLibrary
{
	//
	// Construction
	//
	
	public RectLibrary()
	{
	}
	
	//
	// Operations
	//
	
	public Rect getRect( int key )
	{
		Rect rect = (Rect) rects.get( new Integer( key ) );
		if( rect == null )
			rect = defaultRect;
		return rect;
	}
	
	public Rect getRect( int key, int originX, int originY )
	{
		Rect rect = getRect( key );
		if( rect != null )
		{
			try
			{
				rect = (Rect) rect.clone();
			}
			catch( CloneNotSupportedException x )
			{
			}
			rect.transform( originX, originY );
		}
		return rect;
	}
	
	public void putRect( int key, Rect rect )
	{
		rects.put( new Integer( key ), rect );
	}

	public void putDefaultRect( Rect rect )
	{
		defaultRect = rect;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private Hashtable rects = new Hashtable();
	private Rect defaultRect = null;
}
