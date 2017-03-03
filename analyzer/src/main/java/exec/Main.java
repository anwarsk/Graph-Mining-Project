package exec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import analyzer.GraphAnalyzer;
import data.EvaluationInput;
import db.csv.CSVAccessLayer;
import environment.Constant;
import processor.ResultProcessor;
import processor.ResultSerializer;
import result.Result;

public class Main {

	//private static final String INPUT_FILE_PATH = "D:\\Work\\Database\\CSV\\evaluation_aid_proc_id_gt_5.csv";
	private static final String INPUT_FILE_PATH = Constant.EVALUATION_INPUT_FILE;

	public static void main(String[] args) {

		// Read the evaluation file
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		EvaluationInput evaluationInput = csvAccessLayer.readEvaluationInput(INPUT_FILE_PATH);
		System.out.println(evaluationInput.size());


		GraphAnalyzer analyzer = new GraphAnalyzer();
		List<Result> results = new ArrayList<Result>();

		ResultProcessor resultProcessor = new ResultProcessor();
		ResultSerializer resultSerializer = new ResultSerializer();
		// Send the input to analyzer for analysis
		Iterator<Entry<String, Integer>> inputIterator = evaluationInput.getIterator();
		while(inputIterator.hasNext())
		{
			Entry<String, Integer> entry = inputIterator.next();
			String authorId = entry.getKey(); //"a_37712";
			int proceedingId = entry.getValue(); //1148170;

			System.out.println(String.format("Proceessing for author: %s | Conference: %d", authorId, proceedingId ));
			// find nodes between these ids
			Result result = analyzer.generateResults(authorId, proceedingId);

			resultProcessor.process(result);
			resultSerializer.writeResultToFile(result);
		}
	}

}
