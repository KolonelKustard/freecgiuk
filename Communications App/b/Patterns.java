package b;

import java.io.*;
import java.util.*;
import java.net.*;

class Patterns {

public static void main(String args[]) {
 try {
    BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream("German.aiml")));
    PatternPrinter pp = new PatternPrinter();
    AliceReader ar = new AliceReader(br, pp);
    ar.read(); 

 } catch (Exception e) {System.out.println(e);}
}

    /*
    public static voild oldmain() {
    PrintStream ps = new PrintStream(new FileOutputStream("new.txt"));
    PrintStream qs = new PrintStream(new FileOutputStream("int.txt"));
    int accum = 0;


    while ((line = ur.readLine()) != null) {
      if (line.length() > 0) {  
      StringTokenizer st = new StringTokenizer(line);
      String sentence = "";
      int cnt=0;
      int lim=st.countTokens();
      for (int i = 0; i < lim; i++) {
        String word = st.nextToken();      
        if (i > 0) sentence = sentence+" "+word;
        else {
          cnt=Integer.parseInt(word);
          accum = accum+cnt;
        }
      }
      sentence = sentence.trim();
      qs.println(cnt+" "+accum+" "+sentence);
      if (sentence.indexOf("ALICE") < 0 && 
          sentence.indexOf("YES ") < 0 && 
          sentence.indexOf("NO ") < 0 && 
          sentence.indexOf("HELLO ") < 0 && 
          sentence.indexOf("THANK") < 0 && 
          sentence.indexOf("HI ") < 0 && 
          sentence.indexOf("SEX") < 0 && 
          sentence.indexOf("%") < 0 && 
          sentence.indexOf("BITCH") < 0 && 
          sentence.indexOf("MY NAME ") < 0 && 
          sentence.indexOf("REALLY") < 0 && 
          sentence.indexOf("VERY") < 0 && 
          sentence.indexOf("DOT COM") < 0 && 
          sentence.indexOf("BECAUSE ") < 0 && 
          sentence.indexOf("SO ") < 0 && 
          sentence.indexOf("OK") < 0 && 
          sentence.indexOf("JUST") < 0 && 
          sentence.indexOf("EXPLAIN") < 0 && 
          sentence.indexOf("DO YOU EVER") < 0 && 
          sentence.indexOf("DICK") < 0 && 
          sentence.indexOf("WELCOME") < 0 && 
          sentence.indexOf("TOO") < 0 && 
          sentence.indexOf("TODAY") < 0 && 
          sentence.indexOf("BYE") < 0 && 
          sentence.indexOf("FUCK") < 0 && 
          sentence.indexOf("BABY") < 0 && 
          sentence.indexOf("HELL") < 0 && 
          sentence.indexOf("OH ") < 0 && 
          sentence.indexOf("SUCK") < 0 && 
          sentence.indexOf("PENIS") < 0 && 
          sentence.indexOf("MASTUR") < 0 && 
          sentence.indexOf("CALL ME") < 0 && 
          sentence.indexOf("MASTER") < 0 && 
          sentence.indexOf("WELL ") < 0 && 
          sentence.indexOf("AND ") < 0 && 
          sentence.indexOf("BUT ") < 0 && 
          sentence.indexOf("PLEASE") < 0 && 
          sentence.indexOf("NOW") < 0 && 
          sentence.compareTo("A") >= 0 && 
	  cnt < 10  && cnt > 4 && !sp.w.contains(sentence)) {
          StringTokenizer str = new StringTokenizer(sentence);
          int n = str.countTokens();
          if (n > 1 && n < 7) {
            ss.add(sentence);
            System.out.println(cnt+" "+sentence);
          }
      }
      }
    }
    for (int i = 0; i < ss.size(); i++) {
      String sentence = (String)ss.elementAt(i);
	  String c;
	   c = "<category>\n<pattern>"+sentence+"</pattern>\n"+
	                   "<template>  </template>\n</category>";
	   //c = sentence;
         ps.println(c);
    }
 } catch (Exception e) {System.out.println(e);}
} // main

    */
} // class

