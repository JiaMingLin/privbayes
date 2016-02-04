package data.privacy.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import data.privacy.query.*;

import data.privacy.tools.Dependence;
import data.privacy.tools.GenTool;
import data.privacy.tools.Sortable;

public class Data implements CountingQuery, ContingencyTable{
	
	private HashMap<String, Double> global;
	private int[][] entries;
	private int num;
	private Domain domain;
	private HashMap<cQuery, Double> cCache;
	private HashMap<Marginal, Double> fCache;
	private HashMap<Dependence, Double> iCache;
	private HashMap<Dependence, Double> l1Cache;
	
	
	//Basic
	
	public Data(String dataFileName, Domain domain1) throws Exception {
		
		BufferedReader dataFile = new BufferedReader(new FileReader(dataFileName));
		
		domain = domain1;
		num = Integer.parseInt(dataFile.readLine());
		entries = new int[num][domain.getDim()];
		global = new HashMap<String, Double>();
		
		for (int s = 0; s < num; s++){
			int[] intTokens = domain.str2int(dataFile.readLine().split("\\s+"));
			updateGlobal(intTokens);
			entries[s] = intTokens;
		}
		dataFile.close(); // System.out.println(global.size)
		
		cCache = new HashMap<cQuery, Double>();
		fCache = new HashMap<Marginal, Double>();
		iCache = new HashMap<Dependence, Double>();
		l1Cache = new HashMap<Dependence, Double>();
	}
	
	public Data(int[][] entries1, Domain domain1) {
		// TODO Auto-generated constructor stub
		entries = entries1;
		domain = domain1;
		
		num = entries.length;
		global = new HashMap<String, Double>();
		
		for (int s = 0; s < num; s++){
			updateGlobal(entries[s]);
		}
		
		cCache = new HashMap<cQuery, Double>();
		fCache = new HashMap<Marginal, Double>();
		iCache = new HashMap<Dependence, Double>();
		l1Cache = new HashMap<Dependence, Double>();
	}	
	
	private void updateGlobal(int[] intTokens) {
		// TODO Auto-generated method stub
		String tuple = Arrays.toString(intTokens);		
		Double gCount = global.get(tuple);
		
		if (gCount == null) gCount = 0.0;
		global.put(tuple, gCount + 1.0);
	}

	
	//Domain Related
	
	public Data binarization() {
		System.out.println("Data dimension in binarization: "+domain.getDim());
		return domain.binarization(this);
	}
	
	public Data generalization() {
		// TODO Auto-generated method stub
		return domain.generalization(this);
	}
	
	public cQuery qBinarization(cQuery gCq) {
		// TODO Auto-generated method stub
		return domain.qBinarization(gCq);
	}
	
	public HashSet<cQuery> qBinarization(HashSet<cQuery> gCq) {
		// TODO Auto-generated method stub
		return domain.qBinarization(gCq);
	}
	
	public HashMap<cQuery, cQuery> qBinarizationMap(HashSet<cQuery> gCq) {
		// TODO Auto-generated method stub
		return domain.qBinarizationMap(gCq);
	}
	
	public HashSet<Marginal> mBinarization(HashSet<Marginal> gMrg) {
		// TODO Auto-generated method stub
		return domain.mBinarization(gMrg);
	}
	
	
	
	public boolean tupleCheck(int[] tuple) {
		// TODO Auto-generated method stub
		return domain.tupleCheck(tuple);
	}
	
	public ArrayList<HashSet<Integer>> relatedAttr() {
		return domain.relatedAttr();
	}
	
	//Query Answering
	
	public Double cReq(cQuery q){
		Double qa = cCache.get(q);
		if (qa == null) qa = cAns(q);
		return qa;
	}

	public HashMap<cQuery, Double> cReq(HashSet<cQuery> query){
		HashMap<cQuery, Double> ans = new HashMap<cQuery, Double>();
		
		for (cQuery q : query){
			Double qa = cCache.get(q);
			if (qa == null) qa = cAns(q);
			ans.put(q, qa);
		}
		return ans;
	}

/*	private double cAns(cQuery q) {
//		// TODO Auto-generated method stub
//		double ans = 0.0;
//		for (int i = 0; i<num; i++){
//			ans += cMatch(q, entries[i]);
//		}	
//		
//		cCache.put(q, ans);
//		return ans;
//	}
//	
//	private int cMatch(cQuery q, int[] e) {
//		// TODO Auto-generated method stub
//		for (int i : q.keySet()){
//			if (e[i] != q.get(i))
//				return 0;
//		}
//		return 1;
//	}
*/
	
