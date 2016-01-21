package data.privacy.methods;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Random;

import data.privacy.data.Data;
import data.privacy.query.ContingencyTable;
import data.privacy.query.CountingQuery;
import data.privacy.query.QueryGenerator;
import data.privacy.query.cQuery;
import data.privacy.tools.GenTool;
import data.privacy.tools.PrivTool;

public class Contingency implements CountingQuery, ContingencyTable {

	private Data synthetic;
	private Data data;
	private Random rng;
	private TreeMap<Double, cQuery> cCache_Noisy;
	private double noisySum;

	public Contingency(Random rng1, Data data1, double ep, double theta) {
		data = data1;
		rng = rng1;

		InjectNoise(ep, theta);
		int[][] intSyn = Sampling(data.getNum());
		synthetic = new Data(intSyn, data.getDomain());
	}

	private void InjectNoise(double ep, double theta) {
		// TODO Auto-generated method stub
		cCache_Noisy = new TreeMap<Double, cQuery>();
		noisySum = 0.0;

		for (cQuery cq : QueryGenerator.mrg2cq(data, GenTool.newSet(data.getDim()))){
			double noisy = Math.max(data.cReq(cq) + PrivTool.LaplaceDist(rng, 2.0 / ep), 0.0);
			if (noisy > theta) {
				noisySum += noisy;
				cCache_Noisy.put(noisySum, cq);
			}
		}
	}

	private int[][] Sampling(int sampleSize) {
		// TODO Auto-generated method stub
		int dim = data.getDim();
		int[][] intSyn = new int[sampleSize][dim];

		for (int i = 0; i<sampleSize; i++) {
			double pick = noisySum * rng.nextDouble();

			cQuery selected = cCache_Noisy.ceilingEntry(pick).getValue();
			for (int j = 0; j<dim; j++) {
				intSyn[i][j] = selected.get(j);
			}

		}
		return intSyn;
	}

	@Override
	public HashMap<String, Double> getTable() {
		// TODO Auto-generated method stub
		return synthetic.getTable();
	}

	@Override
	public Double cReq(cQuery q) {
		// TODO Auto-generated method stub
		return synthetic.cReq(q);
	}

	@Override
	public HashMap<cQuery, Double> cReq(HashSet<cQuery> query) {
		// TODO Auto-generated method stub
		return synthetic.cReq(query);
	}

	public Data release() {
		// TODO Auto-generated method stub
		return synthetic;
	}
}
