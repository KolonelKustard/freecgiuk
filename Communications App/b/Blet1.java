package b;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.*;

public class Blet1 extends Applet implements Runnable {
String Query = "";
Thread thread;
Image icon;
Image buffer; // the "double" buffer
Graphics G; 
Color fill = new Color(100, 200, 50);
Color outline = new Color(0, 100, 50);
Color background  = new Color(20, 0, 60);
Color textcolor = Color.black;

int screenrows = 10;
int screencols = 80;
String screenline[] = new String[screenrows];

String icebreaker="What can I call you?";

boolean paused = false;
  public void init() {
    String gender = getParameter("gender");
    Globals.setBotGender(gender);
    String botname = getParameter("botname");
    Globals.setBotName(botname);    
    String botfile = getParameter("botfile");
    Globals.setBotFile(botfile);
    String location = getParameter("location");
    Globals.setBotLocation(location);
    String birthday = getParameter("birthday");
    Globals.setBotBirthday(birthday);
    String codebase = getParameter("codebase");
    Globals.setCodeBase(codebase);
    String botmaster = getParameter("botmaster");
    Globals.setBotMaster(botmaster);
    String applethost = getParameter("applethost");
    Globals.setAppletHost(applethost);

    /*
Sign - Astrological sign
Wear - clothing and apparel
ForFun - What the robot does for fun
BotFile - Root file of robot personality
BotName - Robot name
Friends - The robot's friends
LookLike - The robot appearance
Question - A random question
TalkAbout - favorite subjects
KindMusic - Favorite kind of music
BoyFriend - Does the robot have a boyfriend?
BotMaster - Robot author
BotGender - male, female or custom
GirlFriend - Does the robot have a girlfriend?
BotLocation - Robot location
BotBirthday - Robot activation date
FavoriteBook - Robot's favorite book
FavoriteFood - Robot's favorite food
FavoriteSong - Robot's favorite song
FavoriteBand - Robot's favorite band
FavoriteMovie - Robot's favorite movie
FavoriteColor - Robot's favorite color
    */


    Globals.setSign(getParameter("Sign"));
    Globals.setWear(getParameter("Wear"));
    Globals.setForFun(getParameter("ForFun"));
    Globals.setFriends(getParameter("Friends"));
    Globals.setLookLike(getParameter("LookLike"));
    Globals.setQuestion(getParameter("Question"));
    Globals.setTalkAbout(getParameter("TalkAbout"));
    Globals.setKindMusic(getParameter("KindMusic"));
    Globals.setBoyFriend(getParameter("BoyFriend"));
    Globals.setGirlFriend(getParameter("GirlFriend"));
    Globals.setFavoriteBook(getParameter("FavoriteBook"));
    Globals.setFavoriteFood(getParameter("FavoriteFood"));
    Globals.setFavoriteSong(getParameter("FavoriteSong"));
    Globals.setFavoriteBand(getParameter("FavoriteBand"));
    Globals.setFavoriteMovie(getParameter("FavoriteMovie"));
    Globals.setFavoriteColor(getParameter("FavoriteColor"));

    System.out.println("Start Loader"); 
    Loader loader = new Loader(true);
    loader.setPriority(Thread.MIN_PRIORITY);
    loader.start();
    try {
      Dimension size = this.size();
      buffer = this.createImage(size.width, size.height);
      G = buffer.getGraphics();
      for (int i = 0; i < screenrows-2; i++) screenline[i] = "";
        screenline[screenrows-2] = icebreaker;
      screenline[screenrows-1] = "> ";
    }
    catch (Exception e) {System.out.println("INIT: "+e);}
    try {
      icon = getImage(new URL(codebase+"/MXMVII.JPG"));
    }
    catch (Exception e) {System.out.println("ICON: "+e);}
    setBackground(background);
    addKeyListener(new KeyAdapter() {

       public void keyTyped(KeyEvent e) { 

// Kris Drent modified the Applet to detect the "Enter" key better;
// and provided a delete mechanism for the Green Screen.

    if (e.getKeyChar() == '\n' || e.getKeyChar() == '\r') {
       Loader.pause = true;
       doAskRobot();
       Query = "";
       repaint();
       Loader.pause = false;
    } // if
    // To enable the backspace key he used:
    else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
      if (Query.length() > 0)
        Query = Query.substring(0, Query.length() - 1);
    }
    else {
      System.out.println("Query = "+Query);
      Query = Query + e.getKeyChar();
    }
    screenline[screenrows-1] = "> "+Query;
    System.out.println("Query = "+Query);
    repaint();
       } // keyTyped 
    }); // inner class
    System.out.println("Start"); 
    thread = new Thread(this);
    //    thread.setPriority(Thread.MAX_PRIORITY);
    thread.start();
  } // init
    public void start() {
      paused = false;
    }
    public void stop() {
      System.out.println("Stop"); 
      paused = true;
    }
    public void doAskRobot() {
      String hname = "localhost";
      Responder robot = new AppletResponder();
      String response = Classifier.multiline_response(Query, hname, robot);
      StringTokenizer st = new StringTokenizer(response, "\n");
      int rlines = st.countTokens();
      System.out.println(rlines+" line response");
      for (int i = 0; i < screenrows-rlines-1; i++) {
        screenline[i] = screenline[i+rlines];
      }
      for (int i = 0; i < rlines; i++) {
         String line = st.nextToken();
         int m = i+screenrows-rlines-1;
         if (m >= 0) screenline[m] = line;
      }
      //      System.out.println("Repaint");
      repaint();
    }
    public void paint(Graphics g) {
	//      System.out.println("Paint");
	try {
      int yoffset=16;
      G.setColor(background);
      Dimension dsize = this.size();
      G.fillRect(0, 0, dsize.width, dsize.height);
      G.drawImage(icon, 20, yoffset+14, 64, 60, this);
      G.setColor(outline);
      G.fillRoundRect(105, yoffset, 445, 200, 160, 20);
      G.fillRoundRect(105, yoffset+210, 445, 50, 160, 20);
      G.setColor(fill);
      G.fillRoundRect(125, yoffset, 400, 200, 160, 20);
      G.fillRoundRect(125, yoffset+210, 400, 50, 160, 20);
      int size = Classifier.brain.size();
      G.setColor(textcolor);
      int spacing = 18; 
      for (int i = 0; i < 10; i++) 
        G.drawString(screenline[i],135, yoffset+20+i*spacing);
      G.drawString(Globals.getBotName()+" by "+Globals.getBotMaster(),
                   195, yoffset+235);
      G.drawString(size+" Categories and Loading", 195, yoffset+250);
      g.drawImage(buffer, 0, 0, this);
	} catch (Exception e) { System.out.println("Paint: "+e); }
    }
    public void run() {
      if (paused) System.out.println("Paused");
	try {
	    while (!paused) {
		try {
		    repaint();  Thread.sleep(1000);
                } catch (Exception e) {System.out.println("Run "+e);}
            }
	}
	catch (Exception e) {System.out.println("Run "+e);}
    } // run
    public void update(Graphics g) { paint(g); }
} 







