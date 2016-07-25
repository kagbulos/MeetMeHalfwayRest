package hello;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.beust.jcommander.Parameter;

/**
 * Code sample for accessing the Yelp API V2.
 * 
 * This program demonstrates the capability of the Yelp API version 2.0 by using
 * the Search API to query for businesses by a search term and location, and the
 * Business API to query additional information about the top result from the
 * search query.
 * 
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp
 * Documentation</a> for more info.
 * 
 */
public class YelpAPI {

	private static final String API_HOST = "api.yelp.com";
	private static String DEFAULT_TERM;
	private static String DEFAULT_LOCATION;
	private static int SEARCH_LIMIT;
	private static final String SEARCH_PATH = "/v2/search";
	private static final String BUSINESS_PATH = "/v2/business";
	private static boolean fullPrint;

	/*
	 * Update OAuth credentials below from the Yelp Developers API site:
	 * http://www.yelp.com/developers/getting_started/api_access
	 */
	private static final String CONSUMER_KEY = "sjpWoFhjvWlKxhS7Ycmt9A";
	private static final String CONSUMER_SECRET = "0NROJAdqJHcot7bC4Kccis0z_E0";
	private static final String TOKEN = "a9Om6Gc3ua5A5i3KBqpd7oGfhaPX4Y73";
	private static final String TOKEN_SECRET = "EqrvOzCXzCBYhHXiMuwYSyL1xEE";

	OAuthService service;
	Token accessToken;

	/**
	 * Setup the Yelp API OAuth credentials.
	 * 
	 * @param consumerKey
	 *            Consumer key
	 * @param consumerSecret
	 *            Consumer secret
	 * @param token
	 *            Token
	 * @param tokenSecret
	 *            Token secret
	 */
	public YelpAPI() {
		this.service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET)
				.build();
		this.accessToken = new Token(TOKEN, TOKEN_SECRET);
	}

	/**
	 * Creates and sends a request to the Search API by term and location.
	 * <p>
	 * See
	 * <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp
	 * Search API V2</a> for more info.
	 * 
	 * @param term
	 *            <tt>String</tt> of the search term to be queried
	 * @param location
	 *            <tt>String</tt> of the location
	 * @return <tt>String</tt> JSON Response
	 */
	public String searchForBusinessesByLocation(String term, String location) {
		OAuthRequest request = createOAuthRequest(SEARCH_PATH);
		request.addQuerystringParameter("term", term);
		request.addQuerystringParameter("location", location);
		request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
		return sendRequestAndGetResponse(request);
	}

	/**
	 * Creates and sends a request to the Business API by business ID.
	 * <p>
	 * See
	 * <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp
	 * Business API V2</a> for more info.
	 * 
	 * @param businessID
	 *            <tt>String</tt> business ID of the requested business
	 * @return <tt>String</tt> JSON Response
	 */
	public String searchByBusinessId(String businessID) {
		OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
		return sendRequestAndGetResponse(request);
	}

	/**
	 * Creates and returns an {@link OAuthRequest} based on the API endpoint
	 * specified.
	 * 
	 * @param path
	 *            API endpoint to be queried
	 * @return <tt>OAuthRequest</tt>
	 */
	private OAuthRequest createOAuthRequest(String path) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
		return request;
	}

	/**
	 * Sends an {@link OAuthRequest} and returns the {@link Response} body.
	 * 
	 * @param request
	 *            {@link OAuthRequest} corresponding to the API request
	 * @return <tt>String</tt> body of API response
	 */
	private String sendRequestAndGetResponse(OAuthRequest request) {
		if(fullPrint){ System.out.println("Querying " + request.getCompleteUrl() + " ..."); }
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	/**
	 * Queries the Search API based on the command line arguments and takes the
	 * first result to query the Business API.
	 * 
	 * @param yelpApi
	 *            <tt>YelpAPI</tt> service instance
	 * @param yelpApiCli
	 *            <tt>YelpAPICLI</tt> command line arguments
	 * @return 
	 */
	private static List<String> queryAPI(YelpAPI yelpApi, YelpAPICLI yelpApiCli) {
		List<String> listOfResults = new Vector<String>();
		String searchResponseJSON = yelpApi.searchForBusinessesByLocation(yelpApiCli.term, yelpApiCli.location);

		JSONParser parser = new JSONParser();
		JSONObject response = null;
		try {
			response = (JSONObject) parser.parse(searchResponseJSON);
		} catch (ParseException pe) {
			System.out.println("Error: could not parse JSON response:");
			System.out.println(searchResponseJSON);
			System.exit(1);
		}

		JSONArray businesses = (JSONArray) response.get("businesses");
		
		if(fullPrint) {
		JSONObject firstBusiness = (JSONObject) businesses.get(0);
		String firstBusinessID = firstBusiness.get("id").toString();
		System.out.println(String.format("%s businesses found, querying business info for the top result \"%s\" ...",
				businesses.size(), firstBusinessID));

		// Select the first business and display business details
		String businessResponseJSON = yelpApi.searchByBusinessId(firstBusinessID.toString());
		System.out.println(String.format("Result for business \"%s\" found:", firstBusinessID));
		System.out.println(businessResponseJSON);
		}
		
		// print out a list of the top places
		for(Object business : businesses) {
//			System.out.println("***************");
//			System.out.println(((JSONObject) business).get("name").toString());
			listOfResults.add(((JSONObject) business).get("name").toString());
		}
		return listOfResults;
	}

	/**
	 * Command-line interface for the sample Yelp API runner.
	 */
	public static class YelpAPICLI {
		@Parameter(names = { "-q", "--term" }, description = "Search Query Term")
		public String term = DEFAULT_TERM;

		@Parameter(names = { "-l", "--location" }, description = "Location to be Queried")
		public String location = DEFAULT_LOCATION;
	}

	public List<String> run(String defaultTerm, String defaultLocation, int searchLimit, boolean fullprint) {
		setDefaultTerm(defaultTerm);
		setDefaultLocation(defaultLocation);
		setSearchLimit(searchLimit);
		setFullPrint(fullprint);
		YelpAPICLI yelpApiCli = new YelpAPICLI();

		List<String> listOfResults;
		YelpAPI yelpApi = new YelpAPI();
		listOfResults = queryAPI(yelpApi, yelpApiCli);
		return listOfResults;
	}

	private void setDefaultTerm(String defaultTerm) {
		DEFAULT_TERM = defaultTerm;
	}

	private void setDefaultLocation(String defaultLocation) {
		DEFAULT_LOCATION = defaultLocation;
	}
	
	private void setSearchLimit(int searchLimit) {
		SEARCH_LIMIT = searchLimit;
	}
	
	private void setFullPrint(boolean fp) {
		fullPrint = fp;
	}
}
