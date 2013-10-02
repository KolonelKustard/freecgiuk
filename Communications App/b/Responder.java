package b;

import java.util.*;
import java.net.*;
import java.io.*;



// the Responder is designed to provide a generic
// interface to the low-level functions needed to
// construct responses from inputs.
// The three required methods are:
// log: instruct the program how to log the conversation
// append: tells how to format the reply
// post_process: runs at the end of the response loop.
//
// Responder works with Classifier.multiline_response().

public interface Responder {
  String pre_process(String input, String hname);
  void log(String input, String response, String hname);
  String append(String input, String response, String scroll);
  String post_process(String reply);
}

class HTMLResponder implements Responder {

private String voice="";
private String hname="localhost";

  public String pre_process(String input, String hname) {
    return input;
  }
  public void log(String input, String response, String hname) {
    this.hname = hname;
    StringFile sf = new StringFile();
    String logfile = Globals.getLogFile();
    String logline = new Date()+" size "+Classifier.brain.size()+
       " '"+Classifier.getname(hname)+"' "+ 
       ((Classifier.get_age(hname)=="how many") ? "" : 
         Classifier.get_age(hname))+"/"+
         Classifier.get_property("personality", hname)+" "+
       ((Classifier.get_gender(hname).trim()=="he") ? "male" : 
         "female")+"/"+
       ((Classifier.get_location(hname)=="where") ? "" : 
         Classifier.get_location(hname));
    System.out.println(logline);
    sf.appendLine(logfile,logline);
    sf.appendLine(logfile,hname+" "+Globals.getClientLineContains()+input);
    System.out.println(hname+": "+input);
    sf.appendLine(logfile,new Date().toString());
    response = Substituter.suppress_html(response);
    response = response.replace('\n',' ');
    System.out.println(Globals.getBotName()+": "+response);
    sf.appendLine(logfile,Globals.getRobotLineStarts()+" "+response);
  }
  public String append(String input, String response, String scroll) {
    scroll += "<font face=\"Helvetica\" color=darkblue size=+1><b><i>";
    scroll += "> " + input + "<br>";
    scroll += "</font></i></b><b><font face=\"Helvetica\" color=black size=+1>";
    scroll +=  response + "<br>";
    scroll += "</font></b>";
    voice = voice + response;
    return scroll;
  }
 static String[] banners = {

"FunAB.gif","OS.gif","convrs.gif","Yak.gif","Domo.gif",
"Turing.gif","SleepAB.gif"
  };
  public String post_process(String reply) {
    if (Classifier.get_animagent(hname)) 
      reply += Animagent.vbscript_html(voice);
    if (Globals.getAdvertize()==true) {
      double r = Classifier.RNG.nextDouble();
      int random_amt = (int)((double)(banners.length)*r);        
      String banr = banners[random_amt];
      reply = "<table cellpadding=8 cellspacing=8>"+
      "<tr><td><a href=\"http://register.alicebot.com\">"+
      "<img src=\"http://www.alicebot.com/"+banr+"\"></a></td></tr>"+
      "<tr><td>"+reply+"</td></tr></table>";
    }
    Classifier.toFile();
    return reply;
  }
} // HTMLResponder

/*
      reply = "<table cellpadding=16 cellspacing=16><tr><td>"+reply+"</td><td>"
        +"<A href=\
        +"<img src=\"http://216.167.42.224/ad.jpg\" border=0><br><center><font size=-2>Create your own chat robot!</font></center></A></td></tr></table>";
*/
class AppletResponder implements Responder {

  public String pre_process(String input, String hname) {
    return input;
  }
    // this log() method used by the applet to try
    // to log dialogues on the applet host's web server
    // (see Access.java)
public void log(String logstring) {
try {
  InputStream in;
  logstring = Substituter.format_http(logstring);
  String urlname = "http://"+Globals.getAppletHost()+
         "/cgi-bin/Blog?"+logstring;
  //  System.out.println("URL = "+urlname);
  URL url = new URL(urlname);
  in = url.openStream();
  InputStreamReader isr = new InputStreamReader (in);
  BufferedReader br = new BufferedReader(isr);
  String line ="";
  int linesread = 0;
  int limit = 1000;
  if (br != null)
    while ((line = br.readLine()) != null) {
      //     System.out.println(line);
    }
  in.close();
  } catch (Exception e) {
      //    System.out.println(e+" -- Logging: "+logstring);
  }
}      // method log

