package data.privacy.experiments.marginals;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Random;
import data.privacy.methods.*;

import data.privacy.data.*;

import data.privacy.query.*;

public class Fourier_marginals {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Random rng = new Random();

		int ds = Integer.parseInt(args[0]);
		int wl = Integer.parseInt(args[1]);
		double epsilon = Double.parseDouble(args[2]);

		Data gData = new Data("Data"+ds+".dat", new Domain("Data"+ds+".domain"));
		Data bData = gData.binarization();
		int rep = 20;
		HashSet<Marginal> mrgs = QueryGenerator.kwayMrg(gData, wl);


		PrintStream outFile = new PrintStream(new File("data"+ds+"mrg"+wl+"_" +epsilon+".txt"));

		for (int i = 0; i < rep; i++){
			System.out.println(i);

			Fourier   	fourier = new Fourier(rng, bData, epsilon, mrgs, "mrg");
			for (Marginal mrg : mrgs) {
				gData.mError(fourier, mrg, outFile);
			}
			//outFile.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
		}

		outFile.close();			
	}
}
