package data.privacy.translation;

import java.util.HashMap;

public class dTranslator implements Translator{
	private HashMap<String, Integer> s2i;
	private HashMap<Integer, String> i2s;
	
	public dTranslator(){
		s2i = new HashMap<String, Integer>();
		i2s = new HashMap<Integer, String>();
	}
	
	public void insert(String key) {
		// TODO Auto-generated method stub
		Integer v = s2i.get(key);
		if (v == null){
			v = s2i.size();
			s2i.put(key, v);
			i2s.put(v, key);
		}
	}
	
	public int str2int(String key){	
		return s2i.get(key);
	}
	
	public String int2str(int key){
		return i2s.get(key);
	}

	@Override
	public String int2libsvm(int key, int[] index) {
		// TODO Auto-generated method stub
		String str = (index[0] + key) + ":1";
		index[0] += s2i.size();
		return str;
	}

	@Override
	public String src2libsvm(String key, int[] index) {
		// TODO Auto-generated method stub
		String str = (index[0] + s2i.get(key)) + ":1";
		index[0] += s2i.size();
		return str;
	}

	@Override
	public String src2gsvm(String key) {
		// TODO Auto-generated method stub
		String str = "";
		int pos = s2i.get(key);
		
		for (int i = 0; i < s2i.size(); i++) {
			if (pos == i) 
				str += "1 ";
			else
				str += "0 ";
		}
		return str.substring(0, str.length() - 1);
	}
}
