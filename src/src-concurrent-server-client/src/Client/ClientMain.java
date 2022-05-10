package Client;

import Server.Server;
import StatusRequests.PackageStatusRequests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ClientMain {
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

    /*
    This contains the information of how many active clients are to be initialized.
     */
    public static int numberOfActiveClients =32;

    //----------------------------------------------------------------------
    // CONSTANTS
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
    //TODO: Hacer los metodos de procesamiento de datos y metricas
    public static ArrayList<PackageStatusRequests> testNConsults(int consultNumber){
        ArrayList<PackageStatusRequests> packageStatusRequestsArrayList = new ArrayList<>();

        //Generate usernames and ids
        for(int i = 1; i<consultNumber+1;i++){
            String username = "user" + i;
            PackageStatusRequests tempPackage = new PackageStatusRequests(username,i);
            packageStatusRequestsArrayList.add(tempPackage);
        }

        return packageStatusRequestsArrayList;
    }

    /**
     *
     */
    public static void runAsymmetricRetoTest() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        //This creates the list of requests that are going to be made
        ArrayList<PackageStatusRequests> packageStatusRequestsList = testNConsults(numberOfActiveClients);

        //Gets the number of clients that need to be created to fulfill all the requests
        numberOfActiveClients = packageStatusRequestsList.size();

        RETO_SYMMETRIC = false;

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients,packageStatusRequestsList,RETO_SYMMETRIC,DEBUG);
    }

    /**
     *
     */
    public static void runSymmetricRetoTest() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        //This creates the list of requests that are going to be made
        ArrayList<PackageStatusRequests> packageStatusRequestsList = testNConsults(numberOfActiveClients);

        //Gets the number of clients that need to be created to fulfill all the requests
        numberOfActiveClients = packageStatusRequestsList.size();

        RETO_SYMMETRIC = true;

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients,packageStatusRequestsList,RETO_SYMMETRIC,DEBUG);

    }

    /**
     *
     */
    public static void runStandardConfiguration() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        //This creates the list of requests that are going to be made
        ArrayList<PackageStatusRequests> packageStatusRequestsList = testNConsults(numberOfActiveClients);

        //Gets the number of clients that need to be created to fulfill all the requests
        numberOfActiveClients = packageStatusRequestsList.size();

        RETO_SYMMETRIC = false;

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients,packageStatusRequestsList,DEBUG);

    }

}
