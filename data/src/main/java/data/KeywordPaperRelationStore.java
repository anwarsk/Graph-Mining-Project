package data;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.map.MultiValueMap;

public class KeywordPaperRelationStore {

	private MultiValueMap<Long, Long> articleIdToKeywordIdMap;
	
	public KeywordPaperRelationStore()
	{
		this.articleIdToKeywordIdMap = new MultiValueMap<Long, Long>();
	}
	
	public Set<Long> getArticleIds()
	{
		return this.articleIdToKeywordIdMap.keySet();
	}
	
	public List<Long> getListOfRelatedKeywords(long articleId)
	{
		return (List<Long>) this.articleIdToKeywordIdMap.get(articleId);
	}
	
	public void addRelations(long articleId, long keywordId)
	{
		this.articleIdToKeywordIdMap.put(articleId, keywordId);
	}

	public int size() {
		// TODO Auto-generated method stub
		return this.articleIdToKeywordIdMap.size();
	}
}
