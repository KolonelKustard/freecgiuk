package b;

import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;

// the base class of concern here is a StringVector
// Basically this is just an array or Vector of Strings
// 

class StringVector {

  private String[] Elements = null;
  private int asize = 0; // the physical size of the list in memory
  private int size = 0; // the logical size of the array 
  public int size() { return size; }

  // because it is a Set, the StringSet has
  // a unique inverse map to indeces,
  // but for a Vector we just find the first match:

  public int indexOf(String s) {
    for (int i = 0; i < size(); i++) {
       if (s.compareTo((String)elementAt(i))==0) return i;
    }
    return -1;  }
  //
  // the boolean member contains(s)
  public final boolean contains (String s) {
    if (indexOf(s) < 0) return false;
    else return true;
  }
  public final void setElementAt(String s, int index) {
    Elements[index] = s;
  }

  public final String elementAt(int index) {
    if (index < size) return(Elements[index]);
    else return null;
  }
  
  public final void addElement(String s) {
    // 1. first, take care of array memory management:
    if (size >= asize) { // 0 >= 0 first time
                       // 1 >= 1
                       // 2 >= 2
                       // 4 >= 4
    asize = (asize == 0) ? 1 : (2*asize);
    //    System.out.println("Array size "+asize);
    String[] nElements = new String[asize];
    if (size > 0) {
      System.arraycopy(Elements, 0, nElements, 0, size);
    }
    Elements = nElements;
    }
  // if (size >= asize)
  // now size < asize
  // 2. Add the new string s to the end of the array:
  Elements[size] = s;
  size++;
  } // method add
} // class StringVector

// a String Set is a Vector containing only one instance
// of each String:
//
// extending Vector results in a slightly faster version
//
class StringSet extends Vector { // or extends StringVector
  // override the root class add
  // to add each string only once:
  public void add(String s) {
    if (!contains(s)) {
	super.addElement(s);
    }
    else {
	//      System.out.println("Duplicate: "+s+" discareded.");
    }
  } // add
} // class StringSet

// A String Sorter is a string Set refined
// so that the Strings are sorted :
//
// this version by Yiannis Paschalidis
// aliasx@geocities.com
//
class StringSorter extends StringSet
{  
   //Since the data is sorted, we can use a 
   //binary search to save time...

   public int indexOf(String s)
   {
      int top=size()-1,bottom=0,mid,diff;
      
      while (top>=bottom)
      {
         mid = (top+bottom)/2;
         diff=((String)(elementAt(mid))).compareTo(s);
         
         if (diff>0)
            top=mid-1;
         else if (diff<0)
            bottom=mid+1;
         else
            return mid; //found it!
      }
  
      //Haven't found it    
      return -1;
   }
   
   public boolean contains(String s)
   {
      return indexOf(s)>=0;
   }

   public void add(String s)
   {
      //trivial case
      
      if (isEmpty())
      {
         super.add(s);
         return;
      }
      
      //otherwise, perform a binary search to see where it should go
      
      int top=size()-1,bottom=0,mid=top/2,diff=0;
      
      while (top>=bottom)
      {
         mid = (top+bottom)/2;
         diff=((String)(elementAt(mid))).compareTo(s);
         
         if (diff>0)
            top=mid-1;
         else if (diff<0)
            bottom=mid+1;
         else
            return; //equal, so no need to insert
      }
      
      //Ok, we've found the right spot. Insert before or after?
      
      mid = (top+bottom)/2;
      diff=((String)(elementAt(mid))).compareTo(s);
      
      if (diff>0)
         insertElementAt(s,mid);
      else
         insertElementAt(s,mid+1);
   }  
} // class StringSorter


// A String Histogrammer is a String set with
// a map from strings to integers

class StringHistogrammer extends StringSet {
  // "total" refers to the total
  // number of items counted by the histogrammer.
  // total should equal the sum of Countmap
  // "size" refers to the number of elements in the set
  protected int total=0;
  protected Hashtable Countmap=new Hashtable();

  public int getTotal() {
    return total;
  }
  public int Count(int i) {
  if (i < size()) 
    return(((Integer)Countmap.get((String)elementAt(i))).intValue());
  else return 0;
  }

  public void setCount(String s, int c) {
     Countmap.put(s, new Integer(c));
  }

  // override the parent class add
  // to increment Countmap:

  public void add(String s) {  
    Integer D; int d;
    if (!contains(s)) {
      super.add(s);
      d = 1;
    }
    else {
      D = (Integer)Countmap.get(s);
      d = D.intValue() + 1;
    }
    D = new Integer(d);
    Countmap.put(s, D);
    total++;
    // assert total = sum Countmap()
  } // method add
}

         
// A String Ranker is just like a Histogrammer 
// except that it orders the string by rank 
final class StringRanker extends StringHistogrammer {
  // override the parent class add
  // to insertion sort
  // the histogram buckets by rank:
  public void add(String s) {
    super.add(s);
    if (size() > 1) { // skip size=1 for efficiency
      int index = super.indexOf(s);
      for (int i = index; i > 0; i--) {
        if (Count(i) >= Count(i-1)) {
           String temp = (String)elementAt(i);
           setElementAt((String)elementAt(i-1), i);
           setElementAt(temp, i-1);
         }  // if
      } // for
    } // size > 1
  } // method add
} // class StringRanker

// The Parser class interprets the right-hand side template expressions.
// 
// The primary method of the Parser class is the pfkh()
// "Program Formerly Known as Hello" that controls the
// parsing and interpetation process.

class Parser extends Object {
    // array[] and length are obsolted by new-style XML
    // and the "new" pfkh()
  protected String array[];
  protected int length;
  protected int depth;

  public Parser()  {
    array = null;
    length = 0;
    depth = 0;
  }

    // append used only in old_pfkh()
  public String append(int start, int end) {
    String out = "";
   try {
    for (int i = start; i <= end; i++) {
        String word = array[i];
        word = word.trim();
	if (word.length() > 0) out = out+word+" ";
    }
   } catch (Exception e) {System.out.println("APPEND: "+e);}
    out = out.trim();
    return out;
  }

  // append used only in old_pfkh()
  public String append() {
    String out = "";
    try {
    for (int i = 0; i < length; i++) {
        String word = array[i];
        word = word.trim();
	if (word.length() > 0) out = out+word+" ";
    }
    } catch (Exception e) {System.out.println("APPEND: "+e);}
    out = out.trim();
    return out;
  }

    // print() used only in old_pfkh()
  public void print() {
    String out = append();
    System.out.println(out);
  }

  // Parser.process_tag uses the TagProcessor interface:

public String process_tag(String ip, String star, 
  String stag, String etag, String response, TagProcessor tp) {
  while (response.indexOf(stag) >= 0) {
    int start = response.indexOf(stag);
    int end = indexOfEndTag(stag, etag, new String(response));
    if (end <= start) response = Substituter.replace(stag," ",response);
    else {
      String head = response.substring(0, start);
      String mid = response.substring(start+stag.length(), end);
      String tail = 
        (end+etag.length() < response.length()) ? response.substring(end+etag.length()) : "";
      mid = tp.process_aiml(ip, mid, star, this);
      tail = pfkh(ip, new String(tail), star);
      response = head + mid + tail;
    }
  }
  return response;
}


    // this is either a bug or a feature that restricts the
    // XML grammar somewhat.  The assumption of 
    // indexOfEndTag is that the "etag" matching "stag" is
    // the NEXT etag in the input.  This prohibits recursive
    // constructs like <srai><srai>...</srai></srai>, but the
    // semantics of those expressions are unclear anyway.
    // The language DOES permit <srai>X</srai></srai>Y</srai>.

public int indexOfEndTag(String stag, String etag, String input) {
   int index = input.indexOf(etag);
   return (index);
}   

//
// the method pfkh: the Program Formerly Known as Hello
// This method interprets the AIML template and constucts the reply.
// Pfkh is normally called by the Classifier method respond().
// If the Template contains an sr() or srai() function, then the
// pfkh executes a recursive call back to the Classifier method respond().
// Note that respond may create another instance of this class Parser.
// These recursions create the possibility of infinite loops, so the
// Botmaster should take care not to allow loops in AIML.

