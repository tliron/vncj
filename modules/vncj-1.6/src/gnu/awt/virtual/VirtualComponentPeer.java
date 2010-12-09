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
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

public abstract class VirtualComponentPeer implements ComponentPeer // extends sun.awt.windows.PublicWComponentPeer// implements ComponentPeer
{
	//static VirtualToolkit defaultToolkit;
        public void dispose()
        {
        }


        // Cursor

        public void setCursor( Cursor cursor )
        {
        }

        //
        // Construction
        //

        public VirtualComponentPeer( Toolkit toolkit, Component component )
        {
            this.toolkit = toolkit;
            // super(component);
            this.component = component;
            setBounds( component.getX(), component.getY(), component.getWidth(), component.getHeight() );

            // Disable double-buffering for Swing components
            javax.swing.RepaintManager.currentManager( component ).setDoubleBufferingEnabled( false );
        }

        public Component getComponent()
        {
        	return(component);
        }

        //
        // ComponentPeer
        //

        // Graphics

        public void paint( Graphics g )
        {
                //System.err.println("paint");
        }

        public void repaint( long tm, int x, int y, int width, int height )
        {
                //System.err.println("repaint");
        }

        public void print( Graphics g )
        {
        }

        public Graphics getGraphics()
        {
                Component parent = component.getParent();
                if( parent != null )
                {
                        System.err.println("creating relative graphics");
                        return parent.getGraphics().create( location.x, location.y, size.width, size.height );
                }
                else
                        throw new Error();
        }

        public GraphicsConfiguration getGraphicsConfiguration()
        {
                System.err.println("getGraphicsConfiguration");
                return null;
        }

        // Bounds

        public void setBounds( int x, int y, int width, int height )
        {
                //System.err.println("setBounds "+x+","+y+","+width+","+height);
                size.width = width;
                size.height = height;
        }

        public Point getLocationOnScreen()
        {
                Point screen = new Point( location );
                Component parent = component.getParent();
                if( parent != null )
                {
                        Point parentScreen = parent.getLocationOnScreen();
                        screen.translate( parentScreen.x, parentScreen.y );
                }

                return screen;
        }

        public Dimension getPreferredSize()
        {
                return size;
        }

        public Dimension getMinimumSize()
        {
                return size;
        }

        // State

        public void setVisible( boolean b )
        {
        }

        public void setEnabled( boolean b )
        {
        }

        // Focus

        public void requestFocus()
        {
        }

        public boolean isFocusTraversable()
        {
                return true;
        }

        // Events

        public void handleEvent( AWTEvent e )
        {
                System.err.println(e);
        }

        public void coalescePaintEvent( PaintEvent e )
        {
                System.err.println(e);
        }

        // Color

        public ColorModel getColorModel()
        {
                return getToolkit().getColorModel();
        }

        public void setForeground( Color c )
        {
        }

        public void setBackground( Color c )
        {
        }

        // Fonts

        public FontMetrics getFontMetrics( Font font )
        {
                return null;
        }

        public void setFont( Font f )
        {
        }


        // Misc

        public Toolkit getToolkit()
        {
        	//if( toolkit==null){
        	//	toolkit = defaultToolkit;
        	//}
        	if( toolkit==null){
        		toolkit = new VirtualToolkit();
        	}
            return toolkit;
        }



        // Image

        public Image createImage( ImageProducer producer )
        {
                System.err.println( "createImage(producer)" );
                return null;
        }

        public Image createImage( int width, int height )
        {
                Component parent = component.getParent();
                if( parent != null )
                        return parent.createImage( width, height );
                else
                        throw new Error();
        }

        public boolean prepareImage( Image img, int w, int h, ImageObserver o )
        {
                System.err.println( "prepareImage" );
                return true;
        }

        public int checkImage( Image img, int w, int h, ImageObserver o )
        {
                System.err.println( "checkImage" );
                return ImageObserver.ALLBITS;
        }

        // Deprecated

        public Dimension preferredSize()
        {
                return getPreferredSize();
        }

        public Dimension minimumSize()
        {
                return getMinimumSize();
        }

        public void show()
        {
                setVisible( true );
        }

        public void hide()
        {
                setVisible( false );
        }

        public void enable()
        {
                setEnabled( true );
        }

        public void disable()
        {
                setEnabled( false );
        }

        public void reshape( int x, int y, int width, int height )
        {
                setBounds( x, y, width, height );
        }

        public void reparent(ContainerPeer newContainer) {
    		// TODO Auto-generated method stub
    		
    	}

        ///////////////////////////////////////////////////////////////////////////////////////
        // Private

        protected Component component;
        protected Toolkit toolkit;
        protected Point location = new Point();
        protected Dimension size = new Dimension();
}
