package data.privacy.query;

import java.util.HashMap;
import java.util.HashSet;


public interface CountingQuery {
	public Double cReq(cQuery q);
	public HashMap<cQuery, Double> cReq(HashSet<cQuery> query);
}
