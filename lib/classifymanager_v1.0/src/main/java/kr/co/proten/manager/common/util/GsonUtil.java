package kr.co.proten.manager.common.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class GsonUtil {

//	static Gson gson = new Gson();

	static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new DoubleSerializer()).setPrettyPrinting().create();

	private static class DoubleSerializer implements JsonSerializer<Double> {
		@Override
		public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
			return src == src.longValue() ? new JsonPrimitive(src.intValue()) : new JsonPrimitive(src);
		}
	}

	public static String toString(Object src){
		 return gson.toJson(src);
	}

	public static List<HashMap<String, Object>> toListHashMap(String jsonAsString){
		 List<HashMap<String, Object>> myList = gson.fromJson(jsonAsString,
			    new TypeToken<List<HashMap<String, Object>>>(){}.getType());
		return myList;
	}

	public static List<Map<String, Object>> toListLikedHashMap(String jsonAsString){
		List<Map<String, Object>> myList = gson.fromJson(jsonAsString,
				new TypeToken<List<LinkedHashMap<String, Object>>>(){}.getType());
		return myList;
	}

	public static HashSet<String> toHashSet(String jsonAsString){
		HashSet<String> set = new Gson().fromJson(jsonAsString, 
			    new TypeToken<HashSet<String>>(){}.getType());
		
		return set;
	}
	
	public static HashMap<String, Object> toHashMap(String jsonAsString){
		 HashMap<String, Object> map = new Gson().fromJson(jsonAsString, 
			    new TypeToken<HashMap<String, Object>>(){}.getType());
		return map;
	}

	public static Map<String, Object> toMap(String jsonAsString){
		 Map<String, Object> map = new Gson().fromJson(jsonAsString, 
			    new TypeToken<HashMap<String, Object>>(){}.getType());
		return map;
	}
}
