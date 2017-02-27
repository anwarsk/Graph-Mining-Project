package data;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.map.MultiValueMap;

public class JournalPaperRelationStore {

	private MultiValueMap<Integer, Integer> journalUidToArticleIdMap;

	public JournalPaperRelationStore()
	{
		this.journalUidToArticleIdMap = new MultiValueMap<Integer, Integer>();
	}

	public Set<Integer> getJournalUIds()
	{
		return this.journalUidToArticleIdMap.keySet();
	}

	public List<Integer> getListOfArticleIds(Integer journalUId)
	{
		return (List<Integer>) this.journalUidToArticleIdMap.get(journalUId);
	}

	public void addRelations(Integer journalUId, Integer articleId)
	{
		this.journalUidToArticleIdMap.put(journalUId, articleId);
	}

	public int size() {
		// TODO Auto-generated method stub
		return this.journalUidToArticleIdMap.size();
	}

}
