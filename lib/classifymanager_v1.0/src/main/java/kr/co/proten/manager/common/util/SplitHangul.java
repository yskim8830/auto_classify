package kr.co.proten.manager.common.util;

public class SplitHangul {

    public final static char[] chosung = { '\u3131','\u3132','\u3134','\u3137','\u3138','\u3139','\u3141'
            ,'\u3142','\u3143','\u3145','\u3146','\u3147','\u3148','\u3149','\u314a','\u314b','\u314c'
            ,'\u314d','\u314e' };
    public final static char[] moeum = { '\u314f','\u3150','\u3151','\u3152','\u3153','\u3154','\u3155'
            ,'\u3156','\u3157','\u3158','\u3159','\u315a','\u315b','\u315c','\u315d','\u315e','\u315f'
            ,'\u3160','\u3161','\u3162','\u3163' };
    public  final static char[] badchim = { '\u3131','\u3132','\u3133','\u3134','\u3135','\u3136','\u3137'
            ,'\u3139','\u313a','\u313b','\u313c','\u313d','\u313e','\u313f','\u3140','\u3141','\u3142'
            ,'\u3144','\u3145','\u3146','\u3147','\u3148','\u314a','\u314b','\u314c','\u314d','\u314e' };
    public final static char[] complex = {'\u3133','\u3135','\u3136','\u313a','\u313b','\u313c','\u313d'
            ,'\u313e','\u313f','\u3140','\u3144'};

    /**
     * Hangul jaso separator method
     * @param s input string
     * @return  String result Hangul jaso
     */
    public static String toDaro(String s) {
        if (s == null)
            return null;
        String t = "";
        String tmp = "";
        int n, n1, n2, n3;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            n = (int)(c & 0xFFFF);
            if (n >= 0xAC00 && n <= 0xD7A3) {
                n = n - 0xAC00;
                n1 = n / (21 * 28);
                n = n % (21 * 28);
                n2 = n / 28;
                n3 = n % 28;
                if (n3 == 0)
                    tmp = "" + chosung[n1] + moeum[n2];
                else
                    tmp = "" + chosung[n1] + moeum[n2] + badchim[n3 - 1];
                t += tmp;
            }
            else {
                t += c;
            }
        }

