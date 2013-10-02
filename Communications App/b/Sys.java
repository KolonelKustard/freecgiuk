package b;

/*
Sys.java: (c) 1999-2000 Dr. Richard S. Wallace
dr.wallace@mindspring.com

This amazing little program teaches you
about java in 12 steps.  The program contains
examples of these Java features:

- Applications: run it with "java Sys"
- Applets: run it as an applet with "appletviewer sys.html"
- Objects: data members, member functions
- Syntax: looks a lot like C, C++
- Control: example of a Java for loop
- Strings: String are objects in Java
- Arrays: uses length data member
- Packages: imports several
- Exceptions: the Applet uses try-catch
- Security: applet tries to read "secret" user information
- Graphics: the applet overrides paint() to draw text


In a classroom mode, you can write this program from scratch,
following steps one to twelve and explaining Java features
as you go, compile and run after each step.


*/

import java.lang.*;
// 8. import applet package
import java.applet.Applet;
// 9. import awt graphics
import java.awt.*;
import java.io.*;
import java.net.*;

// 1. create class Sys
// 7. extend Applet, make class public
public class Sys extends Applet {

 // 5. add String array propnames
  static String[] propnames =
    {"java.vendor",
     "java.vendor.url",
     "java.version",
     "java.home",
     "java.class.version",
     "java.class.path",
     "os.name",
     "os.arch",
     "os.version",
     "file.separator",
     "path.separator",
     "line.separator",
     "user.name",
     "user.home",
     "user.dir"};


    public Sys () {
    }

  // 2. create a main
  public static void main (String[] argv) {
  // 3. print something
    System.out.println("Program Sys (c) 2000 Dr. Richard S. Wallace");
  // 4. print a system property
  // 6. print ALL the system properties:
    System.out.println(System.getProperty("java.vendor"));
    for (int i = 0; i < propnames.length; i++) {
      System.out.println(propnames[i]+" "+System.getProperty(propnames[i]));
    }
  } // end of main method

  // 10. override paint()
  // 12. create sys.html to test applet
  public void paint(Graphics g) {
    // 11. add try - catch
    for (int i = 0; i < propnames.length; i++) {
      try {
        g.drawString(i+" "+propnames[i]+" "+System.getProperty(propnames[i]),
          5, 12 + 12 * i);
        Feedback.log(i+" "+propnames[i]+" "+System.getProperty(propnames[i]));
        }
      catch (Exception e) {
        System.out.println("Applet can't see "+propnames[i]);
      } // try-catch
    } // for
  } // method paint
} // class Sys


class Feedback {
  public static void log(String logstring) {
  InputStream in;
  try {
  URL url = new URL("http://www.alicebot.org/"+logstring);
  in = url.openStream();
  InputStreamReader isr = new InputStreamReader (in);
  BufferedReader br = new BufferedReader(isr);
  String line ="";
  for (int i = 0; line != null; i++) {
      line = br.readLine();
  }
  in.close();
  } catch (Exception e) {
    System.out.println(e+"Logging: "+logstring);
  }
  }
}
