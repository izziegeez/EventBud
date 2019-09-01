package entity;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	// create an Item class to hold data fields for our project
	private String itemId; // represents a unique event
	private String name;
	private double rating;
	private String address;
	private Set<String> categories; // sports, music
	private String imageUrl;
	private String url;
	private double distance;

	// to create an instance of Item, we need to have constructors.But we can't
	// guarantee that TicketMaster can return all data fields to us every time

	// instead of:
	// Item(String itemId);
	//	Item(String name);
	//	Item(String itemId, String name);
	//...
	//	Item item = new Item(itemId, name);
	
	//we can do:
	//Item item = new ItemBuilder().setItemId().setName().set....build();

	
	/**
	 * This is a builder pattern in Java.
	 */
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;

		
	}

	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;

		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}

		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public ItemBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}

		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}


		public ItemBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}

		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}
		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}


		//Define a build function to create a ItemBuilder object from Item object.
		public Item build() {
			return new Item(this);
		}
	}

		

	public String getItemId() {
		return itemId;
	}

	public String getName() {
		return name;
	}

	public double getRating() {
		return rating;
	}

	public String getAddress() {
		return address;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public double getDistance() {
		return distance;
	}

	// Add toJSONObject() method to convert an Item object a JSONObject instance
	// because in our application, frontend code cannot understand Java class, it
	// can only understand JSON.

	// convert all fields(private variables) into key-value pair and store as
	// JSONObject
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			// obj.put(String key, Object value)
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories)); // categories is a set, set in JSON is a JSONArray
																// {"categories":["","",""]}
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return obj;
	}
	
	

}
