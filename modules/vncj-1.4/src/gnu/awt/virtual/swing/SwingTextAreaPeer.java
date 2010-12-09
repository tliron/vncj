package gnu.awt.virtual.swing;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
* AWT text area peer implemented as a {@link javax.swing.JTextArea}.
**/

class SwingTextAreaPeer extends JTextArea implements TextAreaPeer
{
	//
	// Construction
	//
	
	public SwingTextAreaPeer( TextArea textArea )
	{
		super();
		
		switch( textArea.getScrollbarVisibility() )
		{
			case TextArea.SCROLLBARS_BOTH:
				SwingFramePeer.add( textArea, new JScrollPane( this, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS ) );
				break;
			case TextArea.SCROLLBARS_HORIZONTAL_ONLY:
				SwingFramePeer.add( textArea, new JScrollPane( this, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS ) );
				break;
			case TextArea.SCROLLBARS_VERTICAL_ONLY:
				SwingFramePeer.add( textArea, new JScrollPane( this, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER ) );
				break;
			case TextArea.SCROLLBARS_NONE:
				SwingFramePeer.add( textArea, this );
				break;
		}

		setText( textArea.getText() );
		setRows( textArea.getRows() );
		setColumns( textArea.getColumns() );
		setEditable( textArea.isEditable() );
	}
	
	//
	// TextAreaPeer
	//
	
	public Dimension getPreferredSize( int rows, int columns )
	{
		return null;
	}
	
	public Dimension getMinimumSize( int rows, int columns )
	{
		return null;
	}
	
	// Deprectated
	
	public void insertText( String txt, int pos )
	{
		insert( txt, pos );
	}	
	
	public void replaceText( String txt, int start, int end )
	{
		replaceRange( txt, start, end );
	}
	
	public Dimension preferredSize( int rows, int cols )
	{
		return getPreferredSize( rows, cols );
	}
	
	public Dimension minimumSize( int rows, int cols )
	{
		return getMinimumSize( rows, cols );
	}
	
	//
	// TextComponentPeer
	//
	
	public int getIndexAtPoint( int x, int y )
	{
		return 0;
	}
	
	public Rectangle getCharacterBounds( int i )
	{
		return null;
	}
	
	public long filterEvents( long mask )
	{
		return 0;
	}
	
	//
	// ComponentPeer
	//
	
	// Events
	
	public void handleEvent( AWTEvent e )
	{
		//System.err.println(e);
	}
	
	public void coalescePaintEvent( PaintEvent e )
	{
		System.err.println(e);
	}
	
	public boolean handlesWheelScrolling()
	{
		return false;
	}
	
	// Obscurity
	
	public boolean isObscured()
	{
		return false;
	}
	
	public boolean canDetermineObscurity()
	{
		return false;
	}
	
	// Focus
	
	public boolean requestFocus( Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time )
	{
		return true;
	}
	
	// Buffer
	
	public void createBuffers( int x, BufferCapabilities bufferCapabilities )
	{
	}
	
	public void destroyBuffers()
	{
	}
	
	public void flip( BufferCapabilities.FlipContents flipContents )
	{
	}
	
	public Image getBackBuffer()
	{
		return null;
	}
	
	// Cursor
	
	public void updateCursorImmediately()
	{
	}
	
	// Misc
	
	public void dispose()
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
}
