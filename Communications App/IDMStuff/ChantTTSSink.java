//
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

import net.chant.speechkit.chantjpa.ChantTTSEvents;
import java.lang.Byte;

public class ChantTTSSink implements net.chant.speechkit.chantja.ChantTTSEvents
{
    private IDMSpeaker m_pTTSClient;
    
    public ChantTTSSink(IDMSpeaker aTTSClient)
    {
        m_pTTSClient = aTTSClient;
    }
    
    public void playWaveFilesDone()
    {
    }
    
    public void textToSpeechAudioDone()
    {
        m_pTTSClient.onAudioDone();
    }
    
    public void textToSpeechDone()
    {
    }
    public void textToSpeechSettingsChanged(int dwAttribute)
    {
    }
    public void textToSpeechAudioStart()
    {
    }
    public void textToSpeechBookMark(int dwMarkNum)
    {
    }
    public void textToSpeechStarted()
    {
    }
    public void textToSpeechWordPosition(int dwOffset)
    {
    }
    public void textToSpeechVisual(short ttsMouth[])
    {
    }
}

