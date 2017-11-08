package environment;

public class Constant {


	static String SYSTEM = "GCLOUD";
	static String PROJECT_PATH = "null";

	static
	{
		if(SYSTEM.equalsIgnoreCase("HOME"))
		{
			PROJECT_PATH = "/Users/anwar/java_workspace/Graph-Mining-Project";	
		}
		else if (SYSTEM.equalsIgnoreCase("JETSTREAM"))
		{
			PROJECT_PATH = "/root/GraphSearch/";	
		}
		else if (SYSTEM.equalsIgnoreCase("GCLOUD"))
		{
			PROJECT_PATH = "/home/mail2anwarshaikh/GraphMiner";	
		}
	}

	/**    AZURE 
	public static final String NEO_GRAPH_DB_PATH = "/home/anwar/project/database/graph.acm2015";
	public static final String JSON_OUTPUT_DIRECTORY = "/home/anwar/project/output/output_v6.0_gt_3/json";
	public static final String EVALUATION_INPUT_FILE = "/home/anwar/project/input/evaluation_data_gt_3_id.csv";
	public static final String EVALUATION_OUTPUT_FILE = "/home/anwar/project/output/output_v6.0_gt_3/results.csv";
	// */

	/// *
	//public static final String NEO_GRAPH_DB_PATH = "/Users/anwar/GraphMiner/data/graph.acm2015.complete";
	//public static final String NEO_GRAPH_DB_PATH = "/root/GraphSearch/data/graph.acm2015.complete";
	public static final String JSON_OUTPUT_DIRECTORY = "/Users/anwar/GraphMiner/ouput/json";	
	public static final String EVALUATION_TRUTH_FILE = "/Users/anwar/GraphMiner/data/evaluation_data_unique1.csv";
	public static final String EVALUATION_OUTPUT_FILE = "/Users/anwar/GraphMiner/ouput/result/results.csv";




	public static final String PROJECT_DIRECTORY = PROJECT_PATH;

	public static final String NEO_GRAPH_DB_PATH = PROJECT_DIRECTORY + "/graphdb/graph.acm2015.complete";
	public static final String FEATURE_GENERATOR_INPUT_FILE = PROJECT_DIRECTORY + "/input/author_proceeding_cite_gt_1.csv";
	public static final String FEATURE_GENERATOR_OUTPUT_FILE = PROJECT_DIRECTORY + "/output/featureOutput_maxDepth_6.csv";
	public static final String FEATURE_GENERATOR_PROCESS_LOG_FILE = PROJECT_DIRECTORY + "/output/feature_generator_process_6.csv";

	//*/

	public static final String JSON_FILE_NAME_FORMAT = "%s_%d.json";

	// This is depth at which analyzer start looking for path between author paper and conference paper
	public static final int START_PATH_DEPTH = 2;

	// This is max number of paths analyzer finds between target and source
	public static final long MAX_PATHS = 100;

	// This is max depth until which analyzer look for path between author paper and conference paper
	public static final int MAX_PATH_DEPTH = 6;

	//
	public static final int MAX_THREAD_COUNT = 10;

	public static final double RANDON_WALK_PROB_CUTOFF = 1e-6;

	// Evaluation result output file
	public static final String EVALUATION_RESULT_FILE = "/Users/anwar/GraphMiner/ouput/result/FinalResult.csv";

	// Maximum number of shortest path to retrieve (used in feature generator)
	public static final int SHORTEST_PATH_COUNT = 1;

	/**
	 * Following constants defines the structure of Evaluation Input file.
	 * Columns depicting different values
	 */

	public static final int GROUND_TRUTH_AUTHOR_ID_COLUMN_INDEX = 0;
	public static final int GROUND_TRUTH_AUTHOR_ARTICLE_ID_COLUMN_INDEX = 1;
	public static final int GROUND_TRUTH_PROC_ARTICLE_ID_COLUMN_INDEX = 2;
	public static final int GROUND_TRUTH_PROCEEDING_ID_COLUMN_INDEX = 3;
	public static final int GROUND_TRUTH__AUTHOR_PROC_COUNT_COLUMN_INDEX = 4;

	public static final int FEATURE_GENERATOR_AUTHOR_ID_COLUMN_INDEX = 0;
	public static final int FEATURE_GENERATOR_PROCEEDING_ID_COLUMN_INDEX = 1;
	
	/** CONVERTER - EDGELIST */
	public static final String EDGE_LIST_OUTPUT_FILE = PROJECT_DIRECTORY + "/output/graph.edgelist";
	public static final String EDGE_LIST_MAP_FILE = PROJECT_DIRECTORY + "/output/graph.map";
	public static final int MAX_PATH_COUNT = 1000000; // Max path 1M
	


}