	private double cAns(cQuery q) {
		// TODO Auto-generated method stub
		int[] pos = new int[q.size()];	
		int[] val = new int[q.size()];	
		int len = 0;
		for (int i : q.keySet()){
			pos[len] = i;
			val[len] = q.get(i);
			len++;
		}
		
		double ans = 0.0;
		for (int i = 0; i<num; i++){
			int inc = 1;
			for (int l = 0; l<len; l++)
				if (entries[i][pos[l]] != val[l]) {
					inc = 0;
					break;
				}
			ans += inc;
		}
		
		cCache.put(q, ans);
		return ans;
	}
	
	public Double fReq(Marginal q){
		Double qa = fCache.get(q);
		if (qa == null) qa = fAns(q);
		return qa;
	}

	public HashMap<Marginal, Double> fReq(HashSet<Marginal> query){
		HashMap<Marginal, Double> ans = new HashMap<Marginal, Double>();
		
		for (Marginal q : query){
			Double qa = fCache.get(q);
			if (qa == null) qa = fAns(q);
			ans.put(q, qa);
		}
		return ans;
	}

/*	private double fourier(fQuery q) {
//		// TODO Auto-generated method stub
//		int fsize = (int) Math.pow(2.0, q.size());
//		double ans = 0.0;
//		
//		for (int i = 0; i < fsize; i++){
//			HashMap<Integer, Integer> cq = new HashMap<Integer, Integer>();
//			int code = i;
//			int sgn = 1;
//			
//			for (int p : q.set()){
//				if (code % 2 == 0){
//					cq.put(p, 0);
//				}
//				else {
//					cq.put(p, 1);
//					sgn *= -1;
//				}
//				code = code/2;
//			}
//			
//			ans += sgn * cReq(new cQuery(cq));
//		}
//		
//		fCache.put(q, ans);
//		return ans;
//	}
*/
	
/*	private double fAns(Marginal q) {
//		// TODO Auto-generated method stub
//		double ans = 0.0;
//		
//		for (int i = 0; i<num; i++){
//			ans += fMatch(q, entries[i]);
//		}
//		
//		fCache.put(q, ans);
//		return ans;
//	}
//	
//	private int fMatch(Marginal q, int[] e) {
//		// TODO Auto-generated method stub
//		int sgn = 1;
//		for (int i : q.set()){
//			if (e[i] == 1)
//				sgn *= -1;
//		}
//		return sgn;
//	}
*/
	
	private double fAns(Marginal q) {
		// TODO Auto-generated method stub
		int[] pos = new int[q.size()];		
		int len = 0;
		for (int i : q.set()){
			pos[len] = i;
			len++;
		}
		
		double ans = 0.0;
		for (int i = 0; i<num; i++){
			int inc = 1;
			for (int l = 0; l<len; l++)
				inc += entries[i][pos[l]];
			ans += inc%2 * 2 - 1;
		}
		
		fCache.put(q, ans);
		return ans;
	}

	public Double l1Req(Dependence dep){
		Double qa = l1Cache.get(dep);
		if (qa == null) qa = l1Ans(dep);
		return qa;
	}

