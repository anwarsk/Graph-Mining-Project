package data;

public class Journal {
	
	private int journal_uid;
	private String journal_id;
	private String journal_name;
	private int year;
	
	public Journal(int journal_uid, String journal_id, String journal_name, int year)
	{
		this.journal_uid = journal_uid;
		this.journal_id = journal_id;
		this.journal_name = journal_name;
		this.year = year;
	}
	
	public String getJournal_name() {
		return journal_name;
	}
	public void setJournal_name(String journal_name) {
		this.journal_name = journal_name;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getJournal_uid() {
		return journal_uid;
	}
	public String getJournal_id() {
		return journal_id;
	}

}
