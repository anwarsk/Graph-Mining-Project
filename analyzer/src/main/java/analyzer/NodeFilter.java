package analyzer;

import java.util.function.Predicate;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

// Node Filter just traversing paper and keyword
public class NodeFilter implements Predicate<Node>{

	private long cutOffDate;
	private long startNodeId;
	private long endNodeId;
	
	public NodeFilter(long cutOffDate, long startNodeId, long endNodeId)
	{
		this.cutOffDate = cutOffDate;
		this.startNodeId = startNodeId;
		this.endNodeId = endNodeId;
	}

	@Override
	public boolean test(Node testNode) {
		// TODO Auto-generated method stub
		boolean isNodeMatch = false;

		if(testNode.hasLabel(Label.label("paper"))) 
		{
			if(/*testNode.getId() == startNodeId |*/ testNode.getId()== endNodeId)
			{
				isNodeMatch = true;
			}
			else if(((long)testNode.getProperty("publication_date")) < cutOffDate)
			{
				isNodeMatch = true;
			}
		}
		else if(testNode.hasLabel(Label.label("author")) | testNode.hasLabel(Label.label("keyword")))
		{
			isNodeMatch = true;
		}

		return isNodeMatch;
	}
}
