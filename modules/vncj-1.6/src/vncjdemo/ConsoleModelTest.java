package vncjdemo;

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

import gnu.vnc.console.*;

import java.io.*;

public class ConsoleModelTest extends VNCConsole
{
	//
	// Construction
	//
	
	public ConsoleModelTest( int display, String displayName )
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

