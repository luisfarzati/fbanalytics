package models;

import java.util.*;
import java.text.*;
import javax.persistence.*;

import play.db.jpa.*;

import com.google.gson.*;

public class ApplicationActivity {
	public static String overall() {
		Query query = JPA.em().createQuery("select date(created), appId, appName, count(*) from FbStreamObject group by date(created), appId, appName");
		StringBuilder sb = new StringBuilder();
		sb.append("{\"data\":[");
		
		List results = query.getResultList();
		if(results.size() > 0) {
			Iterator iterator = results.iterator();
			while(iterator.hasNext()) {
				Object[] o = (Object[]) iterator.next();
				sb.append("{\"date\":\"" + o[0].toString()+"\"");
				sb.append(",\"n\":" + o[3].toString());
				sb.append(",\"appId\":" + (o[1] != null ? o[1].toString() : "null"));
				sb.append(",\"appName\":\"" + (o[2] != null ? o[2].toString() : "") + "\"}");
				if(iterator.hasNext()) sb.append(",");
			}
		}
		sb.append("]}");
		return sb.toString();
	}
}