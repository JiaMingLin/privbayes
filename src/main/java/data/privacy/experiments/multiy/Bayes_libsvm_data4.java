package data.privacy.experiments.multiy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import data.privacy.methods.*;

import data.privacy.data.*;

public class Bayes_libsvm_data4 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();

		int model = Integer.parseInt(args[0]);
		double epsilon = Double.parseDouble(args[1]);
		int rep = 50;
		
		
		//model setting
		int yPos = 0;
		HashSet<Integer> ySet = new HashSet<Integer>();
		if (model == 1) {
			// Y12: if disabled on getting about outside
			yPos = 15;
			ySet.add(1);
		}
		else if (model == 2) {
			// Y14: if disabled on managing money
			yPos = 2;
			ySet.add(1);
		}
		else if (model == 3) {
			// Y6: if disabled on getting to the bathroom or using toilet
			yPos = 10;
			ySet.add(1);
		}
		else if (model == 4) {
			// Y4: if disabled on dressing
			yPos = 12;
			ySet.add(1);
		}
		else if (model == 5) {
			// Y13: if disabled on traveling
			yPos = 3;
			ySet.add(1);
		}
		else if (model == 6) {
			// Y8: if disabled on doing light housework
			yPos = 8;
			ySet.add(1);
		}
		else {
			System.err.println("Model selection error!");
			System.exit(1);
		}
		//end of model setting
		
		
		Domain domain = new Domain("Data4.domain");
		Data gTrain = new Data("Train4.dat", domain);
		Data bTrain = gTrain.binarization();
		bTrain.loadCache("bTrain4_7ways.cache");
		
		//new DataPrinter("Test4.dat", domain).printo_libsvm("d4model"+model+".test", yPos, ySet); 			//TestDataConvertor
		
		PrintStream outFile = new PrintStream(new File("data4model"+model+"_"+epsilon+".txt"));
		
		//loading test file=========================
		BufferedReader testFile = new BufferedReader(new FileReader("d4model"+model+".test"));
		String s = testFile.readLine();
		ArrayList<Integer> testY = new ArrayList<Integer>();
		
		while (s != null) {
			testY.add(Integer.parseInt(s.split(" ")[0]));
			s = testFile.readLine();
		}
		
		testFile.close();
		//end of loading test file==================

		for (int i = 0; i < rep; i++) {
			System.out.println(i);

			Bayesian	bayes = new Bayesian(rng, bTrain, epsilon, 0.5, 4.0, 6);
			bayes.release().generalization().printo_libsvm("d4model"+model, yPos, ySet);   //print the synthetic training data (LIBSVM format)

			Runtime.getRuntime().exec("svm-train -t 2 d4model"+model).waitFor();	
			Runtime.getRuntime().exec("svm-predict d4model"+model+".test d4model"+model+".model d4model"+model+".pred").waitFor();

			BufferedReader predFile = new BufferedReader(new FileReader("d4model"+model+".pred"));
			double err = 0.0;				
			for (int t = 0; t < testY.size(); t++) {
				if (testY.get(t) != Integer.parseInt(predFile.readLine()))
					err++;
			}
			predFile.close();

			outFile.println(err/testY.size() + "\t");
		}
		outFile.close();
	}
}
