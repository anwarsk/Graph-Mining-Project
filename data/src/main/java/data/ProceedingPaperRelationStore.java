package data;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.map.MultiValueMap;

public class ProceedingPaperRelationStore {

private MultiValueMap<Integer, Integer> procIdToArticleIdMap;
	
	public ProceedingPaperRelationStore()
	{
		this.procIdToArticleIdMap = new MultiValueMap<Integer, Integer>();
	}
	
	public Set<Integer> getProceedingIds()
	{
		return this.procIdToArticleIdMap.keySet();
	}
	
	public List<Integer> getListOfArticleIds(Integer proceedingId)
	{
		return (List<Integer>) this.procIdToArticleIdMap.get(proceedingId);
	}
	
	public void addRelations(Integer proceedingId, Integer articleId)
	{
		this.procIdToArticleIdMap.put(proceedingId, articleId);
	}

	public int size() {
		// TODO Auto-generated method stub
		return this.procIdToArticleIdMap.size();
	}
}
