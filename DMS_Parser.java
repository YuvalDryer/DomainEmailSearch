import java.util.ArrayList;

/* 
	Interface for HTML Parser	
*/

public interface DMS_Parser {

	public  ArrayList<String>  GetLinks(String url, boolean reload);
	public  ArrayList<String>  GetEmails(String url, boolean reload);
	
	public boolean IsAbsolute(String url);
	public String  ConvertToAbs(String baseUrl, String relUrl);
}
