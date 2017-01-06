import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DMS_JSoupParser implements DMS_Parser {

	private final static String LINK_ATTRIBUTE = "a[href]"; 

	private final static String EMAIL_PATTERN = "href=\"mailto:(.*?)\""; 
	private final static String EMAIL_PREFIX  = "href=\"mailto:";
	private final static int    EMAIL_PREFIX_LEX = 13;

	
	private static DMS_JSoupParser instance = null;
	private Document page;
	
	
	private DMS_JSoupParser() {
		page = null;
	}
	
	
	public static DMS_JSoupParser Create() { 
		if(null == instance) {
			instance = new DMS_JSoupParser();			
		}
		
		return instance;			
	}

	@Override
	public ArrayList<String> GetLinks(String url, boolean reload) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		if(reload) {
			try {
				page = readHTML(url);
			} catch (IOException e) {
				
				// Something went wrong while reading the page return null
				return null;
			}
		}

		//Get links from document object. 
		Elements links = page.select(LINK_ATTRIBUTE);   
		
		//Iterate links and print link attributes. 
		for (Element link : links) { 
			if(!isInternalTag(link)) {
				String tmp = extractLinkFromURL(link);
				if(null != tmp)	
					list.add(tmp.replace(" ", "%20"));
			}
		}   

		return list;
	}

	
	@Override
	public ArrayList<String> GetEmails(String url, boolean reload) {

		ArrayList<String> list = new ArrayList<String>();
		
		if(reload) {
			try {
				page = readHTML(url);
			} catch (IOException e) {
				
				// Something went wrong while reading the page return null
				return null;
			}
		}

		
		Pattern linkPattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		Matcher pageMatcher = linkPattern.matcher(page.toString());
		
		while(pageMatcher.find()){
			String line = pageMatcher.group();
		    list.add(extractEmailFromURL(line));
		}
		
		return list;		
	}

	
	@Override
	public boolean IsAbsolute(String url) {
		// all links are retrieved as absolute so return true to all
		return true;		
	}

	
	@Override
	public String ConvertToAbs(String baseUrl, String relUrl) {
		// all links are retrieved as absolute so return as is
		return relUrl;
	}
	
	
	private Document readHTML(String url) throws IOException {
		return Jsoup.connect(url).get();		
    }

	
	private  String extractLinkFromURL(Element url) {
		// extract all links as absolute instead of relative
		return url.attr("abs:href");
		
	}
	
	
	private String extractEmailFromURL(String url) {
		return url.substring(EMAIL_PREFIX_LEX, url.length()-1);
	}

	
	private boolean isInternalTag(Element url) {
		String link = url.attr("href");
		int len = link.length();
		
		if(len > 0) {
			if(link.substring(0, 1).contentEquals("#")) 
				return true;
		}
		
		return false;
	}
}
