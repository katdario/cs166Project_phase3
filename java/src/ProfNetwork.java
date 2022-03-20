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
	public static int conn_level;
	public static int num_friends;
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
            System.out.print (rs.getString(i).trim() + "\t");
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
//	String query = String.format("SELECT * FROM MESSAGE");
    //    MESSAGES_SIZE = esql.executeQuery(query);
        //MESSAGES_SIZE = esql.executeQueryAndPrintResult(query);
	conn_level = 0;
	//System.out.println("messages size = " + MESSAGES_SIZE); 
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
			String query = String.format("SELECT * FROM CONNECTION_USR WHERE (userId = '%s' OR connectionId = '%s') AND status = 'Accept'", authorisedUser, authorisedUser);
                	num_friends = esql.executeQuery(query);

			System.out.println("MAIN MENU");
	                System.out.println("---------");
	                System.out.println("0. View My Info");
			System.out.println("1. Go to Friend List");
	                System.out.println("2. Update Profile");
	                System.out.println("3. Write a new message");
	                //System.out.println("4. Send Friend Request");		
			System.out.println(".........................");
			if(num_friends < 5)
		        System.out.println("4. Send Connection Request");
	                System.out.println("5. Search People");
	                System.out.println("6. Change Password");
	                System.out.println("7. View Connection Requests");
	                System.out.println("8. View Messages");
	                //System.out.println("9. View My Info");
			System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql, authorisedUser, authorisedUser); break;
                   case 2: UpdateProfile(esql); break;
                   case 3: NewMessage(esql, authorisedUser); break;
                   case 4:
			if(num_friends < 5) 
				SendRequest(esql, authorisedUser); 
			else
				System.out.println("You have at least 5 friends. To add more, you must add through your friend's list");
			break;
//                 ====================================
                   case 5: SearchPeople(esql); break;
                   case 6: ChangePassword(esql, authorisedUser); break;
                   case 7: ViewRequests(esql, authorisedUser); break;
                   case 8: ViewMessages(esql, authorisedUser); break;
		case 0: viewMyInfo(esql, authorisedUser); break;
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
    public static void FriendList(ProfNetwork esql, String id, String myId){
        //TO DO: ALLOW USER TO BROWSE LIST OF FRIENDS
		try{
			boolean answer = true;
			while(answer){
				System.out.println("\n\tFriends:\n\t--------");
				//grab friends that accepted my connection request
				String query1 = String.format("SELECT U.userId, U.name FROM CONNECTION_USR C, USR U WHERE C.connectionId = U.userId AND C.userId = '%s' AND C.status = 'Accept'", id);
				//grab friends where I accepted their connection request
				String query2 = String.format("SELECT U.userId, U.name FROM CONNECTION_USR C, USR U WHERE C.userId = U.userId AND C.connectionId = '%s' AND C.status = 'Accept'", id);
				int friendFound = esql.executeQueryAndPrintResult(query1);
				friendFound += esql.executeQueryAndPrintResult(query2);
				//num_friends = friendFound;
				if (friendFound > 0){
					//System.out.print("\tEnter a friend's name to view their profile");
					System.out.println("OPTIONS:\n--------");
					System.out.println("1. View a friend's profile\n2. Back to Previous Page");
					switch(readChoice()){
						case 1: 
							System.out.print("Enter User id: ");
							String friend_id = in.readLine();
							//query1 = String.format("SELECT");
							ViewProfile(esql, friend_id, myId);
							break;
						case 2:
							answer = false;
							break;
						default: System.out.println("Invalid choice!"); break;
					}
				}
				else{
					System.out.println("\tFriend not found!");
				}
				
			}
		}
		catch(Exception e){
			System.err.println(e.getMessage());	
		}
    }
	public static void ViewProfile(ProfNetwork esql, String id, String myId){
		try{
			boolean view=true;
			conn_level++;			
			while(view){
				String query = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' AND connectionId = '%s' AND status = 'Accept'",id,myId);
				int conn = esql.executeQuery(query);
				query = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' AND connectionId = '%s' AND status = 'Accept'",myId,id);
                                conn += esql.executeQuery(query);
				
				//checks if the owner of profile is connected to authorized user
				if(conn > 0){
					conn_level = 1;
					System.out.println("\n\nPROFILE VIEW\tlevel = " + conn_level + "\n");
					System.out.println("Personal Info:\n----------------");
					query = String.format("SELECT userId,name, dateOfBirth FROM USR WHERE userId = '%s'", id);
					esql.executeQueryAndPrintResult(query);
					System.out.println("\nEducation Details:\n----------------");
					query = String.format("SELECT institutionName,major,degree,startdate,enddate FROM EDUCATIONAL_DETAILS WHERE userId = '%s'",id);
					esql.executeQueryAndPrintResult(query);
					System.out.println("\nWork Experienc:\n----------------");
					query = String.format("SELECT company,role,location,startDate,endDate FROM WORK_EXPR WHERE userId = '%s'",id);
					esql.executeQueryAndPrintResult(query);
					
					System.out.println("\n\tOPTIONS:\n\t-------");
					System.out.println("1. View Friends list\n2. Send Message\n3. Back");
					switch(readChoice()){
						case 1: FriendList(esql, id, myId); break;
						case 2: NewMessageFromProfile(esql, myId, id); break;
						case 3: view = false; break;
						default: System.out.println("Invalid choice!"); break;
					}
					
				}
				else{
					System.out.println("\n\nPROFILE VIEW\tlevel = " + conn_level + "\n");
                                        System.out.println("Personal Info:\n----------------");
                                        query = String.format("SELECT userId,name FROM USR WHERE userId = '%s'", id);
                                        esql.executeQueryAndPrintResult(query);
                                        System.out.println("\nEducation Details:\n----------------");
                                        query = String.format("SELECT institutionName,major,degree,startdate,enddate FROM EDUCATIONAL_DETAILS WHERE userId = '%s'",id);
                                        esql.executeQueryAndPrintResult(query);
                                        System.out.println("\nWork Experienc:\n----------------");
                                        query = String.format("SELECT company,role,location,startDate,endDate FROM WORK_EXPR WHERE userId = '%s'",id);
                                        esql.executeQueryAndPrintResult(query);
                                	
					//counts the number of friends
					query = String.format("SELECT COUNT(*) FROM CONNECTION_USR WHERE (userId = '%s' OR connectionId = '%s') AND status = 'Accept'", myId, myId);
					//num_friends = esql.executeQuery(query);
				
					List<List<String>> num = esql.executeQueryAndReturnResult(query);
					num_friends = Integer.parseInt(num.get(0).get(0)) ;		
					//num_friends = Integer.parseInt(num.get(1));
                                       	System.out.println("\n\tOPTIONS:\n\t-------");
                                       	System.out.println("1. View Friends list\n2. Send Message\n3. Send Connection request\n4. Back");
					switch(readChoice()){
                                               	case 1: FriendList(esql, id, myId); break;
                                               	case 2: NewMessageFromProfile(esql, myId, id); break;
                                               	case 3:
							System.out.println("\n\nnumber of friends: " + num_friends); 
							if(num_friends < 5 || (num_friends >= 5 && conn_level <=3))
								SendRequest(esql, id);
							else
								System.out.println("ERROR. Connection level limit surpassed");
							break;
						case 4: view = false; break;
                                               	default: System.out.println("Invalid choice!"); break;
                                       	}
					
				}
			}
			conn_level--;
		}
		catch(Exception e){
            System.err.println(e.getMessage());
        }
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



    public static void NewMessageFromProfile(ProfNetwork esql, String userlogin, String rec_id){
        try{
                //System.out.print("\tWho would you like to message?\n\tEnter receiver's user id: ");
                //String rec_id = in.readLine();
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
                        query = String.format("INSERT INTO MESSAGE ( senderId, receiverId,contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", userlogin, rec_id, msg_content.trim(), new Timestamp(System.currentTimeMillis()) , del_stat, deliv_stat);
                }
                else{
                        System.out.println("\tSaving message as Draft");
                        query = String.format("INSERT INTO MESSAGE ( senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", userlogin, rec_id, msg_content.trim(), new Timestamp(System.currentTimeMillis()), del_stat, draft_stat );
                }
		esql.executeUpdate(query);
                System.out.println("Sent!");
        }

        catch(Exception e){
        	System.err.println (e.getMessage ());
      	}
}



