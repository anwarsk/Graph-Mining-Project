package result;

import java.util.Collections;
import java.util.PriorityQueue;

public class ProceeedingPaperSubResult {
	
	int proceedingArticleId;
	double score;
	
	PriorityQueue<AuthorPaperSubResult> keywords = new PriorityQueue<AuthorPaperSubResult>(20, Collections.reverseOrder());
}
