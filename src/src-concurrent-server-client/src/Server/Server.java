package Server;

import SecurityUtils.KeyGenerators;
import Records.RecordList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Server. Its responsible for dispatching the server threads to respond to client requests.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Server {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The port the server will connect to . Its the logical endpoint of the network connection that is used to exchange information between a server and a client. This is what the client socket will be attached to
     */
    public static final int PORT = 3333;

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the server socket. It's responsible for listening for any client that wants to establish a connection to the server. It will return a socket with the connection to the client.
     */
    private static ServerSocket serverSocket;

    /*
    This is the servers private key. K_S-
     */
    private static PrivateKey privateKey;

    /*
    This is the servers public key. K_S+
     */
    private static PublicKey publicKey;

    /*
    This contains the list to access all the records
     */
    private static RecordList recordList;

    /*
    This contains the info of the file where the servers public key is writen to
     */
    private static String publicKeyStorageFileName;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * The server constructor. It manages the creation of server threads according to user demand.
     * @param publicKeyStorageFileName the file where the public key is stored so the client has acces to it
     * @param debug if debug mode is turned on or not
     * @throws NoSuchAlgorithmException
     */
    public Server(String publicKeyStorageFileName,boolean debug) throws NoSuchAlgorithmException, IOException {
        System.out.println("Im the server");

        //Generates the private and public key
        KeyPair kp = KeyGenerators.generateKeyPair();
        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();

        //Writes the public key storage file name
        this.publicKeyStorageFileName = publicKeyStorageFileName;

        //Writes the servers public key to a file accesible by the client
        writePublicKeyToFile();

        //Creates a record list with all the usernames, package ids and statuses
        recordList = new RecordList();
        recordList.load();

        //This socket will hold the endpoint of the network connection with the client. It holds the clients direction and port that the server will be sending information to.
        Socket socket = null;

        //The server socket is created and attached to the given port
        try{
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //The server will be permanently listening for any incoming connection until its shut off.
        while(true){
            try{
                //Listens for a connection and if there is one it accepts it. This creates a socket that its tied to the client in such a way that the server can communicate with him.
                socket = serverSocket.accept();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            //It sets a thread colour for easier visualization during debug and testing
            Random random = new Random();
            int num = random.ints(0,7).findFirst().getAsInt();
            String threadColour = getColour(num);

            //Launches a new thread to deal with the client connection
            new ServerThread(socket,privateKey,publicKey,recordList,debug,threadColour).start();
        }
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Writes the serialized public key to a file within the main package for it to be latter accessed by the client.
     */
    public void writePublicKeyToFile() throws IOException {
        //Creates a file output stream with the given file name
        FileOutputStream file = new FileOutputStream(publicKeyStorageFileName);

        //Creates an object output stream anchored to the given file, in such a way that an object can be written.
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(file);

        //Writes the public key to the file
        objectOutputStream.writeObject(publicKey);

        //Closes the object output stream
        objectOutputStream.close();
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
