package data.privacy.tools;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

public class GenTool {
	
	public static HashSet<Integer> newSet(int d){
		HashSet<Integer> ans = new HashSet<Integer>();
		for (int i = 0; i < d; i++)
			ans.add(i);
		return ans;
	}
	
	public static HashSet<Integer> newSet(int l, int r) {
		HashSet<Integer> ans = new HashSet<Integer>();
		for (int i = l; i < r; i++)
			ans.add(i);
		return ans;
	}
	
	public static Double log2(double d) {
		// TODO Auto-generated method stub
		return Math.log(d)/Math.log(2);
	}
	
	public static <T> HashSet<HashSet<T>> kSub(HashSet<T> S, int k) {
		// TODO Auto-generated method stub
		HashSet<HashSet<T>> ans = new HashSet<HashSet<T>>();
		if (k == 0) {
			ans.add(new HashSet<T>());
			return ans;
		}
		
		HashSet<T> subS = new HashSet<T>(S);
		
		for (T s : S){
			subS.remove(s);
			
			for (HashSet<T> sub : kSub(subS, k-1)){
				HashSet<T> full = new HashSet<T>(sub);
				full.add(s);
				ans.add(full);
			}
		}
		return ans;
	}
	
	public static <T> HashSet<HashSet<T>> kSub(HashSet<T> S, int k, HashSet<T> Prior) {
		// TODO Auto-generated method stub
		
		HashSet<T> interSet = intersection(S, Prior);
		HashSet<T> complSet = complement(S, Prior); 
		
		HashSet<HashSet<T>> ans = kSub(complSet, k-interSet.size());
		
		for (HashSet<T> subS : ans){
			subS.addAll(interSet);
		}
		
		return ans;
	}

	public static <T> HashSet<T> rndSubset(Random rng, HashSet<T> full, int m){
	    ArrayList<T> res = new ArrayList<T>(m);
	    int count = 0;
	    
	    for(T e : full){
	        count++;
	        if (count <= m)
	            res.add(e);
	        else{
	            int r = rng.nextInt(count);
	            if (r < m)
	                res.set(r, e);
	        }
	    }
	    return new HashSet<T>(res);
	}
	
	public static void time() {
		// TODO Auto-generated method stub
		DateFormat df = DateFormat.getDateTimeInstance (DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale ("en", "EN"));
		System.out.println(df.format (new Date()));
	}
	
	public static void time(String msg) {
		// TODO Auto-generated method stub
		DateFormat df = DateFormat.getDateTimeInstance (DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale ("en", "EN"));
		System.out.println(msg + "@"+ df.format (new Date()));
	}

	public static double hamming(int[] a, int[] b, int y) {
		// TODO Auto-generated method stub
		int l = a.length;
		int ans = 0;
		
		for (int i = 0; i < l; i++) {
			if (a[i] != b[i] && i != y)
				ans--;
		}
		return ans;
	}

	public static <T> HashSet<T> intersection(HashSet<T> a, HashSet<T> b) {
		// TODO Auto-generated method stub
		HashSet<T> c = new HashSet<T>(a);
		c.retainAll(b);
		return c;
	}
	
	public static <T> HashSet<T> complement(HashSet<T> a, HashSet<T> b) {
		// TODO Auto-generated method stub
		HashSet<T> c = new HashSet<T>(a);
		c.removeAll(b);
		return c;
	}
}
