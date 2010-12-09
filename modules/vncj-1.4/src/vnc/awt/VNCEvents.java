package gnu.vnc.awt;

import gnu.rfb.*;
import gnu.rfb.server.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
* Generates AWT events from RFB events for multiple RFB clients. Note that because the event
* models do not correspond, it must manage an event state for each client as a property 
**/

public class VNCEvents
{
	//
	// Construction
	//
	
	public VNCEvents( Window container, RFBClients clients )
	{
		this.container = container;
		this.clients = clients;
	}
	
	//
	// Operations
	//
	
	public void translateKeyEvent( RFBClient client, boolean down, int key )
	{
		// Get state
		State state = getState( client );
		
		// Modifiers
		int[] mask = new int[2];
		keysym.toMask( key, mask );
		if( mask[0] != 0 )
		{
			if( down )
				state.keyModifiers |= mask[0];
			else
				state.keyModifiers &= ~mask[0];
			return;
		}
		
		int[] vk = new int[2];
		keysym.toVK( key, vk );
		char character = KeyEvent.CHAR_UNDEFINED;
		if( vk[0] == KeyEvent.VK_UNDEFINED )
		{
			// This is a character key
			character = keysym.toCharacter( key );
		}
		
		if( down )
		{
			// Pressed
			fireKeyEvent( KeyEvent.KEY_PRESSED, vk[0], vk[1], character, state.keyModifiers );
			state.keyPressed.add( "" + vk[0] );
			if( character != KeyEvent.CHAR_UNDEFINED )
			{
				// Typed (for character keys only)
				fireKeyEvent( KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, KeyEvent.KEY_LOCATION_UNKNOWN, character, state.keyModifiers );
			}
		}
		else if( state.keyPressed.remove( "" + vk[0] ) )
		{
			// We keep the state of pressed keys because some viewers sent multiple releases
			// of the same key. That's not nice.
			
			// Released
			fireKeyEvent( KeyEvent.KEY_RELEASED, vk[0], vk[1], character, state.keyModifiers );
		}
	}
	
