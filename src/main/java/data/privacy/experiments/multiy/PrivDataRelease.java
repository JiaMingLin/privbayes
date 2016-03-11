package data.privacy.experiments.multiy;

import java.io.File;
import java.util.Random;

import data.privacy.data.Data;
import data.privacy.data.Domain;
import data.privacy.methods.Bayesian;
import data.privacy.system.PropReader;
import data.privacy.tools.TimeBottle;

public class Fakedata {

	private static final String RESOURCE_PATH = PropReader.getPropStr("RESOURCE_PATH");
	private static final String RESULT_PATH = PropReader.getPropStr("RESULT_PATH");
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Random rng = new Random();
		
		String sourceData = args[0];
		String domainFile = args[1];
		double epsilon = Double.parseDouble(args[2]);
		int degree = Integer.parseInt(args[3]);
		String convertedEp = args[4];

		Domain domain = new Domain(domainFile);
		Data gTrain = new Data(sourceData, domain);
		//
//		gTrain.printo_int("result/fakedata-coarse.csv", ",");
		Data bTrain = gTrain.binarization();

		Bayesian bayes = new Bayesian(rng, bTrain, epsilon, 0, 4.0, 2);
		System.out.println();
		String syntheticFileName = String.format("%s%dD1M%sE%dK_SyntheticData.csv", RESULT_PATH, domain.getDim(), convertedEp, degree);
		File synFile = new File(syntheticFileName);
		if(!synFile.exists()){
			synFile.createNewFile();
		}
		bayes.release().generalization()
				.printo_data(syntheticFileName, ",");
		
		TimeBottle.showAverage();
	}
}
