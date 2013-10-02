package b;

import java.io.*;
import java.util.*;

class Loebner {
    static String prompt = "+ ";
    public static String logfile = "";
    public static String judge = "00";
    public static void main (String argv[]) {
      System.out.println("Starting A. L. I. C. E.--stand by");
      Globals.fromFile();
      Classifier.fromFile(); // read any cached property lists
      Classifier.console = false; // block console messages
      Loader loader = new Loader();
      loader.start();
      while (logfile.length() < 1) {
        System.out.println("Enter filename to append transcripts:");
        logfile = readLines().trim();
      }
      header();
      while (true) {
        String input = readLines();
        if (input.startsWith("@@X")) 
          System.out.println("Type Control-C to Exit");
        else if (input.startsWith("@@T")) 
          prompt = "? ";
        else if (input.startsWith("@@")) {
          prompt = "> ";
          String z = input.substring(2,input.indexOf(" "));
          judge = z;
          header();
          String output = Classifier.multiline_response("MY NAME IS JUDGE"+judge,
                          "localhost", 
                          new ContestResponder());
          Format(output);
        }
        else {
          String output = Classifier.multiline_response(input, "localhost", 
                          new ContestResponder());
          Format(output);
        }
      }
    }
    public static void Format(String x) {
      System.out.println(x+"\n");
    }
    public static String readLines() {
    System.out.print(prompt);
    String input="";
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        while ((str = br.readLine()).length() > 0) {
          input = input + str + " ";
        }
      }
      catch (Exception e) {}
    return input;
    }

    public static void header() {
      StringFile sf = new StringFile();
      sf.appendLine(logfile,"(c) 2000 Cambridge Center for Behavioral Sciences all rights reserved");
      sf.appendLine(logfile,"[ALICE] [Dr. Richard S. Wallace]");
      sf.appendLine(logfile,"Start at: "+new Date()+" ("+Classifier.brain.size()+" categories)");
      sf.appendLine(logfile,"*** JUDGE"+judge+" ***");
    }
}  



