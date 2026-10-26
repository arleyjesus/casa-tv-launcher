package tv.casa.launcher;

import java.io.IOException;
import java.util.regex.*;

final class RangePolicy {
    static long length(String header,long position)throws IOException{
        try{Matcher m=Pattern.compile("bytes (\\d+)-(\\d+)/(\\d+)").matcher(header==null?"":header);if(!m.matches())throw new IOException("Content-Range no válido");long start=Long.parseLong(m.group(1)),end=Long.parseLong(m.group(2)),total=Long.parseLong(m.group(3));if(start!=position||end<start||total<=end||total>8L*1024*1024*1024)throw new IOException("Rango no válido");return total;}catch(NumberFormatException e){throw new IOException("Rango fuera de límites",e);}
    }
}
