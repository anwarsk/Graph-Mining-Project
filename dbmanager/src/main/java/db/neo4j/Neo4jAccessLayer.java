package db.neo4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import data.Author;
import data.Journal;
import data.JournalPaperRelationStore;
import data.Keyword;
import data.KeywordPaperRelationStore;
import data.Paper;
import data.PaperReferenceRelationStore;
import data.Proceeding;
import data.ProceedingPaperRelationStore;

public class Neo4jAccessLayer {

	private final String DB_PATH = "./../../../Database/graph.acm2015";
	//private final String DB_PATH = "./../../../Database/graph.test.acm2015";
	//private final String DB_PATH = "/home/anwar/acm_2015"; //Test

	public void test()
	{
		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseBuilder dbBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(databaseDirectory);
		GraphDatabaseService dbService =  dbBuilder.newGraphDatabase();

		try (Transaction tx=dbService.beginTx()) 
		{
			System.out.println(dbService.findNodes(Label.label("author")).next().getProperty("first_name"));
			tx.success();
			tx.close();
		}

		dbService.shutdown();
	}

	public void addAuthorNodes(List<Author> authorList)
	{
		assert authorList.isEmpty() == false : "Empty Author List";

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseBuilder dbBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(databaseDirectory);
		GraphDatabaseService dbService =  dbBuilder.newGraphDatabase();

		try (Transaction tx=dbService.beginTx()) 
		{
			for(Author author: authorList)
			{
				Node node = dbService.createNode();
				node.addLabel(Label.label("author"));
				node.setProperty("author_id", author.getAuthorID());
				node.setProperty("first_name", author.getFirstName());
				node.setProperty("last_name", author.getLastName());
			}

			tx.success();
			tx.close();
		}

		dbService.shutdown();
	}

	public void addPaperNodes(List<Paper> paperList)
	{
		assert paperList.isEmpty() == false : "Empty Paper List";

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

		try (Transaction tx=dbService.beginTx()) 
		{
			for(Paper paper: paperList.subList(0, paperList.size()/2))
			{

				Node node = dbService.createNode();
				node.addLabel(Label.label("paper"));
				node.setProperty("article_id", paper.getArticleId());
				node.setProperty("paper_id", paper.getPaperId());
				node.setProperty("publication_date", paper.getPublicationDate().getTime());
				node.setProperty("title", paper.getTitle());
				//node.setProperty("type", paper.getPaperType().name().toLowerCase());

			}
			tx.success();
			tx.close();
		}

		try (Transaction tx=dbService.beginTx()) 
		{
			for(Paper paper: paperList.subList(paperList.size()/2, paperList.size()))
			{
				Node node = dbService.createNode();
				node.addLabel(Label.label("paper"));
				node.setProperty("article_id", paper.getArticleId());
				node.setProperty("paper_id", paper.getPaperId());
				node.setProperty("publication_date", paper.getPublicationDate().getTime());
				node.setProperty("title", paper.getTitle());
				//node.setProperty("type", paper.getPaperType().name().toLowerCase());
			}
			tx.success();
			tx.close();
		}

		dbService.shutdown();
	}

	public void addKeywordNodes(List<Keyword> keywordList) {
		// TODO Auto-generated method stub

		assert keywordList.isEmpty() == false : "Empty Paper List";

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

		try (Transaction tx=dbService.beginTx()) 
		{
			for(Keyword keyword: keywordList.subList(0, keywordList.size()/2))
			{

				Node node = dbService.createNode();
				node.addLabel(Label.label("keyword"));
				node.setProperty("keyword_id", keyword.getKeywordId());
				node.setProperty("keyword", keyword.getKeyword());
			}
			tx.success();
			tx.close();
		}

		try (Transaction tx=dbService.beginTx()) 
		{
			for(Keyword keyword: keywordList.subList(keywordList.size()/2, keywordList.size()))
			{
				Node node = dbService.createNode();
				node.addLabel(Label.label("keyword"));
				node.setProperty("keyword", keyword.getKeyword());
				node.setProperty("keyword_id", keyword.getKeywordId());
			}
			tx.success();
			tx.close();
		}

		dbService.shutdown();
	}

