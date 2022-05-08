package Utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

/**
 * Class responsible for encrypting data with a variety of algorithms and encryption types.
 * This class also contains the HMAC algorithm used to create a authentication certificate for a message.
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
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    //TODO: REVISAR CON GEOVANNY SI ESTO ESTA BIEN
    public static byte[] encryptWithSymmetricKey(byte[] unencryptedMessageBytes, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);//TODO: Esta esto del IV bien, o me toca generarlo de una forma mas inteligente
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        byte[] encryptedMessageBytes = cipher.doFinal(unencryptedMessageBytes);
        return encryptedMessageBytes;
    }

    /**
     * Encrypts the given message using the given private key.
     * @param unencryptedMessageBytes the unencrypted message in the format of a byte array
     * @param publicKey the public key used to encrypt the message
     * @return the encrypted message in the format of a byte array
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encryptWithPrivateKey(byte[] unencryptedMessageBytes, PublicKey publicKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        //REFERENCE -> https://www.baeldung.com/java-rsa
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessageBytes = cipher.doFinal(unencryptedMessageBytes);
        return encryptedMessageBytes;
    }

    /**
     * The signature of a given message using HMAC. This is used to create the authentication certificate.
     * @param messageBytes the message in a byte array format (the message shouldn´t be encrypted)
     * @param secretKey the secret key used to sign the HMAC
     * @return a byte array with the HMAC signature
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] signWithHMAC(byte[] messageBytes, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        //TODO: No tengo muy claro si HMAC usa la llave LS o no
        String hmacSHA256Algorithm = "HmacSHA256";

        Mac mac = Mac.getInstance(hmacSHA256Algorithm);
        mac.init(secretKey);
        return mac.doFinal(messageBytes);
    }

    /**
     * Calculates a messages digest.
     * A Message digest is basically a secure one way hash function that takes a byte array and outputs a fixed length hash value.
     * @param messageBytes
     * @return a byte array containing the produced hash, the digest
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getMessageDigest(byte[] messageBytes) throws NoSuchAlgorithmException {
        String algorithm = "SHA-256";
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(messageBytes);
        return digest.digest();
    }
}
