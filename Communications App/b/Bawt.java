package b;

/** 
 *    This program is an implementation of ALICE in Java.
 *    Copyright (C) 1998-2000 Richard S. Wallace
 *
 * Program B
 * January 1, 2000 release
 * (see the file "release.txt" for the latest release date)
 *
 * Program B is a revision of the Program formerly known as "A".
 * The original Program A was a console application; Program B
 * includes a Java AWT GUI interface.
 * Program A contained a Web Server Thread; Program B also supports
 * an applet called "Blet.java" that runs the chatterbot on the
 * remote client computer.    The Web Server in program B is
 * simpler than Program A, because it no longer supports the
 * Web-based AIML editor.
 * Program B uses a GUI Text window and implements several
 * important Botmaster functions as JButtons and JMenu Items.
 *
 * The basic AIML specification remains the same as in Program A.
 * Each Category of AIML is
 * <category>
 * <pattern> Pattern </pattern>
 * <that> ThatPattern </that>     * optional
 * <template> Template </template>
 * </category>
 *
 * in the current implementation.
 * In general the AIML Categories form a dataset that might be
 * stored in a database or with different syntax for the tags.
 * The ThatPattern specifies an optional second pattern to match
 * with the robot output "that".  
 *
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
//import javax.swing.*;
//import javax.swing.border.*;
// This program has been intentionally "dumbed down" to
// use AWT features (for the time being) due
// to the widespread use of Java VM's without Swing packages.
//
import java.net.*;
import java.awt.datatransfer.*; // for clipboard access

/**
 * class B is derived from Frame (We're using AWT - Java 1.1 here)
 * to create a GUI application 
 */

public class Bawt extends Frame implements ActionListener // was JFrame 
{
   static int WIDTH = 512;
   static int HEIGHT = 480;
   static Loader loader;
   TextArea ta;
   Label statusInfo;

   /** 
    * Adds the given options to the menu
    *
    * @param menu - the menu the options are to be added to
    * @param options - String[2]{actionId, defaultDescription}. Pass null to
    *                  add a separator to the menu.
    * @param language - the language to use for the description.
    */
   
   private void setupMenu(Menu menu, String[][] options, Predicates language)
   {
      MenuItem item;
      
      for (int i=0 ; i<options.length ; i++)
      {
         if (options[i]==null)
            menu.addSeparator();
         else
         {          
            item = new MenuItem(language.getProperty(options[i][0],options[i][1]));
            menu.add(item);
            item.addActionListener(this);
            item.setActionCommand(options[i][0]);
         }
      }
   }

   /** 
    * Creates a Button to be added to the toolbar. 
    * Also registers this as an actionListener for the button.
    *
    * @param id - the actionId of the button. Also used to get language-specific description.
    * @param defaultDescription - the default description of the button.
    * @param language - the language to use for the description.
    */

   private Button createButton(String id, String defaultDescription, Predicates language)
   {
      Button jb = new Button(language.getProperty(id,defaultDescription));
      jb.setActionCommand(id);
      jb.addActionListener(this);
      return jb;
   }
   
   /** Constructs the frame with the default title */

   public Bawt() 
   {
      this("ALICE Botmaster "+Globals.getVersion()+" "+Globals.getBotName());
   }

   /** Constructs the frame with the specified title */
   
