package db.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import data.Author;

public class SQLAccessLayer {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://rdc04.uits.iu.edu:3264/ACM2015";

	//  Database credentials
	static final String USERNAME = "summary_proj";
	static final String PASSWORD = "xpa25Rd";

	public List<Author> getUniqueAuthorList()
	{
		List<Author> authorList = new ArrayList<Author>();
		
		String query = "select distinct first_name, last_name from paper_author;";
		System.out.println("Executing query:" + query);
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);

			//STEP 4: Execute a query
			stmt = conn.createStatement();

			String sql;
			sql = query;
			ResultSet rs = stmt.executeQuery(sql);
			long authorCount = 1;
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String authorID = "a_" + authorCount;
				authorCount++;
				
				authorList.add(new Author(authorID, firstName, lastName));

			}
			//STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
		//System.out.println("Goodbye!");
		return authorList;
	}
	
	public List<String> getListOfAuthorIDs(String authorFirstName, String authorLastName)
	{
		
		assert authorFirstName != null & !authorFirstName.isEmpty(): "Invalid Author First Name" + authorFirstName;
		assert authorLastName != null & !authorLastName.isEmpty(): "Invalid Author Last Name" + authorFirstName;

		String query = "select id from paper_author where first_name = '%s' and last_name='%s'";
		List<String> authorIDs = new ArrayList<String>();

		query = String.format(query, authorFirstName, authorLastName);
		System.out.println("Executing query:" + query);
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();



			//STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);

			//STEP 4: Execute a query
			stmt = conn.createStatement();

			String sql;
			sql = query;
			ResultSet rs = stmt.executeQuery(sql);

			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				int id  = rs.getInt("id");

				authorIDs.add(String.valueOf(id));
				//Display values
				//System.out.println("ID: " + id);

			}
			//STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
		//System.out.println("Goodbye!");
		return authorIDs;
	}
	
	

}
