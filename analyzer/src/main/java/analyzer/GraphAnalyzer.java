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

		/**
		 * Initialize the result
		 */
		Result result =  null;

		try (Transaction tx=dbService.beginTx()) 
		{

			
			// (1) - Find the node in the graph for the author 
			Node author = dbService.findNode(Label.label("author"), "author_id", authorId);
			if(author == null){ System.out.println("Can not find author with id-" + authorId); return null;}

			// (2) - Find the node for the conference/proceeding in the graph
			Node proceeding = dbService.findNode(Label.label("proceeding"), "proc_id", proceedingId);
			if(proceeding == null){ System.out.println("Can not find proceeding with id-" + proceedingId); return null;}

			// (3) - Get the iterator over all the papers in the conference or proceeding 
			Iterator<Relationship> publishedRelations = proceeding.getRelationships(RelationshipType.withName("published_at")).iterator();
			
			// (4) - Initialize weight calculator and Result
			WeightCalculator weightCalculator = new WeightCalculator();
			result = new Result(authorId, proceedingId);

			// (5) - Iterate over the papers published at the conference
			while(publishedRelations.hasNext())
			{
				// Paper at the conference or proceeding
				Node procPaperNode = publishedRelations.next().getStartNode();
				int procArticleId = (int)(long) procPaperNode.getProperty("article_id"); 
				
				System.out.println("Running for Proceeding article_id" + procArticleId);
				
				// Cut-Of-Date to omit papers published before the proceeding paper
				long cutOffDate = (long)procPaperNode.getProperty("publication_date");
				
				ProceeedingPaperSubResult procPaperSubResult = new ProceeedingPaperSubResult(procArticleId);
				double procPaperScore = 0;

				// Get the iterator for the papers written by the author
				Iterator<Relationship> writtenRelations = author.getRelationships(RelationshipType.withName("written")).iterator();
				
				// Iterate over the papers written by the author 
				while(writtenRelations.hasNext())
				{
					// Get the node for paper written by the author
					Node authorPaperNode = writtenRelations.next().getEndNode();
					
					// Initialize the variables 
					double authorPaperScore =  0;
					long numberOfPathTraversed = 0;
					int currentDepth = Constant.START_PATH_DEPTH;
					Map<Integer, Double> keywordIdToScoreMap = new HashMap<Integer, Double>();
					
					/**
					 *  Traverse the paths between author paper and the conference paper until any terminating (boundary) condition is met. 
					 *  Which is one of the following-
					 *  1. Maximum allowed number of paths are traversed
					 *  2. Maximum allowed depth is reached 
					 */
					do
					{
						/** TO-TEST::
						System.out.println("Current Path Length: " + currentDepth);
						System.out.println("Current Path Count: " + numberOfPathTraversed);
						long testCutOffDate = Long.MIN_VALUE;
						*/
						
						/**
						 *  Create a path expander which has source and destination node. 
						 *  Also, know which nodes to omit cut-of-date. It internally creates NodeFilter object.
						 */
						PathExpander<Object> pathExpander = this.createPathExpander(cutOffDate, authorPaperNode.getId(), procPaperNode.getId());
						
						// Get path finder with above expander finding all the paths at specific depth
						PathFinder<Path> allPathFinder = GraphAlgoFactory.pathsWithLength(pathExpander, currentDepth);
						
						// Get the iterator over all the paths matching the condition
						Iterator<Path> allPaths = allPathFinder.findAllPaths(authorPaperNode, procPaperNode).iterator();

						// Iterate over the paths
						while(allPaths.hasNext())
						{
							Path path = allPaths.next();
							//							System.out.println("Processing Path: " + path.toString());
							//							System.out.println("Path Length: " + path.length());
							
							// Initialize the random walk probability for the current path nodes 
							double pathRWProbability = 1.0;
							
							// Get the iterator for all the edges in the current path
							Iterator<Relationship> connections  = path.relationships().iterator();

							// Iterate over all the edges unless random walk probability becomes insignificant
							while(connections.hasNext() && (pathRWProbability > Constant.RANDON_WALK_PROB_CUTOFF))
							{
								// Get the edge
								Relationship connection = connections.next();
								
								// Get the weight for the edge
								double weight = weightCalculator.getWeightForRelation(connection);
								
								// Multiply the edge weight with path random walk probability 
								pathRWProbability = pathRWProbability * weight;
								
								// Make edge/connection null
								connection = null;
							}
							connections = null;

							// Update the author paper score with the current path random walk probability
							authorPaperScore += pathRWProbability/path.length();
							
							/**
							 *  If the random walk probability is less than a significant value then ignore iterating over 
							 *  the keywords and updating score for the keyword. 
							 */
							if(pathRWProbability < Constant.RANDON_WALK_PROB_CUTOFF){ 
								//System.out.println("Terminated with: " + pathRWProbability); 
								continue;	
							}

							// If the probability is significant then iterate over each node in the path
							Iterator<Node> nodes = path.nodes().iterator();
							while(nodes.hasNext())
							{
								// Store the level of the node
								Node pathNode = nodes.next();
								
								// Update the score for each keyword in the path
								if(pathNode.hasLabel(Label.label("keyword")))
								{
									// Get the keyword id 
									int keywordId = (int) pathNode.getProperty("keyword_id");
									
									// Get the current keyword score if exists or 0 if does not with default 
									double currentKeywordScore = keywordIdToScoreMap.getOrDefault(keywordId, 0.0);
									
									// Update the keyword score by adding the current random walk probability
									keywordIdToScoreMap.put(keywordId, currentKeywordScore + pathRWProbability);
								}
								
								pathNode = null;						
								// TO-DO: Use the formula for the score calculations.
							}
							
							nodes = null;
							path = null;
							
							// Update the number of path traversed between author paper and the conference paper
							numberOfPathTraversed++;
							
							// If maximum number of paths are traversed then break the loop
							if(numberOfPathTraversed > Constant.MAX_PATHS){break;}
						}
						allPaths = null;

						/**
						 * If we have traversed all the path at current depth n. And it still falls short to the maximum path 
						 * allowed between author paper and the conference paper. Increase the depth by 1 i.e. new depth n+1.
						 */
						if(numberOfPathTraversed < Constant.MAX_PATHS){currentDepth++;}
						
						// Reset the path expander and finder 
						pathExpander = null;
						allPathFinder = null;
						
					}while(numberOfPathTraversed < Constant.MAX_PATHS && currentDepth < Constant.MAX_PATH_DEPTH);
					
					/**
					 *  Call the garbage collector in case after processing current pair of author paper and conference paper
					 *  we have free memory less than 25%. In order to avoid hanging up the system.   
					 */
					double memRatio = (Runtime.getRuntime().freeMemory() *1.00) / Runtime.getRuntime().totalMemory();
					if(memRatio < 0.25)
					{
						Runtime.getRuntime().gc();
						//System.out.println("GC-RAN");
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

			// Close the transaction for neo4j database
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
