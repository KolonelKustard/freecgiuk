//
// COPYRIGHT:
//
// Copyright (C) Chant Inc., 1996-1999.
//
// DISCLAIMER OF WARRANTIES:
//
// The following code is sample code created by Chant
// Inc. This sample code is not part of any standard Chant
// product and is provided to you solely for the purpose of
// assisting you in the development of your applications.  The
// code is provided "AS IS", without warranty of any kind.  Chant
// shall not be liable for any damages arising out of your use of
// the sample code, even if they have been advised of the
// possibility of such damages.
//

package IDMStuff;

import net.chant.speechkit.chantja.ChantSREvents;

public class ChantSRSink implements net.chant.speechkit.chantja.ChantSREvents
{
    private IDMSpeak m_pSRClient;

    public ChantSRSink(IDMSpeak aSRClient)
    {
        m_pSRClient = aSRClient;
    }

    public void audioLevel(int dwLevel)
    {
        //System.out.println("Audio Level: " + dwLevel);
    }

    public void hasPhrase(String pszPhrase, String pszVocabulary)
    {
        //System.out.println("Phrase (" + pszVocabulary + "): " + pszPhrase);
        m_pSRClient.onHasPhrase(pszPhrase, pszVocabulary);
    }

    public void hasPhraseHypothesis(String pszPhraseHypothesis, String pszVocabulary)
    {
        //System.out.println("Phrase hypothesis (" + pszVocabulary + "): " + pszPhraseHypothesis);
        m_pSRClient.onHasPhrase(pszPhraseHypothesis, pszVocabulary);
    }

    public void hasPhraseOther(String pszPhraseOther, String pszVocabulary)
    {
        //System.out.println("Phrase other (" + pszVocabulary + "): " + pszPhraseOther);
        m_pSRClient.onHasPhrase(pszPhraseOther, pszVocabulary);
    }

    public void interference(int dwInterference)
    {
        //System.out.println("Received interference: " + dwInterference);
    }

    public void settingsChanged(int dwSettings)
    {
        //System.out.println("Settings Changed: " + dwSettings);
    }

    public void utteranceBegin()
    {
        //System.out.println("Utterance Begin!");
        m_pSRClient.onUtteranceBegin();
    }

    public void utteranceEnd()
    {
        //System.out.println("Utterance End!");
        m_pSRClient.onUtteranceEnd();
    }
    public void bookMark(int dwID)
    {
    }

    public void paused()
    {
        //System.out.println("Recognition paused");
    }
    public void phraseStart()
    {
        //System.out.println("Phrase Started");
    }

    public void sound()
    {
        //System.out.println("A sound is detected!  Woo hoo!");
    }
    public void reEvaluate()
    {
        //System.out.println("Something or other was re-evaluated");
    }
    public void training(int dwTrain)
    {
        //System.out.println("Looks like training is required...: " + dwTrain);
    }
    public void unArchive()
    {
    }
    public void audioFileBegin(int dwID)
    {
    }
    public void audioFileEnd(int dwID)
    {
    }
    public void audioFileQueueEmpty()
    {
    }
}
