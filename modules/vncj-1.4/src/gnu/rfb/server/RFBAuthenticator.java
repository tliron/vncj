package gnu.rfb.server;

import gnu.rfb.*;

import java.io.*;

/**
* To be implemented by RFB authentication models.
**/

public interface RFBAuthenticator
{
	// Operations
	
	public boolean authenticate( RFBClient client ) throws IOException;
}
