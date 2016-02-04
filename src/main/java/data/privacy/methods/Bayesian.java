package data.privacy.methods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import data.privacy.query.ContingencyTable;
import data.privacy.query.CountingQuery;
import data.privacy.query.QueryGenerator;
import data.privacy.query.cQuery;

import data.privacy.tools.PrivTool;
import data.privacy.tools.DAG;
import data.privacy.tools.Dependence;
import data.privacy.tools.GenTool;

import data.privacy.data.Data;


public class Bayesian implements CountingQuery, ContingencyTable {
	
	private Data data;												//sensitive database
	private Data synthetic;											//synthetic database
	private Random rng;												//random number generator
	private int k;													//degree of the Bayesian network
	private HashMap<cQuery, Double> cCache_Noisy;					//noisy marginals

	public Bayesian(Random rng1, Data data1, double ep, double alloc, double theta, int kbound) {
		// TODO Auto-generated constructor stub
		// rng1: random seed;		data1: sensitive database;		ep: privacy budget;		alloc: budget split (default 0.5)
		// theta: theta-usefulness (default 4.0)					kbound: upper bound of k (bound computational cost)
//		System.out.println("Data dimension in Bayesian: "+data.getDim());
		data = data1;
		rng = rng1;
		
		k = kbound;													//theta-usefulness
		while (k > 0){
			double num1 = ep * (1-alloc) * data.getNum();
			double num2 = theta * (data.getDim() - k) * Math.pow(2.0, k+2);
			if (num1 >= num2)
				break;
			k--;
		}
		if (k == 0) alloc = 0.0;									//k=0 -> all attributes are independent, no need to learn Bayesian network
		
		System.out.println(k);
		
		DAG model = Model_Greedy(ep * alloc);						//learn Bayesian network, budget: ep * alloc 
		int[][] intSyn = null;										
		InjectNoise(ep * (1-alloc), model);							//generate noisy marginals, budget: ep * (1-alloc)
		intSyn = Sampling(data.getNum(), model);					//sampling

		synthetic = new Data(intSyn, data.getDomain());				//build the synthetic database
	}

	public Bayesian(Random rng1, Data data1, double ep, double alloc, int k1) {
		// TODO Auto-generated constructor stub
		// given k
		
		data = data1;
		rng = rng1;
		
		k = k1;
		if (k == 0) alloc = 0.0;
		
		DAG model = Model_Greedy(ep * alloc);
		int[][] intSyn = null;
		InjectNoise(ep * (1-alloc), model);
		intSyn = Sampling(data.getNum(), model);
		
		synthetic = new Data(intSyn, data.getDomain());
	}

	private DAG Model_Greedy(double ep) {
		// TODO Auto-generated method stub
		DAG model = new DAG();
		int dim = data.getDim();		
		double delta = 1.0;
		
		HashSet<Integer> S = new HashSet<Integer>();
		// [0,1,2,...,51]
		HashSet<Integer> V = GenTool.newSet(dim);
		int init = rng.nextInt(dim); // init
		S.add(init); // [init]
		V.remove(init); //  [0,1,2,...,51] \ [init]
		model.put(init, new HashSet<Integer>()); //[ init <- []]
		
		for (int i = 0; i<dim-1; i++){
			System.out.println("Learning for dim= "+i);
			
			HashMap<Dependence, Double> deps = new HashMap<Dependence, Double>();
			
//			long start_s2v = System.currentTimeMillis();
			HashSet<Dependence> tempSet = S2V(S, V, k);
//			long stop_s2v = System.currentTimeMillis();
//			System.out.println("Length of candidates: "+tempSet.size());
//			System.out.println("S2V Spands: "+(stop_s2v-start_s2v));
			
//			long start_l1 = System.currentTimeMillis();
			for (Dependence dep : tempSet){
				double l1 = data.l1Req(dep);
				deps.put(dep, l1);
			}
//			long stop_l1 = System.currentTimeMillis();
//			System.out.println("l1 Spands: "+(stop_l1-start_l1));
			
			
//			long start_pick = System.currentTimeMillis();
			Dependence picked = PrivTool.ExpoMech(rng, deps, ep/(dim-1), delta);		
//			long stop_pick = System.currentTimeMillis();
//			System.out.println("Pick Spands: "+(stop_pick-start_pick));
//			System.out.println("======================================");
			S.add(picked.x);
			V.remove(picked.x);
			
			model.put(picked);
		}
		System.out.println("model: "+model);
		return model;
	}
	
	private HashSet<Dependence> S2V(HashSet<Integer> S, HashSet<Integer> V, int k) {
		// TODO Auto-generated method stub
		// generate candidate set for exponential mechanism
		
		HashSet<Dependence> ans = new HashSet<Dependence>();
		HashSet<HashSet<Integer>> kS = GenTool.kSub(S, k);
		// 1. [[init]]
		if (kS.isEmpty()) kS.add(new HashSet<Integer>(S));
		
		for (HashSet<Integer> source : kS){
			for (int target : V){
				ans.add(new Dependence(target, source));
			}
		}
		return ans;
	}

