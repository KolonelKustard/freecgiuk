package b;

import java.io.*;      
import java.util.*;

//
// A Dialogue (not to be confused with a Dialog class!) is
// the representation of the intercourse between the client
// and the robot.
// The basic data structure is a pair of String arrays
// client_said and robot_said that store the alternating
// statements of client and robot.  The Dialogue also
// encodes the length, hostname, and start and end tag
// information.

class Dialogue {
  String hostname=""; // we identify clients by host name
  String startTag=""; // dialogue start time
  String endTag ="";  // end time
  Vector Client_said = new Vector();
  Vector Robot_said = new Vector();
  public int length() { 
    return (Client_said.size());
  }
  String getthat() {   // "that" is what the robot said before
    if (length() <= 0) return ("");
    else return ((String)(Robot_said.elementAt(length()-1)));
  }
  // constructor:
  public Dialogue(String ip) { //
    hostname = ip;
  }
  public void add_line(String csay, String rsay) {
    Client_said.addElement(csay);
    Robot_said.addElement(rsay);
  }                              
}

// class Access is an abstraction for log file analysis.

public class Access {

public static StringSet ipSet = new StringSet();

public static void add_ip(String ip) {
  ipSet.add(ip);
}

public static String ipDialogue(String hname) {
  String dialogue = "";
  Dialogue d = (Dialogue)Dialogmap.get(hname);
  int length = d.length(); 
  dialogue += "------------------"+hname+"-----------------"+"\n";
  dialogue += hname+" length "+ d.length()+"\n";
  dialogue += d.startTag+"\n";
  dialogue += d.endTag+"\n";
  for (int c = 0; c < d.length(); c++) {
     dialogue += "Client: "+d.Client_said.elementAt(c)+".\n";
     dialogue += "Robot: "+d.Robot_said.elementAt(c)+"\n";
  }
  return dialogue;
}

public static void dump() {
  System.out.println("dump");
  StringFile lsf = new StringFile();
  lsf.open(Globals.getDataDir()+"dialogt.txt","w");
  int sum = 0;
  int hostcount = ipSet.size();
  System.out.println(hostcount+" hosts");
  SortedIntSet sis = new SortedIntSet();
  for (int i = hostcount-1; i >= 0; i--) {
    String hname = (String)ipSet.elementAt(i);
    Dialogue d = (Dialogue)Dialogmap.get(hname);
    int length = d.length(); 
    sis.add(length);
  } 
  for (int u = sis.size()-1; u >= 0; u--) {
    int slength = ((Integer)(sis.elementAt(u))).intValue();
  try {
  for (int i = hostcount-1; i >= 0; i--) {
    String hname = (String)ipSet.elementAt(i);
    Dialogue d = (Dialogue)Dialogmap.get(hname);
    int length = d.length(); 
    if (length == slength) {
	if (true) { // if (d.endTag.indexOf("abusive") < 0 && d.length() > 10 && d.length() < 100) {
          lsf.appendLine("------------------"+hname+"-----------------");
          lsf.appendLine("dialogue length "+d.length());
          // lsf.appendLine(d.startTag);
	  lsf.appendLine(d.endTag.substring(28,d.endTag.length()));
          sum += d.length();
          for (int c = 0; c < d.length(); c++) {
            lsf.appendLine("Client: "+d.Client_said.elementAt(c)+".");
            lsf.appendLine("Robot: "+d.Robot_said.elementAt(c));
	  }
      } // for
    } // if
  } // for
  } catch (Exception e) {
     System.out.println("Dialogue Exception "+e);
  }
  } // for
  lsf.appendLine("Start = "+start_tag);
  lsf.appendLine("End   = "+end_tag);
  lsf.appendLine("hostcount ="+ hostcount);
  System.out.println("Start = "+start_tag);
  System.out.println("End   = "+end_tag);
  System.out.println("hostcount ="+ hostcount);
  int avg = sum/(hostcount+1);
  System.out.println(avg+" avg dialog length");
  lsf.appendLine(avg+" avg dialog length");
  lsf.close();
} // dump

public static void add_line(String ip, String tag, String csay, String rsay) {
  Dialogue d;
  Substituter subst = new Substituter();
  try {
  if (Dialogmap.containsKey(ip))
     d = (Dialogue)Dialogmap.get(ip);
  else {
    System.out.println("Connection from "+ip);
    add_ip(ip);
    d = new Dialogue(ip);
    d.startTag = tag;
  }
  d.add_line(csay, rsay);
  d.endTag = tag;
  Dialogmap.put(ip, d);
  } catch (Exception e) {System.out.println("ADD LINE: "+e);}
}

static int numlines =0;
protected static Hashtable Dialogmap=new Hashtable();
protected static String start_tag="";
protected static String end_tag="";

public static void init() {
    numlines = 0;
    Dialogmap = new Hashtable();
    ipSet = new StringSet();
    start_tag="";
    end_tag="";
  }

    //
public static void analyze(String ip) {
  init();  // call initializer
  String client_line_contains = Globals.getClientLineContains(); // "Says: ";
  String robot_line_start = Globals.getRobotLineStarts(); // "ALICE Says:";
  System.out.println("Client Line: '"+client_line_contains+"'");
  System.out.println("Robot Line: '"+robot_line_start+"'");
  Substituter subst = new Substituter();
  System.out.println("Begin Log File Analysis.");
  StringFile sf = new StringFile();
  sf.open(Globals.getAnalysisFile(),"r");
  String line;               
  int linecnt = 0;
  String hname = new String("127.0.0.1");
  String csay =new String("");
  String rsay =new String("");
  String stag =new String("");
    try {
  while ((line = sf.readLine()) != null) {
    linecnt ++;
    int n = line.indexOf(robot_line_start);
    if (n >= 0) {
      System.out.println(line);
      rsay = line.substring(n+robot_line_start.length(),line.length());
      rsay = Substituter.cleanup_http(rsay);
      if (ip == null || hname == ip)
        add_line(hname, stag, csay, rsay);
    }
    else if ((n=line.indexOf(client_line_contains)) >= 0) {
      System.out.println(line);
      hname = line.substring(0, line.indexOf(" "));
      csay = line.substring(n+client_line_contains.length(),line.length());
      csay = Substituter.cleanup_http(csay);
    }
    else if (line.indexOf("size") >= 0) {
      stag = line;
      if (start_tag.length() <= 0) start_tag = stag;
      end_tag = stag;
    }
  }
} catch (Exception e) {
}
  System.out.println(linecnt+" dialogue lines read.");
  dump();
}


public static void analyze() {
  analyze(null);
}  // analyze();

public static void main (String[] arg)
    {
  Globals.fromFile();
  analyze(); 
  //  analyze_applet_log();
    }
} // Access class


