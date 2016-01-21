package data.privacy.experiments.multiy;

import java.util.HashSet;

import data.privacy.data.DataPrinter;
import data.privacy.data.Domain;

public class TestDataConvertor {
	public static void main(String[] args) throws Exception {
		int d = Integer.parseInt(args[0]);
		int model = Integer.parseInt(args[1]);
		
		//model setting
		int yPos = 0;
		HashSet<Integer> ySet = new HashSet<Integer>();
		if (d == 2) {
			if (model == 1) {
				// Salary: if salary > 50K
				yPos = 14;
				ySet.add(1);
			}
			else if (model == 2) {
				// Sex: if is male
				yPos = 9;
				ySet.add(1);
			}
			else if (model == 3) {
				// Education: if holds a post-secondary degree
				yPos = 3;
				ySet.add(9);
				ySet.add(10);
				ySet.add(11);
				ySet.add(12);
				ySet.add(13);
				ySet.add(14);
				ySet.add(15);
			}
			else if (model == 4) {
				// Martial-staus: if never-married
				yPos = 5;
				ySet.add(4);
			}
			else {
				System.err.println("Model selection error!");
				System.exit(1);
			}
		}
		else if (d == 4) {
			if (model == 1) {
				// Y12: if disabled on getting about outside
				yPos = 15;
				ySet.add(1);
			}
			else if (model == 2) {
				// Y14: if disabled on managing money
				yPos = 2;
				ySet.add(1);
			}
			else if (model == 3) {
				// Y6: if disabled on getting to the bathroom or using toilet
				yPos = 10;
				ySet.add(1);
			}
			else if (model == 4) {
				// Y4: if disabled on dressing
				yPos = 12;
				ySet.add(1);
			}
			else if (model == 5) {
				// Y13: if disabled on traveling
				yPos = 3;
				ySet.add(1);
			}
			else if (model == 6) {
				// Y8: if disabled on doing light housework
				yPos = 8;
				ySet.add(1);
			}
			else {
				System.err.println("Model selection error!");
				System.exit(1);
			}
		}
		else {
			System.err.println("Dataset selection error!");
			System.exit(1);
		}
		//end of model setting
		
		
		Domain domain = new Domain("Data"+d+".domain");
		new DataPrinter("Test"+d+".dat", domain).printo_libsvm("d"+d+"model"+model+".test", yPos, ySet);
	}
}
