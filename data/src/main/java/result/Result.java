package result;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.commons.collections4.map.MultiValueMap;

import com.google.common.collect.MinMaxPriorityQueue;

public class Result {
	
	

	private String authorId;
	private int proceedingId;
	
	MinMaxPriorityQueue<ProceeedingPaperSubResult> procPaperSubResutls;

	public Result(String authorId, int proceedingId) {
		
		this.authorId = authorId;
		this.proceedingId = proceedingId;
		procPaperSubResutls = MinMaxPriorityQueue.orderedBy(Collections.reverseOrder()).maximumSize(20).create();
	}

	public void addProceedingPaperSubResult(ProceeedingPaperSubResult procPaperSubResult) {
		this.procPaperSubResutls.add(procPaperSubResult);
	}
}
