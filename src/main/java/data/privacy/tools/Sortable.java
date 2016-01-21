package data.privacy.tools;

public class Sortable <T> implements Comparable<Sortable <T>> {
	
	public T key;
	public double val;
	
	public Sortable(T key1, double val1){
		key = key1;
		val = val1;
	}
	
	public int compareTo(Sortable <T> a) {
		// TODO Auto-generated method stub
		if (this.val < a.val) return 1;
		else if (this.val == a.val) return 0;
		else return -1;		
	}
}
