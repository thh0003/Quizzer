package quizzer;

import java.util.HashMap;
import java.util.TreeSet;
/*
 * 
 *  Credit for portions of this command line argument parser go to Jakob Jenkov and his 
 * 	https://github.com/jjenkov/cli-args
 * 
 * 
 * */
public class CliParser {

	private String[] args = null;
	private HashMap<String, Integer> switchIndexes = new HashMap<String, Integer>();
	private TreeSet<Integer>         takenIndexes  = new TreeSet<Integer>();

//	private List<String> targets = new ArrayList<String>();

	public CliParser(String[] args){
	   parse(args);
	}

	public void parse(String[] arguments){
		this.args = arguments;
		     //locate switches.
		switchIndexes.clear();
		takenIndexes.clear();
		for(int i=0; i < args.length; i++) {
			if(args[i].startsWith("-") ){
				switchIndexes.put(args[i], i);
				takenIndexes.add(i);
			}
		}
	}
	
	public String[] args() {
		return args;
	}
	
	public String arg(int index){
		return args[index];
	}
	
	public boolean switchPresent(String switchName) {
		return switchIndexes.containsKey(switchName);
	}
	
	public String switchValue(String switchName) {
	    return switchValue(switchName, null);
	}
	
	public String switchValue(String switchName, String defaultValue) {
		if(!switchIndexes.containsKey(switchName)) return defaultValue;
	    
		int switchIndex = switchIndexes.get(switchName);
		if(switchIndex + 1 < args.length){
			takenIndexes.add(switchIndex +1);
			return args[switchIndex +1];
		}
		return defaultValue;
	}
	
	public Long switchLongValue(String switchName) {
		return switchLongValue(switchName, null);
	}
	
	public Long switchLongValue(String switchName, Long defaultValue) {
		String switchValue = switchValue(switchName, null);
		
		if(switchValue == null) return defaultValue;
		return Long.parseLong(switchValue);
	}
	
	public Double switchDoubleValue(String switchName) {
		return switchDoubleValue(switchName, null);
	}
	
	public Double switchDoubleValue(String switchName, Double defaultValue) {
		String switchValue = switchValue(switchName, null);
	    
		if(switchValue == null) return defaultValue;
		return Double.parseDouble(switchValue);
	}
	
	
	public String[] switchValues(String switchName) {
		if(!switchIndexes.containsKey(switchName)) return new String[0];
	
		int switchIndex = switchIndexes.get(switchName);
	    
		int nextArgIndex = switchIndex + 1;
		while(nextArgIndex < args.length && !args[nextArgIndex].startsWith("-")){
			takenIndexes.add(nextArgIndex);
			nextArgIndex++;
		}
	
		String[] values = new String[nextArgIndex - switchIndex - 1];
		for(int j=0; j < values.length; j++){
			values[j] = args[switchIndex + j + 1];
	    }
		return values;
	}
	
	  
}
