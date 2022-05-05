package server;

import records.Record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;

import static java.lang.System.out;

public class ServerThread extends Thread{

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This socket will hold the endpoint of the network connection with the client. It holds the clients direction and port that the server will be sending information to.
     */
    protected Socket clientSocket;

    /*
    This is the servers private key. K_S-
     */
    public long privateKeyServer;

    /*
    This is the servers public key. K_S+
     */
    public long publicKeyServer;

    /*
    A table containing all the information of usernames,package id's and statuses
     */
    public ArrayList<Record> informationTable;

    /*
    The "reto" sent by the client
     */
    public long reto;

    /*
    The LS secret key shared by the client and the server
     */
    public long sharedSecretKey;

    /*
    The username given by the client to be searched in the recordList to then be able to ask for package status
     */
    public String username;

    /*
    The package id given by the client to be searched in the recordList to then be able to ask for package status
     */
    public String packageId;

    /*
    The chanel where the serverThread will be writing the messages that it sends to the client. Prints formatted representation of objects into a text-output stream. It's the socket who actually sends it, but it makes it easier to use.
     */
    public PrintWriter outgoingMessageChanel;

    /*
    The chanel where the serverThread will be receiving the messages that the client sends to it. It makes it easier to read the messages received.
     */
    public BufferedReader incomingMessageChanel;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * Builds a new server thread with the given client socket and the servers public and private key.
     * It represents the connection that a single client has to the server. And is responsible for executing the protocols for communication.
     * @param clientSocket the socket containing the endpoint of the network connection with the client.
     * @param privateKeyServer the servers private key (K_S-)
     * @param publicKeyServer the servers public key (K_S+)
     * @param informationTable the table that contains all the records of usernames, package id's and statuses
     */
    public ServerThread(Socket clientSocket, long privateKeyServer, long publicKeyServer,ArrayList<Record> informationTable){
        this.clientSocket = clientSocket;
        this.privateKeyServer = privateKeyServer;
        this.publicKeyServer = publicKeyServer;
        this.informationTable = informationTable;

        try{
            outgoingMessageChanel = new PrintWriter(clientSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //TODO: Crear un metodo para mandar al cliente la public key del server (unencrypted)

    /**
     * This method is responsible for sending to the client the servers private key. It does so unencrypted and in plain text.
     */
    public void sendServerPrivateKey(){
        outgoingMessageChanel.println(privateKeyServer);
    }

    //TODO: Crear un metodo solo para mandar 'ACK'

    //TODO: Crear metodo para generar el digest

    //TODO: Crear metodo para llamar en RecordList el metodo que busque usuarios existentes

    //TODO: Crear metodo para llamar en RecordList el metodo que busque paquetes existentes (paquetes asociados a usaurios, pero toca antes preguntar si es asi)

    //TODO: Crear metodo para cerrar el socket, el printwriter, el buffer reader

    /**
     * This closes all the connections. It closes the clientSocket, the incomingMessageChannel(BufferedReader) and the outgoingMessageChannel(PrintWriter).
     * This method will be called when the connection is to be terminated, either because of an error in the protocol (like the username or id haven't been found in the table) or when the protocol ends with "TERMINAR"
     * @throws IOException Exception in closing the incomingMessageChannel (BufferedReader)
     */
    public void closeAllConnectionsToClient() throws IOException {
        incomingMessageChanel.close();
        outgoingMessageChanel.close();
        clientSocket.close();
    }

    //TODO: EN GENERAL CREAR PUROS METODOS PARA MANEJAR CADA PARTE DEL PROTOCOLO

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    //TODO: ENCRYPT MESSAGE WITH LS KEY

    //TODO: ENCRYPT MESSAGE WITH PRIVATE KEY (K_S-)

    //TODO: ENCRYPT MESSAGE USING HMAC

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    //TODO: DECRYPT MESSAGE USANDO PRIVATE KEY

    //TODO: DECRYPT MESSAGE USANDO LS KEY

    //----------------------------------------------------------------------
    // GETTERS AND SETTERS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // RUN
    //----------------------------------------------------------------------

    public void run(){
        //TODO: Completar el run con todos los protocolos
        //TODO: Cambiar los numero si refleja estado de docs documentandolo
        //TODO: Creo que va a tocar meter un while se este reciviendo input del cliente o algo por el estilo

        //TODO: Iba a implementar la cosa con un while de un tutorial(https://www.baeldung.com/a-guide-to-java-sockets ) pero se ve super sucio entonces usare yieldya que en teoria estariamos manejando multiples conexiones y no me gustaria que se quede bloqueado en un thread mientras que otros esperan. Me toca verificar con harold o geovanny si tengo la idea correcta
        //Como la veo con esto tocaria poner muchos whiles internos
        //Voy a meter un try catch por que se queja con los read lines
        try{
            String currentReceivedMessage;//The latest message that the server has read from the client.

            //1) SEND THE SERVERS PUBLIC KEY TO THE CLIENT

            //2) WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)

            //3) ACKNOWLEDGE CLIENTS INICIO WITH "ACK"

            //4&5&6) WAIT FOR CLIENT TO GENERATE THE reto AND SEND IT. SAVE THE reto

            //7) ENCRYPT THE reto USING SERVER PRIVATE KEY -> reto' = C(K_S-,reto)

            //8&9) WAIT FOR CLIENT TO DECRYPT AND CHECK PREVIOUSLY SENT ENCRYPTED reto

            //10&11&12) WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)

            //13) RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')

            //14) ACKNOWLEDGE CLIENTS LS WITH "ACK"

            //15) WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)

            //16) DECRYPT RECEIVED USERNAME -> username = D(K_S-,username'). SEARCH IF USERNAME IN DATABASE, ACT ACCORDINGLY.

            //17) ACKNOWLEDGE CLIENTS EXISTING USERNAME WITH "ACK"

            //18) WAIT FOR CLIENT TO SEND ENCRYPTED PACKAGE ID -> id_pkg' = C(LS,id_pkg)

            //19) DECRYPT RECEIVED PACKAGE ID -> id = D(LS,id_pkg')
            //SEARCH FOR PACKAGE ASSOCIATED TO USERNAME, ACT ACCORDINGLY.
            //ENCRYPT PACKAGE STATUS  -> es' = C(LS,es)

            //20) SEND ENCRYPTED es TO CLIENT

            //21&22) WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS AND RECEIVE ACK (from client)

            //23) GENERATE DIGEST WITH HMAC -> HMAC(LS,digest)

            //24) SEND HMAC DIGEST TO CLIENT

            //25) WAIT FOR CLIENT TO READ DIGEST INFORMATION


            //26) WAIT UNTIL CLIENT SENDS "TERMINAL" AND CULMINATE THE THREAD
            while(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("TERMINAR")){
                Thread.yield();
            }

            //Closes all connections to the client,closes the incomingMessageChanel(BufferedReader), the outgoingMessageChannel(PrintWriter)
            closeAllConnectionsToClient();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
