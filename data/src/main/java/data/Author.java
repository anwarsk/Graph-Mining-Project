package data;

import java.util.List;

public class Author {
	
	private String firstName;
	private String lastName;
	private String authorID;
	
	private List<Paper> paperList;
	
	public List<Paper> getPaperList() {
		return paperList;
	}

	public void setPaperList(List<Paper> paperList) {
		this.paperList = paperList;
	}

	public Author(String authorID, String firstName, String lastName)
	{
		this.authorID = authorID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAuthorID() {
		return authorID;
	}
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}

	@Override
	public String toString() {

		String print = "";
		print = print + this.getFirstName() + " " + this.getLastName();
		return print;
	}
}
