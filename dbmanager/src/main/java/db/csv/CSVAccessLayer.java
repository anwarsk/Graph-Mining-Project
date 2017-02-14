package db.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import data.Author;

public class CSVAccessLayer {

	private final String DB_PATH = "/media/anwar/825ED72B5ED716AF/Work/Database/CSV/";

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

}
