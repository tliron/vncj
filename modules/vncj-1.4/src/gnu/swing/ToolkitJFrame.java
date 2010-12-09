package gnu.swing;


import java.awt.*;

import javax.swing.*;

/**
* Standard JFC frame that can use a toolkit other than the default one.
**/

public class ToolkitJFrame extends JFrame
{
	//
	// Construction
	//
	
	public ToolkitJFrame( Toolkit toolkit, String name )
	{
		super( name );
		this.toolkit = toolkit;
	}
	
	public ToolkitJFrame( Toolkit toolkit )
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

	///////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private Toolkit toolkit;
}

