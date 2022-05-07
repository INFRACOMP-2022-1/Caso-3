package Client;

import Utils.KeyGenerators;

import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Client class is responsible for initiating and managing all the client threads, one client corresponds to one thread.
 * Its child class (ClientThread) is responsible for initiating the protocol communications.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Client {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The port the client threads will connect to.It's the logical endpoint of the network connection that is used to exchange information between a server and a client. This is what the server socket(Socket) will be attached to
     */
    public static final int PORT = 3333;

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the servers public key. K_S+
     */
    private static PublicKey serverPublicKey;

    /*
    The number of active client threads to be generated to make petitions to the server.
     */
    private static int numberOfActiveClients;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * The client constructor. It manages the creation of client threads.
     * Each client makes a request to the server, which will dispatch a thread in order to deal with his petition.
     * @param publicKeyStorageFileName the file where the servers public key is stored
     * @param numberOfActiveClients the number of clients that are going to be making a petition to the server. It will also correspond to the number of created server threads to deal with the pettitions.
     */
    public Client(String publicKeyStorageFileName,int numberOfActiveClients) throws IOException {
        System.out.println("Im the client");

        //Read the servers public key from the storage file
        serverPublicKey = readPrivateKeyFromFile();

        //"127.0.0.1"
        for(int i = 0; i < numberOfActiveClients; i++){
            Socket socketToServer = new Socket("127.0.0.1", PORT);
            Thread thread = new ClientThread(socketToServer,serverPublicKey);
            thread.start();
        }
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------
    public PublicKey readPrivateKeyFromFile(String fileName) throws IOException, NoSuchAlgorithmException {
        //TODO: Ver lo del taller
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

}
