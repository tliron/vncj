package gnu.vnc.awt;

import gnu.vnc.*;
import gnu.awt.*;
import gnu.awt.virtual.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.applet.*;
import java.util.*;

import javax.swing.*;

/**
* Manages a {@link gnu.awt.PixelsOwner} for {@link gnu.vnc.awt.VNCRepaintManager}.
**/

// Much of this code is copied from javax.swing.RepaintManager, JDK 1.4.0

class VNCOwnerRepaintManager
{
	//
	// Construction
	//
	
	protected VNCOwnerRepaintManager( PixelsOwner pixelsOwner, RFBClients clients )
	{
		this.pixelsOwner = pixelsOwner;
		queue = new VNCQueue( clients );
	}
	
	//
	// Operations
	//
	
	public void frameBufferUpdate( RFBClient client, boolean incremental, int x, int y, int w, int h, PixelsOwner pixelsOwner ) throws IOException
	{
		validateInvalidComponents();
		paintDirtyRegions();
		queue.frameBufferUpdate( client, incremental, x, y, w, h, pixelsOwner );
	}
	
    public synchronized void addInvalidComponent( JComponent invalidComponent )
    {
		Component validateRoot = null;
		
		// Find the first JComponent ancestor of this component whose
		// isValidateRoot() method returns true.
		for( Component c = invalidComponent; c != null; c = c.getParent() )
		{
			if( ( c instanceof CellRendererPane ) || ( c.getPeer() == null ) )
				return;
			if( ( c instanceof JComponent ) && ( ((JComponent) c).isValidateRoot() ) )
			{
				validateRoot = c;
				break;
			}
		}
		
		if( validateRoot == null )
			return;
		
		// If the validateRoot and all of its ancestors aren't visible
		// then we don't do anything.  While we're walking up the tree
		// we find the root Window or Applet.
		Component root = null;
		
		for( Component c = validateRoot; c != null; c = c.getParent() )
		{
			if( !c.isVisible() || ( c.getPeer() == null ) )
				return;
			if( ( c instanceof Window ) || ( c instanceof Applet ) )
			{
				root = c;
				break;
			}
		}
		
		if( root == null )
			return;
		
		// Lazily create the invalidateComponents vector and add the
		// validateRoot if it's not there already.  If this validateRoot
		// is already in the vector, we're done.
		if( invalidComponents == null )
		{
			invalidComponents = new Vector();
		}
		else
		{
			int n = invalidComponents.size();
			for( int i = 0; i < n; i++ )
			{
				if( validateRoot == (Component) invalidComponents.get( i ) )
					return;
			}
		}
		invalidComponents.add( validateRoot );
		
		/*
		// Use screen coordinates
		java.awt.Point location = new java.awt.Point();
		Dimension size = c.getSize();
		SwingUtilities.convertPointToScreen( location, c );
		
		// Queue rectangle
		PixelsOwner pixelsOwner = getPixelsOwner( c );
		VNCQueue queue = (VNCQueue) queues.get( pixelsOwner );
		queue.addRectangle( location.x, location.y, size.width, size.height, pixelsOwner );
		
		//System.err.println(new Rectangle( location.x, location.y, size.width, size.height ) );
		*/
	}
	
    public synchronized void removeInvalidComponent( JComponent component )
	{
		if( invalidComponents != null )
		{
			int index = invalidComponents.indexOf( component );
			if( index != -1 )
			{
				invalidComponents.remove( index );
			}
		}
    }
	