	public void createAuthorPaperConnection(List<Author> authorList) {
		// TODO Auto-generated method stub

		System.out.println("Function Started");

		assert authorList != null : "Invalid Author List";
		assert authorList.isEmpty() == false : "Empty Author List";

		System.out.println("Checkout: Assert");

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

		System.out.println("Checkout: GraphDatabaseBuilder");

		System.out.println("Checkout: GraphDatabaseService");

		try (Transaction tx=dbService.beginTx()) 
		{
			System.out.println("Checkout: Transaction Begin");
			for(Author author : authorList)
			{
				List<Paper> paperList = author.getPaperList();
				Node authorNode = dbService.findNode(Label.label("author"), "author_id", author.getAuthorID());

				//				System.out.println("Checkout: AuthorNode Found");
				if (authorNode != null)
				{
					double weight = (1.0/paperList.size());
					for(Paper paper: paperList)
					{
						//					System.out.println("Checkout: PaperLoop-1");
						Node paperNode = dbService.findNode(Label.label("paper"), "article_id", paper.getArticleId());
						if (paperNode == null){System.out.println("Can not find paper with aid" + paper.getArticleId());}
						else{
							//					System.out.println("Checkout: PaperFound");
							Relationship relationship = authorNode.createRelationshipTo(paperNode, RelationshipType.withName("written"));
							//					System.out.println("Checkout: Relationship Created");
							relationship.setProperty("f_weight", weight);
							relationship.setProperty("seq_no", paper.getSeq_no());
							//					System.out.println("Checkout: Relationship Properties Set");
						}
					}
				}
				else {System.out.println("Can not find author with aid" + author.getAuthorID());}
			}


			tx.success();
			System.out.println("Checkout: Transaction Success");
			tx.close();
			System.out.println("Checkout: Transaction Close");
		}

		dbService.shutdown();
		System.out.println("Checkout: Database Shutdown");
	}

	public void createPaperKeywordRelation(KeywordPaperRelationStore keywordPaperRelationStore) {
		// TODO Auto-generated method stub
		assert keywordPaperRelationStore != null : "Null Keyword Paper Relation Stores";

		System.out.println("SIZE>=" + keywordPaperRelationStore.size());
		List<Long> articleIds =  new ArrayList<Long>(keywordPaperRelationStore.getArticleIds());

		int startIndex = 0; 
		int batchSize = 20000;
		int endIndex = 0;

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		do
		{
			endIndex = startIndex + batchSize;
			if(endIndex > articleIds.size()){ endIndex = articleIds.size(); }

			try (Transaction tx=dbService.beginTx()) 
			{
				List<Long> articleIdSubList = articleIds.subList(startIndex, endIndex);
				for(Long articleId : articleIdSubList)
				{
					Node paperNode = dbService.findNode(Label.label("paper"), "article_id", articleId);
					if(paperNode != null)
					{
						List<Long> keywordIds = keywordPaperRelationStore.getListOfRelatedKeywords(articleId);
						double weight = 1.0/keywordIds.size();
						for(Long keywordId : keywordIds)
						{
							Node keywordNode = dbService.findNode(Label.label("keyword"), "keyword_id", keywordId);
							if(keywordNode != null)
							{
								Relationship relation = paperNode.createRelationshipTo(keywordNode, RelationshipType.withName("contains"));
								relation.setProperty("f_weight", weight);
							}
						}
					}
					System.out.println("Processed Article Id:" + articleId);
				}
				tx.success();
				System.out.println("Checkout: Transaction Success");
				tx.close();
			}
			startIndex = endIndex;
		}
		while(startIndex < articleIds.size());
		dbService.shutdown();
		System.out.println("Checkout: Database Shutdown");
	}

	public void createPaperReferenceRelation(PaperReferenceRelationStore paperAndReferenceRelationStore) {
		
		assert paperAndReferenceRelationStore != null : "Null Keyword Paper Relation Stores";

		System.out.println("SIZE>=" + paperAndReferenceRelationStore.size());
		List<Integer> articleIds =  new ArrayList<Integer>(paperAndReferenceRelationStore.getArticleIds());

		int startIndex = 0; 
		int batchSize = 10000;
		int endIndex = 0;

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		do
		{
			endIndex = startIndex + batchSize;
			if(endIndex > articleIds.size()){ endIndex = articleIds.size(); }

			try (Transaction tx=dbService.beginTx()) 
			{
				List<Integer> articleIdSubList = articleIds.subList(startIndex, endIndex);
				for(Integer articleId : articleIdSubList)
				{
					Node paperNode = dbService.findNode(Label.label("paper"), "article_id", articleId);
					if(paperNode != null)
					{
						List<Integer> refArticleIds = paperAndReferenceRelationStore.getListOfReferenceIds(articleId);
						double weight = 1.0/refArticleIds.size();
						for(Integer refArticleId : refArticleIds)
						{
							Node referencePaperNode = dbService.findNode(Label.label("paper"), "article_id", refArticleId);
							if(referencePaperNode != null)
							{
								Relationship relation = paperNode.createRelationshipTo(referencePaperNode, RelationshipType.withName("cite"));
								relation.setProperty("f_weight", weight);
							}
						}
					}
					System.out.println("Processed Article Id:" + articleId);
				}
				tx.success();
				System.out.println("Checkout: Transaction Success");
				tx.close();
			}
			startIndex = endIndex;
		}
		while(startIndex < articleIds.size());
		dbService.shutdown();
		System.out.println("Checkout: Database Shutdown");
		
	}

