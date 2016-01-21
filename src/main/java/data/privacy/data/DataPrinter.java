package data.privacy.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashSet;

public class DataPrinter {
	
	private String[][] entries;
	private int num;
	private Domain domain;
	
	public DataPrinter(String dataFileName, Domain domain1) throws Exception {
		
		BufferedReader dataFile = new BufferedReader(new FileReader(dataFileName));
		
		domain = domain1;
		num = Integer.parseInt(dataFile.readLine());
		entries = new String[num][domain.getDim()];
	
		for (int s = 0; s < num; s++){
			entries[s] = dataFile.readLine().split("\\s+");
		}
		dataFile.close();
	}

	public void printo_libsvm(String outName, int label, double thres) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
	
		for (int i = 0; i<num; i++) {
			//outFile.print(domain.str2int(entries[i][label], label));
			if (domain.str2int(entries[i][label], label) > thres)
				outFile.print("+1");
			else 
				outFile.print("-1");
			
			int[] index = {1};
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)	{
					outFile.print(" " + domain.src2libsvm(entries[i][j], j, index));
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
			//outFile.print(domain.str2int(entries[i][label], label));
			if (thres.contains(domain.str2int(entries[i][label], label)))
				outFile.print("+1");
			else 
				outFile.print("-1");
			
			int[] index = {1};
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)	{
					outFile.print(" " + domain.src2libsvm(entries[i][j], j, index));
				}
			}
			outFile.println();
		}
		
		outFile.close();
	}

	public void printo_gsvm(String outName, int label, double thres) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
		
		for (int i = 0; i<num; i++) {
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)	{
					outFile.print(domain.src2gsvm(entries[i][j], j) + " ");
				}
			}
			
			//outFile.println(domain.str2int(entries[i][label], label));
			if (domain.str2int(entries[i][label], label) > thres)
				outFile.println("1");
			else 
				outFile.println("0");
		}
		
		outFile.close();
	}

	public void printo_gsvm(String outName, int label, HashSet<Integer> thres) throws Exception {
		// TODO Auto-generated method stub
		PrintStream outFile = new PrintStream(new File(outName));
		
		for (int i = 0; i<num; i++) {
			for (int j = 0; j < domain.getDim(); j++) {
				if (j != label)	{
					outFile.print(domain.src2gsvm(entries[i][j], j) + " ");
				}
			}
			
			//outFile.println(domain.str2int(entries[i][label], label));
			if (thres.contains(domain.str2int(entries[i][label], label)))
				outFile.println("1");
			else 
				outFile.println("0");
		}
		
		outFile.close();
	}
}
