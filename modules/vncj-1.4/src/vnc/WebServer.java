package gnu.vnc;

import java.io.*;
import java.net.*;
import java.util.*;

/**
* Extremely simple HTTP server for serving the VNC viewer applet.
**/

public class WebServer implements Runnable
{
	//
	// Construction
	//
	
	public WebServer( int display, String title, int width, int height )
	{
		this.display = display;
		this.title = title;
		this.width = width;
		this.height = height + 21;
		
		try
		{
			url = new URL( "http", InetAddress.getLocalHost().getHostAddress(), 5800 + display, "/" );
		}
		catch( IOException x )
		{
		}
		
		new Thread( this, "VNCWebServer-" + display ).start();
	}

	//
	// Runnable
	//
	
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket( url.getPort() );
			while( true )
			{
				// Create client for each connected socket
				new WebServerSocket( serverSocket.accept() );
			}
		}
		catch( IOException x )
		{
			x.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Private

	private int display;
	private String title;
	private int height;
	private int width;
	private URL url;
	private static String serverName = "VNCj";
	private static String[] allowed =
	{
		"/vncviewer.jar",
		"/animatedMemoryImageSource.class",
		"/authenticationPanel.class",
		"/clipboardFrame.class",
		"/DesCipher.class",
		"/optionsFrame.class",
		"/rfbProto.class",
		"/vncCanvas.class",
		"/vncviewer.class",
		// MF viewer:
		"/ButtonPanel.class",
		"/optionsFrame$1.class",
		"/optionsFrame$2.class",
		"/optionsFrame$3.class",
		"/optionsFrame.class",
		"/TimeEvent.class",
		"/TimeOut.class",
		"/vncviewer$1.class"
	};

	private class WebServerSocket implements Runnable
	{
		//
		// Construction
		//
		
		public WebServerSocket( Socket socket ) throws IOException
		{
			this.socket = socket;
			
			// Streams
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			print = new PrintStream( socket.getOutputStream() );
			
			// Start socket listener thread
			new Thread( this, "VNCWebServerSocket-" + socket.getInetAddress().getHostAddress() ).start();
		}

		//
		// Runnable
		//
		
		public void run()
		{
			try
			{
				// Read request
				Vector request = new Vector();
				String line, lineU, content = null;
				boolean post = false;
				int contentLength = -1;
				while( true )
				{
					line = reader.readLine();
					
					if( line == null )
						break;
						
					if( line.length() == 0 )
					{
						if( post && ( contentLength > -1 ) )
						{
							// For HTTP POST
							char[] array = new char[ contentLength ];
							reader.read( array );
							content = new String( array );
							request.addElement( content );
						}
						break;
					}
					else
					{
						lineU = line.toUpperCase();	
						StringTokenizer tokenizer = new StringTokenizer( line );
						tokenizer.nextToken();
						if( lineU.startsWith( "GET" ) )
						{
							content = tokenizer.nextToken();
						}
						else if( lineU.startsWith( "POST" ) )
						{
							post = true;
						}
						else if( lineU.startsWith( "CONTENT-LENGTH" ) )
						{
							contentLength = Integer.parseInt( tokenizer.nextToken() );
						}
					}
					
					//System.err.println( line );
					request.addElement( line );
				}
				
				processRequest( post, content );
				print.close();
				socket.close();
			}
			catch( IOException x )
			{
				x.printStackTrace();
			}
		}

		///////////////////////////////////////////////////////////////////////////////////////
		// Private

		private Socket socket;
		private BufferedReader reader;
		private PrintStream print;
		
		private void processRequest( boolean post, String content ) throws IOException
		{
			if( content.equals( "/" ) )
			{
				printApplet();
				return;
			}
			
			for( int i = 0; i < allowed.length; i++ )
			{
				if ( allowed[i].equals( content ) )
				{
					printResource( content );
					return;
				}
			}

			printNotFound();
		}

		private void printApplet()
		{
			print.print
			(
				"HTTP/1.0 200 Document follows\r\n" +
				"Server: " + serverName + "\r\n" +
				"Content-Type: text/html\r\n" +
				"\r\n" +
				"<!DOCTYPE HTML PUBLIC " +
				"\"-//W3C//DTD HTML 3.2//EN\">\n" +
				"<HTML>\n" +
				"<HEAD>\n" +
				"<TITLE>" + serverName + " [" + title + "]</TITLE>\n" +
				"</HEAD>\n" +
				"\n" +
				"<BODY>\n" +
				"<APPLET CODE=\"vncviewer.class\" HEIGHT=\"" + height + "\" WIDTH=\"" + width + "\" ARCHIVE=\"vncviewer.jar\">" +
				"<PARAM NAME=\"PORT\" VALUE=\"" + ( 5900 + display ) + "\">\n" +
				"</APPLET>\n" +
				"</BODY>\n" +
				"</HTML>\n"
			);
			
			/*"<PARAM NAME=\"PORT\" VALUE=\"" + ( 5900 + display ) + "\">" +
			"<PARAM NAME=\"CODE\" VALUE=\"vncviewer.class\">" +
			"<PARAM NAME=\"HEIGHT\" VALUE=\"" + height + "\">" +
			"<PARAM NAME=\"WIDTH\" VALUE=\"" + width + "\">" +
			"<PARAM NAME=\"ARCHIVE\" VALUE=\"vncviewer.jar\">" +
			"<PARAM NAME=\"CODEBASE\" VALUE=\"" + url + "\">" +*/
		}

		private void printNotFound()
		{
			print.print
			(
				"HTTP/1.0 404 Not found\r\n" +
				"Server: " + serverName + "\r\n" +
				"Content-Type: text/html\r\n" +
				"\r\n" +
				"<HEAD><TITLE>File Not Found</TITLE></HEAD>\n" +
				"<BODY><H1>The requested file could not be found</H1></BODY>\n"
			);
		}
		
		private void printResource( String name ) throws IOException
		{
			if( name.startsWith( "/" ) )
				name = name.substring( 1 );
			
			name = "resource/" + name;	
			
			// Change ".class" suffix to ".data"
			if( name.substring( name.length() - 6 ).equals( ".class" ) )
				name = name.substring( 0, name.length() - 6 ) + ".data";
			
			InputStream stream = ClassLoader.getSystemResourceAsStream( name );
			if( stream == null )
			{
				printNotFound();
				return;
			}
			
			print.print
			(
				"HTTP/1.0 200 OK\r\n" +
				"Server: " + serverName + "\r\n" +
				"\r\n"
			);

			print.write( toByteArray( stream ) );
		}
		
		private byte[] toByteArray( InputStream stream ) throws IOException
		{
			int blockSize = 4096;
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream( blockSize );
			byte[] block = new byte[ blockSize ];
			int bytes, totalBytes = 0;
			while( ( bytes = stream.read( block, 0, blockSize ) ) > -1 )
			{
				bytestream.write( block, 0, bytes );
			}
			
			return bytestream.toByteArray();
		}
	}
}
