package Server;

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
    private static final String publicKeyStorageFileName = "src/src-iterative-server-client/src/Client/publicKeyStorageFile";

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    If debug is turned on
     */
    private static int MODE = DEFAULT;

    /*
    If the reto is going to be cyphered SYMMETRICALLY(true) or ASYMMETRICALLY(false)
     */
    private static boolean RETO_ASYMMETRIC;

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

    /**
     * Runs the asymmetric reto encryption time test
     */
    public static void runAsymmetricRetoTest() throws NoSuchAlgorithmException, IOException {
        //Firsts it has to initialize the list where the times are going to be stored
        ArrayList<Long> retoCypherTimeList = new ArrayList<>();

        //Specify what encryption method will be used for the algorithm (ASYMMETRIC)
        RETO_ASYMMETRIC = true;

        //Launch the server
        serverManager = new Server(publicKeyStorageFileName,retoCypherTimeList,false,DEBUG);

        //Collect data from tests
        collectDataFromTests(retoCypherTimeList);
    }

    /**
     * Runs the symmetric reto encryption time test
     */
    public static void runSymmetricRetoTest() throws NoSuchAlgorithmException, IOException {
        //Firsts it has to initialize the server
        ArrayList<String> responseList = new ArrayList<>();
        ArrayList<Long> retoCypherTimeList = new ArrayList<>();

        //Specify what encryption method will be used for the algorithm (SYMMETRIC)
        RETO_ASYMMETRIC = false;

        //Launch the server
        serverManager = new Server(publicKeyStorageFileName,retoCypherTimeList,false,DEBUG);

        //Collect data from tests
        collectDataFromTests(retoCypherTimeList);
    }

    /**
     * Runs the default configuration of the server as specified in the original protocol.
     * As in the normal protocol, the reto is encrypted asymmetricaly.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void runStandardConfiguration() throws NoSuchAlgorithmException, IOException {
        //By default, the reto is encrypted the servers private key, so ASYMMETRIC
        RETO_ASYMMETRIC = true;

        //Run server
        serverManager = new Server(publicKeyStorageFileName,DEBUG);
    }

    /**
     * Collects data about the cypher times.
     * @param retoCypherTimeList
     */
    public static void collectDataFromTests(ArrayList<Long> retoCypherTimeList){
        //Saves information for symmetric retos
        if(RETO_ASYMMETRIC == true){
            //TODO: SAVE DATA ON CSV
            //TODO: CALCULATE DIFFERENT STATISTICS ON THE RESPONSE TIMES
            //TODO: WRITE AND STORE A MINI REPORT
        }
        else{
            //TODO: SAVE DATA ON CSV
            //TODO: CALCULATE DIFFERENT STATISTICS ON THE RESPONSE TIMES
            //TODO: WRITE AND STORE A MINI REPORT
        }
    }

}
