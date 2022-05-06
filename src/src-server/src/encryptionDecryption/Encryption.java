package encryptionDecryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

//TODO: Descripcion de la clase
public class Encryption {

    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //TODO: ENCRYPT MESSAGE WITH LS KEY
    //TODO: REVISAR CON GEOVANNY SI ESTO ESTA BIEN
    public static byte[] encryptWithSymmetricKey(byte[] unencryptedMessageBytes, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);//TODO: Esta esto del IV bien, o me toca generarlo de una forma mas inteligente
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        byte[] encryptedMessageBytes = cipher.doFinal(unencryptedMessageBytes);
        return encryptedMessageBytes;
    }

    //TODO: ENCRYPT MESSAGE WITH PRIVATE KEY (K_S-)
    public static byte[] encryptWithPrivateKey(byte[] unencryptedMessageBytes, PublicKey publicKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        //REFERENCE -> https://www.baeldung.com/java-rsa
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessageBytes = cipher.doFinal(unencryptedMessageBytes);
        return encryptedMessageBytes;
    }

    //TODO: ENCRYPT MESSAGE USING HMAC
    public static byte[] encryptWithHMAC(byte[] unencryptedMessageBytes, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        //TODO: No tengo muy claro si HMAC usa la llave LS o no
        String hmacSHA256Algorithm = "HmacSHA256";

        Mac mac = Mac.getInstance(hmacSHA256Algorithm);
        mac.init(secretKey);
        return mac.doFinal(unencryptedMessageBytes);
    }


}
