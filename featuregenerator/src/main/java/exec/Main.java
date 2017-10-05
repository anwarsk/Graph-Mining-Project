package exec;

import data.EvaluationInput;
import data.FeatureGeneratorInput;
import data.FeatureGeneratorOutput;
import db.csv.CSVAccessLayer;
import environment.Constant;
import featuregenerator.GraphFeatureGenerator;

public class Main {

	public static void main(String[] args) {
		
		// (1) - Read the feature generator input
		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		FeatureGeneratorInput featureGeneratorInput = csvAccessLayer.readFeatureGeneratorInput(Constant.FEATURE_GENERATOR_INPUT_FILE);
		
		// (2) - Generate distance features 
		GraphFeatureGenerator graphFeatureGenerator = new GraphFeatureGenerator();
		FeatureGeneratorOutput featureGeneratorOutput = graphFeatureGenerator.generateDistanceFeature(featureGeneratorInput);
		
		csvAccessLayer.writeFeatureGeneratorOutput(Constant.FEATURE_GENERATOR_OUTPUT_FILE, featureGeneratorOutput);
	}

}
