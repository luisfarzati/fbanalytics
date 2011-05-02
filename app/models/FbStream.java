/*

package models;

import java.util.*;
import java.text.*;
import javax.persistence.*;
import play.db.jpa.*;
import com.google.gson.*;
import play.modules.facebook.*;

public class FbStream {
	@SuppressWarnings("deprecation")
	public static void update() throws FbGraphException {
		JsonObject stream = FbGraph.getObject("me/home");
		Date lastEntry = getLastEntry();
		if(lastEntry == null) lastEntry = new GregorianCalendar(2011, 1, 1).getTime();

		System.out.println("last: " + lastEntry.toString());

		Boolean nextPageNeeded = true;
	
		try {
			while(nextPageNeeded) {
				for(JsonElement element : stream.get("data").getAsJsonArray()) {
					JsonObject entry = element.getAsJsonObject();
					FbStreamObject obj = new FbStreamObject(entry);
					System.out.println("entry: " + obj.created.toString() + " (" + obj.appName + ")");
					if(obj.created.before(lastEntry)) {
						System.out.println("finishing");
						nextPageNeeded = false;
						break;
					}
					obj.create();
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
		}
		catch(FbGraphException fe) {
			System.out.println("*** FbGraphException");
		}
		catch(ParseException pe) {
			System.out.println("*** ParseException");
		}
		catch(Exception e) {
			System.out.println("*** Exception");
		}
		
		System.out.println("finished");
	}

	private static Date getLastEntry() {
		Query query = JPA.em().createQuery("select MAX(created) from FbStreamObject");
		Date last = (Date) query.getSingleResult();
		return last;
	}
}

 * 
 */
