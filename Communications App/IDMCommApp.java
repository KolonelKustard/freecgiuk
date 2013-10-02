/*
 * IDMComm.java
 *
 * Created on 22 December 2000, 13:12
 */

import IDMStuff.*;

/**
 *
 * @author  Kolonel Kustard
 * @version
 */
public class IDMCommApp extends java.awt.Frame {
    
    /** Creates new form IDMComm */
    public IDMCommApp() {
        initComponents ();
        pack ();
        this.setSize(500, 300);
        outPrimary("Welcome to the IDM Communications Util...");
        outPrimary("Please enter the IP address of the machine managing the phone line");
        outPrimary("");
    }
    
    public void outPrimary(String s) {
        output.append(s + "\n");
    }
    
    public void outSecondary(String s) {
        System.out.println(s);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
private void initComponents() {//GEN-BEGIN:initComponents
output = new java.awt.TextArea();
panel2 = new java.awt.Panel();
label2 = new java.awt.Label();
input = new java.awt.TextField();
label3 = new java.awt.Label();
addWindowListener(new java.awt.event.WindowAdapter() {
public void windowClosing(java.awt.event.WindowEvent evt) {
exitForm(evt);
}
}
);

output.setBackground(java.awt.Color.white);
output.setName("output");
output.setFont(new java.awt.Font ("Dialog", 0, 11));
output.setForeground(java.awt.Color.black);

add(output, java.awt.BorderLayout.CENTER);


panel2.setLayout(new java.awt.GridBagLayout());
java.awt.GridBagConstraints gridBagConstraints1;
panel2.setFont(new java.awt.Font ("Dialog", 0, 11));
panel2.setBackground(new java.awt.Color (204, 204, 204));
panel2.setForeground(java.awt.Color.black);

label2.setFont(new java.awt.Font ("Arial", 0, 14));
  label2.setName("label2");
  label2.setBackground(new java.awt.Color (204, 204, 204));
  label2.setForeground(java.awt.Color.black);
  label2.setText("Input:");
  gridBagConstraints1 = new java.awt.GridBagConstraints();
  panel2.add(label2, gridBagConstraints1);
  
  
input.setBackground(java.awt.Color.white);
  input.setName("input");
  input.setFont(new java.awt.Font ("Dialog", 0, 11));
  input.setForeground(java.awt.Color.black);
  input.addActionListener(new java.awt.event.ActionListener() {
  public void actionPerformed(java.awt.event.ActionEvent evt) {
  ipSubmitted(evt);
  }
  }
  );
  gridBagConstraints1 = new java.awt.GridBagConstraints();
  gridBagConstraints1.ipadx = 300;
  panel2.add(input, gridBagConstraints1);
  
  
add(panel2, java.awt.BorderLayout.SOUTH);


label3.setFont(new java.awt.Font ("Arial", 0, 18));
label3.setName("label3");
label3.setBackground(new java.awt.Color (204, 204, 204));
label3.setForeground(java.awt.Color.black);
label3.setText("IDM Communications Util");
label3.setAlignment(java.awt.Label.CENTER);

add(label3, java.awt.BorderLayout.NORTH);

}//GEN-END:initComponents

  private void ipSubmitted(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipSubmitted
      input.setEnabled(false);
      outPrimary("Using IP address: " + input.getText());
      new IDMComm(input.getText(), this);
  }//GEN-LAST:event_ipSubmitted
  
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit (0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        new IDMCommApp ().show ();
    }
    
    
// Variables declaration - do not modify//GEN-BEGIN:variables
private java.awt.TextArea output;
private java.awt.Panel panel2;
private java.awt.Label label2;
private java.awt.TextField input;
private java.awt.Label label3;
// End of variables declaration//GEN-END:variables

}
