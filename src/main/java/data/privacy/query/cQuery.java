package data.privacy.query;

import java.util.HashMap;
import java.util.HashSet;

/**
 * implements of a query, for example, height=170 and weight=60
 *
 */
public class cQuery {
	private HashMap<Integer, Integer> details;
	
	public cQuery(){
		details = new HashMap<Integer, Integer>();
	}
	
	public cQuery(HashMap<Integer, Integer> d1){
		details = new HashMap<Integer, Integer>(d1);
	}
	
	public cQuery(cQuery cq1){
		details = new HashMap<Integer, Integer>(cq1.getDetails());
	}
	
	public cQuery(int p1, int v1){
		details = new HashMap<Integer, Integer>();
		details.put(p1, v1);
	}
	
	public cQuery(int p1, int v1, int p2, int v2){
		details = new HashMap<Integer, Integer>();
		details.put(p1, v1);
		details.put(p2, v2);
	}
	
	public cQuery(int p1, int v1, int p2, int v2, int p3, int v3){
		details = new HashMap<Integer, Integer>();
		details.put(p1, v1);
		details.put(p2, v2);
		details.put(p3, v3);
	}
	
	public void put(int p1, int v1){
		details.put(p1, v1);
	}
	
	public void merge(cQuery cq1){
		details.putAll(cq1.details);
	}
	
	public HashSet<Integer> keySet(){
		return new HashSet<Integer>(details.keySet());
	}
	
	public Integer get(Integer key){
		return details.get(key);
	}
	
	public HashMap<Integer, Integer> getDetails(){
		return details;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        cQuery rhs = (cQuery) obj;
        return this.details.equals(rhs.details);
    }
	
	@Override
    public int hashCode() {
		int myHash = 0;
		for (int e : details.keySet()){
			myHash += (e + details.get(e)) * e;
		}
    	return myHash + details.hashCode() + details.size() * 7117;
    }
	
	public String toString() {
		return details.toString();
	}

	public int size() {
		// TODO Auto-generated method stub
		return details.size();
	}
}