  public void log(String input, String response, String hname) {
    log(hname+" "+Globals.getClientLineContains()+input);
    log(Globals.getRobotLineStarts()+" "+response);
  }
  public String append(String input, String response, String scroll) {
    response = response.replace('\n',' ');
    response = Substituter.suppress_html(response);
    response = Substituter.wrapText(response, 60);
    input = Substituter.wrapText(input, 60);
    scroll += "> " + input + "\n\n";
    scroll +=  response + "\n\n";
    return scroll;
  } // AppletResponder.append
  public String post_process(String reply) {
    return reply;
  }
} // AppletResponder

class RobotResponder implements Responder {

  public String pre_process(String input, String hname) {
    return input;
  }
  public void log(String input, String response, String hname) {
    StringFile sf = new StringFile();
    String logfile = Globals.getLogFile();
    String logline = new Date()+" size "+Classifier.brain.size()+
       " '"+Classifier.getname(hname)+"' "+ 
       ((Classifier.get_age(hname)=="how many") ? "" : 
         Classifier.get_age(hname))+"/"+
         Classifier.get_property("personality", hname)+" "+
       ((Classifier.get_gender(hname).trim()=="he") ? "male" : 
         "female")+"/"+
       ((Classifier.get_location(hname)=="where") ? "" : 
         Classifier.get_location(hname));
    sf.appendLine(logfile,logline);
    sf.appendLine(logfile,hname+" "+Globals.getClientLineContains()+input);
    sf.appendLine(logfile,new Date().toString());
    sf.appendLine(logfile,Globals.getRobotLineStarts()+" "+response);
    System.out.println(logline);
    System.out.println(hname+": "+input);
    System.out.println(Globals.getBotName()+": "+response);
  }
  public String append(String input, String response, String scroll) {
    response = response.replace('\n',' ');
    response = Substituter.suppress_html(response);
    response = Substituter.wrapText(response, 60);
    input = Substituter.wrapText(input, 60);
    scroll += "> " + input + "\n\n";
    scroll +=  response + "\n\n";
    return scroll;
  } // RobotResponder.append
  public String post_process(String reply) {
    Classifier.toFile();
    return reply;
  }
} // RobotResponder

class CustomResponder implements Responder {
  public String pre_process(String input, String hname) {
    return input;
  }
  public void log(String input, String response, String hname) {
  }
  public String append(String input, String response, String scroll) {
    response = Substituter.wrapText(response, 80);
    scroll = scroll + "\n" + response;
    return scroll;
  }
  public String post_process(String reply) {
    Classifier.toFile();
    return reply;
  }
} // CustomResponder

class ContestResponder implements Responder {
  public String pre_process(String input, String hname) {
    return input;
  }
  public void log(String input, String response, String hname) {
    response  = Substituter.suppress_html(response);
    response = response.replace('\n',' ');
    response = Substituter.wrapText(response, 60);
    StringFile sf = new StringFile();
    StringTokenizer st = new StringTokenizer(input, "\n");
    int n = st.countTokens();
    for (int i = 0; i < n; i++) {
      String line = st.nextToken();
      Date d = new Date();
      sf.appendLine(Loebner.logfile, "JUDGE"+Loebner.judge+"["+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds()+"]"+line);      

    }
    st = new StringTokenizer(response, "\n");
    n = st.countTokens();
    for (int i = 0; i < n; i++) {
      String line = st.nextToken();
      Date d = new Date();
      sf.appendLine(Loebner.logfile, 
"PROGRAM["+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds()+"]"+line);      
    }
  }
  public String append(String input, String response, String scroll) {
    response  = Substituter.suppress_html(response);
    response = response.replace('\n',' ');
    response = Substituter.wrapText(response, 80);
    scroll = scroll + "\n" + response;
    return scroll;
  }
  public String post_process(String reply) {
    Classifier.toFile();
    return reply;
  }
} // ContestResponder



