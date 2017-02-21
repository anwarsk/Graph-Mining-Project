package db.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import data.Author;
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
				String line = author.getAuthorID() + "," + author.getFirstName() + "," + author.getLastName();
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
				String line = keyword.getKeywordId() + "," + keyword.getKeyword();
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
