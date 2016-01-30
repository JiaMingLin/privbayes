package data.privacy.experiments.multiy;

import java.util.HashSet;

import data.privacy.tools.GenTool;

public class TestArea {

	public static void main(String[] args) {
		
		HashSet<Integer> setInt = new HashSet<Integer>();
		for(int i=1 ;i<8 ;i++){
			setInt.add(i);

		}
		HashSet<HashSet<Integer>> output = GenTool.kSub(setInt, 4);
		System.out.println(output.size());								
	}

}