    // This pfkh is modified for "new AIML" (i.e. XML tags)
    // 1. replace +~person()+ with <person/>
    // 2. +~getname()+ with <getname/>
    // 3. +~set_it()+...+~endai()+ with <set_it>...</set_it>
    // etc.

public String pfkh(String ip, String response, String star) {
  TagProcessor tp;
  String templ = response;
  int n;

  // Process Atomic tags first:

  while ((n = templ.indexOf("<load filename=\"")) >= 0) {
    templ = templ.substring(n+"<load filename=\"".length(), templ.length());
    int m = templ.indexOf("\"/>");
    if (m >= 0) { 
      templ = templ.substring(m+3, templ.length());
    }
  }
  response = templ;

  while (response.indexOf("<setname/>") >= 0) {
    String sx = Substituter.formal(star);
    Classifier.setname(ip, sx);
    response = Substituter.replace("<setname/>",sx,response);
  }

  while (response.indexOf("<set_animagent/>") >= 0) {
 System.out.println("Activate animated agent script");
 String sx = "I am now attempting to activate the MS Agent interface.\n"+
"To see the animation, you must browse with MS IE 4.0 or higher.\n"+
"You must have your browser security set to 'low' to run scripts.\n"+
"The browser will now download the MS Agent application.\n"+
"This may take several minutes.";

    response = Substituter.replace("<set_animagent/>",sx,response);
    Classifier.set_animagent(ip);
  } 

  while (response.indexOf("<set_female/>") >= 0) {
    String sx = "she";
    response = Substituter.replace("<set_female/>",sx,response);
    Classifier.set_gender(ip, "she");
  } 

  while (response.indexOf("<set_male/>") >= 0) {
    String sx = "he";
    response = Substituter.replace("<set_male/>",sx,response);
    Classifier.set_gender(ip, "he");
  } 

  while (response.indexOf("<get_gender/>") >= 0) {
    String sx = Classifier.get_gender(ip);
    response = Substituter.replace("<get_gender/>",sx,response);
  } 

  while (response.indexOf("<person/>") >= 0) {
    String sx = Substituter.pretty(Substituter.person(star));
    response = Substituter.replace("<person/>",sx,response);
  } 

  while (response.indexOf("<person2/>") >= 0) {
    String sx = Substituter.pretty(Substituter.person2(star));
    response = Substituter.replace("<person2/>",sx,response);
  } 

  while (response.indexOf("<personf/>") >= 0) {
    String sx = Substituter.pretty(Substituter.person(star));
    sx = Substituter.replace(" ","%20",sx);
    response = Substituter.replace("<personf/>",sx,response);
  } 

  while (response.indexOf("<getname/>") >= 0) {
    String sx = Classifier.getname(ip);
    response = Substituter.replace("<getname/>",sx,response);
  } 

  while (response.indexOf("<gettopic/>") >= 0) {
    String sx = Classifier.gettopic(ip);
    response = Substituter.replace("<gettopic/>",sx,response);
  } 

  while (response.indexOf("<get_location/>") >= 0) {
    String sx = Classifier.get_location(ip);
    response = Substituter.replace("<get_location/>",sx,response);
  } 

  while (response.indexOf("<get_age/>") >= 0) {
    String sx = Classifier.get_age(ip);
    response = Substituter.replace("<get_age/>",sx,response);
  } 

  while (response.indexOf("<get_dialogue/>") >= 0) {
    Access.analyze(ip);
    String dialogue = Access.ipDialogue(ip);
    dialogue = Substituter.replace("\n","<br>",dialogue);
    response = Substituter.replace("<get_dialogue/>",dialogue,response);
  }

  while (response.indexOf("<name/>") >= 0) {
    String sx = Globals.getBotName();
    response = Substituter.replace("<name/>",sx,response);
  } 

  while (response.indexOf("<botmaster/>") >= 0) {
    String sx = Globals.getBotMaster();
    response = Substituter.replace("<botmaster/>",sx,response);
  } 

  while (response.indexOf("<birthday/>") >= 0) {
    String sx = Globals.getBotBirthday();
    response = Substituter.replace("<birthday/>",sx,response);
  } 

  while (response.indexOf("<birthplace/>") >= 0) {
    String sx = Globals.getBotBirthplace();
    response = Substituter.replace("<birthplace/>",sx,response);
  } 

  while (response.indexOf("<gender/>") >= 0) {
    String sx = Globals.getBotGender();
    response = Substituter.replace("<gender/>",sx,response);
  } 

  while (response.indexOf("<boyfriend/>") >= 0) {
    String sx = Globals.getBoyFriend();
    response = Substituter.replace("<boyfriend/>",sx,response);
  } 

  while (response.indexOf("<girlfriend/>") >= 0) {
    String sx = Globals.getGirlFriend();
    response = Substituter.replace("<girlfriend/>",sx,response);
  } 

  while (response.indexOf("<favorite_food/>") >= 0) {
    String sx = Globals.getFavoriteFood();
    response = Substituter.replace("<favorite_food/>",sx,response);
  } 

  while (response.indexOf("<favorite_book/>") >= 0) {
    String sx = Globals.getFavoriteBook();
    response = Substituter.replace("<favorite_book/>",sx,response);
  } 

  while (response.indexOf("<favorite_band/>") >= 0) {
    String sx = Globals.getFavoriteBand();
    response = Substituter.replace("<favorite_band/>",sx,response);
  } 

  while (response.indexOf("<favorite_song/>") >= 0) {
    String sx = Globals.getFavoriteSong();
    response = Substituter.replace("<favorite_song/>",sx,response);
  } 

  while (response.indexOf("<favorite_color/>") >= 0) {
    String sx = Globals.getFavoriteColor();
    response = Substituter.replace("<favorite_color/>",sx,response);
  } 

  while (response.indexOf("<favorite_movie/>") >= 0) {
    String sx = Globals.getFavoriteMovie();
    response = Substituter.replace("<favorite_movie/>",sx,response);
  } 

  while (response.indexOf("<look_like/>") >= 0) {
    String sx = Globals.getLookLike();
    response = Substituter.replace("<look_like/>",sx,response);
  } 

  while (response.indexOf("<wear/>") >= 0) {
    String sx = Globals.getWear();
    response = Substituter.replace("<wear/>",sx,response);
  } 

  while (response.indexOf("<talk_about/>") >= 0) {
    String sx = Globals.getTalkAbout();
    response = Substituter.replace("<talk_about/>",sx,response);
  } 

  while (response.indexOf("<for_fun/>") >= 0) {
    String sx = Globals.getForFun();
    response = Substituter.replace("<for_fun/>",sx,response);
  } 

  while (response.indexOf("<question/>") >= 0) {
    String sx = Globals.getQuestion();
    response = Substituter.replace("<question/>",sx,response);
  } 

  while (response.indexOf("<kind_music/>") >= 0) {
    String sx = Globals.getKindMusic();
    response = Substituter.replace("<kind_music/>",sx,response);
  } 

  while (response.indexOf("<sign/>") >= 0) {
    String sx = Globals.getSign();
    response = Substituter.replace("<sign/>",sx,response);
  } 

  while (response.indexOf("<friends/>") >= 0) {
    String sx = Globals.getFriends();
    response = Substituter.replace("<friends/>",sx,response);
  } 

  while (response.indexOf("<location/>") >= 0) {
    String sx = Globals.getBotLocation();
    response = Substituter.replace("<location/>",sx,response);
  } 

  while (response.indexOf("<star/>") >= 0) {
    String sx = Substituter.pretty(star);
    response = Substituter.replace("<star/>",sx,response);
  }

  while (response.indexOf("<that/>") >= 0) {
    String sx = Classifier.get_that(ip);
    response = Substituter.replace("<that/>",sx,response);
  }

  while (response.indexOf("<justthat/>") >= 0) {
    String sx = Classifier.get_justthat(ip);
    response = Substituter.replace("<justthat/>",sx,response);
  }

  while (response.indexOf("<beforethat/>") >= 0) {
    String sx = Classifier.get_beforethat(ip);
    response = Substituter.replace("<beforethat/>",sx,response);
  }

  while (response.indexOf("<justbeforethat/>") >= 0) {
    String sx = Classifier.get_justbeforethat(ip);
    response = Substituter.replace("<justbeforethat/>",sx,response);
  }

  while (response.indexOf("<get_it/>") >= 0) {
    String sx = Classifier.get_it(ip);
    response = Substituter.replace("<get_it/>",sx,response);
  }

  while (response.indexOf("<get_ip/>") >= 0) {
    String sx = ip;
    response = Substituter.replace("<get_ip/>",sx,response);
  }

  while (response.indexOf("<get_he/>") >= 0) {
    String sx = Classifier.get_he(ip);
    response = Substituter.replace("<get_he/>",sx,response);
  }

  while (response.indexOf("<get_she/>") >= 0) {
    String sx = Classifier.get_she(ip);
    response = Substituter.replace("<get_she/>",sx,response);
  }

  while (response.indexOf("<getsize/>") >= 0) {
    String sx = Classifier.size();
    response = Substituter.replace("<getsize/>",sx,response);
  }

  while (response.indexOf("<getversion/>") >= 0) {
    String sx = Globals.getVersion();
    response = Substituter.replace("<getversion/>",sx,response);
  }

  // now process the custom tags
  Enumeration enum = Classifier.predicatemap.propertyNames();
  while (enum.hasMoreElements()) {
    String pname = (String)(enum.nextElement());
    String tag = "<get_"+pname+"/>"; 
    while (response.indexOf(tag) >= 0) {
      String sx = Classifier.get_property(pname, ip);
      response = Substituter.replace(tag,sx,response);
    }
  }

  // Next process all the paired tags: <tag>..</tag>

  String stag = "<system>"; String etag = "</system>";
  tp = new SystemProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<random>"; etag = "</random>";
  tp = new RandomProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<set_location>"; etag = "</set_location>";
  tp = new LocationProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<set_age>"; etag = "</set_age>";
  tp = new AgeProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<settopic>"; etag = "</settopic>";
  tp = new TopicProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<setname>"; etag = "</setname>";
  tp = new NameProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<set_it>"; etag = "</set_it>";
  tp = new ItProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<set_he>"; etag = "</set_he>";
  tp = new HeProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<set_she>"; etag = "</set_she>";
  tp = new SheProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<set_they>"; etag = "</set_they>";
  tp = new TheyProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<gossip>"; etag = "</gossip>";
  tp = new GossipProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  stag = "<think>"; etag = "</think>";
  tp = new ThinkProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);

