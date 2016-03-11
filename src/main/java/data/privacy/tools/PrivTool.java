package data.privacy.tools;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Collections;


public class PrivTool {
	public static double LaplaceDist(Random rng, double scale){
		double U = rng.nextDouble()-0.5;
		return -scale*Math.signum(U)*Math.log(1-2*Math.abs(U));
	}
	
	public static double GeometricDist(Random rng, double alpha){
		double ProbAt0 = (1-alpha) / (1+alpha);
		double ProbGreater0 = alpha / (1+alpha);
		double ProbGeq0 = ProbAt0 + ProbGreater0;
		double logAlpha = Math.log(alpha);
		
		double randDouble = rng.nextDouble();

		if (randDouble < ProbAt0)
			return 0;
		
		int sign;
		if (randDouble < ProbGeq0) {
			sign = 1;
			randDouble -= ProbAt0;
		}
		else { 
			sign = -1;
			randDouble -= ProbGeq0;
		}
		//System.out.println("alpha/(1+alpha)=" + ProbGreater0 + ", rand=" + randDouble);
		int n = (int) Math.floor(Math.log(alpha - (1+alpha)*randDouble) / logAlpha);
		return sign * n;
	}
	
	/**
	 * Exponential mechanism: sample solution i with a probability 
	 * proportional to exp(epsilon*utility[i]/2 delta)
	 * @param rng
	 * @param solutions
	 * @param epsilon
	 * @param delta
	 * @return AP-Pair
	 */
	public static <T> T ExpoMech(Random rng, HashMap<T, Double> solutions, double epsilon, double delta){
		
		double sum = 0.0;
		
		// the max of AP-Pair scores.
		double maxu = Collections.max(solutions.values());
		
		// avoid precision error (inf or 0) when epsilon is large
		double balance = 10 - maxu*epsilon/(2*delta);				
	
		// for each AP-Pair score.
		for (double u : solutions.values()){
			sum += Math.exp(u*epsilon/(2*delta) + balance);
		}
		
		double pick = sum*rng.nextDouble();		
		double cum = 0.0;
		
		// for each entry, AP-Pair with its score.
		for (Map.Entry<T, Double> e : solutions.entrySet()){
			cum += Math.exp(e.getValue()*epsilon/(2*delta) + balance);
			
			if (cum>pick)
				return e.getKey();
		}
		
		return null;
	}
}