// connection status:
// 	Accept = userid and connectionid are already connected
// 	Request = userid has requested a connection with connectionid
// 	Reject = there is no connection between userid and connectionid
    public static void SendRequest(ProfNetwork esql, String myId){
	try{
		System.out.println("\tWho would you like to connect with?");
		System.out.print("\tEnter user id: ");
		String conn_id = in.readLine();
		String query = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' AND connectionId = '%s' AND status = 'Reject'", myId, conn_id);
		int queryFound = esql.executeQuery(query);
		//if connection status is in connection table and status=Reject
		if (queryFound>0){
			query = String.format("UPDATE CONNECTION_USR SET status = 'Request' WHERE userId = '%s' AND connectionId = '%s'", myId, conn_id);
			System.out.println("\tConnection Request Sent to " + conn_id);
		}
		else{
			query = String.format("SELECT * FROM CONNECTION_USR WHERE userId = '%s' AND connectionId = '%s'", myId, conn_id);
                	queryFound = esql.executeQuery(query);
			if(queryFound > 0) //if connection is in connection table but just not Reject
				System.out.println("\tConnection Request Failed!\nYou either already sent a Connection Request and are waiting on a response\nor the user is already connected with you!");	
			else{	//connection status doesn't exist
				//query = String.format("UPDATE CONNECTION_USR SET status = 'Request' VALUES  userId = '%s' AND connectionId = '%s'", myId, conn_id);
                        	query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s', '%s', 'Request')",myId, conn_id);
				esql.executeUpdate(query);
				System.out.println("\tConnection Request Sent to " + conn_id);
			}
		}
	}
	catch(Exception e){
        	System.err.println (e.getMessage ());
	}      
    }
    public static void SearchPeople(ProfNetwork esql){
	try{
		System.out.print("\tEnter a name to search: ");
	  	String name = in.readLine();
	  	String percent = "%";
		String userName = String.format("%s%s%s", percent, name, percent);
		String query = String.format("SELECT userId,name FROM USR WHERE name LIKE '%s'", userName);
       	  	esql.executeQueryAndPrintResult(query);
	} 
	catch(Exception e){
         	System.err.println (e.getMessage ());
        // 	return null;
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
			System.err.println (e.getMessage ());
                }
    }

	public static void ViewRequests(ProfNetwork esql, String myId){
    	try{
			String query = String.format("SELECT userId, status FROM CONNECTION_USR WHERE (connectionId = '%s') AND status = 'Request'", myId, myId);
			int numRequests = esql.executeQueryAndPrintResult(query);
			if(numRequests > 0){
				System.out.print("\nWould you like to Accept or Reject a request? [A]ccept, [R]eject, e[X]it: ");
				String input = in.readLine();
				String id;
				if(input.equals("A")){
					System.out.print("Please enter the user ID you'd like to connect with: ");
					id = in.readLine();
					query = String.format("UPDATE CONNECTION_USR SET status = 'Accept' WHERE userId = '%s' AND connectionId = '%s'", id, myId);
					esql.executeQuery(query);
					System.out.println("\n\tConnection Accepted!");	
					num_friends++;		
				}
				else if(input.equals("R")){
			                System.out.print("Please enter the user ID whose request you'd like to reject: ");
			                id = in.readLine();
			                query = String.format("UPDATE CONNECTION_USR SET status = 'Reject' WHERE userId = '%s' AND connectionId = '%s'", id, myId);  
			                esql.executeQuery(query);
			                System.out.println("\n\tConnection Rejected!");         
            			}
			}
			else{
				System.out.println("\n\tYou currently do not have any pending requests.");
			}
			
		}
		catch(Exception e){
			System.err.println(e.getMessage() );
		}       
    }

