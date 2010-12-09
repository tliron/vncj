package gnu.vnc;

import gnu.rfb.server.*;

import vncjdemo.*;

import java.util.*;

/**
* Main method creates a group of {@link gnu.rfb.server.RFBServer RFB servers} and
* {@link gnu.vnc.WebServer VNC-viewer-applet-serving web servers} according to
* information provided in the 'vnc.properties' file.
**/

public class VNCHost
{
	//
	// Main
	//

	public static void main( String args[] )
	{
		//VirtualToolkit.setDefaultToolkit( "gnu.awt.virtual.swing.VirtualSwingToolkit" );
		
		ResourceBundle properties = null;
		try
		{
			properties = ResourceBundle.getBundle( "vncj" );
		}
		catch( MissingResourceException x )
		{
			System.err.println( "Can't load 'vncj.properties'." );
			return;
		}
		
		// Create RFB hosts and web servers
		String key, serverClassName, displayName, password = null, restrictedTo = null, noPasswordFor = null;
		Class serverClass;
		int display, width, height;
		for( Enumeration e = properties.getKeys(); e.hasMoreElements(); )
		{
			key = (String) e.nextElement();
			if( key.endsWith( ".class" ) )
			{
				display = Integer.parseInt( key.substring( 0, key.indexOf( '.' ) ) );
				serverClassName = properties.getString( key );
				displayName = properties.getString( display + ".name" );
				width = Integer.parseInt( properties.getString( display + ".width" ) );
				height = Integer.parseInt( properties.getString( display + ".height" ) );
				try
				{
					password = properties.getString( display + ".password" );
				}
				catch( MissingResourceException x )
				{
				}
				try
				{
					restrictedTo = properties.getString( display + ".restrictedTo" );
				}
				catch( MissingResourceException x )
				{
				}
				try
				{
					noPasswordFor = properties.getString( display + ".noPasswordFor" );
				}
				catch( MissingResourceException x )
				{
				}
				
				System.out.println( displayName );
				
				try
				{
					serverClass = Class.forName( serverClassName );
					
					// RFB host
					new RFBHost( display, displayName, serverClass, width, height, new DefaultRFBAuthenticator( password, restrictedTo, noPasswordFor ) );
					
					// Webserver
					new WebServer( display, displayName, width, height );
					
					System.out.println( "  VNC display " + display );
					System.out.println( "  Web server on port " + ( 5800 + display ) );
					System.out.println( "  Class: " + serverClassName );
				}
				catch( ClassNotFoundException x )
				{
					System.out.println( "  Could not load class: " + serverClassName );
				}
				catch( NoSuchMethodException x )
				{
					System.out.println( "  Unsupported class: " + serverClassName );
				}
			}
		}
	}
}
