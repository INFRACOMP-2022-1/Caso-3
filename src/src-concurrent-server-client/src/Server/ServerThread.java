package Server;

import Utils.ByteUtils;
import Utils.Decryption;
import Utils.Encryption;
import Records.RecordList;
import Utils.HashingAndAuthCodes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import static Utils.ByteUtils.byte2str;

/**
 * ServerThread represents a thread created by the server to attend to a clients request for package status.
 * The dispatched thread will deal with the server side of the established protocol.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class ServerThread extends Thread{
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
    protected PrivateKey privateKeyServer;

    /*
    This is the servers public key. K_S+
     */
    protected static PublicKey publicKeyServer;

    /*
    A table containing all the information of usernames,package id's and statuses
     */
    public RecordList recordList;

    /*
    The "reto" sent by the client
     */
    public String reto;

    /*
    The LS secret key shared by the client and the server
     */
    protected SecretKey sharedSecretKey;

    /*
    The username given by the client to be searched in the recordList to then be able to ask for package status
     */
    public String username;

    /*
    The package id given by the client to be searched in the recordList to then be able to ask for package status
     */
    public int packageId;

    /*
    The status of the searched package corresponding to the username and package id
     */
    public String status;

    /*
    The digest is a MessageDigest created using the status(response)
     */
    public String digest;

    /*
    The chanel where the serverThread will be writing the messages that it sends to the client.
    */
    public PrintWriter outgoingMessageChanel;

    /*
    The chanel where the serverThread will be receiving the messages that the client sends to it.
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
     * @param recordList the table that contains all the records of usernames, package id's and statuses
     */
    public ServerThread(Socket clientSocket, PrivateKey privateKeyServer, PublicKey publicKeyServer, RecordList recordList){
        this.clientSocket = clientSocket;
        this.privateKeyServer = privateKeyServer;
        this.publicKeyServer = publicKeyServer;
        this.recordList = recordList;

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

    /**
     * Sends a message to the client indicating that it has acknowledged ("ACK" in the protocol) the previous received message
     */
    public void acknowledgeClient(){
        outgoingMessageChanel.println("ACK");
    }

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

    /**
     * Send message is a general purpose method that is responsible for sending to the client a message given by parameter
     * @param message The string that is to be sent to the client.
     */
    public void sendMessage(String message){
        outgoingMessageChanel.println(message);
    }

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    /**
     * The encryption of a reto using the servers private key
     * @param reto 24-digit number originally sent by the client
     * @return String corresponding to the encrypted bytes of the reto
     */
    public String encryptRetoWithPrivateKey(String reto){
        //Encrypts byte[] version of the parameter
        byte[] retoByteArray = ByteUtils.str2byte(reto);
        byte[] encryptedReto = Encryption.encryptWithPrivateKey(retoByteArray, privateKeyServer);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedReto);
    }

    /**
     * Encrypts a package status using the symmetric key LS
     * @param status the status of the searched package
     * @return String corresponding to the bytes of the encrypted status
     */
    public String encryptPackageStatusWithSymmetricKey(String status){
        //Encrypts byte[] version of the parameter
        byte[] statusByteArray = ByteUtils.str2byte(status);
        byte[] encryptedStatus = Encryption.encryptWithSymmetricKey(statusByteArray,sharedSecretKey);

        //Since there are problems with byte transmission through sockets the encrypted status byte array is converted to a string
        return byte2str(encryptedStatus);
    }

    //----------------------------------------------------------------------
    // AUTHENTICATION CODES AND HASHES
    //----------------------------------------------------------------------

    /**
     * Calculates the HMAC (Authentication code) for a given digest
     * @param digest the digest containing the information about the message (its a hash, calculated with message digest, see createDigest for more information)
     * @return String containing the bytes for the HMAC (hash) of the digest
     */
    public String calculateHMACofDigest(String digest){
        //Converts to byte array
        byte[] digestByteArray = ByteUtils.str2byte(digest);

        //Gets HMAC of digest
        byte[] authenticationCodeHMAC = HashingAndAuthCodes.signWithHMAC(digestByteArray,sharedSecretKey);

        //Since there are problems with byte transmission through sockets the encrypted authentication code byte array is converted to a string
        return byte2str(authenticationCodeHMAC);
    }

    /**
     * Creates a message digest using the status response on a package
     * @param response contains the status of the requested package
     * @return a String containing the bytes of the message digest
     */
    public String createDigest(String response){
        //Converts to byte array
        byte[] responseByteArray = ByteUtils.str2byte(response);

        //Gets Message Digest
        byte[] messageDigest = HashingAndAuthCodes.getMessageDigest(responseByteArray);

        //Since there are problems with byte transmission through sockets the encrypted authentication code byte array is converted to a string
        return byte2str(messageDigest);
    }

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    /**
     * Decrypts the shared symmetric key (Secret, LS) using the servers private key
     * @param encryptedSharedSymmetricKey String version of the encrypted bytes of the symmetric key
     * @return SecretKey object that corresonds to the secret key that will be used to do symmetric encryption
     */
    public SecretKey decryptSharedSymmetricKeyWithPrivateKey(String encryptedSharedSymmetricKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted sharedSymmetricKey string is converted to a byte array
        byte[] encryptedSharedSymmetricKeyByteArray = ByteUtils.str2byte(encryptedSharedSymmetricKey);

        //Decrypts the key with decrypt method and returns byte array
        byte[] decryptedSharedSymmetricKeyByteArray = Decryption.decryptWithPrivateKey(encryptedSharedSymmetricKeyByteArray,privateKeyServer);

        //Converts decrypted byte array to a secret key
        return new SecretKeySpec(decryptedSharedSymmetricKeyByteArray,0,decryptedSharedSymmetricKeyByteArray.length, "AES");
    }

    /**
     * Decrypts the encrypted package id using the servers secret key (symmetric key).
     * @param encryptedPackageId the encrypted package id bytes in a string format
     * @return int corresponding to the encrypted package id.
     */
    private int decryptPackageIdWithSymmetricKey(String encryptedPackageId) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted package id string is converted to a byte array
        byte[] encryptedPackageIdWithSymmetricKey = ByteUtils.str2byte(encryptedPackageId);

        //Decrypts the package id with decrypt method and returns byte array
        byte[] decryptedPackageId = Decryption.decryptWithSymmetricKey(encryptedPackageIdWithSymmetricKey,sharedSecretKey);

        //Converts decrypted byte array to int
        return Integer.parseInt(ByteUtils.byte2str(decryptedPackageId));
    }

    /**
     * Decrypts the encrypted username using the servers private key
     * @param encryptedUsername the encrypted username bytes in a string format
     * @return String with the unencrypted username
     */
    public String decryptUsernameWithPrivateKey(String encryptedUsername) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedPackageIdWithSymmetricKey = ByteUtils.str2byte(encryptedUsername);

        //Decrypts the username with decrypt method and returns byte array
        byte[] decryptedPackageId = Decryption.decryptWithPrivateKey(encryptedPackageIdWithSymmetricKey,privateKeyServer);

        //Converts decrypted byte array to string
        return ByteUtils.byte2str(decryptedPackageId);
    }

    //----------------------------------------------------------------------
    // RUN
    //----------------------------------------------------------------------

    /**
     * The server side of the communication protocol
     */
    public void run(){
        try{
            //The latest message that the server has read from the client.
            String currentReceivedMessage;

            /*
            PROTOCOL BEGINS
             */

            //WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("INICIO")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient();
                return;
            }

            //ACKNOWLEDGE CLIENTS INICIO WITH "ACK"
            acknowledgeClient();

            //WAIT FOR CLIENT TO GENERATE THE reto
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient();
                return;
            }

            //RECEIVE THE RETO AND SAVE IT

            //Stores the reto in its unencrypted form in the corresponding attribute (long)
            reto = currentReceivedMessage;

            //ENCRYPT THE reto USING SERVER PRIVATE KEY AND SEND IT -> reto' = C(K_S-,reto)
            sendMessage(encryptRetoWithPrivateKey(reto));

            //WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient();
                return;
            }

            //RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')
            sharedSecretKey = decryptSharedSymmetricKeyWithPrivateKey(currentReceivedMessage);

            //ACKNOWLEDGE CLIENTS LS WITH "ACK"
            acknowledgeClient();

            //WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient();
                return;
            }

            //DECRYPT RECEIVED USERNAME -> username = D(K_S-,username'). SEARCH IF USERNAME IN DATABASE, ACT ACCORDINGLY.
            username = decryptUsernameWithPrivateKey(currentReceivedMessage).toString();
            if(!recordList.searchForUsername(username)){
                sendMessage("ERROR");
                closeAllConnectionsToClient();
                return;
            }

            //ACKNOWLEDGE CLIENTS EXISTING USERNAME WITH "ACK"
            acknowledgeClient();

            //WAIT FOR CLIENT TO SEND ENCRYPTED PACKAGE ID -> id_pkg' = C(LS,id_pkg)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient();
                return;
            }

            //DECRYPT RECEIVED PACKAGE ID -> id = D(LS,id_pkg')
            packageId = decryptPackageIdWithSymmetricKey(currentReceivedMessage);

            //SEARCH FOR PACKAGE ASSOCIATED TO USERNAME, ACT ACCORDINGLY.
            if(!recordList.searchForPackageId(packageId)){
                sendMessage("ERROR");
                closeAllConnectionsToClient();
                return;
            }

            status = recordList.searchForPackage(username,packageId);

            //ENCRYPT AND SEND PACKAGE STATUS-(response)  -> es' = C(LS,es)
            sendMessage(encryptPackageStatusWithSymmetricKey(status));

            //WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS(response) AND RECEIVE ACK (from client)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                closeAllConnectionsToClient();
                return;
            }

            //GENERATE DIGEST USING THE STATUS(RESPONSE)
            digest = createDigest(status);

            //GET HMAC OF THE DIGEST AND SEND IT TO CLIENT -> HMAC(LS,digest)
            sendMessage(calculateHMACofDigest(digest));

            //25) WAIT FOR CLIENT TO READ DIGEST INFORMATION AND UNTIL THE CLIENT SENDS "TERMINAL" AND CULMINATE THE THREAD
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("TERMINAR")){
                closeAllConnectionsToClient();
                return;
            }

            //Closes all connections to the client,closes the incomingMessageChanel(BufferedReader), the outgoingMessageChannel(PrintWriter)
            closeAllConnectionsToClient();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
