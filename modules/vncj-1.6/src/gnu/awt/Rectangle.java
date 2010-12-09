package gnu.awt;

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






