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
import gnu.logging.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class RFBSocket implements RFBClient, Runnable {
    //
    // Construction
    //

    ///////////////////////////////////////////////////////////////////////////////////////
    // Private

    private Socket socket;
    private RFBHost host;
    private RFBAuthenticator authenticator;
    private RFBServer server = null;
    private DataInputStream input;
    private DataOutputStream output;

    private PixelFormat pixelFormat = null;
    private String protocolVersionMsg = "";
    private boolean shared = true;
    private int[] encodings = new int[0];
    private int preferredEncoding = rfb.EncodingHextile;
    private boolean isRunning = false;
    private boolean threadFinished = false;
    private Vector updateQueue=new Vector();
    private boolean updateAvailable = true;
    

    /**
     * new constructor by Marcus Wolschon
     */
    public RFBSocket( Socket socket, RFBServer server, RFBHost host, RFBAuthenticator authenticator ) throws IOException {
        this.socket = socket;
        this.server = server;
        this.host = host;
        this.authenticator = authenticator;
        // Streams
        input = new DataInputStream( new BufferedInputStream( socket.getInputStream() ) );
        output = new DataOutputStream( new BufferedOutputStream( socket.getOutputStream(), 16384 ) );

        // Start socket listener thread
        new Thread( this ).start();
    }

    /**
     * new constructor by Marcus Wolschon
     */
    public RFBSocket( Socket socket, RFBServer server, RFBHost host, RFBAuthenticator authenticator, boolean syncronous ) throws IOException {
        this.socket = socket;
        this.server = server;
        this.host = host;
        this.authenticator = authenticator;

        // Streams
        input = new DataInputStream( new BufferedInputStream( socket.getInputStream() ) );
        output = new DataOutputStream( new BufferedOutputStream( socket.getOutputStream(), 16384 ) );

        // Start socket listener thread
        if(syncronous) {
            run();
        }
        else {
            new Thread( this ).start();
        }
    }



    //
    // RFBClient
    //

    // Attributes

    public synchronized PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    public synchronized String getProtocolVersionMsg() {
        return protocolVersionMsg;
    }

    public synchronized boolean getShared() {
        return shared;
    }

    public synchronized int getPreferredEncoding() {
        return preferredEncoding;
    }

    public synchronized void setPreferredEncoding( int encoding ) {
        if( encodings.length > 0 ) {
            for( int i = 0; i < encodings.length; i++ ) {
                if( encoding == encodings[i] ) {
                    // Encoding is supported
                    preferredEncoding = encoding;
                    return;
                }
            }
        }
        else {
            // No list
            preferredEncoding = encoding;
        }
    }

    public synchronized int[] getEncodings() {
        return encodings;
    }

    // Messages from server to client

    public synchronized void writeFrameBufferUpdate( Rect rects[] ) throws IOException {
        writeServerMessageType( rfb.FrameBufferUpdate );
        output.writeByte( 0 ); // padding

        // Count rects
        int count = 0;
        int i;
        for( i = 0; i < rects.length; i++ )
            count += rects[i].count;
        output.writeShort( count );

        for( i = 0; i < rects.length; i++ )
            rects[i].writeData( output );

        output.flush();
    }

    public synchronized void writeSetColourMapEntries( int firstColour, Colour colours[] ) throws IOException {
        writeServerMessageType( rfb.SetColourMapEntries );
        output.writeByte( 0 ); // padding
        output.writeShort( firstColour );
        output.writeShort( colours.length );
        for( int i = 0; i < colours.length; i++ ) {
            output.writeShort( colours[i].r );
            output.writeShort( colours[i].g );
            output.writeShort( colours[i].b );
        }
        output.flush();
    }

    public synchronized void writeBell() throws IOException {
        writeServerMessageType( rfb.Bell );
    }

    public synchronized void writeServerCutText( String text ) throws IOException {
        writeServerMessageType( rfb.ServerCutText );
        output.writeByte( 0 );  // padding
        output.writeShort( 0 ); // padding
        output.writeInt( text.length() );
        output.writeBytes( text );
        output.writeByte( 0 );
        output.flush();
    }

    // Operations

    public synchronized void close(){
        isRunning = false;
        // Block until the thread has exited gracefully
        while(threadFinished == false){
            try{
                Thread.currentThread().sleep(20);
            }
            catch(InterruptedException x){
            }
        }
        try{
        	output.close();
        	input.close();
            socket.close();
        }
        catch(IOException e){
            VLogger.getLogger().log("Got and exception shutting down RFBSocket ",e);
        }
        finally{
        	output=null;
        	input=null;
            socket=null;
        }
    }

    //
    // Runnable
    //

    public void run() {
        isRunning =true;
        try {
            //                 System.err.println("DEBUG[RFBSocket] run() calling writeProtocolVersionMsg()");
            // Handshaking
            writeProtocolVersionMsg();
            //                 System.err.println("DEBUG[RFBSocket] run() calling readProtocolVersionMsg()");
            readProtocolVersionMsg();
            //                 System.err.println("DEBUG[RFBSocket] run() calling writeAuthScheme()");
            //if(((DefaultRFBAuthenticator)authenticator).authenticate(input,output)==false){
            if(authenticator.authenticate(input,output, this)==false){
                System.out.println("Authentiation failed");
                return;
            }
            //                 System.err.println("DEBUG[RFBSocket] run() calling readClientInit()");
            readClientInit();
            //                 System.err.println("DEBUG[RFBSocket] run() calling initServer()");
            initServer();
            //                 System.err.println("DEBUG[RFBSocket] run() calling writeServerInit()");
            writeServerInit();
            //                 System.err.println("DEBUG[RFBSocket] run() message loop");

            // RFBClient message loop
            while( isRunning ) {
                if(getUpdateIsAvailable()){
                    // go ahead and send the updates
                    doFrameBufferUpdate();
                }                    
                if(input.available() == 0){
                    try{
                        Thread.currentThread().sleep(10);
                    }
                    catch(InterruptedException x){
                    }
                }
                else{
                    switch( input.readUnsignedByte() ) {
                        case rfb.SetPixelFormat:
                            readSetPixelFormat();
                            break;
                        case rfb.FixColourMapEntries:
                            readFixColourMapEntries();
                            break;
                        case rfb.SetEncodings:
                            readSetEncodings();
                            break;
                        case rfb.FrameBufferUpdateRequest:
                            readFrameBufferUpdateRequest();
                            break;
                        case rfb.KeyEvent:
                            readKeyEvent();
                            break;
                        case rfb.PointerEvent:
                            readPointerEvent();
                            break;
                        case rfb.ClientCutText:
                            readClientCutText();
                            break;
                    }
                }
            }
        }
        catch( IOException x ) {
            System.out.println("Got an IOException, drop the client");
        }
        catch(Throwable t){
            t.printStackTrace();
        }

        if( server != null ){
            server.removeClient( this );
        }

        threadFinished = true;
        close();
    }


    private void initServer() throws IOException {
        // We may already have a shared server
        if( shared ){
            server = host.getSharedServer();
        }
        server.addClient( this );
        server.setClientProtocolVersionMsg( this, protocolVersionMsg );
        server.setShared( this, shared );
    }

    // Handshaking

    private synchronized void writeProtocolVersionMsg() throws IOException {
        output.writeBytes( rfb.ProtocolVersionMsg );
        output.flush();
    }

    private synchronized void readProtocolVersionMsg() throws IOException {
        byte[] b = new byte[12];
        input.readFully( b );
        protocolVersionMsg = new String( b );
    }

    private synchronized void readClientInit() throws IOException {
        shared = input.readUnsignedByte() == 1;
    }

    private synchronized void writeServerInit() throws IOException {
        //         System.err.println("DEBUG[RFBSocket] writeServerInit() writing FB-dimension");
        output.writeShort( server.getFrameBufferWidth( this ) );
        output.writeShort( server.getFrameBufferHeight( this ) );
        //         System.err.println("DEBUG[RFBSocket] writeServerInit() writing pixel-format");
        server.getPreferredPixelFormat( this ).writeData( output );
        //         System.err.println("DEBUG[RFBSocket] writeServerInit() writing padding");
        output.writeByte( 0 ); // padding
        output.writeByte( 0 ); // padding
        output.writeByte( 0 ); // padding
        //         System.err.println("DEBUG[RFBSocket] writeServerInit() writing desktopname");
        String desktopName = server.getDesktopName( this );
        output.writeInt( desktopName.length() );
        output.writeBytes( desktopName );
        output.flush();
    }

    // Authentication
    
    private synchronized void writeAuthScheme() throws IOException {
        output.writeInt( authenticator.getAuthScheme( this ) );
        output.flush();
    }
    
    // Messages from server to client

    private synchronized void writeServerMessageType( int type ) throws IOException {
        output.writeByte( type );
    }

    // Messages from client to server

    private synchronized void readSetPixelFormat() throws IOException {
        input.readUnsignedByte();  // padding
        input.readUnsignedShort(); // padding
        pixelFormat = new PixelFormat( input );
        input.readUnsignedByte();  // padding
        input.readUnsignedShort(); // padding

        // Delegate to server
        server.setPixelFormat( this, pixelFormat );
    }

    private synchronized void readFixColourMapEntries() throws IOException {
        input.readUnsignedByte(); // padding
        int firstColour = input.readUnsignedShort();
        int nColours = input.readUnsignedShort();
        Colour colourMap[] = new Colour[ nColours ];
        for( int i = 0; i < nColours; i++ )
            colourMap[i].readData( input );

        // Delegate to server
        server.fixColourMapEntries( this, firstColour, colourMap );
    }

    private synchronized void readSetEncodings() throws IOException {
        input.readUnsignedByte(); // padding
        int nEncodings = input.readUnsignedShort();
        encodings = new int[ nEncodings ];
        for( int i = 0; i < nEncodings; i++ )
            encodings[i] = input.readInt();

        preferredEncoding = Rect.bestEncoding( encodings );

        // Delegate to server
        server.setEncodings( this, encodings );
    }

    private synchronized void readFrameBufferUpdateRequest() throws IOException {
        boolean incremental = ( input.readUnsignedByte() == 1 );
        int x = input.readUnsignedShort();
        int y = input.readUnsignedShort();
        int w = input.readUnsignedShort();
        int h = input.readUnsignedShort();
        UpdateRequest r = new UpdateRequest(incremental,x,y,w,h);
        synchronized(updateQueue){
            int index = updateQueue.indexOf(r);
            if(index >= 0){
                // replace only if update is non incremental
                if(r.incremental ==false){
                    updateQueue.setElementAt(r,index);
                }
            }
            else{
                updateQueue.add(r);
            }
        }
    }
    private synchronized void doFrameBufferUpdate() throws IOException{
        Iterator iter = updateQueue.iterator();
        while(iter.hasNext()){
            UpdateRequest ur = (UpdateRequest)iter.next();
            iter.remove();
            // Delegate to server
            try {
                System.out.println("RFBSocket is doing an update");
                server.frameBufferUpdateRequest( this, ur.incremental, ur.x, ur.y, ur.w, ur.h );
                System.out.println("RFBSocket is done");
            }
            catch(IOException e)  // some times we have w==h==0 and it would result in a blue screen on the official VNC client.
            {
                // if there is nothing to encode, encode the top left pixel instead
                if(e.getMessage().startsWith("rects.length == 0")){
                    server.frameBufferUpdateRequest( this, false, 0, 0, 1, 1 );
                }
                else{
                    // rethrow it
                    throw e;
                }
            }
            finally{
                setUpdateIsAvailable(false);
            }
        }
    }

    private synchronized void readKeyEvent() throws IOException {
        boolean down = ( input.readUnsignedByte() == 1 );
        input.readUnsignedShort(); // padding
        int key = input.readInt();

        // Delegate to server
        server.keyEvent( this, down, key );
    }

    private synchronized void readPointerEvent() throws IOException {
        int buttonMask = input.readUnsignedByte();
        int x = input.readUnsignedShort();
        int y = input.readUnsignedShort();

        // Delegate to server
        server.pointerEvent( this, buttonMask, x, y );
    }

    private synchronized void readClientCutText() throws IOException {
        input.readUnsignedByte();  // padding
        input.readUnsignedShort(); // padding
        int length = input.readInt();
        byte[] bytes = new byte[ length ];
        input.readFully( bytes );
        String text = new String( bytes );

        // Delegate to server
        server.clientCutText( this, text );
    }
    public InetAddress getInetAddress(){
        return(socket.getInetAddress());
    }

    public String getName(){
        return(host.getDisplayName());
    }

    public void setUpdateIsAvailable(boolean value) {
        updateAvailable = value;
    }

    public boolean getUpdateIsAvailable() {
        return(updateAvailable);
    }

    private class UpdateRequest{
        boolean incremental;
        int x;
        int y;
        int w;
        int h;
        public UpdateRequest(boolean incremental, int x, int y, int w, int h){
            this.incremental=incremental;
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
        }

        public boolean equals(Object obj){
            UpdateRequest u2 = (UpdateRequest)obj;
            return(x==u2.x && y==u2.y && w==u2.w && h==u2.h);
        }

    }

}