	public void translatePointerEvent( RFBClient client, int buttonMask, int x, int y )
	{
		// Get state
		State state = getState( client );
		
		// Coordinates relative to the container
		Insets insets = container.getInsets();
		x += insets.left;
		y += insets.top;
		
		// New component
		Component newComponent;
		if( state.dragging )
			newComponent = state.oldComponent;
		else
			newComponent = container.findComponentAt( x, y );
			
		// Coordinates relative to the new component
		Point newLocation = getLocation( newComponent );
		int newX = x - newLocation.x;
		int newY = y - newLocation.y;
		
		// Button modifiers
		int newMouseModifiers = 0;
		if( ( buttonMask & rfb.Button1Mask ) != 0 )
			newMouseModifiers |= MouseEvent.BUTTON1_DOWN_MASK;
		if( ( buttonMask & rfb.Button2Mask ) != 0 )
			newMouseModifiers |= MouseEvent.BUTTON2_DOWN_MASK;
		if( ( buttonMask & rfb.Button3Mask ) != 0 )
			newMouseModifiers |= MouseEvent.BUTTON3_DOWN_MASK;
		
		// Buttons
		if( newMouseModifiers != state.mouseModifiers )
		{
			// Button 1
			if( ( ( newMouseModifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 ) &&
				( ( state.mouseModifiers & MouseEvent.BUTTON1_DOWN_MASK ) == 0 ) )
			{
				// Pressed
				fireMouseEvent( newComponent, MouseEvent.MOUSE_PRESSED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON1 );
				state.dragging = true;
			}
			else if( ( ( newMouseModifiers & MouseEvent.BUTTON1_DOWN_MASK ) == 0 ) &&
					 ( ( state.mouseModifiers & MouseEvent.BUTTON1_DOWN_MASK ) != 0 ) )
			{
				// Released
				fireMouseEvent( newComponent, MouseEvent.MOUSE_RELEASED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON1 );
				state.dragging = false;
				
				// Check for double-click
				long now = System.currentTimeMillis();
				long delta = now - state.lastClick1;
				if( delta < 300 )
				{
					// Double-click
					fireMouseEvent( newComponent, MouseEvent.MOUSE_CLICKED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON1, 2 );
					state.lastClick1 = 0;
				}
				else
				{
					state.lastClick1 = now;
				}
				
				if( newComponent.isFocusable() && !newComponent.isFocusOwner() )
				{
					// New focus
					
					// Lose old focus
					Component oldComponent = getFocusComponent( container );
					if( oldComponent != null )
					{
						fireFocusEvent( oldComponent, FocusEvent.FOCUS_LOST, newComponent );
					}
						
					// Gain new focus
					newComponent.enableInputMethods( false ); // native input methods will not work!
					fireFocusEvent( newComponent, FocusEvent.FOCUS_GAINED, oldComponent );
				}
			}
			
			// Button 2
			if( ( ( newMouseModifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 ) &&
				( ( state.mouseModifiers & MouseEvent.BUTTON2_DOWN_MASK ) == 0 ) )
			{
				// Pressed
				fireMouseEvent( newComponent, MouseEvent.MOUSE_PRESSED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON2 );
			}
			else if( ( ( newMouseModifiers & MouseEvent.BUTTON2_DOWN_MASK ) == 0 ) &&
					 ( ( state.mouseModifiers & MouseEvent.BUTTON2_DOWN_MASK ) != 0 ) )
			{
				// Released
				fireMouseEvent( newComponent, MouseEvent.MOUSE_RELEASED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON2 );
				
				// Check for double-click
				long now = System.currentTimeMillis();
				long delta = now - state.lastClick2;
				if( delta < 300 )
				{
					// Double-click
					fireMouseEvent( newComponent, MouseEvent.MOUSE_CLICKED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON2, 2 );
					state.lastClick2 = 0;
				}
				else
				{
					state.lastClick2 = now;
				}
			}
			
			// Button 3
			if( ( ( newMouseModifiers & MouseEvent.BUTTON3_DOWN_MASK ) != 0 ) &&
				( ( state.mouseModifiers & MouseEvent.BUTTON3_DOWN_MASK ) == 0 ) )
			{
				// Pressed
				fireMouseEvent( newComponent, MouseEvent.MOUSE_PRESSED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON3 );
			}
			else if( ( ( newMouseModifiers & MouseEvent.BUTTON3_DOWN_MASK ) == 0 ) &&
					 ( ( state.mouseModifiers & MouseEvent.BUTTON3_DOWN_MASK ) != 0 ) )
			{
				// Released
				fireMouseEvent( newComponent, MouseEvent.MOUSE_RELEASED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON3 );
				
				// Check for double-click
				long now = System.currentTimeMillis();
				long delta = now - state.lastClick3;
				if( delta < 300 )
				{
					// Double-click
					fireMouseEvent( newComponent, MouseEvent.MOUSE_CLICKED, newX, newY, state.keyModifiers | newMouseModifiers, MouseEvent.BUTTON3, 2 );
					state.lastClick3 = 0;
				}
				else
				{
					state.lastClick3 = now;
				}
			}
			
			state.mouseModifiers = newMouseModifiers;
		}
		
		if( newComponent != state.oldComponent )
		{
			if( state.oldComponent != null )
			{
				// Exited old component
				fireMouseEvent( state.oldComponent, MouseEvent.MOUSE_EXITED, state.oldX, state.oldY, state.keyModifiers | state.mouseModifiers );
			}
			
			// Entered new component
			fireMouseEvent( newComponent, MouseEvent.MOUSE_ENTERED, newX, newY, state.keyModifiers | state.mouseModifiers );
			
			state.oldComponent = newComponent;
			state.oldX = newX - 1; // make sure that we will get a motion event
		}
		
		if( ( newX != state.oldX ) || ( newY != state.oldY ) )
		{
			// New location
			if( state.dragging )
			{
				// Dragged (button pressed)
				fireMouseEvent( newComponent, MouseEvent.MOUSE_DRAGGED, newX, newY, state.keyModifiers | state.mouseModifiers );
			}
			else
			{
				// Moved (no button pressed)
				fireMouseEvent( newComponent, MouseEvent.MOUSE_MOVED, newX, newY, state.keyModifiers | state.mouseModifiers );
			}
			
			state.oldX = newX;
			state.oldY = newY;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private Window container;
	private RFBClients clients;
	
	private static class State
	{
		public int keyModifiers = 0;
		public Set keyPressed = new HashSet();
		public int mouseModifiers = 0;
		public Component oldComponent = null;
		public int oldX, oldY;
		public long lastClick1 = 0, lastClick2 = 0, lastClick3 = 0;
		public boolean dragging = false;
	}
	
	private State getState( RFBClient client )
	{
		State state = (State) clients.getProperty( client, "events" );
		if( state == null )
		{
			state = new State();
			clients.setProperty( client, "events", state );
		}
		
		return state;
	}
	
	private void fireEvent( ComponentEvent event )
	{
		Component source = (Component) event.getSource();
		if( source != null )
			source.getToolkit().getSystemEventQueue().postEvent( event );
	}
	
	private void fireKeyEvent( int id, int vk, int location, char character, int modifiers )
	{
		fireEvent( new KeyEvent( getFocusComponent( container ), id, System.currentTimeMillis(), modifiers, vk, character, location ) );
	}
	
	private void fireFocusEvent( Component component, int id, Component opposite )
	{
		fireEvent( new FocusEvent( component, id, true, opposite ) );
	}
	
	private void fireMouseEvent( Component component, int id, int x, int y, int modifiers )
	{
		fireMouseEvent( component, id, x, y, modifiers, MouseEvent.NOBUTTON, 0 );
	}
	
	private void fireMouseEvent( Component component, int id, int x, int y, int modifiers, int button )
	{
		fireMouseEvent( component, id, x, y, modifiers, button, 0 );
	}
	
	private void fireMouseEvent( Component component, int id, int x, int y, int modifiers, int button, int clicks )
	{
		fireEvent( new MouseEvent( component, id, System.currentTimeMillis(), modifiers, x, y, clicks, button == MouseEvent.BUTTON2, button ) );
	}
	
	private Point getLocation( Component component )
	{
		Point location = component.getLocation();
		if( component == container )
		{
			return location;
		}
		else
		{
			Component parent = component.getParent();
			if( parent != null )
			{
				Point parentLocation = getLocation( parent );
				return new Point( location.x + parentLocation.x, location.y + parentLocation.y );
			}
			else
			{
				return location;
			}
		}
	}
	
	private Component getFocusComponent( Component component )
	{
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		/*if( component.hasFocus() )
		{
			// We have focus
			return component;
		}
		else if( component instanceof Container )
		{
			// Does one of our children have the focus?
			Component[] components = ((Container) component).getComponents();
			Component focusComponent = null;
			for( int i = 0; i < components.length; i++ )
			{
				focusComponent = getFocusComponent( components[i] );
				if( focusComponent != null )
					// Has focus!
					return focusComponent;
			}
		}
		
		return null;*/
	}
}