	private Double l1Ans(Dependence dep) {
		// TODO Auto-generated method stub
		double ans = 0.0;
		
		HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();
		states.put(0, 0);
		int ceil = (num+1)/2; // num = data size
		
		long cq_start = System.currentTimeMillis();
		HashSet<cQuery> CQSet = QueryGenerator.mrg2cq(this, dep.p);
//		System.out.println(CQSet.size());
//		System.out.println(CQSet);
		long cq_stop = System.currentTimeMillis();
//		System.out.println("Numbers of CQ: "+CQSet.size());
//		System.out.println("CQ Spands: "+(cq_stop - cq_start));
		
		
		long scoring_start = System.currentTimeMillis();
		for (cQuery cq : CQSet){
			
			cq.put(dep.x, 0);
			int a = cReq(new cQuery(cq)).intValue();
			cq.put(dep.x, 1);
			int b = cReq(new cQuery(cq)).intValue();
			
			HashMap<Integer, Integer> newStates = new HashMap<Integer, Integer>();
			
			for (int A : states.keySet()){
				int B = states.get(A);
				int newA = Math.min(A + a, ceil);
				int newB = Math.min(B + b, ceil);
				
				if (newStates.get(newA) == null || newStates.get(newA) < B){
					newStates.put(newA, B);
				}
				
				if (newStates.get(A) == null || newStates.get(A) < newB){
					newStates.put(A, newB);
				}
			}
//			System.out.println("State size: "+states.size());
			states = newStates;
		}
		
		long scoring_stop = System.currentTimeMillis();
//		System.out.println("Scoring Spends: "+ (scoring_stop - scoring_start));
		ans = -num;
		
		for (int A : states.keySet()){
			int B = states.get(A);
			double score = A + B - num;
			if (score > ans) ans = score;
		}

		
		l1Cache.put(dep, ans);
		return ans;
	}

	public Double iReq(Dependence dep){
		Double qa = iCache.get(dep);
		if (qa == null) qa = iAns(dep);
		return qa;
	}

/*	private Double iAns(Dependence dep) {
//		// TODO Auto-generated method stub
//		double ans = 0.0;
//		double A = cReq(new cQuery(dep.x, 0));
//		double B = cReq(new cQuery(dep.x, 1));
//		
//		for (cQuery cq : GenTool.mrg2cq(this, dep.p)){
//			
//			cq.put(dep.x, 0);
//			double a = cReq(new cQuery(cq));
//			cq.put(dep.x, 1);
//			double b = cReq(new cQuery(cq));
//			
//	
//			if (a > 0){
//				ans += (a / num) * GenTool.log2(num * a / (A * (a + b)));
//			}
//			
//			if (b > 0){
//				ans += (b / num) * GenTool.log2(num * b / (B * (a + b)));
//			}
//		}
//		
//		iCache.put(dep, ans);
//		return ans;
//	}
*/
	
	private Double iAns(Dependence dep) {
	// TODO Auto-generated method stub
	double ans = 0.0;

	for (cQuery cq : QueryGenerator.mrg2cq(this, dep.p)) {
		for (int i = 0; i < domain.getCell(dep.x); i++) {
			cQuery joint = new cQuery(cq);
			joint.put(dep.x, i);
			double a = cReq(joint);

			if (a > 0) {
				ans += (a / num) * GenTool.log2(num * a / (cReq(cq) * cReq(new cQuery(dep.x, i))));
			}
		}
	}

	iCache.put(dep, ans);
	return ans;
}
	
	
	//Evaluation
	
	public double pError(CountingQuery cmp, HashSet<cQuery> cq){
		double pErr = 0.0;
		cQuery sum = new cQuery();
	
		for (cQuery q : cq){
			pErr += Math.abs(this.cReq(q)/this.cReq(sum) - cmp.cReq(q)/cmp.cReq(sum));
		}
		
		return pErr/cq.size();
	}
	
	public double cError(CountingQuery cmp, HashSet<cQuery> cq){
		double cErr = 0.0;
	
		for (cQuery q : cq){
			cErr += Math.abs(this.cReq(q) - cmp.cReq(q));
		}
		
		return cErr/cq.size();
	}
	
	public double c2Error(CountingQuery cmp, HashSet<cQuery> cq) {
		// TODO Auto-generated method stub
		double cErr = 0.0;
		
		for (cQuery q : cq){
			double err = this.cReq(q) - cmp.cReq(q);
			cErr += err * err;
		}
		
		return cErr/cq.size();
	}
	
