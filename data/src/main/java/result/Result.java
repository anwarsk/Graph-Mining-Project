package result;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.commons.collections4.map.MultiValueMap;

public class Result {
	
	

	private String authorId;
	private int proceedingId;
	
	PriorityQueue<ProceeedingPaperSubResult> procPaperSubResutls;

	public Result(String authorId, int proceedingId) {
		
		this.authorId = authorId;
		this.proceedingId = proceedingId;
		procPaperSubResutls = new PriorityQueue<ProceeedingPaperSubResult>(20, Collections.reverseOrder());
	}

	public void addProceedingPaperSubResult(ProceeedingPaperSubResult procPaperSubResult) {
		this.procPaperSubResutls.add(procPaperSubResult);
	}
}
