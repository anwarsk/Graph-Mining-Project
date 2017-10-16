package converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import environment.Constant;

public class EdgeListConverter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File  databaseDirectory = new File(Constant.NEO_GRAPH_DB_PATH);
		GraphDatabaseService dbService =  new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		HashMap<String, Long> nodeIdToValueMap = new HashMap<String, Long>();

		try (Transaction tx=dbService.beginTx()) 
		{
			PrintWriter writer = new PrintWriter(Constant.EDGE_LIST_OUTPUT_FILE);
			PrintWriter mapWriter = new PrintWriter(Constant.EDGE_LIST_MAP_FILE);
			ResourceIterable<Relationship> relationShips =dbService.getAllRelationships();
			boolean isStart =  true;
			long nodeCount = 1;
			for (Relationship relationship : relationShips)
			{
				Node startNode = relationship.getStartNode();
				Node endNode = relationship.getEndNode();

				if(true /*isQualifiedNodes(startNode) & isQualifiedNodes(endNode)*/)
				{

					String startNodeID = getNodeId(startNode);
					String endNodeID = getNodeId(endNode);

					if(isStart)
					{
						isStart = false;
					}
					else
					{
						writer.write("\n");
						//mapWriter.write("\n");
					}

					long defaultValue = -10;
					long startNodeValue = nodeIdToValueMap.getOrDefault(startNodeID, defaultValue);
					long endNodeValue = nodeIdToValueMap.getOrDefault(endNodeID, defaultValue);

					if(startNodeValue == -10) 
					{ 
						startNodeValue =  nodeCount++; 
						nodeIdToValueMap.put(startNodeID, startNodeValue);
						mapWriter.write(startNodeID + "," + startNodeValue + "\n");
					}

					if(endNodeValue == -10)
					{
						endNodeValue = nodeCount++;
						nodeIdToValueMap.put(endNodeID, endNodeValue);
						mapWriter.write(endNodeID + "," + endNodeValue + "\n");
					}

					writer.write(startNodeValue + " " + endNodeValue);

				}
			}

			writer.close();
			mapWriter.close();
			tx.success();
			tx.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private static String getNodeId(Node node)
	{
		assert node != null : "Null node for qualification";

		String nodeId = "";

		if(node.hasLabel(Label.label("author")))
		{
			nodeId = "author," + node.getProperty("author_id") ;
		}
		else if(node.hasLabel(Label.label("journal")))
		{
			nodeId = "journal," + node.getProperty("journal_id") ;
		}
		else if(node.hasLabel(Label.label("keyword")))
		{
			nodeId = "keyword," + node.getProperty("keyword_id") ;
		}
		else if(node.hasLabel(Label.label("paper")))
		{
			nodeId = "paper," + node.getProperty("article_id") ;
		}
		else if(node.hasLabel(Label.label("proceeding")))
		{
			nodeId = "proceeding," + node.getProperty("proc_id") ;
		}

		assert nodeId.isEmpty() == false : "Empty Node Id";

		return nodeId;
	}


	private static boolean isQualifiedNodes(Node node1)
	{
		assert node1 != null : "Null node for qualification";

		boolean isQualifiedNodes = true;

		Iterable<Label> labels = node1.getLabels();

		for(Label label : labels)
		{
			String labelName = label.name();
			if(labelName.compareToIgnoreCase("topic") == 0)
			{
				if(node1.getDegree(RelationshipType.withName("relevant")) < 50)
				{
					isQualifiedNodes = false;
					break;
				}
			}
			else if(labelName.compareToIgnoreCase("author") == 0)
			{
				if(node1.getDegree(RelationshipType.withName("writtenby")) < 4)
				{
					isQualifiedNodes = false;
					break;
				}

			}
			else if(labelName.compareToIgnoreCase("venue") == 0)
			{
				if(node1.getDegree(RelationshipType.withName("publishedat")) < 30)
				{
					isQualifiedNodes = false;
					break;
				}

			}
			else if(labelName.compareToIgnoreCase("paper") == 0)
			{
				if(node1.getDegree(RelationshipType.withName("cite")) < 4)
				{
					isQualifiedNodes = false;
					break;
				}

			}
		}

		return isQualifiedNodes;

	}

}