	public double mError(CountingQuery cmp, Marginal mrg, PrintStream outFile) {
		// TODO Auto-generated method stub
		double mErr = 0.0;
		HashSet<cQuery> cQs = QueryGenerator.mrg2cq(this, mrg);
		
		double[] aArr = new double[cQs.size()];
		double[] bArr = new double[cQs.size()];
		double sum = 0.0;
		int count = 0;
		
		for (cQuery cq : cQs) {
			aArr[count] = this.cReq(cq);
			bArr[count] = cmp.cReq(cq);
			sum += bArr[count];
			count++;
		}
		
		if (sum == 0.0) {
			for (int i = 0; i<count; i++)
				bArr[i] = (double) num/count;
			sum = num;
		}
		
		for (int i = 0; i<count; i++) {
			mErr += Math.abs(aArr[i] - bArr[i]/sum * num);
		}
		
		outFile.println(mErr + "\t" +count);
		return mErr;
	}

	public double tError(ContingencyTable cmp){
		
		HashMap<String, Double> aTable = this.getTable();
		HashMap<String, Double> bTable = cmp.getTable();
		
		double tErr = 0.0;
		
		HashSet<String> nonZero = new HashSet<String>(aTable.keySet());
		nonZero.addAll(bTable.keySet());
		
		for (String s : nonZero) {
			Double a = aTable.get(s);
			Double b = bTable.get(s);
			
			if (a == null) a = 0.0;
			if (b == null) b = 0.0;
			
			tErr += Math.abs(a - b);
		}
		
		return tErr;
	}

	public double kNN(Random rng, Data test, int topk, int yPos) {
		
		double error = 0.0;
		for (int t = 0; t < test.getNum(); t++) {
			ArrayList<Sortable<Integer>> dist = new ArrayList<Sortable<Integer>>(num);
			for (int i = 0; i<num; i++) {
				dist.add(new Sortable<Integer>(i, GenTool.hamming(test.getEntry(t), entries[i], yPos)));
			}
			Collections.shuffle(dist, rng);
			Collections.sort(dist);
			
			int sol = 0;
			for (int i = 0; i<topk; i++) {
				sol += entries[dist.get(i).key][yPos];
			}
			sol = 2*sol/(topk+1);		// 0-1 majority
			
			if (test.getEntry(t)[yPos] != sol) error++;
		}
		
		return error/test.getNum();
	}
	
	
	//Properties
	
	public Domain getDomain() {
		return domain;
	}
	
	public int getDim() {
		// TODO Auto-generated method stub
		return domain.getDim();
	}

	public int getNum() {
		// TODO Auto-generated method stub
		return num;
	}

	public int[] getCell() {
		// TODO Auto-generated method stub
		return domain.getCell();
	}

	public int getCell(int pos) {
		// TODO Auto-generated method stub
		return domain.getCell(pos);
	}
		
	public HashMap<String, Double> getTable() {
		// TODO Auto-generated method stub
		return global;
	}
	
	public int[] getEntry(int row) {
		// TODO Auto-generated method stub
		return entries[row];
	}

	public double jointEntropy() {
		double entropy = 0.0;
		
		for (double gC : global.values()){
			entropy += (gC / num) * GenTool.log2(num / gC);
		}
		
		return entropy;
	}

	public double sigmaEntropy() {
		double entropy = 0.0;
		
		for (int i = 0; i<domain.getDim(); i++){
			HashSet<Integer> oneWay = new HashSet<Integer>();
			oneWay.add(i);
			
			for (cQuery cq : QueryGenerator.mrg2cq(this, oneWay)){
				double a = cReq(cq);
				if (a > 0) {
					entropy += (a / num) * GenTool.log2(num / a);
				}
			}
		}
		
		return entropy;
	}

	public void getCacheHash() {
		System.out.println(cCache.hashCode());
		System.out.println(l1Cache.hashCode());
	}
	
	
	// I/O
	
	public void printo_int(String outName, String separator) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
		int dim = domain.getDim();
		
		for (int i = 0; i<num; i++){
			for (int j = 0; j < dim - 1; j++) {
				outFile.print(entries[i][j] + separator);
			}
			outFile.println(entries[i][dim - 1]);
		}
		
