package exec;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import result.AuthorPaperSubResult;
import result.KeywordSubResult;
import result.ProceeedingPaperSubResult;
import result.Result;


class PQDeserializer<E> implements JsonDeserializer<MinMaxPriorityQueue> {
	  public MinMaxPriorityQueue<ProceeedingPaperSubResult> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	      throws JsonParseException {
		  MinMaxPriorityQueue<ProceeedingPaperSubResult> priorityQueue = MinMaxPriorityQueue.orderedBy(Collections.reverseOrder()).maximumSize(20).create();
		  Iterator<JsonElement> j = json.getAsJsonArray().iterator();
		  while (j.hasNext())
		  {
			  JsonElement jsonElement = j.next();
			  ProceeedingPaperSubResult proceedingPaperSubResult = this.getProceedingPaperSubResult(jsonElement);
			  priorityQueue.add(proceedingPaperSubResult);
		  }
//		  System.out.println(j.get(0).getAsJsonObject().get("proceedingArticleId"));
//		  System.out.println(j.get(0).toString() + "\n");
//		  System.out.println(json.toString() + "\n");
	    return priorityQueue;  
	  }
	  
	  private ProceeedingPaperSubResult getProceedingPaperSubResult(JsonElement jsonElement)
	  {
		  JsonObject object = jsonElement.getAsJsonObject();
		  int proceedingArticleId = Integer.parseInt(object.get("proceedingArticleId").toString());
		  double score =  Double.parseDouble(object.get("score").toString());
		  ProceeedingPaperSubResult proceedingPaperSubResult = new ProceeedingPaperSubResult(proceedingArticleId);
		  proceedingPaperSubResult.setScore(score);
		  Iterator<JsonElement> element = object.get("authorPaperSubResults").getAsJsonArray().iterator();
		  while(element.hasNext())
		  {
			  JsonElement authorPaperSubResultElement = element.next();
			  AuthorPaperSubResult authorPaperSubResult = this.getAuthorPaperSubResult(authorPaperSubResultElement);
			  proceedingPaperSubResult.addAuthorPaperSubResult(authorPaperSubResult);
		  }
		  
		  return proceedingPaperSubResult;
		  
	  }
	  
	  private AuthorPaperSubResult getAuthorPaperSubResult(JsonElement jsonElement)
	  {
		  JsonObject object = jsonElement.getAsJsonObject();
		  int authorArticleId = Integer.parseInt(object.get("authorArticleId").toString());
		  double score =  Double.parseDouble(object.get("score").toString());
		  AuthorPaperSubResult authorPaperSubResult = new AuthorPaperSubResult(authorArticleId, score);
		  Iterator<JsonElement> element = object.get("keywords").getAsJsonArray().iterator();
		  Gson gson = new Gson();

		  while(element.hasNext())
		  {
			  JsonElement j = element.next();
			  int keywordId = j.getAsJsonObject().get("keywordId").getAsInt();
			  double score1 =  j.getAsJsonObject().get("score").getAsDouble();
			  KeywordSubResult ksbrs = new KeywordSubResult(keywordId, score1);
			  authorPaperSubResult.addKeyword(ksbrs);
		  }
		  
		  return authorPaperSubResult;
	  }
	}


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			String jsonFilePath = "D:\\Work\\Code\\json_result\\a_320547_1736020.json";
			String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
			
			System.out.println(jsonContent.toString());

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(MinMaxPriorityQueue.class, new PQDeserializer());
			Gson gson = gsonBuilder.create();
			Result r = gson.fromJson(jsonContent.toString(), Result.class);
			System.out.println("Completed.");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
