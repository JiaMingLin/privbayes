package data.privacy.translation;


/**
 * Translator between the readable value and index of bin(grid). 
 *
 */
public interface Translator {
	public int str2int(String key);
	public String int2str(int key);
	public String int2libsvm(int key, int[] index);
	public String src2libsvm(String key, int[] index);
	public String src2gsvm(String key);
}