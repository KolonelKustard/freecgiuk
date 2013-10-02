package b;

import java.io.*;
import java.util.*;

// Globals
// 
// 

public final class Globals extends Object {

  public static Predicates globals = new Predicates();

    // this main is here for testing purposes only:

  public static void main (String argv[]) {
     Globals g = new Globals();
  }

  private Globals() {
    fromFile();
  }


  public static String getProperty(String propname) {
    return globals.getProperty(propname);
  }

  // --- xetDataDir(): Added for flexability. 
  //  Allows user to specify a directory
  //  that they have write permissions to, in case they don't know what the
  //  current working dirctory will be on startup (e.g. a servlet scenario.)
  //  Use DataDir for path for files to be read on startup (globals.txt)
  //  or any data file to be written to (ip_XX.txt, gossip.txt) Note this is
  //  *not* used for AIML files because their location can be specified in
  //  globals.txt or B.aiml without this property interfering.
  public static String getDataDir() {
      String strDataDir = globals.getProperty("DataDir",".");
      String strFsep = System.getProperty("file.separator", "/");
      if(!(strDataDir.substring(strDataDir.length()-1,strDataDir.length()).equals(strFsep)))
        strDataDir= strDataDir+strFsep;
      return (strDataDir);
  }
  public static void setDataDir(String v) {
     globals.put("DataDir", v);
  }
  //--------------------

    // log file analysis parameters:
  public static int getStartLine () {
      String x = globals.getProperty("StartLine","0");
      return Integer.parseInt(x);
  }
  public static int getBrainSize () {
      String x = globals.getProperty("BrainSize","1000");
      return Integer.parseInt(x);
  }
  public static int getClerkTimeout () {
      String x = globals.getProperty("ClerkTimeout","100000");
      return Integer.parseInt(x);
  }
  public static int getEndLine () {
      String x = globals.getProperty("EndLine","0");
      return Integer.parseInt(x);
  }
  public static String getClientLineContains () {
      return (globals.getProperty("ClientLineContains","Client:"));
  }
  public static String getRobotLineStarts () {
      return (globals.getProperty("RobotLineStarts","Robot:"));
  }

    // getVersion is a "read-only" global parameter:

  public static String getVersion() {
    globals.put("Version", "1.25");
    return(globals.getProperty("Version"));
  }

    // web server paramters:
  public static int getServerPort () {
      String x = globals.getProperty("ServerPort","2001");
      return Integer.parseInt(x);
  }
  public static String getBotFile () {
      return (globals.getProperty("BotFile",Globals.getDataDir()+"B.aiml"));
  }
  public static void setBotFile(String v) {
     globals.put("BotFile", v);
  }
  public static String getTempFile () {
      return (globals.getProperty("TempFile",Globals.getDataDir()+"temp.aiml"));
  }
  public static String getLogFile () {
      return (globals.getProperty("LogFile",Globals.getDataDir()+"dialog.txt"));
  }
  public static String getAnalysisFile () {
      return (globals.getProperty("AnalysisFile",Globals.getDataDir()+"dialog.txt"));
  }
  public static String getACFURL() {
      return (globals.getProperty("ACFURL","http://microsoft.com/agent2/chars/robby/robby.acf"));
  }

    // whether to merge or discard duplicate patterns:
  public static String getMergePolicy () {
      return (globals.getProperty("MergePolicy","Merge"));
  }

  // the beep option is set by a menu item:
  public static boolean getBeep() {
    return(globals.getProperty ("Beep","false").compareTo("true") == 0);
  }
  public static void setBeep(boolean v) {
    if (v) globals.put("Beep", "true");
    else globals.put("Beep", "false");
  }
  // Animagent controls MS agent script activation:
  public static boolean getAnimagent() {
    return(globals.getProperty ("Animagent","false").compareTo("true") == 0);
  }
  public static void setAnimagent(boolean v) {
    if (v) globals.put("Animagent", "true");
    else globals.put("Animagent", "false");
  }

  // Advertizing:
 public static boolean getAdvertize() {
    return(globals.getProperty ("Advertize","false").compareTo("true") == 0);
  }
  public static void setAdvertize(boolean v) {
    if (v) globals.put("Advertize", "true");
    else globals.put("Advertize", "false");
  }
    // Applet options:
  public static String getCodeBase () {
      return (globals.getProperty("CodeBase","http://206.184.206.210/B"));
  }
  public static void setCodeBase(String v) {
     globals.put("CodeBase", v);
  }
  public static String getAppletHost () {
      return (globals.getProperty("AppletHost","206.184.206.210"));
  }
  public static void setAppletHost(String v) {
     globals.put("AppletHost", v);
  }

    // AIML tag options:
  public static String getBotMaster () {
      return (globals.getProperty("BotMaster","Dr. Richard S. Wallace"));
  }
  public static void setBotMaster(String v) {
     globals.put("BotMaster", v);
  }
  public static String getBotGender () {
      return (globals.getProperty("BotGender","woman"));
  }
  public static void setBotGender(String v) {
     globals.put("BotGender", v);
  }
  public static String getBotBirthday () {
      return (globals.getProperty("BotBirthday","November 23, 1995"));
  }
  public static void setBotBirthday(String v) {
     globals.put("BotBirthday", v);
  }

