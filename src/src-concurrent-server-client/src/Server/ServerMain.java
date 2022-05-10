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
    Default running mode
     */
    private static final int DEFAULT = 0;

    /*
    Testing Asymmetric running mode
     */
    private static final int TEST_ASYMMETRIC = 1;

    /*
    Testing Symmetric running mode
     */
    private static final int TEST_SYMMETRIC = 2;

    /*
    This contains the info of the file where the servers public key is writen to
     */
    private static final String publicKeyStorageFileName = "src/src-concurrent-server-client/src/Client/publicKeyStorageFile";

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
   If debug is turned on
    */
    private static int MODE = TEST_ASYMMETRIC;

    /*
    If the reto is going to be cyphered SYMMETRICALLY(true) or ASYMMETRICALLY(false)
     */
    private static boolean RETO_SYMMETRIC;

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
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ClassNotFoundException, InterruptedException {

        //Runs serving according to what mode has been selected by default
        if(MODE == DEFAULT){
            runStandardConfiguration();
        }
        else if(MODE == TEST_ASYMMETRIC){
            runAsymmetricRetoTest();
        }
        else if(MODE == TEST_SYMMETRIC){
            runSymmetricRetoTest();
        }
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    private static void runAsymmetricRetoTest() throws NoSuchAlgorithmException, IOException, InterruptedException {
        //Firsts it has to initialize the server
        RETO_SYMMETRIC = false;

        serverManager = new Server(publicKeyStorageFileName,RETO_SYMMETRIC,DEBUG);
    }

    private static void runSymmetricRetoTest() throws NoSuchAlgorithmException, IOException, InterruptedException {
        //Firsts it has to initialize the server
        RETO_SYMMETRIC = true;

        serverManager = new Server(publicKeyStorageFileName,RETO_SYMMETRIC,DEBUG);
    }

    private static void runStandardConfiguration() throws NoSuchAlgorithmException, IOException {
        //Firsts it has to initialize the server
        serverManager = new Server(publicKeyStorageFileName,DEBUG);
    }



}
