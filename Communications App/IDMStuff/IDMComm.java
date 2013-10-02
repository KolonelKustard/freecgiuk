/*
 * IDMComm.java
 *
 * Created on 06 November 2000, 18:07
 */

package IDMStuff;

import java.util.Date;
import IDMCommApp;
import b.*;
//import IDMStuff.*;

/**
 *
 * @author  Kolonel Kustard
 * @version
 */
public class IDMComm extends Thread {

    public static final String[] RINGER_LIST = {"it's a", "hurt", "good who", "coup", "aha", "a", "they", "they they", "a. a.", "a good", "good a", "good good", "ring", "tring", "spring", "to", "to to", "a a", "and", "and and", "a new", "new new"};
    public static final long TIME_FOR_CALL = 180000;

    private IDMCommApp parent;

    private IDMSpeak speak;
    private IDMSpeaker speaker;
    private ManageUsers users;
    private PhoneConnection conn;

    private Loader loader;

    private String ip;

    /** Creates new form IDMComm */
    public IDMComm(String ip, IDMCommApp parent) {
        this.ip = ip;
        this.parent = parent;
        initIDM();
        start();
    }

    // initialises all the necessary components...
    private void initIDM() {
        outPrimary("Trying to connect to Phone Manager on IP: " + ip + ":" + PhoneConnection.PORT);
        conn = new PhoneConnection(ip, this, true); // set last param to true if not testing (false runs connection in null mode)
        outPrimary("Connected");
        outPrimary("");
        outPrimary("Starting Alice loader thread...");
        Globals.fromFile();
        Classifier.fromFile();
        loader = new Loader();
        loader.setPriority(Thread.NORM_PRIORITY);
        loader.start();
        outPrimary("");
        outPrimary("Opening database...");
        users = new ManageUsers(this);
        outPrimary("Successfully collected user details");
        outPrimary("");
        outPrimary("Initialising speech engines...");
        speak = new IDMSpeak(this);
        speak.waitTillReady();
        outSecondary("Bottoms");
        speaker = new IDMSpeaker(this);
        outPrimary("Speech engines initialised");
    }

    // makes all the calls!
    public void run() {
        String phrase = null;
        Responder robot = new GUIResponder();
        String hname = "localhost";
        String response = "";
        boolean conversationRunning = true;
        Date startTime;

        outPrimary("");
        outPrimary("IDM Communicator will begin!");

        //speak.resumeDictation();
        while (users.next()) {
            speak.resetPhrase();

            // This if statement checks to see if call connected OK, then runs call procedure.
            if (conn.createCall(users.getPhoneNo())) {

                startTime = new Date();

                // Start up listening for someone.
                outPrimary("Waiting for utterance from other end of phone line");

                // This loop takes account for all possible phrases the noise a
                // telephone ringing makes.  If I don't, then the stupid thing
                // will start talking away regardless!
                do {
                    phrase = speak.getNewPhrase();
                } while (parseRinger(phrase));

                outPrimary("Other end has spoken!  Now call structure will begin.");

                conn.setAudioOutgoing();
                speaker.say("Hello " + users.getFullName() + " from Free C G I UK.  Thank you for registering with us.  We are very " +
                            "grateful for your interest.  In order for your application to be completed we will need to evaluate your " +
                            "property.  When would be a suitable time for one of our representatives to visit?");

                conn.setAudioIncoming();
                speak.getNewPhrase();

                conn.setAudioOutgoing();
                speaker.say("Thank you from Free C G I UK.  We will be in contact with you regarding your appointment for " + speak.phrase +
                            ".  Is there anything you would like to ask me regarding Free C G I UK?");

                while (conversationRunning) {
                    conn.setAudioIncoming();
                    if (speak.getNewPhrase() == null) {  // The wait for null stuff is that the getNewPhrase returns a null after a timeout period if no speech is recognised.
                        break;
                    }

                    conn.setAudioOutgoing();
                    response = Classifier.multiline_response(speak.phrase, hname, robot);
                    speaker.say(response);

                    if (new Date().getTime() > (startTime.getTime() + TIME_FOR_CALL)) {
                        outSecondary("(Main) Current time: " + new Date().getTime());
                        conversationRunning = false;
                    }
                }

                conversationRunning = true;

                conn.setAudioOutgoing();
                speaker.say("Right I'm off now.  Thanks for your time.");

                // This if statement tries to disconnect the call.  If returns false, means phone dialer could be dead.
                // Due to the flaky nature of cheap home voice modems, this does have a tendency to crash!!!
                if (!conn.endCall()) {
                    outPrimary("Could not end call correctly.  Phone dialer may have crashed.  Terminating...");
                    System.out.println("Could not end call correctly.  Phone dialer may have crashed.  Terminating...");
                    System.exit(1);
                }
            }

            // Stop dictation entirely to make sure all input is purged before next phone call.
            //speak.suspendDictation();
        }
        outPrimary("");
        outPrimary("IDM Communicator has reached the end of the list of users.  Program has finished...");
    }

    private boolean parseRinger(String phrase) {
        boolean isRinger = false;
        outSecondary("(Main) Parsing received phrase to see if it might be a ringing tone");

        // This loop compares all me words/phrases which might be a phone ringing
        // to the phrase which has just come in from the speech recogniser.
        for (int num = 0; num < RINGER_LIST.length; num++) {
            outSecondary("(Main) Comparing: " + RINGER_LIST[num] + " to " + speak.phrase);
            if (RINGER_LIST[num].equalsIgnoreCase(speak.phrase)) {
                outSecondary("(Main) This would appear to be a ringing tone and not receipt of a call.");
                isRinger = true;
                break;
            }
        }

        return isRinger;
    }

    public void outPrimary(String s) {
        parent.outPrimary(s);
    }

    public void outSecondary(String s) {
        parent.outSecondary(s);
    }

}
