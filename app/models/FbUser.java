package models;

import play.*;
import play.db.jpa.*;
import javax.persistence.*;
import com.google.gson.*;
import play.modules.facebook.*;

@Entity
public class FbUser extends Model {
	public long fbId;
	public String name;
	public String gender;
	public String locale;

	public FbUser(long id, String name) {
		this.fbId = id;
		this.name = name;
	}

	public static FbUser getCurrent() throws FbGraphException {
		JsonObject result = FbGraph.getObject("me");
		return new FbUser(result.get("id").getAsLong(), result.get("name").getAsString());
	}
}
