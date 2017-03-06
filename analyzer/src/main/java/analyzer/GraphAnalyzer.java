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

import environment.Constant;
import result.AuthorPaperSubResult;
import result.ProceeedingPaperSubResult;
import result.Result;

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
				long cutOffDate = (long)procPaperNode.getProperty("publication_date");
				
				ProceeedingPaperSubResult procPaperSubResult = new ProceeedingPaperSubResult(procArticleId);

				double procPaperScore = 0;

				Iterator<Relationship> writtenRelations = author.getRelationships(RelationshipType.withName("written")).iterator();
				while(writtenRelations.hasNext())
				{
					double authorPaperScore =  0;
					Node authorPaperNode = writtenRelations.next().getEndNode();
					long numberOfPathTraversed = 0;
					int currentDepth = Constant.START_PATH_DEPTH;
					Map<Integer, Double> keywordIdToScoreMap = new HashMap<Integer, Double>();
					do
					{
//						System.out.println("Current Path Length: " + currentDepth);
//						System.out.println("Current Path Count: " + numberOfPathTraversed);
						
						//long testCutOffDate = Long.MIN_VALUE;
						PathExpander<Object> pathExpander = this.createPathExpander(cutOffDate, authorPaperNode.getId(), procPaperNode.getId());
						
						PathFinder<Path> allPathFinder = GraphAlgoFactory.pathsWithLength(pathExpander, currentDepth);
						Iterator<Path> allPaths = allPathFinder.findAllPaths(authorPaperNode, procPaperNode).iterator();
						
						while(allPaths.hasNext())
						{
							Path path = allPaths.next();
//							System.out.println("Processing Path: " + path.toString());
//							System.out.println("Path Length: " + path.length());
							double pathRWProbability = 1.0;
							Iterator<Relationship> connections  = path.relationships().iterator();

							while(connections.hasNext() && (pathRWProbability > Constant.RANDON_WALK_PROB_CUTOFF))
							{
								Relationship connection = connections.next();
								double weight = weightCalculator.getWeightForRelation(connection);
								pathRWProbability = pathRWProbability * weight;
							}
							
							if(pathRWProbability < Constant.RANDON_WALK_PROB_CUTOFF){ 
//								System.out.println("Terminated with: " + pathRWProbability); 
								continue;	
								}
							
							authorPaperScore += pathRWProbability/path.length();

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
							
							numberOfPathTraversed++;
							if(numberOfPathTraversed > Constant.MAX_PATHS){break;}
						}
						
						if(numberOfPathTraversed < Constant.MAX_PATHS){currentDepth++;}
						
					}while(numberOfPathTraversed < Constant.MAX_PATHS && currentDepth < Constant.MAX_PATH_DEPTH);

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

	private PathExpander<Object> createPathExpander(long cutOffDate, long startNodeId, long endNodeId)
	{
		PathExpander<Object> pathExpander = null;

		PathExpanderBuilder pathExpanderBuilder = PathExpanderBuilder.allTypesAndDirections();
		//pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("published_at"));
		//pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("published_in"));
		//pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("written"));
		//pathExpanderBuilder = pathExpanderBuilder.remove(RelationshipType.withName("cite"));

		NodeFilter nodeFilter = new NodeFilter(cutOffDate, startNodeId, endNodeId);
		pathExpanderBuilder = pathExpanderBuilder.addNodeFilter(nodeFilter);

		pathExpander = pathExpanderBuilder.build();

		return pathExpander;
	}

	public static void shutdown(){
		dbService.shutdown();
	}
}
