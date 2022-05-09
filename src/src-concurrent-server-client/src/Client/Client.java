package Client;

import StatusRequests.PackageStatusRequests;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
    If debug is turned on
     */
    private static final boolean DEBUG = true;


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
     * @param clientRequestsNumber the number of clients that are going to be making a petition to the server. It will also correspond to the number of created server threads to deal with the petitions.
     * @param packageStatusRequestList the list that contains the information for making the client requests that each thread will be doing (username, packageId)
     * @param debug if debug mode is turned on
     */
    public Client(String publicKeyStorageFileName, int clientRequestsNumber, ArrayList<PackageStatusRequests> packageStatusRequestList,boolean debug) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        System.out.println("Im the client");

        //Stores the file name where the public key is going to be retreived from
        Client.publicKeyStorageFileName = publicKeyStorageFileName;

        //The number of clients that need to be created to fullfil all the requests
        Client.clientRequestsNumber = clientRequestsNumber;

        //Read the servers public key from the storage file
        serverPublicKey = readServerPublicKeyFromFile();

        for(int i = 0; i < clientRequestsNumber; i++){
            //Get the request that this client thread is going to make
            PackageStatusRequests request = packageStatusRequestList.get(i);

            //Creates the socket that the client is going to be attached to
            Socket socketToServer = new Socket(HOST, PORT);

            //It sets a thread colour for easier visualization during debug and testing
            String threadColour = getColour(i);

            //Creates the client thread that is going to be launched and follow the request making protocol
            ClientThread thread = new ClientThread(socketToServer,serverPublicKey,request,debug,threadColour);

            //Starts the client thread protocol
            thread.start();
        }
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Reads the public key from the file where the server stored the serialized object.
     * @return The server public key
     */
    public PublicKey readServerPublicKeyFromFile() throws IOException, ClassNotFoundException {
        //Creates file input stream for the file with the public key
        FileInputStream file = new FileInputStream(publicKeyStorageFileName);

        //Creates a object input stream to be able to read from the file
        ObjectInputStream objectInputStream = new ObjectInputStream(file);

        //Retrieves the public key stored in the file and returns it
        return (PublicKey) objectInputStream.readObject();
    }

    /**
     * Gets a colour for the thread based on its number in the for loop
     * @param i the number of the request being sent
     * @return String with the colour for the thread
     */
    public String getColour(int i){
        //Colour array
        ArrayList<String> colourArray = new ArrayList<>();

        //String colours
        String TEXT_RED = "\u001B[31m";
        colourArray.add(TEXT_RED);
        String TEXT_GREEN = "\u001B[32m";
        colourArray.add(TEXT_GREEN);
        String TEXT_YELLOW = "\u001B[33m";
        colourArray.add(TEXT_YELLOW);
        String TEXT_BLUE = "\u001B[34m";
        colourArray.add(TEXT_BLUE);
        String TEXT_PURPLE = "\u001B[35m";
        colourArray.add(TEXT_PURPLE);
        String TEXT_CYAN = "\u001B[36m";
        colourArray.add(TEXT_CYAN);

        //Returns colour for string based on the parameter i modulo 6
        return colourArray.get(i%6);
    }

}
