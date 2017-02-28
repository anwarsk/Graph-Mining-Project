package data;

import java.util.Map;

import org.apache.commons.collections4.map.MultiValueMap;

public class Result {
	
	private String authorId;
	private int proceedingId;
	private MultiValueMap<Integer, Integer> proceedingArticleIdToKeywordIdMap;
	private Map<Integer, Float> proceedingArticleIdToScoreMap;
	//private Map<>

}
