package result;

public class KeywordSubResult implements Comparable<KeywordSubResult>{

	
	private int keywordId;
	private double score;
	
	public KeywordSubResult(int keywordId, double score) {
		this.keywordId = keywordId;
		this.score = score;
	}

	public int getKeywordId() {
		return keywordId;
	}


	public double getScore() {
		return score;
	}

	public int compareTo(KeywordSubResult that) {
		
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
