package featuregenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import analyzer.WeightCalculator;
import data.FeatureGeneratorOutput;
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
			if(!isInitialized)
			{
				databaseDirectory = new File(Constant.NEO_GRAPH_DB_PATH);
				dbService =  new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
				isInitialized = true;
			}
		}

	}

	public FeatureGeneratorOutput generateGraphFeatures(String authorId, int proceedingId)
	{
		assert authorId != null && authorId.isEmpty() == false : "Invalid Author ID";
		assert proceedingId > 0 : "Invalid Proceeding Id";

		FeatureGeneratorOutput featureGeneratorOutput = new FeatureGeneratorOutput();

		//if(!isInitialized){intialize();}

		try (Transaction tx=dbService.beginTx()) 
		{
			Node authorNode = dbService.findNode(Label.label("author"), "author_id", authorId);
			if(authorNode == null){ System.out.println("Can not find author with id-" + authorId); return null;}

			// (2) -  
			Node proceeding = dbService.findNode(Label.label("proceeding"), "proc_id", proceedingId);
			if(proceeding == null){ System.out.println("Can not find proceeding with id-" + proceedingId); return null;}

			//(3) - Get the iterator over all the papers in the conference or proceeding 
			Iterator<Relationship> publishedRelations = proceeding.getRelationships(RelationshipType.withName("published_at")).iterator();

			while(publishedRelations.hasNext())
			{
				Relationship publishedRelationShip = publishedRelations.next();
				if(publishedRelationShip == null){System.out.println("Null published Relationship"); continue;}

				Node paperNode = publishedRelationShip.getOtherNode(proceeding);
				if(paperNode == null){System.out.println("Null paper Node from the relationship"); continue;}

				int articleId = (int) (long) paperNode.getProperty("article_id");

				int shortestDistance = this.getShortestDistanceBetweenAuthorAndPaper(authorNode, paperNode);
				PathFeatures pathFeatures = this.getPathRelatedFeatures(authorNode, paperNode);

				//featureGeneratorOutput.addDistance(authorId, articleId, shortestDistance);
				featureGeneratorOutput.addEntry(authorId, articleId, 
						shortestDistance, 
						pathFeatures.randomWalkProbability, 
						pathFeatures.pathLengthToCountMap, 
						pathFeatures.currentScoringMethod);
			}
			tx.success();
			tx.close();
		}

		return featureGeneratorOutput;

	}

	int getShortestDistanceBetweenAuthorAndPaper(Node author, Node paper)
	{
		assert author != null : "Null Author node";
		assert paper != null : "Null Paper node";

		//if(!isInitialized){intialize();}

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

	PathFeatures getPathRelatedFeatures(Node author, Node paper)
	{
		assert author != null : "Null Author node";
		assert paper != null : "Null Paper node";

		//if(!isInitialized){intialize();}

		PathFeatures pathFeatures = new PathFeatures();

		double randomWalkProbability = 0;
		double currentScoringMethod = 0;
		Map<Integer, Integer> pathLengthToCountMap = new HashMap<Integer, Integer>();

		WeightCalculator weightCalculator = new WeightCalculator();

		// (3) - Get the path finder 
		PathExpander<Object> pathExpander = this.createPathExpanderForPaperAndKeyword();

		// 
		PathFinder<Path> pathFinder = GraphAlgoFactory.allPaths(pathExpander, Constant.MAX_PATH_DEPTH);

		Iterable<Path> allPaths = pathFinder.findAllPaths(author, paper);

		if(allPaths == null || allPaths.iterator().hasNext() == false) {System.out.println("No Path between Author and Paper");}

		for(Path path : allPaths)
		{
			double pathRandomWalkProbability = 1;
			if(path == null) { System.out.println("Null Path");continue;}

			for(Relationship realtionship : path.relationships())
			{
				if(realtionship == null) {continue;}
				pathRandomWalkProbability *= weightCalculator.getWeightForRelation(realtionship);
			}

			randomWalkProbability += pathRandomWalkProbability;

			int pathLength = path.length();

			int currentPathLengthCount = pathLengthToCountMap.getOrDefault(pathLength, 0);
			pathLengthToCountMap.put(pathLength, currentPathLengthCount+1);

			currentScoringMethod += (pathRandomWalkProbability/pathLength);

			//System.out.println("\n# RandomWalkProbability = " + randomWalkProbability);
			//System.out.println("\n* PathLength= " + pathLength + " * CurrentScoringMethod= " + currentScoringMethod);
		}

		pathFeatures.randomWalkProbability = randomWalkProbability;
		pathFeatures.pathLengthToCountMap = pathLengthToCountMap;
		pathFeatures.currentScoringMethod = currentScoringMethod;

		return pathFeatures;
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

	public static void shutDown() {
		// TODO Auto-generated method stub
		if(isInitialized)
		{
			dbService.shutdown();
			isInitialized = false;
		}
	}

}

class PathFeatures
{
	double randomWalkProbability = 0;
	double currentScoringMethod = 0;
	Map<Integer, Integer> pathLengthToCountMap = null;
}
