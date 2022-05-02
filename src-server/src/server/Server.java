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

    /*
    This is the client socket. It represents the connection to the client, it has all his info in such a way we can send information to it.
    NOTE: IF WE ARE EXPECTING MULTIPLE CONNECTIONS THIS DOESN'T WORK
     */
    Socket clientSocket;

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
