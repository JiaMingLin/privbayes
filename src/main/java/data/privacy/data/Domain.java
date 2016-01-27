package data.privacy.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import data.privacy.query.Marginal;
import data.privacy.query.cQuery;

import data.privacy.translation.Translator;
import data.privacy.translation.cTranslator;
import data.privacy.translation.dTranslator;

public class Domain {
	
	private HashMap<Integer, Translator> trans;
	private DomainConvertor convertor;
	private int dim;
	private boolean isBinary;
	private int[] cells;
	
	
	public Domain(String dataDomain) throws Exception {
		// TODO Auto-generated method stub
		trans = new HashMap<Integer, Translator>();
		BufferedReader domainFile = new BufferedReader(new FileReader(dataDomain));
		
		dim = Integer.parseInt(domainFile.readLine());
		cells = new int[dim];
		isBinary = false;
		
		for (int d = 0; d < dim; d++) {
			
			// read each line in "*.domain" file, and splitting by space. 
			String[] tokens = domainFile.readLine().split("\\s+");
			
			// the first token defines the attribute type, continuous "C" or discrete "D".
			if (tokens[0].equals("C")) {
				
				// the last integer specified in "*.domain" file when type is "C", which means continuous.
				int grid = Integer.parseInt(tokens[3]);
				
				cells[d] = grid;
				trans.put(d, new cTranslator(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), grid));
			}
			else {
				dTranslator dt = new dTranslator();	
				for (int i = 1; i < tokens.length; i++) {
					dt.insert(tokens[i]);
				}
				
				// the number of categories of discrete attribute.
				// for a discrete attribute, the first token defines type, the remaining specifies categories. 
				cells[d] = tokens.length - 1;
				trans.put(d, dt);
			}
		}
		
		domainFile.close();
	}
	
	public Domain(int[] cells1, DomainConvertor convertor1) {
		convertor = convertor1;
		cells = cells1;
		dim = cells.length;
		isBinary = true;
	}
	
	//Properties
	
	public int getDim() {
		return dim;
	}
	
	public boolean isBinary() {
		return isBinary;
	}
	
	public int[] getCell() {
		// TODO Auto-generated method stub
		return cells;
	}
	
	public int getCell(int pos) {
		// TODO Auto-generated method stub
		return cells[pos];
	}

	
	//Functions

	public int[] str2int(String[] str) {
		// TODO Auto-generated method stub
		int[] arr = new int[dim];
		for (int i = 0; i<dim; i++){
			arr[i] = str2int(str[i], i);
		}
		return arr;
	}
	
	public int str2int(String key, int pos) {
		// TODO Auto-generated method stub
		return trans.get(pos).str2int(key);
	}
	
	public String int2str(int key, int pos) {
		// TODO Auto-generated method stub
		return trans.get(pos).int2str(key);
	}
	
	public String int2libsvm(int key, int pos, int[] index) {
		return trans.get(pos).int2libsvm(key, index);
	}
	
	public String src2libsvm(String key, int pos, int[] index) {
		// TODO Auto-generated method stub
		return trans.get(pos).src2libsvm(key, index);
	}
	
	public String src2gsvm(String key, int pos) {
		// TODO Auto-generated method stub
		return trans.get(pos).src2gsvm(key);
	}
	
/*	public String[] int2str(int[] arr) {
//		// TODO Auto-generated method stub
//		String[] str = new String[dim];
//		for (int i = 0; i<dim; i++){
//			str[i] = trans.get(i).revTranslate(arr[i]);
//		}
//		return str;
//	}
*/

	public Data binarization(Data gData) {
		// TODO Auto-generated method stub
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryData(gData);
	}
	
	public cQuery qBinarization(cQuery gCq) {
		// TODO Auto-generated method stub
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryQuery(gCq);
	}
	
	public HashSet<cQuery> qBinarization(HashSet<cQuery> gCq) {
		// TODO Auto-generated method stub
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryQuery(gCq);
	}
	
	public HashMap<cQuery, cQuery> qBinarizationMap(HashSet<cQuery> gCq) {
		// TODO Auto-generated method stub
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.qBinarizationMap(gCq);
	}
	
	
	public HashSet<Marginal> mBinarization(HashSet<Marginal> gMrg) {
		// TODO Auto-generated method stub
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryMarginal(gMrg);
	}

	private int[] generalization(int[] bTuple) {
		// TODO Auto-generated method stub
		return convertor.generalData(bTuple);
	}
	
	public Data generalization(Data bData) {
		// TODO Auto-generated method stub
		return convertor.generalData(bData);
	}
	
	public boolean tupleCheck(int[] tuple) {
		// TODO Auto-generated method stub
		if (isBinary) {
			return convertor.tupleCheck(generalization(tuple));
		}
		else {
			for (int d = 0; d < dim; d++) {
				if (tuple[d] >= cells[d]) return false;
			}
			
			return true;
		}
	}
	
	public ArrayList<HashSet<Integer>> relatedAttr() {
		return convertor.relatedAttr();
	}

	public boolean isCategorical(int pos) {
		// TODO Auto-generated method stub
		// if (cells[pos] == 2) return true;
		return trans.get(pos) instanceof dTranslator;
	}


}
