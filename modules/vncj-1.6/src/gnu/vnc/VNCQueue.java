package gnu.vnc;

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
import gnu.awt.*;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.MemoryImageSource;
public class VNCQueue {
    //
    // Construction
    //
    int[] snapshot;
    int scanline;
    
   
    Object snapshotSemaphor = new Object();
    public VNCQueue( RFBClients clients ) {
        this.clients = clients;
    }
    
    //
    // Operations
    //
    
    public void frameBufferUpdate( RFBClient client, boolean incremental, int x, int y, int w, int h) throws IOException {        
        Rect[] rects;
        if( snapshot==null)
        {
        	System.out.println("None:snapshot");
        	return;
        }
        synchronized(snapshotSemaphor){
            if( incremental ) {
                // Encode queued regions
                rects = popEncoded( client, x, y, w, h, snapshot, scanline );
            }
            else {
                // Encode specified region
                rects = new Rect[1];
                rects[0] = Rect.encode( client.getPreferredEncoding(), snapshot, client.getPixelFormat(), scanline, x, y, w, h );
            }
            client.writeFrameBufferUpdate( rects );
        }
    }
    
    public Rect[] popEncoded( RFBClient client, int x, int y, int w, int h, int[] pixels, int scanline ) throws IOException {
        return popEncoded( client, new Rectangle( x, y, w, h ), pixels, scanline );
    }
    
    //public Graphics g = null;
    public Rect[] popEncoded( RFBClient client, Rectangle clip, int[] pixels, int scanline ) throws IOException {
        // Pop
        Rectangle[] rectangles = pop( client, clip );
        
        // Encode rectangles
        PixelFormat pixelFormat = client.getPixelFormat();
        int encoding = client.getPreferredEncoding();
        Rect[] rects = new Rect[ rectangles.length ];
        for( int i = 0; i < rectangles.length; i++ ) {
            rects[i] = Rect.encode( encoding, pixels, pixelFormat, scanline, rectangles[i].x, rectangles[i].y, rectangles[i].width, rectangles[i].height );
        }
               
        if( rects.length == 0 ) {
            throw new IOException("rects.length == 0, encoding an empty raw rect would cause blue screen on the official VNC-client (not a Windows(tm)-BlueScreen(tm)).");
        }
        
        return rects;
    }
    
    public void addRectangle( int x, int y, int w, int h, PixelsOwner pixelsOwner ) {
        addRectangle( new Rectangle( x, y, w, h ), pixelsOwner );
    }
    
    public void addRectangle( Rectangle addition, PixelsOwner pixelsOwner ) {
        // Clip addition
        addition = new Rectangle( 0, 0, pixelsOwner.getPixelWidth(), pixelsOwner.getPixelHeight() ).intersection( addition );

        Vector queue;
        for( Enumeration e = clients.elements(); e.hasMoreElements(); ) {
            queue = getQueue( (RFBClient) e.nextElement() );
            addRectangle( queue, addition );
        }
    }
    
    public void addRectangle( PixelsOwner pixelsOwner ) {
        // Entire area
        Rectangle addition = new Rectangle( 0, 0, pixelsOwner.getPixelWidth(), pixelsOwner.getPixelHeight() );
        
        // Set all queues
        Vector queue;
        for( Enumeration e = clients.elements(); e.hasMoreElements(); ) {
            queue = getQueue( (RFBClient) e.nextElement() );
            synchronized( queue ) {
                queue.removeAllElements();
                queue.addElement( addition );
            }
        }
    }
    
    public Rectangle[] pop( RFBClient client, int x, int y, int w, int h ) {
        return pop( client, new Rectangle( x, y, w, h ) );
    }
    
    public Rectangle[] pop( RFBClient client, Rectangle clip ) {
        //         System.err.println("DEBUG[VNCQueue]: pop()");
        Vector queue = getQueue( client );
        
        // Collect rectangles in area
        Vector v = new Vector();
        Rectangle r;
        synchronized( queue ) {
            for( Enumeration e = queue.elements(); e.hasMoreElements(); ) {
                r = (Rectangle) e.nextElement();
                if( clip.contains( r.getLocation() ) || clip.contains( new Point( r.x + r.width, r.y + r.height ) ) ) {
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
    
    private Vector getQueue( RFBClient client ) {
        Vector queue = (Vector) clients.getProperty( client, "queue" );
        if( queue == null ) {
            queue = new Vector();
            clients.setProperty( client, "queue", queue );
        }
        
        return queue;
    }
    
    private void addRectangle( Vector queue, Rectangle addition ) {
        // Ignore linear regions
        if( ( addition.width <= 0 ) || ( addition.height <= 0 ) )
            return;
        
        Enumeration e;
        Rectangle r;
        
        synchronized( queue ) {
            // Are we already contained?
            for( e = queue.elements(); e.hasMoreElements(); ) {
                r = (Rectangle) e.nextElement();
                if( r.contains( addition ) )
                    return;
            }
            
            // Do we contain others?
            for( e = queue.elements(); e.hasMoreElements(); ) {
                r = (Rectangle) e.nextElement();
                if( addition.contains( r ) ) {
                    // We contain a previous rect
                    queue.removeElement( r );
                    e = queue.elements();
                }
            }
            
            // Do we overlap others?
            Rectangle[] union = null;
            for( e = queue.elements(); e.hasMoreElements(); ) {
                r = (Rectangle) e.nextElement();
                if( addition.contains( r.getLocation() ) ) {
                    // We overlap previous rect, so add union
                    union = nonOverlappedUnion( addition, r );
                }
                else if( r.contains( addition.getLocation() ) ) {
                    // A previous rect overlaps us, so add union
                    union = nonOverlappedUnion( r, addition );
                }
                
                if( union != null ) {
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
    
    private static Rectangle[] nonOverlappedUnion( Rectangle r1, Rectangle r2 ) {
        Rectangle s[] = null;
        if( ( r2.y + r2.height ) <= ( r1.y + r1.height ) ) {
            // +---+     +---+
            // | +-+--+  |   +--+
            // | +-+--+  |   +--+
            // +---+     +---+
            
            s = new Rectangle[2];
            s[0] = r1;
            s[1] = new Rectangle( r1.x + r1.width, r2.y, r2.x + r2.width - r1.x - r1.width, r2.height );
        }
        else if( ( r2.x + r2.width ) <= ( r1.x + r1.width ) ) {
            // +-----+  +-----+
            // | +-+ |  |     |
            // +-+-+-+  +-+-+-+
            //   | |      | |
            //   +-+      +-+
            
            s = new Rectangle[2];
            s[0] = r1;
            s[1] = new Rectangle( r2.x, r1.y + r1.height, r2.width, r2.y + r2.height - r1.y - r1.height );
        }
        else {
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
    
    public void takeSnapshot(PixelsOwner p){
    	if(p==null || p.getPixels()==null )
    	{
    		System.out.println("Empty:"+p);
    		return;
    	}
        synchronized(this.snapshotSemaphor){
            if(snapshot == null){
                snapshot = new int[p.getPixels().length];
            }
            
           // System.out.println("Taking a snapshot");
            System.arraycopy(p.getPixels(),0,snapshot,0,p.getPixels().length);
           // System.out.println("Done Taking a snapshot");
            
            scanline=p.getPixelWidth();
            Enumeration enumerate = clients.elements();
            while(enumerate.hasMoreElements()){
                ((RFBClient)enumerate.nextElement()).setUpdateIsAvailable(true);
            }
        }
    }            
}
