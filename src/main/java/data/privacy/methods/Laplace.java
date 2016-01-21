package data.privacy.methods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import data.privacy.query.CountingQuery;
import data.privacy.query.cQuery;
import data.privacy.query.Marginal;
import data.privacy.tools.PrivTool;
import data.privacy.data.Data;

public class Laplace implements CountingQuery {
	
	private Data data;
	private Random rng;
	private double noise;
	private HashMap<cQuery, Double> cCache_Noisy;
	
	public Laplace(Random rng1, Data data1, double ep, HashSet<cQuery> cq) {
		// TODO Auto-generated constructor stub
		data = data1;
		rng = rng1;
		cCache_Noisy = new HashMap<cQuery, Double>();
		
		HashSet<Marginal> mrg = new HashSet<Marginal>();
		for (cQuery q : cq){
			mrg.add(new Marginal(q.keySet()));
		}
		
		noise = 2.0 * mrg.size() / ep;
	}
	
	public Laplace(Random rng1, Data data1, double ep, HashSet<Marginal> mrgs, String type) {
		// TODO Auto-generated constructor stub
		data = data1;
		rng = rng1;
		cCache_Noisy = new HashMap<cQuery, Double>();
		noise = 2.0 * mrgs.size() / ep;
	}
	
	
	public Double cReq(cQuery q) {
		Double qa = cCache_Noisy.get(q);
		if (qa == null) qa = cAns(q);
		return qa;
	}

	private Double cAns(cQuery q) {
		// TODO Auto-generated method stub
		double ans = Math.max(0.0, data.cReq(q) + PrivTool.LaplaceDist(rng, noise));
		cCache_Noisy.put(q, ans);
		return ans;
	}

	public HashMap<cQuery, Double> cReq(HashSet<cQuery> query) {
		HashMap<cQuery, Double> ans = new HashMap<cQuery, Double>();
		for (cQuery q : query){
			ans.put(q, cReq(q));
		}
		return ans;
	}
}
