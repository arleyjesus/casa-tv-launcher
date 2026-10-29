package tv.casa.launcher;

import java.io.IOException;
import java.util.regex.*;

final class RangePolicy {
    static boolean strongTag(String value){return value!=null&&value.matches("\"[\\x21\\x23-\\x7e\\x80-\\xff]*\"");}
    static long[] bounds(String header,long position)throws IOException{
        try{Matcher m=Pattern.compile("bytes (\\d+)-(\\d+)/(\\d+)").matcher(header==null?"":header);if(!m.matches())throw new IOException("Content-Range no válido");long start=Long.parseLong(m.group(1)),end=Long.parseLong(m.group(2)),total=Long.parseLong(m.group(3));if(start!=position||end<start||total<=end||total>8L*1024*1024*1024)throw new IOException("Rango no válido");return new long[]{start,end,total};}catch(NumberFormatException e){throw new IOException("Rango fuera de límites",e);}
    }
    static long length(String header,long position)throws IOException{return bounds(header,position)[2];}
}
