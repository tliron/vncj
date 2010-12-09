package gnu.awt.virtual.swing;

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

import gnu.awt.virtual.VirtualToolkit;

import java.awt.*;

import javax.swing.*;

public class ToolkitJFrame extends JFrame
{
  public static VirtualToolkit defaultToolkit; 
 /**
  *  for debug-loging (using Log4J or System.err depending on avliability)
  */ 
  static Object log;
 /**
  *  for debug-loging (using Log4J or System.err depending on avliability)
  */ 
  static java.lang.reflect.Method logmethod;
  
 /**
  *  for debug-loging (using Log4J or System.err depending on avliability)
  */   
  private void logDebug(String msg)
  {
   try
     {
      if(log==null)
      try
        {
         Class c = Class.forName("org.apache.log4j.Category");
         if(c==null)
           throw new ClassNotFoundException();
         log = (c.getMethod("getInstance", new Class[]{java.lang.String.class})).invoke(null, new Object[]{ToolkitJFrame.class.getName()});  
         logmethod = c.getMethod("debug", new Class[]{Object.class});
        }
      catch(ClassNotFoundException x)
        {
         log = System.err;
         logmethod = java.io.PrintStream.class.getMethod("println", new Class[]{String.class});
        }  

      logmethod.invoke(log, new Object[]{msg});
     } 
   catch(Exception x)
     {
      x.printStackTrace();
      System.err.println(msg);
     }
  }
        
        //
        // Construction
        //
        
        public ToolkitJFrame(String name )
  		{
            super( name );
            if( defaultToolkit!=null ){
                this.toolkit = defaultToolkit;
                defaultToolkit = null;
            }else{
            	this.toolkit = new VirtualDesktop(name,10000,10000);
            }
  		}
        public ToolkitJFrame( )
  		{
            this(null);       	
  		}
        
        //
        // Window
        //
        
        public Toolkit getToolkit()
        {
//         logDebug("getToolkit()="+toolkit); 
        	if( toolkit==null){
        		toolkit = defaultToolkit;
        	}
        	if( toolkit==null){
        		toolkit = new VirtualToolkit();
        	}
        	//System.out.println("Toolkit:"+toolkit+":"+this);
            return toolkit;
        }
        
        /*
        public ComponentPeer getPeer()
        {
         ComponentPeer peer = super.getPeer();
         if(peer instanceof VirtualComponentPeer)
           {
            VirtualComponentPeer peer2 = (VirtualComponentPeer)peer;
            Component c2 = peer2.getComponent();
            gnu.awt.virtual.swing.Virtu
            return(new gnu.vnc.awt.swing.ToolkitJFrame);
           }
        }*/
        
        

        ///////////////////////////////////////////////////////////////////////////////////////
        // Private
        
        private Toolkit toolkit;
}

