package Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;

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
    public static final int PORT = 2023;

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
    public Client(String publicKeyStorageFileName,int numberOfActiveClients){
        System.out.println("Im the client");

        //Read the servers public key from the storage file
        serverPublicKey = readPrivateKeyFromFile();
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------
    public PublicKey readPrivateKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        byte[] encodedKey = new byte[fis.available()];
        fis.read(encodedKey);
        fis.close();

        //TODO: Terminar esto
        //ESTOY AQUI
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
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
