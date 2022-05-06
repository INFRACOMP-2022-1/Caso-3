package client;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * Client
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Client {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the server socket. It represents the connection to the server, it has all his info in such a way we can send information to it.
     */
    Socket serverSocket;

    //TODO: Documentar
    static SecretKey secretKey;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //TODO: Document
    public static void closeAllConnectionsToServer() throws IOException {
        //incomingMessageChanel.close();
        //outgoingMessageChanel.close();
        //clientSocket.close();
    }

    //TODO: Generate Secret Key
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        String CIPHER_AES = "AES";
        int SECRET_KEY_SIZE = 256;

        KeyGenerator kg = KeyGenerator.getInstance(CIPHER_AES);
        kg.init(SECRET_KEY_SIZE);
        return kg.generateKey();
    }

    //----------------------------------------------------------------------
    // GETTERS AND SETTERS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Im the client");

        //Generate LS (SecretKey)
        secretKey = generateSecretKey();
    }
}
