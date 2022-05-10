package SecurityUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Class responsible for encrypting data with a variety of algorithms and encryption types.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Encryption {

    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------


    /**
     * Encrypts a message using a given secret key (symmetric).
     * @param unencryptedMessageBytes the unencrypted message in a byte array format.
     * @param secretKey the secret key used to encrypt the message
     * @return a byte array containing the encrypted message.
     */
    public static byte[] encryptWithSymmetricKey(byte[] unencryptedMessageBytes, SecretKey secretKey){
        try{
            //Note: in some documentation the option to create an IV (initialization vector) is also given, but taller 8 doesn't use it so im going to skip over that
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(unencryptedMessageBytes);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypts the given message using the given private key.
     * @param unencryptedMessageBytes the unencrypted message in the format of a byte array
     * @param privateKey the private key used to encrypt the message
     * @return the encrypted message in the format of a byte array
     */
    public static byte[] encryptWithPrivateKey(byte[] unencryptedMessageBytes, PrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(unencryptedMessageBytes);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypts the given message using the given public key.
     * @param unencryptedMessageBytes the unencrypted message in the format of a byte array
     * @param publicKey the public key used to encrypt the message
     * @return the encrypted message in the format of a byte array
     */
    public static byte[] encryptWithPublicKey(byte[] unencryptedMessageBytes,PublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(unencryptedMessageBytes);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
