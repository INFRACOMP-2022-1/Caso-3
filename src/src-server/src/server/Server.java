package server;

import encryptionDecryption.KeyGenerators;
import records.RecordList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

/**
 * Server
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Server {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The port the server. Its the logical endpoint of the network connection that is used to exchange information between a server and a client. This is what the client socket will be attached to
     */
    //TODO: Definir que puerto vamos a usar por ahora puse uno random
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


    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Im the server");

        //Generates the private and public key
        KeyPair kp = KeyGenerators.generateKeyPair();
        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();

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
            //TODO: Activar esto cuando ya hallamos terminado la clase de server socket
            new ServerThread(socket,privateKey,publicKey,recordList).start();
        }
    }
}
