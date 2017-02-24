package db.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import data.Author;
import data.Keyword;
import data.KeywordPaperRelationStore;
import data.Paper;
import data.PaperReferenceRelationStore;
import data.PaperType;
import data.Proceeding;

public class SQLAccessLayer {

	static  String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static  String DB_URL = "jdbc:mysql://rdc04.uits.iu.edu:3264/ACM2015";

	//  Database credentials
	static  String USERNAME = "summary_proj";
	static  String PASSWORD = "xpa25Rd";

	public List<Author> getUniqueAuthorListFromLocal()
	{
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		String DB_URL = "jdbc:mysql://127.0.0.1:3306/acm2015";

		//  Database credentials
		String USERNAME = "root";
		String PASSWORD = "root";

		List<Author> authorList = new ArrayList<Author>();

		String query = "select author_id, first_name, last_name from author;";
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
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String authorID =  rs.getString("author_id");
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
		return authorList;
	}

	public List<Author> getUniqueAuthorList()
	{
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		String DB_URL = "jdbc:mysql://127.0.0.1:3306/acm2015";

		//  Database credentials
		String USERNAME = "root";
		String PASSWORD = "root";

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
		return authorList;
	}

	public List<Paper> getListOfPapers()
	{
		List<Paper> paperList = new ArrayList<Paper>();

		String query = "select DISTINCT article_id, title, article_publication_date from paper;";
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
			long paperCount = 1;
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				long articleId = rs.getLong("article_id");
				String title = rs.getString("title");
				Date publicationDate = dateFormat.parse(rs.getString("article_publication_date"));
				String paperId = "p_" + paperCount;
				//PaperType paperType = PaperType.valueOf(rs.getString("type").toUpperCase());
				Paper paper = new Paper(articleId, paperId, title);
				paper.setPublicationDate(publicationDate);
				//paper.setPaperType(paperType);
				paperList.add(paper);

				paperCount++;
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
		return paperList;
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

	public List<Keyword> getUniqueKeywordList() {
		// TODO Auto-generated method stub

		String query = "select distinct keyword from paper_keyword;";
		List<Keyword> keywordList = new ArrayList<Keyword>();

		query = String.format(query);
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
			int keywordCount = 1;
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String keyword  = rs.getString("keyword");
				Keyword keywordObject = new Keyword(keywordCount, keyword);
				keywordList.add(keywordObject);
				keywordCount++;

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
		return keywordList;
	}

	public List<Paper> getListOfPapersForAuthor(Author author) {
		String DB_URL = "localhost";

		//  Database credentials
		String USERNAME = "root";
		String PASSWORD = "root";
//		
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setServerName(DB_URL);
		
		List<Paper> paperList = new ArrayList<Paper>();

		String query = "select article_id, first_name, last_name, seq_no from acm2015.paper_author where first_name='%s' and last_name='%s';";
		query = String.format(query, author.getFirstName(), author.getLastName());
		System.out.println("Executing query:" + query);
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			//Class.forName("com.mysql.jdbc.Driver").newInstance();

			//STEP 3: Open a connection
			conn = dataSource.getConnection();
			
			
			//STEP 4: Execute a query
			stmt = conn.createStatement();

			String sql;
			sql = query;
			ResultSet rs = stmt.executeQuery(sql);

			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				long articleId = rs.getLong("article_id");
				int seq_no = rs.getInt("seq_no");
				Paper paper = new Paper(articleId, "", "");
				paper.setSeq_no(seq_no);
				paperList.add(paper);

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
		return paperList;
	}

	public KeywordPaperRelationStore getPaperIdKeywordIdRelations() {
		//  Database credentials
		String DB_URL = "localhost";
		String USERNAME = "root";
		String PASSWORD = "root";
//		
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setServerName(DB_URL);
		
		KeywordPaperRelationStore keywordPaperRelationStore = new KeywordPaperRelationStore();

		String query = "select article_id, keyword_id from acm2015.paper_keywordId where keyword_id!=0";
		System.out.println("Executing query:" + query);
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 3: Open a connection
			conn = dataSource.getConnection();

			//STEP 4: Execute a query
			stmt = conn.createStatement();
			String sql;
			sql = query;
			ResultSet rs = stmt.executeQuery(sql);

			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				long articleId = rs.getLong("article_id");
				long keywordId = rs.getLong("keyword_id");
				
				keywordPaperRelationStore.addRelations(articleId, keywordId);

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
		return keywordPaperRelationStore;
	}

	public PaperReferenceRelationStore getPaperAndReferenceRelationStore() {
		
		String DB_URL = "localhost";
		String USERNAME = "root";
		String PASSWORD = "root";

		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setServerName(DB_URL);
		
		PaperReferenceRelationStore paperAndReferenceRelationStore = new PaperReferenceRelationStore();

		String query = "select article_id, ref_obj_id from acm2015.paper_ref where ref_obj_id>0;";
		System.out.println("Executing query:" + query);
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 3: Open a connection
			conn = dataSource.getConnection();

			//STEP 4: Execute a query
			stmt = conn.createStatement();
			String sql;
			sql = query;
			ResultSet rs = stmt.executeQuery(sql);

			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				int articleId = rs.getInt("article_id");
				int referenceArticleId = rs.getInt("ref_obj_id");
				
				paperAndReferenceRelationStore.addRelations(articleId, referenceArticleId);

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
		return paperAndReferenceRelationStore;
	}

	public List<Proceeding> getListOfProceeding() {
		
		String DB_URL = "localhost";
		String USERNAME = "root";
		String PASSWORD = "root";

		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setServerName(DB_URL);
		
		List<Proceeding> proceedingList = new ArrayList<Proceeding>();

		String query = "select * from acm2015.proceeding_series;";
		System.out.println("Executing query:" + query);
		Connection conn = null;
		Statement stmt = null;
		try{

			conn = dataSource.getConnection();

			//STEP 4: Execute a query
			stmt = conn.createStatement();

			String sql;
			sql = query;
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				int proc_id = rs.getInt("proc_id");
				String series_id = rs.getString("series_id");
				String series_title = rs.getString("series_title");
				int year = rs.getInt("year");

				Proceeding proceeding = new Proceeding(proc_id, series_id, series_title, year);
				proceedingList.add(proceeding);
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
		return proceedingList;
	}



}
