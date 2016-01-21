package data.privacy.query;

import java.util.HashSet;

public class Marginal {
	private HashSet<Integer> details;
	
	public Marginal(){
		details = new HashSet<Integer>();
	}
	
	public Marginal(HashSet<Integer> d1){
		details = new HashSet<Integer>(d1);
	}
	
	public Marginal(Marginal fq1){
		details = new HashSet<Integer>(fq1.set());
	}
	
	public Marginal(int p1){
		details = new HashSet<Integer>();
		details.add(p1);
	}
	
	public Marginal(int p1, int p2){
		details = new HashSet<Integer>();
		details.add(p1);
		details.add(p2);
	}
	
	public Marginal(int p1, int p2, int p3){
		details = new HashSet<Integer>();
		details.add(p1);
		details.add(p2);
		details.add(p3);
	}
	
	public void add(int p1){
		details.add(p1);
	}
	
	public int size() {
		// TODO Auto-generated method stub
		return details.size();
	}

	public HashSet<Integer> set() {
		// TODO Auto-generated method stub
		return details;
	}

	public HashSet<Marginal> powerSet() {
		// TODO Auto-generated method stub
		int fsize = (int) Math.pow(2.0, details.size());
		HashSet<Marginal> ans = new HashSet<Marginal>();
		
		for (int i = 0; i < fsize; i++){
			HashSet<Integer> subset = new HashSet<Integer>();
			int code = i;
			
			for (int p : details){
				if (code % 2 == 0){
					subset.add(p);
				}
				code = code/2;
			}
			
			ans.add(new Marginal(subset));
		}
		
		return ans;
	}

	public int fsgn(cQuery q) {
		// TODO Auto-generated method stub
		int ans = 1;
		for (Integer i : details){
			if (q.get(i) == 1) ans *= -1;
		}
		return ans;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        Marginal rhs = (Marginal) obj;
        return this.details.equals(rhs.details);
    }
	
	@Override
    public int hashCode() {
		int myHash = 0;
		for (int e : details){
			myHash += e*e;
		}
    	return myHash + details.hashCode() + details.size() * 7117;
    }
	
	public String toString() {
		return details.toString();
	}
}
