package result;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;

public class AuthorPaperSubResult implements Comparable<AuthorPaperSubResult> {


	private int authorArticleId;
	private double score;
	PriorityQueue<KeywordSubResult> keywords;

	public AuthorPaperSubResult(int authorArticleId, double authorPaperScore) 
	{
		this.authorArticleId = authorArticleId;
		this.score = authorPaperScore;
		this.keywords = new PriorityQueue<KeywordSubResult>(20, Collections.reverseOrder());
	}

	public void addKeywords(Map<Integer, Double> keywordIdToScoreMap)
	{
		for(int keywordId : keywordIdToScoreMap.keySet())
		{
			double score = keywordIdToScoreMap.get(keywordId);
			this.keywords.add(new KeywordSubResult(keywordId, score));
		}

	}

	public int compareTo(AuthorPaperSubResult that) {
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

}