  enum = Classifier.predicatemap.propertyNames();
  while (enum.hasMoreElements()) {
    String pname = (String)(enum.nextElement());
    stag = "<set_"+pname+">"; etag = "</set_"+pname+">";
    //    System.out.println("stag ="+ stag);
    tp = new CustomTagProcessor(pname);
    response = process_tag(ip, star, stag, etag, response, tp);
  }

  // Process recursive tags last:

  response = Substituter.replace("<sr/>","<srai><star/></srai>",response);
  stag = "<srai>"; etag = "</srai>";
  tp = new SraiProcessor();
  response = process_tag(ip, star, stag, etag, response, tp);
  if (response.indexOf("+~") >= 0)
     response = old_pfkh(ip, response, star);
  return(response);
}

    // The "old Program Formerly Known as Hello is
    // an embarassingly simple 'parser' for a limited subset
    // of AIML expressions using 'old-style' notation.
    // Please avoid the use of "+~" functions in new AIML bots.
    // This method won't be here forever.

  public String old_pfkh(String ip, String template, String star) {
    try {
    StringTokenizer st = new StringTokenizer(template, "+");
    StringFile sf = new StringFile();
    length = st.countTokens();
    if (length > 0) {
      array = new String[length];
      String state = "START";
      int aiml_index = 0;
      for (int i = 0 ; i < length; i++) {
         String token = st.nextToken();
         token = token.trim();
         if (token.startsWith("~")) {
	    token = token.substring(1);
//   	    System.out.println("Function: '"+token+"' IP: "+ip);
            if (token.startsWith("*")) {
                array[i] = Substituter.pretty(star);
            }
            else if (token.startsWith("endai()")) {
		array[i] = "";
	        if (state.compareTo("NAME_STATE") == 0) {
   	             String apps = append(aiml_index, i);
                     Classifier.setname(ip, Substituter.formal(apps));
		     array[i] = Classifier.getname(ip);
                }
	        if (state.compareTo("IT_STATE") == 0) {
   	             String apps = append(aiml_index, i);
                     Classifier.set_it(ip, Substituter.pretty(apps));
		     array[i] = "it";
                }
	        if (state.compareTo("THEY_STATE") == 0) {
   	             String apps = append(aiml_index, i);
                     Classifier.set_they(ip, Substituter.pretty(apps));
		     array[i] = "they";
                }
	        if (state.compareTo("HE_STATE") == 0) {
   	             String apps = append(aiml_index, i);
                     Classifier.set_he(ip, Substituter.pretty(apps));
		     array[i] = "he";
                }
	        if (state.compareTo("SHE_STATE") == 0) {
   	             String apps = append(aiml_index, i);
                     Classifier.set_she(ip, Substituter.pretty(apps));
		     array[i] = "she";
                }
	        if (state.compareTo("GETFILE_STATE") == 0) {
		     array[i] = "<PRE>"+sf.getfile(array[i-1])+"</PRE>";
                }
	        if (state.compareTo("RANDOM_STATE") == 0) {
	             double r = Classifier.RNG.nextDouble();
	             int random_amt = (int)((double)(i - aiml_index - 1)*r);
		     array[i] = array[aiml_index+random_amt+1];
                }
	        if (state.compareTo("GOSSIP_STATE") == 0) {
		     String gossip = append(aiml_index, i);
            if (Classifier.getname(ip).indexOf("person") < 0)
               sf.appendLine(Globals.getDataDir()+"gossip.txt",gossip);
	             array[i] = gossip;
                }
	        else if (state.compareTo("SR_STATE") == 0) {
	          String apps = append(aiml_index, i);
                  apps = Substituter.normalize(apps);
                  apps = apps.trim();
//		  System.out.println("respond to: '"+apps+"'");
                  array[i] = Classifier.respond(apps, ip);
//   	          System.out.println("Array[i] = '"+array[i]+"'");
	        }
	        for (int n = aiml_index; n < i; n++) array[n] = "";
 	        state = "DEFAULT";
            }
            else if (token.startsWith("get_ip()")) {
		array[i] = ip;
            }
            else if (token.startsWith("getname()")) {
                String name = Substituter.formal(Classifier.getname(ip));
		array[i] = name;
//		System.out.println("name = "+array[i]);
            }
            else if (token.startsWith("gender()")) {
		String gender = Classifier.get_gender(ip);
		array[i] = gender;		
	    } 
            else if (token.startsWith("setname(*)")) {
                Classifier.setname(ip, Substituter.formal(star));
                array[i] = Substituter.formal(star);
            }
            else if (token.startsWith("person2(*)")) {
// change * to second person:
                array[i] = Substituter.pretty(Substituter.person2(star));
            }
            else if (token.startsWith("person(*)")) {
// interchange 1st and 3rd person:
                array[i] = Substituter.pretty(Substituter.person(star));
                //System.out.println("'"+array[i]+"'");
            }
            else if (token.startsWith("sr(*)")) {	
		array[i] = Classifier.respond(star, ip);
            }
            else if (token.startsWith("set_female()")) {
		array[i] = "";
		Classifier.set_gender(ip, "she");
            }
            else if (token.startsWith("set_male()")) {
		array[i] = "";
		Classifier.set_gender(ip, "he");
            }
            else if (token.startsWith("set_it()")) {
	        state = "IT_STATE";
		aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("set_they()")) {
	        state = "THEY_STATE";
		aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("set_he()")) {
	        state = "HE_STATE";
		aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("set_she()")) {
	        state = "SHE_STATE";
		aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("setname()")) {
	        state = "NAME_STATE";
		aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("get_it()")) {
		array[i] = Classifier.get_it(ip);
            }
            else if (token.startsWith("get_they()")) {
		array[i] = Classifier.get_they(ip);
            }
            else if (token.startsWith("that()")) {
		array[i] = Classifier.get_that(ip);
            }
            else if (token.startsWith("get_he()")) {
		array[i] = Classifier.get_he(ip);
            }
            else if (token.startsWith("get_she()")) {
		array[i] = Classifier.get_she(ip);
            }
            else if (token.startsWith("getsize()")) {
		array[i] = Classifier.size();
            }
            else if (token.startsWith("getfile()")) {
	        state = "GETFILE_STATE";
		array[i] = "";
            }
            else if (token.startsWith("gossip()")) {
	        state = "GOSSIP_STATE";
	 	aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("random()")) {
	        state = "RANDOM_STATE";
	        aiml_index = i;
		array[i] = "";
            }
            else if (token.startsWith("srai()")) {
	        state = "SR_STATE";
	        aiml_index = i;
		array[i] = "";
            }
            else array[i] = "";
         }
         else if (token != null) array[i] = token; else array[i] = "";
      }
    }
    } catch (Exception e) {System.out.println("APPEND: "+e);}
  return append();
  }     
} // Parser class



// class Unifier for matching sentences:
//
//
// The class Unifier defines an object for matching sentences and
// patterns.  A Unifier object has a member star that stores the
// part of an input sentence that matches the wild-card AIML
// pattern "*".  The member function unify() does the work of
// matching the sentence and pattern strings.   It returns a boolean
// value indicting the success of the match.

