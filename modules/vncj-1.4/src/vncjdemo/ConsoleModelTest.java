package vncjdemo;

import gnu.vnc.console.*;

import java.io.*;

public class ConsoleModelTest extends VNCConsole
{
	//
	// Construction
	//
	
	public ConsoleModelTest( int display, String displayName, int width, int height )
	{
		super( displayName, 60, 30, 8, 12 );
	}
	
	//
	// VNCConsole
	//
	
	public void main()
	{
		//out = System.out;
		//in = System.in;
		help();
				
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String command = "";
		while( true )
		{
			out.print( "> " );
			try
			{
				command = reader.readLine();
			}
			catch( IOException x )
			{
				x.printStackTrace();
			}
			
			if( command.equals( "quit" ) )
			{
				return;
			}
			else if( command.equals( "help" ) )
			{
				help();
			}
			else if( command.equals( "jump" ) )
			{
				jump();
			}
			else if( command.equals( "dance" ) )
			{
				dance();
			}
			else
			{
				out.println( "Unknown command '" + command + "'" );
			}
		}
	}
	

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private void help()
	{
		out.println();
		out.println( "VNCj - Console Model Test" );
		out.println();
		out.println( "Commands:" );
		out.println( "  help" );
		out.println( "  jump" );
		out.println( "  dance" );
		out.println( "  quit" );
		out.println();
	}
	
	private void jump()
	{
		out.println( "I don't feel like it. Maybe later." );
	}

	private void dance()
	{
		out.println( "I need inspiration. I can't just dance." );
	}
}

