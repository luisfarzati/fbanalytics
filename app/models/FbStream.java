package models;

import java.util.*;
import java.text.*;
import javax.persistence.*;
import play.db.jpa.*;
import com.google.gson.*;
import play.modules.facebook.*;

public class FbStream {
	@SuppressWarnings("deprecation")
	public static void update() throws FbGraphException, ParseException {
		JsonObject stream = FbGraph.getObject("me/home");
//		JsonObject stream = FbGraph.getObject("me/home", play.modules.facebook.Parameter.with("limit", "25").and("until", "1299773070").parameters());
		Long lastEntry = getLastEntry();

		System.out.println("last: " + lastEntry.toString());

		Boolean nextPageNeeded = true;
		
		while(nextPageNeeded) {
			for(JsonElement element : stream.get("data").getAsJsonArray()) {
				JsonObject entry = element.getAsJsonObject();
				
//				if(entry.has("application") && entry.get("application").isJsonObject()) {
					FbStreamObject obj = new FbStreamObject(entry);
					if(obj.fbId <= lastEntry) {
						System.out.println("entry: " + new Long(obj.fbId).toString() + "; finishing");
						nextPageNeeded = false;
						break;
					}
					obj.create();
//				}
			}

			if(nextPageNeeded) {
				if(nextPageNeeded = stream.has("paging")) { 
					String until = stream.get("paging").getAsJsonObject().get("next").getAsString();
					until = until.substring(until.lastIndexOf('=')+1);
					System.out.println("next >> "+until);
					stream = FbGraph.getObject("me/home", play.modules.facebook.Parameter.with("limit", "25").and("until", until).parameters());
				}
			}
		}
		
		System.out.println("finished");
	}

	private static long getLastEntry() {
		Query query = JPA.em().createQuery("select MAX(fbId) from FbStreamObject");
		Long last = (Long) query.getSingleResult();
		return last != null ? last : 0;
	}
}
