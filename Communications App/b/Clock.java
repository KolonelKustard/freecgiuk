package b;

/*
Clock.java: (c) 1999-2000 Dr. Richard S. Wallace
dr.wallace@mindspring.com
*/

import java.lang.*;
import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.util.*;


public class Clock extends Applet {

  public void paint(Graphics g) {
      try {
        Date now = new Date();
        g.drawString(now.toString(), 5, 12);
      }
      catch (Exception e) {
      } // try-catch
  } // method paint
} // class Clock

