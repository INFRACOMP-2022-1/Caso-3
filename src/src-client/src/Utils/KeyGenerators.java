package Utils;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * This class contains the key generators used to generate key paris (asymmetric) and secret keys (symmetric)
 */
public class KeyGenerators {

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Method responsible for generating the private and public key of the server.
     * It stores the generated keys in their respective attributes.
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        int keySize = 1024;//Safe key size
        String algorithm = "RSA";//RSA is Asymmetric

        //Chooses the algorithm to be used and the key size for the keys
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
        kpg.initialize(keySize);

        //Generates a pair of private and public keys
        KeyPair kp = kpg.generateKeyPair();
        return kp;

    }

    /**
     * This generates a secret key (symmetric) using the AES algorithm
     * @return A secret key object
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static SecretKey generateSecretKeyLS() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String algorithm = "AES";
        int keySize = 256;

        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(keySize);

        return kg.generateKey();

    }

}
