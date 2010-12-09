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
import java.awt.peer.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.dnd.peer.*;
import java.awt.datatransfer.*;
import java.awt.im.*;
import java.awt.color.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * we derive from sun.awt.SunToolkit because GlobalCursorManager
 * depends on it in Windows if we are the default-toolkit.
 */
public class VirtualToolkit extends sun.awt.SunToolkit//Toolkit
{
        //
        // Static attributes
        //

        private static EventQueue eventQueue = new EventQueue();
        
        public void setDefaultToolkit( String className )
        {
                setDefaultToolkit();
                System.setProperty( "awt.toolkit", className );
        }

        //
        // Construction
        //

        public VirtualToolkit( DirectColorModel colorModel, int width, int height )
        {
                super();

                if( defaultToolkit == null )
                        setDefaultToolkit();

                this.colorModel = colorModel;

                screenSize = new Dimension( width, height );
        }

        public VirtualToolkit( int depth, int rMask, int gMask, int bMask, int width, int height )
        {
                this( new DirectColorModel( ColorSpace.getInstance( ColorSpace.CS_sRGB ), depth, rMask, gMask, bMask, 0, true, DataBuffer.TYPE_INT ), width, height );
        }

        
        
        public VirtualToolkit( int width, int height )
        {
                this( 24, 0xFF0000, 0xFF00, 0xFF, width, height );
        }

        public VirtualToolkit(EventQueue eventQueue){
            this(10000,10000);
            this.eventQueue = eventQueue;
        }
        public VirtualToolkit()
        {
                this( 10000, 10000 );
        }

        //
        // Operations
        //

        public void setColorModel( DirectColorModel colorModel )
        {
                this.colorModel = colorModel;
        }

        //
        // Toolkit
        //

        // Peers

        public ButtonPeer createButton( Button target )
        {
                return null;
        }

        public CanvasPeer createCanvas( Canvas target )
        {
                return null;
        }

        public CheckboxPeer createCheckbox( Checkbox target )
        {
                return null;
        }

        public CheckboxMenuItemPeer createCheckboxMenuItem( CheckboxMenuItem target )
        {
                return null;
        }

        public ChoicePeer createChoice( Choice target )
        {
                return null;
        }

        protected LightweightPeer createComponent( Component target )
        {
                return new VirtualLightweightPeer(this, target ); // using new constructor by Marcus Wolschon
        }

        public DialogPeer createDialog( Dialog target )
        {
                return null;
        }

        public DragSourceContextPeer createDragSourceContextPeer( DragGestureEvent dge )
        {
                return null;
        }

        public FileDialogPeer createFileDialog( FileDialog target )
        {
                return null;
        }

        public FramePeer createFrame( Frame target )
        {

                if( !( target instanceof PixelsOwner ) )
                {
                   //test   throw new Error( "Virtual toolkit does not support this frame - it is not a PixelsOwner" );
                   System.err.println("ERROR[VirtualToolkit]: Virtual toolkit does not support this frame - it is not a PixelsOwner - let's hope it is newer made visible");
                   return new VirtualFramePeer( target, null );
                }

                return new VirtualFramePeer( target, (PixelsOwner) target );
        }

        public LabelPeer createLabel( Label target )
        {
                return null;
        }

        public ListPeer createList( java.awt.List target )
        {
                return null;
        }

        public MenuPeer createMenu( Menu target )
        {
                return null;
        }

        public MenuBarPeer createMenuBar( MenuBar target )
        {
                return null;
        }

        public MenuItemPeer createMenuItem( MenuItem target )
        {
                return null;
        }

        public PanelPeer createPanel( Panel target )
        {
                return null;
        }

        public PopupMenuPeer createPopupMenu( PopupMenu target )
        {
                return null;
        }

        public ScrollbarPeer createScrollbar( Scrollbar target )
        {
                return null;
        }

        public ScrollPanePeer createScrollPane( ScrollPane target )
        {
                return null;
        }

        public TextAreaPeer createTextArea( TextArea target )
        {
                return null;
        }

