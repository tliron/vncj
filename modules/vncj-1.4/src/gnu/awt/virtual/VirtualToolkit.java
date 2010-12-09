package gnu.awt.virtual;

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
* This AWT toolkit does not create any native peers (hence, it is virtual). Because it does not
* target a real display device, it can support any screen size and color model. Any frame created
* with it must support the {@link gnu.awt.PixelsOwner} interface.
**/

public class VirtualToolkit extends Toolkit
{
	//
	// Static attributes
	//
	
	public static void setDefaultToolkit( String className )
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
	
	protected ButtonPeer createButton( Button target )
	{
		return null;
	}

	protected CanvasPeer createCanvas( Canvas target )
	{
		return null;
	}

	protected CheckboxPeer createCheckbox( Checkbox target )
	{
		return null;
	}

	protected CheckboxMenuItemPeer createCheckboxMenuItem( CheckboxMenuItem target )
	{
		return null;
	}

	protected ChoicePeer createChoice( Choice target )
	{
		return null;
	}

	protected LightweightPeer createComponent( Component target )
	{
		return new VirtualLightweightPeer( target );
	}

	protected DialogPeer createDialog( Dialog target )
	{
		return null;
	}

	public DragSourceContextPeer createDragSourceContextPeer( DragGestureEvent dge )
	{
		return null;
	}

	protected FileDialogPeer createFileDialog( FileDialog target )
	{
		return null;
	}
 
	protected FramePeer createFrame( Frame target )
	{
		if( !( target instanceof PixelsOwner ) )
			throw new Error( "Virtual toolkit does not support this frame - it is not a PixelsOwner" );
		
		return new VirtualFramePeer( target, (PixelsOwner) target );
	}

	protected LabelPeer createLabel( Label target )
	{
		return null;
	}

	protected ListPeer createList( java.awt.List target )
	{
		return null;
	}

	protected MenuPeer createMenu( Menu target )
	{
		return null;
	}

	protected MenuBarPeer createMenuBar( MenuBar target )
	{
		return null;
	}

	protected MenuItemPeer createMenuItem( MenuItem target )
	{
		return null;
	}

	protected PanelPeer createPanel( Panel target )
	{
		return null;
	}

	protected PopupMenuPeer createPopupMenu( PopupMenu target )
	{
		return null;
	}

	protected ScrollbarPeer createScrollbar( Scrollbar target )
	{
		return null;
	}

	protected ScrollPanePeer createScrollPane( ScrollPane target )
	{
		return null;
	}

	protected TextAreaPeer createTextArea( TextArea target )
	{
		return null;
	}

	protected TextFieldPeer createTextField( TextField target )
	{
		return null;
	}

	protected WindowPeer createWindow( Window target )
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
	
	protected FontPeer getFontPeer( String name, int style )
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
		return defaultToolkit.getSystemEventQueue();
		//return Toolkit.getDefaultToolkit().getSystemEventQueue();
		//protected: return defaultToolkit.getSystemEventQueueImpl();
	}

	public Map mapInputMethodHighlight( InputMethodHighlight highlight )
	{
		return null;
	}

	public void sync()
	{
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	protected static Toolkit defaultToolkit = null;
	private DirectColorModel colorModel;
 	private Dimension screenSize;

	private static void setDefaultToolkit()
	{
		defaultToolkit = Toolkit.getDefaultToolkit();	
	
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
