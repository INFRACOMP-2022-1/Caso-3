package Utils;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Class responsible for decrypting data with a variety of algorithms and decryption types.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Decryption {

    /**
     * There are no attributes to be initialized so there is no constructor needed. The compiler will provide a default constructor.
     */

    //----------------------------------------------------------------------
    // CONSTANTS
    //---------------------------------------------------------------------

    /*
    The private key algorithm to implement asymmetric encryption. RSA
     */
    public static final String PRIVATE_KEY_ALGORITHM = "RSA";

    /*
    The public key algorithm to implement symmetric encryption. AES with ECB
     */
    public static final String SYMMETRIC_KEY_ALGORITHM = "AES/ECB/PKCS5Padding";

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Decrypts a message using the private key given by parameter.
     * This method will only work if the private key used is a pair to the public key used to encrypt the message.
     * @param encryptedMessageBytes the encrypted message in the format of a byte array
     * @param privateKey the private key used to decrypt the message. (It should be the pair of the public key used to encrypt the message)
     * @return A byte array with the unencrypted message
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decryptWithPrivateKey(byte[] encryptedMessageBytes, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(PRIVATE_KEY_ALGORITHM);//Uses RSA
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(encryptedMessageBytes);
    }

    /**
     * Decrypts a message using the public key given by parameter.
     * The method will only work if the public key used is a pair to the private key used to encrypt the message.
     * @param encryptedMessageBytes the encrypted message in the format of a byte array
     * @param publicKey the public key used to decrypt the message. (It should be the pair of the private key used to encrypt the message)
     * @return A byte array with the unencrypted message
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decryptWithPublicKey(byte[] encryptedMessageBytes, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(PRIVATE_KEY_ALGORITHM);//Uses RSA
        cipher.init(Cipher.DECRYPT_MODE,publicKey);
        return cipher.doFinal(encryptedMessageBytes);
    }

    /**
     * Decrypts a message using the shared secret key given by parameter.
     * This method will only work if the secret key used is the same as the key used to encrypt the message
     * @param encryptedMessageBytes the encrypted message in a byte array format
     * @param secretKey the secret key used to decrypt the message
     * @return an unencrypted byte array with the message content.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decryptWithSymmetricKey(byte[] encryptedMessageBytes, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(SYMMETRIC_KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedMessageBytes);
    }
}
