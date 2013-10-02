package b;

import java.net.*;
import java.io.*;
import java.util.*;

// The Log class in this file is a general purpose
// file analyzer for line-formatted files.
// The interfannce LineProcessor specifies a method
// ProcessLine(s) that tells the line reader what
// to do with each line.
// The static method ProcessLines processes any
// text file with any LineProcessor interface.
// 

interface LineProcessor {
  final int LineMax=1000000;
  public void ProcessLine(String s);
}

// the simples LineProcessor is the line counter:

class LineCounter implements LineProcessor {
int linecnt=0;
    public int getLineCount () {
      return linecnt;
    }
    public void ProcessLine(String s) {
      linecnt++;
    }
}

class LineSaver implements LineProcessor {
PrintStream ps;
    public void setPS (String f) {
      try {
        FileOutputStream fos = new FileOutputStream(f);
        ps = new PrintStream(fos); 
      } catch (Exception e) {System.out.println("LINESAVER: "+e);}
    }
    public void ProcessLine(String s) {
        int n = s.indexOf(" t:");
        if (n > 0) {
            String line = s.substring(n+3);
	    ps.println(line);
        }
    }
}

class FastLoader implements LineProcessor {

protected String pattern="";
protected String that="*";
protected String template="";
protected String category="";
protected String filename="";

public void ProcessLine(String s) {
  int n; 
  //if ((n=s.indexOf("<alice>")) >= 0) {System.out.println(n+"ALICE");}
  //if ((n=s.indexOf("</alice>")) >= 0) {System.out.println(n+"/ALICE");}
  if ((n=s.indexOf("<category>")) >= 0) {
     s = "";  // assumping <category> alone on line
     category = s;
  }
  if ((n=s.indexOf("</category>")) >= 0) {
     String stag="<pattern>"; String etag ="</pattern>";  
     if (category.indexOf(stag) >= 0) {
       int start = category.indexOf(stag);
       int end = category.indexOf(etag);
       if (end <= start) System.out.println("Something funny");
       else {
         String mid = category.substring(start+stag.length(), end);
         pattern = mid;
       }
     }
     stag="<that>"; etag ="</that>";  
     if (category.indexOf(stag) >= 0) {
       int start = category.indexOf(stag);
       int end = category.indexOf(etag);
       if (end <= start) System.out.println("Something funny");
       else {
         String mid = category.substring(start+stag.length(), end);
         that = mid;
       }
     }
     stag="<template>"; etag ="</template>";  
     if (category.indexOf(stag) >= 0) {
       int start = category.indexOf(stag);
       int end = category.indexOf(etag);
       if (end <= start) System.out.println("Something funny");
       else {
         String mid = category.substring(start+stag.length(), end);
         template = mid;
       }
     }
     /*
     System.out.println(pattern);
     System.out.println(that);
     System.out.println(template);
     */
     if (Classifier.brain.size() % 1000 == 0) 
        System.out.println(Classifier.brain.size()+" Categories and Loading");
     Classifier.brain.add(pattern, that, template, filename);
  }
  else {
    category += s+"\n";
  }
}

public static void ProcessLines (String fname, FastLoader lp) {
  FileInputStream fis;
  BufferedReader br;  
  lp.filename=fname;
  try { 
    fis = new FileInputStream(fname);
    br = new BufferedReader(new InputStreamReader(fis));
    String line;
    while ((line = br.readLine()) != null) {
       lp.ProcessLine(line);
    }
  } catch (Exception e) {
    System.out.println("FastLoader.ProcessLines "+e);
  }
}  

} // class FastLoader

// Log is the class with "main" and the static "ProcessLines"
// methods:

