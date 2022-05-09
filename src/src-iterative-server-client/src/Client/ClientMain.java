package Client;

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
    private static final String publicKeyStorageFileName = "src/src-iterative-server-client/src/Client/publicKeyStorageFile";

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

        //This creates the list of requests that are going to be made
        ArrayList<PackageStatusRequests> packageStatusRequestsList = testNConsults(32);

        //Gets the number of clients that need to be created to fulfill all the requests
        numberOfActiveClients = packageStatusRequestsList.size();

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients,packageStatusRequestsList,DEBUG);
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Creates N package consults in the format that is in the recordTable, so all of them should be successful) and stores them in a list
     * @param consultNumber the number of consults to be generated
     * @return An arraylist containing all the consults
     */
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
