package Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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

    //TODO: Documentar
    //TODO: Preguntar si esto cumple con el req de AES. Modo ECB, esquema de relleno PKCS5, llave de 256 bits.? Solo he visto que el keusize con kg va hasta 128
    public static SecretKey generateSecretKeyLS() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String algorithm = "AES";
        int keySize = 256;

        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(keySize);

        return kg.generateKey();

    }

}
