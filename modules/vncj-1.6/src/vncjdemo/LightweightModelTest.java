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

import gnu.rfb.*;
import gnu.rfb.server.*;
import gnu.vnc.awt.*;
import gnu.awt.virtual.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LightweightModelTest extends VNCFrame implements MouseListener, MouseMotionListener
{
	//
	// Construction
	//
	
	public LightweightModelTest( int display, String displayName )
	{
		//super( new VirtualToolkit(), displayName, 400, 300 );
		super( displayName, 400, 300 );
		
		addMouseListener( this );
		addMouseMotionListener( this );
		
		clean( false );
	}
	
	//
	// RFBServer
	//

	public void setEncodings( RFBClient client, int[] encodings ) throws IOException
	{
		client.setPreferredEncoding( rfb.EncodingRRE );
	}
	
	//
	// MouseListener
	//
	
	public void mouseClicked( MouseEvent e )
	{
	}

	public void mouseEntered( MouseEvent e )
	{
	}

	public void mouseExited( MouseEvent e )
	{
	}

	public void mousePressed( MouseEvent e )
	{
		if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) != 0 )
		{
			cursor();
			
			fromX = e.getX();
			fromY = e.getY();
		}
		else
		{
			clean( true );
		}
	}

	public void mouseReleased( MouseEvent e )
	{
		if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) != 0 )
		{
			// Draw line
			Graphics g = getGraphics();

			if( toX != -1 )
			{						
				// Erase old line
				g.setXORMode( Color.gray );
				g.drawLine( fromX, fromY, toX, toY );
				queueRectangle( fromX, fromY, toX, toY );
				g.setPaintMode();
			
				// Draw new line
				g.setColor( Color.blue );
				g.drawLine( fromX, fromY, toX, toY );
				queueRectangle( fromX, fromY, toX, toY );
			}

			fromX = -1;
			fromY = -1;
			toX = -1;
			toY = -1;
			cursorX = -1;
			cursorY = -1;
		}
	}
	
	//
	// MouseMotionListener
	//

	public void mouseDragged( MouseEvent e )
	{
		if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) != 0 )
		{
			Graphics g = getGraphics();
			g.setXORMode( Color.gray );
			
			if( toX != -1 )
			{
				// Erase old line
				g.drawLine( fromX, fromY, toX, toY );
				queueRectangle( fromX, fromY, toX, toY );
			}
			
			toX = e.getX();
			toY = e.getY();

			// Draw new line			
			g.drawLine( fromX, fromY, toX, toY );
			queueRectangle( fromX, fromY, toX, toY );
			
			g.setPaintMode();
		}
	}

	public void mouseMoved( MouseEvent e )
	{
		if( cursorX != -1 )
			cursor();
		cursorX = e.getX();
		cursorY = e.getY();
		cursor();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	int fromX = -1, fromY = -1, toX = -1, toY = -1, cursorX = -1, cursorY = -1;
	int cursorR = 8;
	
	private void clean( boolean queue )
	{
		Graphics g = getGraphics();
		g.setColor( Color.white );
		g.fillRect( 0, 0, getSize().width, getSize().height );

		// Queue entire frame
		if( queue )
			this.queue.addRectangle( this );
		
		cursorX = -1;
		cursorY = -1;	
	}
	
	private void cursor()
	{
		Graphics g = getGraphics();
		g.setXORMode( Color.black );
		g.drawOval( cursorX - cursorR, cursorY - cursorR, cursorR * 2 + 1, cursorR * 2 + 1 );
		g.drawLine( cursorX - cursorR, cursorY, cursorX + cursorR, cursorY );
		g.drawLine( cursorX, cursorY - cursorR, cursorX, cursorY + cursorR );
		queueRectangle( cursorX - cursorR, cursorY - cursorR, cursorX + cursorR + 1, cursorY + cursorR + 1 );
		g.setPaintMode();
	}
	
	private void queueRectangle( int x1, int y1, int x2, int y2 )
	{
		int t;
		if( x2 < x1 )
		{
			t = x1;
			x1 = x2;
			x2 = t;		
		}
		if( y2 < y1 )
		{
			t = y1;
			y1 = y2;
			y2 = t;		
		}
		
		queue.addRectangle( x1, y1, x2 - x1 + 1, y2 - y1 + 1, this );
		try
		{
			updateAll();
		}catch(Exception e){};
	}
}