	public void addDirtyRegion( JComponent c, int x, int y, int w, int h )
	{
		// Special cases we don't have to bother with
		if( ( w <= 0 ) || ( h <= 0 ) || ( c == null ) )
			return;
		if( ( c.getWidth() <= 0 ) || ( c.getHeight() <= 0 ) )
			return;
		
		java.awt.Rectangle r = (java.awt.Rectangle) dirtyComponents.get( c );
		if( r != null )
		{
			// A non-null r implies c is already marked as dirty,
			// and that the parent is valid. Therefore we can
			// just union the rect and bail.
			SwingUtilities.computeUnion( x, y, w, h, r );
			return;
		}
		
		// Make sure that c and all it ancestors (up to an Applet or
		// Window) are visible.  This loop has the same effect as 
		// checking c.isShowing() (and note that it's still possible 
		// that c is completely obscured by an opaque ancestor in 
		// the specified rectangle).
		Component root = null;
		
		for( Container p = c; p != null; p = p.getParent() )
		{
			if( !p.isVisible() || ( p.getPeer() == null ) )
				return;
			if( ( p instanceof Window ) || ( p instanceof Applet ) )
			{
				root = p;
				break;
			}
		}
		
		if( root == null )
			return;
		
		dirtyComponents.put( c, new java.awt.Rectangle( x, y, w, h ) );
	}
	
    public java.awt.Rectangle getDirtyRegion( JComponent aComponent )
	{
		java.awt.Rectangle r = null;
		synchronized( this )
		{
			r = (java.awt.Rectangle) dirtyComponents.get( aComponent );
		}
		if( r == null )
			return new java.awt.Rectangle( 0, 0, 0, 0 );
		else
			return new java.awt.Rectangle( r );
    }
	
    public void markCompletelyClean( JComponent component )
	{
		synchronized( this )
		{
			dirtyComponents.remove( component );
		}
		
		// VNCj:
		//
		// Aha... some Swing actions (such as dragging and scrolling) bypass the
		// RepaintManager by calling this method. We have no choice but to "outsmart"
		// them (ahem) by adding the entire component to the queue. The result is that
		// performance actually goes down. AND LET THAT BE A LESSON TO YOU! Follow
		// the rules, buddy.
		
		// Add rectangle to VNC queue (in screen coordinates)
		java.awt.Point location = new java.awt.Point( component.getLocation() );
		SwingUtilities.convertPointToScreen( location, component );
		queue.addRectangle( location.x, location.y, component.getWidth(), component.getHeight(), pixelsOwner );
    }
	
	public void validateInvalidComponents()
	{
		java.util.List ic;
		synchronized( this )
		{
			if(invalidComponents == null)
				return;
				
			ic = invalidComponents;
			invalidComponents = null;
		}
		int n = ic.size();
		for( int i = 0; i < n; i++ )
		{
			( (Component) ic.get( i ) ).validate();
		}
	}
	
