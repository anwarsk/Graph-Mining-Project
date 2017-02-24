package data;

public class Proceeding {

	private int id;
	private String series_id;
	private String series_title;
	private int year;
	
	public Proceeding(int id, String series_id, String series_title, int year)
	{
		this.id = id;
		this.series_id = series_id;
		this.series_title = series_title;
		this.year = year;
	}
	
	public String getSeries_id() {
		return series_id;
	}
	public void setSeries_id(String series_id) {
		this.series_id = series_id;
	}
	public String getSeries_title() {
		return series_title;
	}
	public void setSeries_title(String series_title) {
		this.series_title = series_title;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public Object getId() {
		return this.id;
	}
	
	
}
