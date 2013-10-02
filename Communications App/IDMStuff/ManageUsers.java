/*
 * GetUsers.java
 *
 * Created on 18 December 2000, 22:51
 */

package IDMStuff;

import java.util.*;
import java.sql.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 1
 *
 * The idea of this class is to simply run sequentially through the list of people
 * in the database, and to send back their details (phone number and name primarily).
 *
 * I'll also update the database to acknowledge that they've already been phoned,
 * and not to bother them again (maybe)!
 *
 */
public class ManageUsers extends Object {
    
    public static final String dbURL = "jdbc:HypersonicSQL:DB/FreecgiukDB";  // Absolute path for DB URL
    
    private ResultSet result;
    private IDMComm parent;
    
    
    /** Creates new GetUsers */
    public ManageUsers(IDMComm parent) {
        this.parent = parent;
        
        try {
            // Try to connect to the DB.
            parent.outSecondary("(Users) Opening database connection");
            Class.forName("org.hsql.jdbcDriver");
            Connection conn=DriverManager.getConnection(dbURL,"sa","");
            
            // Check number of users already registered...
            Statement stat=conn.createStatement();
            
            // Get all users data from the database into a resultset in memory.
            try {
                result=stat.executeQuery("SELECT title, firstname, lastname, email, address, country, daytimetel, eveningtel, username, password, dateadded, userip FROM users");
                parent.outSecondary("(Users) Request to database successful.  Users read into memory.");
            }
            catch (SQLException e) {
                System.out.println(e);
                System.exit(1);
            }
            
            // Close connection to the database.  May change this if I decide to update the database at all...
            parent.outSecondary("(Users) Closing database connection");
            conn.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    private String parsePhoneNum(String s) {
        String x = "";
        
        for (int num = 0; num < s.length(); num++) {
            switch (s.charAt(num)) {
                case ' ' :
                    break;
                case '(' :
                    break;
                case ')' :
                    break;
                    default :
                        x += s.charAt(num);
                        break;
            }
        }
        
        return x;
    }
    
    public boolean next() {
        try {
            result.next();
            if (!result.isAfterLast()) {
                return true;
            }
            else {
                parent.outSecondary("(Users) Reached end of user list");
                return false;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
            return false;
        }
    }
    
    public String getPhoneNo() {
        String phoneNum = "";
        String phoneNumUnparsed = "";
        
        try {
            phoneNumUnparsed = result.getString("eveningtel");
            phoneNum = parsePhoneNum(phoneNumUnparsed);
            parent.outSecondary("(Users) Evening Tel: " + phoneNum);
            
            try {
                phoneNum.trim();
                Integer.valueOf(phoneNum);
            }
            catch (NumberFormatException e) {
                phoneNumUnparsed = result.getString("daytimetel");
                phoneNum = parsePhoneNum(phoneNumUnparsed);
                parent.outSecondary("(Users) Daytime Tel: " + phoneNum);
                
                try {
                    phoneNum.trim();
                    Integer.valueOf(phoneNum);
                }
                catch (NumberFormatException e2) {
                    phoneNum = null;
                    parent.outSecondary("(Users) Phone numbers invalid");
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
        }
        
        return phoneNum;
    }
    
    public String getFullName() {
        String fullName = "";
        String title = "";
        String firstName = "";
        String lastName = "";
        
        try {
            title = result.getString("title");
            if (title.equals("Mr")) {
                title = "Mister";
            }
            else if (title.equals("Miss")) {
                title = "Miss";
            }
            else if (title.equals("Mrs")) {
                title = "Mrs";
            }
            else {
                title = "";
            }
            
            firstName = result.getString("firstname");
            lastName = result.getString("lastname");
        }
        catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
        }
        
        fullName = title + " " + firstName + " " + lastName;
        return fullName;
    }
    
}
