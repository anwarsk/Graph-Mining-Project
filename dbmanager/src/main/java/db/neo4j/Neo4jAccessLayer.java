package db.neo4j;

import java.io.File;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import data.Author;

public class Neo4jAccessLayer {

	private final String DB_PATH = "/media/anwar/825ED72B5ED716AF/Work/Database/graph.acm2015";
	
	public void addAuthorNodes(List<Author> authorList)
	{
		assert authorList.isEmpty() == false : "Empty Author List";
		 
		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseBuilder dbBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(databaseDirectory);
		GraphDatabaseService dbService =  dbBuilder.newGraphDatabase();

		try (Transaction tx=dbService.beginTx()) 
		{
			for(Author author: authorList)
			{
				Node node = dbService.createNode();
				node.addLabel(Label.label("author"));
				node.setProperty("author_id", author.getAuthorID());
			}
			
			tx.success();
			tx.close();
		}
		
		dbService.shutdown();
		
	}
}
