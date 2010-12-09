package gnu.awt.virtual;

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

import gnu.awt.*;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.peer.*;

import java.awt.BufferCapabilities.FlipContents;

import java.awt.event.*;
import java.awt.image.*;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

class VirtualFramePeer extends VirtualComponentPeer implements FramePeer
{
	//
	// Construction
	//

	public VirtualFramePeer( Frame frame, PixelsOwner pixelsOwner )
	{
		super( frame.getToolkit(), frame );
		this.pixelsOwner = pixelsOwner;
	}

	//
	// FramePeer
	//

	public void setTitle( String title )
	{
	}

	public void setIconImage( Image im )
	{
	}

	public void setMenuBar( MenuBar mb )
	{
		// TODO
	}

	public void setResizable( boolean resizeable )
	{
	}

	public void setState( int state )
	{
		// Always Frame.NORMAL
	}

	public int getState()
	{
		return Frame.NORMAL;
	}

	//
	// WindowPeer
	//

	public void toFront()
	{
		// Always "in front"
	}

	public void toBack()
	{
		// Always "in front"
	}

	public int handleFocusTraversalEvent( KeyEvent e )
	{
		return -1;
	}

	//
	// ContainerPeer
	//

	public Insets getInsets()
	{
		return insets;
	}

	public void beginValidate()
	{
	}

	public void endValidate()
	{
	}

	public Insets insets()
	{
		return getInsets();
	}

	//
	// ComponentPeer
	//

	public void setVisible( boolean b )
	{
		if( b == true )
		{
			// First paint
			component.paint( getGraphics() );
		}
	}

	public Graphics getGraphics()
	{
		// We will always be writing to the same image
		if( image == null )
		{
			//Thread.dumpStack();
			/*if( size.width == 0 )
				size.width = 100;
			if( size.height == 0 )
				size.height = 100;*/

			if( ( size.width > 0 ) && ( size.height > 0 ) )
			{
				// Color model
				DirectColorModel colorModel = (DirectColorModel) getColorModel();

				// Pixel data
				int[] pixels = new int[ size.width * size.height ];
				DataBuffer dataBuffer = new DataBufferInt( pixels, pixels.length );

				// Sample model
				SampleModel sampleModel = new SinglePixelPackedSampleModel( DataBuffer.TYPE_INT, size.width, size.height, colorModel.getMasks() );

				// Raster
				WritableRaster raster = Raster.createWritableRaster( sampleModel, dataBuffer, null );

				// Image
				image = new BufferedImage( colorModel, raster, true, null );

				// Set pixel owner
				pixelsOwner.setPixelArray( pixels, size.width, size.height );
			}
                        else
                        {
                         System.err.println("ERROR[VirtualFramePeer] getGraphics() - width and/or height == 0, cannot create Graphics");
                         return null;
                        }
		}

		return image.getGraphics();
	}

	public Image createImage( int width, int height )
	{
		//System.err.println( "createImage("+width+","+height+")" );

		// Color model
		DirectColorModel colorModel = (DirectColorModel) getColorModel();

		// Sample model
		SampleModel sampleModel = new SinglePixelPackedSampleModel( DataBuffer.TYPE_INT, width, height, colorModel.getMasks() );

		// Raster
		WritableRaster raster = Raster.createWritableRaster( sampleModel, null );

		// Image
		return new BufferedImage( colorModel, raster, true, null );
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private PixelsOwner pixelsOwner;
	private Insets insets = new Insets( 0, 0, 0, 0 );
	private BufferedImage image = null;
	public void setMaximizedBounds(Rectangle rec) {
		// TODO Auto-generated method stub
		//TODO:Need set
	}

	public void beginLayout() {
		// TODO Auto-generated method stub
		
	}

	public void endLayout() {
		// TODO Auto-generated method stub
		
	}

	public boolean isPaintPending() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canDetermineObscurity() {
		// TODO Auto-generated method stub
		return false;
	}

	public void createBuffers(int arg0, BufferCapabilities arg1) throws AWTException {
		// TODO Auto-generated method stub
		
	}

	public VolatileImage createVolatileImage(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void destroyBuffers() {
		// TODO Auto-generated method stub
		
	}

	public void flip(FlipContents arg0) {
		// TODO Auto-generated method stub
		
	}

	public Image getBackBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean handlesWheelScrolling() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFocusable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isObscured() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean requestFocus(Component arg0, boolean arg1, boolean arg2, long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateCursorImmediately() {
		// TODO Auto-generated method stub
		
	}

	public void setBoundsPrivate(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	public boolean requestWindowFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateAlwaysOnTop() {
		// TODO Auto-generated method stub
		
	}

	public void updateFocusableWindowState() {
		// TODO Auto-generated method stub
		
	}

	public void cancelPendingPaint(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		
	}

	public boolean isRestackSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public void restack() {
		// TODO Auto-generated method stub
		
	}

	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isReparentSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public void layout() {
		// TODO Auto-generated method stub
		
	}

	public void setBounds(int x, int y, int width, int height, int op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getBoundsPrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void repositionSecurityWarning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModalBlocked(Dialog blocker, boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOpacity(float opacity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOpaque(boolean isOpaque) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateIconImages() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMinimumSize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyShape(Region shape) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean requestFocus(Component lightweightChild, boolean temporary,
			boolean focusedWindowChangeAllowed, long time, Cause cause) {
		// TODO Auto-generated method stub
		return false;
	}
}
