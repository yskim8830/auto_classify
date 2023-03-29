package kr.co.proten.manager.common.util;

import java.util.Comparator;
import java.util.HashMap;

public class CompareHotList implements Comparator<HashMap<String, String>> {
 
    private final String key;
    
    public CompareHotList(String key) {
        this.key = key;
    }
    
    @Override
    public int compare(HashMap<String, String> first, HashMap<String, String> second) {
    	int fInt = 0;
    	int sInt = 0;
    	fInt = Integer.parseInt(first.get(key));
    	sInt = Integer.parseInt(second.get(key));
       // System.out.println(fInt+"/"+sInt+"="+Integer.compare( sInt,fInt));
        return Integer.compare( sInt,fInt);
    }
}
 