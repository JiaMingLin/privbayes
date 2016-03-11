package data.privacy.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import data.privacy.tools.GenTool;

import data.privacy.data.Data;

public class QueryGenerator {
	public static HashSet<cQuery> kwayCQ(Data data, int k) {
		
		HashSet<cQuery> cq = new HashSet<cQuery>();
		HashSet<Integer> S = GenTool.newSet(data.getDim());
		
		HashSet<HashSet<Integer>> mrgs = GenTool.kSub(S, k);
		for (HashSet<Integer> mrg : mrgs){
			cq.addAll(mrg2cq(data, mrg));
		}
		return cq;
	}
	
	public static HashSet<cQuery> kwayCQ(Random rng, Data data, int k, double ratio) {
		
		HashSet<cQuery> cq = new HashSet<cQuery>();
		HashSet<Integer> S = GenTool.newSet(data.getDim());
		
		HashSet<HashSet<Integer>> mrgs = GenTool.kSub(S, k);	
		for (HashSet<Integer> mrg : mrgs){
			cq.addAll(mrg2cq(data, mrg));
		}
		
		return GenTool.rndSubset(rng, cq, (int) (ratio * cq.size()));
	}
	
	/**
	 * For the parents set in an AP-Pair, return all the possible queries. 
	 * 
	 * @param data: the whole data set in memory. Here it is only used to find the bin(grid) numbers. 
	 * @param mrg: the parents set in an AP-Pair.
	 * @return
	 */
	public static HashSet<cQuery> mrg2cq(Data data, HashSet<Integer> mrg) { // mrg = 1. [init]
		
		HashSet<cQuery> ans = new HashSet<cQuery>();
		if (mrg.isEmpty()){
			ans.add(new cQuery());
			return ans;
		}
		
		int pos = mrg.iterator().next();
		HashSet<Integer> subset = new HashSet<Integer>(mrg);
		subset.remove(pos);
		for (cQuery cq : mrg2cq(data, subset)){
			for (int i = 0; i<data.getCell(pos); i++){// grid count if continuous, categories count if discrete.
				HashMap<Integer, Integer> subMap = new HashMap<Integer, Integer>(cq.getDetails());
				subMap.put(pos, i);
				ans.add(new cQuery(subMap));
			}
		}
		return ans;
	}
	
	public static HashSet<cQuery> mrg2cq(Data data, Marginal mrg) {
				
		return mrg2cq(data, mrg.set());
	}

	public static HashSet<Marginal> kwayMrg(Data data, int k) {
		
		HashSet<Marginal> mrgs = new HashSet<Marginal>();
		for (HashSet<Integer> mrg : GenTool.kSub(GenTool.newSet(data.getDim()), k)){
			mrgs.add(new Marginal(mrg));
		}
		return mrgs;
	}
	
}
