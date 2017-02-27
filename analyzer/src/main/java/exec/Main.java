package exec;

import data.EvaluationInput;
import db.csv.CSVAccessLayer;

public class Main {

	private static final String INPUT_FILE_PATH = "D:\\Work\\Database\\CSV\\evaluation_aid_proc_id_gt_5.csv";
	
	public static void main(String[] args) {
	
		// Read the evaluation file
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		EvaluationInput evaluationInput = csvAccessLayer.readEvaluationInput(INPUT_FILE_PATH);
		System.out.println(evaluationInput.size());
		
		
		
	}

}
