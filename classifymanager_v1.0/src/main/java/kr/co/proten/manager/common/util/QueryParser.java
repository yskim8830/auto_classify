package kr.co.proten.manager.common.util;  

import org.elasticsearch.index.query.*;

import java.util.*;

 
public class QueryParser {
		 
	    private String input;
	    public QueryParser() {
	    }
	   
	    public QueryParser(String input) {
	         //= input.trim(); // mark the end
	    	getQueryFilter(input);
	    }
	    
	    
	    public QueryBuilder getQueryFilter(String input) {
	    	this.input = input;
	    	 
	    	ArrayList<Object> list = setList() ;
	    	//System.out.println(list.toString());
	    	QueryBuilder queryBuilder =getChildList(list);
	    	return queryBuilder;
	    }
	    
	    public QueryBuilder getChildList(Object obj ) {
	    	
	    	BoolQueryBuilder qbuilder = new BoolQueryBuilder();
	    	
	    	String operator = "";
	    	if(obj instanceof  ArrayList) {
	    		ArrayList<Object> list = (ArrayList<Object>)obj;
	    		//System.out.println("list.size"+list.size());
	    		if(list.size()==0) {
	    			return qbuilder;
	    		}
	    		
	    		qbuilder.must(getChildList(list.get(0)));
	    		for(int idx =1 ; idx < list.size() ; idx=idx+2) {
	    			
	    			if(idx+1<list.size()) {
	    				QueryBuilder first = getChildList( list.get(idx+1));
	    				operator =  list.get(idx).toString();
	    				if("+".equals(operator)) {
	    					qbuilder.must(first);
	    				}else if("-".equals(operator)) {
	    					qbuilder.mustNot(first);
	    				}else if("|".equals(operator)) {
	    					qbuilder.should(first);
	    				}else {
	    					//System.out.println("Error operator :" + operator);
	    				}
	    			}
	    		}
	    		return qbuilder;
	    	}else {
	    		return getFilterString(obj.toString()) ;
	    	} 
	    }
	    
	    
	    
	    
	    public QueryBuilder getFilterString(String filterQuery) {
	    	
	    	StringBuilder data = new StringBuilder();
	    	filterQuery = filterQuery.trim();
	    	String operator[]=QueryExpression.OPERATOR;
	    	for(String oper : operator) {
	    		int idxOper = filterQuery.indexOf(oper);
	    		
	    		if(idxOper>-1) {
	    			if(filterQuery.charAt(idxOper-1)=='\\') {
	    				idxOper = filterQuery.indexOf(oper);
	    				if(idxOper==-1) {
	    					continue;
	    				}
	    			}
	    			String key = filterQuery.substring(0, idxOper).trim();
	    			String value = filterQuery.substring( idxOper+oper.length()).trim();
	    			if(oper.equals(QueryExpression.GTE)||oper.equals(QueryExpression.LTE)||oper.equals(QueryExpression.GT)||oper.equals(QueryExpression.LT)) {	
	    				return  getRange(key,value,oper );
	    			}else if(oper.equals(QueryExpression.MATCH)) {
	    				return getTermQuery(key,value,"||");
	    			}else if(oper.equals(QueryExpression.IN)) {	
	    				return getSimpleQuery(key,value,",",0);
	    			}else if(oper.equals(QueryExpression.NOMATCH)) {
	    				BoolQueryBuilder qbuilder = new BoolQueryBuilder();
	    				qbuilder.mustNot(getTermQuery(key,value,"||")); 
	    				return qbuilder;
	    			}
	    			break;
	    		}
	    	}
	    	
			/*RangeQueryBuilder sQuery = QueryBuilders.rangeQuery(field);
			operator = operator.trim();
			 
			if(!"".equals(operator)) {
				sQuery.format(format);
			}
			*/
			return null; 
		} 
	    
	    public QueryBuilder getSimpleQuery(String fields,String query,String fspliter,int andor) {
			Map<String, Float> fieldInfo = new HashMap<String, Float>();
			if("".equals(fields)) {
				return null;
			}
			String[] arField = StringUtil.split(fields,",");
			for(String af:arField) {
				if(af.indexOf("/")>-1) {
					String _field[]=StringUtil.split(af,"/");
					float boost = 1.0f;
					try {
						boost = Float.parseFloat(_field[1]);
					}catch(Exception ex) {
						boost = 1.0f;
					}
				
					fieldInfo.put(_field[0], boost);
				}else {
					fieldInfo.put(af, 1.0f);
				}
			}
			Operator op = Operator.AND;
			if(andor==0) {
				op = Operator.OR;
			}
			QueryBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery(query).fields(fieldInfo).defaultOperator(op);
			return queryBuilder;
		}
	    
