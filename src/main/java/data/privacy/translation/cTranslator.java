package data.privacy.translation;

public class cTranslator implements Translator{
	private double min, step, grid;
	
	public cTranslator(double min1, double max1, int grid1){
		min = min1;
		// length of interval
		step = (max1-min1)/grid1;
		grid = grid1;
	}
	
	public int str2int(String key){
		return (int) Math.floor( (Double.parseDouble(key) - min) / (step + 1e-4) );
	}
	
	public String int2str(int key){
		return Double.toString( (key + 0.5) * step + min );
	}

	@Override
	public String int2libsvm(int key, int[] index) {
		// TODO Auto-generated method stub
		return index[0]++ + ":" + Double.toString((key + 0.5) / grid);			//centering & [0, 1]
	}

	@Override
	public String src2libsvm(String key, int[] index) {
		// TODO Auto-generated method stub
		double max = min + step * grid;
		return index[0]++ + ":" + (Double.parseDouble(key) - min) / (max - min);			//[0, 1]
	}

	@Override
	public String src2gsvm(String key) {
		// TODO Auto-generated method stub
		double max = min + step * grid;
		return Double.toString((Double.parseDouble(key) - min) / (max - min));				//[0, 1]
	}
}
