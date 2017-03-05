package result;

import java.util.Collections;
import java.util.Iterator;

import com.google.common.collect.MinMaxPriorityQueue;

public class Result {
	
	private String authorId;
	private int proceedingId;
	
	MinMaxPriorityQueue<ProceeedingPaperSubResult> procPaperSubResults;

	public Result(String authorId, int proceedingId) {
		
		this.authorId = authorId;
		this.proceedingId = proceedingId;
		procPaperSubResults = MinMaxPriorityQueue.orderedBy(Collections.reverseOrder()).maximumSize(20).create();
	}
	
	public String getAuthorId() {
		return authorId;
	}

	public int getProceedingId() {
		return proceedingId;
	}

	public void addProceedingPaperSubResult(ProceeedingPaperSubResult procPaperSubResult) {
		this.procPaperSubResults.add(procPaperSubResult);
	}
	
	public Iterator<ProceeedingPaperSubResult> getProceedingPaperSubResultIterator()
	{
		return this.procPaperSubResults.iterator();
	}
}
