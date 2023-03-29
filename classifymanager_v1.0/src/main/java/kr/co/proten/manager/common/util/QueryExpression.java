package kr.co.proten.manager.common.util;  
 

public class QueryExpression{
	public static final char AND = '+';
	public static final char OR  = '|';
	public static final char NOT = '-';
	public static final String MATCH = "=";
	public static final String NOMATCH = "-";
	public static final String IN = ":"; 
	public static final String GTE = ">=";
	public static final String LTE = "<=";
	public static final String GT = ">";
	public static final String LT = "<";
	
	public static final String OPERATOR[]=new String[] {GTE,LTE,GT,LT,MATCH,NOMATCH,IN};
	
	

}
