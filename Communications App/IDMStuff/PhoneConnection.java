/*
 * PhoneConnection.java
 *
 * Created on 22 December 2000, 13:29
 */

package IDMStuff;

import java.io.*;
import java.net.*;

//import IDMStuff.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class PhoneConnection extends Object {
    public static final int PORT = 20020;
    
    private IDMComm parent;
    private boolean connExist;
    
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    
    private NetParser netParser;

    /** Creates new PhoneConnection */
    public PhoneConnection(String ip, IDMComm parent, boolean connExist) {
        this.parent = parent;
        this.connExist = connExist;
        netParser = new NetParser();
        
        if (connExist) {
            try {
                InetAddress addr = InetAddress.getByName(ip);
                parent.outSecondary("(Conn) Connecting...");
                socket = new Socket(addr, PORT);
                parent.outSecondary("(Conn) Connected.");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }
    
    public boolean createCall(String phoneNum) {
        
        if (phoneNum!= null) {
            parent.outPrimary("");
            parent.outPrimary("Connecting to phone number: " + phoneNum);
            
            if (connExist) {
                // Sending phone number to IDM dialer.
                netParser.newOutput(NetParser.PHONE_NUM, phoneNum);
                out.println(netParser.getOutput());
                
                try {
                    // Wait for reply (confirmation of connection) from dialer.
                    parent.outSecondary("(Conn) Waiting for response from dialer.");
                    netParser.newInput(in.readLine());
                    
                    // If the parameter from the dialer is connected then woohoo!!!
                    if (netParser.getInputParamString(0).equals(NetParser.CONNECTED)) {
                        parent.outPrimary("Phone is connected.");
                        return true;
                    }
                    else if (netParser.getInputParamString(0).equals(NetParser.OTHER)) {
                        parent.outPrimary("There is a problem with the dialer.  Program terminating.");
                        parent.outSecondary("There is a problem with the dialer.  Program terminating.");
                        System.exit(1);
                        return false;
                    }
                    else {
                        parent.outPrimary("Could not connect.");
                        parent.outSecondary("(Conn) Could not connect to phone line.  May be that phone is engaged.");
                        return false;
                    }
                }
                catch (IOException e) {
                    System.out.println(e);
                    System.exit(1);
                    return false;
                }
            }
            else {
                parent.outPrimary("IDM Communicator is in offline mode");
                return true;
            }
        }
        else {
            parent.outPrimary("Phone number was invalid");
            return false;
        }
    }
    
    public boolean endCall() {
        parent.outPrimary("Disconnecting...");
        
        if (connExist) {
            // Sending request to disconnect
            netParser.newOutput(NetParser.DISCONNECT, "True");
            out.println(netParser.getOutput());
            
            try {
                // Wait for reply (confirmation of connection) from dialer.
                parent.outSecondary("(Conn) Waiting for response from dialer.");
                netParser.newInput(in.readLine());
                
                // If the parameter from the dialer is connected then woohoo!!!
                if (netParser.getInputParamString(0).equals(NetParser.DISCONNECTED)) {
                    parent.outPrimary("Disconnected.");
                    return true;
                }
                else if (netParser.getInputParamString(0).equals(NetParser.OTHER)) {
                    parent.outPrimary("There is a problem with the dialer.  Program terminating.");
                    parent.outSecondary("There is a problem with the dialer.  Program terminating.");
                    System.exit(1);
                    return false;
                }
                else {
                    parent.outPrimary("Could not disconnect.");
                    parent.outSecondary("(Conn) Could not disconnect phone line.");
                    return false;
                }
            }
            catch (IOException e) {
                System.out.println(e);
                System.exit(1);
                return false;
            }
        }
        else {
            parent.outPrimary("IDM Communicator is in offline mode");
            return true;
        }
    }
    
    public boolean setAudioOutgoing() {
        parent.outPrimary("Setting audio outgoing...");
        
        if (connExist) {
            // Sending request to disconnect
            netParser.newOutput(NetParser.SET_OUTGOING, "True");
            out.println(netParser.getOutput());
            
            try {
                // Wait for reply (confirmation of connection) from dialer.
                parent.outSecondary("(Conn) Waiting for response from dialer.");
                netParser.newInput(in.readLine());
                
                // If the parameter from the dialer is connected then woohoo!!!
                if (netParser.getInputParamString(0).equals(NetParser.AUDIO_OUTGOING)) {
                    parent.outPrimary("Audio set outgoing.");
                    return true;
                }
                else if (netParser.getInputParamString(0).equals(NetParser.OTHER)) {
                    parent.outPrimary("There is a problem with the dialer.  Program terminating.");
                    parent.outSecondary("There is a problem with the dialer.  Program terminating.");
                    System.exit(1);
                    return false;
                }
                else {
                    parent.outPrimary("Could not set outgoing.");
                    parent.outSecondary("(Conn) Could not set audio outgoing.");
                    return false;
                }
            }
            catch (IOException e) {
                System.out.println(e);
                System.exit(1);
                return false;
            }
        }
        else {
            parent.outPrimary("IDM Communicator is in offline mode");
            return true;
        }
    }

    public boolean setAudioIncoming() {
        parent.outPrimary("Setting audio incoming...");
        
        if (connExist) {
            // Sending request to disconnect
            netParser.newOutput(NetParser.SET_INCOMING, "True");
            out.println(netParser.getOutput());
            
            try {
                // Wait for reply (confirmation of connection) from dialer.
                parent.outSecondary("(Conn) Waiting for response from dialer.");
                netParser.newInput(in.readLine());
                
                // If the parameter from the dialer is connected then woohoo!!!
                if (netParser.getInputParamString(0).equals(NetParser.AUDIO_INCOMING)) {
                    parent.outPrimary("Audio set incoming.");
                    return true;
                }
                else if (netParser.getInputParamString(0).equals(NetParser.OTHER)) {
                    parent.outPrimary("There is a problem with the dialer.  Program terminating.");
                    parent.outSecondary("There is a problem with the dialer.  Program terminating.");
                    System.exit(1);
                    return false;
                }
                else {
                    parent.outPrimary("Could not set incoming.");
                    parent.outSecondary("(Conn) Could not set audio incoming.");
                    return false;
                }
            }
            catch (IOException e) {
                System.out.println(e);
                System.exit(1);
                return false;
            }
        }
        else {
            parent.outPrimary("IDM Communicator is in offline mode");
            return true;
        }
    }

}
