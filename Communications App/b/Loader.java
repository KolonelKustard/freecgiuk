package b;

import java.io.*;
import java.util.*;
import java.net.*;

// class Loader is our routine to read the robot data file
// (e.g. "B.html") in a background thread, so that the
// the User Interface can start without waiting for the
// robot brain to load.
//
//
public class Loader extends Thread {
  public boolean isApplet = false;
  public static String currfile="None";
  public static boolean pause=false;
  public boolean finished=false;
 
  public boolean isFinished() {
    return finished;
  }

  public Loader(boolean isApplet) {
    this.isApplet = isApplet;
    this.finished = false;
  }

  public Loader() {
  }

  public void run() {
    if (isApplet)  {
      String bfile = Globals.getBotFile();
      if (bfile.charAt(0)=='/') bfile = bfile.substring(1,bfile.length());
      Classifier.read_aiml_url(Globals.getCodeBase()+"/"+bfile);
      //      Classifier.read_aiml_url("http://206.184.206.210/B/Blet.aiml");
       }
    else {
      Classifier.read_aiml(Globals.getBotFile());
    }
    this.finished = true;
  }
}
