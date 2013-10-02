package b;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*; // for "beep"

// class WebServer implements a faux http server.  It contains
// 	
// method sendfile(s, fname) sends the contents of file fname through
// the connection on socket 

class WebServer extends Thread {
public
  ServerSocket ServerSock; // has accept() member
  public int ServerPort;   // integer (should be > 1024)
  boolean ready;           // web server start indicator
  Toolkit tk;              // has "Beep()" member

public WebServer(int s, Toolkit tk) {
  ServerPort = s; // default is 2001 
  this.tk = tk;  // the toolkit has "Beep"
  ready = false; // not ready to accept clients
} // constructor

// method sendfile(s, fname) sends the contents of file fname through
// the connection on socket s.
//
public void sendfile(Socket s, String fname) {
  byte buff[] = new byte[2048];
  OutputStream os;
  try {
    os = s.getOutputStream();
    int d;
    FileInputStream fis = 
      new FileInputStream(fname);
    while ((d = fis.read(buff)) > 0) {
      os.write(buff, 0, d);
    } // while
    fis.close();
    } catch (Exception e) {
    // System.out.println("Sendfile exception "+e);
  } // try-catch
} // end of method sendfile 


// method sendString(s, name) sends the contents of String name through
// the connection on socket 
//
public void sendString(Socket s, String name) {
  byte buff[] = new byte[name.length()];
  OutputStream os;
  buff = name.getBytes();
  try {
    os = s.getOutputStream();
    os.write(buff, 0, name.length());
  } catch (Exception e) { 
  }
} // end of method sendString

public void run () {       // run method for class webserver

//Again the constructor from Bawt.java

    Predicates Language = new Predicates();
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   

  ThreadGroup clerks = new ThreadGroup("Clerks");

//Added variable "ServerStartMsg"

  System.out.println(Language.getProperty("ServerStartMsg","Starting Web Server"));
  StringFile sf = new StringFile();
  try {	
    PrintStream wps = new PrintStream(new FileOutputStream("W.log"));
    ServerSock = new ServerSocket(ServerPort);
    ClerkManager CM = new ClerkManager(clerks, wps);
    CM.start();

//Added variable "ServerReadyMsg"

    System.out.println(Language.getProperty("ServerReadyMsg","Web Server started on port")+" "+String.valueOf(ServerPort));
    ready = true; // alerts others that web server is running
      for (;;) { // shutdown by GUI or program exit
        try { 
          Socket V = ServerSock.accept();  // wait for accept()
          Clerk tom = new Clerk(clerks, V, this, tk, wps);
          tom.setPriority(Thread.NORM_PRIORITY+2);
          tom.start();
	} // try
        catch (Exception e) {
      System.out.println("WebServer EXCEPTION: "+e);     
        } // catch
      }  // for (;;)
    } // try
    catch (Exception e) {
      System.out.println("WebServer EXCEPTION: "+e);     
    } // try-catch
  } // end of method run()
} // end of class WebServer

// ClerkManager creates a thread to
// notify any waiting clerks

class ClerkManager extends Thread {
ThreadGroup clerks;
PrintStream wps;

public ClerkManager(ThreadGroup clerks, PrintStream wps) {
  this.clerks = clerks;
  this.wps = wps;
}
 
public void run () {
  for (;;) {
    try {
      Thread.sleep(1000); 
      synchronized(this) {
        if (Clerk.waiting) {
          notifyAllClerks();
          Clerk.waiting = false;
        }
      }
    } catch (Exception e) {
      System.out.println("CLERKMANAGER "+e);		     
      wps.println("CLERKMANAGER "+e);		     
    }
  }  // for 
} // run


public synchronized void notifyAllClerks() {
  int w = clerks.activeCount();   
  if (w > 0) {
    //System.out.println(w+" active clerks");
    wps.println(w+" active clerks");
    Clerk worker[] = new Clerk[clerks.activeCount()];
    try {
      clerks.enumerate(worker);
    } catch (Exception e) {
      System.out.println("ENUMERATE: "+e);
      wps.println("ENUMERATE: "+e);
    } // catch
  for (int u = 0; u < clerks.activeCount(); u++) {
    try {
      synchronized (worker[u]) { // lock each clerk thread
        worker[u].notify();
     } // sync on worker
     } catch (Exception e) {
        System.out.println("NOTIFY "+u+" :"+e);		     
        wps.println("NOTIFY "+u+" :"+e);		     
     } // catch
   } // for each worker
  } // if
} // notifyAll()

} // ClerkManager

