package db.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import data.Author;
import data.EvaluationInput;
import data.FeatureEntry;
import data.FeatureGeneratorInput;
import data.FeatureGeneratorOutput;
import data.Keyword;
import data.Paper;
import environment.Constant;

public class CSVAccessLayer {

	private final String DB_PATH = "./../../../Database/CSV/";

	public void writeAuthorsToCSV(List<Author> authorList)
	{
		assert authorList.isEmpty() == false : "Empty Author List";

		String fileName = "author.csv";
		String filePath = DB_PATH + fileName;
		PrintWriter fileWriter;
		try {
			fileWriter = new PrintWriter(new File(filePath));

			fileWriter.write("author_id,first_name,last_name\n");
			for (Author author : authorList)
			{
				String line = author.getAuthorID() + ",\"" + author.getFirstName() + "\",\"" + author.getLastName()+"\"";
				fileWriter.println(line);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writePapersToCSV(List<Paper> paperList) {
		// TODO Auto-generated method stub

		assert paperList.isEmpty() == false : "Empty Author List";

		String fileName = "paper.csv";
		String filePath = DB_PATH + fileName;
		PrintWriter fileWriter;
		try {
			fileWriter = new PrintWriter(new File(filePath));

			fileWriter.write("paper_id,article_id\n");
			for (Paper paper : paperList)
			{
				String line = paper.getPaperId() + "," + paper.getArticleId();
				fileWriter.println(line);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeKeywordsToCSV(List<Keyword> keywordList) {

		assert keywordList.isEmpty() == false : "Empty Author List";

		String fileName = "keyword.csv";
		String filePath = DB_PATH + fileName;
		PrintWriter fileWriter;
		try {
			fileWriter = new PrintWriter(new File(filePath));

			fileWriter.write("keyword_id,keyword\n");
			for (Keyword keyword : keywordList)
			{
				String line = keyword.getKeywordId() + ",\"" + keyword.getKeyword() + "\"";
				fileWriter.println(line);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public EvaluationInput readEvaluationInput(String inputFilePath)
	{
		assert inputFilePath != null : "Null input file path";
		assert inputFilePath != "" : "Empty input file path";

		EvaluationInput evaluationInput = new EvaluationInput();

		String line = "";
		
		try (BufferedReader buffReader = new BufferedReader(new FileReader(inputFilePath))) 
		{
			buffReader.readLine();
			
			while ((line = buffReader.readLine()) != null) {

				// use comma as separator
				String[] data = line.split(",");
				
				String authorId = data[Constant.GROUND_TRUTH_AUTHOR_ID_COLUMN_INDEX];
				int proceedingId = Integer.parseInt(data[Constant.GROUND_TRUTH_PROCEEDING_ID_COLUMN_INDEX]);
				
				evaluationInput.addEntry(authorId, proceedingId);
				
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return evaluationInput;
	}
	
	public FeatureGeneratorInput readFeatureGeneratorInput(String inputFilePath)
	{
		assert inputFilePath != null : "Null input file path";
		assert inputFilePath != "" : "Empty input file path";

		FeatureGeneratorInput featureGeneratorInput = new FeatureGeneratorInput();

		String line = "";
		
		try (BufferedReader buffReader = new BufferedReader(new FileReader(inputFilePath))) 
		{
			buffReader.readLine();
			
			while ((line = buffReader.readLine()) != null) {

				// use comma as separator
				String[] data = line.split(",");
				
				String authorId = data[Constant.FEATURE_GENERATOR_AUTHOR_ID_COLUMN_INDEX];
				int proceedingId = Integer.parseInt(data[Constant.FEATURE_GENERATOR_PROCEEDING_ID_COLUMN_INDEX]);
				
				featureGeneratorInput.addEntry(authorId, proceedingId);
				
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return featureGeneratorInput;
	}
	
	public void writeFeatureGeneratorOutput(String outputFilePath, FeatureGeneratorOutput featureGeneratorOutput)
	{
		assert outputFilePath != null : "Null output file path";
		assert outputFilePath != "" : "Empty output file path";
		assert featureGeneratorOutput != null : "Null featureGeneratorOutput object";
		
		PrintWriter fileWriter;
		try {
			fileWriter = new PrintWriter(new File(outputFilePath));
			String pathLengthCountHeader = "";
			for(int pathLength=1; pathLength <= Constant.MAX_PATH_DEPTH; pathLength++)
			{
				pathLengthCountHeader += ",PathLength=" + pathLength;  
			}
			
			fileWriter.write("AuthorId,ArticleId,"
					+ "ShortestDistance,"
					+ "RandomWalkProbability,"
					+ "CurrentScoringMethod"
					+ pathLengthCountHeader 
					+ "\n");
			for (FeatureEntry featureEntry : featureGeneratorOutput.getListOfFeatureEntry())
			{
				String pathLengthCountString = "";
				Map<Integer,Integer> pathLengthToCountMap = featureEntry.pathLengthToCountMap;
				for(int pathLength=1; pathLength <= Constant.MAX_PATH_DEPTH; pathLength++)
				{
					pathLengthCountString += "," + pathLengthToCountMap.getOrDefault(pathLength, 0); 
				}
				
				String line = featureEntry.authorId + "," + 
							  featureEntry.articleId + "," + 
							  featureEntry.distance + "," +
							  featureEntry.randomWalkProbability + "," +
							  featureEntry.currentScoringMethod +
							  pathLengthCountString;
				fileWriter.println(line);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
