package gnu.awt.virtual;

import java.awt.*;
import java.awt.peer.*;

/**
* AWT lightweight component peers that does nothing. Used for {@link gnu.awt.virtual.VirtualToolkit}.
**/

class VirtualLightweightPeer extends VirtualComponentPeer implements LightweightPeer
{
	//
	// Construction
	//
	
	public VirtualLightweightPeer( Component component )
	{
		super( null, component );
	}
	
	//
	// LightweightPeer
	//
}