class Log implements LineProcessor {

public void ProcessLine(String s) {

} 

public static void ProcessLines (String fname, LineProcessor lp) {
  ProcessLines (fname, lp, LineProcessor.LineMax) ;
}

public static void ProcessLines (String fname, LineProcessor lp, int limit) {
  ProcessLines (fname, lp, 0, limit);
}

public static void ProcessLines (String fname, LineProcessor lp, 
				 int start, int end) {
  FileInputStream fis;
  BufferedReader br;  
  int cnt=0;
  System.out.println("PL: "+fname+" "+start+" "+end);
  try { 
    fis = new FileInputStream(fname);
    br = new BufferedReader(new InputStreamReader(fis));
    String line;
    while (cnt < end  && 
	   (line = br.readLine()) != null && line.length() >= 0) {
       if (cnt >= start) lp.ProcessLine(line);
       cnt++;
    }
  } catch (Exception e) {
    System.out.println("fname="+fname+" start="+start+" end="+end);
    System.out.println("PROCESSLINES: "+e);
  }
}  

public static void main (String argv[]) {
convert_dont("dont.txt","/home/httpd/html/dont.html");
   //FastLoader fl = new FastLoader();
    //fl.ProcessLines("B.aiml", fl);
//convert_dont("/home/httpd/html/edit.txt","/home/httpd/html/edit.html");
analyze_refers();
//analyze_access();
//analyze_dialog("../superlog");
}

public static void convert_dont(String dontfile, String htmlfile) {
  LineCounter lc = new LineCounter();
  ProcessLines(dontfile, lc);
  int numlines = lc.getLineCount();
  System.out.println(numlines+" lines in don't read me");
  DontHTMLer dh = new DontHTMLer(htmlfile, "Dont.aiml");
  ProcessLines(dontfile, dh, 0, numlines);    
  StringFile sf = new StringFile();
  sf.appendLine("Dont.aiml","<category>");
  sf.appendLine("Dont.aiml","<pattern>HELP</pattern>");
  sf.appendLine("Dont.aiml","<template><random>"+dh.faq+"</random></template>");
  sf.appendLine("Dont.aiml","</category>");
  sf.appendLine("Dont.aiml","<category>");
  sf.appendLine("Dont.aiml","<pattern>FAQ</pattern>");
  sf.appendLine("Dont.aiml","<template><ul>"+dh.faq+"</ul></template>");
  sf.appendLine("Dont.aiml","</category>");
  sf.appendLine("Dont.aiml","</alice>");
}

// I'm assuming a Linux installation of
// an Apache web server
// 
public static void analyze_refers() {
  FileOutputStream fos;
  PrintStream ps;
  try {
  LineCounter lc = new LineCounter();
  //ProcessLines("referer_log", lc);
  //ProcessLines("/var/log/httpd/referer_log.4", lc);
  ProcessLines("/var/log/httpd/referer_log", lc);
  int numlines = lc.getLineCount();
  System.out.println(numlines+" lines in referer logs");
  //ProcessLines("/var/log/httpd/referer_log.4", new RefererLog(), 0, numlines);   
  ProcessLines("/var/log/httpd/referer_log", new RefererLog(), 0, numlines); 
  //ProcessLines("referer_log", new RefererLog(), 0, numlines); 
  fos = new FileOutputStream("/home/httpd/html/mine.html");
  ps = new PrintStream(fos);
  ps.println("<H1>ALICE's Top 10 Referers</H1>");
  ps.println("San Francisco, CA ("+new Date()+") <BR>");
  ps.println("The web sites providing the most traffic to ALICE this week were:<BR>"); 
  ps.println("Rank -- Refering Site<BR>"); 
  ps.println("<ol>"); 
  for (int i = 0; i < RefererLog.S.size(); i++) {
    int n = RefererLog.S.Count(i);
    String s = (String)RefererLog.S.elementAt(i);
    System.out.println(n+" "+s);
    String t = s;
    int fontsize = 1+(int)(0.5+Math.log((double)n)); 
   
    if (s.indexOf(".au/") > 0) t = "<font color=purple size="+fontsize+">Australia</font>";
    if (s.indexOf(".br/") > 0) t = "<font color=purple size="+fontsize+">Brazil</font>";
    if (s.indexOf(".bg/") > 0) t = "<font color=purple size="+fontsize+">Bulgaria</font>";
    if (s.indexOf(".dk/") > 0) t = "<font color=purple size="+fontsize+">Denmark</font>";
    if (s.indexOf(".fi/") > 0) t = "<font color=purple size="+fontsize+">Finland</font>";
    if (s.indexOf(".de/") > 0) t = "<font color=purple size="+fontsize+">Germany</font>";
    if (s.indexOf(".gr/") > 0) t = "<font color=purple size="+fontsize+">Greece</font>";
    if (s.indexOf(".fr/") > 0) t = "<font color=purple size="+fontsize+">France</font>";
    if (s.indexOf(".nl/") > 0) t = "<font color=purple size="+fontsize+">Holland</font>";
    if (s.indexOf(".it/") > 0) t = "<font color=purple size="+fontsize+">Italy</font>";
    if (s.indexOf(".jp/") > 0) t = "<font color=purple size="+fontsize+">Japan</font>";
    if (s.indexOf(".kr/") > 0) t = "<font color=purple size="+fontsize+">Korea</font>";
    if (s.indexOf(".nz/") > 0) t = "<font color=purple size="+fontsize+">New Zealand</font>";
    if (s.indexOf(".no/") > 0) t = "<font color=purple size="+fontsize+">Norway</font>";
    if (s.indexOf(".ro/") > 0) t = "<font color=purple size="+fontsize+">Romania</font>";
    if (s.indexOf(".su/") > 0) t = "<font color=purple size="+fontsize+">Russia</font>";
    if (s.indexOf(".es/") > 0) t = "<font color=purple size="+fontsize+">Spain</font>";
    if (s.indexOf(".ch/") > 0) t = "<font color=purple size="+fontsize+">Switzerland</font>";
    if (s.indexOf(".th/") > 0) t = "<font color=purple size="+fontsize+">Thailand</font>";
    if (s.indexOf(".tw/") > 0) t = "<font color=purple size="+fontsize+">Taiwan</font>";
    if (s.indexOf(".uk/") > 0) t = "<font color=purple size="+fontsize+">United Kingdom</font>";


    if (s.indexOf(".edu") > 0) t = "<font color=darkgray size="+fontsize+">Academic project</font>";
    if (s.indexOf("pilobil") > 0) t = "<font color=darkgray size="+fontsize+">Academic project</font>";
    if (s.indexOf(".ac.") > 0) t = "<font color=darkgray size="+fontsize+">Academic project</font>";
    if (s.indexOf(".tamu") > 0) t = "<font color=darkgray size="+fontsize+">Intro to Philosophy  Philosophy of Mind -- by Colin Allen</font>";

    if (s.indexOf("uga.edu") > 0) t = "<font color=darkgray size="+fontsize+">English 3K by Nelson Hilton</font>";
    if (s.indexOf("tuwien") > 0) t = "<font color=darkgray size="+fontsize+">The Turing Test by Robert Kosara</font>";
    if (s.indexOf("towson.edu") > 0) t = "<font color=darkgray size="+fontsize+">Types of Information Systems by Sharma Pillutla</font>";
    if (s.indexOf("msmary.edu") > 0) t = "<font color=darkgray size="+fontsize+">CS 101 Computer Technology by Prof. Scott Weiss</font>";
    if (s.indexOf("it.bton") > 0) t = "<font color=darkgray size="+fontsize+">Univ. Brighton, UK, CS 241 People and Computers by Lyn Pemberton</font>";
    if (s.indexOf("bus.olemiss") > 0) t = "<font color=darkgray size="+fontsize+">Olemiss MIS 309 Homework 6 Answers</font>";
    if (s.indexOf("um.edu.mt") > 0) t = "<font color=darkgray size="+fontsize+">Natural Language Resources by Michael Rosner</font>";
    if (s.indexOf("rmit.edu.au") > 0) t = "<font color=darkgray size="+fontsize+">CS436 Intelligent Systems by Vic Ciesielski</font>";
    if (s.indexOf("Alevel") > 0) t = "<font color=darkgray size="+fontsize+">UK School IT Coordinator A Level</font>";
    if (s.indexOf("patana") > 0) t = "<font color=darkgray size="+fontsize+">Bangkok Patana School IT Tools & Their Impact</font>";
    if (s.indexOf("uwaterloo") > 0) t = "<font color=darkgray size="+fontsize+">Consciousness and Common Sense by Christopher Small</font>";
    if (s.indexOf("tekmom") > 0) t = "<font color=darkgray size="+fontsize+">TekMom's Fun & Learning Page for Tech Teachers</font>";
    if (s.indexOf("goertzel") > 0) t = "<font color=darkgray size="+fontsize+">Artificial Intelligence Sites by Ted Goetzel</font>";
    if (s.indexOf("consalvo") > 0) t = "<font color=darkgray size="+fontsize+">Sites of Interest by Mia Consalvo</font>";
    if (s.indexOf("mpinet") > 0) t = "<font color=darkgray size="+fontsize+">Privacy and Security on the Internet by SF Kinney</font>";
    if (s.indexOf("washington.edu") > 0) t = "<font color=darkgray size="+fontsize+">Introduction to Philosophy 100a by Cathy Yu</font>";
    if (s.indexOf("edu.pl") > 0) t = "<font color=darkgray size="+fontsize+">(in Polish)</font>";
    if (s.indexOf("sdsu.edu") > 0) t = "<font color=darkgray size="+fontsize+">Computers and Human Language, Linguistics 354 by Eric Scott </font>";
    if (s.indexOf("gvu/ii") > 0) t = "<font color=darkgray size="+fontsize+">Personalized Software Agents by Jun Xiao and John Stasko</font>";

    if (s.indexOf("winmag") > 0) t = "<font color=gray size="+fontsize+">Mike Elgan in Windows Magazine</font>";
    if (s.indexOf("slashdot") > 0) t = "<font color=gray size="+fontsize+">Slashdot Linux News</font>";
    if (s.indexOf("/cimh") > 0) t = "<font color=gray size="+fontsize+">Computers in Mental Health</font>";

    if (s.indexOf("bots.internet") > 0) t = "<font color=red size="+fontsize+">Bots.Internet.Com (formerly BotSpot)</font>";
    if (s.indexOf("toptown.com") > 0) t = "<font color=red size="+fontsize+">The Simon Laven Page: Chatterbot Central</font>";
    if (s.indexOf("yahoo") > 0) t = "<font color=red size="+fontsize+">Yahoo Ontology</font>";
    if (s.indexOf("dfki") > 0) t = "<font color=red size="+fontsize+">Natural Language Software Registry</font>";
    if (s.indexOf("freshmeat") > 0) t = "<font color=red size="+fontsize+">Freshmeat Open Source Directory</font>";
    if (s.indexOf("dmoz") > 0) t = "<font color=red size="+fontsize+">Mozilla Open Directory Project</font>";
    if (s.indexOf("aaai") > 0) t = "<font color=red size="+fontsize+">American Association for Artificial Intelligence AI Topics -- Natural Language</font>";
    if (s.indexOf("u/db/acl") > 0) t = "<font color=red size="+fontsize+">Association for Computational Linguistics NLP/CL Universe</font>";
    if (s.indexOf("sourceforge") > 0) t = "<font color=red size="+fontsize+">Sourceforge Open Source Directory</font>";
    if (s.indexOf("synz") > 0) t = "<font color=red size="+fontsize+">Synz nerd search directory</font>";

    if (s.indexOf("amused") > 0) t = "<font color=cyan size="+fontsize+">Center for the Easily Amused</font>";
    if (s.indexOf("funmain") > 0) t = "<font color=cyan size="+fontsize+">Fun Links</font>";
    if (s.indexOf("coolsite") > 0) t = "<font color=cyan size="+fontsize+">Cool Site of the Day</font>";

    if (s.indexOf("triumphpc") > 0) t = "<font color=green size="+fontsize+">John Lennon Artificial Intelligence Project</font>";
    if (s.indexOf("sirkus") > 0) t = "<font color=green size="+fontsize+">The ALICE Connection by Kris Drent</font>";
    if (s.indexOf("accessterminal") > 0) t = "<font color=green size="+fontsize+">Ally by Diana Andreacchio</font>";
    if (s.indexOf("barryhughes") > 0) t = "<font color=green size="+fontsize+">Barry Hughes by Barry Hughes</font>";
    if (s.indexOf("alicebot.com") > 0) t = "<font color=green size="+fontsize+">Alicebot.com site</font>";
    if (s.indexOf("optopia.alicebot.com") > 0) t = "<font color=green size="+fontsize+">ALICE graphics by Sage Greco</font>";
    if (s.indexOf("elvis.alicebot.com") > 0) t = "<font color=green size="+fontsize+">ELVIS by Ace Craig</font>";
    if (s.indexOf("c.alicebot.com") > 0) t = "<font color=green size="+fontsize+">WinALICE by Jacco Bikker</font>";
    if (s.indexOf("hippie.alicebot.com") > 0) t = "<font color=green size="+fontsize+">Hippie: Cgi-ALICE and IRC-ALICE by Anthony Taylor</font>";
    if (s.indexOf("alison.alicebot.com") > 0) t = "<font color=green size="+fontsize+">Alison by Kris Drent</font>";
    if (s.indexOf("german.alicebot.com") > 0) t = "<font color=green size="+fontsize+">German ALICE by Christian Drossmann</font>";
    if (s.indexOf("216.167") > 0) t = "<font color=green size="+fontsize+">Www.AliceBot.Com</font>";
    if (s.indexOf("swi.hu") > 0) t = "<font color=green size="+fontsize+">MobiALICE by Norbert Batfai</font>";

    if (s.indexOf("cyberet") > 0) t = "<font color=yellow size="+fontsize+">More ESL Links from Korea</font>";
    if (s.indexOf("sol.no/~andreas") > 0) t = "<font color=yellow size="+fontsize+">English as Another Language by Andreas Lund</font>";
    if (s.indexOf("richardx") > 0) t = "<font color=yellow size="+fontsize+">Multimedia Language Lab</font>";
    if (s.indexOf("venus") > 0) t = "<font color=green size="+fontsize+">J Group Voice to Speech demo</font>";
    if (s.indexOf("bond.edu") > 0) t = "<font color=darkgray size="+fontsize+">Bond University (Australia) Intelligent Systems</font>";
    if (s.indexOf("lshauser") > 0) t = "<font color=darkgray size="+fontsize+">AI and Other Minds Larry Hauser</font>";
    if (s.indexOf("ml-nlp") > 0) t = "<font color=darkgray size="+fontsize+">Natural Language Processing by Dr. Lyold Greenwald</font>";
    if (s.indexOf("goldberg") > 0) t = "<font color=darkgray size="+fontsize+">The Robot in the Garden (Ch. 1) by Ken Goldberg</font>";
    if (s.indexOf("csus.edu") > 0) t = "<font color=darkgray size="+fontsize+">Can Computers Think? by Matt McCormick</font>";
    if (s.indexOf("plattsburgh.edu") > 0) t = "<font color=darkgray size="+fontsize+">The Most Complex Machine, CSC 121 by Catherine Lavelle</font>";

    if (s.indexOf("loebner.net") > 0) t = "<font color=magenta size="+fontsize+">Loebner Prize, an annual Turing Test</font>";
    if (s.indexOf("turing.org") > 0) t = "<font color=magenta size="+fontsize+">Andrew Hodge's Alan Turing Scrapbook</font>";
    if (s.indexOf("saygin") > 0) t = "<font color=magenta size="+fontsize+">Ayse Pinar Saygin's Turing Test Site</font>";
   
    if (s.indexOf("robitron") > 0) t = "Robitron, Robby Garner";
    if (s.indexOf("geeknews") > 0) t = "Geek News";
    if (s.indexOf("arstechnica") > 0) t = "Ars Technica PC News";
    if (s.indexOf("vandaveer") > 0) t = "Online Arcades, Java Games";
    if (s.indexOf("j/w/h") > 0) t = "Southern Humor and Recipes";
    if (s.indexOf("birch") > 0) t = "Lehigh.edu (old ALICE site includes all traffic from Yahoo/Web AI Games)";

    if (t.indexOf("color=") < 0) t = "<font color=black size="+fontsize+">"+t+"</font>";
    //    ps.print(t+" <A HREF="+s+"><font size=-1>"+s+"</font></A>");
    ps.print("<li>"+t+" <A HREF="+s+"><img src=http://alicebot.com/rarrow.gif></A>");
    //    ps.print(" "+(i+1)+". ("+n+") ");
    ps.println("</li>");
    //    ps.println("<br>");
  }
  ps.println("<HR>");
  } catch (Exception e) {System.out.println("LOG: "+e);}
}

public static void analyze_dialog(String logfile) {
  FileOutputStream fos;
  PrintStream ps;
  try {
  LineCounter lc = new LineCounter();
  LineSaver ls = new LineSaver(); 
  ls.setPS("human.txt");
  ProcessLines(logfile, lc);
  ProcessLines(logfile, ls);
  int numlines = lc.getLineCount();
  System.out.println(numlines+" lines in dialog log");
  ProcessLines(logfile, new DialogLog(), 0, numlines); 
  fos = new FileOutputStream("access.txt");
  ps = new PrintStream(fos);
  int n = DialogLog.S.size();
  int sum = 0;
  for (int i = n-1; i >= 0; i--) {
    sum = sum + DialogLog.S.Count(i);
    System.out.println(DialogLog.S.Count(i)+" "+DialogLog.S.elementAt(i));
    ps.println(DialogLog.S.Count(i)+" "+DialogLog.S.elementAt(i));
  }
  for (int i = 0; i < n; i++) {
    System.out.println(DialogLog.R.elementAt(i));
    ps.println(DialogLog.R.elementAt(i));
  }
  System.out.println(DialogLog.S.size()+" hosts");
  System.out.println("Mean: "+sum/(n+1)+" Median: "+DialogLog.S.Count(n/2));
  ps.println(DialogLog.S.size()+" hosts");
  ps.println("Mean: "+sum/(n+1)+" Median: "+DialogLog.S.Count(n/2));

  } catch (Exception e) {System.out.println("Exception "+e);}
}

public static void analyze_access() {
  FileOutputStream fos;
  PrintStream ps;
  try {
  LineCounter lc = new LineCounter();
  ProcessLines("/var/log/httpd/access_log", lc);
  int numlines = lc.getLineCount();
  System.out.println(numlines+" lines in access log");
  ProcessLines("/var/log/httpd/access_log", new AccessLog(), 0, numlines); 
  fos = new FileOutputStream("access.txt");
  ps = new PrintStream(fos);
  for (int i = AccessLog.S.size()-1; i >= 0; i--) {
    System.out.println(AccessLog.S.Count(i)+" "+AccessLog.S.elementAt(i));
    ps.println(AccessLog.S.Count(i)+" "+AccessLog.S.elementAt(i));
  }
  } catch (Exception e) {System.out.println("LOG: "+e);}
}
} // class 

