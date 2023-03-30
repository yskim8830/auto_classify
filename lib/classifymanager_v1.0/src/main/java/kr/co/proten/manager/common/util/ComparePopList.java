package kr.co.proten.manager.common.util;

import java.util.Comparator;
import java.util.HashMap;

public class ComparePopList implements Comparator<HashMap<String, String>> {
 
    private final String key;
    private final String key1;
    public ComparePopList(String key,String key1) {
        this.key = key;
        this.key1 = key1;
    }
    
    
    
    @Override
    public int compare(HashMap<String, String> first, HashMap<String, String> second) {
    	int fInt = 0;
    	int sInt = 0;
    	
    	fInt = Integer.parseInt(first.get(key));
    	sInt = Integer.parseInt(second.get(key));
    	
    	if(fInt==sInt){
    		fInt = Integer.parseInt(first.get(key1));
        	sInt = Integer.parseInt(second.get(key1));
           // System.out.println(fInt+"/"+sInt+"="+Integer.compare( sInt,fInt));
            return Integer.compare( sInt,fInt);
    	}else{
    		return Integer.compare( sInt,fInt);
    	} 
    	
    }
}
 