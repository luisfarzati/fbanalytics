package models;

import java.text.ParseException;
import java.util.*;

import javax.persistence.*;

import org.hamcrest.core.IsNull;

import play.db.jpa.*;
import com.google.gson.*;

import play.modules.facebook.*;

@Entity
public class ActivityStream extends Model {
	@OneToOne(mappedBy="activityStream")
	public User user;

	public Date lastUpdate;

	public ActivityStream(User user) {
		this.user = user;
		this.lastUpdate = new GregorianCalendar(2010, 6, 1).getTime();
	}
	
	public void update() throws FbGraphException, ParseException {
		JsonObject json = FbGraph.getObject(this.user.fbId.toString() + "/feed");
		Date now = new GregorianCalendar().getTime();
		
		Boolean nextPageNeeded = true;
		while(nextPageNeeded) {
			for(JsonElement entry: json.get("data").getAsJsonArray()) {
				Activity activity = new Activity(entry.getAsJsonObject(), this.user);

				if(activity.date.before(this.lastUpdate)) {
					nextPageNeeded = false;
					break;
				}
				
				activity.create();
				System.out.println("+ " + activity.type + " " + (activity.author != activity.user ? "from " + activity.author.name : "") + (activity.application == null ? "" : " via " + activity.application.name) + " " + activity.date.toString() + " (#"  + activity.fbId + ")");
			}
			
			if(nextPageNeeded && (nextPageNeeded = json.get("data").getAsJsonArray().size() > 0)) {
				String until = json.get("paging").getAsJsonObject().get("next").getAsString();
				Long timestamp = Long.parseLong(until.substring(until.lastIndexOf('=')+1))-1;
				System.out.println(">> next page " + until);
				
				json = FbGraph.getObject(this.user.fbId.toString() + "/feed", play.modules.facebook.Parameter.with("limit", "25").and("until", timestamp.toString()).parameters());
			}
		}
		
		this.lastUpdate = now;
	}
}