// the idea behind class Clerk is to put a "firewall"
// between the client and the server so that a 
// misbehaving client can't tie up the server forever.
// We made the Classifier method mutliline_response
// "synchronized" so that requests to the robot
// are atomic.  
// The "clerk" will wait until the client
// request is completed before activating
// the robot.
//
class Clerk extends Thread {

  public static boolean waiting; // is ANY clerk waiting?
  private Socket V;        // current open socket
  private WebServer WS;    // the primary server 
  private Toolkit tk;      // has "Beep()" member
  public long sleepytime;  // activation time
  public String hostname=new String("none"); // serving this client
  private PrintStream wps; // log file output stream
  private long t;          // time
  private static float tavg=0; // time average
  private static int tcnt=0; // time sample count
 
protected Clerk(ThreadGroup TG, Socket V, WebServer WS, Toolkit tk, PrintStream wps) {
  super(TG, "Clerk");
  Date D = new Date();
  t = D.getTime();
  InetAddress ia = V.getInetAddress();
  String hname = ia.getHostName();
  hostname = hname;
  // on a Linux system with IBM JDK118 we have seen the 
  // hostname 'malformed', a string with
  // unprintable characters:
  boolean malformed = false;
  for (int i = 0; i < hostname.length(); i++) {
     malformed = malformed || 
       (hostname.charAt(i)<' ') || (hostname.charAt(i)>'z');
  }
  //System.out.println("ia="+ia.toString()+" "+malformed);
  wps.println("ia="+ia.toString()+" "+malformed);
  // if the hostname is malformed, use the IP address:
  if (malformed) hostname = ia.getHostAddress();
  this.V = V; // socket
  this.WS= WS; // webserver
  this.tk= tk; // Toolkit
  this.wps= wps; // log file ps
  sleepytime = new Date().getTime();
} // constructor

public void process_client(Socket V, String hname, WebServer WS, Toolkit tk) {
  System.out.println("Clerk accepted "+hname);
  wps.println("Clerk accepted "+hname);
  StringFile sf = new StringFile();
  boolean burnout=false;
  String command="";
  try {
    BufferedReader tbr =
      new BufferedReader(new InputStreamReader(V.getInputStream()));
    PrintStream ps = new PrintStream(V.getOutputStream());
    String buf = "";
    synchronized (this) { // lock down the clerk
      while (!tbr.ready() && !burnout) {
        long dtime = new Date().getTime();
        dtime -= sleepytime; // duration since activation
	// System.out.println("Clerk "+hname+" waiting (1)"+dtime);
        wps.println("Clerk "+hname+" waiting (1)"+dtime);
        if (dtime > Globals.getClerkTimeout()) burnout = true; 
        waiting = true;
        wait();
	// System.out.println("Clerk "+hname+" awoke (1)");
	wps.println("Clerk "+hname+" awoke (1)");
      } // while
    } // synchronized
    while ((buf = tbr.readLine()) != null && buf.length() > 0 && !burnout) {
    if (buf.indexOf("User-Agent") >= 0) 
      System.out.println(buf);
      if (buf.startsWith("GET ")) {
        sf.appendLine(Globals.getDataDir()+"HTTP.txt",hname+" "+new Date() + " " +buf);
        StringTokenizer st = new StringTokenizer(buf);
        String token = st.nextToken();
        token = st.nextToken();
        if (token.startsWith("/")) token = token.substring(1);
        command = token;
      } // if GET
    } // while
    //    System.out.println("Command branch on '"+command+"'");
    if (command.startsWith("CHAT")) {
      String virtualip="none";
      String query="";
      command = Substituter.replace("?virtual=","&virtual=",command);
      command = Substituter.replace("?text=","&text=",command);
      command = Substituter.replace("?=","&text=",command);
      // /CHAT?=Marco&=Reply&virtual=none HTTP/1.1
      // /CHAT?virtual=none HTTP/1.1
      int n=-1;
      try {
      while (command.indexOf("&") >= 0) {
        n = command.indexOf("&"); 
        command = command.substring(n, command.length());
        if (command.startsWith("&virtual=")) { 
          int m = "&virtual=".length();
          virtualip = command.substring(m, command.length());
          int p = virtualip.indexOf("&"); 
          if (p > 0) {
            virtualip = virtualip.substring(0, virtualip.indexOf("&"));
          }
        }
        else if (command.startsWith("&text=")) {
        int m = "&text=".length();
        query = new String(command.substring(m, command.length()));
        int p = query.indexOf("&"); 
        if (p >= 0) {
          query = query.substring(0, query.indexOf("&"));
        } // if (p > 0)
      } // else if
      // make command shorter on each loop:
      if (command.length() > 0)
  	      command = command.substring(1, command.length());
	  } // while exists "&" in command
          }
          catch (Exception e) {
            System.out.println("CLERK: "+e);
            System.out.println("CLERK: "+command);
            System.out.println("CLERK: "+query);
            wps.println("CLERK: "+e);
            wps.println("CLERK: "+command);
            wps.println("CLERK: "+query);
          }
          if (virtualip.compareTo("none") == 0) {
            virtualip = hname;
          }
          query = Substituter.cleanup_http(query);
          if (Globals.getBeep()) {
            if (tk != null) tk.beep();
          }
          ps.println("HTTP/1.1 200 OK");
          ps.println("Date: "+new Date());
          ps.println("Server: ALICE Chat Robot");
          ps.println("Connection: close");
          ps.println("Content-Type: text/html");
          ps.println(sf.getfile(Globals.getDataDir()+"header.html"));
          if (Classifier.brain.size() < Globals.getBrainSize()) {
            ps.println(sf.getfile(Globals.getDataDir()+"loading.html"));
          }
      // multiline_response is a synchronized method
          String response="Cough.";
          try {
             response = Classifier.multiline_response(query, virtualip, new HTMLResponder());
          }
          catch (Exception e) {
             System.out.println("Virtual IP: "+virtualip);
             System.out.println("CLERK: "+e);
          }
          ps.println(response+"<P>");
          ps.println(
"<FORM METHOD=\"GET\" ACTION=\"CHAT\">\n"+
"<FONT FACE=\"Helvetica, Arial\">You say: \n"+
"&quot;<INPUT TYPE=\"text\" NAME=\"text\" SIZE=\"30\">&quot;\n"+
"<INPUT TYPE=\"submit\" NAME=\"submit\" VALUE=\"Reply\">\n"+
"<INPUT TYPE=\"hidden\" NAME=\"virtual\" VALUE=\""+virtualip+"\">\n"+
"<INPUT TYPE=\"reset\" NAME=\"name\" VALUE=\"Reset\">\n"+
"</FORM>");
	  //          WS.sendfile(V, "chat1.html");
          ps.println(sf.getfile(Globals.getDataDir()+"trailer.html"));
        } // if
        else {
            if (command.length() > 0)
              WS.sendfile(V,    command);
            else {
              WS.sendfile(V, Globals.getDataDir()+"HOME.html");
            }
          }
        wps.println("Clerk "+hname+" terminated normally");
        ps.flush();
        tbr.close();
        V.close();
       } catch (Exception e) {
              System.out.println("CLERK.PROCESS_CLIENT exception "+e);
       } // catch
  finally {
    t = new Date().getTime()-t;
    tcnt = tcnt + 1;
    tavg = (tavg * (tcnt-1) + t)/tcnt;
    System.out.println("Elapsed: "+t+"ms ("+tavg+") "+tcnt);
  }
} // process_client

  public void run () { // run method of Clerk()
    process_client(V, hostname, WS, tk);
  } // run method
} // Clerk class






