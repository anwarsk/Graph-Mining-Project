/** Algorithm for ranking documents similarity score with query
 *  (requires a similarity kernel)	
 * 
 * @author Scott Sanner (ssanner@gmail.com)
 */

package diversity;

import diversity.kernel.Kernel;

public class ScoreRanker extends ResultListSelector {
	
	public Kernel _sim;
	
	// Constructor
	public ScoreRanker(HashMap<String, String> docs, Kernel sim) { 
		super(docs);
		_sim = sim;
	}
	
	public void addDoc(String doc_name) {
		_docOrig.add(doc_name);
	}
	
	public void clearDocs() {
		_docRepr.clear();
		_docOrig.clear();
		_sim.clear();
	}
	
	public void initDocs() {

		// The similarity kernel may need to do pre-processing (e.g., LDA training)
		_sim.init(_docOrig); // LDA should maintain keys for mapping later
		
		// Store local representation for later use with kernels
		// (should we let _sim handle everything and just interact with keys?)
		for (String doc : _docOrig) {
			Object repr = _sim.getObjectRepresentation(doc);
			_docRepr.put(doc, repr);
		}
	}
	
	@Override
	public ArrayList<String> getResultList(String query, int list_size) {
		
		ArrayList<String> result_list = new ArrayList<String>();

		// Intialize document set
		initDocs();
		
		// Get representation for query
		Object query_repr = _sim.getNoncachedObjectRepresentation(query);

		// Initialize the set of all sentences minus
		// the selected sentences
		DocScore[] ds = new DocScore[_docRepr.keySet().size()];
		int i = 0;
		for (String doc : _docRepr.keySet())
			ds[i++] = new DocScore(doc, _sim.sim(_docRepr.get(doc), query_repr));
			
		// Sort and return
		Arrays.sort(ds);
		for (i = 0; i < list_size && i < ds.length; i++) {
			//System.out.println(i + ": " + ds[i]);
			result_list.add(ds[i]._doc);
		}
		return result_list;		
	}

	public class DocScore implements Comparable {
		public String _doc;
		public double _score;
		public DocScore(String doc, double score) {
			_doc = doc;
			_score = score;
		}
		// Highest comes first
		public int compareTo(Object arg0) {
			DocScore s = (DocScore)arg0;
			double diff = (_score - s._score);
			if (diff < 0d)
				return 1; // s larger, so comes first
			else if (diff > 0d)
				return -1; // this larger, so comes first
			else
				return 0;
		}
		public String toString() {
			return "(" + _doc + ", " + _score + ")";
		}
	}
	
	@Override
	public String getDescription() {
		return "ScoreRanker -- score: " + _sim.getKernelDescription();
	}
}
