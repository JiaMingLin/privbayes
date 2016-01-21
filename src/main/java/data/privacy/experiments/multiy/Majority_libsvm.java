package data.privacy.experiments.multiy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Random;

import data.privacy.tools.PrivTool;

public class Majority_libsvm {
	public static void main(String[] args) throws Exception {
		int d = Integer.parseInt(args[0]);
		int model = Integer.parseInt(args[1]);
		double epsilon = Double.parseDouble(args[2]) / 4.0;    	//4 classifiers simultaneously
		int rep = 100;

		BufferedReader testFile = new BufferedReader(new FileReader("d"+d+"model"+model+".test"));
		String s = testFile.readLine();
		double[] testY = {0.0, 0.0};
		int tSize = 0;

		while (s != null) {
			if (Integer.parseInt(s.split(" ")[0]) > 0)
				testY[1] ++;
			else 
				testY[0] ++;
			
			tSize ++;
			s = testFile.readLine();
		}

		testFile.close();
		
		BufferedReader trainFile = new BufferedReader(new FileReader("d"+d+"model"+model));
		s = trainFile.readLine();
		int majority = 0;

		while (s != null) {
			majority += Integer.parseInt(s.split(" ")[0]);
			s = trainFile.readLine();
		}

		trainFile.close();
		
		
		PrintStream outFile = new PrintStream(new File("majority"+d+"_model"+model+"_"+args[2]+".txt"));
		double err = 0.0;
		Random rng = new Random();
		
		for (int i = 0; i < rep; i++) {
			double nGuess = majority + PrivTool.LaplaceDist(rng, 2.0/epsilon);
			if (nGuess > 0) 
				err += testY[0] / tSize;
			else
				err += testY[1] / tSize;
		}
		
		outFile.println(err/rep);
		outFile.close();
	}
}
