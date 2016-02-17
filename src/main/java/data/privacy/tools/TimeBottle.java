package data.privacy.tools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TimeBottle {

	private static TimeBottle singleton = null;
	private static Map<String, List<Integer>> bucket = null; 
	
	static{
		if(singleton == null){
			bucket = new HashMap<String, List<Integer>>();
		}
	}
	
	private TimeBottle(){
		
	}
	
	public static void saveTime(String funcName, int time){
		List<Integer> timeList = null;
		if(bucket.keySet().contains(funcName)){
			timeList = bucket.get(funcName);						
		}else{
			timeList = new LinkedList<Integer>();					
		}
		timeList.add(time);
		bucket.put(funcName, timeList);
	}
	
	public static void showAverage(){
		
		for(String key : bucket.keySet()){
			System.out.format("Function Name: %s, Spends %s \n", key, average(bucket.get(key)));
		}
	}
	
	private static Double average(List<Integer> arr){
		int sum = 0;
		for(Integer num : arr){
			sum += num;
		}
		return (double)sum / arr.size();
	}
}
