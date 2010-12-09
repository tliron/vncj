package gnu.rfb.server;

import java.net.*;
import java.io.*;
import java.lang.reflect.*;

/**
* Waits on a standard VNC socket and creates an {@link gnu.rfb.server.RFBServer RFBServer}
* implementation for each new, authenticated client.
**/

public class RFBHost implements Runnable
{
	//
	// Construction
	//
	
	public RFBHost( int display, String displayName, Class rfbServerClass, int width, int height, RFBAuthenticator authenticator ) throws NoSuchMethodException
	{
		// Get constructor
		constructor = rfbServerClass.getDeclaredConstructor( new Class[] { int.class, String.class, int.class, int.class } );
		
		// Are we assignable to RFBServer
		if( !RFBServer.class.isAssignableFrom( rfbServerClass ) )
			throw new NoSuchMethodException( "Class " + rfbServerClass + " does not support RFBServer interface" );
		
		this.display = display;
		this.displayName = displayName;
		this.width = width;
		this.height = height;
		this.authenticator = authenticator;
		
		// Start listener thread
		new Thread( this, "RFBHost-" + display ).start();
	}
	
	//
	// Operations
	//
	
	public synchronized void setSharedServer( RFBServer sharedServer )
	{
		this.sharedServer = sharedServer;
	}
	
	public synchronized RFBServer getSharedServer()
	{
		return sharedServer;
	}
	
	//
	// Runnable
	//
	
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket( 5900 + display );
			while( true )
			{
				// Create client for each connected socket
				//new RFBSocket( serverSocket.accept(), (RFBServer) constructor.newInstance( new Object[] { new Integer( display ), displayName } ) );
				new RFBSocket( serverSocket.accept(), constructor, new Object[] { new Integer( display ), displayName, new Integer( width ), new Integer( height ) }, this, authenticator );
			}
		}
		catch( IOException x )
		{
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private int display;
	private String displayName;
	private int width;
	private int height;
	private RFBAuthenticator authenticator;
	private Constructor constructor;
	private RFBServer sharedServer = null;
}
