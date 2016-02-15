package data.privacy.experiments.multiy;

import java.util.Random;

import data.privacy.data.Data;
import data.privacy.data.Domain;
import data.privacy.methods.Bayesian;
import data.privacy.system.PropReader;

public class Fakedata {

	private static final String RESOURCE_PATH = PropReader.getPropStr("RESOURCE_PATH");

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();
		double epsilon = 0.8;// Double.parseDouble(args[1]);

		Domain domain = new Domain(RESOURCE_PATH + "fakedata.domain.csv");
		Data gTrain = new Data(RESOURCE_PATH + "fakedata.csv", domain);
		//
		gTrain.printo_int("result/fakedata-coarse.csv", ",");
		Data bTrain = gTrain.binarization();

		Bayesian bayes = new Bayesian(rng, bTrain, epsilon, 0.5, 4.0, 2);
		bayes.release().generalization()
				.printo_int("result/FakeSyntheticDataTrans.csv", ",");
		bayes.release().generalization()
				.printo_data("result/FakeSyntheticData.csv", ",");
	}
}
