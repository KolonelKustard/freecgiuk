package b;

// a class created to interface to an MS Agent character:
// Animagent

class Animagent {

protected static boolean on = false;

private Animagent(){/* */}

public static String vbscript_html(String input) {

  if (Globals.getAnimagent() == false) return "";
  String reply = Substituter.suppress_html(input);
  reply = reply.replace('"',' ');
  reply = reply.replace('\n',' ');
  String Vbstuff = " <OBJECT ID=\"TruVoice\" width=0 height=0\n";
  Vbstuff += " CLASSID=\"CLSID:B8F2846E-CE36-11D0-AC83-00C04FD97575\"\n";
  Vbstuff += " CODEBASE=\"#VERSION=6,0,0,0\">\n";
  Vbstuff += "</OBJECT>\n";

  Vbstuff += "\n<OBJECT ID=\"AgentControl\" width=0 height=0 \n ";
  Vbstuff += " CLASSID=\"CLSID:D45FD31B-5C6E-11D1-9EC1-00C04FD7081F\" \n ";
  Vbstuff += " CODEBASE=\"#VERSION=2,0,0,0\">\n ";
  Vbstuff += "</OBJECT>\n";

  Vbstuff += "<SCRIPT language=VBScript>\n";
  Vbstuff += "Dim Robby \n";
  Vbstuff += "Sub window_OnLoad \n";
  Vbstuff += "	AgentControl.Connected = True	\n";
  Vbstuff += "	AgentControl.Characters.Load \"Robby\",";
  Vbstuff += " \""+Globals.getACFURL()+"\"\n";
  // ACFURL should be something like: 
  // http://microsoft.com/agent2/chars/robby/robby.acf or
  // C:\WINDOWS\MSAGENT\CHARS\ROBBY.ACF
  Vbstuff += "	Set Robby = AgentControl.Characters(\"Robby\")\n";
  Vbstuff += "	Robby.LanguageID = &H0409 \n";	
  Vbstuff += "	Robby.Get \"State\", \"Showing, Speaking\"\n";
  Vbstuff += "	Robby.Get \"Animation\", \"Greet, GreetReturn\"\n";
  Vbstuff += "	Robby.Show\n";
  Vbstuff += "	Robby.Get \"State\", \"Hiding\"\n";
  Vbstuff += "	Robby.Play \"Greet\"\n";
  Vbstuff += "	Robby.Speak \"";
  //  String Vbstuff2 = "\" \n Robby.Hide \n";
  String Vbstuff2 = "\" \n";
  Vbstuff2 += " End Sub  \n";
  Vbstuff2 += "</SCRIPT>\n";
return(Vbstuff+reply+Vbstuff2);
}
}

