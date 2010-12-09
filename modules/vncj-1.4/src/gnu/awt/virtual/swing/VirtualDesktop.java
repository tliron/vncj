package gnu.awt.virtual.swing;

import gnu.awt.virtual.*;
import gnu.swing.virtual.*;

import java.awt.*;
import java.awt.peer.*;
import java.awt.dnd.*;
import java.awt.dnd.peer.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT toolkit implemented entirely with JFC peers, thus allowing a lightweight simulation
* of the operating system desktop.
**/

public class VirtualDesktop extends VirtualToolkit
{
	//
	// Construction
	//
	
	public VirtualDesktop( DirectColorModel colorModel, String title, int width, int height )
	{
		super( colorModel, width, height );
		init( title );
	}
	
	public VirtualDesktop( int bitsPerPixel, int rMask, int gMask, int bMask, String title, int width, int height )
	{
		super( bitsPerPixel, rMask, gMask, bMask, width, height );
		init( title );
	}
	
	public VirtualDesktop( String title, int width, int height )
	{
		super( width, height );
		init( title );
	}
	
	//
	// Operations
	//
	
	public void show()
	{
		desktopFrame.show();
	}
	
	public void dispose()
	{
		desktopFrame.dispose();
	}
	
	//
	// Toolkit
	//

	// Peers
	
	protected ButtonPeer createButton( Button target )
	{
		return new SwingButtonPeer( target );
	}

	protected CanvasPeer createCanvas( Canvas target )
	{
		//return super.createCanvas( target );
		return new SwingCanvasPeer( target );
	}

	protected CheckboxPeer createCheckbox( Checkbox target )
	{
		return new SwingCheckboxPeer( target );
	}

	protected CheckboxMenuItemPeer createCheckboxMenuItem( CheckboxMenuItem target )
	{
		return new SwingCheckboxMenuItemPeer( target );
	}

	protected ChoicePeer createChoice( Choice target )
	{
		return new SwingChoicePeer( target );
	}

	protected LightweightPeer createComponent( Component target )
	{
		return super.createComponent( target );
	}

	protected DialogPeer createDialog( Dialog target )
	{
		return new SwingDialogPeer( target );
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
		if( !initialized )
		{
			// Only desktop is real frame
			initialized = true;
			return super.createFrame( target );
		}
		else
			// Other frames are emulated
			return new SwingFramePeer( desktop, target );
	}

	protected LabelPeer createLabel( Label target )
	{
		return new SwingLabelPeer( target );
	}

	protected ListPeer createList( java.awt.List target )
	{
		return new SwingListPeer( target );
	}

	protected MenuPeer createMenu( Menu target )
	{
		return new SwingMenuPeer( target );
	}

	protected MenuBarPeer createMenuBar( MenuBar target )
	{
		return new SwingMenuBarPeer( target );
	}

	protected MenuItemPeer createMenuItem( MenuItem target )
	{
		return new SwingMenuItemPeer( target );
	}

	protected PanelPeer createPanel( Panel target )
	{
		return new SwingPanelPeer( target );
	}

	protected PopupMenuPeer createPopupMenu( PopupMenu target )
	{
		return new SwingPopupMenuPeer( target );
	}

	protected ScrollbarPeer createScrollbar( Scrollbar target )
	{
		return new SwingScrollbarPeer( target );
	}

	protected ScrollPanePeer createScrollPane( ScrollPane target )
	{
		return new SwingScrollPanePeer( target );
	}

	protected TextAreaPeer createTextArea( TextArea target )
	{
		return new SwingTextAreaPeer( target );
	}

	protected TextFieldPeer createTextField( TextField target )
	{
		return new SwingTextFieldPeer( target );
	}

	protected WindowPeer createWindow( Window target )
	{
		return new SwingWindowPeer( target );
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	protected VirtualJFrame desktopFrame = null;
	protected JDesktopPane desktop = null;
	private boolean initialized = false;
	
	private void init( String title )
	{
		desktopFrame = new VirtualJFrame( this, title );
		desktopFrame.setSize( getScreenSize().width, getScreenSize().height );
		desktop = new JDesktopPane();
		desktopFrame.getContentPane().add( desktop );
	}
}
