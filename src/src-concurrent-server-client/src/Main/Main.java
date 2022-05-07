package Main;

import Client.Client;
import Server.Server;
import java.security.NoSuchAlgorithmException;


public class Main {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    This contains the info of the file where the servers public key is writen to
     */
    private static final String publicKeyStorageFileName = "publicKeyStorage";

    /*
    This contains the information of how many active clients are to be initialized.
     */
    public static final int numberOfActiveClients = 1;

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The server manager. Its responsible for dispatching the serverThreads that handle client requests according to demand.
     */
    private static Server serverManager;

    /*
    The client manager. Its responsible for initializing a set number of client threads to send petitions to the server regarding package statuses.
     */
    private static Client clientManager;

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    /**
     * The main method that starts the server and the client simulation according to the parameters specified in caso 3.
     * @param args the arguments of the program
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        //Firsts it has to initialize the server
        serverManager = new Server(publicKeyStorageFileName);

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients);

        //Record data
        //TODO: Ver que metricas me toca keep track of.
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------
    //TODO: Hacer los metodos de procesamiento de datos y metricas

}
