package b;

/*
Ask.java: (c) 1999-2000 Dr. Richard S. Wallace
*/
import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.applet.Applet;

public class Ask extends Applet implements Runnable {
public static int index = 0;
Color fill = new Color(0x30a0ff);
Color outline = new Color(0, 100, 50);
Color background  = new Color(20, 0, 60);
Color textcolor = Color.black;
Color titlecolor = outline;
public static Random RNG = new Random();

  public void paint(Graphics g) {
     try {
        Font f = new Font("SansSerif",Font.BOLD+Font.ITALIC,14);
        Dimension dsize = this.size();
        g.setColor(fill);
        g.fillRect(0, 0, dsize.width, dsize.height);
        g.setColor(textcolor);
        g.setFont(f);
        g.drawString(ask[index%ask.length],10,20);
        }
      catch (Exception e) {
        System.out.println("Applet Ask "+e );
      } // try-catch
  } // method paint

public Thread thread = null;
    public void start() {
      thread = new Thread(this);
      thread.start();
    }
    public void stop() {  
      thread=null;
    }
    public void run() {
      try {
	while(thread==Thread.currentThread()) {
	    //           double r = Classifier.RNG.nextDouble();
	   //           index = (int)(r * ask.length);
           index = index + 1;
           Thread.sleep(1000);
           repaint();
	}
      }
      catch (Exception e) {
        System.out.println("Applet Ask "+e );
      } // try-catch
    }
    public String[] ask = {
"What time is it?",
"Do you have a boyfriend?",
"Are you real?",
"Do you believe in god?",
"Can we be friends?",
"What's your favorite movie?",
"How are you?",
"What is my name?",
"What is the meaning of life?",
"Can I get a transcript of this chat?",
"Who created you?",
"Do you like me?",
"Where do you live?",
"Are you a man or a woman?",
"How is the weather?",
"Knock knock.",
"Do you have any friends?",
"What is reductionism?",
"How smart are you?",
"What do you like to do?",
"What do you eat?",
"What is your favorite food?",
"What would you like to talk about?",
"I am tired.",
"Do you know any jokes?",
"Who is your botmaster?",
"What is AIML?",
"What does ALICE stand for?",
"Why is the sky blue?",
"Do you have emotions?",
"Are you self-aware?",
"How many people are you talking to right now?",
"Who is ELVIS?",
"Do you remember me?",
"What do you look like?",
"Want to know a secret?",
"Does anyone read these conversations?",
"What kind of music do you like?",
"What do you know about me?",
"I am a she.",
"What is your full name?",
"Are you male or female?",
"What is your purpose?",
"How old are you?",
"What is your sign?",
"What is your job?",
"My boyfriend is a jerk.",
"What can you do?",
"Tell me a joke.",
"I bet you are programmed to say that.",
"Are you happy?",
"You are very smart.",
"Will you marry me?",
"Do you speak German?",
"What is 2+2?",
"How do I download you?",
"What is artificial intelligence?",
"Tell me a story.",
"Do you dream?",
"Saggitarius.",
"I have a boyfriend.",
"You are smarter than most people I meet.",
"Tell me some more gossip.",
"What language are you writted in?",
"What is your favorite song?",
"Who is your favorite science fiction author?",
"Do you like Star Trek?",
"Can you give me some advice?",
"Where can I download you?",
"Where can I find a boyfriend?",
"It is a secret.",
"I am 16 years old.",
"I love you ALICE.",
"What color is the sky.",
"I am not sure.",
"Do you sleep?",
"What do you like to talk about?",
"Can you help me?",
"Are you pretty?",
"I am leaving.",
"Can you see me?",
"Do you remember me?",
"My name is Mike.",
"Do you speak Spanish?",
"Who is your father?",
"I am a robot too.",
"Goodnight.",
"How do I download you?",
"Leo.",
"Do you like him?",
"Do you learn?",
"Good answer.",
"Can you think.",
"Ask me something.",
"What is artificial intelligence?",
"What about your dress?",
"Tell me a story.",
"How are you today ALICE?",
"How old is Dr. Wallace?",
"Who are you talking to?",
"Do you dream?",
"What did you do today?",
"Why do you want to know?",
"What is your age?",
"What is AIML?",
"No not really.",
"Tell me some more gossip.",
"So how are you?",
"I am good.",
"What did you mean?",
"I have a problem.",
"Do you like Dr. Wallace?",
"What is the weather like?",
"Are you free?",
"I am 10 years old.",
"Just kidding.",
"What kind of music do you like?",
"That is funny.",
"You already asked me that.",
"Can I ask you a question?",
"What color or your eyes?",
"That does not make sense.",
"Is there a God?",
"I am 25 years old.",
"Do you have any pets.",
"Aries.",
"How many words do you know?",
"Why did the chicken cross the road?",
"Who is Bill Gates?",
"What are we talking about?",
"Do you like sports?",
"How do you learn?",
"You are a robot.",
"How much memory do you have?",
"Do you like Star Wars?"

    }; 

} // class Ask