class DontHTMLer implements LineProcessor {

FileOutputStream fos;
PrintStream ps; 
    PrintStream aps; // AIML File
int linecount = 0;
String filename;
public DontHTMLer(String file, String afile) {
filename = file;
try {
  fos = new FileOutputStream(filename);
  ps = new PrintStream(fos);
  fos = new FileOutputStream(afile);
  aps = new PrintStream(fos);
  aps.println("<alice>");
} catch (Exception e) {System.out.println("LOG: "+e);}
} // constructor

String pattern = "";
String template="";
public String faq = "";

public String section(String s) {
   String reply = 
     "<font face=\"Helvetica,Arial\" size=+1><H3>"+s+"</H3></font>";
  return reply;
}


    // span style=\"background: yellow\"><H4>"+s+"</H4></span>";

public void ProcessLine(String s) {
if (s.indexOf(">") >= 0) s = Substituter.replace(">","&gt;",s);
if (s.indexOf("<") >= 0) s = Substituter.replace("<","&lt;",s);
if (linecount ==  0) s = "<html><head><title>Don't Read Me: ALICE and AIML Documentation</title></head><body bgcolor=\"#FFFFFF\"><CENTER>"+
"<font face=\"Helventica,Arial\"><H1><EM><B>"+s+"</B></EM></H1></font></CENTER>";
else if (s.startsWith("\t")) s = "<CENTER><font color=darkblue><H2>"+s+"</H2></font></CENTER>";
else if (s.startsWith("Preface")) s = section(s);
else if (s.startsWith("Contents")) s = section(s);
else if (s.startsWith("I.")) s = section(s);
else if (s.startsWith("II.")) s = section(s);
else if (s.startsWith("III.")) s = section(s);
else if (s.startsWith("IV.")) s = section(s);
else if (s.startsWith("V.")) s = section(s);
else if (s.startsWith("VI.")) s = section(s);
else if (s.startsWith("VII.")) s = section(s);
else if (s.startsWith("VIII.")) s = section(s);
else if (s.startsWith("IX.")) s = section(s);
else if (s.startsWith("X.")) s = section(s);
else if (s.startsWith("Appendix")) s = section(s);
else if (s.startsWith("Contents")) s = section(s);
else if (s.startsWith("- ")) {
  if (template.length() > 0 && pattern.length() > 0) {
    aps.println("<category>");
    aps.println("<pattern>"+pattern+"</pattern>");
    aps.println("<template>"+template+"</template>");
    aps.println("</category>");
  }
  template = "";
  pattern = s;
  faq = faq + "<li>"+s+"<br></li>"+"\n";
  System.out.println(pattern);
  pattern = Substituter.normalize(pattern);
  System.out.println(pattern);
  // <A NAME="#Applet"></A>A L I C E as an Applet</H2> 
  String tag = pattern.replace(' ','_');
  String l = "<a name=\"#"+tag+"\"></a>";
  s = l+"<span style=\"background: yellow\"><H4>"+s+"</H4></span>";
}
 else {template += s+"<br>\n"; s = s+"<BR>"; }
//System.out.println(s);
ps.println(s);
linecount++;
}
}

  


