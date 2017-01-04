import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DMS_Main {
		
	private final static String DOMAIN_PREFIX = "www.";
	private final static String PROTOCOL_HTTP = "http://";
	private final static String PROTOCOL_HTTPS = "https://";

	private String domainName;
	
	private HashSet<String> emailSet;
	private HashSet<String> scannedPagesSet;
	private HashSet<String> errorPagesSet;
	private String  currAddress;
	
	private boolean outputHeader;
	private boolean outputContinuous;
	private boolean outputAll;
	
	
	// interface to support multiple parser implementations
	private DMS_Parser parser;
	
	
	public DMS_Main() {
		emailSet = new HashSet<String>();
		scannedPagesSet = new HashSet<String>();
		errorPagesSet = new HashSet<String>();
		
		outputHeader = false;
		outputContinuous = false;
		outputAll = false;
		
		parser = (DMS_JSoupParser) DMS_JSoupParser.Create();
	}
	
	
	public void Start(String aWebsite) {
		
		// Current unread url addresses 
		LinkedList<String> stack = new LinkedList<String>();
		
		// Previously read url addresses
		ArrayList<String>  prev = new ArrayList<String>();
				
		stack.addFirst(aWebsite);
		
		ArrayList<String> emails;
		ArrayList<String> links;
			
		while(!stack.isEmpty()) {
			currAddress = stack.removeFirst();
			
			// If address prev visited skip to the next one
			if(prev.contains(currAddress))
				continue;
			
			// Mark the address as visited so not to loop into it again
			prev.add(currAddress);
				
			// Parse the page for emails			
			emails = parser.GetEmails(currAddress, true);
			
			if(null != emails) {
				for(String email : emails) {
					if(!emailSet.contains(email)) {
						emailSet.add(email);
						
						if(outputContinuous) {
							if(!outputHeader) {
								System.out.println("Found these email addresses:");
								outputHeader = true;
							}
							System.out.println(email);
						}
					}
				}
				
				// Store the scanned URL
				scannedPagesSet.add(currAddress);
			}
			else {				
				// Something went wrong, store the address and move to the next url
				errorPagesSet.add(currAddress);
				continue;
			}

			
			// parse for page for additional links
			links = parser.GetLinks(currAddress, false);
			if(null != links) {
				
				ListIterator<String> itr = links.listIterator();
				while(itr.hasNext()) {
				
					String nextUrl = itr.next();
					if(nextUrl.isEmpty()) {
						itr.remove();
						continue;
					}
									
					
					if(!parser.IsAbsolute(nextUrl)) {						
						nextUrl = parser.ConvertToAbs(currAddress, nextUrl);						
						
						// If next url was not visited before and does not lead outside the domain replace the 
						// absolute link, otherwise remove it
						if(prev.contains(nextUrl) || isExternalLink(nextUrl))
						{							
							itr.remove();						
						}
						else
							itr.set(nextUrl);
					}
					else {
						if(prev.contains(nextUrl) || isExternalLink(nextUrl)) {
							itr.remove();
						}
					}
				}
				
				// add all new valid links to the stack
				stack.addAll(links);
			}		
		}
		
		
		// Output section
		if(emailSet.isEmpty())
			System.out.println("No emails found");
		else {
			if(!outputContinuous) {
				System.out.println("Found these email addresses:");
				for(String email : emailSet)
					System.out.println(email);
			}
		}
				
		if(outputAll) {
			if(!errorPagesSet.isEmpty()) {
				System.out.format("%nCould not access %d pages:%n", errorPagesSet.size());
				for(String errUrl : errorPagesSet)
					System.out.println(errUrl);
			}
			
			if(!scannedPagesSet.isEmpty()) {
				System.out.format("%nScanned %d pages:%n", scannedPagesSet.size());
				for(String scannedUrl : scannedPagesSet)
					System.out.println(scannedUrl);				
			}
		}
 	}
	

	private boolean isExternalLink(String url) {
	
		String regex  = "^(http|https)://[a-zA-Z0-9]+\\." + "(" + domainName + "){1}";

		Pattern httpPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		Matcher urlMatcher = httpPattern.matcher(url);
		
		if(urlMatcher.find()) {	
			return false;
		}
		
		return true;
	}
	
	
	public static void main(String[] args) {
		DMS_Main dms = new DMS_Main();
		
		int argCount = args.length;
		
		switch(argCount) {
			case 0: {
				System.out.println("Error: Domain name missing");
				return;
			}
			
			case 1:
				dms.domainName = args[0];
			break;				
			
			case 2:
				dms.domainName = args[0];
				if(args[1].contentEquals("-cont"))
					dms.outputContinuous = true;
				else
					dms.outputContinuous = false;
				
				if(args[1].contentEquals("-all"))
					dms.outputAll = true;
				else
					dms.outputAll = false;				
			break;

			case 3:
				dms.domainName = args[0];
				if((args[1].contentEquals("-cont")) ||
				   (args[2].contentEquals("-cont")))
					dms.outputContinuous = true;
				else
					dms.outputContinuous = false;
				
				if((args[1].contentEquals("-all")) ||
				   (args[2].contentEquals("-all")))
					dms.outputAll = true;
				else
					dms.outputAll = false;				
			break;
			
			default: {		
				System.out.println("Error: Too many options");
				return;
			}
		}
		
		dms.Start(PROTOCOL_HTTP + DOMAIN_PREFIX + dms.domainName);		
	}
}
