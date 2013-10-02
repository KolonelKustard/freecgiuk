/*
 * IDMPhone.java
 *
 * Created on 20 December 2000, 19:15
 */

import java.awt.*;
import java.io.*;
import java.net.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
class ManagePhoneLine extends Object {
    private boolean stillRunning;
    private String phoneNum;
    private IDMPhone parent;
    private PhoneConn phoneConn;
    
    private NetParser netParser;
    
    private int currentState;
    private String stringState;
    
    public ManagePhoneLine(IDMPhone parent) {
        stillRunning = true;
        this.parent = parent;
        
        phoneConn = null;
        
        netParser = new NetParser();
    }
    
    public int getStatus() {
        int currState;
        
        if (phoneConn != null) {
            return phoneConn.getStatus();
        }
        else {
            return -1;
        }
    }
    
    public int connect(String phoneNum) {
        if (phoneConn != null) {
            phoneConn.disconnect();
            phoneConn = null;
        }
        
        if (phoneConn == null) {
            phoneConn = new PhoneConn(phoneNum);
        }
        
        return getStatus();
    }
    
    public int disconnect() {
        if (phoneConn != null) {
            phoneConn.disconnect();
            phoneConn = null;
        }
        
        return getStatus();
    }
    
    public int setDirectionOutgoing() {
        phoneConn.setDirectionOutgoing();
        
        return getStatus();
    }
    
    public int setDirectionIncoming() {
        phoneConn.setDirectionIncoming();
        
        return getStatus();
    }
}

public class IDMPhone extends Thread {
    public static final int PORT = 20020;
    
    private Frame window;
    private TextArea output;
    
    private ServerSocket sock;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    private String dataIn;
    private boolean appStillRunning;
    
    private NetParser netParser;
    
    private ManagePhoneLine managePhoneLine;

    /** Creates new IDMPhone */
    public IDMPhone() {
        appStillRunning = true;
        managePhoneLine = new ManagePhoneLine(this);
        netParser = new NetParser();
        
        window = new Frame("IDM Phoner");
        output = new TextArea();
        window.add(output);
        
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(1);
            }
        });
        
        window.setLocation(100, 50);
        window.setSize(400, 600);
        window.show();
        
        outputPrimary("Welcome to the IDM Phoner.");
        outputPrimary("Listening for Speech thingy on port: " + PORT + "...");
        
        try {
            sock = new ServerSocket(PORT);
            socket = sock.accept();
            outputPrimary("Ace!  Got a connection!");
            outputPrimary("");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            start();
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    public void outputPrimary(String s) {
        output.append(s + "\n");
    }
    
    public void outputSecondary(String s) {
        System.out.println(s);
    }
    
    public void run() {
        int status;
        
        while (appStillRunning) {
            try {
                dataIn = in.readLine();
            }
            catch (Exception e) {
                System.out.println(e);
                System.out.println("Seem to have lost connection...  Quitting app.");
                System.exit(1);
            }
            
            netParser.newInput(dataIn);
            
            if (netParser.getInputCommand().equals(NetParser.PHONE_NUM)) {
                outputPrimary("Connecting to phone number: " + netParser.getInputParamString(0));
                
                status = managePhoneLine.connect(netParser.getInputParamString(0));
                
                
                // Need to wait a few seconds if this says it's connected...  Has a tendency to lie!
                if (status == PhoneConn.CONNECTED) {
                    try {
                        this.sleep(5000);
                    }
                    catch (Exception e) {
                        System.out.println(e);
                        System.exit(1);
                    }
                }
                
                status = managePhoneLine.getStatus();
                
                switch (status) {
                    case PhoneConn.CONNECTED:
                        out.println("&STATUS~" + NetParser.CONNECTED + "|");
                        break;
                    case PhoneConn.DISCONNECTED:
                        out.println("&STATUS~" + NetParser.DISCONNECTED + "|");
                        break;
                    default:
                        out.println("&STATUS~" + NetParser.OTHER + "|");
                        break;
                }
            }
            
            if (netParser.getInputCommand().equals(NetParser.DISCONNECT)) {
                outputPrimary("Disconnecting current call");
                
                status = managePhoneLine.disconnect();
                
                switch (status) {
                    case -1:
                        out.println("&STATUS~" + NetParser.DISCONNECTED + "|");
                        break;
                    default:
                        out.println("&STATUS~" + NetParser.OTHER + "|");
                        break;
                }
            }
            
            if (netParser.getInputCommand().equals(NetParser.SET_OUTGOING)) {
                outputPrimary("Setting audio stream outgoing...");
                
                status = managePhoneLine.setDirectionOutgoing();
                
                out.println("&STATUS~" + NetParser.AUDIO_OUTGOING + "|");
                outputPrimary("Audio stream set");
            }
            
            if (netParser.getInputCommand().equals(NetParser.SET_INCOMING)) {
                outputPrimary("Setting audio stream incoming...");
                
                status = managePhoneLine.setDirectionIncoming();
                
                out.println("&STATUS~" + NetParser.AUDIO_INCOMING + "|");
                outputPrimary("Audio stream set");
            }
            
            try {
                this.sleep(500);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        IDMPhone phoner = new IDMPhone();
    }

}
