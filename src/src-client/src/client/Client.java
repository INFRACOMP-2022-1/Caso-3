package client;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

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
    protected static Socket serverSocket;

    //TODO: Documentar
    protected static SecretKey secretKey;

    //TODO: Documentar
    protected static PublicKey publicKeyServer;

    //TODO: Documentar
    public long reto;

    //TODO: Documentar
    public String username;

    //TODO: Documentar
    public int packageId;

    //TODO: Documentar
    public String status;

    //TODO: Documentar
    public String digest;

    //TODO: Documentar
    public static PrintWriter outgoingMessageChanel;

    public static BufferedReader incomingMessageChanel;

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
    // ENCRYPTION
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Im the client");

        try {

            //TODO: Document
            outgoingMessageChanel = new PrintWriter(serverSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            //TODO: ESCRIBIR EL PROTOCOLO DETALLADO EN LAS NOTAS (VERONOTAS) AQUI (DE LA MISMA FORMA QUE ESTA EN SERVERTHREAD
            //Generate LS (SecretKey)
            secretKey = generateSecretKey();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
