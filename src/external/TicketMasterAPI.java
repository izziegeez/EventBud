package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json"; // endpoint
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "lPSpxyuOhhd45tMfvE89rwKVXNFOoy88";

	public List<Item> search(double lat, double lon, String keyword) {// latitude and longitude, keyword
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}

		try {
			// xxxx?keyword=mountain%20view, handle special characters
			keyword = URLEncoder.encode(keyword, "UTF-8");// e.g. keyword=mountain view, UTF-8 converts space to %20

		} catch (Exception e) {
			e.printStackTrace();
		}
		// %s is place holder for "API_KEY, lat, lon, keyword, 50", concatenation of 2
		// strings,we're only using latitude, longitude, keyword, and radius(defaulted
		// to be 50 miles) as identifiers
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		// "https://app.ticketmaster.com/discovery/v2/events.json?apikey=lPSpxyuOhhd45tMfvE89rwKVXNFOoy88&latlong=37,-120&keyword=event&radius=50"
		String url = URL + "?" + query;

		try {
			// cast it to HttpURLConnection because openConnection return type URLConnection
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(); // pass in the url
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();// send a request to TicketMaster,success=200,invalid
															// Response code=401
			System.out.println("url = " + url);
			System.out.println("response code: " + responseCode);
			if (responseCode != 200) { // not successful
				return new ArrayList<>();
			}
			// Create a BufferedReader to help read text from a character-input stream.
			// Provide for the efficient reading of characters, arrays, and lines.
			// BufferedReader takes in inputStreamReader, inputStreamReader takes in
			// inputStream
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));// read the
																											// TicketMaster
																											// data and
																											// stores it
																											// in memory
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			// Append response data to response StringBuilder instance line by line.
			while ((line = reader.readLine()) != null) { // reads line by line
				stringBuilder.append(line);
			}
			reader.close();// Close the BufferedReader after reading the inputStream/response data.
			// Extract events array only.
			// Create a JSON object out of the response string.
			JSONObject object = new JSONObject(stringBuilder.toString());

			if (!object.isNull("_embedded")) {// check if object contains "_embedded"
				JSONObject embedded = object.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));// check if "_embedded" contains "events" and return it
			}

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	// add purify method in TicketMasterAPI.java to convert JSONArray to a list of items.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			
			builder.setAddress(getAddress(event))
			.setCategories(getCategories(event))
			.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		return itemList;

	}

	/**
	 * implement some helper methods to fetch data fields which are
	 * included deeply in TicketMaster response body.
	 * Step 2.2.1, fetch address from event JSONObject.
	 * 
	 */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder builder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							builder.append(address.getString("line1"));
						}

						if (!address.isNull("line2")) {
							builder.append(",");
							builder.append(address.getString("line2"));
						}

						if (!address.isNull("line3")) {
							builder.append(",");
							builder.append(address.getString("line3"));
						}
					}

					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						builder.append(",");
						builder.append(city.getString("name"));
					}

					String result = builder.toString();
					if (!result.isEmpty()) {
						return result;
					}
				}
			}
		}
		return "";

	}
	// fetch imageUrl from event JSONObject
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); ++i) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";

	}
// fetch Categories from event JSONObject.
private Set<String> getCategories(JSONObject event) throws JSONException {
	Set<String> categories = new HashSet<>();
	if (!event.isNull("classifications")) {
		JSONArray classifications = event.getJSONArray("classifications");
		for (int i = 0; i < classifications.length(); ++i) {
			JSONObject classification = classifications.getJSONObject(i);
			if (!classification.isNull("segment")) {
				JSONObject segment = classification.getJSONObject("segment");
				if (!segment.isNull("name")) {
					categories.add(segment.getString("name"));
				}
			}
		}
	}
	return categories;

		
		
	}



	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);

		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}

	}

	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);

	}
}
