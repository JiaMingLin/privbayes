package data.privacy.tools;

public class UniSearch {
	public UniSearch(int a){
		size = a;
		father = new int[size];
		rank = new int[size];
		
		for (int i = 0; i<size; i++){
			father[i] = i;
			rank[i] = 0;
		}
	}
	
	public int getFather(int v){
		if (father[v] == v){
			return v;
		}
		else
		{
			father[v] = getFather(father[v]);
			return father[v];
		}
	}
	
	public boolean join(int v, int u){
		int fv = getFather(v);
		int fu = getFather(u);
		
		if (fv == fu) return false;
		
		if (rank[fv] > rank[fu])
			father[fu] = fv;
		else if (rank[fv] < rank[fu])
			father[fv] = fu;
		else{
			father[fv] = fu;
			rank[fu]++;
		}
		
		return true;
	}
	
	private int[] father, rank;
	private int size;
}