	private void InjectNoise(double ep, DAG model) {
		// TODO Auto-generated method stub
		// inject noise into marginals
		
		
		// [k, d-1]
		int dim = data.getDim();
		cCache_Noisy = new HashMap<cQuery, Double>();
		
		for (int i = k; i<dim; i++){
			HashSet<Integer> mrg = new HashSet<Integer>(model.get(i).p);
			mrg.add(model.get(i).x);
			
			for (cQuery cq : QueryGenerator.mrg2cq(data, mrg)){
				cCache_Noisy.put(cq, Math.max(data.cReq(cq) + PrivTool.LaplaceDist(rng, 2.0*(dim-k)/ep), 0.0));
			}
		}
		
		
		
		// [0, k-1] based on k-th marginal
		HashSet<Integer> pre = new HashSet<Integer>(model.get(k).p);
		pre.add(model.get(k).x);
		
		for (int i = 0; i<k; i++){
			HashSet<Integer> mrg = new HashSet<Integer>(model.get(i).p);
			mrg.add(model.get(i).x);
			
			HashSet<Integer> sub = new HashSet<Integer>(pre);
			sub.removeAll(mrg);
			
			for (cQuery cq : QueryGenerator.mrg2cq(data, mrg)){
				double sum = 0.0;
				
				for (cQuery subCq : QueryGenerator.mrg2cq(data, sub)){
					HashMap<Integer, Integer> mix = new HashMap<Integer, Integer>();
					mix.putAll(cq.getDetails());
					mix.putAll(subCq.getDetails());
					
					sum += cCache_Noisy.get(new cQuery(mix));
				}
				
				cCache_Noisy.put(cq, sum);
			}
		}
	}

	private int[][] Sampling(int sampleSize, DAG model) {
		// TODO Auto-generated method stub
		int dim = data.getDim();
		int[][] intSyn = new int[sampleSize][dim];
		
		int rej = 0;
		double par_smooth = 0.01;
		
		for (int i = 0; i<sampleSize; i++){
			for (int j = 0; j<dim; j++){
				Dependence dep = model.get(j);
				cQuery pre = new cQuery();
				
				for (int p : dep.p) {
					pre.put(p, intSyn[i][p]);
				}
				
				intSyn[i][dep.x] = conditional(dep.x, pre);
			}
			
			if (!data.tupleCheck(intSyn[i])) {  					//domain check. e.g., if we generate a BINARY tuple [1,1,1],  
				i--;												//and the corresponding GENERAL tuple is [7], which exceeds 
				rej++;												//the domain 0-6.
				
				if (rej > 50) {
					System.out.println("bad marginals!");			//It rarely happens, but is possible when ep is extremely small
					smooth(par_smooth);								//Make noisy marginals more smooth (does NOT violate dp)
					par_smooth *= 2;
					rej = 0;
				}
			}
			else {
				rej = 0;
			}
		}
		
		return intSyn;
	}
	
	private void smooth(double par){
		for (cQuery cq : cCache_Noisy.keySet()){
			cCache_Noisy.put(cq, cCache_Noisy.get(cq) + par * data.getNum() / Math.pow(2.0, cq.size()));
		}
	}
	
	private int conditional(int pos, cQuery pre) {
		// TODO Auto-generated method stub
		int d = data.getCell(pos);
		double sum = 0.0;
		
		for (int i = 0; i < d; i++){
			pre.put(pos, i);
			sum += cCache_Noisy.get(pre);
		}
		
		if (sum == 0.0) return rng.nextInt(d);
		double pick = sum * rng.nextDouble();
		double cum = 0.0;
		
		for (int i = 0; i < d; i++){
			pre.put(pos, i);
			cum += cCache_Noisy.get(pre);
			if (cum > pick) return i;
		}
		return -1;
	}
	
	public Double cReq(cQuery q){
		return synthetic.cReq(q);
	}

	public HashMap<cQuery, Double> cReq(HashSet<cQuery> query){
		return synthetic.cReq(query);
	}
	
	@SuppressWarnings("unused")
	private double evaluate(DAG model) {
		// TODO Auto-generated method stub
		double g = 0.0;
		for (int i = 0; i<data.getDim(); i++){
			g += data.iReq(model.get(i));
		}
		return g;
	}

	public void printo_int(String out, String sep) throws Exception {
		// TODO Auto-generated method stub
		synthetic.printo_int(out, sep);
	}

	@Override
	public HashMap<String, Double> getTable() {
		// TODO Auto-generated method stub
		return synthetic.getTable();
	}

	public Data release() {
		return synthetic;
	}
}