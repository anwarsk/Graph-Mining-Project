package featuregenerator;

import environment.Constant;

public class GraphFeatureGeneratorConcurrent implements Runnable {

	private String authorId;
	private int proceedingId;
	private static int currentThreadCount = 0;

	public GraphFeatureGeneratorConcurrent(String authorId, int proceedingId) {
		this.authorId = authorId;
		this.proceedingId = proceedingId;
	}

	public void generateGraphFeatures()
	{
		GraphFeatureGenerator graphFeatureGenerator = new GraphFeatureGenerator();
		graphFeatureGenerator.generateGraphFeatures(this.authorId, this.proceedingId);
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
		this.generateGraphFeatures();
		decreaseThreadCount();
	}
	
	public static synchronized void increaseThreadCount()
	{
		currentThreadCount++;
	}
	
	public static synchronized void decreaseThreadCount()
	{
		currentThreadCount--;
	}

}
