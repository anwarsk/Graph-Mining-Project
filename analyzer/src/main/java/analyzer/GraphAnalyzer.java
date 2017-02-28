package analyzer;

import java.io.File;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.Label;

public class GraphAnalyzer {

	private String NEO_GRAPH_DB_PATH = "";

	public String generateResults(String authorId, int proceedingId)
	{
		assert authorId != null && authorId.isEmpty() == false : "Invalid Author Id";
		assert proceedingId >0 : "Invalid Proceeding Id";
		File databaseDirectory = new File(NEO_GRAPH_DB_PATH);
		GraphDatabaseService dbService =  new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

		try (Transaction tx=dbService.beginTx()) 
		{

			Node author = dbService.findNode(Label.label("author"), "author_id", authorId);
			if(author == null){ System.out.println("Can not find author with id-" + authorId); return null;}
			
			Node proceeding = dbService.findNode(Label.label("proceeding"), "proc_id", proceedingId);
			if(proceeding == null){ System.out.println("Can not find proceeding with id-" + proceedingId); return null;}
			
			Iterator<Relationship> writtenRelations = author.getRelationships(RelationshipType.withName("written")).iterator();
			Iterator<Relationship> publishedRelations = proceeding.getRelationships(RelationshipType.withName("published_at")).iterator();
			
			while(writtenRelations.hasNext())
			{
				Node authorPaperNode = writtenRelations.next().getEndNode();
				
				while(publishedRelations.hasNext())
				{
					Node procPaperNode = publishedRelations.next().getStartNode();
					
					
				}
			}
			
			tx.success();
			tx.close();
		}

		dbService.shutdown();
		
		return null;
	}
}
