package server;

import records.RecordList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.ECField;

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
    public static ServerSocket serverSocket;


    /*
    This is the servers private key. K_S-
     */
    public static long privateKey;

    /*
    This is the servers public key. K_S+
     */
    public static long publicKey;

    /*
    This contains the list to access all the records
     */
    public RecordList recordList;

    //TODO: Toca pensar con mucho cuidado que pasara cuando pasemos esto a tener threads. Porque el servidor tendra un pool of threads para lidiar con cada conexion de cliente. Tener como variable global cietos datos no serviria, no me acuerdo como se manejaria en teoria eso con threads.
    //TODO: https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server en este stack hablan de eso , toca revisarlo para ver que hacemos. Depronto es una buena idea comenzar con threads y no intentar integrarlo mas tarde.
    //Socket clientSocket //goes in servere thread
    //Servers private key (K_S-)
    //Servers public key (K_S+)
    //Shared secret (LS) //won't work having it as an attribute if we use threads and plan to connect to many clients. Goes in ServerThread
    //Reto (r) //won't work having it as a variable if we use threads and plan to connect to many clients. Goes in server thread.
    //Client username (u) //won't work having it as a variable if we use threads and plan to connect to many clients. Goes in serverthread.

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------
    public static void main(String[] args){
        System.out.println("Im the server");

        //TODO: En recordList crear un metodo para automatiamente cargar todos los records de recordTable.csv
        //recordList = new RecordList();
        //recordList.loadTable();

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
            //new ServerThread(socket,privateKey,publicKey,recordList).start();
        }
    }
}
