package data;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.map.MultiValueMap;

public class PaperReferenceRelationStore {

	private MultiValueMap<Integer, Integer> articleIdToReferenceIdMap;
	
	public PaperReferenceRelationStore()
	{
		this.articleIdToReferenceIdMap = new MultiValueMap<Integer, Integer>();
	}
	
	public Set<Integer> getArticleIds()
	{
		return this.articleIdToReferenceIdMap.keySet();
	}
	
	public List<Integer> getListOfReferenceIds(Integer articleId)
	{
		return (List<Integer>) this.articleIdToReferenceIdMap.get(articleId);
	}
	
	public void addRelations(Integer articleId, Integer ReferenceArticlaId)
	{
		this.articleIdToReferenceIdMap.put(articleId, ReferenceArticlaId);
	}

	public int size() {
		// TODO Auto-generated method stub
		return this.articleIdToReferenceIdMap.size();
	}
}
