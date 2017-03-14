package db.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import result.AuthorPaperSubResult;
import result.KeywordSubResult;
import result.ProceeedingPaperSubResult;
import result.Result;

public class JSONAccessLayer {

	public Result readResultFromJSONFile(String jsonFile)
	{
		assert jsonFile.isEmpty() == false : "Empty file path";

		Result result = null;

		try 
		{
			String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));

			System.out.println(jsonContent.toString());

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(MinMaxPriorityQueue.class, new MinMaxPriorityQueueDeserializer());
			Gson gson = gsonBuilder.create();
			result = gson.fromJson(jsonContent.toString(), Result.class);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return result;
	}
}

class MinMaxPriorityQueueDeserializer<E> implements JsonDeserializer<MinMaxPriorityQueue> {
	public MinMaxPriorityQueue<ProceeedingPaperSubResult> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException 
	{
		MinMaxPriorityQueue<ProceeedingPaperSubResult> priorityQueue = MinMaxPriorityQueue.orderedBy(Collections.reverseOrder()).maximumSize(20).create();
		Iterator<JsonElement> proceedingSubResultArray = json.getAsJsonArray().iterator();
		while (proceedingSubResultArray.hasNext())
		{
			JsonElement proceedingResultJson = proceedingSubResultArray.next();
			ProceeedingPaperSubResult proceedingPaperSubResult = this.getProceedingPaperSubResult(proceedingResultJson);
			priorityQueue.add(proceedingPaperSubResult);
		}
		return priorityQueue;  
	}

	private ProceeedingPaperSubResult getProceedingPaperSubResult(JsonElement proceedingResultJson)
	{
		JsonObject object = proceedingResultJson.getAsJsonObject();
		int proceedingArticleId = Integer.parseInt(object.get("proceedingArticleId").toString());
		double score =  Double.parseDouble(object.get("score").toString());
		ProceeedingPaperSubResult proceedingPaperSubResult = new ProceeedingPaperSubResult(proceedingArticleId);
		proceedingPaperSubResult.setScore(score);
		Iterator<JsonElement> authorSubResultArray = object.get("authorPaperSubResults").getAsJsonArray().iterator();
		while(authorSubResultArray.hasNext())
		{
			JsonElement authorPaperSubResultElement = authorSubResultArray.next();
			AuthorPaperSubResult authorPaperSubResult = this.getAuthorPaperSubResult(authorPaperSubResultElement);
			proceedingPaperSubResult.addAuthorPaperSubResult(authorPaperSubResult);
		}

		return proceedingPaperSubResult;

	}

	private AuthorPaperSubResult getAuthorPaperSubResult(JsonElement authorSubResultJson)
	{
		JsonObject object = authorSubResultJson.getAsJsonObject();
		int authorArticleId = Integer.parseInt(object.get("authorArticleId").toString());
		double score =  Double.parseDouble(object.get("score").toString());
		AuthorPaperSubResult authorPaperSubResult = new AuthorPaperSubResult(authorArticleId, score);
		Iterator<JsonElement> keywordArray = object.get("keywords").getAsJsonArray().iterator();
		Gson gson = new Gson();

		while(keywordArray.hasNext())
		{
			JsonElement keywordJson = keywordArray.next();
			int keywordId = keywordJson.getAsJsonObject().get("keywordId").getAsInt();
			double score1 =  keywordJson.getAsJsonObject().get("score").getAsDouble();
			KeywordSubResult keywordSubResult = new KeywordSubResult(keywordId, score1);
			authorPaperSubResult.addKeyword(keywordSubResult);
		}

		return authorPaperSubResult;
	}
}
