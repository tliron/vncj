package gnu.vnc.awt;

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

import gnu.rfb.*;
import gnu.rfb.server.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class VNCEvents {
    
    /**
     *  for debug-loging (using Log4J or System.err depending on avliability)
     */
    static Object log;
    /**
     *  for debug-loging (using Log4J or System.err depending on avliability)
     */
    static java.lang.reflect.Method logmethod;
    
    /**
     *  for debug-loging (using Log4J or System.err depending on avliability)
     */
    private void logDebug(String msg) {
        try {
            if(log==null)
            {
                log = System.err;
                logmethod = java.io.PrintStream.class.getMethod("println", new Class[]{String.class});
            }
            
            logmethod.invoke(log, new Object[]{msg});
        }
        catch(Exception x) {
            x.printStackTrace();
            System.err.println(msg);
        }
    }
    //
    // Construction
    //
    //char character;
    
    public VNCEvents( Window container, RFBClients clients ) {
        this.container = container;
        this.clients = clients;
    }
    
    //
    // Operations
    //
    
    /**
     * translate the from the RFBClient received KeyEvent and 
     * send it to the fireKeyEvent method. 
     * At the time key is the primaryLevelUnicode key, and the KeyEvent needs the raw code
     * TODO add or use the list from KeyEvent.class to extend the vk-list in keysym.java
     */
    public void translateKeyEvent( RFBClient client, boolean down, int key ) {
        // Get state
        State state = getState( client );

        // Characters  
        //  only RETRANSLATE the characters
        char character = (char)key;

        // Modifiers
        int newKeyModifiers = keysym.toMask( key );
        if( newKeyModifiers != 0x0 ) {
            if( down )
                state.keyModifiers |= newKeyModifiers;
            else
                state.keyModifiers &= ~newKeyModifiers;
            // do not return, because swing needs also the modify keys as events
            //return;
        }
        
        // check for capital character without pressed shift 
        if ((state.keyModifiers %2)==0 & Character.isUpperCase(character) )
        	state.keyModifiers |= KeyEvent.SHIFT_MASK;
     
        // Virtual Key Code
        int virtualKeyCode = keysym.toVKall( key );

        if (virtualKeyCode == 0x0)
        {
        	virtualKeyCode = key;
        }
        //System.out.println("DEBUG[VNCEvents] translateKeyEvent() state.oldComponent="+state.oldComponent.getClass().getName());
        
        if( down ) {
            // Pressed
            fireKeyEvent(client, KeyEvent.KEY_PRESSED, virtualKeyCode, character, state.keyModifiers , null);
        }
        else {
        	// Typed (for character and number keys only)
            if( (key >= 0x0020 & key <= 0x007E) |
            		((key >= 0x00A0) & (key <= 0x00FF)))//  || ( virtualKey == KeyEvent.VK_BACK_SPACE ) )
            {
            	// !!! the virtualKeyCode should stay 0
                fireKeyEvent(client, KeyEvent.KEY_TYPED, 0, character, state.keyModifiers , null );
            }
            // Released
            fireKeyEvent(client, KeyEvent.KEY_RELEASED, virtualKeyCode, character, state.keyModifiers , null );            
        }
    }
    
    public void translatePointerEvent( RFBClient client, int buttonMask, int x, int y ) {
        // Get state
        State state = getState( client );
        
        // Modifiers
        int newMouseModifiers = 0;
        int wheelRotation = 0;
        boolean pressed = false;
        int button = -1;
        if( ( buttonMask & rfb.Button1Mask ) != 0 ){
            button = 0;
            newMouseModifiers |= MouseEvent.BUTTON1_MASK;
            pressed = (newMouseModifiers & MouseEvent.BUTTON1_MASK) > 0;
        }
        if( ( buttonMask & rfb.Button2Mask ) != 0 ){
            button = 1;
            newMouseModifiers |= MouseEvent.BUTTON2_MASK;
            pressed = (newMouseModifiers & MouseEvent.BUTTON2_MASK) > 0;
        }
        if( ( buttonMask & rfb.Button3Mask ) != 0 ){
            button = 2;
            newMouseModifiers |= MouseEvent.BUTTON3_MASK;
            pressed = (newMouseModifiers & MouseEvent.BUTTON3_MASK) > 0;
        }
        if ( ( buttonMask & rfb.Button4Mask) != 0 ){
        	// wheel up
        	wheelRotation = -1;
        }
        if ( ( buttonMask & rfb.Button5Mask) != 0 ){
        	// wheel down
        	wheelRotation = 1;
        }
        
        // Coordinates relative to the container
        Insets insets = container.getInsets();
        //         logDebug("translatePointerEvent(x="+x+" insets.left="+insets.left+", y="+y+" insets.top="+insets.top+"");
        x += insets.left;
        y += insets.top;
        
        // New component
        Component newComponent;
        if( state.dragging )
            newComponent = state.oldComponent;
        else
            newComponent = container.findComponentAt( x, y );
                
    	//System.out.println(newComponent);
        
        // Wheel Events
        // TODO maybe it is better to avoid firing MOUSE_MOVED on MOUSE_WHEEL 
        if (wheelRotation != 0)
        	fireWheelEvent(client, container, MouseEvent.MOUSE_WHEEL, x, y, 0, state.keyModifiers | state.mouseModifiers, wheelRotation);
        
        state.dragging = false;
        if( newMouseModifiers == state.mouseModifiers ) {
            // No buttons changed state
            if( newMouseModifiers == 0 ) {
                // Moved (no button pressed)
                fireMouseEvent(client, container, MouseEvent.MOUSE_MOVED, x, y, 0, state.keyModifiers | state.mouseModifiers );
            }
            else {
                // Dragged (button pressed)
                state.dragging = true;
                fireMouseEvent(client, container, MouseEvent.MOUSE_DRAGGED, x, y, 0, state.keyModifiers | state.mouseModifiers );
            }
        }
        else {
            if( pressed == true){
                int numClicks = 1;
                
                // if 300ms since last click, doubleclick
                long diff = System.currentTimeMillis()-state.lastMouseClickTime[button];
                if(diff < 1000){
                    numClicks = 2;
                }
                // Pressed
                state.mouseModifiers = newMouseModifiers;
                state.lastMouseClickTime[button]= System.currentTimeMillis();
                fireMouseEvent(client, container, MouseEvent.MOUSE_PRESSED, x, y, numClicks, state.keyModifiers | state.mouseModifiers );
                
                state.lastMouseClickTime[button] = System.currentTimeMillis();
                fireMouseEvent(client, container, MouseEvent.MOUSE_CLICKED, x, y, numClicks, state.keyModifiers | state.mouseModifiers );
            }
            else{
                // Released (old modifiers)
                fireMouseEvent(client, container, MouseEvent.MOUSE_RELEASED, x, y, 0, state.keyModifiers | state.mouseModifiers );
            }
            
            // Clicked (old modifiers)
            
            state.mouseModifiers = newMouseModifiers;
            
            // Focus
            Component focusComponent = getFocusComponent( container );
            if( focusComponent != container ) {
                // Lose old focus
                if( focusComponent != null ) {
                    fireEvent(client, new FocusEvent( focusComponent, FocusEvent.FOCUS_LOST ) );
                }
                
                // parent gains focus too
                if(newComponent != null && newComponent.getParent()!=null){
                        fireEvent( client,new FocusEvent( newComponent.getParent(), FocusEvent.FOCUS_GAINED ) );
                        // Gain new focus
                        fireEvent(client, new FocusEvent( newComponent, FocusEvent.FOCUS_GAINED ) );
                }else{
                	System.out.println("bad:"+newComponent);
                }
                focusComponent = container;
            }
        }
        
        
        if( newComponent != state.oldComponent ) {
            if( state.oldComponent != null ) {
                // Exited old component
                fireMouseEvent(client, container, MouseEvent.MOUSE_EXITED, state.oldX, state.oldY, 0, state.keyModifiers | state.mouseModifiers );
            }
            
            // Entered new component
            fireMouseEvent(client, container, MouseEvent.MOUSE_ENTERED, x, y, 0, state.keyModifiers | state.mouseModifiers );
            
            state.oldComponent = newComponent;
            state.oldX = x;
            state.oldY = y;
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////
    // Private
    
    private Window container;
    private RFBClients clients;
    private static HashMap eventMap = new HashMap();
    
    private static class State {
        public int keyModifiers = 0;
        public int mouseModifiers = 0;
        public Component oldComponent = null;
        public int oldX;
		public int oldY;
        public boolean dragging = false;
        public long lastMouseClickTime[] = new long[5];
        
    }
    
    public static HashMap getEventMap(){
        return eventMap;
    }
    
    private State getState( RFBClient client ) {
        State state = (State) clients.getProperty( client, "events" );
        if( state == null ) {
            state = new State();
            clients.setProperty( client, "events", state );
        }
        
        return state;
    }
    
    private void fireEvent(RFBClient client, ComponentEvent event ) {
        if(event==null) {
            System.err.println("VNCEvents.fireEvent(event==null)");
            return;
        }

        ((Component) event.getSource()).enableInputMethods(false); 
        /*
         *  InputMethods causes problems under Windows, because the event
         *  ends up in a java.awt.windows-class that needs all peers to be 
         *  children in WComponentPeer and our peers are not 
         */ 

        /*
         try
           {
            logDebug("fireEvent(["+event.getClass().getName()+"]"+event.paramString()+") parent=["+(((Component) event.getSource()).getParent()==null?"null":((Component) event.getSource()).getParent().getClass().getName())+"] event.getSource().isVisible()="+((Component)event.getSource()).isVisible());
           }
         catch(Exception e)
           {
            e.printStackTrace();
           }
         */
        
        getEventMap().put(event,((RFBSocket)client).getInetAddress().getHostName());
        ((Component) event.getSource()).getToolkit().getSystemEventQueue().postEvent( event );
    }
    
    /*
     * The function to create the KeyEvent
     * 		TODO: check if there is a need of the location translation
     */
    private void fireKeyEvent(RFBClient client, int id, int vk, char character, int keyModifiers, Component component ) {
    	
        KeyEvent ke = new KeyEvent( container, id, System.currentTimeMillis(), keyModifiers, vk, character, KeyEvent.KEY_LOCATION_UNKNOWN );
        //logDebug("fireKeyEvent(id="+id+", vk="+vk+" character="+character+" getKeyText="+ke.getKeyText(ke.getKeyCode())+" container="+container.getClass().getName()+"focused compunent="+(component==null?"null":component.getClass().getName()));
        fireEvent(client, ke );
    }
    
    private void fireMouseEvent(RFBClient client, Component component, int id, int x, int y, int clicks, int modifiers ) {
        //         logDebug("fireMouseEvent(x="+x+", y="+y+" component=["+component.getClass().getName()+"]");
        fireEvent(client, new MouseEvent( component, id, System.currentTimeMillis(), modifiers, x, y, clicks, false ) );
    }
    
    private void fireWheelEvent(RFBClient client, Component component, int id, int x, int y, int clicks, int modifiers, int rotation ) {
    	fireEvent(client, new MouseWheelEvent(component, id, System.currentTimeMillis(), modifiers, x, y, clicks, false, 
    			MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, rotation));
    }
    private Point getLocation( Component component ) {
        Point location = component.getLocation();
        if( component == container ) {
            return location;
        }
        else {
            Component parent = component.getParent();
            if( parent != null ) {
                Point parentLocation = parent.getLocationOnScreen();//getLocation( parent ); // this recursive algorithm gave gigantic values in some cases, the getLocationOnScreen actually works
                //                                         logDebug("getLocation(parent.getLocationOnScreen()="+parentLocation);
                return new Point( location.x - parentLocation.x, location.y - parentLocation.y );
            }
            else {
                return location;
            }
        }
    }
    
    private Component getFocusComponent( Component component ) {
        if( component.hasFocus() ) {
            // We have focus
            return component;
        }
        else if( component instanceof Container ) {
            // Does one of our children have the focus?
            Component[] components = ((Container) component).getComponents();
            Component focusComponent = null;
            for( int i = 0; i < components.length; i++ ) {
                focusComponent = getFocusComponent( components[i] );
                if( focusComponent != null )
                    // Has focus!
                    return focusComponent;
            }
        }
        
        return null;
    }
}