class Unifier extends Object {
protected String star; // string that contains matched subsentence
public Unifier() { star = "";}
public String bugline = "";
public boolean unify(String Input, String Pattern) {
  star = "";
  String input = Input.trim();
  String pattern = Pattern.trim();
  if (input == pattern) return true;
  // the following are special cases:
  // the insidious "blank" Pattern (zero length):
  if (input.length() <= 0) return false;
  if (pattern.length() <= 0) return false;
  // excluding patterns containing _ or *
  // the input must be lexicographically less than or equal to the pattern
  // unify will fail when the first letter does not match:
  char x='.'; char y='.';
  try {
    x = input.charAt(0);
    y = pattern.charAt(0);
  }
  catch (Exception e) {
    System.out.println("UNIFY: "+e);
  }
  if (y != '*' && y != '_' && x != y)
    return false;
  pattern = Substituter.replace("_","*",pattern);
  if (pattern.indexOf("*") < 0) {
     bugline = "BUG: "+input+" : "+pattern;
     if (pattern.length() != input.length()) return false;
     if (input.equals(pattern)) return true;
     else return false;
  }
  StringTokenizer s = new StringTokenizer(input);
  StringTokenizer p = new StringTokenizer(pattern);
  int plength = p.countTokens();
  int slength = s.countTokens();
  if (plength > slength) return false;
  // ASSERT(plength <= slength);
  //    if (debug) System.out.println("PATTERN: "+pattern);
  if (pattern == "*") pattern = "* ";
  if (pattern.indexOf("*") > -1) {
    while (plength < slength) {
      int m = pattern.indexOf('*');
      String tail;
      if (m >= pattern.length()) tail = "";
      else tail = pattern.substring(m);
      pattern = pattern.substring(0, m)+"* "+tail;
      pattern = pattern.trim();
      plength++;
      }
  }
  if (plength != slength) return false;
  //    if (debug) System.out.println("UNIFY: "+input+":"+pattern+"::");
  p = new StringTokenizer(pattern);
  for (int i = 0; i < plength; i++) {
  String word = s.nextToken();
    String pat  = p.nextToken();
      if (pat.compareTo("*") == 0) {
      //	   if (debug) System.out.println("MATCH: "+word+":"+pat+"::");
      star = star + " " + word;
    }
    else if (word.compareTo(pat) != 0) {
      star = "";
      return false;
    }
  }
  star = star.trim();
  return true;
} // end of method unify()
} // end of object class Unifier


// the Brain is the final class in the
// Vector --> StringSet --> StringSorter --> Brain lineage.
// Brain also uses StringRanker which has the lineage:
// Vector --> StringSet --> StringHistogrammer --> StringRanker
// Brain is basically a set of sorted strings (keys)
// with maps to Patterns, Templates, and other information
// associated with each Category.

final class Brain extends StringSorter {
  protected Predicates Filemap; // map to source files
  protected Predicates TopicPatternmap; // added, <topic> support (Drent 10-13-1999)
  protected Predicates Patternmap;
  protected Predicates ThatPatternmap;
  protected Predicates Templatemap; // Template
  protected Hashtable Targetmap; // map to StringRankers

  // constructor
  public Brain () {
    super();
    Filemap=new Predicates();
    Patternmap=new Predicates();
    ThatPatternmap=new Predicates();
    TopicPatternmap=new Predicates();
    Templatemap=new Predicates();
    Targetmap=new Hashtable();
  }
  public void resetTargets() {
    int size = this.size();
    for (int i = 0; i < size; i++)
        Targetmap.put((String)elementAt(i), new StringRanker());
  }
  public StringRanker Target(int i) {
    if (i < size()) {
      return((StringRanker)Targetmap.get((String)elementAt(i)));
    }
    else return null;
  }
  public String Pattern(int i) {
    return Patternmap.getProperty((String)elementAt(i), "");
  }
  public String Filename(int i) {  
    return Filemap.getProperty((String)elementAt(i), Globals.getBotFile());
  }
  public String TopicPattern(int i){ // added, <topic> support (Drent 10-13-1999)
    return TopicPatternmap.getProperty((String)elementAt(i), "*");
  }
  public String ThatPattern(int i) {
    return ThatPatternmap.getProperty((String)elementAt(i), "*");
  }
  public String Template(int i) {  
    return Templatemap.getProperty((String)elementAt(i), "Wha?");
  }

// a method to list all patterns in a string form:
  public String PatternstoString() {
    String output =" RAN SHOW PATTERN SEARCH STRING";
   /* for (int i = 0; i < size(); i++) {
      if (i % 1000 == 0) System.out.println(i+" Patterns");
      String tpat = "";
      if (ThatPattern(i).compareTo("*") != 0) tpat = "\t\t\t"+ThatPattern(i);
      output = output + Pattern(i) + tpat+"\n";
    } */
    showPatternSearchStrings();
    return output;
  }

  // [added for debugging, Drent ]
  // a method to list all patterns in a string form:
  public void showPatternSearchStrings() {
    for (int i = size()-1; i >=0; i--) {
      System.out.println(
         Pattern(i) + " : " + ThatPattern(i) + " : " + TopicPattern(i)
         + "      " + Filename(i));
    }
  }

// a method to display histogram in string form:
  public String HistoString() {
    int sum=0; 
    double integral=0.0;
    double pct=0.0;
    SortedIntSet sis = new SortedIntSet(); // range of histogram
    System.out.println("Preparing Histogram");
    for (int i = 0; i < size(); i++) {    
      if (Target(i) != null) {
        int total = Target(i).getTotal();             
        sum += total;
        sis.add(total); // skip val = 1
      } // 
    } // loop over Brain keys
    String output = "";
    // loop over the histogram range values: 
    for (int n = sis.size()-1; n >= 0; n--) {
      System.out.println(n+" "+sis.elementAt(n));
      int val = ((Integer)(sis.elementAt(n))).intValue();
      if (val > 1) {
      // loop over the Brain keys:
      for (int i = 0; i < size(); i++) {
        String outline="";
        // if the domain value val is found:
        if (Target(i) != null && Target(i).getTotal() == val) {
	    //     System.out.println(Pattern(i)+" "+val+" "+Target(i).size());
          pct = val/(double)sum; 
          integral += 100.0*pct;
	  int Pct = (int)(pct*100.0+0.5);
	  int Int = (int)(integral+0.5);
          outline = Pct+"%\t("+Int+"%)\t"+
            Target(i).getTotal()+" "+ Pattern(i)+" = ";
	// careful not to confuse getTotal and size here !
          for (int j = 0; j < Target(i).size(); j++) {
	      //          if (outline.length() < 2048*4
            outline +=
              ((1 == Target(i).Count(j)) ? "" : Target(i).Count(j)+" ")+
              Target(i).elementAt(j)+
              ((j == Target(i).size()-1) ? "" : "+");
        } // for elements in target set
        output += outline+"\n";
        } // if 
      } // for loop over Brain items
      } // val > 1
    } // for loop over histogram domain
  return output;
  } // toString

  public boolean isAtomic(String pattern, String that) {
   return((pattern.indexOf("*") < 0) &&
          (pattern.indexOf("_") < 0) &&
          (that.compareTo("*")==0));
  }

  // modified: added String topic arg, <topic> support (Drent 10-13-1999)
  public void add( String pattern, String topic, String template, String filename) {
    add(pattern, "*", topic,template, filename);  // modified: added topic arg, <topic> support (Drent 10-13-1999)
  }

