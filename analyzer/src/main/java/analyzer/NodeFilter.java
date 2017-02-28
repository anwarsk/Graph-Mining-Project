package analyzer;

import java.util.function.Predicate;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

// Node Filter just traversing paper and keyword
public class NodeFilter implements Predicate<Node>{

	@Override
	public boolean test(Node testNode) {
		// TODO Auto-generated method stub
		boolean isNodeMatch = false;

		if(testNode.hasLabel(Label.label("paper")) | testNode.hasLabel(Label.label("keyword")))
		{
			isNodeMatch = true;
		}

		return isNodeMatch;
	}
}
