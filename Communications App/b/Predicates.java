package b;

import java.util.*;
import java.net.*;
import java.lang.*;
import java.io.*;

// the purpose of this class is to
// replicate the functionality of the
// Java Properties class but overcome the
// major headache of that class: the 
// confusion over store() and save()
// in various JDK versions / releases
// The Predicates class overrides the
// store() method of Properties

public class Predicates extends Properties {

    public void store(FileOutputStream fos, String label) {
      try {      
        Enumeration enum = propertyNames();
        PrintStream ps = new PrintStream(fos);
        while (enum.hasMoreElements()) {
          String pname = (String)(enum.nextElement());
          String pval = getProperty(pname);
          ps.println(pname+"="+pval);
        }
        fos.close();
      }
      catch (Exception e) {
        System.out.println("PREDICATES.STORE: "+e);
      }
    }

    // main method for testing:

    public static void main(String argv[]) {
      PredicateMap pm = new PredicateMap();
      try {
        pm.load(new FileInputStream(Globals.getDataDir()+"predicates.txt"));
      }
      catch (Exception e) {}
    }
} // class Predicate


// Predicatemap is a class
// that maps predicate names to their default values.
// The pmap member contains a map (hashtable)
// from the predicate names to the associated 
// Predicates map.
//
// For a fuller understanding of this class 
// consult Classifier.set_property()
// Classifier.get_property()
// Parser.pfkh() [sections for custom tags]
// and the CustomTagProcessor class.

class PredicateMap extends Predicates {

  Hashtable pmap = new Hashtable();

    // the name of the predicate is the key
    // value in the PM (this).
    // the default value is the map value of
    // the PM (this) itself

public void store () {
    try {
      Enumeration enum = propertyNames();
      while (enum.hasMoreElements()) {
        String pn = (String)(enum.nextElement());
        Predicates ip_map = (Predicates)(pmap.get(pn));
        if (ip_map.size() > 0) // skip empty maps
          ip_map.store(new FileOutputStream(Globals.getDataDir()+"ip_"+pn+".txt"), new Date().toString());
      }
    }
    catch (Exception e) {}
} // PredicateMap.store

  public void load(FileInputStream fis) {
    try {
      super.load(fis);
      Enumeration enum = propertyNames();
      while (enum.hasMoreElements()) {
        String pn = (String)(enum.nextElement());
        Predicates ip_map = new Predicates();
        pmap.put(pn, ip_map);
        try {
          ip_map.load(new FileInputStream(Globals.getDataDir()+"ip_"+pn+".txt"));
	}
        catch (Exception e) { // No predicate file found // 
	} // catch
      } // while
    } // try
    catch (Exception e) {}
  } // PredicateMap.load

} // class PredicateMap

  
  
