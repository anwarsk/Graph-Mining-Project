package exec;

import java.util.List;

import data.Author;
import db.csv.CSVAccessLayer;
import db.neo4j.Neo4jAccessLayer;
import db.sql.SQLAccessLayer;

public class Main {

	public static void main(String[] args) {

		createAuthorNodes();
		
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

}
