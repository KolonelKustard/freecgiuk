package b;

import java.io.*;
import java.util.*;
 
// class StringFile is a general-purpose high-level string-file
// handling abstraction.  

class StringFile extends Object {

protected FileInputStream fis;
protected BufferedReader br;
protected FileOutputStream fos;
protected PrintStream ps;

void close() {
try {fis.close();} catch (Exception e) {System.out.print("-");}
try {fos.close();} catch (Exception e) {System.out.print("-");}
try {br.close();} catch (Exception e) {System.out.print("-");}
try {ps.close();} catch (Exception e) {System.out.print("-");}
}
   
boolean open(String fname, String mode) {
  try {
    if (mode.compareTo("r") == 0) { 
      fis = new FileInputStream(fname);
      br = new BufferedReader(new InputStreamReader(fis));
    }
    else 
    {
      fos = new FileOutputStream(fname);
      ps = new PrintStream(fos);
    }
  }
  catch (Exception e) {System.out.println("STRINGFILE: "+e);}
return true;
}

String readLine () { 
  try {
    return(br.readLine());
  }
  catch (Exception e) {System.out.println("STRINGFILE: "+e);}
  return null;
}


// method getfile() 
// 
// getfile() reads the contents of a file fname (presumably ascii)
// and returns it as a string
//
String getfile(String fname) {
    String outstring = "";
    try {
      fis = new FileInputStream(fname);
      br = new BufferedReader(new InputStreamReader(fis));
      FileInputStream tfis = new FileInputStream(fname);
      BufferedReader tbr = new BufferedReader(new InputStreamReader(fis));
      String s;
      while ((s = tbr.readLine()) != null) outstring += (s+"\n");
      tfis.close();
     } catch (Exception e) {System.out.println("STRINGFILE: "+e);}
return(outstring);
} // end of method getfile()

int countLines(String fname) {
int sum = 0;
     try {
        RandomAccessFile thefile = new RandomAccessFile(fname, "r");
	String s;
	while ((s = thefile.readLine()) != null) sum ++;
        thefile.close();
     }
     catch (Exception e) { }
return(sum);} // end of method countLines()	 

public void delete(String fname) {
  try {
    File hfile = new File(fname);
    hfile.delete();
  }
  catch (Exception e) {System.out.println("STRINGFILE: "+e);}
}

public void appendLine(String s) {
  try {
    ps.println(s);
  }
  catch (Exception e) {System.out.println("STRINGFILE: "+e);}
}   
  
public void appendLine (String fname, String s) {
     try {
        RandomAccessFile logfile = new RandomAccessFile(fname, "rw");
        int n = (int) logfile.length();
        logfile.skipBytes(n); 	
        logfile.writeBytes(s+"\n");
        logfile.close();
        }
     catch (Exception e) { System.out.println("APPENDLINE: "+e);}
} // end of method StringFile.Line()

} // end of class StringFile{System.out.println("STRINGFILE: "+e);}
