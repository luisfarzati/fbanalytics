package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.db.jpa.*;

@Entity
public class Activity extends Model {
	@NaturalId
	public String fbId;

	@ManyToOne
	public User user;

	@ManyToOne
	public User author;
	
	@ManyToOne
	public Application application;

	public String type;

	public Date date;
	
	public String action;
	
	@Lob
	public String icon;

	@Lob
	public String picture;
	
	public Integer likes;
	
	public Integer comments;
	
	private static final DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public Activity(JsonObject json, User user) throws ParseException {
		this(json);
		this.user = user;
	}
	
	public Activity(JsonObject json) throws ParseException {
		this.fbId = json.get("id").getAsString();
		this.date = iso8601.parse(json.get("created_time").getAsString());
		this.author = new User(json.get("from").getAsJsonObject());
		this.type = json.get("type").getAsString();

		if(json.has("to")) {
			this.user = new User(json.get("to").getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject());
		}
		else {
			this.user = this.author;
		}
		
		if(User.findById(this.author.fbId) == null) {
			this.author.create();
		}
		else {
			this.author = User.findById(this.author.fbId);
		}

		if(json.has("application") && json.get("application").isJsonObject()) {
			this.application = new Application(json.get("application").getAsJsonObject());

			if(Application.findById(this.application.fbId) == null) {
				this.application.create();
			}
			else {
				this.application = Application.findById(this.application.fbId);
			}
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
