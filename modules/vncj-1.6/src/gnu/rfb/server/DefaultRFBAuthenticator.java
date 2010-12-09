package gnu.rfb.server;

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

import gnu.rfb.*;
import java.io.*;
import java.util.*;

public class DefaultRFBAuthenticator implements RFBAuthenticator
{
	//
	// Construction
	//
	///////////////////////////////////////////////////////////////////////////////////////
    // Private
    
    private int auth;
    String password = null;
    boolean authSuccessfull = false;
    String ip = null;
    
	public DefaultRFBAuthenticator(String password)
	{
            this.password = password;
		auth = rfb.VncAuth;
	}
	public int getAuthScheme( RFBClient client )
	{
		return auth;
	}
    
    public boolean authenticate(DataInputStream in, DataOutputStream out, RFBSocket clientSocket) throws IOException{
        System.out.println("Starting authenication for defaultRFBAuthenicator: " );
        out.writeInt( rfb.VncAuth);
        ip = clientSocket.getInetAddress().getHostAddress();
        System.out.println("Starting authenication for SecurityRFBAuthenicator: " + ip);
        authSuccessfull = DefaultRFBAuthenticator.enterPassword(in, out, password); 
        if(authSuccessfull == true){
            System.out.println("authentication successfull.  Asking user to enter password");
            //authSuccessfull = DefaultRFBAuthenticator.enterPassword(in, out, password); 
        }
        else{
            out.writeInt(rfb.ConnFailed);
            out.writeBytes("Security Authentication failed.  You must be logged on to ESPM in order to use this function");
        }
        
        out.flush();
        return(authSuccessfull);
        
    }
    public static boolean enterPassword(DataInputStream in, DataOutputStream out, String password) throws IOException{
        Random rand = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[16];
        rand.nextBytes(bytes);
        
        // send the bytes to the client and wait for the response
        out.write(bytes);
        out.flush();
        
        byte[] encryptedBytes = new byte[16];
        byte[] decryptedBytes = new byte[16];
        in.read(encryptedBytes);
        
        // Initialize the cipher
        DesCipher cipher = new DesCipher(password.getBytes());
        System.out.println("Password is:" + new String(password));
        cipher.decrypt(encryptedBytes,0,decryptedBytes,0);
        cipher.decrypt(encryptedBytes,8,decryptedBytes,8);
        System.out.println("Client sent us:" + new String(decryptedBytes));
        
        boolean authSuccessful = true;
        for(int i = 0;i<16;i++){
            if(bytes[i] != decryptedBytes[i]){
                authSuccessful = false;
                break;
            }
        }
        
        if(authSuccessful == true){
            out.writeInt(rfb.VncAuthOK);
        }
        else{
            out.writeInt(rfb.VncAuthFailed);
        }
        
        out.flush();
        return(authSuccessful);
        
    }
}
	
