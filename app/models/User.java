package models;

import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

import play.db.jpa.*;
import play.db.jpa.GenericModel.JPAQuery;

import com.google.gson.*;

import play.modules.facebook.*;

@Entity
public class User extends Model {
	@NaturalId
	public Long fbId;
	
	public String name;
	
	public String gender;

	public String locale;
	
	public String relationship;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ActivityStream activityStream;

	public User(Long fbId) {
		this.fbId = fbId;
	}
	
	public User(JsonObject json) {
		
		this.fbId = json.get("id").getAsLong();
		this.name = json.get("name").getAsString();
		
		if(json.has("gender")) {
			this.gender = json.get("gender").getAsString();
		}
		
		if(json.has("locale")) {
			this.locale = json.get("locale").getAsString();
		}
		
		if(json.has("relationship_status")) {
			this.relationship = json.get("relationship_status").getAsString();
		}
		
		this.activityStream = new ActivityStream(this);
	}
	
	public static User findById(Long fbId) {
		return User.find("fbId = ?", fbId).first();
	}

	public static User getFacebookCurrent() throws FbGraphException {
		JsonObject json = FbGraph.getObject("me");
		return new User(json);
	}
	
	public List<User> getFacebookFriends() throws FbGraphException {
		JsonObject json = FbGraph.getObject(this.fbId.toString() + "/friends");
		
		ArrayList<User> friends = new ArrayList<User>();
		for(JsonElement user : json.get("data").getAsJsonArray()) {
			friends.add(new User(user.getAsJsonObject()));
		}
		
		return friends;
	}
}
 