   public Bawt(String title) 
   {
      super(title);
      
      Predicates Language = new Predicates();

      try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
      catch(Exception e) {}

      // Setup Screen
      // From top to bottom, have a ToolBar
      // TextArea, and Label.
      Panel ToolBar = new Panel();
      add(ToolBar, BorderLayout.NORTH);
      
      // Create the text area:
      
      ta = new TextArea(24, 80);
      ta.setFont(new Font("Courier", Font.PLAIN, 12));
      add(ta, BorderLayout.CENTER);
      statusInfo = new Label();
      add(statusInfo, BorderLayout.SOUTH);
      // Setup JMenus
      // Create ToolBar
      MenuBar menuBar = new MenuBar();
      setMenuBar(menuBar);
      MenuItem item;
      
      //--------------------------
      // Add file menu to menu bar
      //--------------------------
      
      String[][] fileMenuItems = 
      {
         //id, default label
         {"LoadRobot","Load Robot"},
         {"MergeRobot","Merge Robot"},
         {"SaveRobot", "Save Robot"},
         {"SaveRobotAs", "Save Robot As"},
         {"NewTextFile","New Text File"},
         {"OpenTextFile","Open Text File"},
         {"SaveAsTextFile","Save As Text File"},
         null, //seperator
         {"Exit","Exit"}
      };
      
      Menu file = new Menu(Language.getProperty("FileMenu","File"));
      setupMenu(file, fileMenuItems, Language);
      menuBar.add(file);
      
      //--------------------------
      // Add edit menu to menu bar
      //--------------------------
      
      String[][] editMenuItems = 
      {
         {"Paste","Paste"}
      };
      
      Menu edit = new Menu(Language.getProperty("EditMenu","Edit"));
      setupMenu(edit, editMenuItems, Language);
      menuBar.add(edit);
      
      //-----------------------------
      // Add options menu to menu bar
      //-----------------------------

      String[][] optionsMenuItems = 
      {
         {"PersonalityWizard","Personality Wizard"},
         {"KidInterface","Kid Interface"},
         {"ShowOptions","Show Options"},
         {"SaveOptions","Save Options"},
         {"CreateApplet","Create Applet"},
         {"ToggleBeep","Toggle Beep"},
         {"ToggleMSAgent","Toggle MS Agent"}
      };
      
      Menu options = new Menu(Language.getProperty("OptionsMenu","Options"));
      setupMenu(options, optionsMenuItems, Language);
      menuBar.add(options);

      //-------------------------
      // Add bot menu to menu bar
      //-------------------------

      String[][] botMenuItems = 
      {
         {"Dialogues","Dialogues"},
         {"ClassifyButton","Classify"},
         {"ShowActivation","Show Activation"},
         {"ListPatterns","List Patterns"},
         {"DefaultTargets","Default Targets"},
         {"RecursiveTargets","Recursive Targets"},
         {"AutoChat","AutoChat"},
         {"AddAIML","Add AIML"}
      };

      Menu bot = new Menu("Botmaster");
      setupMenu(bot, botMenuItems, Language);
      menuBar.add(bot);

      //--------------------------
      // Add help menu to menu bar
      //--------------------------

      String[][] helpMenuItems = 
      {
         {"HelpHelp","Help Help"},
         {"RandomHelpQuestion","Random Help Question"},
         {"ShowAllHelpQuestions","Show All Help Questions"},
         {"AskHelpQuestion","Ask Help Question"},
         {"DontReadMe","Don't Read Me"},
         {"GNUPublicLicense","GNU Public License"}
      };

      Menu help = new Menu(Language.getProperty("HelpButton","Help"));
      setupMenu(help, helpMenuItems, Language);
      menuBar.add(help);
            
      //------------------------    
      // Setup ToolBar/Tool Tips
      //------------------------

      ToolBar.add(createButton("SendButton","Send",Language));
      ToolBar.add(createButton("ClearButton","Clear",Language));
      ToolBar.add(createButton("ClassifyButton","Classify",Language));
      ToolBar.add(createButton("QuickTargetsButton","Quick Targets",Language));
      ToolBar.add(createButton("MoreTargetsButton","More Targets",Language));
      ToolBar.add(createButton("HelpButton","Help",Language));
   }


   /** MAIN: allows one optional argument, the path of the startup data directory */

