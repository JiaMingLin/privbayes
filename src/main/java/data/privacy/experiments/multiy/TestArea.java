package data.privacy.experiments.multiy;

import java.util.HashSet;

import data.privacy.data.Data;
import data.privacy.data.Domain;
import data.privacy.query.QueryGenerator;
import data.privacy.query.cQuery;
import data.privacy.tools.Dependence;
import data.privacy.tools.GenTool;

public class TestArea {

	private static final String RESOURCE_PATH = "/root/git/privbayes/resources/";
	public static void main(String[] args) throws Exception {
		testL1Ans();
								
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
}