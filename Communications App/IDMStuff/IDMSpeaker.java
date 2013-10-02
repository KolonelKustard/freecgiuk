/*
 * IDMSpeaker.java
 *
 * Created on 06 November 2000, 11:41
 */

package IDMStuff;

import java.awt.*;
import net.chant.speechkit.chantja.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class IDMSpeaker extends Thread {
    private ChantTTS oChantTTS;
    private ChantTTSSink oChantTTSSink;
    
    private IDMComm parent;
    private Frame ownerFrame;
    
    private boolean audioInProgress;

    /** Creates new IDMSpeaker */
    public IDMSpeaker(IDMComm parent) {
        this.parent = parent;
        ownerFrame = new Frame();
        
        startChant();
        start();
    }
    
    public void run() {
    }
    
    public void say(String s) {
        parent.outPrimary("Saying: " + s);
        
        int rc = oChantTTS.textToSpeech(s, ownerFrame);
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary("(Speaker) TextToSpeech Failed rc:" + rc);
        }
        audioInProgress = true;
        
        while (audioInProgress) {
            try {
                this.sleep(1000);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
        
        parent.outPrimary("Finished speaking");
    }
    
    public void onAudioDone() {
        parent.outSecondary("(Speaker) Finished speaking");
        audioInProgress = false;
    }
    
    public void startChant() {
        
        oChantTTS = new ChantTTS();
        oChantTTSSink = new ChantTTSSink(this);
        oChantTTS.setSink(oChantTTSSink);
        
        // Opens the dialog to set up the tex-to-speech engine
        oChantTTS.selectEngine("IDMSpeaker", ownerFrame);
        
        //  Start the session with the text-to-speech engine.
        int rc = oChantTTS.startTextToSpeech("IDMSpeaker", ownerFrame);
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary("(Speaker) StartTextToSpeech Failed rc:" + rc);
        }
        
        // Register Callbacks for visual cues.
        oChantTTS.registerCallback(CallbackTypes.ChantTextToSpeechAudioDone,ownerFrame);
    }
    
    public void stopChant() {
        //The window is being closed.  Shut down the system.
        int rc = oChantTTS.stopTextToSpeech(ownerFrame);
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary("(Speaker) StopTextToSpeech Failed rc:" + rc);
        }
    }

}
