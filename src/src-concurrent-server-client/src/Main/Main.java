package Main;

import Client.Client;
import Server.Server;
import StatusRequests.PackageStatusRequests;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Main {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    This contains the info of the file where the servers public key is writen to
     */
    private static final String publicKeyStorageFileName = "Client/publicKeyStorage";

    /*
    This contains the information of how many active clients are to be initialized.
     */
    public static int numberOfActiveClients;

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
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        //Create public key file on Client module
        //TODO: Fix esto
        //File file = new File(publicKeyStorageFileName);
        //file.createNewFile();

        //Record data
        //TODO: Ver que metricas me toca keep track of toca configurar todo para que se generen reportes de todos los datos que toque recolectar

        //Firsts it has to initialize the server
        serverManager = new Server(publicKeyStorageFileName);

        //TODO: THIS WILL NOT BE THE FULL TESTING INTERFACE, WHEN I GET TO TESTS MAYBE CREATE A METOD TO DELEGATE THIS
        //TODO: Should do a check that ensures that there are no repeated user ids
        //This creates the list of requests that are going to be made
        ArrayList<PackageStatusRequests> packageStatusRequestsList = new ArrayList<>();
        PackageStatusRequests p1 = new PackageStatusRequests("user1",1);
        packageStatusRequestsList.add(p1);
        PackageStatusRequests p2 = new PackageStatusRequests("user2",2);
        packageStatusRequestsList.add(p2);
        PackageStatusRequests p3 = new PackageStatusRequests("user3",3);
        packageStatusRequestsList.add(p3);
        PackageStatusRequests p4 = new PackageStatusRequests("user4",4);
        packageStatusRequestsList.add(p4);
        PackageStatusRequests p5 = new PackageStatusRequests("user5",5);
        packageStatusRequestsList.add(p5);

        //Gets the number of clients that need to be created to fulfill all the requests
        numberOfActiveClients = packageStatusRequestsList.size();

        //Then it has to initialize a given number of clients and make each client thread run
        clientManager = new Client(publicKeyStorageFileName,numberOfActiveClients,packageStatusRequestsList);
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------
    //TODO: Hacer los metodos de procesamiento de datos y metricas

}
