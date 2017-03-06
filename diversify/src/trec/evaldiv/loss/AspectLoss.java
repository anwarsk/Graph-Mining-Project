/** Loss function interface for diversity
 *   
 * @author Scott Sanner (ssanner@gmail.com)
 */

package trec.evaldiv.loss;

import trec.evaldiv.QueryAspects;

import java.util.List;

public abstract class AspectLoss {

	public String getName() {
		String[] split = this.getClass().toString().split("[\\.]");
		return split[split.length - 1];
	}
	
	public abstract Object eval(QueryAspects qa, List<String> docs);
}
