package analyzer;

import org.neo4j.graphdb.Relationship;

public class WeightCalculator {

	public double getWeightForRelation(Relationship relation)
	{
		assert relation != null : "Null Relation";
		double weight = (Double) relation.getProperty("f_weight");
		return weight;
	}
}
