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
import data.privacy.tools.TimeBottle;
import data.privacy.data.Data;


public class Bayesian implements CountingQuery, ContingencyTable {
	
	private Data data;												//sensitive database
	private Data synthetic;											//synthetic database
	private Random rng;												//random number generator
	private int k;													//degree of the Bayesian network
	private HashMap<cQuery, Double> cCache_Noisy;					//noisy marginals

	/**
	 * @param rng1: random seed
	 * @param data1: sensitive database;
	 * @param ep: privacy budget;
	 * @param alloc: budget split (default 0.5)
	 * @param theta: theta-usefulness (default 4.0)
	 * @param kbound: upper bound of k (bound computational cost)
	 */
	public Bayesian(Random rng1, Data data1, double ep, double alloc, double theta, int kbound) {

		data = data1;
		rng = rng1;
		
		//theta-usefulness
		k = kbound;													
		while (k > 0){
			double num1 = ep * (1-alloc) * data.getNum();
			double num2 = theta * (data.getDim() - k) * Math.pow(2.0, k+2);
			if (num1 >= num2)
				break;
			k--;
		}
		
		//k=0 -> all attributes are independent, no need to learn Bayesian network
		if (k == 0) alloc = 0.0;									
		
		//learn Bayesian network, budget: ep * alloc
		DAG model = Model_Greedy(ep * alloc);						 
		int[][] intSyn = null;								
		
		//generate noisy marginals, budget: ep * (1-alloc)
		InjectNoise(ep * (1-alloc), model);							
		intSyn = Sampling(data.getNum(), model);					//sampling

		synthetic = new Data(intSyn, data.getDomain());				//build the synthetic database
	}

