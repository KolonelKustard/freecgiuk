package IDMStuff;

import java.awt.*;
import java.util.Date;
import net.chant.speechkit.chantja.*;

public class IDMSpeak extends Thread {
    public static final long TIMEOUT = 30000;
    
    public String phrase = "";
    public String oldPhrase = "";
    private boolean isListening = false;
    
    private ChantSRSink oChantSRSink;
    private ChantSR oChantSR;
    private Frame ownerFrame;
    
    private IDMComm parent;

    public IDMSpeak(IDMComm parent) {
        this.ownerFrame = new Frame();
        this.parent = parent;
        
        setupChant();
        start();
    }
    
    public void run() {
        startDictation();
        while (true) {
            try {
                this.sleep(10000);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }

    /**
    * Create Chant speech recognition session object and initialize.
    */
    public void setupChant() {
        ChantInt m_varVocabTypes;

        // Create SR session Object
        oChantSR = new ChantSR();
        oChantSR.selectEngine("IDMSpeak", ownerFrame);
        
        // Create Callback Sink Object
        oChantSRSink = new ChantSRSink(this);

        // Set Callback Sink Object
        oChantSR.setSink(oChantSRSink);

        m_varVocabTypes = new ChantInt();
        //  Check to see if this engine/mode supports DictationVocabulary type.
        oChantSR.getVocabularyTypes("IDMSpeak", m_varVocabTypes);
        
        oChantSR.registerCallback(CallbackTypes.ChantHasPhrase, ownerFrame);
        oChantSR.registerCallback(CallbackTypes.ChantUtteranceBegin, ownerFrame);
        oChantSR.registerCallback(CallbackTypes.ChantUtteranceEnd, ownerFrame);
    }
    
    public void startDictation() {
        int rc;
        // Create command words array
        String[] blankArray = {};
        
        parent.outSecondary("(Speak) Starting Dictation");
        rc = oChantSR.startRecognition("IDMSpeak", "text", VocabularyTypes.DictationVocabulary, blankArray, false, ownerFrame);
        
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary( "(Speak) StartRecognition Failed rc:" + rc);
        }
        else {
            parent.outSecondary("(Speak) Speech recognition started successfully");
        }
    }

/**
 * Destroy Chant speech recognition session object and terminate.
 */
    public void stopDictation() {
        parent.outSecondary("(Speak) Stopping Dictation");
        int rc = oChantSR.stopRecognition(ownerFrame);
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary( "(Speak) StopRecognition Failed rc:" + rc);
        }
    }
    
    /**
     * Process recognized utterances.
     */
    // Process recognized phrase event.
    public void onHasPhrase(String pszPhrase, String pszVocabulary)
    {
        parent.outSecondary("(Speak) Phrase spoken: " + pszPhrase);
        
        if (isListening) {
            phrase = pszPhrase;
        }
    }
    
    public void onUtteranceBegin() {
        parent.outSecondary("(Speak) Utterance Begins.");
    }
    
    public void onUtteranceEnd() {
        parent.outSecondary("(Speak) Utterance Ends.");
    }
    
    /**
     * Resume dictation session with engine.
    */
    public void resumeDictation()
    {
        // Start dictation by enabling the vocabulary
        parent.outSecondary("(Speak) Resuming Dictation");
        int rc = oChantSR.enableVocabulary("text",ownerFrame);
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary( "(Speak) enableVocabulary Failed rc:" + rc);
        }
    }

    /**
    *  Resume dictation session with engine.
    */
    public void suspendDictation()
    {
        // Stop dictation by disabling the vocabulary
        parent.outSecondary("(Speak) Suspending Dictation");
        int rc = oChantSR.disableVocabulary("text",ownerFrame);
        if (rc != ChantErrorValues.ChantErrNone) {
            parent.outSecondary( "(Speak) disableVocabulary Failed rc:" + rc);
        }
    }
    
    public String getNewPhrase() {
        Date currentTime;
        currentTime = new Date();
        
        resumeDictation();
        parent.outSecondary("(Speak) Waiting for new phrase.");
        isListening = true;
        oldPhrase = phrase;
        
        while(phrase.equals(oldPhrase)) {
            if (new Date().getTime() > (currentTime.getTime() + TIMEOUT)) {
                return null;
            }
            
            try {
                this.sleep(1000);
            }
            catch (Exception e) {
            }
        }
        oldPhrase = phrase;
        isListening = false;
        suspendDictation();
        
        parent.outPrimary("New phrase: " + phrase);
        return (phrase);
    }
    
    public void resetPhrase() {
        phrase = "";
        oldPhrase = "";
    }
    
    public void waitTillReady() {
        int rc = -1000;
        
        parent.outSecondary("(Speak) Waiting for speech recognition engine to become available");
        
        while (rc != ChantErrorValues.ChantErrNone) {
            rc = oChantSR.enableVocabulary("text",ownerFrame);
            try {
                this.sleep(1000);
            }
            catch (Exception e) {
            }
        }
        
        parent.outSecondary("(Speak) Speech recognition available");
        
        suspendDictation();
    }
}
