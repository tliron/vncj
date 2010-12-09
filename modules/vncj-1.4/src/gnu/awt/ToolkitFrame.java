package gnu.awt;

import java.awt.*;
//import java.awt.peer.*;

/**
* Standard AWT frame that can use a toolkit other than the default one.
**/

public class ToolkitFrame extends Frame
{
	//
	// Construction
	//
	
	public ToolkitFrame( Toolkit toolkit, String name )
	{
		super( name );
		this.toolkit = toolkit;
	}
	
	public ToolkitFrame( Toolkit toolkit )
	{
		super();
		this.toolkit = toolkit;
	}
	
	//
	// Window
	//
	
	public Toolkit getToolkit()
	{
		if( toolkit == null )
		{
			// The JFrame constructor will want to access the toolkit
			// (since JDK 1.4), and this happens before we get to set
			// the toolkit field, so let's supply it something to work with
			return super.getToolkit();
		}
		else
		{
			return toolkit;
		}
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

