package b;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;
import java.io.*;
import java.util.*;

// Kid.java (c) 1999-2000 Dr. Richard S. Wallace
// 
// Simple user-interface to ALICE chat robot, easy
// enough for kids to use.

class ImageCanvas extends Canvas {

  Image image;

  public ImageCanvas(String fname) {
    image = Toolkit.getDefaultToolkit().getImage(fname);
    setSize(image.getWidth(null), image.getHeight(null)); 
  } 
  public void paint(Graphics g) {
    g.drawImage(image, 0, 0, this);
  }
}


class SplashScreen extends Frame {

    public  SplashScreen () {
       super("A. L. I. C. E. Chat Robot "+Globals.getVersion());
       ImageCanvas ic = new ImageCanvas(Globals.getDataDir()+"Screen.jpg");
       setSize(640, 480);
       add(ic); 
       setVisible(true);
    }
}

// KidPanel contains the button panel,
// the Robot image, and a text string label
// at the bottom

class KidPanel extends Panel {

Label label;
String imagefile = Globals.getDataDir()+"ALICEBot.jpg";
Font labelfont = new Font("Helvetica",Font.BOLD+Font.ITALIC,16);

public KidPanel (RobotCommunicator rcx, Kid fmx) {
  setLayout(new BorderLayout());
  setBackground(RobotCommunicator.bgcolor);
  KidButtonPanel kbp = new KidButtonPanel(rcx, fmx);
  add(kbp, BorderLayout.NORTH);
  ImageCanvas ic = new ImageCanvas(imagefile);
  add(ic, BorderLayout.CENTER);
  label = new Label();
  label.setFont(RobotCommunicator.controlfont);
  label.setAlignment(Label.RIGHT);
  label.setText(new String(RobotCommunicator.prompt));
  add(label, BorderLayout.SOUTH);
}

 public void setText(String text) {
   label.setText(text);
   label.repaint();
 }
}

// KidButtonPanel contains the control buttons:

class KidButtonPanel extends Panel {

static RobotCommunicator rc;
static Kid fm;

public KidButtonPanel(RobotCommunicator rcx, Kid fmx) {
  super();
  Predicates Language = new Predicates();
  try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
  catch (Exception e) { }   
  Font buttonfont = new Font("Helvetica",Font.BOLD,12);
  Color buttoncolor = RobotCommunicator.controlcolor;
  Color filecolor = RobotCommunicator.scrollcolor;
  rc = rcx;
  fm = fmx;
  setLayout(new BorderLayout());

  Panel bPanel = new Panel();

  Button b1 = new Button(Language.getProperty("SaveTranscript","Save Transcript"));
  b1.setFont(buttonfont);
  b1.setBackground(filecolor);
  bPanel.add(b1, BorderLayout.WEST);
  b1.addActionListener (new ActionListener() {
    public void actionPerformed (ActionEvent e) {
      fm.doSaveCommand();
    }
  });
  Button b2 = new Button(Language.getProperty("ClearButton","Clear"));
  b2.setFont(buttonfont);
  b2.setBackground(filecolor);
  b2.addActionListener (new ActionListener() {
    public void actionPerformed (ActionEvent e) {
      rc.doClear();
      //      Kid.play("TOPIC.WAV");
    }
  });
  bPanel.add(b2, BorderLayout.CENTER);
  Button b3 = new Button(Language.getProperty("QuitButton","Quit"));
  b3.setFont(buttonfont);
  b3.setBackground(filecolor);
  b3.addActionListener (new ActionListener() {
    public void actionPerformed (ActionEvent e) {
      System.exit(0);
    }
  });
  bPanel.add(b3, BorderLayout.EAST);
  add(bPanel, BorderLayout.NORTH);
  Button b4 = new Button(Language.getProperty("PersonalityWizard","Personality Wizard"));
  b4.addActionListener (new ActionListener() {
    public void actionPerformed (ActionEvent e) {
      fm.doPersonalityWizard();
      //      Kid.play("Goodjob1.wav");
    }
  });
  b4.setFont(buttonfont);
  b4.setBackground(buttoncolor);
  Button b5 = new Button(Language.getProperty("SaveButton","Save"));
  b5.setFont(buttonfont);
  b5.setBackground(buttoncolor);
  b5.addActionListener (new ActionListener() {
    public void actionPerformed (ActionEvent e) {
      fm.doSavePersonality();
      //      Kid.play("Greatjob.wav");
    }
  });
  Button b6 = new Button(Language.getProperty("LoadButton","Load"));
  b6.setFont(buttonfont);
  b6.setBackground(buttoncolor);
  b6.addActionListener (new ActionListener() {
    public void actionPerformed (ActionEvent e) {
      fm.doLoadPersonality();
      //      Kid.play("Great1.wav");
    }
  });
  Panel bPanel2 = new Panel();
  bPanel2.add(b4, BorderLayout.WEST);
  bPanel2.add(b5, BorderLayout.CENTER);
  bPanel2.add(b6, BorderLayout.EAST);
  add(bPanel2, BorderLayout.CENTER);
} // constructor
} // class KidButtonPanel

// RobotCommunicator abstracts the
// combination of a TextArea output display (scroll)
// with a TextField input area (field).
// The method doAskRobot() communicates with the robot.

