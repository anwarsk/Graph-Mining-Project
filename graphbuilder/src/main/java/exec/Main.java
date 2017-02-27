package exec;

import java.util.List;

import data.Author;
import data.Journal;
import data.JournalPaperRelationStore;
import data.Keyword;
import data.KeywordPaperRelationStore;
import data.Paper;
import data.PaperReferenceRelationStore;
import data.Proceeding;
import data.ProceedingPaperRelationStore;
import db.csv.CSVAccessLayer;
import db.neo4j.Neo4jAccessLayer;
import db.sql.SQLAccessLayer;

public class Main {

	public static void main(String[] args) {

		//		createAuthorNodes();
		//		System.out.println("Author Nodes Created.");
		//		
		//		createPaperNodes();
		//		System.out.println("Paper Nodes Created.");
		//	
//				createJournalNodes();

		//		createKeywordNodes();
		
		//createConferenceNodes();

		//connectAuthorPaper();

		//connectKeywordAndPaper();
		
//		connectPaperAndReferencePaper();
		
//		connectPaperAndJournal();
		
		//connectProceedingAndPaper();
//		MultiValueMap<Integer, Integer> a = new MultiValueMap<>();
//		a.put(1,2); a.put(10, 20); a.put(1, 2);
//		System.out.println(a);
	}


	private static void connectPaperAndJournal() {
		
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		JournalPaperRelationStore journalAndPaperRelationStore = sqlAccessLayer.getJournalAndPaperRelationStore();
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.createJournalAndPaperRelation(journalAndPaperRelationStore);
	}


	private static void connectProceedingAndPaper() {
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		ProceedingPaperRelationStore proceedingAndPaperRelationStore = sqlAccessLayer.getProceedingAndPaperRelationStore();
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.createProceedingAndPaperRelation(proceedingAndPaperRelationStore);
	}


	private static void createConferenceNodes() {
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Proceeding> proceedingList = sqlAccessLayer.getListOfProceeding();
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.createProceedingNodes(proceedingList);
	}


	private static void connectPaperAndReferencePaper() {
		
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		PaperReferenceRelationStore paperAndReferenceRelationStore = sqlAccessLayer.getPaperAndReferenceRelationStore();
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.createPaperReferenceRelation(paperAndReferenceRelationStore);
	}


	private static void connectKeywordAndPaper() {

		//1. get PaperId keyword Id pairs
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		KeywordPaperRelationStore keywordPaperRelationStore = sqlAccessLayer.getPaperIdKeywordIdRelations();
		
		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.createPaperKeywordRelation(keywordPaperRelationStore);
		
	}


	private static void connectAuthorPaper() {
		// TODO Auto-generated method stub
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Author> authorList = sqlAccessLayer.getUniqueAuthorListFromLocal();
		//System.out.println(authorList);
		int startIndex = 0; 
		int batchSize = 10000;
		int endIndex = 0;
		do
		{

			endIndex = startIndex + batchSize;
			if(endIndex > authorList.size()){ endIndex = authorList.size(); }

			try 
			{
				Thread.sleep(3000);
				List<Author> authorSubList = authorList.subList(startIndex, endIndex);
				for(Author author : authorSubList)
				{
					List<Paper> paperList = sqlAccessLayer.getListOfPapersForAuthor(author);
					System.out.println("#Papers Found: " + paperList.size());
					author.setPaperList(paperList);
				}
				Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
				neo4jAccessLayer.createAuthorPaperConnection(authorSubList);
				System.out.println("Neo4j Add Done");

				startIndex = endIndex;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(startIndex < authorList.size());

	}


	private static void createKeywordNodes() {
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Keyword> keywordList = sqlAccessLayer.getUniqueKeywordList();
		System.out.println("SQL Data Fetched.");

		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addKeywordNodes(keywordList);
		System.out.println("Neo4j added to graph.");

		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writeKeywordsToCSV(keywordList);
	}


	public static void createAuthorNodes()
	{
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Author> authorList = sqlAccessLayer.getUniqueAuthorList();

		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addAuthorNodes(authorList);

		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writeAuthorsToCSV(authorList);
	}


	private static void createPaperNodes() {

		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Paper> paperList = sqlAccessLayer.getListOfPapers();
		System.out.println("Paper Data Fetched.");

		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addPaperNodes(paperList);
		System.out.println("Papers added to graph.");

		CSVAccessLayer csvAccessLayer = new CSVAccessLayer();
		csvAccessLayer.writePapersToCSV(paperList);
	}

	private static void createJournalNodes() {
		SQLAccessLayer sqlAccessLayer = new SQLAccessLayer();
		List<Journal> journalList = sqlAccessLayer.getListOfJournals();
		System.out.println("Journal Data Fetched.");

		Neo4jAccessLayer neo4jAccessLayer = new Neo4jAccessLayer();
		neo4jAccessLayer.addJournalNodes(journalList);

	}



}
