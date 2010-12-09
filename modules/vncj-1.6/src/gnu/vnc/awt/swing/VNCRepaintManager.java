package gnu.vnc.awt.swing;

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

import gnu.vnc.*;
import gnu.vnc.awt.*;
import gnu.awt.*;
import gnu.awt.virtual.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.util.*;

import javax.swing.*;

public class VNCRepaintManager extends RepaintManager
{
	//
	// Construction
	//
	public VNCRepaintManager( PixelsOwner pixelsOwner, RFBClients clients )
	{
		super();
		//setDoubleBufferingEnabled( false );
		this.pixelsOwner = pixelsOwner;

		queue = new VNCQueue( clients );
	}

	public VNCRepaintManager( PixelsOwner pixelsOwner, VNCQueue queue )
	{
		super();
		//setDoubleBufferingEnabled( false );
		this.pixelsOwner = pixelsOwner;

		this.queue = queue;
	}

	//
	// Operations
	//

        public void frameBufferUpdate( RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException {
            queue.frameBufferUpdate( client, incremental, x, y, w, h);       
        }


    /**
     * Convert a point from a component's coordinate system to
     * screen coordinates.
     *
     * @param p  a Point object (converted to the new coordinate system)
     * @param c  a Component object
     */
    public static void convertPointToScreen(java.awt.Point p,Component c) {
            java.awt.Rectangle b;
            int x,y;

            do {
                if(c instanceof JComponent) {
                    x = ((JComponent)c).getX();
                    y = ((JComponent)c).getY();
                } else if(c instanceof java.applet.Applet) {
                if(c.isShowing())  // this makes it work with non-showing applets too
                  {
                    java.awt.Point pp = c.getLocationOnScreen();
                    x = pp.x;
                    y = pp.y;
                   }
                 else
                  {
                    x = 0;
                    y = 0;
                   }
                } else {
                    b = c.getBounds();
                    x = b.x;
                    y = b.y;
                }

                p.x += x;
                p.y += y;

                if(c instanceof java.awt.Window || c instanceof java.applet.Applet)
                    break;
                c = c.getParent();
            } while(c != null);
        }

	//
	// RepaintManager
	//

    public void addDirtyRegion( JComponent c, int x, int y, int w, int h )
    {
//     System.err.println("DEBUG[VNCRepaintManager] addDirtyRegion()");
		super.addDirtyRegion( c, x, y, w, h );

		// Use screen coordinates
		java.awt.Point location = new java.awt.Point( x, y );
                /*SwingUtilities.*/convertPointToScreen( location, c );

		// Queue rectangle
		queue.addRectangle( location.x, location.y, w, h, pixelsOwner );
        //System.out.println("X: = " + location.x + " Y: = " + location.y + " w: =" + w + " h: = " + h); 
    }

	public void addInvalidComponent( JComponent c )
	{
//     System.err.println("DEBUG[VNCRepaintManager] addInvalidComponent()");
		super.addInvalidComponent( c );

		// Use screen coordinates
		java.awt.Point location = new java.awt.Point();
		Dimension size = c.getSize();
                try
                  {
    		   SwingUtilities.convertPointToScreen( location, c );
                  }
                catch(java.awt.IllegalComponentStateException x)
                  {
                   throw new java.awt.IllegalComponentStateException(x.getMessage()+" component=["+c.getClass().getName()+"] parent="+c.getParent());
                  }

		// Queue rectangle
		queue.addRectangle( location.x, location.y, size.width, size.height, pixelsOwner );

		//System.err.println(new Rectangle( location.x, location.y, size.width, size.height ) );
	}
        public void paintDirtyRegions() {        
            System.out.println("painting");            
            super.paintDirtyRegions();
            System.out.println("done painting, sending to vnc");
            queue.takeSnapshot(pixelsOwner);
        }       

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private PixelsOwner pixelsOwner;
	private VNCQueue queue;
}
