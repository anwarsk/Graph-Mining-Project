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

import result.AuthorPaperSubResult;
import result.ProceeedingPaperSubResult;
import result.Result;


public class GraphAnalyzer {

	private String NEO_GRAPH_DB_PATH = "./../../../Database/graph.acm2015";
	private int MAX_TREE_DEPTH = 4;

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

			PathExpander<Object> pathExpander = this.createPathExpander();
			WeightCalculator weightCalculator = new WeightCalculator();

			Result result = new Result(authorId, proceedingId);
			
			while(publishedRelations.hasNext())
			{
				Node procPaperNode = publishedRelations.next().getStartNode();
				int procArticleId = (int)(long) procPaperNode.getProperty("article_id"); 
				ProceeedingPaperSubResult procPaperSubResult = new ProceeedingPaperSubResult(procArticleId);
				
				double procPaperScore = 0;

				while(writtenRelations.hasNext())
				{
					double authorPaperScore =  0;
					Node authorPaperNode = writtenRelations.next().getEndNode();
					
					PathFinder<Path> allPathFinder = GraphAlgoFactory.allSimplePaths(pathExpander,MAX_TREE_DEPTH);
					Iterable<Path> allPaths = allPathFinder.findAllPaths(authorPaperNode, procPaperNode);
					Map<Integer, Double> keywordIdToScoreMap = new HashMap<Integer, Double>();
					
					for(Path path : allPaths)
					{
						double pathRWProbability = 1.0;
						Iterable<Relationship> connections  = path.relationships();
					
						for(Relationship connection : connections)
						{
							double weight = weightCalculator.getWeightForRelation(connection);
							pathRWProbability = pathRWProbability * weight;
						}
						authorPaperScore += pathRWProbability;
						
						Iterable<Node> nodes = path.nodes();
						for(Node pathNode : nodes)
						{
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

		dbService.shutdown();

		return null;
	}

	private PathExpander<Object> createPathExpander()
	{
		PathExpander<Object> pathExpander = null;

		PathExpanderBuilder pathExpanderBuilder = PathExpanderBuilder.empty();

		NodeFilter nodeFilter = new NodeFilter();
		pathExpanderBuilder = pathExpanderBuilder.addNodeFilter(nodeFilter);

		pathExpander = pathExpanderBuilder.build();

		return pathExpander;
	}
}
