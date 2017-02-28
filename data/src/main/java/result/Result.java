package result;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.commons.collections4.map.MultiValueMap;

public class Result {
	
	private String authorId;
	private int proceedingId;
	
	PriorityQueue<ProceeedingPaperSubResult> keywords = new PriorityQueue<ProceeedingPaperSubResult>(20, Collections.reverseOrder());
	//private Map<>

}
