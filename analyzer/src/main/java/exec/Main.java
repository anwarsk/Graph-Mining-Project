package exec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import analyzer.GraphAnalyzer;
import data.EvaluationInput;
import db.csv.CSVAccessLayer;
import processor.ResultProcessor;
import result.Result;

public class Main {

	//private static final String INPUT_FILE_PATH = "D:\\Work\\Database\\CSV\\evaluation_aid_proc_id_gt_5.csv";
	private static final String INPUT_FILE_PATH = "D:\\Work\\Database\\CSV\\evaluation_aid_proc_id_gt_5 _test.csv";
	
	public static void main(String[] args) {
	
		// Read the evaluation file
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		EvaluationInput evaluationInput = csvAccessLayer.readEvaluationInput(INPUT_FILE_PATH);
		System.out.println(evaluationInput.size());
		
		
		GraphAnalyzer analyzer = new GraphAnalyzer();
		List<Result> results = new ArrayList<Result>();
		
		ResultProcessor resultProcessor = new ResultProcessor();
		// Send the input to analyzer for analysis
		Iterator<Entry<String, Integer>> inputIterator = evaluationInput.getIterator();
		while(inputIterator.hasNext())
		{
			Entry<String, Integer> entry = inputIterator.next();
			String authorId = "a_14178";//entry.getKey();
			int proceedingId = 1289612; //entry.getValue();
			
			// find nodes between these ids
			Result result = analyzer.generateResults(authorId, proceedingId);
			
			resultProcessor.process(result);
			results.add(result);
			return;
		}
	}

}
