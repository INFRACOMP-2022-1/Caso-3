package Utils;

import java.nio.ByteBuffer;

/**
 * The byte utils class contains a range of methods that convert from a variety of
 * data types (String,Long,Integer,etc) to Byte Arrays (byte[]) and vice versa.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class ByteUtils {

    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------


    /**
     * Transforms bytes to strings.
     * In relation to sockets , this is normally done before writing on the socket.
     * @param b is the byte array
     * @return String corresponding to the given byte array
     */
    public static String byte2str(byte[] b)
    {
        // Encapsulation with hexadecimals
        String ret = "";
        for (int i = 0 ; i < b.length ; i++) {
            String g = Integer.toHexString(((char)b[i])&0x00ff);
            ret += (g.length()==1?"0":"") + g;
        }
        return ret;
    }

    /**
     * Reads the chains and converts them to bytes.
     * In relation to sockets, this is normally done after it's read from a socket
     * @param ss the string that is to be converted
     * @return A byte array , byte[] , of the corresponding string
     */
    public static byte[] str2byte( String ss)
    {
        // Encapsulation with hexadecimals
        byte[] ret = new byte[ss.length()/2];
        for (int i = 0 ; i < ret.length ; i++) {
            ret[i] = (byte) Integer.parseInt(ss.substring(i*2,(i+1)*2), 16);
        }
        return ret;
    }

}
