package result;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.collect.MinMaxPriorityQueue;

public class AuthorPaperSubResult implements Comparable<AuthorPaperSubResult> {


	private int authorArticleId;
	private double score;
	MinMaxPriorityQueue<KeywordSubResult> keywords;

	public AuthorPaperSubResult(int authorArticleId, double authorPaperScore) 
	{
		this.authorArticleId = authorArticleId;
		this.score = authorPaperScore;
		this.keywords = MinMaxPriorityQueue.orderedBy(Collections.reverseOrder()).maximumSize(20).create();
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