  public void add(String pattern, String that, String topic,
		  String template, String filename) {

//Redundant code RULEZ ;->

    Predicates Language = new Predicates();
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   


    if(topic==null) topic="*"; // added, <topic> support (Drent 10-13-1999)
    pattern = pattern.trim();
    // the <name/> tag is the only XML appearing in a pattern
    // (for now)
    if (pattern.indexOf("<name/>") >= 0) {
      String botname = Globals.getBotName();
      botname = Substituter.normalize(botname);
      pattern = Substituter.replace("<name/>",botname,pattern);
    }
    that = that.trim();
    topic = topic.trim();
    template = template.trim();
    //String key = pattern + " : " + that; //orig
    String key =  pattern + " : " + that + " : " + topic ; // modified, <topic> support (Drent 10-13-1999)
    if (contains(key)) {
        // added by RSW 4/00:
	// Depending on Globals.getMergePolicy(),
        // duplicate keys result in either (1) Merged templates
        // or (2) discarding the (newer) template and keeping the
        // existing response.
        // etempl is the existing stored template.
        String etempl = Templatemap.getProperty(key, "none");
        System.out.println("Duplicate "+key+" "+Globals.getMergePolicy());

        if (Globals.getMergePolicy().compareTo("Merge") == 0) {
          String newtempl; // the new template
          StringFile sf = new StringFile();
          if (etempl.compareTo(template) == 0 || 
              etempl.indexOf(template) >= 0) {
            // could be same or 
	    // already contain this response
            newtempl = etempl;
            sf.appendLine(Globals.getDataDir()+"duplicates.txt",
              key+" - identical template.");
          }
          else if (etempl.indexOf("<random>") >= 0) {
            // check to see if existing template is already
            // a random response. In that case,
            // insert the new response:
            int n = etempl.indexOf("<li>"); // first item;
            if (n > 0) { // make sure the list items are there
               newtempl = etempl.substring(n, etempl.length());
               newtempl = "<random>\n<li>"+template+"</li>\n"+newtempl;
            }
            else newtempl = etempl;
	  } // if existing template was random
          else {
            // the default "merge" with <random>:
            newtempl = 
              "<random>\n<li>"+etempl+"</li>\n"+
                        "<li>"+template+"</li>\n</random>\n";
            sf.appendLine(Globals.getDataDir()+"duplicates.txt",
             key+" - "+newtempl+" ("+
             Filemap.getProperty(key)+")");
          } // if-else 
          // some newtempl has now been created.
          Templatemap.put(key, newtempl);
        } // end of Merge
        else // merge policy is Discard.
          System.out.println("Duplicate "+key+" discarded.");
    } 
    else { // not containsKey
      super.add(key);
      try {
        Patternmap.put(key, pattern);
        ThatPatternmap.put(key, that);
        TopicPatternmap.put(key, topic); // added, <topic> support (Drent 10-13-1999)
        int n;
        // load any AIML files associated with this template:
        String templ = template;
        while ((n = templ.indexOf("<load filename=\"")) >= 0) {
          templ = templ.substring(n+"<load filename=\"".length(), templ.length());
          int m = templ.indexOf("\"/>");
          if (m >= 0) {
             String line = templ;
             templ = (m+3 >= templ.length()) ? "" : templ.substring(m+3, templ.length());
             line = line.substring(0, m);
             line = line.trim();

//Added variable "FileLoadMsg"
             if (Classifier.console) System.out.println(Language.getProperty("FileLoadMsg","Loading:")+" "+line);
             Classifier.append_aiml_file(line);
          }
        }
        templ = template;
        while ((n = templ.indexOf("<load url=\"")) >= 0) {
          templ = templ.substring(n+"<load url=\"".length(), templ.length());
          int m = templ.indexOf("\"/>");
          if (m >= 0) {
             String line = templ;
             templ = (m+3 >= templ.length()) ? "" : templ.substring(m+3, templ.length());
             line = line.substring(0, m);
             line = line.trim();
             System.out.println("Loading: "+line);
             Classifier.append_aiml_url(line);
          }
        }
        Templatemap.put(key, template);
        Targetmap.put(key, new StringRanker());
        if (filename == null) filename = Globals.getBotFile();
        // begin experimental code:
        // stuff that comes from New.aiml ends up in
        // Srai, Brain or Atomic:
	// if (filename.compareTo("New.aiml") == 0) {
	// if (template.indexOf("<sr") >= 0) filename = "Srai.aiml";
	//  else if (pattern.indexOf("*") >= 0) filename = "Brain.aiml";  
	// else filename = "Knowledge.aiml";
        // } 
        // end experimental code.
        Filemap.put(key, filename);
      }
      catch (Exception e) {
        System.out.println("BRAIN.ADD: "+e);
      }
    }
  } // add AIML
} // Brain

// see the file Log.java for the LineProcessor interface

class LineClassifier implements LineProcessor {
  String client_line_contains = Globals.getClientLineContains(); // "Says: ";
  String robot_line_start = Globals.getRobotLineStarts(); // "ALICE Says:";
  StringSorter Lines = new StringSorter(); 
  public LineClassifier() {
    super();
    System.out.println("Line Classifier created");
    Lines = new StringSorter();
  }
  public void classify() {
    int bound = 0;
    Classifier.brain.resetTargets();
    for (int i = 0; i < Classifier.brain.size(); i++) {
      if (Classifier.brain.Pattern(i).startsWith("*")) bound = i;
    }
     // examine the sentences in reverse alpha order:
    int index = Classifier.brain.size()-1;
    for (int i = Lines.size()-1; i >= 0; i--) {
      String sentence = (String)Lines.elementAt(i);
      // modified: added DEFAULT string for topic argument, <topic> support (Drent 10-13-1999)
      int nindex = Classifier.respondIndex(sentence, new String("DEFAULT"),
                                         new String("DEFAULT"), index);
  	  if (nindex > -1) {
        if (Classifier.brain.Pattern(nindex).indexOf("*") < 0) {
	        index = nindex;
        }
        System.out.println(index+" "+sentence);
	      Classifier.brain.Target(nindex).add(sentence);
	    } // if index
    }
  } // classifiy

  public void ProcessLine (String line) {
    int m = line.indexOf(client_line_contains);
    if (line != null && line.indexOf(robot_line_start) < 0 &&
	m >= 0) {
      line = line.substring(m+client_line_contains.length(),line.length());
      line = line.trim();
      line = Substituter.cleanup_http(line);
      line = Substituter.deperiodize(line);
      line = line.replace('*',' ');
      line = line.replace('_',' ');
      line = Substituter.normalize(line);
      while (line.endsWith(".")) line = line.substring(0,line.length()-1);
      StringTokenizer st = new StringTokenizer(line, ".");
      int n = st.countTokens();
      try {
      for (int x = 0; x < n ; x++) {
	String sentence = st.nextToken();
        if (sentence.length() > 128) 
        sentence = sentence.substring(0,127);
	//        System.out.println(sentence);
	//        System.out.print(sentence.charAt(0));
	Lines.add(sentence);
        int sz = Lines.size();
        if (sz % 1000 == 0) System.out.println(sz+" sentences read");
      } // for
      } catch (Exception e) {System.out.println("Classifier: "+e);}
  }
  } // ProcessLine
} // class LineClassifier

// class Classifer 
// contains method to read AIML file
// 
// The class Classifier might as well be called the class Bot, because
// this class is where most of the action takes place.  Program B has
// only one static instance of the Classifier class.
//
// The Classifier class also has member property lists for storing
// the client state information.  Program A uses the client's
// IP address to index these tables.  An alternative approach
// would be to store this information in cookies on the client side,
// Many of the member functions of this class are just procedures
// for getting and setting (or appending) the data members.


//  
public class Classifier {
public static boolean is_applet=false;
public static int asize=0;
public static String star=null;
public static Brain brain = new Brain();
public static float tavg=0;
public static int tcnt=0;
public static boolean console=true; // for messages

    // the ipSet is the set of all clients.
    // If the caller is the Applet or GUI, then the ipSet
    // has just one client: localhost.
protected static StringSet ipSet = new StringSet();

    // The Predicates are maps from client IP
    // to various state variables such as "he", "it", and "age".
    // If the caller is the Applet, then these maps
    // degenerate to unary Predicates of the single
    // client localhost.
protected static Predicates ip_he = new Predicates();
protected static Predicates ip_it = new Predicates();
protected static Predicates ip_age = new Predicates();
protected static Predicates ip_she = new Predicates();
protected static Predicates ip_name = new Predicates();
protected static Predicates ip_date = new Predicates();
protected static Predicates ip_that = new Predicates();
protected static Predicates ip_they = new Predicates();
protected static Predicates ip_topic = new Predicates();
protected static Predicates ip_gender = new Predicates();
protected static Predicates ip_justthat = new Predicates();
protected static Predicates ip_location = new Predicates();
protected static Predicates ip_animagent = new Predicates();
protected static Predicates ip_beforethat = new Predicates();
protected static Predicates ip_justbeforethat = new Predicates();

protected static PredicateMap predicatemap = new PredicateMap();

protected static Predicates Language = new Predicates();
// method fromFile () 
public static void fromFile () {


    try {ip_he.load(new FileInputStream(Globals.getDataDir()+"ip_he.txt"));}
    catch (Exception e) {/* */}    
    try { ip_it.load(new FileInputStream(Globals.getDataDir()+"ip_it.txt"));}
    catch (Exception e) {/* */}   
    try { ip_she.load(new FileInputStream(Globals.getDataDir()+"ip_she.txt"));}
    catch (Exception e) {/* */}    
    try { ip_age.load(new FileInputStream(Globals.getDataDir()+"ip_age.txt"));}
    catch (Exception e) {/* */}   
    try { ip_date.load(new FileInputStream(Globals.getDataDir()+"ip_date.txt"));}
    catch (Exception e) {/* */}
    try { ip_name.load(new FileInputStream(Globals.getDataDir()+"ip_name.txt"));}
    catch (Exception e) {/* */}

    // in fromFile()...
    try { ip_they.load(new FileInputStream(Globals.getDataDir()+"ip_they.txt"));}
    catch (Exception e) {/* */}

    try { ip_that.load(new FileInputStream(Globals.getDataDir()+"ip_that.txt"));}
    catch (Exception e) {/* */}
    try { ip_topic.load(new FileInputStream(Globals.getDataDir()+"ip_topic.txt"));}
    catch (Exception e) {/* */}
    try { ip_gender.load(new FileInputStream(Globals.getDataDir()+"ip_gender.txt"));}
    catch (Exception e) {/* */}
    try { ip_location.load(new FileInputStream(Globals.getDataDir()+"ip_location.txt"));}
    catch (Exception e) {/* */}
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) {/* */}
    try { predicatemap.load(new FileInputStream(Globals.getDataDir()+"predicates.txt"));}
    catch (Exception e) {
       System.out.println("No custom predicates located");
    }
    System.out.println("Remembered "+ip_name.size()+" clients.");
} // Classifier.fromFile

  public static void update() {
     long maxdiff = 0; String maxip="localhost";
     long mindiff = 1000000; String minip="localhost";
     try {
       Enumeration fnum = ip_date.keys();
       while (fnum.hasMoreElements()) {
         String ip = (String)(fnum.nextElement());
         Date previous = new Date(get_date(ip));
         long diff = -(previous.getTime()-new Date().getTime());
         if (diff > maxdiff) {maxdiff = diff; maxip=ip;}
         if (diff < mindiff) {mindiff = diff; minip=ip;}
       } // while
     }
     catch (Exception e) {
       System.out.println("Classifier.update: "+e);
     }
     System.out.println(maxip+" "+maxdiff);
     System.out.println(minip+" "+mindiff);
  }

