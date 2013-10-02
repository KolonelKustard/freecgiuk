import java.util.*;
import com.ms.com.*;
import com.ms.ui.*;
import tapi3.*;
import confmsp.PARTICIPANT_EVENT;

public class PhoneConn implements TapiConstants, tapi3.TERMINAL_DIRECTION, tapi3.DISCONNECT_CODE, tapi3.TAPI_EVENT {
    
    public static final int STREAM_OUTGOING = 0;
    public static final int STREAM_INCOMING = 1;
    
    public static final int CONNECTED = tapi3.CALL_STATE.CS_CONNECTED;
    public static final int DISCONNECTED = tapi3.CALL_STATE.CS_DISCONNECTED;
    
    
    // Index of the selected address
    private int m_iSelAddr;

    // Main interface to the TAPI component.
    private tapi3.ITTAPI m_Tapi = null;

    private tapi3.ITStream m_Streams[];
    private tapi3.ITTerminal m_mediaTerms[];

    // Stores ITTerminal interfaces for the (static) terminals in the terminals listbox.
    private Vector m_StaticTerms = new Vector();

    // The current call or null if there isn't one.
    public tapi3.ITBasicCallControl m_Call = null;

    private ITAddress address;

    // chosen DestinationType
    int DstAddress;
    
    public int status;
    
    private int currStream;
    
    public PhoneConn(String phoneNum) {
        
        InitializeTAPI();
        MakeTheCall(phoneNum);
    }


    /////////////////////////////////////////////////////////////////////
    // InitializeTAPI
    //
    // initializes tapi
    /////////////////////////////////////////////////////////////////////
    public void InitializeTAPI() {
        System.out.println("Initializing TAPI 3.0...");

        try {
            m_Tapi = new TAPI();
            m_Tapi.Initialize();
            m_Tapi.putEventFilter(
            TE_TAPIOBJECT |
            TE_ADDRESS |
            TE_CALLNOTIFICATION |
            TE_CALLSTATE |
            TE_CALLMEDIA |
            TE_CALLHUB |
            TE_CALLINFOCHANGE |
            TE_PRIVATE |
            TE_REQUEST |
            TE_AGENT |
            TE_AGENTSESSION |
            TE_QOSEVENT |
            TE_AGENTHANDLER |
            TE_ACDGROUP |
            TE_QUEUE |
            TE_DIGITEVENT |
            TE_GENERATEEVENT);

            System.out.println("Initialization done...");
        }

        catch (ComException e) {
            System.out.println("Fatal Error: Could not initialize TAPI 3.0");
            m_Tapi.Shutdown();
            m_Tapi = null;
            System.out.println("Could not initialize TAPI 3.0");
        }

        // get modem
        System.out.println("Getting Modem...");

        getModem();

        System.out.println("Ready and willing baby!");
    }

    private void getModem() {
        Variant var;
        ITCollection itc;
        String addrname;

        try {
            // get the collection interface for addresses
            var = m_Tapi.getAddresses();
            itc = (ITCollection) var.toDispatch();

            var = itc.getItem(1);
            address = (ITAddress) var.toObject();

            // get the address name
            addrname=address.getAddressName();

            System.out.println(addrname);
        }
        catch (ComException e) {
            e.printStackTrace();
        }
    }

    private boolean enumerateStreams()
    {

        ITStreamControl pITStreamControl;
        tapi3.ITStream pITStream;
        ITSubStreamControl pSubStream;
        ITCollection itcStream;
        Variant var;

        pITStreamControl = (ITStreamControl)m_Call;

        try
        {
            var = pITStreamControl.getStreams();
            itcStream=(ITCollection) var.toDispatch();
            m_Streams= new tapi3.ITStream[itcStream.getCount()];
            for(int n=1;n<=itcStream.getCount();n++)
            {
                var=itcStream.getItem(n);
                pITStream=(tapi3.ITStream)var.toDispatch();
                m_Streams[n-1]=pITStream;

                String strDir = "";
                if(m_Streams[n-1].getDirection()==TERMINAL_DIRECTION.TD_RENDER)
                    strDir+=" Render";
                else
                    strDir+=" Capture";
                System.out.println(m_Streams[n-1].getName()+strDir);
            }
            return true;
        }
        catch (ComException e)
        {
            e.printStackTrace();
            return false;
        }

    }

