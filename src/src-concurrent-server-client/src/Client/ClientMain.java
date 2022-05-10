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
    This contains the info of the file where the servers public key is writen to
     */
    private static final String publicKeyStorageFileName = "src/src-concurrent-server-client/src/Client/publicKeyStorageFile";

    /*
    This contains the information of how many active clients are to be initialized.
     */
    public static int numberOfActiveClients;

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

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

        //Record data
        //TODO: Ver que metricas me toca keep track of toca configurar todo para que se generen reportes de todos los datos que toque recolectar


        //TODO: THIS WILL NOT BE THE FULL TESTING INTERFACE, WHEN I GET TO TESTS MAYBE CREATE A METOD TO DELEGATE THIS
        //TODO: Should do a check that ensures that there are no repeated user ids
        //This creates the list of requests that are going to be made
        ArrayList<PackageStatusRequests> packageStatusRequestsList = testNConsults(3);

        //Gets the number of clients that need to be created to fulfill all the requests
        numberOfActiveClients = packageStatusRequestsList.size();

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients,packageStatusRequestsList,DEBUG);
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

}
