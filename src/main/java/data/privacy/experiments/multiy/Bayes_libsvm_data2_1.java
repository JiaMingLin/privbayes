package data.privacy.experiments.multiy;

import java.util.Random;

import data.privacy.methods.*;
import data.privacy.system.PropReader;
import data.privacy.data.*;

public class Bayes_libsvm_data2_1 {

	private static final String RESOURCE_PATH = PropReader.getPropStr("RESOURCE_PATH");

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Random rng = new Random();
		double epsilon = 0.8;// Double.parseDouble(args[1]);

		Domain domain = new Domain(RESOURCE_PATH + "Data2.domain.csv");
		Data gTrain = new Data(RESOURCE_PATH + "CensusData.dat", domain);
		// 
		gTrain.printo_int("result/CensusDataTrans.csv", ",");
		Data bTrain = gTrain.binarization();
		
		Bayesian bayes = new Bayesian(rng, bTrain, epsilon, 0, 4.0, 2);
		bayes.release().generalization().printo_int("result/SyntheticDataTrans.csv", ",");
		bayes.release().generalization()
				.printo_data("result/SyntheticData.csv", ",");
		
	}
}