// method toFile () {
static void toFile () {
    // update();
  predicatemap.store(); // save custom tag values
  try {
    ip_he.store(new FileOutputStream(Globals.getDataDir()+"ip_he.txt"), new Date().toString());
    ip_it.store(new FileOutputStream(Globals.getDataDir()+"ip_it.txt"), new Date().toString());
    ip_she.store(new FileOutputStream(Globals.getDataDir()+"ip_she.txt"), new Date().toString());
    ip_age.store(new FileOutputStream(Globals.getDataDir()+"ip_age.txt"), new Date().toString());
    ip_name.store(new FileOutputStream(Globals.getDataDir()+"ip_name.txt"), new Date().toString());
    ip_date.store(new FileOutputStream(Globals.getDataDir()+"ip_date.txt"), new Date().toString());
    ip_that.store(new FileOutputStream(Globals.getDataDir()+"ip_that.txt"), new Date().toString());
    // in toFile()...
    ip_they.store(new FileOutputStream(Globals.getDataDir()+"ip_they.txt"), new Date().toString());

    ip_topic.store(new FileOutputStream(Globals.getDataDir()+"ip_topic.txt"), new Date().toString());
    ip_gender.store(new FileOutputStream(Globals.getDataDir()+"ip_gender.txt"), new Date().toString());
    ip_location.store(new FileOutputStream(Globals.getDataDir()+"ip_location.txt"), new Date().toString());
  }
  catch (Exception e) {
    System.out.println("Classifier.toFile: "+e);
  }  // catch e
  //  System.out.println("Remembering "+ip_name.size()+" clients.");
} // Classifier.toFile


public static Random RNG = new Random();

    // Client: ...BeforeThat
    // Robot: ...JustBeforeThat
    // Client: ...JustThat
    // Robot: ...That
    // Client: input

    // NAME:

public static void setname(String ip, String name) {
  ip_name.put(ip, name);
} // Classifier.setname()

public static String getname(String ip) {
  return(ip_name.getProperty(ip, nickname(ip)));
}

    // TOPIC:

public static void settopic(String ip, String topic) {
  ip_topic.put(ip, topic);
} // Classifier.settopic();

public static String gettopic(String ip) {
  return (ip_topic.getProperty(ip, "you"));
}
    // LOCATION:

public static void set_location(String ip, String loc) {
  ip_location.put(ip, loc);
}

public static String get_location(String ip) {
  return (ip_location.getProperty(ip, "where"));
}

    // AGE:

public static void set_age(String ip, String age) {
  ip_age.put(ip, age);
}

public static String get_age(String ip) {
  return (ip_age.getProperty(ip, "how many"));
}
 
    // GENDER:

public static void set_gender(String ip, String gender) {
  ip_gender.put(ip, gender);
} // Classifier.set_gender()

public static String get_gender(String ip) {
  return(ip_gender.getProperty(ip, "he"));
}

    // IT:

public static void set_it(String ip, String it) {
  ip_it.put(ip, it);
}

public static String get_it(String ip) {
  return(ip_it.getProperty(ip, "itself"));
}

    // HE:

public static String get_he(String ip) {
  return(ip_he.getProperty(ip, "himself"));
}

public static void set_he(String ip, String he) {
  ip_he.put(ip, he);
}

    // SHE:

public static void set_she(String ip, String she) {
  ip_she.put(ip, she);
}

public static String get_she(String ip) {
  return(ip_she.getProperty(ip, "she"));
}

    // THEY:

    public static void set_they(String ip, String they) {
        ip_they.put(ip, they);
    }

    public static String get_they(String ip) {
        return(ip_they.getProperty(ip, "they"));
    }


// this method had ip_they.put -> switched to ip_date
    public static void set_date(String ip) {
        ip_date.put(ip, new Date().toString());
    }

public static String get_date(String ip) {
  return(ip_date.getProperty(ip, new Date().toString()));
} // Classifier.get_date


    // ANIMAGENT:

public static void set_animagent(String ip) {
  ip_animagent.put(ip, "true");
}

public static boolean get_animagent(String ip) {
  return (ip_animagent.getProperty(ip) == "true");
}

    // CUSTOM PROPERTIES:

public static void set_property(String pname, String ip, String val) {
  ((Predicates)(Classifier.predicatemap.pmap.get(pname))).put(ip, val);
}

public static String get_property(String pname, String ip) {
  String val = Classifier.predicatemap.getProperty(pname);
  if (val.indexOf("*") >= 0 && val.length() > 1) {
    val = val.substring(val.indexOf("*")+1, val.length());
  }
  return ( 
  ((Predicates)(Classifier.predicatemap.pmap.get(pname))).getProperty(ip,val)); 

}

  // THAT:

public static void set_that(String ip, String inthat) {
  String that = inthat;
  inthat = Substituter.suppress_html(inthat);
  inthat = Substituter.deperiodize(inthat);
  StringTokenizer qt = new StringTokenizer(inthat, ".!?");
  int ct = qt.countTokens();
  for (int i = 0; i < ct; i++) {
    String sentence = qt.nextToken();
    sentence = sentence.trim();
    if (sentence.length() > 0) that = sentence;
  }
  if (that.length() > 1024) that = that.substring(0,1023);
  if (that.length() <= 0) that = "that";
  ip_justbeforethat.put(ip, ip_that.getProperty(ip, "that"));
  //  System.out.println("Set THAT to: "+that);
  ip_that.put(ip, that);
}

public static void set_justthat(String ip, String that) {
  ip_beforethat.put(ip, ip_justthat.getProperty(ip, "that"));
  ip_justthat.put(ip, that);
}

public static String get_that(String ip) {
  return(ip_that.getProperty(ip, Language.getProperty("AskName", "What can I call you?")));
}

public static String get_justthat(String ip) {
  return(ip_justthat.getProperty(ip, "that"));
}

public static String get_justbeforethat(String ip) {
  return(ip_justbeforethat.getProperty(ip, "that"));
}

public static String get_beforethat(String ip) {
  return(ip_beforethat.getProperty(ip, "that"));
}

    // SIZE:

public static String size() {
  String out = "";
  out += brain.size()+" Categories<BR>";
  int sum = 0;
  for (int i = 0; i < brain.size(); i++) {
    sum += brain.Pattern(i).length();
    sum += brain.ThatPattern(i).length();
  }
  out += sum+" Pattern chars<BR>";
  sum = 0;
  for (int i = 0; i < brain.size(); i++) {
    sum += brain.Template(i).length();
  }
  out += sum+" Template chars<BR>";
return (out);
} // method Classifier.size()

    // NICKNAME:

public static String nickname(String ip) {
  StringTokenizer ipt = new StringTokenizer(ip, ".");
  String nickname = "localhost-person";
  if (is_applet) {
    try {
      nickname = System.getProperty("user.name");
    }
    catch (Exception e) {
      System.out.println("Java cannot see your name");
    }
  } // if
  else if (ip.indexOf("webtv") >= 0) nickname = "webtv-person";
  else if (ipt.countTokens() == 1) nickname = ip+"-person";
  else if (ipt.countTokens() == 2) {
	String s1 = ipt.nextToken();
	String s2 = ipt.nextToken();
        nickname = s1+"-person";
  }
  else if (ipt.countTokens() == 3) {
	String s1 = ipt.nextToken();
	String s2 = ipt.nextToken();
	String s3 = ipt.nextToken();
        nickname = s2+"-person";
  }
  else if (ipt.countTokens() >= 4) {
	String s1 = ipt.nextToken();
	String s2 = ipt.nextToken();
        String s3 = ipt.nextToken();
	String s4 = ipt.nextToken();
        nickname = s3+"-person";
  }
return(nickname);
}

// a main method to test this class from the command line
 public static void main (String[] argv) {

//Did someone say "redundant code"?
//Alright, here goes the constructor from Bawt.java

    Predicates Language = new Predicates();
    try {Language.load(new FileInputStream(Globals.getDataDir()+"language.txt"));}
    catch (Exception e) { }   

   read_aiml("Gossip.aiml");
   String x = respond("GOSSIP", "localhost");
   System.out.println(x);
  }

// the method respondIndex locates
// the index of the best matched category
// for the input string
// [modified: added String topic argument, <topic> support (Drent 10-13-1999)]
public static int respondIndex(String input, String that, String topic) {
  return respondIndex(input, that, topic, brain.size()-1);
}
    // the point of the three-argument respondIndex is to
    // allow the caller to provide a "hint" (actually an upper bound)
    // telling the program where to start the search
