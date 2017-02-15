package data;

import java.util.Date;

public class Paper {

	private long articleId;
	private String paperId;
	private Date publicationDate;
	private String title;
	private PaperType paperType;
	
	public Paper(long articleId, String paperId, String title)
	{
		this.articleId = articleId;
		this.title = title;
		this.paperId = paperId;
	}
	
	public Date getPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	public long getArticleId() {
		return articleId;
	}
	
	public String getPaperId() {
		return paperId;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public PaperType getPaperType() {
		return paperType;
	}
	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}
	
	
}
