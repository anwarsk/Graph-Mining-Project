package environment;

public class Constant {

	
	/**    AZURE */
	public static final String NEO_GRAPH_DB_PATH = "/home/anwar/project/data/graph.acm2015";
	public static final String JSON_OUTPUT_DIRECTORY = "/home/anwar/project/output_v5_gt_3/json";
	public static final String EVALUATION_INPUT_FILE = "/home/anwar/project/input/evaluation_data_gt_3_id.csv";
	public static final String EVALUATION_OUTPUT_FILE = "/home/anwar/project/output_v5_gt_3/results.csv";
	
	
	/*
	public static final String NEO_GRAPH_DB_PATH = "./../../../Database/graph.acm2015";
	public static final String JSON_OUTPUT_DIRECTORY = "D:/output/json";	
	public static final String EVALUATION_INPUT_FILE = "C:\\Users\\send2\\Desktop\\evaluation_data_gt_4_id.csv";
	public static final String EVALUATION_OUTPUT_FILE = "D:/results.csv";
	*/
	public static final String JSON_FILE_NAME_FORMAT = "%s_%d.json";
	public static final int START_PATH_DEPTH = 2;
	public static final long MAX_PATHS = 1000000;
	public static final int MAX_PATH_DEPTH = 6;
	public static final double RANDON_WALK_PROB_CUTOFF = 0.0000000001;
}
