package analyzer;

import org.neo4j.graphdb.Relationship;

public class WeightCalculator {

	public double getWeightForRelation(Relationship relation)
	{
		assert relation != null : "Null Relation";
		relation.getProperty("f_weight");
		return 0;
		
	}
}
