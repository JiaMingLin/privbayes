package data.privacy.tools;


public class SortedPair implements Comparable<SortedPair>{
	
	public SortedPair(Pair arg0, double arg1){
		this.pair = arg0;
		this.val = arg1;
	}
	
	public Pair pair;
	public double val;
	
	@Override
	public int compareTo(SortedPair a) {
		// TODO Auto-generated method stub
		if (this.val < a.val) return 1;
		else if (this.val == a.val) return 0;
		else return -1;		
	}
}
