package db.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import data.Author;
import data.EvaluationInput;
import data.Keyword;
import data.Paper;

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

				evaluationInput.addEntry(data[0], Integer.parseInt(data[1]));
				
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

}
