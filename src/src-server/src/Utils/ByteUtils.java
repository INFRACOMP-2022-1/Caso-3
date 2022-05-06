package Utils;

import java.nio.ByteBuffer;

//TODO: Descripcion
public class ByteUtils {

    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //TODO: DOCUMENTAR
    //TODO: ESTAS SON LOS METODOS QUE RECOMIENDAN USAR PARA CONVERTIR LOS BYTES A STR ANTES DE TRANSMITIR Y PARA PASAR DE STR A BYTES CUANDO UNO RECIBE
    public static String byte2str(byte[] b)
    {
        // Encapsulamiento con hexadecimales
        String ret = "";
        for (int i = 0 ; i < b.length ; i++) {
            String g = Integer.toHexString(((char)b[i])&0x00ff);
            ret += (g.length()==1?"0":"") + g;
        }
        return ret;
    }

    //TODO: DOCUMENTAR
    //TODO: ESTAS SON LOS METODOS QUE RECOMIENDAN USAR PARA CONVERTIR LOS BYTES A STR ANTES DE TRANSMITIR Y PARA PASAR DE STR A BYTES CUANDO UNO RECIBE
    public static byte[] str2byte( String ss)
    {
        // Encapsulamiento con hexadecimales
        byte[] ret = new byte[ss.length()/2];
        for (int i = 0 ; i < ret.length ; i++) {
            ret[i] = (byte) Integer.parseInt(ss.substring(i*2,(i+1)*2), 16);
        }
        return ret;
    }

    //TODO: DOCUMENTAR
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    //TODO: DOCUMENTAR
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