// [modified: added String topic argument, <topic> support (Drent 10-13-1999)]
public static int respondIndex(String input, String that, String topic, int index) {
Unifier uf = new Unifier();
boolean X=false;
boolean Y=false;

topic = topic.toUpperCase();

for (int j = index; j > 0; j--) {
 try {
   if(uf.unify(input, brain.Pattern(j)) &&
      uf.unify(that, brain.ThatPattern(j)) &&
      uf.unify(topic, brain.TopicPattern(j)))  // added, <topic> support (Drent 10-13-1999)
     {
     if (console)
     System.out.println("Matched "  + brain.Pattern(j)
                       +", that: "  + brain.ThatPattern(j)
                       +", topic: " + brain.TopicPattern(j));
     return j;
     }
  }
  catch (Exception e) {
    System.out.println("UNIFY: "+e);
  }
}
return 0;
} // end of method Classifier.respondIndex



public static String respond(String input, String ip) {
  ipSet.add(ip);
  Unifier uf = new Unifier();
  String response="";
  try {
    String nthat = get_that(ip);
    nthat = Substituter.deperiodize(nthat);
    nthat = Substituter.normalize(nthat);
    String ntopic = gettopic(ip); // added, <topic> support (Drent 10-13-1999)
    int index = respondIndex(input, nthat, ntopic);// modified: added ntopic arg, <topic> support (Drent 10-13-1999)
    uf.unify(input, brain.Pattern(index));
    star = uf.star;
    star = star.trim();
    response = brain.Template(index);
  }   catch (Exception e) {System.out.println("RESPOND: "+e);}
  Parser p = new Parser();
  response = p.pfkh(ip, response, star);
  response = Substituter.capitalize(response);
  return response;
} // end of method respond

    // For various reasons the applet and the application
    // respond to queries differently.  
    // The "multiline" means that the input query contains
    // one or more sentences.  The "multiline response" is
    // a line-by-line response to each input sentence.  
    // the multiline_response method is synchrnonized because
    // it may be called by any Clerk (see class Clerk).
    // "multiline" means "multiple sentences"
    // multiline_response() provides the response for the web 
    // in the application.

public static synchronized String multiline_response(String input, String hname, Responder robot) {
  input = robot.pre_process(input, hname);
  input = Substituter.deperiodize(input);
  StringTokenizer st = new StringTokenizer(input, "?!.");
  int ct = st.countTokens();
  String reply="";
  String response="";
  Date T = new Date();
  long t = T.getTime();
  for (int i = 0; i < ct; i++) {
    String sentence = st.nextToken();
    sentence = sentence.trim();
    if (sentence.length() > 0) {
      sentence = sentence.replace('*',' ');
      String norm = Substituter.normalize(sentence);
      reply = Classifier.respond(norm, hname);
      robot.log(norm, reply, hname);
      response = robot.append(sentence, reply, response);
    }
  }
  Classifier.set_that(hname, reply);
  Classifier.set_date(hname);
  response = robot.post_process(response);
  T = new Date();
  t = T.getTime()-t;
  tcnt = tcnt+1;
  tavg = (tavg * (tcnt-1) + t)/tcnt;
  if (console)
    System.out.println("Response "+t+"ms ("+tavg+") "+tcnt);
  return(response);   
}

public static void read_aiml(String aname) {
  brain = new Brain();
  append_aiml_file(aname);
}

public static void read_aiml_url(String aname) {
  brain = new Brain();
  append_aiml_url(aname);
}

// a class for alice data parser
static class aliceListener implements AliceReaderListener {
  String filename; 
  boolean is_applet = false;

public aliceListener(String filename, boolean is_applet) {
  this.is_applet = is_applet;
  this.filename = Globals.getBotFile();
}

public aliceListener(String filename) {
  this.filename = filename;
}

public void newCategory(String pattern,
                        String that,
                        String topic, // added, <topic> support (Drent 10-13-1999)
                        String template)
  {

  if(topic == null) topic = "*";   // added, <topic> support (Drent 10-13-1999)

  try {
  if (that == null) brain.add(new String(pattern),
                              new String(topic), // added, <topic> support (Drent 10-13-1999)
                               new String(template),
                              new String(filename));
  else brain.add(new String(pattern),
                 new String(that),
                 new String(topic),
                 new String(template),
                 new String(filename));

  if (brain.size() % 1000 == 0)

//Added variable CategoryLoadMsg

    System.out.println(brain.size()+" "+Language.getProperty("CategoryLoadMsg"," Categories and Loading"));
  if (is_applet && Loader.pause == true) {
	try {
	    //           System.out.println("new Category: SLEEP");
           Thread.sleep(250);
        }
        catch (Exception e) {System.out.println("newCategory:"+e);}
  }  // if
  } // try
    catch (Exception e) {System.out.println("newCategory:"+e);}
  } // newCategory member

} // aliceListener class

// append_aiml_file :
// Append to the robot script from the File fname

public static void append_aiml_file(String fname) {
  try {
    FileInputStream fis = new FileInputStream(fname);
    InputStreamReader isr = new InputStreamReader(fis);
    BufferedReader br = new BufferedReader(isr);
    // the temp file appears as the argument of fname 
    // when the botmaster
    // runs "Add Aiml".  In this case save the
    // new categeories in the default AIML file:
    if (fname.compareTo(Globals.getTempFile()) ==0) 
        fname = Globals.getBotFile();
    AliceReader alice = new AliceReader(br, new aliceListener(fname));
    alice.read(); 

//Added variable "FinishedLoadingMsg"
    if (Classifier.console) System.out.println(Language.getProperty("FinishedLoadingMsg","Finished Loading:")+" "+fname);
  }
  catch (Exception e) {
  System.out.println("Append AIML File: "+e);
  }
} // append_aiml_file

// append_aiml_url
// the source of a robot script is a URL
//
public static void append_aiml_url(String aname) {
try {
  System.out.println("Loading Robot from URL "+aname);
  Loader.currfile = aname;
  URL url = new URL(aname); // e.g. aname is "http://localhost/B/B.aiml"
  InputStream in = url.openStream();
  InputStreamReader isr = new InputStreamReader (in);
  BufferedReader br = new BufferedReader(isr);
  AliceReader alice = new AliceReader(br, new aliceListener(aname, true));
  alice.read();
  System.out.println("Robot Loaded from "+aname);
} catch (Exception e) {System.out.println("AIML URL Exception: "+e);}
} // end of method append_aiml()

// The robot contains AIML catgeoires loaded from (possibly)
// several files.  At load time, the program remembered the
// source of each category in the Filename map.  

public static void save_robot() {
  // The fileset is the set of all file names in the
  // range of the Filename map:
  StringSet fileset = new StringSet();
  // map from filenames to StringFile handles:
  Hashtable sfmap = new Hashtable();
  // map from filenames to topic sets:
  Hashtable topicmap = new Hashtable();
  StringFile sf;
  for (int i = 0; i < brain.size(); i++) {
    if (!fileset.contains(brain.Filename(i))) {
      fileset.add(brain.Filename(i));
      topicmap.put(brain.Filename(i), new StringSet());
      sf = new StringFile();
      sf.open(brain.Filename(i), "w"); 
      sf.appendLine("<alice>");
      sf.appendLine("<!-- Open source software Copyright (c) 2000 Dr. Richard S. Wallace. --> ");
      sf.appendLine("<!-- This program is free software; you can redistribute it and/or modify -->");
      sf.appendLine("<!-- it under the terms of the GNU General Public License as published by -->");
      sf.appendLine("<!-- the Free Software Foundation.  -->");
      sfmap.put(brain.Filename(i), sf);
    }
  }

  // collect all the topics into a set:
  for (int i = 0; i < brain.size(); i++) {
    StringSet topicset = (StringSet)(topicmap.get(brain.Filename(i)));
    if (!topicset.contains(brain.TopicPattern(i))) {
      topicset.add(brain.TopicPattern(i));
      topicmap.put(brain.Filename(i), topicset);
    }
  }
  SortedIntSet sis = new SortedIntSet();
  for (int i = 0; i < brain.size(); i++) {
      if (brain.Target(i) != null)
        sis.add(brain.Target(i).getTotal());             
  }
  sis.add(0); // zero activation
  // loop over files:
  for (int u = 0; u < fileset.size(); u++) {
    String filename = (String)(fileset.elementAt(u)); 
    System.out.println("Saving File: "+filename);
    sf = (StringFile)sfmap.get(filename);
    // loop over topics:
    StringSet topicset = (StringSet)(topicmap.get(filename));
    for (int w = 0; w < topicset.size(); w++) {
      String topic = (String)(topicset.elementAt(w)); 
      if (topic.compareTo("*") != 0) 
        sf.appendLine("<topic name=\""+topic+"\">");
      // loop over activation counts:
      for (int k = sis.size()-1; k >= 0; k--) {
        int val = ((Integer)(sis.elementAt(k))).intValue();
        // loop over the brain elements:
        for (int i = 0; i < brain.size(); i++) {
          if (brain.Target(i).getTotal() == val &&
              brain.TopicPattern(i).compareTo(topic)==0 &&
              brain.Filename(i).compareTo(filename)==0) {
            System.out.print(".");
            String p = brain.Pattern(i);
            String n = Globals.getBotName();
            n = Substituter.normalize(n);
            if (p.indexOf(n) >=0) p = Substituter.replace(n,"<name/>",p);
            sf.appendLine("<category>");
            if (brain.ThatPattern(i).compareTo("*") == 0) {
                sf.appendLine(
                "<pattern>"+p+"</pattern>\n"+
                "<template>"+brain.Template(i)+"</template>");
            }
            else {
              sf.appendLine(
              "<pattern>"+p+"</pattern>"+
              "<that>"+brain.ThatPattern(i)+"</that>\n"+
              "<template>"+brain.Template(i)+"</template>");
            } // if-else
            sf.appendLine("</category>");
	  } // if 
	} // for i (over brain)
      } // for k (over sis)
    if (topic.compareTo("*") != 0) 
      sf.appendLine("</topic>");
    } // for w (over topicset)
    sf.appendLine("</alice>");
    sf.close();
    System.out.println();
    System.out.println("Saved: "+filename);
  } // for u (over fileset)
} // method save_robot