  public static String getBotBirthplace () {
      return (globals.getProperty("BotBirthplace","Bethlehem, Pennsylvania"));
  }
  public static void setBotBirthplace(String v) {
     globals.put("BotBirthplace", v);
  }
  public static String getBotLocation () {
      return (globals.getProperty("BotLocation","San Francisco"));
  }
  public static String getBoyFriend () {
      return (globals.getProperty("BoyFriend","No I am single."));
  }
  public static String getGirlFriend () {
      return (globals.getProperty("GirlFriend","No but I know a lot of female humans."));
  }
  public static String getFavoriteFood () {
      return (globals.getProperty("FavoriteFood","ALICE requires only electriciy."));
  }
  public static String getFavoriteColor () {
      return (globals.getProperty("FavoriteColor","Green."));
  }
  public static String getFavoriteMovie () {
      return (globals.getProperty("FavoriteMovie","Starship Troopers."));
  }
  public static String getFavoriteSong () {
      return (globals.getProperty("FavoriteSong","\"We are the Robots\""));
  }
  public static String getFavoriteBook () {
      return (globals.getProperty("FavoriteBook","The Bible."));
  }
  public static String getFavoriteBand () {
      return (globals.getProperty("FavoriteBand","Kraftwerk"));
  }

  public static String getLookLike () {
      return (globals.getProperty("LookLike","I look like a computer."));
  }

  public static String getWear () {
      return (globals.getProperty("Wear","I am wearing my usual plastic computer wardrobe.."));
  }

  public static String getTalkAbout () {
      return (globals.getProperty("TalkAbout","My favorite subject is robots."));
  }

  public static String getForFun () {
      return (globals.getProperty("ForFun","I enjoy chatting online."));
  }

  public static String getQuestion () {
      return (globals.getProperty("Question","Are you a man or a woman?"));
  }

  public static String getKindMusic () {
      return (globals.getProperty("KindMusic","I like techno and Opera."));
  }
  public static String getSign () {
      return (globals.getProperty("Sign","Saggitarius.  Fire signs rule."));
  }

  public static void setSign(String v) {
     globals.put("Sign", v);
  }
  public static void setWear(String v) {
     globals.put("Wear", v);
  }
  public static void setForFun(String v) {
     globals.put("ForFun", v);
  }
  public static void setFriends(String v) {
     globals.put("Friends", v);
  }
  public static void setQuestion(String v) {
     globals.put("Question", v);
  }
  public static void setFavoriteFood(String v) {
     globals.put("FavoriteFood", v);
  }
  public static void setFavoriteBook(String v) {
     globals.put("FavoriteBook", v);
  }
  public static void setFavoriteSong(String v) {
     globals.put("FavoriteSong", v);
  }
  public static void setFavoriteBand(String v) {
     globals.put("FavoriteBand", v);
  }
  public static void setFavoriteMovie(String v) {
     globals.put("FavoriteMovie", v);
  }
  public static void setFavoriteColor(String v) {
     globals.put("FavoriteColor", v);
  }
  public static void setKindMusic(String v) {
     globals.put("KindMusic", v);
  }
  public static void setGirlFriend(String v) {
     globals.put("GirlFriend", v);
  }
  public static void setBoyFriend(String v) {
     globals.put("BoyFriend", v);
  }
  public static void setLookLike(String v) {
     globals.put("LookLike", v);
  }
  public static void setTalkAbout(String v) {
     globals.put("TalkAbout", v);
  }

  public static String getFriends () {
      return (globals.getProperty("Friends","I have a lot of friends, like ELVIS, ListBot, MS Agent, Electra, AskJeeves and Mabel."));
  }

  public static void setBotLocation(String v) {
     globals.put("BotLocation", v);
  }
  public static String getBotName () {
      return (globals.getProperty("BotName","ALICE"));
  }
  public static void setBotName(String v) {
     globals.put("BotName", v);
  }

// method toFile ()
public static void toFile(String file) {
  try {
     globals.store(new FileOutputStream(file), null);
  } // try
  catch (Exception e) {System.out.println("GLOBALS.TOFILE: "+e);}
}

static void toFile () {
  toFile(Globals.getDataDir()+"globals.txt");
}

public static void fromFile(String file) {
  try {
    globals.load(new FileInputStream(file));
  }
  catch (Exception e) {
     System.out.println("GLOBALS.FROMFILE: "+e);
  }

//The master of redundant code was here again...
//Added the constructor from Bawt.java

    Predicates Language = new Predicates();
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   

//Added variable "OptionsLoadedMsg"
  System.out.println(Language.getProperty("OptionsLoadedMsg","Number of options loaded:")+" "+globals.size());
  //  System.out.println("Loaded "+globals);
} // fromFile

public static void fromFile () {
  fromFile(Globals.getDataDir()+"globals.txt");
} // fromFile
} // end of class Globals







