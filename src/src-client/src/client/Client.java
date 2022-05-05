package client;

import java.io.IOException;
import java.net.Socket;

/**
 * Client
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Client {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the server socket. It represents the connection to the server, it has all his info in such a way we can send information to it.
     */
    Socket serverSocket;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    public void closeAllConnectionsToServer() throws IOException {
        //incomingMessageChanel.close();
        //outgoingMessageChanel.close();
        //clientSocket.close();
    }

    //----------------------------------------------------------------------
    // GETTERS AND SETTERS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    public static void main(String[] args){
        System.out.println("Im the client");
    }
}
