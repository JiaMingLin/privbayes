package data.privacy.query;

import java.util.HashMap;
import java.util.HashSet;


public interface CountingQuery {
	/**
	 * Find how many rows satisfy the given query.
	 * @param q: a query
	 * @return
	 */
	public Double cReq(cQuery q);
	public HashMap<cQuery, Double> cReq(HashSet<cQuery> query);
}
