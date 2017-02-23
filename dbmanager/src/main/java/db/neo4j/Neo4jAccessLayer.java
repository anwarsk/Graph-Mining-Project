package db.neo4j;

import java.io.File;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import data.Author;
import data.Keyword;
import data.Paper;

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
}
