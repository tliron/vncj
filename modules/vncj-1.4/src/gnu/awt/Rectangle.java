package gnu.awt;

/**
* This is a substitute for the {@link java.awt.Rectangle java.awt.Rectangle} class.
**/

public class Rectangle
{
	//
	// Construction
	//
	
	public Rectangle( int x, int y, int width, int height )
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	//
	// Attributes
	//
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	//
	// Operations
	//
	
	public Point getLocation()
	{
		return new Point( x, y );
	}
	
	public Rectangle intersection( Rectangle r )
	{
		int x1 = Math.max( x, r.x );
		int x2 = Math.min( x + width, r.x + r.width );
		int y1 = Math.max( y, r.y );
		int y2 = Math.min( y + height, r.y + r.height );
		
		if( ( ( x2 - x1 ) < 0 ) || ( ( y2 - y1 ) < 0 ) )
			return new Rectangle( 0, 0, 0, 0 );
		else
			return new Rectangle( x1, y1, x2 - x1, y2 - y1 );
	}
	
	public boolean contains( Point p )
	{
		return
			( p.x >= x ) &&
			( p.y >= y ) &&
			( p.x - x < width ) &&
			( p.y - y < height );
	}
	
	public boolean contains( Rectangle r )
	{
		if( ( width <= 0 ) || ( height <= 0 ) || ( r.width <= 0 ) || ( r.height <= 0 ) )
			return false;
			
		return
			( r.x >= x ) &&
			( r.y >= y ) &&
			( r.x + r.width < x + width ) &&
			( r.y + r.height < y + height );
	}
}






