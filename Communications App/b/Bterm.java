package b;

import java.io.*;
import java.util.*;
import java.net.*;

// Bterm: A non-GUI B. Useful for running in the background.

public class Bterm {

public static void main (String args[]) {

//Constructor taken from Bawt.java, maybe you find a better solution to
//achieve this without redundant code...

    Predicates Language = new Predicates();
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   

//Variable "LoadingMsg" added

  System.out.println(Language.getProperty("LoadingMsg","Starting A. L. I. C. E.--stand by"));

  // First set data path so we can find "global.txt"
  if(args.length>0)
    Globals.setDataDir(args[0]);
  //else, default is "." or current working directory

  Globals.fromFile();
  //  System.out.println("Animagent on = "+Globals.getAnimagent());
  Classifier.fromFile(); // read any cached property lists
  Loader loader = new Loader();
  //  loader.setPriority(Thread.MAX_PRIORITY-1);
  loader.start();
  WebServer ws = new WebServer(Globals.getServerPort(), null);
  ws.setPriority(Thread.NORM_PRIORITY+1);
  ws.start();

  //  while (!loader.isFinished()) {}
  //  Classifier.save_robot();

} // end of method main


} // end of Class Bterm

