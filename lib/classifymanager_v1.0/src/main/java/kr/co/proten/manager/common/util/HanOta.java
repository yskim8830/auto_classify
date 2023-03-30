package kr.co.proten.manager.common.util;

public class HanOta {
    final static String[] org ={
            "\u3133","\u3135","\u3136","\u313a","\u313b","\u313c","\u313d","\u313e","\u313f","\u3140",
            "\u3144","\u314f","\u3150","\u3151","\u3152","\u3153","\u3154","\u3155","\u3156","\u3158",
            "\u3159","\u315a","\u3157","\u315b","\u315d","\u315e","\u315f","\u315c","\u3160","\u3162",
            "\u3161","\u3163","\u3131","\u3132","\u3134","\u3137","\u3138","\u3139","\u3141","\u3142",
            "\u3143","\u3145","\u3146","\u3147","\u3148","\u3149","\u314a","\u314b","\u314c","\u314d",
            "\u314e"
        ,"rt","sw","sg","fr","fa","fq","ft","fx","fv","fg","qt"
        ,"r" ,"R" ,"s" ,"e" ,"E" ,"f" ,"a" ,"q" ,"Q" ,"t" ,"T" ,"d" ,"w" ,"W" ,"c" ,"z" ,"x" ,"v" ,"g"
        ,"k" ,"o" ,"i" ,"O" ,"j" ,"p" ,"u" ,"P" ,"hk","ho","hl","h" ,"y" ,"nj","np","nl","n" ,"b" ,"ml","m" ,"l"
    };
    final static String[] dest = {
        "rt","sw","sg","fr","fa","fq","ft","fx","fv","fg","qt"
        ,"k" ,"o" ,"i" ,"O" ,"j" ,"p" ,"u" ,"P" ,"hk","ho","hl","h" ,"y" ,"nj","np","nl","n" ,"b" ,"ml","m" ,"l"
        ,"r" ,"R" ,"s" ,"e" ,"E" ,"f" ,"a" ,"q" ,"Q" ,"t" ,"T" ,"d" ,"w" ,"W" ,"c" ,"z" ,"x" ,"v" ,"g"
        ,"\u3133","\u3135","\u3136","\u313a","\u313b","\u313c","\u313d","\u313e","\u313f","\u3140",
            "\u3144","\u3131","\u3132","\u3134","\u3137","\u3138","\u3139","\u3141","\u3142","\u3143",
            "\u3145","\u3146","\u3147","\u3148","\u3149","\u314a","\u314b","\u314c","\u314d","\u314e",
            "\u314f","\u3150","\u3151","\u3152","\u3153","\u3154","\u3155","\u3156","\u3158","\u3159",
            "\u315a","\u3157","\u315b","\u315d","\u315e","\u315f","\u315c","\u3160","\u3162","\u3161",
            "\u3163"
    };
    // 6자 이상의 경우만 처리 한다.
    public static String swaphaneng(String value){
        String ret = "";
        value= SplitHangul.toDaro(value);
        
        if(value != null && (value.length()<5)) {
        	return "";
        }
        value = convertChar(value);
        if(value != null) {
        	ret = SplitHangul.toGati(value+" ");        	
        }
        return ret;
    }


    public static String convertChar(String value){
        String nextcheckstr="";
        String convtemp="";
        int idx = -1;
        int  idx2 = -1;
        for (int cl=0 ; cl<value.length();){
            idx=-1;
            for(int cl2=0 ; cl2 < org.length ; cl2++){
                int end = cl+org[cl2].length();
                if(cl == (value.length()-1)) end = cl+1;
                if(cl == (value.length()))end = cl;
                if (value.substring(cl, end).equals(org[cl2])){
                    if (org[cl2].length()>=2  && (SplitHangul.getBadchimIndex(dest[cl2].charAt(0))!=-1
                            || SplitHangul.getChosungIndex(dest[cl2].charAt(0))!=-1)){
                        if(end == value.length() ) {
                            nextcheckstr=value.substring(end-1,(end));
                        }else{
                            nextcheckstr=value.substring(end,(end+1));
                        }
                        idx2 = -1;
                        for(int cl3=0 ; cl3 < org.length ; cl3++){
                            if (nextcheckstr.equals(org[cl3])){
                                idx2=cl3;
                                if ((SplitHangul.getBadchimIndex(dest[cl3].charAt(0))!=-1 || SplitHangul.getChosungIndex(dest[cl3].charAt(0))!=-1)){
                                    convtemp=convtemp+dest[cl2];
                                    idx=cl2;
                                    cl = end;
                                    break;
                                }
                            }
                        }

                        if (idx2==-1){
                            convtemp=convtemp+dest[cl2];
                            idx=cl2;
                            cl = end;
                            break;
                        }

                    } else {
                        convtemp=convtemp+dest[cl2];
                        idx=cl2;
                        cl = end;
                        break;
                    }
                }
            }
            if (idx==-1) {
                convtemp=convtemp+value.substring(cl,cl+1);
                cl++;
            }
        }
        return convtemp;
    }

    /*
    public static void main(String args[]) {
        String s = "";
        //System.out.println("INPUT : " + s);
        s = swaphaneng(s);
        //System.out.println("swap[1] " + s);
        s = swaphaneng(s);
        //System.out.println("swap[2] " + s);
    }
    */
}

