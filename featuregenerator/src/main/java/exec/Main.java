package exec;

import java.util.Iterator;
import java.util.Map.Entry;

import data.EvaluationInput;
import data.FeatureGeneratorInput;
import data.FeatureGeneratorOutput;
import db.csv.CSVAccessLayer;
import environment.Constant;
import featuregenerator.GraphFeatureGenerator;
import featuregenerator.GraphFeatureGeneratorConcurrent;

public class Main {

	public static void main(String[] args) {

		// (1) - Read the feature generator input
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		FeatureGeneratorInput featureGeneratorInput = csvAccessLayer.readFeatureGeneratorInput(Constant.FEATURE_GENERATOR_INPUT_FILE);


		// (2) - Generate distance features 
		Iterator<Entry<String, Integer>> inputIterator = featureGeneratorInput.getIterator();

		while(inputIterator.hasNext() || GraphFeatureGeneratorConcurrent.isThreadRunning())
		{
			try
			{
				if(!GraphFeatureGeneratorConcurrent.isMaxThreadCountReached() && inputIterator.hasNext())
				{
					Entry<String, Integer> input = inputIterator.next();
					String authorId = input.getKey();
					int proceedingId = input.getValue();

					GraphFeatureGeneratorConcurrent graphFeatureGeneratorConcurrent = new GraphFeatureGeneratorConcurrent(authorId, proceedingId);
					Thread childThread = new Thread(graphFeatureGeneratorConcurrent);
					childThread.start();
				}
				else
				{
					Thread.sleep(50);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		csvAccessLayer.writeFeatureGeneratorOutput(Constant.FEATURE_GENERATOR_OUTPUT_FILE, FeatureGeneratorOutput.getInstance());
	}

}
