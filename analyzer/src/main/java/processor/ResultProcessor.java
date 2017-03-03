package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import environment.Constant;
import result.AuthorPaperSubResult;
import result.KeywordSubResult;
import result.ProceeedingPaperSubResult;
import result.Result;

public class ResultProcessor {

	private final String RESULT_FILE_PATH = Constant.EVALUATION_OUTPUT_FILE;
	
	public void process(Result result)
	{
		assert result !=  null : "Invalid Result";
		
		String authorId = result.getAuthorId();
		int proceedingId = result.getProceedingId();
		
		Iterator<ProceeedingPaperSubResult> proceedingPaperIterator = result.getProceedingPaperSubResultIterator();
		while(proceedingPaperIterator.hasNext())
		{
			String resultString = "";
			resultString += authorId + "," + proceedingId;
			
			ProceeedingPaperSubResult procPaperSubResult = proceedingPaperIterator.next();
			int procArticleId = procPaperSubResult.getProceedingArticleId();
			double procArticleScore = procPaperSubResult.getScore();
			resultString += "," + procArticleId + "," + procArticleScore;
			
			AuthorPaperSubResult relatedAuthorPaperSubResult = procPaperSubResult.getTopRelatedAuthorPaperSubResult();
			if(relatedAuthorPaperSubResult == null){continue;}
			
			int authorArticleId = relatedAuthorPaperSubResult.getAuthorArticleId();
			resultString += "," + authorArticleId;
			
			String keywordString = "{";
			Iterator<KeywordSubResult> keywordIterator = relatedAuthorPaperSubResult.getKeywordSubResultIterator();
			while(keywordIterator.hasNext())
			{
				KeywordSubResult keywordSubResult = keywordIterator.next();
				int keywordId = keywordSubResult.getKeywordId();
				double keywordScore = keywordSubResult.getScore();
				
				keywordString += keywordId + ":" + keywordScore + ";";
			}
			
			keywordString += "}";
			
			resultString += "," + keywordString;
			this.writeToResultFile(resultString);
		}
	}
	
	private void writeToResultFile(String resultString)
	{
		
		try 
		{
			PrintWriter writer;
			writer = new PrintWriter(new FileOutputStream(new File(this.RESULT_FILE_PATH), true));
			writer.println(resultString);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
