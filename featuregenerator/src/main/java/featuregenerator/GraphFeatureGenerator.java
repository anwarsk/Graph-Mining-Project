package featuregenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.PathExpanderBuilder;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import data.FeatureGeneratorInput;
import data.FeatureGeneratorOutput;
import db.csv.CSVAccessLayer;
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

	public FeatureGeneratorOutput generateDistanceFeature(FeatureGeneratorInput featureGeneratorInput)
	{
		assert featureGeneratorInput != null : "NULL Feature Generator";
		assert featureGeneratorInput.size() > 0 : "Empty Feature Generator";

		FeatureGeneratorOutput featureGeneratorOutput = FeatureGeneratorOutput.getInstance();
		Iterator<Entry<String, Integer>> inputIterator = featureGeneratorInput.getIterator();

		if(!isInitialized){intialize();}

		System.out.println("\n Starting to Generate Distance Feature");

		try (Transaction tx=dbService.beginTx()) 
		{
			while(inputIterator.hasNext())
			{
				Entry<String, Integer> input = inputIterator.next();
				String authorId = input.getKey();
				int proceedingId = input.getValue();

				// (1) - Find the node in the graph for the author 
				Node author = dbService.findNode(Label.label("author"), "author_id", authorId);
				if(author == null){ System.out.println("Can not find author with id-" + authorId); continue;}

				// (2) -  
				Node proceeding = dbService.findNode(Label.label("proceeding"), "proc_id", proceedingId);
				if(proceeding == null){ System.out.println("Can not find proceeding with id-" + proceedingId); continue;}

				//(3) - Get the iterator over all the papers in the conference or proceeding 
				Iterator<Relationship> publishedRelations = proceeding.getRelationships(RelationshipType.withName("published_at")).iterator();

				while(publishedRelations.hasNext())
				{
					Relationship publishedRelationShip = publishedRelations.next();
					if(publishedRelationShip == null){System.out.println("Null published Relationship"); continue;}

					Node paperNode = publishedRelationShip.getOtherNode(proceeding);
					if(paperNode == null){System.out.println("Null paper Node from the relationship"); continue;}

					int articleId = (int) (long) paperNode.getProperty("article_id");
					int shortestDistance = this.getShortestDistanceBetweenAuthorAndPaper(author, paperNode);

					featureGeneratorOutput.addDistance(authorId, articleId, shortestDistance);
				}
			}

			tx.success();
			tx.close();
		}


		System.out.println("\n Completed Generating Distance Feature");

		return featureGeneratorOutput;
	}

	int getShortestDistanceBetweenAuthorAndPaper(Node author, Node paper)
	{
		assert author != null : "Null Author node";
		assert paper != null : "Null Paper node";

		if(!isInitialized){intialize();}

		/**
		 * Find the shortest distance between author node and paper node 
		 * if path does not exists returns -1
		 */
		int distance = -1;

		// (3) - Get the path finder 
		PathExpander<Object> pathExpander = this.createPathExpanderForPaperAndKeyword();

		// 
		PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(pathExpander, 
																	Constant.MAX_PATH_DEPTH, 
																	Constant.SHORTEST_PATH_COUNT);

		Path path = pathFinder.findSinglePath(author, paper);
		if( path == null) { System.out.println("Can not find path between a-p"); return -1;}
		distance = path.length();
		return distance;
	}

	private PathExpander<Object> createPathExpanderForPaperAndKeyword()
	{
		PathExpander<Object> pathExpander = null;

		PathExpanderBuilder pathExpanderBuilder = PathExpanderBuilder.allTypesAndDirections();
		pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("published_at"));
		pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("published_in"));
		//pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("written"));
		//pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("cite"));

		//NodeFilter nodeFilter = new NodeFilter(cutOffDate, startNodeId, endNodeId);
		//pathExpanderBuilder = pathExpanderBuilder.addNodeFilter(nodeFilter);

		pathExpander = pathExpanderBuilder.build();

		return pathExpander;
	}

}
