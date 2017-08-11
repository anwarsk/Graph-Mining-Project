package data;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.map.MultiValueMap;

public class EvaluationInput {

	/**
	 * TO-DO: MultiValueMap class is deprecated in future versions. Need to change it to
	 * MultiValuedMap which is a interface and implemented by some concrete classes.
	 */
	private MultiValueMap<String, Integer> authorIdToProcIdMap;
	
	public EvaluationInput()
	{
		this.authorIdToProcIdMap = new MultiValueMap<String, Integer>();
	}
	
	public Set<String> getAuthorIds()
	{
		return this.authorIdToProcIdMap.keySet();
	}
	
	public List<Integer> getListOfProceedingIds(String authorId)
	{
		return (List<Integer>) this.authorIdToProcIdMap.get(authorId);
	}
	
	public void addEntry(String authorId, Integer proceedingId)
	{
		this.authorIdToProcIdMap.put(authorId, proceedingId);
		
	}

	public int size() {
		// TODO Auto-generated method stub
		return this.authorIdToProcIdMap.size();
	}
	
	public Iterator<Entry<String, Integer>> getIterator()
	{
		return this.authorIdToProcIdMap.iterator();
	}
	
}