	public void paintDirtyRegions()
	{
		int i, count;
		java.util.List roots;
		JComponent dirtyComponent;
		
		synchronized( this )
		{  // swap for thread safety
			Map tmp = tmpDirtyComponents;
			tmpDirtyComponents = dirtyComponents;
			dirtyComponents = tmp;
			dirtyComponents.clear();
		}
		
		count = tmpDirtyComponents.size();
		if( count == 0 )
			return;
		
		java.awt.Rectangle rect;
		int localBoundsX = 0;
		int localBoundsY = 0;
		int localBoundsH = 0;
		int localBoundsW = 0;
		Iterator keys;
		
		roots = new ArrayList( count );
		keys = tmpDirtyComponents.keySet().iterator();
		
		while( keys.hasNext() )
		{
			dirtyComponent = (JComponent) keys.next();
			collectDirtyComponents( tmpDirtyComponents, dirtyComponent, roots );
		}
		
		count = roots.size();
		//        System.out.println("roots size is " + count);
		for( i = 0; i < count; i++ )
		{
			dirtyComponent = (JComponent) roots.get( i );
			rect = (java.awt.Rectangle) tmpDirtyComponents.get( dirtyComponent );
			//            System.out.println("Should refresh :" + rect);
			localBoundsH = dirtyComponent.getHeight();
			localBoundsW = dirtyComponent.getWidth();
			
			SwingUtilities.computeIntersection( localBoundsX, localBoundsY, localBoundsW, localBoundsH, rect );
			// System.out.println("** paint of " + dirtyComponent + rect);
			dirtyComponent.paintImmediately( rect.x, rect.y, rect.width, rect.height );
			
			// Add rectangle to VNC queue (in screen coordinates)
			java.awt.Point location = new java.awt.Point( rect.x, rect.y );
			SwingUtilities.convertPointToScreen( location, dirtyComponent );
			queue.addRectangle( location.x, location.y, rect.width, rect.height, pixelsOwner );
		}
		tmpDirtyComponents.clear();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private PixelsOwner pixelsOwner;
	private VNCQueue queue;
	private Map dirtyComponents = Collections.synchronizedMap( new HashMap() );
	private Map tmpDirtyComponents = Collections.synchronizedMap( new HashMap() );
	private java.util.List invalidComponents = Collections.synchronizedList( new LinkedList() );
	
	private void collectDirtyComponents( Map dirtyComponents, JComponent dirtyComponent, java.util.List roots )
	{
		java.awt.Rectangle tmp = new java.awt.Rectangle();
		
		int dx, dy, rootDx, rootDy;
		Component component, rootDirtyComponent, parent;
		//Rectangle tmp;
		java.awt.Rectangle cBounds;
		boolean opaqueAncestorFound = false;
		
		// Find the highest parent which is dirty.  When we get out of this
		// rootDx and rootDy will contain the translation from the
		// rootDirtyComponent's coordinate system to the coordinates of the
		// original dirty component.  The tmp Rect is also used to compute the
		// visible portion of the dirtyRect.
		
		component = rootDirtyComponent = dirtyComponent;
		
		cBounds = dirtyComponent.getBounds();
		
		dx = rootDx = 0;
		dy = rootDy = 0;
		tmp.setBounds( (java.awt.Rectangle) dirtyComponents.get( dirtyComponent ) );
		
		// System.out.println("Collect dirty component for bound " + tmp + 
		//                                   "component bounds is " + cBounds);;
		SwingUtilities.computeIntersection( 0, 0, cBounds.width, cBounds.height, tmp );
		
		if( tmp.isEmpty() )
		{
			// System.out.println("Empty 1");
			return;
		} 
		
		if( dirtyComponent.isOpaque() )
		opaqueAncestorFound = true;
		
		for( ;; )
		{
			parent = component.getParent();
			if( parent == null )
				break;
			
			if( !( parent instanceof JComponent ) )
				break;
			
			component = parent;
			
			if( ( (JComponent) component ).isOpaque() )
				opaqueAncestorFound = true;
			
			dx += cBounds.x;
			dy += cBounds.y;
			tmp.setLocation( tmp.x + cBounds.x, tmp.y + cBounds.y );
			
			cBounds = ( (JComponent) component ).getBounds();
			tmp = SwingUtilities.computeIntersection( 0, 0, cBounds.width, cBounds.height, tmp );
			
			if( tmp.isEmpty() )
			{
				// System.out.println("Empty 2");
				return;
			}
			
			if( dirtyComponents.get( component ) != null )
			{
				rootDirtyComponent = component;
				rootDx = dx;
				rootDy = dy;
			}
		} 
		
		if( dirtyComponent != rootDirtyComponent )
		{
			java.awt.Rectangle r;
			tmp.setLocation( tmp.x + rootDx - dx, tmp.y + rootDy - dy );
			r = (java.awt.Rectangle) dirtyComponents.get( rootDirtyComponent );
			SwingUtilities.computeUnion( tmp.x, tmp.y, tmp.width, tmp.height, r );
		}
		
		// If we haven't seen this root before, then we need to add it to the
		// list of root dirty Views.
		
		if( !roots.contains( rootDirtyComponent ) )
			roots.add( rootDirtyComponent );
	}
}
