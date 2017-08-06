package environment;

public class Constant {

	
	/**    AZURE 
	public static final String NEO_GRAPH_DB_PATH = "/home/anwar/project/database/graph.acm2015";
	public static final String JSON_OUTPUT_DIRECTORY = "/home/anwar/project/output/output_v6.0_gt_3/json";
	public static final String EVALUATION_INPUT_FILE = "/home/anwar/project/input/evaluation_data_gt_3_id.csv";
	public static final String EVALUATION_OUTPUT_FILE = "/home/anwar/project/output/output_v6.0_gt_3/results.csv";
	// */
	
	 /// *
	public static final String NEO_GRAPH_DB_PATH = "/Users/anwar/GraphMiner/data/graph.acm2015.complete";
	public static final String JSON_OUTPUT_DIRECTORY = "/Users/anwar/GraphMiner/ouput/json";	
	public static final String EVALUATION_TRUTH_FILE = "/Users/anwar/GraphMiner/data/evaluation_data_unique1.csv";
	public static final String EVALUATION_OUTPUT_FILE = "/Users/anwar/GraphMiner/ouput/result/results.csv";
	//*/
	
	public static final String JSON_FILE_NAME_FORMAT = "%s_%d.json";
	public static final int START_PATH_DEPTH = 2;
	public static final long MAX_PATHS = 1000000;
	public static final int MAX_PATH_DEPTH = 8;
	public static final double RANDON_WALK_PROB_CUTOFF = 1e-80;
	public static final String EVALUATION_RESULT_FILE = "/Users/anwar/GraphMiner/ouput/result/FinalResult.csv";
}
