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

import java.awt.*;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.image.VolatileImage;
import java.awt.peer.*;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

public class VirtualLightweightPeer extends VirtualComponentPeer implements LightweightPeer, ContainerPeer
{
        //
        // Construction
        //


        /**
         * new Constructor by Marcus Wolschon
         */
        public VirtualLightweightPeer( Toolkit toolkit,Component component )
        {
                super(toolkit,component );
        }

        /**
         * new Constructor by Marcus Wolschon
         */
        public VirtualLightweightPeer( Component component )
        {
                super(null,component );
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
		
		public Insets getInsets()
		{
			return insets;
		}

		public Insets insets()
		{
			return getInsets();
		}

		private Insets insets = new Insets( 0, 0, 0, 0 );

		public void beginLayout() {
			// TODO Auto-generated method stub
			
		}

		public void beginValidate() {
			// TODO Auto-generated method stub
			
		}

		public void cancelPendingPaint(int x, int y, int w, int h) {
			// TODO Auto-generated method stub
			
		}

		public void endLayout() {
			// TODO Auto-generated method stub
			
		}

		public void endValidate() {
			// TODO Auto-generated method stub
			
		}

		public boolean isPaintPending() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRestackSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		public void restack() {
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
		public boolean requestFocus(Component lightweightChild,
				boolean temporary, boolean focusedWindowChangeAllowed,
				long time, Cause cause) {
			// TODO Auto-generated method stub
			return false;
		}

}
