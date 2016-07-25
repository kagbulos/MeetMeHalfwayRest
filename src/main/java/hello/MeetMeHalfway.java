package hello;

import java.util.List;
import java.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jackson.map.ObjectMapper;

public class MeetMeHalfway {

    private final long id;
    private List<String> listOfResults;
    private int midpoint = 0;
	private int limit;
    private String type;
    private String URL = "http://maps.googleapis.com/maps/api/geocode/json";
	private String loc1 = null;
	private String loc2 = null;
	
	private GoogleResponse convertToLatLong(String fullAddress) throws IOException {
		URL url = new URL(URL + "?address=" + URLEncoder.encode(fullAddress, "UTF-8") + "&sensor=false");
		URLConnection conn = url.openConnection();

		InputStream in = conn.getInputStream();
		ObjectMapper mapper = new ObjectMapper();
		GoogleResponse response = (GoogleResponse) mapper.readValue(in, GoogleResponse.class);
		in.close();
		return response;
	}

	private GoogleResponse convertFromLatLong(String latlongString) throws IOException {
		URL url = new URL(URL + "?latlng=" + URLEncoder.encode(latlongString, "UTF-8") + "&sensor=false");
		URLConnection conn = url.openConnection();

		InputStream in = conn.getInputStream();
		ObjectMapper mapper = new ObjectMapper();
		GoogleResponse response = (GoogleResponse) mapper.readValue(in, GoogleResponse.class);
		in.close();
		return response;
	}

    public MeetMeHalfway(long id, String location1, String location2, String type, String limit) {
        this.id = id;
        this.type = type;
        this.limit = Integer.parseInt(limit);
        loc1 = location1;
        loc2 = location2;
        
    	GoogleResponse location1GR = null;
    	GoogleResponse location2GR = null;
        
		float midpointLat = 0;
		float midpointLng = 0;
		String strMidpointLat;
		String strMidpointLng;
		GoogleResponse midPointResp = null;
		boolean fullPrint = false;
		
		try {
			location1GR = new MeetMeHalfway().convertToLatLong(location1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			location2GR = new MeetMeHalfway().convertToLatLong(location2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (location1GR.getStatus().equals("OK") && location2GR.getStatus().equals("OK")) {
			midpointLat = Float.valueOf(location1GR.getResults()[0].getGeometry().getLocation().getLat())
					+ Float.valueOf(location2GR.getResults()[0].getGeometry().getLocation().getLat());
			midpointLat /= 2;

			midpointLng = Float.valueOf(location1GR.getResults()[0].getGeometry().getLocation().getLng())
					+ Float.valueOf(location2GR.getResults()[0].getGeometry().getLocation().getLng());
			midpointLng /= 2;
		} else {
			System.out.println(location1GR.getStatus() + " && " + location2GR.getStatus());
		}

		if (midpointLat != 0 && midpointLng != 0) {
			strMidpointLat = Float.toString(midpointLat);
			strMidpointLng = Float.toString(midpointLng);

			try {
				midPointResp = new MeetMeHalfway().convertFromLatLong(strMidpointLat + "," + strMidpointLng);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (midPointResp.getStatus().equals("OK") && fullPrint) {
				for (Result result : midPointResp.getResults()) {
					System.out.println("address is :" + result.getFormatted_address());
				}
			} else {
				//System.out.println(midPointResp.getStatus());
			}
			
			YelpAPI yelpAPI = new YelpAPI();
			listOfResults = yelpAPI.run(type, midPointResp.getResults()[1].getFormatted_address(), this.limit, fullPrint);
		}
    }

    public MeetMeHalfway() {
    	id = 1;
	}

	public long getId() {
        return id;
    }
    
    public List<String> getListOfResults() {
        return listOfResults;
    }
    
    public int getMidpoint() {
    	return midpoint;
    }
    
    public String getType() {
    	return type;
    }
    
    public int getLimit() {
    	return limit;
    }
    
    public String getLoc1() {
    	return loc1;
    }
    
    public String getLoc2() {
    	return loc2;
    }
}
