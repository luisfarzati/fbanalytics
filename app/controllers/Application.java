package controllers;

import play.*;
import play.db.jpa.JPA;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import play.mvc.*;
import com.google.gson.*;

import play.modules.facebook.*;

import java.util.*;
import java.text.*;

import javax.persistence.Query;

import org.eclipse.jdt.core.dom.ThisExpression;

import models.*;

public class Application extends Controller {

	private static User currentUser;

	@Before(unless="index")
	private static void retrieveSession() throws FbGraphException {
		if(FbGraph.getAccessToken() == null) {
			Application.index();
		} 
		else if(session.get("userId") == null) {
			currentUser = User.getFacebookCurrent();
			session.put("userId", currentUser.fbId);
			session.put("name", currentUser.name);
		}
		else {
			currentUser = new User(Long.parseLong(session.get("userId")));
		}
	}
	
    public static void index() {
        render();
    }

	public static void overallAnalytics() {
		renderJSON(ApplicationActivity.overall());
	}
	
	public static void updateFriendStream() throws FbGraphException {
		JPA.em().getTransaction().rollback();
		
		for(User friend : currentUser.getFacebookFriends()) {
			JPA.em().getTransaction().begin();
			
			System.out.println("@" + friend.name);
			
			if(User.findById(friend.fbId) == null) {
				friend.create();
			}
			else {
				friend = User.findById(friend.fbId);
			}
			
			try {
				friend.activityStream.update();
				friend.save();
				JPA.em().getTransaction().commit();
			}
			catch(Exception ex) {
				System.out.println("ERR: " + ex.getMessage());
				JPA.em().getTransaction().rollback();
			}
		}
		
		render();
	}

	public static void stream() {
		render("");
	}
	
	/*
	public static void stream() throws FbGraphException, ParseException
	{
		String userName = session.get("name");
		FbStream.update();
		render(userName);
	}

	public static void friendStream() throws FbGraphException, ParseException {
		JsonObject friends = FbGraph.getObject("me/friends");
		ArrayList<Long> users = new ArrayList<Long>();
		for(JsonElement el : friends.get("data").getAsJsonArray()) {
			users.add(el.getAsJsonObject().get("id").getAsLong());
		}
		
		Integer total = users.size();
//		Query query = JPA.em().createQuery("select DISTINCT userId from FbStreamObject");
//		List<Long> users = (List<Long>) query.getResultList();
		
		Integer count = 0;

		System.out.println("FRIENDS = " + total.toString());
		
		try {
			for(Long id : users) {
				count++;
				System.out.println(" (" + count.toString() + "/" + total.toString() + ") USER >> " + id.toString());
				System.out.println();
				
				JsonObject stream = FbGraph.getObject(id.toString() + "/feed");
				Boolean nextPageNeeded = true;
	
				while(nextPageNeeded) {
					for(JsonElement element : stream.get("data").getAsJsonArray()) {
						JsonObject entry = element.getAsJsonObject();
						FbStreamObject obj = new FbStreamObject(entry, id);
						nextPageNeeded = obj.created.after(new GregorianCalendar(2010, 6, 1).getTime());
						if(!nextPageNeeded) break;
						System.out.println("-entry: " + obj.created.toString() + " (" + obj.application.name + ")");
						obj.create();
					}
		
					if(nextPageNeeded) {
						if(nextPageNeeded = stream.has("paging")) { 
							String until = stream.get("paging").getAsJsonObject().get("next").getAsString();
							until = until.substring(until.lastIndexOf('=')+1);
							System.out.println("next >> "+until);
							stream = FbGraph.getObject(id.toString() + "/feed", play.modules.facebook.Parameter.with("limit", "25").and("until", until).parameters());
						}
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
	 */

}