//Shows a list of messages not deleted by user
//
//deleteStatus:
// 0 = deleted by both
// 1 = deleted by sender
// 2 = deleted by receiver
// 3 = NOT deleted by either
    public static void ViewMessages(ProfNetwork esql, String userid){
    	try{
			boolean view = true;
			String delete_choice;
			while(view){
				String query = String.format("SELECT msgId,receiverId,senderId, deleteStatus FROM MESSAGE WHERE (receiverId = '%s' AND (deleteStatus != 2 AND deleteStatus != 0)) OR (senderId = '%s' AND (deleteStatus != 0 AND deleteStatus != 1))", userid.trim(), userid);			
				esql.executeQueryAndPrintResult(query);
				System.out.println("\nWould you like to open a message?");
				System.out.println("OPTIONS:\n--------");
				System.out.println("1. Open a message\n2. Back to menu");
				//System.out.print("\tChoose: ");
				switch(readChoice()){
					case 1: System.out.print("Please enter msgId: ");
							String message_id = in.readLine();
							OpenMessage(esql, userid, message_id);
							break;
					case 2: view = false; break;
					default: System.out.println("Invalid choice!"); break;
				}			

			}
			
	}
		catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}    
    public static void OpenMessage(ProfNetwork esql, String userid, String message_id){
		try{
			String query = String.format("SELECT contents FROM MESSAGE WHERE msgId = '%s'", message_id);
            esql.executeQueryAndPrintResult(query);
			
			//checks if i'm the sender of the message
			query = String.format("SELECT * FROM MESSAGE WHERE msgId = '%s' AND senderId = '%s'", message_id, userid);
			int isSender = esql.executeQuery(query);
			System.out.println("isSender = " + isSender);
			
            System.out.println("\nOPTIONS:\n1. Delete this message\n2. Go back to Inbox");
            switch(readChoice()){
                case 1: 
					if(isSender > 0)
						query = String.format("UPDATE MESSAGE SET deleteStatus = 1 WHERE msgId = '%s'", message_id);
					else
						query = String.format("UPDATE MESSAGE SET deleteStatus = 2 WHERE msgId = '%s'", message_id);
					esql.executeQuery(query);
					System.out.println("Message deleted!");
					break;
                case 2: break;
                default: break;
            }
		}
		catch(Exception e){
            System.err.println (e.getMessage ());
        }
	}
	public static void viewMyInfo(ProfNetwork esql, String myId){
		try{
			System.out.println("Personal Info:\n----------------");
                	String query = String.format("SELECT name, dateOfBirth FROM USR WHERE userId = '%s'", myId);
			esql.executeQueryAndPrintResult(query);
                	System.out.println("\nEducation Details:\n----------------");
                	query = String.format("SELECT institutionName,major,degree,startdate,enddate FROM EDUCATIONAL_DETAILS WHERE userId = '%s'",myId);
                	esql.executeQueryAndPrintResult(query);
                	System.out.println("\nWork Experienc:\n----------------");
                	query = String.format("SELECT company,role,location,startDate,endDate FROM WORK_EXPR WHERE userId = '%s'",myId);
                	esql.executeQueryAndPrintResult(query);
		}
		catch(Exception e){
            		System.err.println (e.getMessage ());
        	}
	}

}//end ProfNetwork