		outFile.close();
	}
	
	public void printo_data(String outName, String separator) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
		int dim = domain.getDim();
		
		outFile.println(num);
		
		for (int i = 0; i<num; i++){
			for (int j = 0; j < dim - 1; j++) {
				outFile.print(domain.int2str(entries[i][j], j) + separator);
			}
			outFile.println(domain.int2str(entries[i][dim - 1], dim - 1));
		}
		
		outFile.close();
	}
	
	public void printo_c45(String outName, int label, HashSet<Integer> thres) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
		
		for (int i = 0; i<num; i++){
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)
					outFile.print(entries[i][j] + ", ");
			}
			
			if (thres.contains(entries[i][label]))
				outFile.println("1");
			else 
				outFile.println("0");
		}
		
		outFile.close();
	}
	
	public void printo_libsvm(String outName, int label, double thres) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
	
		for (int i = 0; i<num; i++) {
			if (entries[i][label] > thres)
				outFile.print("+1");
			else 
				outFile.print("-1");
			
			int[] index = {1};
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)	{
					outFile.print(" " + domain.int2libsvm(entries[i][j], j, index));
				}
			}
			outFile.println();
		}
		
		outFile.close();
	}
	
	public void printo_libsvm(String outName, int label, HashSet<Integer> thres) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
		
		for (int i = 0; i<num; i++) {
			if (thres.contains(entries[i][label]))
				outFile.print("+1");
			else 
				outFile.print("-1");
			
			int[] index = {1};
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)	{
					outFile.print(" " + domain.int2libsvm(entries[i][j], j, index));
				}
			}
			outFile.println();
		}
		
		outFile.close();
	}
	
/*	public void printo_libsvm(String outName, int label) throws Exception {
//		// TODO Auto-generated method stub
//		PrintStream outFile = new PrintStream(new File(outName));
//	
//		for (int i = 0; i<num; i++) {
//			outFile.print(entries[i][label]);
//			int pos = 1;
//		
//			for (int j = 0; j < domain.getDim(); j++) {
//				if (j != label)	{
//					if (domain.isCategorical(j)) {
//						outFile.print(" " + (pos + entries[i][j]) + ":" + 1);
//						pos += domain.getCell(j);
//					}
//					else {
//						outFile.print(" " + pos + ":" + (entries[i][j] + 0.5) / domain.getCell(j));		//centering & [0, 1]
//						pos ++;
//					}
//				}
//			}
//			outFile.println();
//		}
//		
//		outFile.close();
//	}
*/
	
/*	public void printo_svm(String outName, int label) throws Exception {
//		// TODO Auto-generated method stub
//		PrintStream outFile = new PrintStream(new File(outName));
//		
//		for (int i = 0; i<num; i++) {		
//			for (int j = 0; j < domain.getDim(); j++) {
//				if (j != label)	{
//					
//					if (domain.isCategorical(j)) {
//						for (int pos = 0; pos < domain.getCell(j); pos++) {
//							if (pos == entries[i][j]) 
//								outFile.print("1\t");
//							else
//								outFile.print("0\t");
//						}
//					}
//					else {
//						outFile.print((entries[i][j] + 0.5) / domain.getCell(j) + "\t");				//centering & [0, 1]
//					}
//					
//				}
//			}
//			outFile.println(entries[i][label]);
//		}
//
//		outFile.close();
//	}
*/

	public void writeCache(HashSet<cQuery> cqs, String string) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(string));
		
		int ncq = cqs.size();
		int count = 0;
		
		for (cQuery cq : cqs){
			for (int key : cq.keySet()){
				outFile.print(key + "\t" + cq.get(key) + "\t");
			}
			outFile.println(cReq(cq));
			count++;
			System.out.println((double) count/ncq);
		}
		
		outFile.close();
	}

	public void loadCache(String string) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader inFile = new BufferedReader(new FileReader(string));
		
		String s = inFile.readLine();
		while (s != null){
			String[] tokens = s.split("\\s+");
			int l = tokens.length;
			cQuery cq = new cQuery();
			
			for (int i = 0; i<l/2; i++){
				cq.put(Integer.parseInt(tokens[2*i]), Integer.parseInt(tokens[2*i+1]));
			}
			
			cCache.put(cq, Double.parseDouble(tokens[l-1]));
			s = inFile.readLine();
		}
		
		inFile.close();
	}



}
