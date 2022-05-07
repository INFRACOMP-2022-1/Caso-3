package Server;

import Utils.KeyGenerators;
import Records.RecordList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

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
    public static final int PORT = 2022;

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
     * @throws NoSuchAlgorithmException
     */
    public Server(String publicKeyStorageFileName) throws NoSuchAlgorithmException {
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
        //TODO: Hablar con Geovanny si hay algun problema con esto.
        while(true){
            try{
                //Listens for a connection and if there is one it accepts it. This creates a socket that its tied to the client in such a way that the server can communicate with him.
                socket = serverSocket.accept();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            //Launches a new thread to deal with the client connection
            new ServerThread(socket,privateKey,publicKey,recordList).start();
        }
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Writes the public key to a file within the main package for it to be latter accesed by the client.
     */
    public void writePublicKeyToFile(){
        try{
            /* save the public key in a file on the Client module*/
            byte[] publicKeyByteArray = publicKey.getEncoded();
            FileOutputStream fileOutputStream = new FileOutputStream(publicKeyStorageFileName,false);//makes sure to rewrite the key every time
            fileOutputStream.write(publicKeyByteArray);
            fileOutputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
