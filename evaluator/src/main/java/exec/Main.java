package exec;

import java.util.Iterator;
import java.util.Map.Entry;

import data.EvaluationInput;
import db.csv.CSVAccessLayer;
import db.json.JSONAccessLayer;
import environment.Constant;
import evaluator.Evaluator;
import result.Result;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
			EvaluationInput evaluationInput = csvAccessLayer.readEvaluationInput(Constant.EVALUATION_TRUTH_FILE);
			System.out.println("INPUT SIZE: " + evaluationInput.size());

			JSONAccessLayer jsonAccessLayer = new JSONAccessLayer();
			
			Iterator<Entry<String, Integer>> inputIterator = evaluationInput.getIterator();
			String jsonFileDirectory = Constant.JSON_OUTPUT_DIRECTORY;
			Evaluator.initialize(Constant.EVALUATION_RESULT_FILE);
			while(inputIterator.hasNext())
			{
				Entry<String, Integer> input = inputIterator.next();
				String jsonFileName = String.format(Constant.JSON_FILE_NAME_FORMAT, input.getKey(), input.getValue());
				String jsonFilePath = jsonFileDirectory + "/" + jsonFileName;
				Result result = jsonAccessLayer.readResultFromJSONFile(jsonFilePath);
				Evaluator evaluator = new Evaluator();
				evaluator.evaluate(result);
				evaluator.writeLocalResults();
			}
			
			Evaluator.writeGlobalResults();
			 
			System.out.println("Completed.");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
