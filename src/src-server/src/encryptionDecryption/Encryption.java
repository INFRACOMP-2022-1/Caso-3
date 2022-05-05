package encryptionDecryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.nio.file.attribute.UserPrincipalNotFoundException;
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
    public static void encryptWithSymmetricKey(byte[] unencryptedMessageBytes, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
    }

    //TODO: ENCRYPT MESSAGE WITH PRIVATE KEY (K_S-)
    public static byte[] encryptWithPrivateKey(byte[] unencryptedMessageBytes, PublicKey publicKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        //REFERENCE -> https://www.baeldung.com/java-rsa
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(unencryptedMessageBytes);
        return encryptedMessageBytes;
    }

    //TODO: ENCRYPT MESSAGE USING HMAC
    public static void encryptWithHMAC(byte[] unencryptedMessageBytes){

    }


}
