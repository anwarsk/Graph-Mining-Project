package evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.google.common.collect.MinMaxPriorityQueue;

import db.sql.SQLAccessLayer;
import result.ProceeedingPaperSubResult;
import result.Result;

public class Evaluator {

	private static Map<Integer, Integer> globalTruePositive;
	private static String outputFilePath;
	private static int totalProcessedResults;
	private static int totalCitedPaperCount;
	private static List<Integer> kValues;
	private static boolean isIntialized = false;

	private Map<Integer, Integer> localTruePositive;
	private String authorId;
	private int proceedingId;


	private int citedPaperCount;

	public static void initialize(String outputFilePath1)
	{
		if(!isIntialized)
		{
			globalTruePositive = new HashedMap<Integer, Integer>();
			outputFilePath = outputFilePath1;
			totalProcessedResults = 0;
			totalCitedPaperCount = 0;
			
			kValues =  new ArrayList<Integer>();

			kValues.add(3);
			kValues.add(5);
			kValues.add(10);
			kValues.add(15);
			kValues.add(20);

			writeHeader();
			isIntialized = true;
		}
	}

	private static void writeHeader() {
		try 
		{
			String truePositveHeader = "";
			for(int kValue : kValues)
			{
				truePositveHeader = truePositveHeader + ",TP@" + kValue; 
			}
			String headerString = "author_id,proc_id,cited_paper_count" + truePositveHeader;
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputFilePath)));

			writer.println(headerString);
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Evaluator()
	{
		citedPaperCount = 0;
		localTruePositive = new HashedMap<Integer, Integer>();
	}
	
	public void evaluate(Result result)
	{
		this.authorId = result.getAuthorId();
		this.proceedingId = result.getProceedingId();

		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Integer> citedPapers = sqlAccessLayer.getCitedPapers(authorId, proceedingId);
		citedPaperCount = citedPapers.size();
		totalCitedPaperCount = totalCitedPaperCount + citedPaperCount;

		MinMaxPriorityQueue<ProceeedingPaperSubResult> recommendedPapers = result.getProceedingPaperSubResults();

		List<Integer> recommendedPaperList = new ArrayList<Integer>();
		// Get First threee
		int pollCount = 0;
		for(int kValue : kValues)
		{
			while(pollCount < kValue && recommendedPapers.size() > 0)
			{
				ProceeedingPaperSubResult procPaperSubResult = recommendedPapers.pollFirst();
				int recommendedArticleId = procPaperSubResult.getProceedingArticleId();
				recommendedPaperList.add(recommendedArticleId);
				pollCount++;
			}

			citedPapers.removeAll(recommendedPaperList);
			int truePositiveCountAtk = citedPaperCount - citedPapers.size();
			localTruePositive.put(kValue, truePositiveCountAtk);

			int globalTruePositiveCountAtk = globalTruePositive.getOrDefault(kValue, 0);
			globalTruePositive.put(kValue, globalTruePositiveCountAtk+truePositiveCountAtk);
		}
		
		totalProcessedResults++;
	}
	
	public void writeLocalResults()
	{
		try 
		{
			String dataString = this.authorId + "," + this.proceedingId + "," + this.citedPaperCount;
			
			String truePositiveDataString = "";
			for(int kValue : kValues)
			{
				truePositiveDataString = truePositiveDataString + "," + localTruePositive.get(kValue);
			}

			dataString = dataString + truePositiveDataString;
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputFilePath), true));

			writer.println(dataString);
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeGlobalResults()
	{
		try 
		{
			String dataString = "Total" + "," + totalProcessedResults + "," + totalCitedPaperCount;
			
			String truePositiveDataString = "";
			for(int kValue : kValues)
			{
				truePositiveDataString = truePositiveDataString + "," + globalTruePositive.get(kValue);
			}

			dataString = dataString + truePositiveDataString;
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputFilePath), true));

			writer.println(dataString);
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
