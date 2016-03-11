package data.privacy.tools;

import java.util.HashSet;

/**
 * implements of AP-Pair(attribute with its parents pair).
 *
 */
public class Dependence {

	public int x;
	public HashSet<Integer> p;
	
	public Dependence(int x1, HashSet<Integer> p1) {
		// TODO Auto-generated constructor stub
		x = x1;
		p = p1;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        Dependence rhs = (Dependence) obj;
        return this.x == rhs.x && this.p.equals(rhs.p);
    }
	
	@Override
    public int hashCode() {
		int myHash = 0;
		for (int e : p) {
			myHash += e*e;
		}
    	return myHash + x * 7117;
	}
	
	public String toString() {
		String s = Integer.toString(x) + " <-";
		for (int e : p) {
			s += " " + Integer.toString(e);
		}
		return s;
	}
}
