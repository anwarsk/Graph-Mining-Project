package featuregenerator;

import data.FeatureGeneratorOutput;
import db.csv.CSVAccessLayer;
import environment.Constant;

public class GraphFeatureGeneratorConcurrent implements Runnable {

	private String authorId;
	private int proceedingId;
	private static int currentThreadCount = 0;

	public GraphFeatureGeneratorConcurrent(String authorId, int proceedingId) {
		this.authorId = authorId;
		this.proceedingId = proceedingId;
	}

	public FeatureGeneratorOutput generateGraphFeatures()
	{
		GraphFeatureGenerator graphFeatureGenerator = new GraphFeatureGenerator();
		FeatureGeneratorOutput featureGeneratorOutput = 
				graphFeatureGenerator.generateGraphFeatures(this.authorId, this.proceedingId);
		
		return featureGeneratorOutput;
	}
	
	private synchronized static void writeFeaturesToFile(FeatureGeneratorOutput featureGeneratorOutput)
	{
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writeFeatureGeneratorOutput(Constant.FEATURE_GENERATOR_OUTPUT_FILE, featureGeneratorOutput);
	}
	
	private synchronized static void writeAuthorProceedingProcessed(String authorId2, int proceedingId2) {
		// TODO Auto-generated method stub
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writeFeatureGeneratorProcessLog(Constant.FEATURE_GENERATOR_PROCESS_LOG_FILE, authorId2, proceedingId2);

	}



	public static boolean isMaxThreadCountReached()
	{
		boolean isMaxThreadCountReached = true;

		if(currentThreadCount < Constant.MAX_THREAD_COUNT)
		{
			try {
				Thread.sleep(10);
				if(currentThreadCount < Constant.MAX_THREAD_COUNT)
				{
					isMaxThreadCountReached = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isMaxThreadCountReached;
	}

	public static boolean isThreadRunning()
	{
		boolean isThreadRunning = true;
		if(currentThreadCount == 0)
		{
			try {

				Thread.sleep(10);
				if(currentThreadCount == 0)
				{
					isThreadRunning = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isThreadRunning;
	}

	@Override
	public void run() {
		increaseThreadCount();
		System.out.println("Processing Author-Proceeding" + authorId +"-" +proceedingId);
		FeatureGeneratorOutput featureGeneratorOutput = this.generateGraphFeatures();
		writeFeaturesToFile(featureGeneratorOutput);
		writeAuthorProceedingProcessed(this.authorId, this.proceedingId);
		decreaseThreadCount();
	}
	

	public static synchronized void increaseThreadCount()
	{
		currentThreadCount++;
	}
	
	public static synchronized void decreaseThreadCount()
	{
		System.gc ();
		//System.runFinalization ();
		currentThreadCount--;
	}

}
