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

public class Bayes_libsvm_data2 {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();

		int model = 1;//Integer.parseInt(args[0]);
		double epsilon = 5;//Double.parseDouble(args[1]);
		int rep = 1;


		//model setting
		int yPos = 0;
		HashSet<Integer> ySet = new HashSet<Integer>();
		if (model == 1) {
			// Salary: if salary > 50K
			yPos = 14;
			ySet.add(1);
		}
		else if (model == 2) {
			// Sex: if is male
			yPos = 9;
			ySet.add(1);
		}
		else if (model == 3) {
			// Education: if holds a post-secondary degree
			yPos = 3;
			ySet.add(9);
			ySet.add(10);
			ySet.add(11);
			ySet.add(12);
			ySet.add(13);
			ySet.add(14);
			ySet.add(15);
		}
		else if (model == 4) {
			// Marital-status: if never-married
			yPos = 5;
			ySet.add(4);
		}
		else {
			System.err.println("Model selection error!");
			System.exit(1);
		}
		//end of model setting


		Domain domain = new Domain("Data2.domain");
		Data gTrain = new Data("Train2_1.dat", domain);
		Data bTrain = gTrain.binarization();
		bTrain.loadCache("bTrain2_4ways.cache");

		//new DataPrinter("Test2.dat", domain).printo_libsvm("d2model"+model+".test", yPos, ySet); 			//TestDataConvertor
		
		PrintStream outFile = new PrintStream(new File("data2model"+model+"_"+epsilon+".txt"));

		//loading test file=========================
		BufferedReader testFile = new BufferedReader(new FileReader("d2model"+model+".test"));
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

			Bayesian	bayes = new Bayesian(rng, bTrain, epsilon, 0.5, 4.0, 3);
			bayes.release().generalization().printo_libsvm("d2model"+model, yPos, ySet);   //print the synthetic training data (LIBSVM format)

			Runtime.getRuntime().exec("svm-train -t 2 d2model"+model).waitFor();
			Runtime.getRuntime().exec("svm-predict d2model"+model+".test d2model"+model+".model d2model"+model+".pred").waitFor();

			BufferedReader predFile = new BufferedReader(new FileReader("d2model"+model+".pred"));
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