        public TextFieldPeer createTextField( TextField target )
        {
                return null;
        }

        public WindowPeer createWindow( Window target )
        {
                return null;
        }

        // Images

        public int checkImage( Image image, int width, int height, ImageObserver observer )
        {
                return defaultToolkit.checkImage( image, width, height, observer );
        }

        public Image createImage( byte[] imagedata, int imageoffset, int imagelength )
        {
                return defaultToolkit.createImage( imagedata, imageoffset, imagelength );
        }

        public Image createImage( ImageProducer producer )
        {
                return defaultToolkit.createImage( producer );
        }

        public Image createImage( String filename )
        {
                return defaultToolkit.createImage( filename );
        }

        public Image createImage( URL url )
        {
                return defaultToolkit.createImage( url );
        }

        public Image getImage( String filename )
        {
                return defaultToolkit.getImage( filename );
        }

        public Image getImage( URL url )
        {
                return defaultToolkit.getImage( url );
        }

        public boolean prepareImage( Image image, int width, int height, ImageObserver observer )
        {
                return defaultToolkit.prepareImage( image, width, height, observer );
        }

        // Color

        public ColorModel getColorModel()
        {
                return colorModel;
        }

        // Fonts

        public String[] getFontList()
        {
                return defaultToolkit.getFontList();
        }

        public FontMetrics getFontMetrics( Font font )
        {
                return defaultToolkit.getFontMetrics( font );
        }

        public FontPeer getFontPeer( String name, int style )
        {
                return null;
                //protected: return defaultToolkit.getFontPeer( name, style );
        }

        // Other

        public void beep()
        {
                // Quiet, please!
        }

        public PrintJob getPrintJob( Frame frame, String jobtitle, Properties prop )
        {
                return null;
        }

        public int getScreenResolution()
        {
                return -1;
        }

        public Dimension getScreenSize()
        {
                return screenSize;
        }

        public Clipboard getSystemClipboard()
        {
                // TODO: virtual clipboard
                return null;
        }

        protected EventQueue getSystemEventQueueImpl()
        {
         return eventQueue;
//                return defaultToolkit.getSystemEventQueue(); //TODO: try using our own instance of EventQueue
                //return Toolkit.getDefaultToolkit().getSystemEventQueue();
                //protected: return defaultToolkit.getSystemEventQueueImpl();
        }  
        
        public EventQueue getTheEventQueue(){
            // Hack Hack hack
            return eventQueue;
        }

        public Map mapInputMethodHighlight( InputMethodHighlight highlight )
        {
                return null;
        }

        public void sync()
        {
        }


        ///////////////////////////////////////////////////////////////////////////////////////
        // from sun.awt.SunToolkit


       //public sun.awt.GlobalCursorManager getGlobalCursorManager()
       //{
       // return defaultToolkit.getGlobalCursorManager();
       //}

       //public String getDefaultUnicodeEncoding()
       //{
       // return defaultToolkit.getDefaultUnicodeEncoding();
       //}

       public java.awt.im.spi.InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException
       {
        return defaultToolkit.getInputMethodAdapterDescriptor();
       }

       public java.awt.peer.RobotPeer createRobot(java.awt.Robot p0, java.awt.GraphicsDevice p1) throws AWTException
       {
        return defaultToolkit.createRobot(p0, p1);
       }

       protected int getScreenWidth()
       {
        return getScreenSize().width;
       }
       protected int getScreenHeight()
       {
        return getScreenSize().height;
       }


        ///////////////////////////////////////////////////////////////////////////////////////
        // Private

        protected sun.awt.SunToolkit defaultToolkit = null;
        private DirectColorModel colorModel;
        private Dimension screenSize;

