package b;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

// the next section of code defines classes
// for the Personality Wizard


// class QuestionPanel defines a simple
// rectangular panel containing a text question and
// blank textarea for the botmaster's reply:

class QuestionPanel extends Panel {
   String param; 
   TextField tf;   
   QuestionPanel(String q, String param) {
      super();
      setLayout(new BorderLayout());
      setBackground(RobotCommunicator.controlcolor);
      setFont(RobotCommunicator.controlfont);
      this.param = param;
      Label label = new Label(q);
      label.setFont(RobotCommunicator.controlfont);
      label.setBackground(RobotCommunicator.controlcolor);
      tf = new TextField(Globals.getProperty(param),40);
      add(label, BorderLayout.WEST);
      add(tf, BorderLayout.EAST);
    }
  public void update() {
    System.out.println("Set "+param+" to "+tf.getText());
    Globals.globals.put(param, tf.getText());
  }
}

class BotWizard extends Panel {
  QuestionPanel qp[];
  Predicates Language = new Predicates();
  BotWizard() {
    super();
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   
  }
  public void init(QuestionPanel qp[]) {
    this.qp = qp;
    setLayout(new GridLayout(6,1));
    setBackground(RobotCommunicator.controlcolor);
    setFont(RobotCommunicator.controlfont);
    for (int i = 0; i < qp.length; i++) {
      add(qp[i]);
    }
  }  
  public void update() {
    for (int i = 0; i < qp.length; i++) {
      qp[i].update();
    }
  }     
} // BotWizard

class IdentityWizard extends BotWizard {
  QuestionPanel qp[] = 
    {new QuestionPanel(Language.getProperty("BotNameQuestion", "What is your name?"),"BotName"),
     new QuestionPanel(Language.getProperty("BotLocationQuestion","Where are you located?"),"BotLocation"),
     new QuestionPanel(Language.getProperty("BotMasterQuestion","Who is your botmaster?"),"BotMaster"),
     new QuestionPanel(Language.getProperty("BotGenderQuestion","What is your gender?"),"BotGender"),
     new QuestionPanel(Language.getProperty("BotBirthdayQuestion","When is your birthday?"),"BotBirthday"),
     new QuestionPanel(Language.getProperty("BotBirthplaceQuestion","Where were you born?"),"BotBirthplace")};
  IdentityWizard() {
    super();
    init(qp);
  }
}

class FavoriteWizard extends BotWizard {
  QuestionPanel qp[] = 
    {new QuestionPanel(Language.getProperty("FavoriteMovieQuestion","What is your favorite movie?"),"FavoriteMovie"),
     new QuestionPanel(Language.getProperty("FavoriteColorQuestion","What is your favorite color?"),"FavoriteColor"),
     new QuestionPanel(Language.getProperty("FavoriteSongQuestion","What is your favorite song?"),"FavoriteSong"),
     new QuestionPanel(Language.getProperty("FavoriteBandQuestion","What is your favorite band?"),"FavoriteBand"),
     new QuestionPanel(Language.getProperty("FavoriteFoodQuestion","What is your favorite food?"),"FavoriteFood"),
     new QuestionPanel(Language.getProperty("FavoriteBookQuestion","What is your favorite book?"),"FavoriteBook")};
  FavoriteWizard() {
    super();
    init(qp);
  }
} // FavoriteWizard

class RelationWizard extends BotWizard {
  QuestionPanel qp[] = 
    {new QuestionPanel(Language.getProperty("BoyFriendQuestion","Who is your boyfriend?"),"BoyFriend"),
     new QuestionPanel(Language.getProperty("GirlFriendQuestion","Who is your girlfriend?"),"GirlFriend"),
     new QuestionPanel(Language.getProperty("LookLikeQuestion","What do you look like?"),"LookLike" ),
     new QuestionPanel(Language.getProperty("WearQuestion","What do you like to wear?"),"Wear"),
     new QuestionPanel(Language.getProperty("SignQuestion","What is your sign?"),"Sign")};
  RelationWizard() {
    super();
    init(qp);
  }
}

class MiscWizard extends BotWizard {  
  QuestionPanel qp[] = 
  {new QuestionPanel(Language.getProperty("TalkAboutQuestion","What do you like to talk about?"),"TalkAbout" ),
   new QuestionPanel(Language.getProperty("ForFunQuestion","What do you like to do for fun?"),"ForFun"),
   new QuestionPanel(Language.getProperty("QuestionQuestion","Ask me a question?"),"Question"),
   new QuestionPanel(Language.getProperty("KindMusicQuestion","What kind of music do you like?"),"KindMusic"),
   new QuestionPanel(Language.getProperty("FriendsQuestion","Who are your friends?"),"Friends")};
  MiscWizard() {
    super();
    init(qp);
  }
}

class WizardCards extends Panel {
    IdentityWizard iw = new IdentityWizard();
    FavoriteWizard fw = new FavoriteWizard();
    RelationWizard rw = new RelationWizard();
    MiscWizard mw = new MiscWizard();
    public CardLayout cards = new CardLayout();
    WizardCards() {
     super();
     setLayout(cards);
     add(iw, "Identity");
     add(fw, "Favorites");
     add(rw, "Relations");
     add(mw, "Misc");
    }
    public void update() {
      iw.update();
      fw.update();
      rw.update();
      mw.update();
    }
}

class WizardControl extends Panel {
  void doCloseDialog() {
     wp.update();
     // save the global options:
     Globals.toFile();
     // hide the Wizard Dialog:
     parent.setVisible(false);
  }
  void doNextCard() {
     wp.cards.next(wp);
  }
  void doPrevCard() {
     wp.cards.previous(wp);
  }
  protected WizardCards wp;
  protected Dialog parent;
  Predicates Language = new Predicates();
  WizardControl(Dialog parent, WizardCards wp) {
    this.parent = parent;
    this.wp = wp;
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   
    setLayout(new GridLayout(1, 3));
    setBackground(RobotCommunicator.bgcolor);
    setFont(RobotCommunicator.controlfont);
    Button next = new Button(Language.getProperty("NextButton","Next >>"));
    next.setBackground(RobotCommunicator.scrollcolor);
    next.setFont(RobotCommunicator.controlfont);
    Button prev = new Button(Language.getProperty("PrevButton","<< Prev"));
    prev.setBackground(RobotCommunicator.scrollcolor);
    prev.setFont(RobotCommunicator.controlfont);
    Button fin =  new Button(Language.getProperty("FinishedButton","Finished"));
    fin.setBackground(RobotCommunicator.scrollcolor);
    fin.setFont(RobotCommunicator.controlfont);
    fin.addActionListener (new ActionListener() {
       public void actionPerformed (ActionEvent e) {
         doCloseDialog();
       }
     });
    prev.addActionListener (new ActionListener() {
       public void actionPerformed (ActionEvent e) {
         doPrevCard();
       }
     });
    next.addActionListener (new ActionListener() {
       public void actionPerformed (ActionEvent e) {
         doNextCard();
       }
     });
    add(prev);
    add(fin);
    add(next);
  }
}

class PersonalityWizard extends Dialog {
   PersonalityWizard(Frame p) {
     super(p, "Robot Personality Configuration Wizard", true);
     setLayout(new BorderLayout());
     WizardCards wp = new WizardCards();
     WizardControl wc = new WizardControl(this, wp);
     add("Center",wp);
     add("South",wc);
     pack();
 }

}







