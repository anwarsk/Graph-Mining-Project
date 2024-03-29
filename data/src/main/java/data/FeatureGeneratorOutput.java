package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureGeneratorOutput {
	
	private HashMap<String, FeatureEntry> featureOutput;
	//private static FeatureGeneratorOutput instance  = null;
	
	public FeatureGeneratorOutput()
	{
		this.featureOutput = new HashMap<String, FeatureEntry>();
	}
	
//	public static FeatureGeneratorOutput getInstance()
//	{
//		if (instance == null)
//		{
//			instance = new FeatureGeneratorOutput();
//		}
//		
//		return instance;
//	}
	
	FeatureEntry getFeatureEntry(String authorId, int articleId)
	{
		assert authorId != null && authorId.isEmpty() == false : "Invalid author Id";
		assert articleId > 0 : "Invalid Article Id";
		
		FeatureEntry featureEntry = null;
		
		String hash = this.getHash(authorId, articleId);
		featureEntry = this.featureOutput.get(hash);
		
		if(featureEntry == null)
		{
			featureEntry = new FeatureEntry();
			featureEntry.articleId = articleId;
			featureEntry.authorId = authorId;
			featureOutput.put(hash, featureEntry);
		}
		
		return featureEntry;
	}
	
	private String getHash(String authorId, int articleId)
	{
		return authorId + articleId;
	}
	
	public void addDistance(String authorId, int articleId, int distance)
	{
		FeatureEntry featureEntry = this.getFeatureEntry(authorId, articleId);
		featureEntry.distance = distance;
	}
	
	public void addEntry(String authorId, int articleId, 
							int distance, 
							double randomWalkProbability,
							Map<Integer,Integer> pathLengthToCountMap,
							double currentScoringMethod)
	{
		FeatureEntry featureEntry = this.getFeatureEntry(authorId, articleId);
		featureEntry.distance = distance;
		featureEntry.randomWalkProbability = randomWalkProbability;
		featureEntry.pathLengthToCountMap = pathLengthToCountMap;
		featureEntry.currentScoringMethod = currentScoringMethod;
	}
	
	public List<FeatureEntry> getListOfFeatureEntry()
	{
		List<FeatureEntry> featureEntryList = new ArrayList<FeatureEntry>();
		featureEntryList.addAll(this.featureOutput.values());
		
		return featureEntryList;
	}
	

}