	public void createProceedingNodes(List<Proceeding> proceedingList) {
		assert proceedingList.isEmpty() == false : "Empty Proceeding List";

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

		
		try (Transaction tx=dbService.beginTx()) 
		{
			for(Proceeding proceeding: proceedingList)
			{
				Node node = dbService.createNode();
				node.addLabel(Label.label("proceeding"));
				node.setProperty("proc_id", proceeding.getId());
				node.setProperty("series_id", proceeding.getSeries_id());
				node.setProperty("series_title", proceeding.getSeries_title());
				node.setProperty("year", proceeding.getYear());
			}
			tx.success();
			tx.close();
		}

		dbService.shutdown();		
	}

	public void createProceedingAndPaperRelation(ProceedingPaperRelationStore proceedingAndPaperRelationStore) {
		assert proceedingAndPaperRelationStore != null : "Null Keyword Paper Relation Stores";

		System.out.println("SIZE>=" + proceedingAndPaperRelationStore.size());
		List<Integer> proceedingIds =  new ArrayList<Integer>(proceedingAndPaperRelationStore.getProceedingIds());

		int startIndex = 0; 
		int batchSize = 20000;
		int endIndex = 0;

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		do
		{
			endIndex = startIndex + batchSize;
			if(endIndex > proceedingIds.size()){ endIndex = proceedingIds.size(); }

			try (Transaction tx=dbService.beginTx()) 
			{
				List<Integer> proceedingIdSubList = proceedingIds.subList(startIndex, endIndex);
				for(Integer proceedingId : proceedingIdSubList)
				{
					Node proceedingNode = dbService.findNode(Label.label("proceeding"), "proc_id", proceedingId);
					if(proceedingNode != null)
					{
						List<Integer> articleIds = proceedingAndPaperRelationStore.getListOfArticleIds(proceedingId);
						double weight = 1.0/articleIds.size();
						for(Integer articleId : articleIds)
						{
							Node paperNode = dbService.findNode(Label.label("paper"), "article_id", articleId);
							if(paperNode != null)
							{
								Relationship relation = paperNode.createRelationshipTo(proceedingNode, RelationshipType.withName("published_at"));
								relation.setProperty("b_weight", weight);
							}
						}
					}
					System.out.println("Processed Proceeding Id:" + proceedingId);
				}
				tx.success();
				System.out.println("Checkout: Transaction Success");
				tx.close();
			}
			startIndex = endIndex;
		}
		while(startIndex < proceedingIds.size());
		dbService.shutdown();
		System.out.println("Checkout: Database Shutdown");
		
	}

	public void addJournalNodes(List<Journal> journalList) {
		
		assert journalList != null : "Null Journal List";
		assert journalList.isEmpty() == false : "Empty Proceeding List";

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

		
		try (Transaction tx=dbService.beginTx()) 
		{
			for(Journal journal: journalList)
			{
				Node node = dbService.createNode();
				node.addLabel(Label.label("journal"));
				node.setProperty("journal_uid", journal.getJournal_uid());
				node.setProperty("journal_id", journal.getJournal_id());
				node.setProperty("journal_name", journal.getJournal_name());
				node.setProperty("year", journal.getYear());
			}
			tx.success();
			tx.close();
		}

		dbService.shutdown();		

	}

	public void createJournalAndPaperRelation(JournalPaperRelationStore journalAndPaperRelationStore) {
		
		assert journalAndPaperRelationStore != null : "Null Journal and Paper Relation Store";
		assert journalAndPaperRelationStore.size() > 0 : "Empty Journal and Paper Relation Store";	

		System.out.println("SIZE>=" + journalAndPaperRelationStore.size());
		List<Integer> JournalUIds =  new ArrayList<Integer>(journalAndPaperRelationStore.getJournalUIds());

		int startIndex = 0; 
		int batchSize = 10000;
		int endIndex = 0;

		File databaseDirectory = new File(DB_PATH);
		GraphDatabaseService dbService = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		do
		{
			endIndex = startIndex + batchSize;
			if(endIndex > JournalUIds.size()){ endIndex = JournalUIds.size(); }

			try (Transaction tx=dbService.beginTx()) 
			{
				List<Integer> journalUIdSubList = JournalUIds.subList(startIndex, endIndex);
				for(Integer journalUId : journalUIdSubList)
				{
					Node journalNode = dbService.findNode(Label.label("journal"), "journal_uid", journalUId);
					if(journalNode != null)
					{
						List<Integer> articleIds = journalAndPaperRelationStore.getListOfArticleIds(journalUId);
						double weight = 1.0/articleIds.size();
						for(Integer articleId : articleIds)
						{
							Node paperNode = dbService.findNode(Label.label("paper"), "article_id", articleId);
							if(paperNode != null)
							{
								Relationship relation = paperNode.createRelationshipTo(journalNode, RelationshipType.withName("published_in"));
								relation.setProperty("b_weight", weight);
							}
						}
					}
					System.out.println("Processed Proceeding Id:" + journalUId);
				}
				tx.success();
				System.out.println("Checkout: Transaction Success");
				tx.close();
			}
			startIndex = endIndex;
		}
		while(startIndex < JournalUIds.size());
		dbService.shutdown();
		System.out.println("Checkout: Database Shutdown");
		
	}
}