	public Bayesian(Random rng1, Data data1, double ep, double alloc, int k1) {
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

	/**
	 * Training the Bayesian Network.
	 * Initialize
	 *   1. S = empty set of integer.
	 *   2. V = the array of attributes.
	 *   3. N = empty Bayesian Network.
	 *   4. Randomly select an attribute, v_1.
	 *   5. Add v_1 to S.
	 *   6. Add AP-Pair of v_1 with empty parents to N.
	 * 
	 * Greedy Select: Repeat following steps for each attributes except v_1.
	 *   1. Initialize an empty set of AP-Pair candidates say O.
	 *   2. For each attribute v in V \ S, and each subset P of S with order less than degree k, add AP-Pair (v, P) to O.
	 *   3. Select an AP-Pair (v, P) from O with maximal mutual information. 
	 *   4. Add the selected AP-Pair (v,P) to N, and add v to S.  	 
	 *     
	 * @param ep: a splitting privacy budget. 
	 * @return: Privacy Bayesian Network 
	 */
	private DAG Model_Greedy(double ep) {
		DAG model = new DAG();
		int dim = data.getDim();
		// TODO what is delta doing?
		double delta = 1.0;
		
		HashSet<Integer> S = new HashSet<Integer>();
		HashSet<Integer> V = GenTool.newSet(dim);
		int init = rng.nextInt(dim); 
		S.add(init); 
		V.remove(init); 
		model.put(init, new HashSet<Integer>()); //[ init <- []]
		
		for (int i = 0; i<dim-1; i++){
			System.out.print(i);
			
			HashMap<Dependence, Double> deps = new HashMap<Dependence, Double>();
			
			long start_s2v = System.currentTimeMillis();
			// the set of AP-Pairs, attributes in V and parents selected from S.
			HashSet<Dependence> tempSet = S2V(S, V, k);
			long stop_s2v = System.currentTimeMillis();
			TimeBottle.saveTime("S2V",(int)(stop_s2v-start_s2v));
			
			long start_l1 = System.currentTimeMillis();
			// scoring each one AP-Pair.
			for (Dependence dep : tempSet){
				double l1 = data.l1Req(dep);
				
				// cache all the AP-Pair with its score.
				deps.put(dep, l1);
			}
			long stop_l1 = System.currentTimeMillis();
			TimeBottle.saveTime("L1Req", (int)(stop_l1-start_l1));
			
			long start_pick = System.currentTimeMillis();
			// pick an AP-Pair according to exponential mechanism.
			Dependence picked = PrivTool.ExpoMech(rng, deps, ep/(dim-1), delta);		
			long stop_pick = System.currentTimeMillis();
			TimeBottle.saveTime("Pick", (int)(stop_pick-start_pick));
			
			S.add(picked.x);
			V.remove(picked.x);
			
			model.put(picked);
		}
		return model;
	}
	
	/**
	 * Generate candidate set(set of @Dependency) for exponential mechanism.
	 * @param S
	 * @param V
	 * @param k
	 * @return
	 */
	private HashSet<Dependence> S2V(HashSet<Integer> S, HashSet<Integer> V, int k) {

		HashSet<Dependence> ans = new HashSet<Dependence>();

		HashSet<HashSet<Integer>> kS = GenTool.kSub(S, k);

		if (kS.isEmpty()) kS.add(new HashSet<Integer>(S));
		
		// the Cartesian product of kS and V.  
		for (HashSet<Integer> source : kS){
			for (int target : V){
				ans.add(new Dependence(target, source));
			}
		}
		return ans;
	}

	/**
	 * Generation of noisy conditional distributions.
	 * 
	 * Initialize
	 * 	  1. P: empty set of noisy conditional distributions.
	 *    2. k: degree of Bayesian Network.
	 *    3. dim: number of attributes.
	 * 
	 * Noisy Conditions
	 * 	 For each attribute "a" indexed from k to dim-1. 
	 * 	  1. Materialize the joint distribution Pr[a, p], where p is the parents set of a. 
	 * TODO how to materialize the joint distribution?
	 * 	  2. Generate differentially private Pr*[a, p] by adding Laplace noise to Pr[a,p].
	 *    3. Set negative values in Pr*[a,p] to 0 and normalize.
	 *    4. Derive Pr*[a | p] from Pr*[a,p].
	 *    5. Add Pr*[a | p] to P.
	 *   
	 *   For each attribute "a" indexed from 1 to k-1.
	 *   // TODO make more clear for the case of [1,k-1] add noise.
	 *     Derive Pr*[a | p] from the first one in P.
	 *  
	 * @param ep: privacy budget
	 * @param model: Baysian Network
	 */
	private void InjectNoise(double ep, DAG model) {
		// 
		// [k, d-1]
		int dim = data.getDim();
		cCache_Noisy = new HashMap<cQuery, Double>();
		
		// for each attribute indexed from k to dim-1
		for (int i = k; i<dim; i++){
			// retrieve the parents set of an attribute.
			HashSet<Integer> mrg = new HashSet<Integer>(model.get(i).p);
			//TODO why add attribute to parents set
			mrg.add(model.get(i).x);
			
			for (cQuery cq : QueryGenerator.mrg2cq(data, mrg)){
				cCache_Noisy.put(cq, Math.max(data.cReq(cq) + PrivTool.LaplaceDist(rng, 2.0*(dim-k)/ep), 0.0));
			}
		}

		// [0, k-1] based on k-th marginal
		// retrieve the parents set of attribute indexed k.
		HashSet<Integer> pre = new HashSet<Integer>(model.get(k).p);
		pre.add(model.get(k).x);
		
		for (int i = 0; i<k; i++){
			// retrieve the parents set of an attribute.
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

	/**
	 * Sampling according to the Private Baysian Network.
	 * This method would refer to the @cCache_Noisy
	 * @param sampleSize
	 * @param model
	 * @return
	 */
	private int[][] Sampling(int sampleSize, DAG model) {

		int dim = data.getDim();
		// synthetic data
		int[][] intSyn = new int[sampleSize][dim];
		
		int rej = 0;
		
		// TODO how is the effective of smooth parameter.
		double par_smooth = 0.01;
		
		for (int i = 0; i<sampleSize; i++){
			for (int j = 0; j<dim; j++){
				// for each attribute, and its parents set.
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
		double g = 0.0;
		for (int i = 0; i<data.getDim(); i++){
			g += data.iReq(model.get(i));
		}
		return g;
	}

	public void printo_int(String out, String sep) throws Exception {

		synthetic.printo_int(out, sep);
	}

	@Override
	public HashMap<String, Double> getTable() {

		return synthetic.getTable();
	}

	public Data release() {
		return synthetic;
	}
}
