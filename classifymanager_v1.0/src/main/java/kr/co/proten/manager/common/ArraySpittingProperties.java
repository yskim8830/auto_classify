package kr.co.proten.manager.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ArraySpittingProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String keys = "";
	List<Object> list = new ArrayList<Object>();
	
	@Override
	public synchronized Object put(Object key, Object value){
		String skey = (String)key;
		if(skey.indexOf(keys) > -1)
			list.add(value);	
		return super.put(key, value);
	}
	
	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public <T> T[] getArray(T[] a){
		return list.toArray(a);
	}
}
