/*te JAVA User Interface
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
import java.sql.Timestamp;
//public class Globals{
//	public static int MESSAGES_SIZE = 0;
//}

//public static int MESSAGES_SIZE;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {
	public static int MESSAGES_SIZE;
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
    //  int MESSAGES_SIZE;
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
	String query = String.format("SELECT * FROM MESSAGE");
        MESSAGES_SIZE = esql.executeQuery(query);
        //MESSAGES_SIZE = esql.executeQueryAndPrintResult(query);
	System.out.println("messages size = " + MESSAGES_SIZE); 
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
              	//String query = String.format("SELECT * FROM MESSAGES WHERE senderId = '%s' OR receiverId = '%s'", authorisedUser, authorisedUser);       
		//MESSAGES_SIZE = esql.executeQuery(query);
		while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Go to Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");		
		System.out.println(".........................");
                System.out.println("4. Send Connection Request");
                System.out.println("5. Search People");
                System.out.println("6. Change Password");
                System.out.println("7. View Connection Requests");
                System.out.println("8. View Messages");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql); break;
                   case 2: UpdateProfile(esql); break;
                   case 3: NewMessage(esql, authorisedUser); break;
                   case 4: SendRequest(esql); break;
//                 ====================================
                   case 5: SearchPeople(esql); break;
                   case 6: ChangePassword(esql, authorisedUser); break;
                   case 7: ViewRequests(esql); break;
                   case 8: ViewMessages(esql, authorisedUser); break;
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
         if (userNum > 0){
                //query = String.format("SELECT * FROM MESSAGE");
		//MESSAGES_SIZE = esql.executeQuery(query);
		//System.out.println("messages size = " + MESSAGES_SIZE);
		return login;
         }
	System.out.println("Invalid credentials.");
	return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

/*
 *
 *SearchPeople(esql); break;
                   case 6: ChangePassword(esql); break;
                   case 7: ViewRequests(esql); break;
                   case 8: ViewMessages(esql); break;
                   case 9: usermenu = false; break;
 */
    public static void FriendList(ProfNetwork esql){
        //TODO: ALLOW USER TO BROWSE LIST OF FRIENDS
    }
    public static void UpdateProfile(ProfNetwork esql){
        //TODO: ALLOW USER TO CHANGE NON IMPORTANT PROFILE DETAILS
        //TODO: ALLOW USER TO UPDATE PROFILE        
    }

	//deleteStatus:
	//	0 = deleted by both
	//	1 = deleted only by sender
	//	2 = deleted only by receiver
	//	3 = not deleted by either
	//status:
	//	Failed to Deliver
	//	Read
	//	Delivered
	//	Draft
	//	Sent
    public static void NewMessage(ProfNetwork esql, String userlogin){
        try{
		System.out.print("\tWho would you like to message?\n\tEnter receiver's user id: ");
        	String rec_id = in.readLine();
//		if(rec_id.lastIndexOf("\n") != -1)
//			rec_id = rec_id.substring(0, rec_id.length()-1);
        	System.out.print("\tEnter your message here: \n\t");
        	String msg_content = in.readLine();
		System.out.print("\tWould you like to send now? Y/N: ");
		String want_send = in.readLine();
		String query;
		String del_stat = "3";
		String deliv_stat = "Delivered";
		String draft_stat = "Draft";
		if(want_send.equals("Y")){
        		System.out.println("\tSending message ...");
        		query = String.format("INSERT INTO MESSAGE ( senderId, receiverId,contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", userlogin, rec_id.trim(), msg_content.trim(), new Timestamp(System.currentTimeMillis()) , del_stat, deliv_stat);
		}
		else{
			System.out.println("\tSaving message as Draft");
			query = String.format("INSERT INTO MESSAGE ( senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", userlogin, rec_id.trim(), msg_content.trim(), new Timestamp(System.currentTimeMillis()), del_stat, draft_stat );			
		}
		//MESSAGES_SIZE++;
		//esql.update(query);
		esql.executeUpdate(query);
		System.out.println("Sent!");
	}

	catch(Exception e){
         System.err.println (e.getMessage ());
      }              
    }
    public static void SendRequest(ProfNetwork esql){
           
    }
    public static void SearchPeople(ProfNetwork esql){
    
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
					System.err.println (e.getMessage ());
                }
    }

	public static void ViewRequests(ProfNetwork esql){
           
    	}
    public static void ViewMessages(ProfNetwork esql, String userid){
    	try{
			String query = String.format("SELECT msgId,receiverId,senderId FROM MESSAGE WHERE receiverId = '%s' OR senderId = '%s'", userid.trim(), userid.trim());
			esql.executeQueryAndPrintResult(query);
			System.out.print("Which message would you like to open?\n\tPlease enter the msgId: ");
			String message = in.readLine();
			query = String.format("SELECT contents FROM MESSAGE WHERE msgId = '%s'", message);
			esql.executeQueryAndPrintResult(query);
	}
		catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}    
    

}//end ProfNetwork
