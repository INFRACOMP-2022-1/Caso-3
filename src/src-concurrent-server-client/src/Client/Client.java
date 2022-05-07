package Client;

import Utils.KeyGenerators;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

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

    /*
    The file name for the file where the server public key has been stored
     */
    private static String publicKeyStorageFileName;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * The client constructor. It manages the creation of client threads.
     * Each client makes a request to the server, which will dispatch a thread in order to deal with his petition.
     * @param publicKeyStorageFileName the file where the servers public key is stored
     * @param numberOfActiveClients the number of clients that are going to be making a petition to the server. It will also correspond to the number of created server threads to deal with the petitions.
     * @param
     */
    public Client(String publicKeyStorageFileName, int numberOfActiveClients, ArrayList<PackageStatusRequests> packageStatusRequestList) throws IOException {
        System.out.println("Im the client");

        //Stores the file name where the public key is going to be retreived from
        this.publicKeyStorageFileName = publicKeyStorageFileName;

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

    /**
     * Reads the private key from the file where the server stored the serialized object.
     * @return The server public key
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ClassNotFoundException
     */
    public PublicKey readPrivateKeyFromFile() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        //Creates file input stream for the file with the public key
        FileInputStream file = new FileInputStream(publicKeyStorageFileName);

        //Creates a object input stream to be able to read from the file
        ObjectInputStream objectInputStream = new ObjectInputStream(file);

        //Retrieves the public key stored in the file and returns it
        return (PublicKey) objectInputStream.readObject();
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
