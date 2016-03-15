package data.privacy.experiments.multiy;

import java.io.File;
import java.util.Random;

import data.privacy.data.Data;
import data.privacy.data.Domain;
import data.privacy.methods.Bayesian;
import data.privacy.tools.TimeBottle;

public class PrivDataRelease {

	/**
	 * 1. loading the domain spec 
	 * 2. loading data
	 * 3. map data to coarse data. 
	 * 4. data binarize. 
	 * 5. training Private Basyian Network and sampling in binary format. 
	 * 6. generalize data and print to file.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Random rng = new Random();

		String sourceData = args[0];
		String coarseData = args[1];
		String outputPath = args[2];
		String domainFile = args[3];
		double epsilon = Double.parseDouble(args[4]);
		double epSplit = Double.parseDouble(args[5]);

		// loading the domain specifications
		Domain domain = new Domain(domainFile);

		// loading data to memory,
		// source data would be mapped to coarse data, here.
		Data gTrain = new Data(sourceData, domain);

		// coarse data records the data mapped to bins indexes.
		gTrain.printo_int(coarseData, " ");

		// binary the general data.
		Data bTrain = gTrain.binarization();

		Bayesian bayes = new Bayesian(rng, bTrain, epsilon, epSplit, 4.0, 2);
		System.out.println();
		String syntheticFileName = String.format("%s", outputPath);
		File synFile = new File(syntheticFileName);
		if (!synFile.exists()) {
			synFile.createNewFile();
		}

		bayes.release().generalization().printo_int(syntheticFileName, ",");
		bayes.release().generalization().printo_data("/released_synthetic_data.csv", ",");
		TimeBottle.showAverage();
	}
}
