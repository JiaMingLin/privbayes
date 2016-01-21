package data.privacy.tools;

import java.util.ArrayList;
import java.util.HashSet;

public class DAG {
	private ArrayList<Dependence> dag;
	
	public DAG() {
		dag = new ArrayList<Dependence>();
	}
	
	public void put(int x, HashSet<Integer> p) {
		dag.add(new Dependence(x, p));
	}
	
	public void put(Dependence dep) {
		dag.add(dep);
	}
	
	public Dependence get(int i) {
		return dag.get(i);
	}
	
	public String toString() {
		return dag.toString();
	}
}