class RobotCommunicator extends Panel {
public static int scrollrows = 20;
public static int scrollcols = 60;
public static String prompt="You say: ";
public static String pickup="What can I call you?";
public static Color bgcolor = new Color(0xffffff);
public static Color scrollcolor = new Color(0x30a0ff);
public static Color controlcolor = new Color(0xff9090);
public static Font inputfont = new Font("Helvetica",Font.BOLD,14);
public static Font scrollfont = new Font("Helvetica",Font.BOLD,14);
public static Font controlfont = new Font("Helvetica",Font.BOLD+Font.ITALIC,14);


Responder robot;
TextArea  scroll;
TextField txtUserInput;

public RobotCommunicator(Responder robot) {
  super();
  Predicates Language = new Predicates();
  try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
  catch (Exception e) { }   
  this.robot = robot;
  prompt = Language.getProperty("YouSay", "You Say")+":";
  pickup= Language.getProperty("AskName","What can I call you?");
  setLayout(new BorderLayout());
  scroll = new TextArea(pickup+"\n",scrollrows,scrollcols);
  scroll.setBackground(scrollcolor);
  scroll.setFont(scrollfont);
  txtUserInput = new TextField("",scrollcols);
  txtUserInput.setFont(inputfont);
  txtUserInput.setBackground(controlcolor);
  txtUserInput.addKeyListener(new KeyAdapter() {
  public void keyPressed(KeyEvent e) {
    char ch = e.getKeyChar();
    if (e.getKeyChar() == '\n' || e.getKeyChar() == '\r') {
      Loader.pause = true;
      doAskRobot();
      txtUserInput.setText("");
      scroll.setCaretPosition(scroll.getText().length());
        // scroll text view to END of document
      Loader.pause = false;
    } // if
   } // keyTyped
  }); // inner class
  add(scroll, BorderLayout.CENTER);
  add(txtUserInput, BorderLayout.SOUTH);
  Classifier.set_that("localhost", pickup);
  } // init

  public void doClear() {
    scroll.setText("");
  }

  public void doAskRobot() {
    String input = txtUserInput.getText();
    txtUserInput.setText("");
    String hname = "localhost";
    scroll.append(Classifier.multiline_response(input, hname, robot));
  } // method doAskRobot();
} // RobotCommunicator

public class Kid extends Frame {

static RobotCommunicator rc;
static KidPanel kp;

public static void play(String file) {
 try {
   String command = new String("sndrec32 -play "+file);
   //   System.out.println("PLAY: "+command);
   Process child = Runtime.getRuntime().exec(command);
 } catch (Exception e) {
  // System.out.println("PLAY: "+e);
 }
}

public static void main(String args[]) {
  play("Choir1.wav");
  SplashScreen ss = new SplashScreen();

  // First set data path so we can find "global.txt"
  if(args.length>0)
    Globals.setDataDir(args[0]);
  //else, default is "." or current working directory

  Globals.fromFile();
  Classifier.fromFile(); // read property map
  Loader loader = new Loader();
  loader.start();
  Kid kid = new Kid("A. L. I. C. E. Chat Robot "+Globals.getVersion()+" -- "+Globals.getBotName());
  try {
    Thread.sleep(6000);
  } catch (Exception e) {/* */}
  kid.setVisible(true);
  ss.setVisible(false);
}

void doPersonalityWizard() {
  PersonalityWizard W = new PersonalityWizard(this);
  W.show();
} // doPersonalityWizard



void doSaveCommand () {
  FileDialog file = new FileDialog (Kid.this, "Save Transcript File", FileDialog.SAVE);
  file.setDirectory ("c:/windows/desktop");  // Set initial filename filter
  file.setFile ("*.txt");  // Set initial filename filter
  file.show(); // Blocks
  String curFile;
  if ((curFile = file.getFile()) != null) {
    String filename = file.getDirectory() + curFile;
    setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    File f = new File (filename);
    try {
      FileWriter fw = new FileWriter (f);
      fw.write (rc.scroll.getText(), 0, rc.scroll.getText().length());
      fw.close ();
    } catch (Exception e) {
    }
    setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}

void doSavePersonality () {
  FileDialog file = new FileDialog (Kid.this, "Save Robot Personality", FileDialog.SAVE);
  file.setFile ("*.txt");  // Set initial filename filter
  file.show(); // Blocks
  String curFile;
  if ((curFile = file.getFile()) != null) {
    String filename = file.getDirectory() + curFile;
    Globals.toFile(filename);
  }
}


void doLoadPersonality () {
  FileDialog file = new FileDialog (Kid.this, "Load Robot Personality", FileDialog.LOAD);
  file.setFile ("*.txt");  // Set initial filename filter
  file.show(); // Blocks
  String curFile;
  if ((curFile = file.getFile()) != null) {
    String filename = file.getDirectory() + curFile;
    Globals.fromFile(filename);
  }
}

public Kid(String title) {
  super(title);
  setLayout(new BorderLayout());
  rc = new RobotCommunicator(new RobotResponder());
  add(rc, BorderLayout.CENTER);
  kp = new KidPanel(rc, this);
  add(kp, BorderLayout.WEST);
  addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent e) {System.exit(0);}
  });
  this.pack();
}
} 