        return t;
    }

   /**
     * Hangul chosung separator method
     * @param s input string
     * @return  String result Hangul chosung
     */
    public static String toChoSung(String s) {
        if (s == null)
            return null;
        StringBuffer t = new StringBuffer();
        int n, n1;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            n = (int)(c & 0xFFFF);
            if (n >= 0xAC00 && n <= 0xD7A3) {
                n = n - 0xAC00;
//                n1 = n / (21 * 28);
                n1 = (((n - (n % 28))/28)/21);
                t.append(chosung[n1]);
            }
            else {
                t.append(c);
            }
        }

        return t.toString();
    }

    /**
     * Hangul jaso append method
     * @param value input string
     * @return  String result Hangul
     */
    public static String toGati(String value) {
        String t = "";
        int vallen=value.length();
        int n = 0, n1 = 0, n2 = 0, n3 = 0, k = 0;
        char c, c2;

        final int WAIT_CHOSUNG_STATE = 0;
        final int WAIT_MOEUM_STATE = 1;
        final int WAIT_BADCHIM_STATE = 2;
        int state = WAIT_CHOSUNG_STATE;

        for ( int tran = 0; tran < vallen; tran++) {
            c = value.charAt(tran);

            if (c <= 0xFF) {
                if (state == WAIT_MOEUM_STATE) {
                    t += chosung[n1];
                    //t += unescape("%"+from10toradix(c,16));
                } else if (state == WAIT_BADCHIM_STATE) {
                    n = 0xAC00 + (n1 * 21 * 28 + n2 * 28);
                    t += (char)n;
                    //t += unescape("%"+from10toradix(c,16));

                }
                t += c;
                state = WAIT_CHOSUNG_STATE;
                continue;
            }

            if (t.length() > 0) {
                c2 = t.charAt(t.length() - 1);
            } else {
                c2 = (char) 0;
            }
            switch (state) {
                case WAIT_CHOSUNG_STATE:
                    k = getChosungIndex(c);
                    if (k >= 0) {
                        n1 = k;
                        state = WAIT_MOEUM_STATE;
                    }else {
                        t += c;
                        state = WAIT_CHOSUNG_STATE;
                    }
                    break;
                case WAIT_MOEUM_STATE:
                    k = getMoeumIndex(c);
                    if (k >= 0) {
                        n2 = k;
                        state = WAIT_BADCHIM_STATE;
                    }else {
                        k = getChosungIndex(c);
                        if (k >= 0) {
                            t += chosung[n1];
                            n1 = k;
                            break;
                        } else {
                            t += chosung[n1];
                            t += c;

                            n1=-1;
                            state = WAIT_CHOSUNG_STATE;
                            break;
                        }
                    }
                    break;
                case WAIT_BADCHIM_STATE:
                    k = getBadchimIndex(c);
                    if (k >= 0) {
                        n3 = k + 1;
                        n = 0xAC00  + (n1 * 21 * 28 + n2 * 28 + n3);
                        if(value.length() > (tran+1)){
                            if( getMoeumIndex(value.charAt(tran+1))>=0 ) {
                                k = getChosungIndex(c);
                                if (k >= 0) {
                                    n = 0xAC00 + (n1 * 21 * 28 + n2 * 28);
                                    t += (char)n;
                                    n1 = k;
                                    state = WAIT_MOEUM_STATE;
                                    break;
                                }else {
                                    n = 0xAC00 + (n1 * 21 * 28 + n2 * 28);
                                    t += (char)n;
                                    t += c;
                                    state = WAIT_CHOSUNG_STATE;
                                }
                            } else {
                                t += (char)n;
                            }
                        }
                        state = WAIT_CHOSUNG_STATE;
                    }else {
                        k = getChosungIndex(c);
                        if (k >= 0) {
                            n = 0xAC00 + (n1 * 21 * 28 + n2 * 28);
                            t += (char)n;
                            n1 = k;
                            state = WAIT_MOEUM_STATE;
                            /*if( (tran+1) <= vallen)
                                t += chosung[n1];*/
                            break;
                        }else {
                            n = 0xAC00 + (n1 * 21 * 28 + n2 * 28);
                            t += (char)n;
                            t += c;
                            state = WAIT_CHOSUNG_STATE;
                        }
                    }
                    break;
            }

        }
        return t.trim();
    }

    /**
     * Chosung index
     * @param a
     * @return  int Chosung index
     */
    public static int getChosungIndex(char a) {
        int len = chosung.length;
        for (int i = 0; i < len; i++) {
            if (a == chosung[i])
                return i;
        }
        return -1;
    }

    /**
     * Jongsung index
     * @param a
     * @return  int Jongsung index
     */
    public static int getBadchimIndex(char a) {
        int len = badchim.length;
        for (int i = 0; i < len; i++) {
            if (a == badchim[i])
                return i;
        }
        return -1;
    }

    /**
     * Moeum index
     * @param a
     * @return int Moeum index
     */
    private static int getMoeumIndex(char a) {
        int len = moeum.length;
        for (int i = 0; i < len; i++) {
            if (a == moeum[i])
                return i;
        }
        return -1;
    }

    private static int getBadchim(char a) {
        int n = (int) a;
        if (n >= 0xAC00 && n <= 0xD7A3)
            return (n - 0xAC00) % 28;
        return -1;
    }

    private static String charVerification(String a) {
        int hex = 0;
        String temp= "";
        //System.out.println(a.length());
        for(int i=0; i< a.length(); i++){
            if(a.length() > 0)
                hex = a.charAt(i);
            //System.out.print(a.charAt(i)+" | "+hex+" | ");
            if(hex == 0 || hex == 32 || hex == 65533)  {
                temp += "";
            }else{
                temp += a.charAt(i);
            }
        }

        return temp;
    }

    /*
    public static void main(String[] args) {
        String sbdata = "대한민국" ;
        //System.out.println(sbdata);
        String daro = toDaro(sbdata) + " ";
        //System.out.println(daro);
        String gati = toGati(daro);
        System.out.println(gati);
    }
    */
}