/*
 * Class.java
 *
 * Created on April 18, 2002, 1:59 PM
 */

package gnu.logging;

/**
 *
 * @author  rpatel
 */
public class VLogger {
    
    static private VLogger instance = null;
    /** Creates a new instance of Class */
    protected VLogger() {
    }
    public void log(String msg){
        System.out.println(msg);
    }
    public void log(String msg,Throwable t){
        System.out.println(msg);
        t.printStackTrace();
    }
    static public VLogger getLogger(){
        if(instance == null){
            instance = new VLogger();
        }
        return instance;
    }      
    static public void  setLogger(VLogger newInstance){
        instance = newInstance;
    }
}
