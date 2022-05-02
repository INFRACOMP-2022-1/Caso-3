package server;

import java.net.ServerSocket;
import java.net.Socket;

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

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the server socket. It's responsible for listening for any client that wants to establish a connection to the server. It will return a socket with the connection to the client.
     */
    ServerSocket serverSocket;




    //TODO: Toca pensar con mucho cuidado que pasara cuando pasemos esto a tener threads. Porque el servidor tendra un pool of threads para lidiar con cada conexion de cliente. Tener como variable global cietos datos no serviria, no me acuerdo como se manejaria en teoria eso con threads.
    //TODO: https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server en este stack hablan de eso , toca revisarlo para ver que hacemos. Depronto es una buena idea comenzar con threads y no intentar integrarlo mas tarde.
    //Socket clientSocket //goes in servere thread
    //Servers private key (K_S-)
    //Servers public key (K_S+)
    //Shared secret (LS) //won't work having it as an attribute if we use threads and plan to connect to many clients. Goes in ServerThread
    //Reto (r) //won't work having it as a variable if we use threads and plan to connect to many clients. Goes in server thread.
    //Client username (u) //won't work having it as a variable if we use threads and plan to connect to many clients. Goes in serverthread.

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // GETTERS AND SETTERS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------
    public static void main(String[] args){
        System.out.println("Im the server");
    }
}