class RefererLog implements LineProcessor {
static StringRanker S = new StringRanker();
Properties dns = new Properties();
int linecnt=0;

public void ProcessLine(String s) {
  StringTokenizer st = new StringTokenizer(s);
  int n = st.countTokens(); 
  linecnt++;
  String hname = st.nextToken();
  hname=hname.trim();
  if (hname.indexOf("#") > 0) hname = hname.substring(0,hname.indexOf("#"));
  hname = Substituter.replace("%7E","~", hname);
  hname = Substituter.replace("%7e","~", hname);
  hname = Substituter.replace("http://amused","http://www.amused",hname);
  hname = Substituter.replace("http://dmoz","http://www.dmoz",hname);
  hname = Substituter.replace("http://botspot","http://bots.internet",hname);
  hname = Substituter.replace("Bot","bot",hname);
  hname = Substituter.replace("http://www.botspot","http://bots.internet",hname);
  hname = Substituter.replace("ars-technica","arstechnica",hname);
  hname = Substituter.replace(".com/index.html",".com",hname);
  hname = Substituter.replace(".net/index.shtml",".net",hname);
  hname = Substituter.replace(".com/index.shtml",".com",hname);
  hname = Substituter.replace("lennon/index.shtml","lennon",hname);
  hname = Substituter.replace("lennon/","lennon",hname);
  hname = Substituter.replace("www.arstechnica","arstechnica",hname);
  hname = Substituter.replace("www.geeknews","geeknews",hname);
  hname = Substituter.replace("207.153.240.12","geeknews.net",hname);
  hname = Substituter.replace("geeknews.net/","geeknews.net",hname);
  hname = Substituter.replace("www2.islandnet.com","www.amused.com",hname);
  hname = Substituter.replace("www.crab.rutgers","crab.rutgers",hname);
  hname = Substituter.replace("home.att.net/~r.g.garner","www.robitron.com",hname);
  hname = Substituter.replace("www.fringeware.com/~robitron","www.robitron.com",hname);
  hname = Substituter.replace("zug.com/zug","zug.com",hname);
  hname = Substituter.replace("www.xml.com","xml.com",hname);
  hname = Substituter.replace("xml.com/xml","xml.com",hname);
  hname = Substituter.replace(".htm/",".htm",hname);
  if (hname.indexOf("hippie") > 0) hname = "http://hippie.alicebot.com";
  if (hname.indexOf("richardx") > 0) hname = "http://user.gru.net/richardx/grammar3.html";
  if (hname.indexOf("freshmeat") > 0) hname = "http://apps.freshmeat.net/homepage/949373166/";
  if (hname.indexOf("yahoo") > 0) hname = "http://www.yahoo.com/Computers_and_Internet/Internet/World_Wide_Web/Chat";
  if (hname.indexOf("sjlaven") > 0) hname = "http://www.toptown.com/hp/sjlaven/aiml.htm";
  if (hname.indexOf("Sjlaven") > 0) hname = "http://www.toptown.com/hp/sjlaven/aiml.htm";
  if (hname.indexOf("Computers/Artificial_Intelligence/Natural") > 0) hname = "http://www.dmoz.org/Computers/Artificial_Intelligence/Natural_Language/Chatterbots";
  if (hname.indexOf("robitron") > 0) hname = "http://www.robitron.com";
  if (hname.indexOf("sirkus") > 0) hname = "http://www.sirkussystem.com/alice";
  if (hname.indexOf("cool.infi") > 0) hname = "http://www.coolsiteoftheday.com/stuff.html";
  if (hname.indexOf("coolsiteof") > 0) hname = "http://www.coolsiteoftheday.com/stuff.html";
  if (hname.indexOf("triumphpc") > 0) hname = "http://www.triumphpc.com/john-lennon";
  if (hname.indexOf("exmachina") > 0) hname = "http://cybercafe.exmachina.net";
  if (hname.indexOf("magelang") > 0) hname = "http://www.magelang.com";
  if (hname.indexOf("amused.com") > 0) hname = "http://www.amused.com/sites.html";
  if (hname.indexOf("lehigh") > 0) hname = "http://birch.eecs.lehigh.edu/alice";
  if (hname.indexOf("oasis") > 0) hname = "XML/SGML Resource Pages";
  if (hname.indexOf("?") < 0 && 
      hname.startsWith("http") &&
      hname.compareTo("-") != 0 &&
      hname.indexOf("rwallace") < 0 &&
      hname.indexOf("127.0.0.1") < 0 &&
      //      hname.indexOf("216.167") < 0 &&
      hname.indexOf("lehigh") < 0 &&
      hname.indexOf("atoma") < 0 &&
      hname.indexOf("adfilter") < 0 &&
      hname.indexOf("charismatic") < 0 &&
      hname.indexOf("internet.internet") < 0 &&
      hname.indexOf("garth") < 0 &&
      hname.indexOf("work1") < 0 &&
      hname.indexOf("geocities") < 0 &&
      hname.indexOf("homepage") < 0 &&
      hname.indexOf("juno.com") < 0 &&
      hname.indexOf("tripod") < 0 &&
      hname.indexOf("home.earthlink") < 0 &&
      hname.indexOf("socal.rr.com") < 0 &&
      hname.indexOf("angelfire") < 0 &&
      hname.indexOf("members.") < 0 &&
      hname.indexOf("homestead") < 0 &&
      hname.indexOf("infind") < 0 &&
      hname.indexOf("iaea.org") < 0 &&
      hname.indexOf("cyberet.co.kr") < 0 &&
      hname.indexOf("bookmarks") < 0 &&
      hname.indexOf("comments.pl") < 0 &&
      hname.indexOf("deptag") < 0 &&
      hname.indexOf("robitron") < 0 &&
      hname.indexOf("cb-il1") < 0 &&
      hname.indexOf("bpm.4mg") < 0 &&
      hname.indexOf("search.msn") < 0 &&
      hname.indexOf("64.28.67") < 0 &&
      hname.indexOf("123access") < 0 &&
      hname.indexOf("diggit") < 0 &&
      hname.indexOf("register.alicebot") < 0 &&
      hname.indexOf("EASTERNL") < 0 &&
      hname.indexOf("info.apple") < 0 &&
      hname.indexOf("directory.mozilla.org") < 0 &&
      hname.indexOf("author-author") < 0 &&
      hname.indexOf("hungryhippo") < 0 &&
      hname.indexOf("bookmark") < 0 &&
      hname.indexOf("easternlincs") < 0 &&
      hname.indexOf("unreality") < 0 &&
      hname.indexOf("checkget") < 0 &&
      hname.indexOf("not.likely") < 0 &&
      hname.indexOf("ptt.dk") < 0 &&
      hname.indexOf("localhost") < 0 &&
      hname.indexOf("Read") < 0 &&
      hname.indexOf("Message") < 0 &&
      hname.indexOf("cgi-bin") < 0 &&
      hname.indexOf(".gif") < 0 &&
      hname.indexOf(".asp") < 0 &&
      hname.indexOf(".exe") < 0 &&
      hname.indexOf("mail") < 0 &&
      hname.indexOf("cimh") < 0 &&
      hname.indexOf("netcraft") < 0 &&
      hname.indexOf("206") < 0 && 
      hname.indexOf("3355") < 0 && 
      hname.indexOf("jguru") < 0 &&
      hname.indexOf("magelang") < 0 &&
      hname.indexOf("Alicebot") < 0 &&
      hname.indexOf("w.alicebot") < 0 &&
      hname.indexOf("http://alicebot") < 0 &&
      hname.indexOf("aliceBot") < 0 &&
      hname.indexOf("AliceBot") < 0) {
    System.out.println(hname);
    S.add(hname);
  }
}
}
 