    private void enumerateStaticTerms()
    {
        ITTerminalSupport termSupp;
        Variant var;
        ITCollection itc;
        ITTerminal terminal;
        String termClass;
        int n;

        try {
            // get the terminal support interface on the address
            termSupp = (ITTerminalSupport) address;

            // get the collection interface for static terminals
            var = termSupp.getStaticTerminals();

            itc = (ITCollection) var.toDispatch();

            for (n=1; n <= itc.getCount(); n++)
            {
                // get the next static terminal
                var = itc.getItem(n);
                terminal = (ITTerminal) var.toObject();
                String strDir=new String("");

                if(terminal.getDirection()==TERMINAL_DIRECTION.TD_RENDER)
                    strDir+=" Render";
                else
                    strDir+=" Capture";

                // display the terminal name and store its interface
                System.out.println(terminal.getName()+strDir);
                m_StaticTerms.addElement(terminal);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createMediaTerminals() {
        ITTerminalSupport termSupport;

        m_mediaTerms = new ITTerminal[m_StaticTerms.size()];

        try
        {
            for (int i=0; i < m_StaticTerms.size(); i++)
                m_mediaTerms[i] = (ITTerminal) m_StaticTerms.elementAt(i);

            System.out.println ("Number of media terminals: " + m_mediaTerms.length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void selectTerminals() {

        // This next bit is specific to my machines hardware setup.  To run
        // on all TAPI3 machines would just need some for loops as per the
        // same method in the jt3call.java example from Microsoft.

        // So basically, my custom stuff here, just matches the output terminal
        // of my soundcard to the input audio stream of my modem. (and vice versa).
        // Important to note is that my modem only supports half-duplex, so you
        // have to specifically change the used terminal/stream combo.

        System.out.println("Capture: " + TERMINAL_DIRECTION.TD_CAPTURE);
        System.out.println("Render: " + TERMINAL_DIRECTION.TD_RENDER);

        String strDir = "";
        if(m_mediaTerms[0].getDirection()==TERMINAL_DIRECTION.TD_RENDER) strDir=" Render "; else strDir=" Capture ";
        System.out.println("Terminal 0: " + m_mediaTerms[0].getName() + strDir + m_mediaTerms[0].getDirection());
        if(m_mediaTerms[1].getDirection()==TERMINAL_DIRECTION.TD_RENDER) strDir=" Render "; else strDir=" Capture ";
        System.out.println("Terminal 1: " + m_mediaTerms[1].getName() + strDir + m_mediaTerms[1].getDirection());

        if(m_Streams[0].getDirection()==TERMINAL_DIRECTION.TD_RENDER) strDir=" Render "; else strDir=" Capture ";
        System.out.println("Stream 0: " + m_Streams[0].getName() + strDir + m_Streams[0].getDirection());
        if(m_Streams[1].getDirection()==TERMINAL_DIRECTION.TD_RENDER) strDir=" Render "; else strDir=" Capture ";
        System.out.println("Stream 1: " + m_Streams[1].getName() + strDir + m_Streams[1].getDirection());

        m_Streams[0].SelectTerminal(m_mediaTerms[0]);
        m_Streams[0].StopStream();
        
        m_Streams[1].SelectTerminal(m_mediaTerms[1]);
        m_Streams[1].StopStream();
        
        System.out.println("Streams set up");
    }
    
    public void setDirectionOutgoing() {
        try {
            m_Streams[1].UnselectTerminal(m_mediaTerms[1]);
        }
        catch (Exception e) {
        }
        m_Streams[0].SelectTerminal(m_mediaTerms[0]);
    }
    
    public void setDirectionIncoming() {
        try {
            m_Streams[0].UnselectTerminal(m_mediaTerms[0]);
        }
        catch (Exception e) {
        }
        m_Streams[1].SelectTerminal(m_mediaTerms[1]);
    }

    private boolean MakeTheCall(String destAddr) {

        ITCallInfo callInfo;
        int CallState;

        try {

            System.out.println();
            System.out.println("Enumerating Static Terminals...");
            enumerateStaticTerms();

            System.out.println();
            System.out.println("Creating Terminals");
            createMediaTerminals();

            System.out.println();
            System.out.println("Setting up call...");
            m_Call = address.CreateCall(destAddr, LINEADDRESSTYPE_PHONENUMBER, TAPIMEDIATYPE_AUDIO);

            System.out.println();
            System.out.println("Enumerating Streams...");
            enumerateStreams();

            //System.out.println();
            //System.out.println("Setting up the terminals");
            //selectTerminals();
            
            System.out.println();
            System.out.println("Setting direction incoming...");
            setDirectionIncoming();

            System.out.println();
            System.out.println("Connecting call...");
            m_Call.Connect(true);

            do {
                callInfo=(ITCallInfo)m_Call;
                CallState=callInfo.getCallState();

                if(CallState==tapi3.CALL_STATE.CS_DISCONNECTED) {
                    System.out.println("The Call Failed to connect");
                    return false;
                }
            } while(CallState!=tapi3.CALL_STATE.CS_CONNECTED);
            
            System.out.println("Call Connected");

            boolean bFlag=true;
            int iCnt=0;

            //while(m_Call != null) System.out.println(CallState);

            return true;
        }

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getStatus() {
        ITCallInfo callInfo = (ITCallInfo)m_Call;
        return callInfo.getCallState();
    }
    
    public void disconnect() {
        try {
            m_Streams[1].UnselectTerminal(m_mediaTerms[1]);
        }
        catch (Exception e) {
        }
        try {
            m_Streams[0].UnselectTerminal(m_mediaTerms[0]);
        }
        catch (Exception e) {
        }
        m_Tapi.Shutdown();
    }
}

class CallNot extends Object
implements ITTAPIEventNotification,	// notification interface
TAPI_EVENT,				// CET_xxx constants
tapi3.CALL_STATE,				// CS_xxx constants
CALL_PRIVILEGE,			// CP_xxx constants
TapiConstants,				// misc. constants
TERMINAL_DIRECTION,
PARTICIPANT_EVENT				 
{
    // Reference to main program object.
    private PhoneConn m_App = null;
    
    
    /////////////////////////////////////////////////////////////////////
    // Constructor
    /////////////////////////////////////////////////////////////////////
    public CallNot(PhoneConn app)
    {
        m_App = app;
    }
    
    /////////////////////////////////////////////////////////////////////
    // Event 
    //
    // The only method in the ITCallNotification interface.  This gets
    // called by TAPI 3.0 when there is a call event to report.
    //
    // It is important that you use the /X:m- switch when importing the
    // TAPI 3.0 typelib to Java using JActiveX.  This will turn off
    // auto-marshalling of variables passed from CallNot to JT3Rec.  If
    // you do not, the program will freeze whenever JT3Rec tries to use
    // a variable given to it by CallNot.
    /////////////////////////////////////////////////////////////////////
    public void Event(int eventType, Object event) {
        ITCallNotificationEvent pCallNotificationEvent;
        String Caption;
        int CallEventType;
        Variant avar;
        ITCollection itc;
        ITCallInfo pCallInfo;
        ITTerminal aterm;
        ITTerminal pITTerminal;
        int astr;
        int vis;
        confmsp.ITSubStream pSubStream;
        confmsp.ITParticipantSubStreamControl pParticipantControl;
        try {
            switch(eventType) {
                case TE_CALLSTATE:
                    // CET_CALLSTATEEVENT is a call state event.  
                    // event is an ITCallStateEvent object.
                    int cs;
                    ITCallStateEvent callStateEvent = (ITCallStateEvent) event;
                    cs = callStateEvent.getState();
                    switch (cs) {
                        case CS_DISCONNECTED:
                            System.out.println("EVENT5 call state disconnect");
                            //m_App.CleanUp();
                            m_App.InitializeTAPI();
                            
                            break;
                        case CS_CONNECTED:
                            System.out.println("EVENT7 call state connect");
                            // make the in dynamic videos visible
                            break;   
                    }			// of cs    
                    break;
                    
                case TE_CALLMEDIA:
                    int evt;
                    ITCallMediaEvent callMediaEvent = (ITCallMediaEvent) event;
                    evt = callMediaEvent.getEvent();
                    switch (evt) {
                        case CALL_MEDIA_EVENT.CME_STREAM_ACTIVE:
                            tapi3.ITStream Strm=callMediaEvent.getStream();
                            break;
                    }
                    break;
                    
                case TE_PRIVATE:
                    break;
            } //main switch
        }
        
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("keep going");
        }
    }
	
}   // of class      