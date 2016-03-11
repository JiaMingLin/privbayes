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

/**
 * description of attributes
 *
 */
public class Domain {
	
	// a map of attributes with @Translator.
	private HashMap<Integer, Translator> trans;
	
	// the dimension of data.
	private int dim;
	
	private boolean isBinary;
	
	// the array of bin(grid) numbers of a(an) column(attribute).
	private int[] cells;
	
	private DomainConvertor convertor;
		
	public Domain(String dataDomain) throws Exception {
		trans = new HashMap<Integer, Translator>();
		
		// read the "*.domain" from file system. 
		BufferedReader domainFile = new BufferedReader(new FileReader(dataDomain));
		
		// specifies the dimension of data.
		dim = Integer.parseInt(domainFile.readLine());
		cells = new int[dim];
		isBinary = false;
		
		// for each attributes.
		for (int d = 0; d < dim; d++) {
			
			// read each line in "*.domain" file, and splitting by space. 
			String[] tokens = domainFile.readLine().split("\\s+");
			
			// the first token defines the attribute type, continuous "C" or discrete "D".
			if (tokens[0].equals("C")) {
				
				// the last integer specified in "*.domain" file, means splitting into numbers(grid) of bins.
				int grid = Integer.parseInt(tokens[3]);
				
				// cache the number of bins of a column.
				cells[d] = grid;
				
				// tokens[1]: max in this column.
				// tokens[2]: min in this column.
				// Translator: translate the real value to bin index.
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
		 
		return cells;
	}
	
	public int getCell(int pos) {
		 
		return cells[pos];
	}

	
	//Functions

	/**
	 * @param str: a row in the source data.
	 * @return: the array of indexes of bin(grid).
	 */
	public int[] str2int(String[] str) {
		int[] arr = new int[dim];
		for (int i = 0; i<dim; i++){
			// for an attribute, map the original value to a bin(grid)
			// arr[i] is the index of bin(grid).
			arr[i] = str2int(str[i], i);
		}
		return arr;
	}
	
	/**
	 * @param key: the instance of an attribute, ex: height = 170.
	 * @param pos: the index of an attribute.
	 * @return: the index of bin(grid).
	 */
	public int str2int(String key, int pos) {
		
		return trans.get(pos).str2int(key);
	}
	
	/**
	 * Translate the coarse data to readable data.
	 * @param key: the value to be translated.
	 * @param pos: index of attribute
	 * @return
	 */
	public String int2str(int key, int pos) {
		 
		return trans.get(pos).int2str(key);
	}
	
	public String int2libsvm(int key, int pos, int[] index) {
		return trans.get(pos).int2libsvm(key, index);
	}
	
	public String src2libsvm(String key, int pos, int[] index) {
		 
		return trans.get(pos).src2libsvm(key, index);
	}
	
	public String src2gsvm(String key, int pos) {
		 
		return trans.get(pos).src2gsvm(key);
	}
	
/*	public String[] int2str(int[] arr) {
//		 
//		String[] str = new String[dim];
//		for (int i = 0; i<dim; i++){
//			str[i] = trans.get(i).revTranslate(arr[i]);
//		}
//		return str;
//	}
*/

	public Data binarization(Data gData) {
		 
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryData(gData);
	}
	
	public cQuery qBinarization(cQuery gCq) {
		 
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryQuery(gCq);
	}
	
	public HashSet<cQuery> qBinarization(HashSet<cQuery> gCq) {
		 
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryQuery(gCq);
	}
	
	public HashMap<cQuery, cQuery> qBinarizationMap(HashSet<cQuery> gCq) {
		 
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.qBinarizationMap(gCq);
	}
	
	
	public HashSet<Marginal> mBinarization(HashSet<Marginal> gMrg) {
		 
		if (convertor == null) {
			convertor = new DomainConvertor(this);
		}
		
		return convertor.binaryMarginal(gMrg);
	}

	private int[] generalization(int[] bTuple) {
		 
		return convertor.generalData(bTuple);
	}
	
	public Data generalization(Data bData) {

		return convertor.generalData(bData);
	}
	
	public boolean tupleCheck(int[] tuple) {
		 
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
		 
		// if (cells[pos] == 2) return true;
		return trans.get(pos) instanceof dTranslator;
	}


}
