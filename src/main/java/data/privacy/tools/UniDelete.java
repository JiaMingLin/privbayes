package data.privacy.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;



public class UniDelete {
	public UniDelete(int a){
		size = a;
		partial = new ArrayList<HashSet<Integer>>();
		tmpGroup = new int[size];
		
		for (int i = 0; i<size; i++){
			tmpGroup[i] = i;
			
			HashSet<Integer> self = new HashSet<Integer>();
			self.add(i);
			partial.add(self);
		}
	}
	
	public void delete(HashMap<Pair, Double> edges, int a, int b) {
		// TODO Auto-generated method stub
		for (int f: partial.get(tmpGroup[a])){
			for (int s: partial.get(tmpGroup[b])){
				edges.remove(new Pair(f, s));
			}
		}
		
		partial.get(tmpGroup[a]).addAll(partial.get(tmpGroup[b]));
		for (int f: partial.get(tmpGroup[a])){
			tmpGroup[f] = tmpGroup[a];
		}
	}
	
	private int size;
	private ArrayList<HashSet<Integer>> partial;
	private int[] tmpGroup;
}
