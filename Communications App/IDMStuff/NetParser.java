/*
 * ParseOutput.java
 *
 * Created on 09 October 2000, 15:21
    *
    *This class encodes and decodes information sent to and from the server...
    *In theory there shouldn't need to be any error checking either.
    *However I may add some if I get time (to stop the server falling over).
    *
    *This is actually a modified version of a better class, made to be compatible
    *with JVM v1.1
 */

package IDMStuff;

import java.util.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class NetParser extends Object {
    
    private String out;
    private String in;
    
    private Vector params = new Vector();
    
    private String stringIn;
    
    // For phone server to receive...
    public static final String PHONE_NUM = "PhoneNum";
    public static final String DISCONNECT = "Disconnect";
    public static final String SEND_STATUS = "SendStatus";
    public static final String SET_OUTGOING = "SetOutgoing";
    public static final String SET_INCOMING = "SetIncoming";
    
    // Params
    public static final String IDLE = "Idle";
    public static final String INPROGRESS = "InProgress";
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";
    public static final String OFFERING = "Offering";
    public static final String HOLD = "Hold";
    public static final String QUEUED = "Queued";
    public static final String OTHER = "Other";
    
    public static final String AUDIO_OUTGOING = "AudioOutgoing";
    public static final String AUDIO_INCOMING = "AudioIncoming";
    
    // For speaking client to receive...
    public static final String STATUS = "Status";
    
    public void NetParser () {
        out = "";
        in = "";
    }
    
    // Methods follow that deal with output...
    
    public void newOutput (String s) {
        out = "&";
        out += s;
        out += "~";
    }
    
    public void newOutput (String s, String s2) {
        out = "&";
        out += s;
        out += "~";
        out += parseText (s2);
    }
    
    public void addOutputCommand (String s) {
        if (out.length() > 0) {
            out += "|";
        }
        out += "&";
        out += s;
        out += "~";
    }
    
    public void addOutputParam (String s) {
        if (out.charAt(out.length()-1) != '~') {
            out += ",";
        }
        out += parseText(s);
    }
    
    public String getOutput () {
        if (out.length() > 0) {
            if (out.charAt(out.length()-1) != '|') {
                out += "|";
            }
            System.out.println("OUT: " + out);
            return out;
        }
        else
            return null;
    }
    
    
    // Methods follow for dealing with input...
    
    public void newInput (String s) {
        System.out.println("In:  " + s);
        params = parseInput(s);
    }
    
    public String getInputCommand () {
        stringIn = (String)params.elementAt(0);
        return stringIn;
    }
    
    // Get the string from the parameter.
    public String getInputParamString (int paramNumber) {
        stringIn = (String)params.elementAt(paramNumber + 1);  // Have to add 1 to param number else you'd get the command instead.
        return stringIn;
    }
    
    public void reset () {
        in = "";
        out = "";
        params.removeAllElements();
    }
        
        
        
    //  Here follows the static methods for encryption and decryption...
    
    private static String parseText (String s) {
        String s2 = "";
        
        for (int num = 0; num < s.length(); num++) {
            switch (s.charAt(num)) {
                case '|' :
                    s2 = s2 + "*|*";
                    break;
                
                case '&' :
                    s2 = s2 + "*&*";
                    break;
                
                case '~' :
                    s2 = s2 + "*~*";
                    break;
                
                case ',' :
                    s2 = s2 + "*,*";
                    break;
                
                case '*' :
                    s2 = s2 + "***";
                    break;
                    
                default :
                    s2 = s2 + s.charAt(num);
                    break;
            }
        }
        
        return s2;
    }

    private static Vector parseInput (String s) {
        Vector v = new Vector();
        String currentString = "";
        
        for (int num = 0; num < s.length(); num++) {           
            switch (s.charAt(num)) {
                case '&' :
                    currentString = "";
                    break;
                
                case '~' :
                    v.addElement(currentString);
                    currentString = "";
                    break;
                
                case ',' :
                    v.addElement(currentString);
                    currentString = "";
                    break;
                    
                case '|' :
                    v.addElement(currentString);
                    currentString = "";
                    break;
                
                case '*' :
                    num++;
                    currentString = currentString + s.charAt(num);
                    num++;
                    break;
                    
                default :
                    currentString = currentString + s.charAt(num);
                    break;
            }
        }
        
        return v;
    }

}
