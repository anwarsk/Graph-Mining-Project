package result;

import java.util.Collections;
import java.util.PriorityQueue;

public class ProceeedingPaperSubResult implements Comparable<ProceeedingPaperSubResult> {

	private int proceedingArticleId;
	private double score;

	private PriorityQueue<AuthorPaperSubResult> authorPaperSubResults;

	public ProceeedingPaperSubResult(int procArticleId) {

		this.proceedingArticleId = procArticleId;
		authorPaperSubResults = new PriorityQueue<AuthorPaperSubResult>(20, Collections.reverseOrder());
	}

	public void addAuthorPaperSubResult(AuthorPaperSubResult authorPaperSubResult) {

		this.authorPaperSubResults.add(authorPaperSubResult);
	}

	public int compareTo(ProceeedingPaperSubResult that) {
		int result = 0;
		if(this.score > that.score)
		{
			result = 1;
		}
		else if(this.score < that.score)
		{
			result = -1;
		}
		return result;
	}

	public void setScore(double procPaperScore) {
		this.score = procPaperScore;
	}
}
