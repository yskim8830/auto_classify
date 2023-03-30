/*
* @(#)IOUtil.java 06/03/09
* WISEnut Common
* Copyright 2000-2006 Korea WISEnut, Inc. All Rights Reserved.
* This software is the proprietary information of WISEnut, Inc.
*/
package kr.co.proten.manager.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Pattern;

public class IOUtil {

    /**
     *
     * @param input
     */
    public static void closeQuietly( Reader input ){
        if( input == null ){
            return;
        }

        try{
            input.close();
        }catch( IOException ioe ){
        	return;
        }
    }

    /**
     *
     * @param output
     */
    public static void closeQuietly( Writer output )
    {
        if( output == null ){
            return;
        }

        try{
        	output.flush();
        	output.close();
        } catch( IOException ioe ){
        	return;
        }
    }


    /**
     *
     * @param output
     */
    public static void closeQuietly( OutputStream output ){
        if( output == null ){
            return;
        }
        try{
        	output.flush();
            output.close();
        } catch( IOException ioe ){
        	return;
        }
    }

    /**
     * @param input
     */
    public static void closeQuietly( InputStream input ){
        if( input == null ) {
            return;
        }

        try{
            input.close();
        }catch( IOException ioe ) {
        	return;
        }
    }

    /**
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static String toString( InputStream input )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw );
        return sw.toString();
    }


    /**
     *
     * @param input the InputStream to read from
     * @param encoding The name of a supported character encoding. See the
     * @return
     * @throws IOException
     */
    public static String toString( InputStream input,
                                   String encoding )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw, encoding );
        return sw.toString();
    }

    /**
     * @param input the InputStream to read from
     * @return the requested byte array
     * @throws IOException In case of an I/O problem
     */
    public static byte[] toByteArray( InputStream input )
            throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOCopy.copy( input, output );
        return output.toByteArray();
    }

    /**
     * Reader to String
     * @param input the Reader to read from
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( Reader input )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw );
        return sw.toString();
    }


    /**
     * Reader to Array
     * @param input the Reader to read from
     * @return the requested byte array
     * @throws IOException In case of an I/O problem
     */
    public static byte[] toByteArray( Reader input )
            throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOCopy.copy( input, output );
        return output.toByteArray();
    }

    /**
     * @param input the String to convert
     * @return the requested byte array
     * @throws IOException In case of an I/O problem
     */
    public static byte[] toByteArray( String input )
            throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOCopy.copy( input, output );
        return output.toByteArray();
    }


    /**
     * @param input the byte array to read from
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( byte[] input )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw );
        return sw.toString();
    }


    /**
     * @param input the byte array to read from
     * @param encoding The name of a supported character encoding. See the
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( byte[] input,
                                   String encoding )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw, encoding );
        return sw.toString();
    }


    /**
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the content of the streams are equal or they both don't exist, false otherwise
     * @throws IOException In case of an I/O problem
     */
    public static boolean contentEquals( InputStream input1,
                                         InputStream input2 )
            throws IOException{
        InputStream bufferedInput1 = new BufferedInputStream( input1 );
        InputStream bufferedInput2 = new BufferedInputStream( input2 );

        int ch = bufferedInput1.read();
        while( -1 != ch ){
            int ch2 = bufferedInput2.read();
            if( ch != ch2 ){
                return false;
            }
            ch = bufferedInput1.read();
        }

        int ch2 = bufferedInput2.read();
        if( -1 != ch2 ){
            return false;
        }else{
            return true;
        }
    }

    /**
     * @return true if the two streams differ at any point
     **/
    public static boolean isDiff(InputStream a, InputStream b) throws IOException{
        int x, y;

        do {
            x = a.read();
            y = b.read();
            if (x != y) return true;
        } while (x != -1);
        return false;
    }

    /**
     * @param input stream to read
     * @return String containing contents of file
     **/
    public static String readStream(InputStream input) throws IOException {
        return readReader(new InputStreamReader(input));
    }

    /**
     * @param input stream to read
     * @return String containing contents of file
     **/
    public static String readReader(Reader input) throws IOException {
        try {
            StringBuffer buf = new StringBuffer();
            BufferedReader in = new BufferedReader(input);
            int ch;
            while ((ch=in.read())!=-1)
                buf.append((char)ch);
            return buf.toString();
        } finally {
            input.close();
        }
    }
 
    /**
     *
     * @param e
     * @return
     */
    /*
    public static String StackTraceToString( Exception e ){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream p = new PrintStream( b );
        //e.printStackTrace( p );
        p.flush();
        return b.toString();
    }
    */

    /**
     *
     * @param e
     * @return
     */
    /*
    public static String StackTraceToString( LinkageError e ){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream p = new PrintStream( b );
        //e.printStackTrace( p );
        p.flush();
        return b.toString();
    }
    */

    /*
    public static String StackTraceToString(Throwable throwable) {
       Writer writer = new StringWriter();
       PrintWriter printWriter = new PrintWriter(writer);
       throwable.printStackTrace(printWriter);
       return writer.toString();
    }
    */

    /**
     * ip 를 pattern matching하여 검사한다.
     * @param str
     * @return
     */
    public static boolean checkIPAddress(String str) {

        str = str.trim().toLowerCase();
        String patternRegex = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";
        return  Pattern.matches(patternRegex, str);

    }

}
