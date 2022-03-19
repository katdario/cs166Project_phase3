/* JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
         if(outputHeader){
            for(int i = 1; i <= numCol; i++){
                System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
        Statement stmt = this._connection.createStatement ();

        ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
        if (rs.next())
                return rs.getInt(1);
        return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Go to Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
		System.out.println(".........................");
                System.out.println("4. Send Connection Request");
                System.out.println("5. Search People");
                System.out.println("6. Change Password");
                System.out.println("7. View Connection Requests");
                System.out.println("8. View Messages");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: NewMessage(esql); break;
                   case 4: SendRequest(esql); break;
                   case 5: SearchPeople(esql); break;
                   case 6: ChangePassword(esql, authorisedUser); break;
                   case 7: ViewRequests(esql); break;
                   case 8: ViewMessages(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface                         \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login/id: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();
	System.out.print("\tEnter user name: ");
	String name = in.readLine();
	System.out.print("\tEnter user\'s birthday [MM/DD/YY]: ");
	String dob = in.readLine();

         //Creating empty contact\block lists for a user
         String query = String.format("INSERT INTO USR (userId, password, email, name, dateOfBirth) VALUES ('%s','%s','%s','%s','%s')", login, password, email, name, dob);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
                return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here    
    public static void FriendList(ProfNetwork esql){
        //TODO: ALLOW USER TO BROWSE LIST OF FRIENDS
	try{
		boolean answer = true;
		while(answer){
			System.out.print("\tFriends");
			String query1 = String.format("SELECT U.name FROM CONNECTION_USR C, USR U WHERE C.status = 'Accept' AND C.userId = U.userId");//grab list of friends
			int friendFound = esql.executeQueryAndPrintResult(query1);
			if (friendFound > 0){
				System.out.print("\tEnter a friend's name to view their profile");
				String userName = in.readLine();
				String query2 = String.format("SELECT U.userId FROM USR U. CONNECTION_USR WHERE C.status = 'Accept' AND  U.name = '%s'", userName);//grab userId of
																				     //specified friend
				int userFound = esql.executeQuery(query2);
				if(userFound>0){
					ViewProfile(esql, userName);
				}
			}else{
				System.out.println("\tFriend not found!");
			}
			System.out.println("\tWould you like to keep looking through your friends list?"+
					"\n1. Yes \n2. No");
			switch(readChoice()){
                	    case 1: break;
                	    case 2: answer=false; break;
                	    default: System.out.println("Unrecognized choice!"); break;
                	}
		}
    	}catch(Exception e){
		System.err.println(e.getMessage());	
	}
    }
    public static void ViewProfile(ProfNetwork esql, String userName){
		System.out.println(userName);
		String companyQuery = String.format("SELECT company, role, location FROM WORK_EXPR  WHERE name = '$s'", userName);                                
		String educationQuery = String.format("SELECT institutionName, major, degree, startDate, endDate FROM EDUCATIONAL_DETAILS WHERE name = '$s'", userName);
                System.out.println("Work History");
                esql.executeQueryAndPrintResult(companyQuery);
                System.out.println("Education History");
                esql.executeQueryAndPrintResult(educationQuery);
                System.out.println("Would you like to send a connection request to this user?"+
				"\n1. Yes \n2.No");
		switch(readChoice()){
		    case 1: SendRequest(esql); break;
		    case 2: break;
		    default: System.out.println("Unrecognized choice!"); break;
		}
			
    }
    public static void UpdateProfile(ProfNetwork esql, String authorizedUser){
        System.out.println("\tWhat would you like to update/change?");
	System.out.println("\t1. Name");
	System.out.println("\t2. Add Work Experience");
	System.out.println("\t3. Add Education Experience");
	System.out.println("\t4. Delete Work Experience");
	System.out.println("\t5. Delete Education Experience");
	switch(readChoice()){
                    case 1:
			System.out.println("Enter your new name");
			String newName = in.readLine();
			String query = String.format("UPDATE USR SET name = '%s' WHERE userId = '%s'", newName, authorizedUser);
			System.out.println("Username Updated!");			  
			break;
                    case 2:
			System.out.println("Enter your company's name:");
			String nameW = in.readLine();
			System.out.println("Enter your role:");
			String roleW = in.readLine();
			System.out.println("Enter the location:");
			String location = in.readLine();
			System.out.println("Enter the start date (MM/DD/YYYY):");
			String startDateW = in.readLine();
                        String[] valuesW1 = startDateW.split("/");
                        int dayW1 = Integer.parseInt(valuesW1[0]);
                        int monthW1 = Integer.parseInt(valuesW1[1]);
                        int yearW1 = Integer.parseInt(valuesW1[2]);
                        Date startDatesW = new Date(yearW1, monthW1, dayW1);	
			System.out.println("Enter the end date (MM/DD/YYYY):");
			String endDateW = in.readLine();
                        String[] valuesW2 = endDateW.split("/");
                        int dayW2 = Integer.parseInt(valuesW2[0]);
                        int monthW2 = Integer.parseInt(valuesW2[1]);
                        int yearW2 = Integer.parseInt(valuesW2[2]);
                        Date endDatesW = new Date(yearW2, monthW2, dayW1); 
			String queryW = String.format("INSERT INTO WORK_EXPR (userId, company, role, location, startDate, endDate) VALUES ('%s', '%s', '%s', '%s', %s, %s)", authorizedUser, nameW, roleW, location, startDatesW, endDatesW);
			int updateW = esql.executeQuery(query);
			if (updateW>0){
				System.out.println("Work Experience updated!");
			}else{
				System.out.println("Update failed!");
			}
			break;
		    case 3: 
			System.out.println("Enter your institution's name:");
                        String nameE = in.readLine();
                        System.out.println("Enter your major:");
                        String roleE = in.readLine();
                        System.out.println("Enter your degree:");
                        String degree = in.readLine();
                        System.out.println("Enter the start date (MM/DD/YYYY):");
                        String startDateE = in.readLine();
			String[] valuesE1 = startDateE.split("/");
			int dayE1 = Integer.parseInt(valuesE1[0]);
			int monthE1 = Integer.parseInt(valuesE1[1]);
			int yearE1 = Integer.parseInt(valuesE1[2]);
                        Date startDatesE = new Date(yearE1, monthE1, dayE1);                                        
                        System.out.println("Enter the end date (MM/DD/YYYY):");
			String endDateE = in.readLine();
                        String[] valuesE2 = endDateE.split("/");
                        int dayE2 = Integer.parseInt(valuesE2[0]);
                        int monthE2 = Integer.parseInt(valuesE2[1]);
                        int yearE2 = Integer.parseInt(valuesE2[2]);
                        Date endDatesE = new Date(yearE2, monthE2, dayE2);
                        String queryE = String.format("INSERT INTO EDUCATIONAL_DETAILS (userId, institutionName, major, degree, startDate, endDate) VALUES ('%s', '%s', '%s', '%s', %s, %s)", authorizedUser, nameE, roleE, degree, startDatesE, endDatesE );
			int updateE = esql.executeQuery(query);
                        if (updateE>0){
                                System.out.println("Education Experience updated!");
                        }else{
                                System.out.println("Update failed!");
                        }
                        break;
		    case 4:
			System.out.println("Enter name of the company that you would like to delete your work experience for:");
			String nameWD = in.readLine();
			String queryWD = String.format("DELETE FROM WORK_EXPR WHERE company = '%s'", nameWD);
			int updateWD = esql.executeQuery(queryWD);
                        if (updateWD>0){
                                System.out.println("Work Experience updated!");
                        }else{
                                System.out.println("Update failed!");
                        }
                        break;
		    case 5:
			System.out.println("Enter name of the institution that you would like to delete your education experience for:");
                        String nameED = in.readLine();
                        String queryED = String.format("DELETE FROM EDUCATIONAL_DETAILS WHERE institutionName = '%s'", nameED);
                        int updateED = esql.executeQuery(queryED);
                        if (updateED>0){
                                System.out.println("Education Experience updated!");
                        }else{
                                System.out.println("Update failed!");
                        }
                        break;
                    default: System.out.println("Unrecognized choice!"); break;
                }
    }
    public static void NewMessage(ProfNetwork esql){
        try{
		System.out.println("\tWho would you like to message: ");
        	String userid = in.readLine();
        	System.out.println("\tEnter your message here: ");
        	String msg_content = in.readLine();
        	System.out.println("\tSending message ...");
        	String query = String.format("");
     	}
	catch(Exception e){
        System.err.println (e.getMessage ());
      }              
    }
    public static void SendRequest(ProfNetwork esql){
	try{
		System.out.println("\tWho would you like to connect with?");
		String userName = in.readLine();
		String query = String.format("SELECT userId FROM USR U, CONNECTION_USR C WHERE U.name = '%s' AND U.userId = C.userId AND C.status = 'Reject'", userName);
		int queryFound = esql.executeQuery(userName);
		if (queryFound>0){
			String makeConnection = String.format("UPDATE CONNECTION_USR SET status = 'Request' WHERE name = %s", userName);
			System.out.println("\tConnection Request Sent to " + userName);
		}
		else{
			System.out.println("\tConnection Request Failed!\nYou either already sent a Connection Request and are waiting on a response\nor the user is already connected with you!");	
		}
	}
	catch(Exception e){
        	System.err.println (e.getMessage ());
	}
    }    
    public static void SearchPeople(ProfNetwork esql){
        	try{
	 		System.out.print("\tEnter a name to search: ");
			String userName = in.readLine(); 
			String query = String.format("SELECT name FROM USR WHERE name = '%s'", userName);
       	  		esql.executeQueryAndPrintResult(query);
		} 
		catch(Exception e){
          	System.err.println (e.getMessage ());
		}
    
    }
    public static void ChangePassword(ProfNetwork esql, String authorizedUser){
                try{
                        System.out.print("\tEnter new password: ");
                        String newPass = in.readLine();
                        String query = String.format("UPDATE USR SET password = '%s' WHERE userId = '%s'", newPass,authorizedUser);
			esql.executeQuery(query);
			System.out.println("Password Updated!");
                }
                catch(Exception e){

                }
    }

    public static void ViewRequests(ProfNetwork esql){
           
    }
    public static void ViewMessages(ProfNetwork esql){
    }    
    

}//end ProfNetwork

