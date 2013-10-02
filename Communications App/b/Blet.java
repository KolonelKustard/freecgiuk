package b;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.*;

public class Blet extends Applet {

static RobotCommunicator rc;

public void init() {
  rc = new RobotCommunicator(new AppletResponder()); // activate remote logging
  add(rc, BorderLayout.CENTER);
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
  
  setVisible(true);
  
  Loader loader = new Loader(true);
  loader.start();
}



} // class Blet