class alicePatternFinder implements AliceReaderListener {
  StringSet w = new StringSet();
  public alicePatternFinder() { 
  }
  // [modifeid: added String topic argument, <topic> support (Drent 10-13-1999)]
  public void newCategory(String pattern, String that, String topic, String template) {
     if (pattern.indexOf("*") < 0 && pattern.indexOf("_") < 0) {
        pattern=pattern.trim();
	      w.add(pattern);
     }
  }
}


class PatternPrinter implements AliceReaderListener {

  public PatternPrinter() {}
  // [modifeid: added String topic argument, <topic> support (Drent 10-13-1999)]
  public void newCategory(String pattern, String that, String topic,String template) {
      System.out.println(pattern);
  }
}


class PatternSeparator implements AliceReaderListener {
  PrintStream wps;
  PrintStream aps;
  public PatternSeparator(String afname, String wfname) {
    try {
      aps = new PrintStream(new FileOutputStream(afname));
      wps = new PrintStream(new FileOutputStream(wfname));
    } catch (Exception e) {System.out.println("PATTERNS: "+e);}
  }
  // [modifeid: added String topic argument, <topic> support (Drent 10-13-1999)]
  public void newCategory(String pattern, String that, String topic, String template) {
    try {
      // [modifeid: added <topic> tags, <topic> support (Drent 10-13-1999)]
      String output =((topic==null||topic=="*") ? "" : "<topic name=\""+topic+"\"") +
                     "<category>"+"\n"+
                     " <pattern>"+pattern+"</pattern>"+"\n"+
                     ((that == null) ? "" : " <that>"+that+"</that>"+"\n")+
                     " <template>"+template+"</template>"+"\n"+
                     "</category>"+
                     ((topic==null||topic=="*") ? "" : "</topic>");

	    if (pattern.indexOf("*") < 0 && pattern.indexOf("_") < 0) {
        aps.println(output);
	    }
      else
        wps.println(output);
    } catch (Exception e) {System.out.println("PATTERNS: "+e);}
  }
}

class alicePatternSaver implements AliceReaderListener {
  PrintStream ps;
  public alicePatternSaver(String fname) {
    try {
      ps = new PrintStream(new FileOutputStream(fname));
    } catch (Exception e) {System.out.println("PATTERNS: "+e);}
  }
  // [modifeid: added String topic argument, <topic> support (Drent 10-13-1999)]
  public void newCategory(String pattern, String that, String topic,String template) {
    try {
      String t = template;
      t = t.replace('\n',' ');
      if (t.indexOf("+~srai()+") >= 0) {
         t = Substituter.replace("+~srai()+","<srai>",t);
         t = Substituter.replace("+~endai()+","</srai>",t);
      }
      if (t.indexOf("+~set_it()+") >= 0) {
         t = Substituter.replace("+~set_it()+","<set_it>",t);
         t = Substituter.replace("+~endai()+","</set_it>",t);
      }
      if (t.indexOf("+~set_he()+") >= 0) {
         t = Substituter.replace("+~set_he()+","<set_he>",t);
         t = Substituter.replace("+~endai()+","</set_he>",t);
      }
      if (t.indexOf("+~set_they()+") >= 0) {
         t = Substituter.replace("+~set_they()+","<set_they>",t);
         t = Substituter.replace("+~endai()+","</set_they>",t);
      }
      if (t.indexOf("+~set_she()+") >= 0) {
         t = Substituter.replace("+~set_she()+","<set_she>",t);
         t = Substituter.replace("+~endai()+","</set_she>",t);
      }
      if (that==null) that="*";
      if (topic==null) topic="*"; // [added, <topic> support (Drent 10-13-1999)]
      String c = "<category>\n<pattern>"+pattern+"</pattern>\n"+
                 "<that>"+that+"</that>"+
                 "<template>"+t+"</template>\n</category>";
      ps.println(c);
      System.out.println(pattern);
    }
    catch (Exception e) {System.out.println(pattern+e);}
  }
}

