package gnu.awt.virtual;

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

import java.awt.*;
//import java.awt.peer.*;

public class ToolkitFrame extends Frame
{
	public static VirtualToolkit defaultToolkit;
	//
	// Construction
	//
	
	public ToolkitFrame( String name )
	{
		super( name );
		if( defaultToolkit!=null ){
	        this.toolkit = defaultToolkit;
	        defaultToolkit = null;
		}
	}
	
	public ToolkitFrame(  )
	{
		this(null);
	}
	
	//
	// Window
	//
	
	public Toolkit getToolkit()
	{
    	if( toolkit==null){
    		toolkit = defaultToolkit;
    	}
    	if( toolkit==null){
    		toolkit = new VirtualToolkit();
    	}
    	//System.out.println("Toolkit:"+toolkit+":"+this);
		return toolkit;
	}
	
	//
	// Container
	//
	
	/*protected void addImpl( Component comp, Object constraints, int index )
	{
		ComponentPeer peer = getPeer();
		if( peer instanceof 
	}*/ 

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private Toolkit toolkit;
}

