package models;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

import play.db.jpa.*;
import com.google.gson.*;

@Entity
public class Application extends Model {
	@NaturalId
	public Long fbId;

	public String name;
	
	public Application(JsonObject json) {
		this.fbId = json.get("id").getAsLong();
		this.name = json.get("name").getAsString();
	}

	public static Application findById(Long fbId) {
		return Application.find("fbId = ?", fbId).first();
	}
	
}
