package exec;

import java.util.List;

import data.Author;
import data.Keyword;
import data.Paper;
import db.csv.CSVAccessLayer;
import db.neo4j.Neo4jAccessLayer;
import db.sql.SQLAccessLayer;

public class Main {

	public static void main(String[] args) {

		
//		createAuthorNodes();
//		System.out.println("Author Nodes Created.");
//		
//		createPaperNodes();
//		System.out.println("Paper Nodes Created.");
//		
		//createJournalNodes();
		
		//createKeywordNodes();
		
		
	}
	
	
	private static void createKeywordNodes() {
		// TODO Auto-generated method stub
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Keyword> keywordList = sqlAccessLayer.getUniqueKeywordList();
		System.out.println("SQL Data Fetched.");
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addKeywordNodes(keywordList);
		System.out.println("Neo4j added to graph.");
		
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writeKeywordsToCSV(keywordList);
	}


	public static void createAuthorNodes()
	{
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Author> authorList = sqlAccessLayer.getUniqueAuthorList();
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addAuthorNodes(authorList);
		
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writeAuthorsToCSV(authorList);
	}
	
	
	private static void createPaperNodes() {
		
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Paper> paperList = sqlAccessLayer.getListOfPapers();
		System.out.println("Paper Data Fetched.");
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addPaperNodes(paperList);
		System.out.println("Papers added to graph.");
		
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writePapersToCSV(paperList);
	}
	
	private static void createJournalNodes() {
		// TODO Auto-generated method stub
		
	}



}
