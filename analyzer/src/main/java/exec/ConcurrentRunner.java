package exec;

import analyzer.GraphAnalyzer;
import result.Result;

public class ConcurrentRunner implements Runnable {
	
	private String authorId;
	private int proceedingId;
	private Result result;
	
	public Result getResult() {
		return result;
	}

	public ConcurrentRunner(String authorId, int proceedingId) {
		// TODO Auto-generated constructor stub
		this.authorId = authorId;
		this.proceedingId = proceedingId;
	}

	@Override
	public void run() {

		GraphAnalyzer analyzer = new GraphAnalyzer();
		System.out.println(String.format("Proceessing for author: %s | Conference: %d", authorId, proceedingId ));
		// find nodes between these ids
		this.result = analyzer.generateResults(authorId, proceedingId);
		Main.resultQueue.add(result);
		Main.threadCount--;
	}

}
