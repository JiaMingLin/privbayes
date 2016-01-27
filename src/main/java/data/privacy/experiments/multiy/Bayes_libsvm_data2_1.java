package data.privacy.experiments.multiy;

import java.util.Random;
import data.privacy.methods.*;

import data.privacy.data.*;

public class Bayes_libsvm_data2_1 {

	private static final String RESOURCE_PATH = "/root/git/privbayes/resources/";  
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();
		double epsilon = 5;//Double.parseDouble(args[1]);

		Domain domain = new Domain(RESOURCE_PATH+"Data2.domain");
		Data gTrain = new Data(RESOURCE_PATH+"Train2_1.dat", domain);
		Data bTrain = gTrain.binarization();
		bTrain.loadCache(RESOURCE_PATH+"bTrain2_4ways_1.cache");
		Bayesian	bayes = new Bayesian(rng, bTrain, epsilon, 0.5, 4.0, 1);
		bayes.release().generalization().printo_data("TestingSample.csv", ",");
	}
}
