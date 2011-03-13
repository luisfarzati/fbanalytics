package controllers;

import play.*;
import play.mvc.*;
import com.google.gson.*;

import play.modules.facebook.*;

import java.util.*;
import java.text.*;

import models.*;

public class Application extends Controller {

	@Before(unless="index")
	static void retrieveSession() throws FbGraphException {
		if(FbGraph.getAccessToken() == null) {
			Application.index();
		} else if(session.get("userId") == null) {
			FbUser user = FbUser.getCurrent();
			session.put("userId", user.fbId);
			session.put("name", user.name);
		}
	}

    public static void index() {
        render();
    }

	public static void stream() throws FbGraphException, ParseException
	{
		String userName = session.get("name");
		FbStream.update();
		render(userName);
	}

	public static void overallAnalytics() {
		renderJSON(ApplicationActivity.overall());
	}
}
