package Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

//TODO: Descripcion de la clase
public class Decryption {

    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // CONSTANTS
    //---------------------------------------------------------------------

    //TODO: Document
    public static final String PRIVATE_KEY_ALGORITHM = "RSA";

    //TODO: Document
    public static final String SYMMETRIC_KEY_ALGORITHM = "AES/ECB/PKCS5Padding";

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //TODO: DECRYPT MESSAGE USANDO PRIVATE KEY
    public static byte[] decryptWithPrivateKey(byte[] encryptedMessageBytes, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(PRIVATE_KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(encryptedMessageBytes);
    }

    //TODO: DECRYPT MESSAGE USANDO LS KEY
    public static byte[] decryptWithSymmetricKey(byte[] encryptedMessageBytes, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(SYMMETRIC_KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedMessageBytes);
    }



}
