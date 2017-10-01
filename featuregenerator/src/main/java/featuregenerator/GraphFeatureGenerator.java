package featuregenerator;

import java.io.File;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import environment.Constant;

/**
 * This class is responsible for generating all the graph related features from the
 * Neo4j graph.
 * @author anwar
 *
 */
public class GraphFeatureGenerator {

	private static File databaseDirectory;
	private static GraphDatabaseService dbService;

	private static boolean isInitialized = false; 

	public static void intialize()
	{
		if(!isInitialized)
		{

			databaseDirectory = new File(Constant.NEO_GRAPH_DB_PATH);
			dbService =  new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
			isInitialized = true;
		}

	}
	void generateDistanceFeature()
	{

	}

	int getShortestDistanceBetweenAuthorAndPaper(String authorId, int articleId)
	{
		assert authorId != null && authorId.isEmpty() == false : "Invalid Author Id";
		assert articleId >0 : "Invalid Article Id";

		if(!isInitialized){intialize();}

		/**
		 * Find the shortest distance between author node and paper node 
		 * if path does not exists returns -1
		 */
		int distance = -1;

		try (Transaction tx=dbService.beginTx()) 
		{

			// (1) - Find the node in the graph for the author 
			Node author = dbService.findNode(Label.label("author"), "author_id", authorId);
			if(author == null){ System.out.println("Can not find author with id-" + authorId); return distance;}

			// (2) - Find the node for the conference/proceeding in the graph
			Node paper = dbService.findNode(Label.label("paper"), "article_id", articleId);
			if(paper == null){ System.out.println("Can not find paper with id-" + articleId); return distance;}
			
			// (3) - Get the path finder 
			PathExpander<Object> pathExpander = PathExpanders.allTypesAndDirections();
			
			// 
			PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(pathExpander, Constant.MAX_PATH_DEPTH, 
					Constant.SHORTEST_PATH_COUNT);
			
			distance = pathFinder.findSinglePath(author, paper).length();
			
			tx.success();
			tx.close();
		}

		return distance;
	}

}
