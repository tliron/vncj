package gnu.vnc.awt;

import gnu.vnc.*;
import gnu.awt.*;
import gnu.awt.virtual.*;
import gnu.rfb.*;
import gnu.rfb.server.*;

import java.io.*;
import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
* A {@link javax.swing.RepaintManager JFC repaint manager} that writes to
* {@link gnu.vnc.VNCQueue VNC queues}. Internally creates a {@link gnu.vnc.awt.VNCOwnerRepaintManager}
* for each managed {@link gnu.awt.PixelsOwner}..
**/

class VNCRepaintManager extends RepaintManager
{
	//
	// Static operations
	//
	
	public static VNCRepaintManager currentManager()
	{
		if( repaintManager == null )
		{
			repaintManager = new VNCRepaintManager();
			RepaintManager.setCurrentManager( repaintManager );
		}
		return repaintManager;
	}
	
	//
	// Operations
	//
	
	public void manage( PixelsOwner pixelsOwner, RFBClients clients )
	{
		managers.put( pixelsOwner, new VNCOwnerRepaintManager( pixelsOwner, clients ) );
	}
	
	public void unmanage( PixelsOwner pixelsOwner )
	{
		managers.remove( pixelsOwner );
	}
	
	public void frameBufferUpdate( PixelsOwner pixelsOwner, RFBClient client, boolean incremental, int x, int y, int w, int h ) throws IOException
	{
		VNCOwnerRepaintManager manager = (VNCOwnerRepaintManager) managers.get( pixelsOwner );
		manager.frameBufferUpdate( client, incremental, x, y, w, h, pixelsOwner );
	}
	
	//
	// RepaintManager
	//
	
	public void addInvalidComponent( JComponent component )
	{
		// Find owner
		PixelsOwner pixelsOwner = getPixelsOwner( component );
		VNCOwnerRepaintManager manager = (VNCOwnerRepaintManager) managers.get( pixelsOwner );
		if( manager == null ) return;
		
		manager.addInvalidComponent( component );
	}
	
    public void removeInvalidComponent( JComponent component )
	{
		// Find owner
		PixelsOwner pixelsOwner = getPixelsOwner( component );
		VNCOwnerRepaintManager manager = (VNCOwnerRepaintManager) managers.get( pixelsOwner );
		if( manager == null ) return;
		
		manager.removeInvalidComponent( component );
	}
	
    public void addDirtyRegion( JComponent component, int x, int y, int w, int h )
    {
		// Find owner
		PixelsOwner pixelsOwner = getPixelsOwner( component );
		VNCOwnerRepaintManager manager = (VNCOwnerRepaintManager) managers.get( pixelsOwner );
		if( manager == null ) return;
		
		manager.addDirtyRegion( component, x, y, w, h );		
    }
	
    public java.awt.Rectangle getDirtyRegion( JComponent component )
	{
		// Find owner
		PixelsOwner pixelsOwner = getPixelsOwner( component );
		VNCOwnerRepaintManager manager = (VNCOwnerRepaintManager) managers.get( pixelsOwner );
		if( manager == null ) return null;
		
		return manager.getDirtyRegion( component );
	}
	
    public void markCompletelyClean( JComponent component )
	{
		// Find owner
		PixelsOwner pixelsOwner = getPixelsOwner( component );
		VNCOwnerRepaintManager manager = (VNCOwnerRepaintManager) managers.get( pixelsOwner );
		if( manager == null ) return;
		
		manager.markCompletelyClean( component );
	}
	
	public void paintDirtyRegions()
	{
		// This is usually called from a thread in SystemEventQueueUtilities,
		// but we want to handle this ourselves
	}
	
	public void validateInvalidComponents()
	{
		// This is usually called from a thread in SystemEventQueueUtilities,
		// but we want to handle this ourselves
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private static VNCRepaintManager repaintManager = null;
	private Map managers = Collections.synchronizedMap( new HashMap() );
	
	//
	// Construction
	//
	
	private VNCRepaintManager()
	{
		super();
		setDoubleBufferingEnabled( false );
	}
	
	private static PixelsOwner getPixelsOwner( Component component )
	{
		if( component instanceof PixelsOwner )
		{
			return (PixelsOwner) component;
		}
		else
		{
			Component parent = component.getParent();
			if( parent == null ) return null;
			return getPixelsOwner( parent );
		}
	}
}
