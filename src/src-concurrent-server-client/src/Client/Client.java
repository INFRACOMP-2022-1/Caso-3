package Client;

import StatusRequests.PackageStatusRequests;
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

    /*
    The host that the client socket is going to be attached to
     */
    public static final String HOST = "127.0.0.1";

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
    private static int clientRequestsNumber;

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
    public Client(String publicKeyStorageFileName, int clientRequestsNumber, ArrayList<PackageStatusRequests> packageStatusRequestList) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        System.out.println("Im the client");

        //Stores the file name where the public key is going to be retreived from
        this.publicKeyStorageFileName = publicKeyStorageFileName;

        //The number of clients that need to be created to fullfil all the requests
        this.clientRequestsNumber = clientRequestsNumber;

        //Read the servers public key from the storage file
        serverPublicKey = readPrivateKeyFromFile();

        //"127.0.0.1"
        for(int i = 0; i < clientRequestsNumber; i++){
            //Get the request that this client thread is going to make
            PackageStatusRequests request = packageStatusRequestList.get(i);

            //Creates the socket that the client is going to be attached to
            //TODO: Check if i need to use different ports for different sockets
            Socket socketToServer = new Socket(HOST, PORT);

            //Creates the client thread that is going to be launched and follow the request making protocol
            ClientThread thread = new ClientThread(socketToServer,serverPublicKey,request);

            //Starts the client thread protocol
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
