package data.privacy.experiments.marginals;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Random;
import data.privacy.methods.*;

import data.privacy.data.*;

import data.privacy.query.*;

public class Bayes_marginals {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();
		
		int[] kb = {0, 4, 3, 2, 6};

		int ds = Integer.parseInt(args[0]);												//dataset
		int wl = Integer.parseInt(args[1]);												//degree of workload
		double epsilon = Double.parseDouble(args[2]);									//privacy budget

		Data gData = new Data("Data"+ds+".dat", new Domain("Data"+ds+".domain"));		//load data (in general domain)
		Data bData = gData.binarization();												//binarization
		bData.loadCache("bData"+ds+"_"+(kb[ds]+1)+"ways.cache");						//cache file contains ALL k-way marginals of the BINARY DATA (makes computation faster)
		int rep = 20;
				
		HashSet<Marginal> mrgs = QueryGenerator.kwayMrg(gData, wl);						//generate workload
		PrintStream outFile = new PrintStream(new File("data"+ds+"mrg"+wl+"_" +epsilon+".txt"));

		for (int i = 0; i < rep; i++){
			System.out.println(i);
			Bayesian	bayes = new Bayesian(rng, bData, epsilon, 0.5, 4.0, kb[ds]);	//PrivBayes
			Data		gSyn = bayes.release().generalization();						//release result of PrivBayes (BINARY), then convert to general domain
			
			for (Marginal mrg : mrgs) {
				gData.mError(gSyn, mrg, outFile);										//total variation distance of each marginal
			}
			//outFile.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
		}

		outFile.close();			
	}
}