        private void setDefaultToolkit()
        {
        	//System.out.println(System.getProperty("awt.toolkit"));	
         //if(System.getProperty("awt.toolkit","sun.awt.motif.MToolkit").endsWith("VirtualToolkit"))
         //  {
            boolean notfound = true;
            String configuredDefaultToolkit = System.getProperty("gnu.awt.virtual.VirtualToolkit.OldDefaultToolkit");
            if(configuredDefaultToolkit != null)
            try
              {
               defaultToolkit = (sun.awt.SunToolkit)Class.forName(configuredDefaultToolkit).newInstance();
               return;
              }
            catch(Exception x)
              {}
            try
              {
               defaultToolkit = (sun.awt.SunToolkit)Class.forName("sun.awt.motif.MToolkit").newInstance();
               return;
              }
            catch(Exception x)
              {}
            try
              {
               defaultToolkit = (sun.awt.SunToolkit)Class.forName("sun.awt.windows.WToolkit").newInstance();
               return;
              }
            catch(Exception x)
              {}
            defaultToolkit = null;
            if(notfound)
           
              System.err.println("ERROR[gnu.awt.virtual.VirtualToolkit]: could not guess System-toolkit, please set the system-property \"gnu.awt.virtual.VirtualToolkit.OldDefaultToolkit\" to the classname your system-toolkit!");
         //  }
         //else
           defaultToolkit = (sun.awt.SunToolkit)Toolkit.getDefaultToolkit(); // this would cause an infinite loop if we are the default-toolkit
         //ZLi changes
         //sun.awt.SunToolkit.lastMetrics = defaultToolkit.lastMetrics;

                /*
                try
                {
                        Class defaultToolkitClass = Class.forName( System.getProperty( "awt.toolkit" ) );
                        VirtualToolkit.defaultToolkit = (Toolkit) defaultToolkitClass.newInstance();
                }
                catch( Exception x )
                {
                        x.printStackTrace();
                }

                loadAssistive();*/
        }

        public String getDefaultCharacterEncoding() {
            return null;
        }

		@Override
		public SystemTrayPeer createSystemTray(SystemTray arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TrayIconPeer createTrayIcon(TrayIcon arg0)
				throws HeadlessException, AWTException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void grab(Window arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isDesktopSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isTraySupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isWindowOpacityControlSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isWindowShapingSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isWindowTranslucencySupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected boolean syncNativeQueue() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void ungrab(Window arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected DesktopPeer createDesktopPeer(Desktop target)
				throws HeadlessException {
			// TODO Auto-generated method stub
			return null;
		}
        
        /*private static void loadAssistive()
        {
            final String sep = File.separator;
            final Properties properties = new Properties();

                java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction() {
                    public Object run() {
                        try {
                            File propsFile = new File(
                              System.getProperty("java.home") + sep + "lib" +
                              sep + "accessibility.properties");
                            FileInputStream in =
                                new FileInputStream(propsFile);
                            properties.load(new BufferedInputStream(in));
                            in.close();
                        } catch (Exception e) {
                                    // File does not exist; no classes will be auto loaded
                        }
                                return null;
                    }
                }
                );

            String atNames = properties.getProperty("assistive_technologies",null);
                ClassLoader cl = ClassLoader.getSystemClassLoader();

            if (atNames != null) {
                StringTokenizer parser = new StringTokenizer(atNames," ,");
            String atName;
                while (parser.hasMoreTokens()) {
                atName = parser.nextToken();
                    try {
                    Class clazz;
                    if (cl != null) {
                        clazz = cl.loadClass(atName);
                    } else {
                        clazz = Class.forName(atName);
                    }
                    clazz.newInstance();
                    } catch (ClassNotFoundException e) {
                        throw new AWTError("Assistive Technology not found: "
                            + atName);
                    } catch (InstantiationException e) {
                        throw new AWTError("Could not instantiate Assistive"
                            + " Technology: " + atName);
                    } catch (IllegalAccessException e) {
                        throw new AWTError("Could not access Assistive"
                            + " Technology: " + atName);
                    } catch (Exception e) {
                        throw new AWTError("Error trying to install Assistive"
                            + " Technology: " + atName + " " + e);
                    }
                }
            }
        }*/
}
