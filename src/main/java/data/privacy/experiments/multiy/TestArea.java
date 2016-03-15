package data.privacy.experiments.multiy;

import java.util.HashSet;
import java.util.Random;

import data.privacy.data.Data;
import data.privacy.data.Domain;
import data.privacy.query.QueryGenerator;
import data.privacy.query.cQuery;
import data.privacy.system.PropReader;
import data.privacy.tools.Dependence;
import data.privacy.tools.GenTool;
import data.privacy.tools.PrivTool;

public class TestArea {

	private static final String RESOURCE_PATH = PropReader.getPropStr("RESOURCE_PATH");
	public static void main(String[] args) throws Exception {		
//		testMrg2CQ();
//		testCAns();
		testIntToStr(2);
		testIntToStr(3);
		testIntToStr(4);
		testIntToStr(5);
		testIntToStr(6);
		testIntToStr(1);
		
								
	}
	
	public static void testS2V(){
		
		HashSet<Integer> setInt = new HashSet<Integer>();
		for(int i=1 ;i<8 ;i++){
			setInt.add(i);

		}
		HashSet<HashSet<Integer>> output = GenTool.kSub(setInt, 4);
		System.out.println(output.size());
	} 
	
	public static void testMrg2CQ() throws Exception{
		HashSet<Integer> mrg = new HashSet<Integer>();
		for(int i=1 ;i<7 ;i++){
			mrg.add(i);
		}
		Domain domain = new Domain(RESOURCE_PATH + "Example.domain.csv");
		Data gTrain = new Data(RESOURCE_PATH + "ExCensus.dat", domain);
		Data bTrain = gTrain.binarization();
		HashSet<cQuery> querySet = QueryGenerator.mrg2cq(bTrain, mrg);
		System.out.println(querySet);
		System.out.println(querySet.size());
	}
	
	public static void testL1Ans() throws Exception{
		HashSet<Integer> mrg = new HashSet<Integer>();
		for(int i=1 ;i<3 ;i++){
			mrg.add(i);
		}
		Dependence dep = new Dependence(0, mrg);
		Domain domain = new Domain(RESOURCE_PATH + "Example.domain.csv");
		Data gTrain = new Data(RESOURCE_PATH + "ExCensus.dat", domain);
		Data bTrain = gTrain.binarization();
		System.out.println(dep);
		double result = bTrain.l1Req(dep);
		System.out.println(result);
	}
	
	public static void testCAns() throws Exception{
		HashSet<Integer> mrg = new HashSet<Integer>();
		for(int i=1 ;i<3 ;i++){
			mrg.add(i);
		}
		Dependence dep = new Dependence(0, mrg);
		Domain domain = new Domain(RESOURCE_PATH + "fakedata.domain.csv");
		Data gTrain = new Data(RESOURCE_PATH + "fakedata.csv", domain);
		Data bTrain = gTrain.binarization();
		
		cQuery cq = new cQuery();
		cq.put(dep.x, 0);
		System.out.println("CQuery: "+cq);
		System.out.println("Dependency: "+dep);				
		int result = bTrain.cReq(cq).intValue();
		System.out.println(result);
	}
	
	public static void testLaplace(){
		Random rng = new Random();
		int dim = 31;
		int k = 2;
		double ep = 10;
		
		
		for(int i = 0; i < 100; i ++ ){
			double U = rng.nextDouble()-0.5;
//			System.out.println(U+", "+Math.signum(U));
			System.out.println(PrivTool.LaplaceDist(rng, 2.0*(dim-k)/(ep*40000)));
		}
	}
	
	public static void testStrToInt(){
		System.out.println(Math.floor(14.999999));
		System.out.println(1e-4 == 0.0001);
		System.out.println((int) Math.floor( (Double.parseDouble("38") - 20) / (3 + 1e-4) ));
		System.out.println((int) Math.floor( (Double.parseDouble("56") - 20) / (3 + 1e-4) ));
		System.out.println((int) Math.floor( (Double.parseDouble("43") - 20) / (3 + 1e-4) ));
		System.out.println((int) Math.floor( (Double.parseDouble("64") - 20) / (3 + 1e-4) ));
		System.out.println((int) Math.floor( (Double.parseDouble("26") - 20) / (3 + 1e-4) ));
	}
	
	public static void testIntToStr(int index){

		int step = 1;
		int min = 12;
		System.out.println((index + 0.5) * step + min);
	}
}
