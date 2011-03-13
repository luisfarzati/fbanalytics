package models;

import java.util.*;
import java.text.*;
import javax.persistence.*;
import play.db.jpa.*;
import com.google.gson.*;

@Entity
public class FbApplication extends Model {
	public long fbId;
	public String name;
}
