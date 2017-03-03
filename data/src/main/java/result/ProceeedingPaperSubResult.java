package result;

import java.io.Serializable;
import java.util.Collections;

import com.google.common.collect.MinMaxPriorityQueue;

public class ProceeedingPaperSubResult implements Serializable, Comparable<ProceeedingPaperSubResult> {

	private int proceedingArticleId;
	private double score;

	private MinMaxPriorityQueue<AuthorPaperSubResult> authorPaperSubResults;

	public ProceeedingPaperSubResult(int procArticleId) {

		this.proceedingArticleId = procArticleId;
		authorPaperSubResults = MinMaxPriorityQueue.orderedBy(Collections.reverseOrder()).maximumSize(20).create();
	}

	public void addAuthorPaperSubResult(AuthorPaperSubResult authorPaperSubResult) {

		this.authorPaperSubResults.add(authorPaperSubResult);
	}

	public int getProceedingArticleId() {
		return proceedingArticleId;
	}

	public double getScore() {
		return score;
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

	public AuthorPaperSubResult getTopRelatedAuthorPaperSubResult() {
		// TODO Auto-generated method stub
		return this.authorPaperSubResults.peekFirst();
	}
}
