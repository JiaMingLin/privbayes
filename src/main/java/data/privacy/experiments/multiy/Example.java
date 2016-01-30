package data.privacy.experiments.multiy;

import java.util.Random;

import data.privacy.data.Data;
import data.privacy.data.Domain;
import data.privacy.methods.Bayesian;

public class Example {

	private static final String RESOURCE_PATH = "/root/git/privbayes/resources/";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();
		double epsilon = 0.8;// Double.parseDouble(args[1]);

		Domain domain = new Domain(RESOURCE_PATH + "Example.domain.csv");
		Data gTrain = new Data(RESOURCE_PATH + "ExCensus.dat", domain);
		// 
		gTrain.printo_int("result/ExCensusDataTrans.csv", ",");
		Data bTrain = gTrain.binarization();
		Bayesian bayes = new Bayesian(rng, bTrain, epsilon, 0.5, 4.0, 2);
		bayes.release().generalization().printo_int("result/ExSyntheticDataTrans.csv", ",");
		bayes.release().generalization()
				.printo_data("result/ExSyntheticData.csv", ",");
		
	}


}
