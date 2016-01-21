package data.privacy.tools;
public class Pair{
	public Pair(int a, int b){
		first = a;
		second = b;
	}
	
	public int endPoint(int a){
		if (a == first) return second;
		if (a == second) return first;
		return - 1;
	}
	
	public int first, second;

	@Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        Pair rhs = (Pair) obj;
        return (rhs.first == this.first && rhs.second == this.second) 
        		|| (rhs.first == this.second && rhs.second == this.first);
    }
	
	@Override
    public int hashCode() {    	
    	return  10001 * Math.min(first, second) + Math.max(first, second); 
    }
}