   public static void main(String args[]) 
   {     
      //Again: redundant code, but I'll try to find a better solution...
      
      Predicates Language = new Predicates();

      try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
      catch(Exception e) {}
      
      //Added variabe "LoadingMsg"
      System.out.println(Language.getProperty("LoadingMsg","Starting A. L. I. C. E. --stand by")+"\nVersion: "+Globals.getVersion());
      
      // First set data path so we can find "global.txt"
      if(args.length>0)
         Globals.setDataDir(args[0]);
      //else, default is "." or current working directory
      
      //Added Variable "NameOfDataDir"
      
      System.out.println(Language.getProperty("NameOfDataDir"," [Using data location ")+" \""+Globals.getDataDir()+"\"]");
      
      Globals.fromFile();
      //  System.out.println("Animagent on = "+Globals.getAnimagent());
      Bawt frame = new Bawt();
      Classifier.fromFile(); // read any cached property lists
      
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {System.exit(0);}
      });

      frame.setSize(WIDTH, HEIGHT);
      frame.setVisible(true);
      Toolkit tk = frame.getToolkit();
      loader = new Loader();
      loader.setPriority(Thread.MAX_PRIORITY-1);
      loader.start();
      //  while(Classifier.brain.size() < 1000);
      WebServer ws = new WebServer(Globals.getServerPort(), tk);
      ws.setPriority(Thread.NORM_PRIORITY+1);
      ws.start();
      
   } 

   /** doNewCommand() : clears the work area */

   void doNewCommand()
   {
      ta.setText("");
      statusInfo.setText("Ready");
   }

   void doHelpCommand()
   {
      panefile(Globals.getDataDir()+"dont.txt");
   }

   void doAboutCommand()
   {
      panefile(Globals.getDataDir()+"gnu.txt");
   }

   void doPaste()
   {
      Clipboard c = this.getToolkit().getSystemClipboard();
      Transferable t = c.getContents(this);

      if (t == null)
      {
         getToolkit().beep();
         return;
      }
      try
      {
         String cliptext = (String) t.getTransferData(DataFlavor.stringFlavor);
         ta.setText(cliptext);
         statusInfo.setText("Paste");
      }

      catch(Exception e) {System.out.println("PASTE: "+e);}
   }

   void doCloseCommand(int status)
   {
      System.exit(status);
   }

   /** save Pane as a text file: */

   void doSaveCommand()
   {
      FileDialog file = new FileDialog(Bawt.this, "Save Text File", FileDialog.SAVE);
      file.show(); // Blocks
      String curFile;

      if ((curFile = file.getFile()) != null)
      {
         String filename = file.getDirectory() + curFile;
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         File f = new File(filename);

         try
         {
            FileWriter fw = new FileWriter(f);
            fw.write(ta.getText(), 0, ta.getText().length());
            fw.close();
            statusInfo.setText("Saved: " + filename);
         }
         catch(IOException exc)
         {
            statusInfo.setText("IOException: " + filename);
         }

         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   void doAddAIML()
   {
      String filename = Globals.getTempFile();
      File f = new File(filename);
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try
      {
         FileWriter fw = new FileWriter(f);
         String text = ta.getText();
         text = new String("<alice>\n"+text+"\n</alice>");
         int textsize = text.length();
         fw.write(text, 0, textsize);
         fw.close();
         statusInfo.setText("Saved: " + filename);
      }
      catch(IOException exc)
      {
         statusInfo.setText("IOException: " + filename);
      }

      Classifier.append_aiml_file(Globals.getTempFile());
      statusInfo.setText("Appended AIML");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   void doSaveRobot()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      if (loader.isFinished())
      {
         Classifier.save_robot();
         statusInfo.setText("Saved: " + Globals.getBotFile());
      }
      else
      {
         String warning = new String("The Loader is still running.  \n"+
            "Please wait until the brain is loaded before saving it.");
         ta.setText(warning);
         statusInfo.setText("Wait: the Loader is still running.");
      }
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   void doSaveRobotAs()
   {
      FileDialog file = new FileDialog(Bawt.this, "Save Robot", FileDialog.SAVE);
      file.setFile("*.aiml");  // Set initial filename filter
      file.show(); // Blocks
      String curFile;

      if ((curFile = file.getFile()) != null)
      {
         String filename = file.getDirectory() + curFile;
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         Classifier.save_robot_as(filename);
         statusInfo.setText("Saved: " + filename);
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   /** displays formatted Dialogues */

   void doDialogues()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Access.analyze();
      panefile(Globals.getDataDir()+"dialogt.txt");
      statusInfo.setText("Dialogues");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }


   boolean isFinal(int i)
   {
      return Classifier.brain.Pattern(i).indexOf("*") < 0;
   }

   boolean isRecursive(int i)
   {
      return Classifier.brain.Template(i).indexOf("<sr") >= 0;
   }

   boolean isDefault(int i)
   {
      return Classifier.brain.Pattern(i).compareTo("*") == 0;
   }

   boolean isTarget(int i)
   {
      return !(isFinal(i) || isRecursive(i) || isDefault(i));
   }

   String doRecursiveTargets()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      String output =  "";  int mx = 0;
      int count = 0;

      for (int i = 0; i < Classifier.brain.size(); i++)
      {
         for (int j = 0; j < Classifier.brain.Target(i).size(); j++)
         {
            if (isRecursive(i))
            {
               String ex = (String)Classifier.brain.Target(i).elementAt(j);

               if (ex.length() > 100) 
                  ex = ex.substring(0,100); 

               String eline =
                  "<category>\n"+
                  "<pattern>"+ex+"</pattern>  "+
                  "<template>"+Classifier.brain.Pattern(i)+"</template>\n</category>\n";

               output += eline;
               count++;
            }
         } 
      }

      ta.setText(output);
      //  ta.setCaretPosition(0); // scroll text view to TOP of document
      statusInfo.setText("Targets");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      return(output);
   }

   String doDefaultTargets()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      String output =  "";  int mx = 0;
      int count = 0;

      for (int i = 0; i < Classifier.brain.size(); i++)
      {
         for (int j = 0; j < Classifier.brain.Target(i).size(); j++)
         {
            if (isDefault(i))
            {
               String ex = (String)Classifier.brain.Target(i).elementAt(j);
               
               if (ex.length() > 100)
                  ex = ex.substring(0,100); 

               String eline =
                  "<category>\n"+
                  "<pattern>"+ex+"</pattern>  "+
                  "<template>"+Classifier.brain.Pattern(i)+"</template>\n</category>\n";

               output += eline;
               count++;
            }
         } 
      }

      ta.setText(output);
      //  ta.setCaretPosition(0); // scroll text view to TOP of document
      statusInfo.setText("Targets");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      return(output);      
   }

   String StringTargets(int limit)
   {
      String output =  "";
      int count = 0;
      SortedIntSet sis = new SortedIntSet();
      SortedIntSet tis = new SortedIntSet();

      for (int i = 0; i < Classifier.brain.size(); i++)
      {
         int w = Classifier.brain.Target(i).size(); 

         if (w > 0) 
            tis.add(w);

         for (int j = 0; j < Classifier.brain.Target(i).size(); j++)
         {
            if (Classifier.brain.Target(i) != null)
               sis.add(Classifier.brain.Target(i).Count(j));
         }
      }

      StringFile sf = new StringFile();
      sf.delete(Globals.getTempFile());

      for (int z = tis.size()-1; z >= 0; z--)
      {
         int v = ((Integer)(tis.elementAt(z))).intValue();

         for (int k = sis.size()-1; k >= 0; k--)
         {
            int n = ((Integer)(sis.elementAt(k))).intValue();

            for (int i = Classifier.brain.size()-1; i >= 0; i--)
            {
               if (Classifier.brain.Target(i).size() == v)
               {
                  for (int j = 0; j < Classifier.brain.Target(i).size(); j++)
                  {
                     if (count < limit && 
                        isTarget(i) && Classifier.brain.Target(i).Count(j)==n)
                     {
                        String ex = (String)Classifier.brain.Target(i).elementAt(j);

                        if (ex.length() > 100)
                           ex = ex.substring(0,100); 

                        String eline =
                           "<category>\n"+
                           "<pattern>"+ex+"</pattern>  "+
                           "<template>"+Classifier.brain.Pattern(i)+"</template>\n"+
                           "</category>\n";

                        sf.appendLine(Globals.getTempFile(), eline);
                        output += eline;
                        count++;
                     }
                  }
               } 
            } 
         } 
      } 

      return output;
   }

   public static int targetcount = 25;
   public static int targetincrement = 25;

   void doMoreTargets()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      String output = "";

      try
      {
         output = StringTargets(targetcount);
         targetcount += targetincrement;
      }
      catch(Exception e) 
      {
         System.out.println("Target exception "+e);
      }

      if (output.length() <= 0)
      {
         output =
            "The Targeting operation did not find any targets.\n\n"+
            "1. Remember to run \"Classify\" before Targets\n"+
            "2. Is the dialog file empty or too short?\n"+
            "3. Please report bugs to alicebot.listbot.com\n";
      }

      ta.setText(output);
      ta.setCaretPosition(output.length()); // scroll text view to TOP of document
      statusInfo.setText("Targets");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   void doQuickTargets()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      String output = "";

      try
      {
         targetcount = targetincrement;
         output = StringTargets(targetcount);  
         targetcount += targetincrement;
      }
      catch(Exception e) 
      {
         System.out.println("Target exception "+e);
      }

      if (output.length() <= 0) 
      {
         output =
            "The Targeting operation did not find any targets.\n\n"+
            "1. Remember to run \"Classify\" before Targets\n"+
            "2. Is the dialog file empty or too short?\n"+
            "3. Please report bugs to alicebot.listbot.com\n";
      }

      ta.setText(output);
      statusInfo.setText("Targets");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   void doRandomHelp()
   {
      ta.setText("HELP"); 
      doAskRobot();
   }

   void doHelpHelp()
   {
      ta.setText("What does help do?"); 
      doAskRobot();
   }

   void doIndex()
   {
      ta.setText("FAQ"); 
      doAskRobot();
   }

   /** method doAskRobot sends the Pane's text to the robot and displays the response. */

   void doAskRobot()
   {
      String input = ta.getText();
      Responder robot = new GUIResponder();
      String hname = "localhost";
      String response = Classifier.multiline_response(input, hname, robot);
      ta.setText(response);
      statusInfo.setText("Chat");
   }

   void doAutochatCommand()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      String query = ta.getText();

      if (query.length() <= 0)
         query = "My name is ALICE";

      Classifier.setname("127.0.0.1", "ALICE");
      String response = "";
      String agent = "A";

      for (int n = 0; n < 32; n++)
      { 
         System.out.println(n);

         try
         {
            query = Substituter.deperiodize(query);
            StringTokenizer qt = new StringTokenizer(query, ".!?");
            int ct = qt.countTokens();
            String reply = "";

            for (int x = 0; x < ct; x++)
            { 
               String sentence = qt.nextToken();
               sentence = sentence.trim();

               if (sentence.length() > 0 && x == ct-1)
               {
                  String norm = Substituter.normalize(sentence);
                  //System.out.println("Norm = "+norm);
                  reply = Classifier.respond(norm, "127.0.0.1");
                  reply = Substituter.suppress_html(reply);
                  Classifier.set_that("127.0.0.1", reply);
               }
            }

            if (agent.startsWith("A")) 
               agent = "B";
            else 
               agent = "A";

            query = reply;
            response += reply+"\n";
         } 
         catch (Exception e)
         {
            System.out.println("Autochat Exception "+e);
         }
      }

      ta.setText(response);
      //  ta.setCaretPosition(0);
      statusInfo.setText ("Autochat");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   // Classify Dialogues

   void doShowActivation()
   {
      ta.setText("");
      ta.setText(Classifier.brain.HistoString());
      //  ta.setCaretPosition(0);
      statusInfo.setText("Brain Activation");
   }

   void doListPatterns()
   {
      String buf ="";
      ta.setText("");
      // [added for debugging, <topic> support (Drent 10-13-1999)]
      //Classifier.brain.showPatternSearchStrings();
      
      try
      {
         PrintStream ps = new PrintStream(new FileOutputStream(Globals.getDataDir()+"patterns.txt"));

         for (int i = 0; i < Classifier.brain.size(); i++)
         {
            if (i % 30 == 0 || i == Classifier.brain.size()-1)
            {
               ta.append(buf);
               buf = "";
            }

            String tpat = "";

            if (Classifier.brain.ThatPattern(i).compareTo("*") != 0)
               tpat = "\t\t\t"+Classifier.brain.ThatPattern(i);

            String pat = Classifier.brain.Pattern(i);
            pat = Substituter.replace(Globals.getBotName(), "<name/>", pat);
            ps.println(pat+tpat+"\t\t\t"+Classifier.brain.Filename(i));
            buf = buf + pat + tpat+"\t\t\t"+Classifier.brain.Filename(i)+"\n";
         }
         //  ta.setText(Classifier.brain.PatternstoString());
         ps.close();
      }
      catch(Exception e)
      {
         System.out.println("List Patterns "+e);
      }

      statusInfo.setText("Pattern Memory");
   }

   void doClassify()
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      if (loader.isFinished())
      {
         LineClassifier lc = new LineClassifier();
         Log.ProcessLines(Globals.getAnalysisFile(), lc,
            Globals.getStartLine(), Globals.getEndLine());
         lc.classify();
         doShowActivation();
      } 
      else
      {
         String warning = new String("The Loader is still running.  \n"+
            "Please wait until the brain is loaded to run Classify.");
         ta.setText(warning);
         statusInfo.setText("Wait: Loader is still running.");
      }
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   // Open a Text file and display it in the work area

   void panefile(String filename)
   {
      File f = new File(filename);
      char[] data;

      try
      {
         FileReader fin = new FileReader(f);
         int filesize = (int)f.length();
         data = new char[filesize];
         fin.read(data, 0, filesize);
         String output = new String(data);
         ta.setText(output);
         statusInfo.setText("Loaded: " + filename);
      }
      catch(FileNotFoundException exc) 
      {
         statusInfo.setText("File Not Found: " + filename);
      }
      catch(IOException exc) 
      {
         statusInfo.setText("IOException: " + filename);
      }
      catch(Exception e) 
      {        
         try 
         {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            String input = "Filename :"+filename+"\n";
            ta.setText("");

            while ((line = br.readLine()) != null)
               input = input+line+"\n";

            ta.setText(input);
         }
         catch(Exception exc)
         {
            statusInfo.setText("File Not Found: " + filename);
            System.out.println("Exception "+exc);
         }
      }
      statusInfo.setText("Loaded: " + filename);
   } // panefile


   void doOpenCommand()
   {
      FileDialog file = new FileDialog(Bawt.this, "Open Text File", FileDialog.LOAD);
      file.setFile("*.txt");  // Set initial filename filter
      file.show(); // Blocks
      String curFile;

      if ((curFile = file.getFile()) != null)
      {
         String filename = file.getDirectory() + curFile;
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         panefile(filename);
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      } 
   }

   /** method doLoadRobot calls read_aiml to read the AIML source file */

   void doLoadRobot()
   {
      FileDialog file = new FileDialog(Bawt.this, "Load Robot", FileDialog.LOAD);
      file.setFile("*.aiml");  // Set initial filename filter
      file.show(); // Blocks
      String curFile;

      if ((curFile = file.getFile()) != null)
      {
         String filename = file.getDirectory() + curFile;
         char[] data;
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         Classifier.read_aiml(filename);
         statusInfo.setText("Loaded: " + filename);
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   void doMergeRobot()
   {
      FileDialog file = new FileDialog(Bawt.this,"Load Robot",FileDialog.LOAD);
      file.setFile("*.aiml");  // Set initial filename filter
      file.show(); // Blocks
      String curFile;

      if ((curFile = file.getFile()) != null)
      {
         String filename = file.getDirectory() + curFile;
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         Classifier.append_aiml_file(filename);
         statusInfo.setText("Merged: " + filename);
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   // show Options variables:

   void doPersonalityWizard()
   {
      PersonalityWizard W = new PersonalityWizard(this);
      W.show();
   }

   void doShowOptions()
   {
      Globals.toFile();

      try
      {
         StringFile sf = new StringFile();
         String t = sf.getfile(Globals.getDataDir()+"globals.txt");  
         ta.setText(t);
      } 
      catch(Exception e)
      {
         System.out.println("Options exception: "+e);
      }
      statusInfo.setText("Program Options");
   }

   // save Options from Pane:
   void doSaveOptions()
   {
      StringFile sf = new StringFile();  
      sf.delete(Globals.getDataDir()+"globals.txt");
      sf.appendLine(Globals.getDataDir()+"globals.txt", ta.getText());
      Globals.fromFile();
   }

   /** Creates HTML to display the ALICE Applet with the current settings */

   void doCreateApplet()
   {
      StringFile sf = new StringFile();
      String btext = sf.getfile(Globals.getDataDir()+"Bletemplate.aiml");
      btext = Substituter.replace("http://206.184.206.210/B",
         Globals.getCodeBase(),btext);
      sf.delete(Globals.getDataDir()+"Blet.aiml");
      sf.appendLine(Globals.getDataDir()+"Blet.aiml",btext);
      String html="<HTML><HEAD><TITLE>The A. L. I. C. E. Applet</TITLE></HEAD>";
      html += "<CENTER>\n";
      html += "<APPLET Code=\"Blet\" codebase=\""+Globals.getCodeBase()+"\"";
      html += " Width=600 Height=400>\n";
      html += "<param name=codebase value=\""+Globals.getCodeBase()+"\">\n";
      html += "<param name=applethost value=\""+Globals.getAppletHost()+"\">\n";
      html += "<param name=botfile value=\"Blet.aiml\">\n";
      html += "<param name=botmaster value=\""+Globals.getBotMaster()+"\">\n";
      html += "<param name=botname value=\""+Globals.getBotName()+"\">\n";
      html += "<param name=gender value=\""+Globals.getBotGender()+"\">\n";
      html += "<param name=location value=\""+Globals.getBotLocation()+"\">\n";
      html += "<param name=birthday value=\""+Globals.getBotBirthday()+"\">\n";
      html += "<param name=birthplace value=\""+Globals.getBotBirthplace()+"\">\n";
      html += "<param name=Sign value=\""+Globals.getSign()+"\">\n";
      html += "<param name=Wear value=\""+Globals.getWear()+"\">\n";
      html += "<param name=ForFun value=\""+Globals.getForFun()+"\">\n";
      html += "<param name=Friends value=\""+Globals.getFriends()+"\">\n";
      html += "<param name=LookLike value=\""+Globals.getLookLike()+"\">\n";
      html += "<param name=Question value=\""+Globals.getQuestion()+"\">\n";
      html += "<param name=TalkAbout value=\""+Globals.getTalkAbout()+"\">\n";
      html += "<param name=KindMusic value=\""+Globals.getKindMusic()+"\">\n";
      html += "<param name=BoyFriend value=\""+Globals.getBoyFriend()+"\">\n";
      html += "<param name=GirlFriend value=\""+Globals.getGirlFriend()+"\">\n";
      html += "<param name=FavoriteBook value=\""+Globals.getFavoriteBook()+"\">\n";
      html += "<param name=FavoriteFood value=\""+Globals.getFavoriteFood()+"\">\n";
      html += "<param name=FavoriteSong value=\""+Globals.getFavoriteSong()+"\">\n";
      html += "<param name=FavoriteBand value=\""+Globals.getFavoriteBand()+"\">\n";
      html += "<param name=FavoriteMovie value=\""+Globals.getFavoriteMovie()+"\">\n";
      html += "<param name=FavoriteColor value=\""+Globals.getFavoriteColor()+"\">\n";
      html += "</APPLET></CENTER></BODY></HTML>\n";
      sf.delete(Globals.getDataDir()+"index.html");
      sf.appendLine(Globals.getDataDir()+"index.html",html);
      panefile(Globals.getDataDir()+"index.html");
   }

   /** Responds to actions received from the menus / toolbar */

   public void actionPerformed(ActionEvent e) 
   {
      String cmd = e.getActionCommand();

      if ("LoadRobot".equals(cmd))
         doLoadRobot();
      else if ("MergeRobot".equals(cmd))
         doMergeRobot();
      else if ("SaveRobot".equals(cmd))
         doSaveRobot();
      else if ("SaveRobotAs".equals(cmd))
         doSaveRobotAs();
      else if ("NewTextFile".equals(cmd))
         doNewCommand();
      else if ("OpenTextFile".equals(cmd))
         doOpenCommand();
      else if ("SaveAsTextFile".equals(cmd))
         doSaveCommand();
      else if ("Exit".equals(cmd))
         doCloseCommand (0);
      else if ("Paste".equals(cmd))
         doPaste();
      else if ("PersonalityWizard".equals(cmd))
         doPersonalityWizard();
      else if ("KidInterface".equals(cmd))
         new Kid("A. L. I. C. E. Chat Robot--"+Globals.getBotName()).setVisible(true);
      else if ("ShowOptions".equals(cmd))
         doShowOptions();
      else if ("SaveOptions".equals(cmd))
         doSaveOptions();
      else if ("CreateApplet".equals(cmd))
         doCreateApplet();
      else if ("ToggleBeep".equals(cmd))
      {
         boolean on = !Globals.getBeep();
         Globals.setBeep(on);
         statusInfo.setText("BEEP " + (on ?  "ON" : "OFF"));
      }
      else if ("ToggleMSAgent".equals(cmd))
      {
         boolean on = !Globals.getAnimagent();
         Globals.setAnimagent(on);
         statusInfo.setText("MS Agent Script " + (on ?  "ON" : "OFF"));
      }
      else if ("Dialogues".equals(cmd))
         doDialogues();
      else if ("ClassifyButton".equals(cmd))
         doClassify();
      else if ("ShowActivation".equals(cmd))
         doShowActivation();
      else if ("ListPatterns".equals(cmd))
         doListPatterns();
      else if ("DefaultTargets".equals(cmd))
         doDefaultTargets();
      else if ("RecursiveTargets".equals(cmd))
         doRecursiveTargets();
      else if ("AutoChat".equals(cmd))
         doAutochatCommand();
      else if ("AddAIML".equals(cmd))
         doAddAIML();
      else if ("HelpHelp".equals(cmd))
         doHelpHelp();
      else if ("RandomHelpQuestion".equals(cmd))
         doRandomHelp();
      else if ("ShowAllHelpQuestions".equals(cmd))
         doIndex();
      else if ("AskHelpQuestion".equals(cmd))
         doAskRobot();     
      else if ("DontReadMe".equals(cmd))
         doHelpCommand();
      else if ("GNUPublicLicense".equals(cmd))
         doAboutCommand();
      else if ("MoreTargetsButton".equals(cmd))
         doMoreTargets();           
      else if ("QuickTargetsButton".equals(cmd))
         doQuickTargets();       
      else if ("ClearButton".equals(cmd))
         doNewCommand();
      else if ("SendButton".equals(cmd))
         doAskRobot();
   }
}
