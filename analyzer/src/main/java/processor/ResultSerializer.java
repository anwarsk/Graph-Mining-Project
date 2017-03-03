package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import environment.Constant;
import result.Result;

public class ResultSerializer {

	public void writeResultToFile(Result result)
	{
		try {

			Gson gson = new Gson();
			String json = gson.toJson(result, Result.class);
			String resultJsonFileName = String.format(Constant.JSON_FILE_NAME_FORMAT, result.getAuthorId(), result.getProceedingId());
			String resultJsonFilePath = Constant.JSON_OUTPUT_DIRECTORY + "/" + resultJsonFileName; 
			PrintWriter fileWriter = new PrintWriter(new File(resultJsonFilePath));
			fileWriter.write(json);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