class IPHandler {
String reverse_ip(String s) {
  StringTokenizer st = new StringTokenizer(s,".");
  int n = st.countTokens(); 
  String outstring = "";
  for (int i = 0; i < n; i++) {
    String w = st.nextToken();
    outstring =  w + ((i==0)? "" : ".") + outstring;
  }
  return outstring;
}
} // class IPHandlder


class DialogLog extends IPHandler implements LineProcessor {
static StringRanker S = new StringRanker();
static StringSorter R = new StringSorter();
public void ProcessLine(String s) {
  int n = s.indexOf(" t:");
  if (n > 0) {
    String ip = s.substring(0, n);
    ip = reverse_ip(ip);
  }
} // process line
} // class

class AccessLog extends IPHandler implements LineProcessor {
Properties dns = new Properties();
static StringRanker S = new StringRanker();

int linecnt=0;

public void ProcessLine(String s) {
  StringTokenizer st = new StringTokenizer(s);
  int n = st.countTokens(); 
  linecnt++;
  String w = st.nextToken();
  S.add(w);
  System.out.println(linecnt);
  if (!dns.containsKey(w)) {
     dns_lookup(w);
  }
}

public void dns_lookup (String hostname) {
  try {
    InetAddress ia = InetAddress.getByName(hostname);
    String hname = ia.getHostName();
    hname = reverse_ip(hname);
    dns.put(hostname, hname);
    System.out.println(hname);
  } catch (Exception e) {System.out.println("LOG: "+e);}
} 
}   









