package data.privacy.methods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import data.privacy.query.CountingQuery;
import data.privacy.query.cQuery;
import data.privacy.query.Marginal;

import data.privacy.data.Data;

import data.privacy.tools.PrivTool;

public class Fourier implements CountingQuery {

	private Data data;
	private Random rng;
	private HashMap<cQuery, Double> cCache_Noisy;
	private HashMap<Marginal, Double> fCache_Noisy;
	
	public Fourier(Random rng1, Data data1, double ep, HashSet<cQuery> gCq) {
		// TODO Auto-generated constructor stub
		data = data1;
		rng = rng1;
		cCache_Noisy = new HashMap<cQuery, Double>();
		fCache_Noisy = new HashMap<Marginal, Double>();
				
		HashSet<cQuery> bCq = data.qBinarization(gCq);
		
		HashSet<Marginal> mrg = new HashSet<Marginal>();
		for (cQuery q : bCq){
			mrg.add(new Marginal(q.keySet()));
		}
		
		HashSet<Marginal> fq = new HashSet<Marginal>();
		for (Marginal q : mrg){
			fq.addAll(q.powerSet());
		}
		
		int nr = fq.size();
		for (Marginal q : fq){
			fCache_Noisy.put(q, data.fReq(q) + PrivTool.LaplaceDist(rng, 2.0*nr/ep));
		}
	}
	
	public Fourier(Random rng1, Data data1, double ep, HashSet<Marginal> gMrg, String type) {
		// TODO Auto-generated constructor stub
		data = data1;
		rng = rng1;
		cCache_Noisy = new HashMap<cQuery, Double>();
		fCache_Noisy = new HashMap<Marginal, Double>();
				
		HashSet<Marginal> bMrg = data.mBinarization(gMrg);
		
		HashSet<Marginal> fq = new HashSet<Marginal>();
		for (Marginal q : bMrg){
			fq.addAll(q.powerSet());
		}
		
		int nr = fq.size();
		for (Marginal q : fq){
			fCache_Noisy.put(q, data.fReq(q) + PrivTool.LaplaceDist(rng, 2.0*nr/ep));
		}
	}
	
	private double f2c(cQuery q) {
		// TODO Auto-generated method stub
		double ans = 0.0;
		
		for (Marginal f : new Marginal(q.keySet()).powerSet()){
			ans += f.fsgn(q) * fCache_Noisy.get(f);
		}
		
		ans = ans / Math.pow(2.0, q.keySet().size());	
		return ans;
	}
	
	public Double cReq(cQuery gCq) {
		Double qa = cCache_Noisy.get(gCq);
		if (qa == null) qa = cAns(gCq);
		return qa;
	}

	private Double cAns(cQuery gCq) {
		// TODO Auto-generated method stub
		double ans =  Math.max(0.0, f2c(data.qBinarization(gCq)));
		cCache_Noisy.put(gCq, ans);
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
