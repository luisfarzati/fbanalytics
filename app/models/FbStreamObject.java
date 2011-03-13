package models;

import java.util.*;
import java.text.*;
import javax.persistence.*;
import play.db.jpa.*;
import com.google.gson.*;

@Entity
public class FbStreamObject extends Model {
	public Date created;
	public long fbId;
	public long userId;
	public long appId;
	public String appName;
	public String type;
	
	@Column(columnDefinition="TEXT")
	public String message;

	@Column(columnDefinition="TEXT")
	public String name;

	@Column(columnDefinition="TEXT")
	public String caption;

	@Column(columnDefinition="TEXT")
	public String description;

	public String action;

	@Column(columnDefinition="VARCHAR(500)")
	public String icon;
	
	@Column(columnDefinition="VARCHAR(500)")
	public String picture;

	public int likes;
	public int comments;

	private static final DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public FbStreamObject(JsonObject json) throws ParseException {
		String id = json.get("id").getAsString();
		this.fbId = Long.parseLong(id.substring(id.indexOf('_')+1));
		this.created = iso8601.parse(json.get("created_time").getAsString());
		this.userId = json.get("from").getAsJsonObject().get("id").getAsLong();

		if(json.has("application") && json.get("application").isJsonObject()) {
			this.appId = json.get("application").getAsJsonObject().get("id").getAsLong();
			this.appName = json.get("application").getAsJsonObject().get("name").getAsString();
		}
		
		this.type = json.get("type").getAsString();

		if(json.has("message")) {
			this.message = json.get("message").getAsString();
		}

		if(json.has("name")) {
			this.name = json.get("name").getAsString();
		}
	
		if(json.has("caption")) {
			this.caption = json.get("caption").getAsString();
		}

		if(json.has("description")) {
			this.description = json.get("description").getAsString();
		}

		if(json.has("actions")) {
			JsonArray actions = json.get("actions").getAsJsonArray();
			if(actions.size() > 2) {
				this.action = actions.get(actions.size()-1).getAsJsonObject().get("name").getAsString();
			}
		}

		if(json.has("icon")) {
			this.icon = json.get("icon").getAsString();
		}

		if(json.has("picture")) {
			this.picture = json.get("picture").getAsString();
		}

		if(json.has("likes")) {
			this.likes = json.get("likes").getAsJsonObject().get("count").getAsInt();
		}

		if(json.has("comments")) {
			this.comments = json.get("comments").getAsJsonObject().get("count").getAsInt();
		}
	}
}
