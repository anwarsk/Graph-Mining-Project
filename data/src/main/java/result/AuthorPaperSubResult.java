package result;

import java.util.Collections;
import java.util.PriorityQueue;

public class AuthorPaperSubResult {

	int authorArticleId;
	double score;
	
	PriorityQueue<KeywordSubResult> keywords = new PriorityQueue<KeywordSubResult>(20, Collections.reverseOrder());
}
