/** Loss function implementation for diversity evaluation
 *   
 * @author Scott Sanner (ssanner@gmail.com)
 */

package trec.evaldiv.loss;

import trec.evaldiv.QueryAspects;

import java.util.List;

public class WSLoss extends AspectLoss {

	@Override
	public Object eval(QueryAspects qa, List<String> docs) {
		return qa.getWeightedSubtopicLoss(docs, docs.size());
	}

}
