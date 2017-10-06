package data;

import java.util.Map;

public class FeatureEntry {
	public String authorId;
	public int articleId;
	public int distance;
	public double randomWalkProbability;
	public double currentScoringMethod = 0;
	public Map<Integer, Integer> pathLengthToCountMap = null;
}
