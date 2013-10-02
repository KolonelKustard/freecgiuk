package b;

import java.util.*;

class IntSet extends Vector { 
  // override the root class add
  // to add each string only once:
  public void add(int x) {
    Integer s = new Integer(x);
    if (!contains(s)) {
	super.addElement(s);
    }
    else {
    }
  } // add
  public int arb() {
  int a = 0;
    try {                  // changed for 1.1.8 sun jdk
     // was one line, Integer s = (Integer)remove(0);
      Integer s = (Integer) elementAt(0);
      removeElementAt(0);
      a = s.intValue();      
    }
    catch (Exception e) {
      System.out.println("INTSET-ARB: no elements: "+e);
    }
  return a;
  }

} // class IntSet

class SortedIntSet extends IntSet {
    public void add(int x) {
      super.add(x);
      for (int i = size()-2; i >= 0; i--) {
         int u = ((Integer)(elementAt(i))).intValue();
         int v = ((Integer)(elementAt(i+1))).intValue();
         if (u > v) {
           Object temp = elementAt(i);
           setElementAt(elementAt(i+1),i);
           setElementAt(temp,i+1);
	 }
       }
    }
}
      
