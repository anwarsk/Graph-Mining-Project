package analyzer;


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
import org.neo4j.graphdb.factory.GraphDatabaseFactoryState;

import result.AuthorPaperSubResult;
import result.ProceeedingPaperSubResult;
import result.Result;
import environment.Constant;

public class GraphAnalyzer {

	private static String NEO_GRAPH_DB_PATH = Constant.NEO_GRAPH_DB_PATH;
	private int MAX_TREE_DEPTH = Constant.MAX_PATH_DEPTH;

	private static File databaseDirectory;
	private static GraphDatabaseService dbService;
	private static boolean isInitialized = false; 
	
	public static void intialize()
	{
		if(!isInitialized)
		{
			databaseDirectory = new File(NEO_GRAPH_DB_PATH);
			dbService =  new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
			isInitialized = true;
		}

	}
	public Result generateResults(String authorId, int proceedingId)
	{
		assert authorId != null && authorId.isEmpty() == false : "Invalid Author Id";
		assert proceedingId >0 : "Invalid Proceeding Id";

		if(!isInitialized){intialize();}
		
		Result result =  null;

		try (Transaction tx=dbService.beginTx()) 
		{

			Node author = dbService.findNode(Label.label("author"), "author_id", authorId);
			if(author == null){ System.out.println("Can not find author with id-" + authorId); return null;}

			Node proceeding = dbService.findNode(Label.label("proceeding"), "proc_id", proceedingId);
			if(proceeding == null){ System.out.println("Can not find proceeding with id-" + proceedingId); return null;}


			Iterator<Relationship> publishedRelations = proceeding.getRelationships(RelationshipType.withName("published_at")).iterator();

			
			WeightCalculator weightCalculator = new WeightCalculator();

			result = new Result(authorId, proceedingId);

			while(publishedRelations.hasNext())
			{
				Node procPaperNode = publishedRelations.next().getStartNode();
				int procArticleId = (int)(long) procPaperNode.getProperty("article_id"); 
				System.out.println("Running for Proceeding article_id" + procArticleId);

				ProceeedingPaperSubResult procPaperSubResult = new ProceeedingPaperSubResult(procArticleId);

				double procPaperScore = 0;

				Iterator<Relationship> writtenRelations = author.getRelationships(RelationshipType.withName("written")).iterator();
				while(writtenRelations.hasNext())
				{
					double authorPaperScore =  0;
					Node authorPaperNode = writtenRelations.next().getEndNode();
					PathExpander<Object> pathExpander = this.createPathExpander();
					PathFinder<Path> allPathFinder = GraphAlgoFactory.allSimplePaths(pathExpander, MAX_TREE_DEPTH);
					Iterator<Path> allPaths = allPathFinder.findAllPaths(authorPaperNode, procPaperNode).iterator();
					
					Map<Integer, Double> keywordIdToScoreMap = new HashMap<Integer, Double>();

					while(allPaths.hasNext())
					{
						Path path = allPaths.next();
						System.out.println("Processing Path: " + path.toString());
						//					System.out.println("Path Length: " + path.length());
						double pathRWProbability = 1.0;
						Iterator<Relationship> connections  = path.relationships().iterator();

						while(connections.hasNext())
						{
							Relationship connection = connections.next();
							double weight = weightCalculator.getWeightForRelation(connection);
							pathRWProbability = pathRWProbability * weight;
						}
						authorPaperScore += pathRWProbability;

						Iterator<Node> nodes = path.nodes().iterator();
						while(nodes.hasNext())
						{
							Node pathNode = nodes.next();
							if(pathNode.hasLabel(Label.label("keyword")))
							{
								int keywordId = (int) pathNode.getProperty("keyword_id");
								double currentKeywordScore = keywordIdToScoreMap.getOrDefault(keywordId, 0.0);
								keywordIdToScoreMap.put(keywordId, currentKeywordScore + pathRWProbability);
							}
						}
						
					}
					// Create AuthorPaperSubResult
					int authorArticleId = (int) (long) authorPaperNode.getProperty("article_id"); 
					AuthorPaperSubResult authorPaperSubResult = new AuthorPaperSubResult(authorArticleId, authorPaperScore);
					authorPaperSubResult.addKeywords(keywordIdToScoreMap);

					procPaperSubResult.addAuthorPaperSubResult(authorPaperSubResult);
					procPaperScore += authorPaperScore;
				}

				procPaperSubResult.setScore(procPaperScore);
				result.addProceedingPaperSubResult(procPaperSubResult);
			}

			tx.success();
			tx.close();
		}

		return result;
	}

	private PathExpander<Object> createPathExpander()
	{
		PathExpander<Object> pathExpander = null;

		PathExpanderBuilder pathExpanderBuilder = PathExpanderBuilder.allTypesAndDirections();
		pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("published_at"));
		pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("published_in"));
		pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("written"));

		NodeFilter nodeFilter = new NodeFilter();
		pathExpanderBuilder = pathExpanderBuilder.addNodeFilter(nodeFilter);

		pathExpander = pathExpanderBuilder.build();

		return pathExpander;
	}
	
	public static void shutdown(){
		dbService.shutdown();
	}
}
