package Client;

import StatusRequests.PackageStatusRequests;
import Utils.ByteUtils;
import Utils.Decryption;

import javax.crypto.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Random;

/**
 * ClientThread represents a client entity that is realizing a request to the server about the state of a package.
 * The dispatched thread will deal with the client side of the established protocol.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class ClientThread extends Thread {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the server socket. It represents the connection to the server, it has all his info in such a way we can send information to it.
     */
    protected Socket serverSocket;

    /*
    The secret key for asymmetric encryption between the client and the server (LS)
     */
    protected SecretKey secretKey;

    /*
    The public key of the server (K_S+)
     */
    protected PublicKey publicKeyServer;

    /*
    The reto is a 24-digit number that is sent to the server
     */
    public Long reto;

    /*
    The username associated to the package that is going to be searched
     */
    public String username;

    /*
    The package id associated to the package that is going to be searched
     */
    public int packageId;

    /*
    The status of the searched package
     */
    public String status;

    /*
    The digest of the status(response) sent by the server
     */
    public String digest;

    /*
    The chanel where the clientThread will be writing the messages that it sends to the server.
     */
    public PrintWriter outgoingMessageChanel;

    /*
    The chanel where the clientThread will be receiving the messages that the server sends to it.
     */
    public BufferedReader incomingMessageChanel;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * The constructor for a client thread. It builds a client thread with a specific request in hand.
     * @param serverSocket the server socket where the client will be conecting to in the server
     * @param publicKeyServer the public key of the server
     */
    public ClientThread(Socket serverSocket , PublicKey publicKeyServer, PackageStatusRequests packageStatusRequest){
        this.serverSocket = serverSocket;
        this.publicKeyServer = publicKeyServer;
        this.username = packageStatusRequest.getUsername();
        this.packageId = packageStatusRequest.getPackageId();
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Closes all the connections to the server
     */
    public void closeAllConnectionsToServer() throws IOException {
        incomingMessageChanel.close();
        outgoingMessageChanel.close();
        serverSocket.close();
    }

    /**
     * Generates the secret key that is going to be used to do symmetric encryption between the client and the server
     * @return SecretKey object that has the created symmetric key.
     */
    public SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        String CIPHER_AES = "AES";
        int SECRET_KEY_SIZE = 256;

        KeyGenerator kg = KeyGenerator.getInstance(CIPHER_AES);
        kg.init(SECRET_KEY_SIZE);
        return kg.generateKey();
    }

    /**
     * Sends a message string to the server. It's a generic method.
     * @param message String containing the message to be sent to the server.
     */
    public void sendMessage(String message){
        outgoingMessageChanel.println(message);
    }

    /**
     * Generates the 24-digit reto randomly
     * @return String that represents a 24 digit random number
     */
    public String generateReto(){

        //This is in charge of generating a 24 character string composed of numbers
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 24; i++) {
            str.append(random.nextInt(10));
        }

        //Gets the string format and the long format of the reto
        String retoStr = str.toString();
        reto = Long.parseLong(retoStr);//stores long reto in its corresponding attribute

        //This is in charge returning the 24 numeric string
        return retoStr;
    }

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    /**
     * Decrypts the reto sent by the server using the servers public key.
     * @param encryptedServerReto the reto sent by the server (It's in string format)
     * @return Long with the 24-digit number that corresponds to the decrypted reto.
     */
    public Long decryptServerRetoWithPublicKey(String encryptedServerReto) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedRetoWithPublicKeyByteArray = ByteUtils.str2byte(encryptedServerReto);

        //Decrypts the reto with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithPublicKey(encryptedRetoWithPublicKeyByteArray,publicKeyServer);

        //Converts decrypted byte array to Long
        return Long.parseLong(ByteUtils.byte2str(decryptedReto));
    }

    //----------------------------------------------------------------------
    // RUN
    //----------------------------------------------------------------------

    public void run(){
        try {
            /*
            Opens the reading and writing channels to read and write to the server
             */
            outgoingMessageChanel = new PrintWriter(serverSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            //Stores the current message read
            String currentReceivedMessage;

            /*
            CLIENT PROTOCOL
             */

            //SENDS THE "INICIO" MESSAGE TO THE SERVER, STARTS THE PROTOCOL
            sendMessage("INICIO");

            //WAITS TO RECEIVE ACK FROM SERVER
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                closeAllConnectionsToServer();
                return;
            }

            //GENERATES THE RETO (24-digit random number) AND SENDS IT TO THE SERVER IN PLAIN TEXT
            sendMessage(generateReto());

            //WAIT FOR THE SERVER TO ENCRYPT THE RETO AND SEND IT
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer();
                return;
            }

            //DECRYPT SENT RETO
            Long serverReto = unencryptRetoWithPublicKey(currentReceivedMessage);









            //8) Decifrar reto cifrado

            //9) Comparar reto decifrado con el reto original que se mando al servidor

            //10) Generar LS
            secretKey = generateSecretKey();

            //11) Cifrar LS con llave publica del servidor

            //12) Mandar llave secreta a servidor

            //13) Esperar a que el servidor decifre la llave secreta

            //14) Esperar a que el servidor haga "ACK"

            //15) Encriptar nombre de usuario a sacar y mandar nombre de usuario encriptado

            //16) Esperar a que el servidor busque en la tabla de usuarios
            //TODO: Ver protocolo para el caso donde no se encuentra el nombre de usuario

            //17) Esperar a que el servidor haga "ACK"

            //18) Encriptar id del paquete y mandar id del paquete encriptado

            //19) esperar a que el seervidor busque el paquete

            //20) esperar a que busque  el estado del paquete encriptado y esperar a recibir el estado del paquete encriptado

            //21) Decifrar el estado del paquete encriptado con la llave secreta

            //22) Mandar ACK

            //23) esperara a que se genere digest y que se firme

            //24) esperar a que se mande digest con hmac certificado de autentica cion

            //25) Sacar digest, ver si el certificado encaja

            //26) Mandar mensaje de  "TERMINAR"


            closeAllConnectionsToServer();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
