package data.privacy.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import data.privacy.query.Marginal;
import data.privacy.query.cQuery;
import data.privacy.tools.GenTool;

public class DomainConvertor {
	
	private int[] pos;
	private int[] len;
	private Domain gDomain;
	private Domain bDomain;
	
	public DomainConvertor(Domain domain){
		gDomain = domain;
		int biDim = 0;
		
		// exactly the number of attributes specified in "*.domain" file.
		int dim = domain.getDim();
		pos = new int[dim];
		len = new int[dim];
		
		for (int i = 0; i<dim; i++){
			// [0, 4, 7, 11, 15, 19, 22, 26, 29, 32, 33, 37, 41, 45, 51]
			pos[i] = biDim;
			len[i] = GenTool.log2(domain.getCell(i) - 1).intValue() + 1;
			biDim += len[i];
		}
		
		int[] biCells = new int[biDim];
		Arrays.fill(biCells, 2);
		
		bDomain = new Domain(biCells, this);
	}
	
	public Domain getBinaryDomain() {
		return bDomain;
	}
	
	
	//binarization
	
	public Data binaryData(Data gData) {
		int num = gData.getNum();
		int[][] bEntries = new int[num][bDomain.getDim()];
		
		for (int i = 0; i<num; i++){
			bEntries[i] = binaryData(gData.getEntry(i));
		}
		
		return new Data(bEntries, bDomain);
	}
	
	public int[] binaryData(int[] general) {
		// TODO Auto-generated method stub
		int[] binary = new int[bDomain.getDim()];
		
		for (int d = 0; d < gDomain.getDim(); d++){
			int tmp = general[d];
			for (int p = pos[d]+len[d]-1; p>= pos[d]; p--){
				binary[p] = tmp % 2;
				tmp = tmp / 2;
			}
		}
		
		return binary;
	}
	
	public HashSet<cQuery> binaryQuery(HashSet<cQuery> gcq) {
		// TODO Auto-generated method stub
		HashSet<cQuery> bcq = new HashSet<cQuery>();
		
		for (cQuery q : gcq){
			bcq.add(binaryQuery(q));
		}
		
		return bcq;
	}
	
	public HashMap<cQuery, cQuery> qBinarizationMap(HashSet<cQuery> gcq) {
		// TODO Auto-generated method stub
		HashMap<cQuery, cQuery> cqMap = new HashMap<cQuery, cQuery>();
		
		for (cQuery q : gcq){
			cqMap.put(binaryQuery(q), q);
		}
		
		return cqMap;
	}
	
	public HashSet<Marginal> binaryMarginal(HashSet<Marginal> gMrg) {
		// TODO Auto-generated method stub
		HashSet<Marginal> bMrg = new HashSet<Marginal>();
		
		for (Marginal mrg : gMrg){
			bMrg.add(binaryMarginal(mrg));
		}
		
		return bMrg;
	}

	public Marginal binaryMarginal(Marginal mrg) {
		// TODO Auto-generated method stub
		HashSet<Integer> bAtt = new HashSet<Integer>();
		for (int k : mrg.set()){	
			bAtt.addAll(GenTool.newSet(pos[k], pos[k]+len[k]));
		}
		return new Marginal(bAtt);
	}

	public cQuery binaryQuery(cQuery q) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> qmap = new HashMap<Integer, Integer>();
		
		for (int k : q.keySet()){	
			int v = q.get(k);
			for (int p = pos[k]+len[k]-1; p>= pos[k]; p--){
				qmap.put(p, v % 2);
				v = v / 2;
			}
		}
		
		return new cQuery(qmap);
	}
	
	
	//generalization
	
	public Data generalData(Data bData) {
		int num = bData.getNum();
		int[][] gEntries = new int[num][gDomain.getDim()];
		
		for (int i = 0; i<num; i++){
			gEntries[i] = generalData(bData.getEntry(i));
		}
		
		return new Data(gEntries, gDomain);
	}
	
	public int[] generalData(int[] binary) {
		// TODO Auto-generated method stub
		int[] general = new int[gDomain.getDim()];
		
		for (int d = 0; d < gDomain.getDim(); d++){
			int tmp = 0;
			for (int p = 0; p < len[d]; p++){
				tmp *= 2;
				tmp += binary[pos[d]+p];
			}
			general[d] = tmp;
		}
		
		return general;
	}


	public boolean tupleCheck(int[] general) {
		// TODO Auto-generated method stub
		return gDomain.tupleCheck(general);
	}
	
	public ArrayList<HashSet<Integer>> relatedAttr() {
		ArrayList<HashSet<Integer>> ans = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < gDomain.getDim(); i++) {
			ans.add(GenTool.newSet(pos[i], pos[i]+len[i]));
		}
		return ans;
	}


}
