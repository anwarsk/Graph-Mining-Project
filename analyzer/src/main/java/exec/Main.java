package exec;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import analyzer.GraphAnalyzer;

import java.util.Queue;

import data.EvaluationInput;
import db.csv.CSVAccessLayer;
import environment.Constant;
import processor.ResultProcessor;
import processor.ResultSerializer;
import result.Result;

public class Main {

	//private static final String INPUT_FILE_PATH = "D:\\Work\\Database\\CSV\\evaluation_aid_proc_id_gt_5.csv";
	private static final String INPUT_FILE_PATH = Constant.EVALUATION_INPUT_FILE;
	public static Queue<Result> resultQueue;
	public static int threadCount;
	public static void main(String[] args) {

		// Read the evaluation file
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		EvaluationInput evaluationInput = csvAccessLayer.readEvaluationInput(INPUT_FILE_PATH);
		System.out.println("INPUT SIZE: " + evaluationInput.size());

		resultQueue = new LinkedList<Result>();
		//List<Result> results = new ArrayList<Result>();

		ResultProcessor resultProcessor = new ResultProcessor();
		ResultSerializer resultSerializer = new ResultSerializer();
		// Send the input to analyzer for analysis
		Iterator<Entry<String, Integer>> inputIterator = evaluationInput.getIterator();
		threadCount = 0;
		int maxThreadCount = 16;
		GraphAnalyzer.intialize();

		while(inputIterator.hasNext())
		{
			do
			{
				Entry<String, Integer> entry = inputIterator.next();
				String authorId = entry.getKey(); //"a_37712";
				int proceedingId = entry.getValue(); //1148170;

				ConcurrentRunner concurrentRunner = new ConcurrentRunner(authorId, proceedingId);
				Thread thread = new Thread(concurrentRunner);
				thread.start();
				threadCount++;
			}
			while(threadCount < maxThreadCount);

			while(resultQueue.isEmpty())
			{
				try 
				{
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Result result = resultQueue.poll();
			resultProcessor.process(result);
			resultSerializer.writeResultToFile(result);
		}
		
		while(threadCount > 0 || !resultQueue.isEmpty())
		{
			if(!resultQueue.isEmpty())
			{
				Result result = resultQueue.poll();
				resultProcessor.process(result);
				resultSerializer.writeResultToFile(result);
			}
			else
			{
				try 
				{
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//GraphAnalyzer.shutdown();
	}

}
