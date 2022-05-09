package Server;

import Client.Client;
import StatusRequests.PackageStatusRequests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ServerMain {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    If debug is turned on
     */
    private static final boolean DEBUG = true;

    /*
    This contains the info of the file where the servers public key is writen to
     */
    private static final String publicKeyStorageFileName = "src/src-concurrent-server-client/src/Client/publicKeyStorageFile";

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The server manager. Its responsible for dispatching the serverThreads that handle client requests according to demand.
     */
    private static Server serverManager;

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    /**
     * The main method that starts the server and the client simulation according to the parameters specified in caso 3.
     * @param args the arguments of the program
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {

        //Record data
        //TODO: Ver que metricas me toca keep track of toca configurar todo para que se generen reportes de todos los datos que toque recolectar

        //Firsts it has to initialize the server
        serverManager = new Server(publicKeyStorageFileName,DEBUG);

    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------
    //TODO: Hacer los metodos de procesamiento de datos y metricas

}
