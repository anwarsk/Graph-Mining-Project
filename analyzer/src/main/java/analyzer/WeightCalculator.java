package analyzer;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * Class to calcuate and maange weight for relation in the graph.
 * @author anwar
 *
 */
public class WeightCalculator {

	/**
	 * Method to calculate weight for specified relationship.
	 */
	public double getWeightForRelation(Relationship relation)
	{
		assert relation != null : "Null Relation";
		
		double weight = 1;
		/**
		 * Relations has 2-kinds of weights. Forward weight and backward weight.
		 * Which is calculated as -
		 * f_weight = 1 / (Total Number of OUTGOING relations of that type)
		 * b_weight = 1 / (Total Number of INCOMING relations of that type)
		 */
		if(relation.hasProperty("b_weight"))
		{
			weight = (Double) relation.getProperty("b_weight");
		}
		else
		{
			weight = (Double) relation.getProperty("f_weight");
		}
		
//		if(relation.getType().name().compareToIgnoreCase("cite") == 0)
//		{
//				weight = (Double) relation.getProperty("b_weight");
//		}
//		else if(relation.getType().name().compareToIgnoreCase("contains") == 0)
//		{
//				weight = (Double) relation.getProperty("b_weight");
//		}
//		else if(relation.getType().name().compareToIgnoreCase("written") == 0)
//		{
//				weight = (Double) relation.getProperty("b_weight");
//		}
//		else
//		{
//			throw new IllegalStateException();
//		}
			
		return weight;
	}
}