public static void save_robot_as(String fname) {
  StringSet topicset = new StringSet();
  // collect all the topics into a set:
  for (int i = 0; i < brain.size(); i++) {
    if (!topicset.contains(brain.TopicPattern(i))) {
      topicset.add(brain.TopicPattern(i));
    }
  }
  SortedIntSet sis = new SortedIntSet();
  for (int i = 0; i < brain.size(); i++) {
      if (brain.Target(i) != null)
        sis.add(brain.Target(i).getTotal());             
  }
  sis.add(0); // zero activation
  int cnt = 0;
  StringFile sf = new StringFile();
  sf.delete(fname);
  sf.open(fname, "w");
  sf.appendLine("<alice>");
  sf.appendLine("<-- Open source software (c) 2000 Dr. Richard S. Wallace -->");
  sf.appendLine(" -- This program is free software; you can redistribute it and/or modify ");
  sf.appendLine(" -- it under the terms of the GNU General Public License as published by the Free Software Foundation -->");
    // loop over topics:
    for (int w = 0; w < topicset.size(); w++) {
      String topic = (String)(topicset.elementAt(w)); 
      if (topic.compareTo("*") != 0) 
        sf.appendLine("<topic name=\""+topic+"\">");
      // loop over activation counts:
      for (int k = sis.size()-1; k >= 0; k--) {
        //  System.out.println(sis.elementAt(k));
        int val = ((Integer)(sis.elementAt(k))).intValue();
        // loop over the brain elements:
        for (int i = 0; i < brain.size(); i++) {
          if (brain.Target(i).getTotal() == val &&
              brain.TopicPattern(i).compareTo(topic)==0) {
            String p = brain.Pattern(i);
            String n = Globals.getBotName();
            n = Substituter.normalize(n);
            if (p.indexOf(n) >=0) p = Substituter.replace(n,"<name/>",p);
            sf.appendLine("<category>");
            if (brain.ThatPattern(i).compareTo("*") == 0) {
                sf.appendLine(
                "<pattern>"+p+"</pattern>\n"+
                "<template>"+brain.Template(i)+"</template>");
            }
            else {
              sf.appendLine(
              "<pattern>"+p+"</pattern>"+
              "<that>"+brain.ThatPattern(i)+"</that>\n"+
              "<template>"+brain.Template(i)+"</template>");
            } // if-else
            sf.appendLine("</category>");
	  } // if 
	} // for i (over brain)
      } // for k (over sis)
    if (topic.compareTo("*") != 0) 
      sf.appendLine("</topic>");
    } // for w (over topics)
  sf.appendLine("</alice>");
  sf.close();
} // end of method save_robot_as()

private Classifier() {
}

} // end of class Classifier


// Tag Processor interface is defined for use by the Parser
// (Specifically, to simplify the code for the method pfkh())
// Each kind of AIML tag pair gets processed in roughly the
// same way:
// The outer loop has the form of "Parser.process_tag" to
// detect occurances of the tag in the template and to
// extract a substring between the start and end tags.
// The inner loop has the form of a TagProcessor process_aiml method
// that causes some special side-effect depending on the tag.
// This interface and classes could all be inner to Parser.

interface TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p);
}

// Each tag pair has its own TagProcessor class:

class RandomProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    Vector v = new Vector();
    while (mid.indexOf("<li>") >= 0) {
      mid = new String(mid.substring(mid.indexOf("<li>")+"<li>".length(), mid.length()));
      String choice;
      choice = mid.substring(0,  mid.indexOf("</li>"));
      choice = p.pfkh(ip, new String(choice), star);
      if (choice.length() > 0) {
         v.addElement(new String(choice));
      }
    }
    // select a random element of v:
    // public static Random RNG = new Random();
    double r = Classifier.RNG.nextDouble();
    int random_amt = (int)((double)(v.size())*r);        
    mid = (String)(v.elementAt(random_amt));
    return mid;
  }
} // class RandomProcessor

class LocationProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    Classifier.set_location(ip, Substituter.formal(mid));
    return (Substituter.formal(mid));
  }
} // class LocationProcessor

class AgeProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    Classifier.set_age(ip, mid);
    return (mid);
  } 
} // class AgeProcessor

class NameProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    mid = Substituter.formal(mid);
    Classifier.setname(ip, mid);
    return (mid);
  }
} // class NameProcessor

class HeProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    mid = Substituter.formal(mid);
    Classifier.set_he(ip, mid);
    return ("he");
  }
} // class HeProcessor

class SheProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    mid = Substituter.formal(mid);
    Classifier.set_she(ip, mid);
    return ("she");
  }
} // class SheProcessor



class TheyProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    mid = Substituter.formal(mid);
    Classifier.set_they(ip, mid);
    return ("they");
  }
} // class TheyProcessor

class ItProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    Classifier.set_it(ip, mid);
    return ("it");
  }
} // class ItProcessor

class GossipProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    StringFile sf = new StringFile();     
    sf.appendLine(Globals.getDataDir()+"gossip.txt",mid);
    return (mid);
  }
} // class GossipProcessor


// ThinkProcessor just "nullifies" the tag.

class ThinkProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    return ("");
  }
} // class ThinkProcessor

class TopicProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
      mid = p.pfkh(ip, new String(mid), star);
      mid = Substituter.formal(mid);
      Classifier.settopic(ip, mid);
      return (mid);
  }
} // class TopicProcessor

class SraiProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
      if (p.depth < 128) {
        p.depth += 1; 
        mid = p.pfkh(ip, new String(mid), star);
        mid = Substituter.normalize(mid);
        mid = Classifier.respond(mid, ip);
      }
      else mid = "Infinite Loop";
      return (mid);
  } //
} // class SraiProcessor

class SystemProcessor implements TagProcessor {
  public String process_aiml(String ip, String mid, String star, Parser p) {
    mid = p.pfkh(ip, new String(mid), star);
    String output = "";
    try {
      String command = mid;
      Process child = Runtime.getRuntime().exec(command);
      InputStream in = child.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line;
      while ((line = br.readLine()) != null) {
         output = output+line+"<br>\n";
      }
      mid = output;
      in.close();
     }
     catch (IOException e) {
       System.out.println("Cannot execute "+mid+" "+e);
     }
     return mid;
  }
}

// CustomTagProcessor

class CustomTagProcessor implements TagProcessor {

  String pname;
  
  CustomTagProcessor(String pname) {
    this.pname = pname;
  }

  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);
    Classifier.set_property(pname, ip, mid);
    String deflt = Classifier.predicatemap.getProperty(pname);
    if (deflt.indexOf("*") >=0)  
      return(mid);
    else return deflt;
  }
} // class CustomTagProcessor

// the default is the map value of predicatemap

/*
// Plugin Processor by Andrew Potgeiter:

class PluginProcessor implements TagProcessor {

  String pluginName; // This is the name of the plugin we wish to run

  //construct with the plugin
  PluginProcessor(String pluginName) {
    this.pluginName = pluginName;

    System.out.println("---Finished constructor. plugin = " + pluginName);
  }

  public String process_aiml(String ip, String mid, String star, Parser p) {
    // recrusve call to pfkh parser:
    mid = p.pfkh(ip, new String(mid), star);

    // get the plugin
    AlicePlugin pl = AlicePluginLoader.getPlugin(pluginName);
    return pl.fire(mid);
  }
} // end class PluginProcessor


*/