	    public QueryBuilder getTermQuery(String field,String query,String spliter) {		
			if("".equals(query.trim())) {
				return QueryBuilders.termsQuery(field, "");
			}
	        String[] arQuery = StringUtil.split(query,spliter);
	        Set<String> filter = new HashSet<String>();
	        for(String _q : arQuery) {
	            if (_q != null && !_q.equals("")) {
	                filter.add(_q);
	            }
	        } 
			return QueryBuilders.termsQuery(field, filter); 
			
		}
		
		public QueryBuilder getRange(String field,String value,String operator) {		
			RangeQueryBuilder sQuery = QueryBuilders.rangeQuery(field);
			if(QueryExpression.GTE.equals(operator)) {
				sQuery.gte(value);
			}else if(QueryExpression.LTE.equals(operator)) {
				sQuery.lte(value);
			}else if(QueryExpression.GT.equals(operator)) {
				sQuery.gt(value);
			}else if(QueryExpression.LT.equals(operator)) {
				sQuery.lt(value);
			}
			 
			return sQuery; 
		} 
	    
	    public ArrayList<Object> setList() {
	    	StringBuilder data = new StringBuilder();
	    	char operator = ' ';
	    	
	    	 
	    	int depth  = 0;
	    	int predepth  = 0;
	    	ArrayList<Object> thisSet = null;
	    	ArrayList<Object> parentSet = new ArrayList<Object>();
	    	
	    	ArrayList<ArrayList<Object>> rootList = new ArrayList<ArrayList<Object>>();
	    	rootList.add(parentSet);
	    	for(int idx = 0 ; idx<input.length() ; idx++) {
	    		char token = input.charAt(idx); 
	    		if(token == '('){
	    			if(idx>0) {
	    				if(input.charAt(idx-1)=='(') {
	    					parentSet = thisSet;
	    					rootList.add(parentSet);
	    				}else {
	    					if(operator!=' ') {
	    	    				//System.out.println(operator+"//");
	    	    				parentSet.add(operator+"");
	    	    			}
	    				}
	    			}
	    			depth++; 
	    			thisSet = new ArrayList<Object>();
	    			
	    			parentSet.add(thisSet);
	    			
	    		}else if(token == ')') {	
	    			depth--;
	    			String value = data.toString();
	    			if(input.charAt(idx-1)==')') {
	    				parentSet = rootList.get(rootList.size()-2); 
	    			}else if(!"".equals(value)) {
	    				thisSet.add(value);
		    			data.setLength(0);
	    			}
	    			operator = ' ';
	    		 
	    		}else {
	    			if(input.charAt(idx+1)=='(') {
		    			if(token == '+') {	
			    			operator = QueryExpression.AND;
			    		}else if(token == '-') {	
			    			operator = QueryExpression.NOT;
			    		}else if(token == '|') {
			    			operator = QueryExpression.OR;
			    		}else {
			    			data.append(token);
			    		}
	    			}else {
	    				data.append(token);
	    			}
	    		}
	    	}
	    	//System.out.println(data.toString());
	    	if(!"".equals(data.toString())) {
	    		thisSet = new ArrayList<Object>();
    			
	    		thisSet.add(data.toString());
	    		parentSet.add(thisSet);
	    	}
	    	return rootList.get(0);	    	
	    }
	    
	     
	    
	    
	    
	    public void appendListQuery(BoolQueryBuilder parentBu,List<QueryBuilder> childList, char oper) {
	    	for(QueryBuilder queryBuilder : childList) {
		    	if(oper == QueryExpression.AND) {
		    		parentBu.must(queryBuilder);
		    	}else if(oper == QueryExpression.OR) {
		    		parentBu.should(queryBuilder);
		    	}else if(oper == QueryExpression.NOT) {
		    		parentBu.mustNot(queryBuilder);
		    	}
	    	}
	    }
	    
	    public void appendQuery(BoolQueryBuilder parentBu,QueryBuilder queryBuilder, char oper) {
    	 
	    	if(oper == QueryExpression.AND) {
	    		parentBu.must(queryBuilder);
	    	}else if(oper == QueryExpression.OR) {
	    		parentBu.should(queryBuilder);
	    	}else if(oper == QueryExpression.NOT) {
	    		parentBu.mustNot(queryBuilder);
	    	}
	    	 
	    }
	    
	    /*
	    public static void main(String[] args) {
	    	QueryParser aa=new QueryParser();
	    	//System.out.println(aa.getFilterString("a: :b"));
	        //System.out.println(new QueryParser2("(b)-((c)+((a)-(b)))"));
	    	//System.out.println(123);      
	    	//System.out.println(aa.getQueryFilter("( startDate <= 20190301 )+( endDate >= 20190301 )+( sType = PWD )"));
	        //System.out.println(new Parser("(( a + b ) * (( c + d )))").parse());
	    }
	    */
	    
}
