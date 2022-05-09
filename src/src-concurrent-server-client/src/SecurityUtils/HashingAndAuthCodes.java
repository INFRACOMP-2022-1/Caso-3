package SecurityUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class responsible for implementing a variety of algorithms for hash functions and authentication codes.
 */
public class HashingAndAuthCodes {
    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * The signature of a given message using HMAC. This is used to create the authentication certificate.
     * @param messageBytes the message in a byte array format (the message shouldn´t be encrypted)
     * @param secretKey the secret key used to sign the HMAC
     * @return a byte array with the HMAC signature
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] signWithHMAC(byte[] messageBytes, SecretKey secretKey){
        try{
            String hmacSHA256Algorithm = "HmacSHA256";

            Mac mac = Mac.getInstance(hmacSHA256Algorithm);
            mac.init(secretKey);
            return mac.doFinal(messageBytes);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates a messages digest.
     * A Message digest is basically a secure one way hash function that takes a byte array and outputs a fixed length hash value.
     * @param messageBytes
     * @return a byte array containing the produced hash, the digest
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getMessageDigest(byte[] messageBytes){
        try{
            String algorithm = "SHA-256";
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(messageBytes);
            return digest.digest();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
