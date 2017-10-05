package exec;

import data.EvaluationInput;
import data.FeatureGeneratorInput;
import db.csv.CSVAccessLayer;
import environment.Constant;
import featuregenerator.GraphFeatureGenerator;

public class Main {

	public static void main(String[] args) {
		
		// (1) - Read the feature generator input
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		FeatureGeneratorInput featureGeneratorInput = csvAccessLayer.readFeatureGenerationInput(Constant.FEATURE_GENERATOR_INPUT_FILE);
		
		// (2) - Generate distance features 
		GraphFeatureGenerator graphFeatureGenerator = new GraphFeatureGenerator();
		graphFeatureGenerator.generateDistanceFeature(featureGeneratorInput);
	}

}
