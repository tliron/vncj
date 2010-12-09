package gnu.rfb.server;

import gnu.rfb.*;

import java.io.*;
import java.util.*;
import java.net.*;

/**
* Free-access RFB authentication models.
**/

public class DefaultRFBAuthenticator implements RFBAuthenticator
{
	//
	// Construction
	//
	
	public DefaultRFBAuthenticator()
	{
		this( null, null, null );
	}
	
	public DefaultRFBAuthenticator( String password, String restrictedTo, String noPasswordFor )
	{
		restrict = ( restrictedTo != null && restrictedTo.length() > 0 );
		addInetAddresses( this.restrictedTo, restrictedTo );
		addInetAddresses( this.noPasswordFor, noPasswordFor );
		this.password = password;
	}
	
	//
	// RFBAuthenticator
	//
	
	public boolean authenticate( RFBClient client ) throws IOException
	{
		if( isRestricted( client ) )
		{
			client.writeConnectionFailed( "Your address is blocked" );
			return false;
		}
		if( password != null && password.length() > 0 && isChallengeRequired( client ) )
		{
			return challenge( client );
		}
		else
		{
			noChallenge( client );
			return true;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private int auth;
	private String password;
	private boolean restrict;
	private Set restrictedTo = new HashSet();
	private Set noPasswordFor = new HashSet();
	
	private boolean isRestricted( RFBClient client )
	{
		if( restrict )
			return !restrictedTo.contains( client.getInetAddress() );
		else
			return false;
	}
	
	private boolean isChallengeRequired( RFBClient client )
	{
		return !noPasswordFor.contains( client.getInetAddress() );
	}
	
	private boolean challenge( RFBClient client ) throws IOException
	{
		client.write( rfb.VncAuth );
		
		// Write 16 byte challenge
		byte[] challenge = new byte[16];
		client.write( challenge );
		client.flush();
		
		// Read 16 byte response
		byte[] response = new byte[16];
		client.read( response );
		
		// Create key (password padded with zeros)
		byte[] key = new byte[8];
		int i;
		for( i = 0; i < password.length(); i++ )
		{
			key[i] = (byte) password.charAt( i );
		}
		for( ; i < 8; i++ )
		{
			key[i] = 0;
		}
		DesCipher des = new DesCipher( key );
		
		// Cipher challange
		des.encrypt( challenge, 0, challenge, 0 );
		des.encrypt( challenge, 8, challenge, 8 );
		
		// Compare ciphers
		if( Arrays.equals( challenge, response ) )
		{
			client.write( rfb.VncAuthOK );
			client.flush();
			return true;
		}
		else
		{
			client.write( rfb.VncAuthFailed );
			client.flush();
			return false;
		}
	}
	
	private void noChallenge( RFBClient client ) throws IOException
	{
		client.write( rfb.NoAuth );
		client.flush();
	}
	
	private static void addInetAddresses( Set set, String string )
	{
		if( string == null )
			return;
		
		InetAddress[] addresses;
		for( StringTokenizer t = new StringTokenizer( string, "," ); t.hasMoreElements(); )
		{
			try
			{
				addresses = InetAddress.getAllByName( t.nextToken() );
				for( int i = 0; i < addresses.length; i++ )
				{
					set.add( addresses[i] );
				}
			}
			catch( UnknownHostException x )
			{
			}
		}
	}
}
