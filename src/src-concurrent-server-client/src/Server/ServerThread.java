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
    public long reto;

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

    //TODO: Descripcion
    public String status;

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
     * This method is responsible for sending to the client the servers private key. It does so unencrypted and in plain text.
     */
    public void sendServerPrivateKey(){
        outgoingMessageChanel.println(privateKeyServer);
    }

    /**
     * Sends a message to the client indicating that it has acknowledged ("ACK" in the protocol) the previous received message
     */
    public void acknowledgeClient(){
        outgoingMessageChanel.println("ACK");
    }

    //TODO: Crear metodo para generar el digest
    public String generateDigest(){
        return " ";
    }

    //TODO: Crear metodo para llamar en RecordList el metodo que busque usuarios existentes

    /**
     * Searches for existing users in the record list. If the user exists then its saved as username in the thread and returns true. The contrary its returned false.
     * @param searchedUsername the searched username
     * @return True if searchedUsername exists, false the contary
     */
    public boolean searchForExistingUsers(String searchedUsername){
        if(recordList.searchForUsername(searchedUsername)){
            username = searchedUsername;
            return true;
        }
        return false;
    }

    //TODO: Crear metodo para llamar en RecordList el metodo que busque paquetes existentes (paquetes asociados a usaurios, pero toca antes preguntar si es asi)
    public boolean searchForExistingPackages(String username,int packageId){
        if(recordList.checkIfPackageExists(username,packageId)){
            status = recordList.searchForPackage(username,packageId);
            return true;
        }
        return false;
    }

    /**
     * This closes all the connections. It closes the clientSocket, the incomingMessageChannel(BufferedReader) and the outgoingMessageChannel(PrintWriter).
     * This method will be called when the connection is to be terminated, either because of an error in the protocol (like the username or id haven't been found in the table) or when the protocol ends with "TERMINAR"
     * @throws IOException Exception in closing the incomingMessageChannel (BufferedReader)
     */
    //TODO: Crear metodo para cerrar el socket, el printwriter, el buffer reader
    public void closeAllConnectionsToClient() throws IOException {
        incomingMessageChanel.close();
        outgoingMessageChanel.close();
        clientSocket.close();
    }

    //TODO: Document. This is a general purpose method. As all messages are sent as strings (even if encrypted) then this is a general message sender
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
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public String encryptRetoWithPrivateKey(Long reto) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Encrypts byte[] version of the parameter
        byte[] retoByteArray = ByteUtils.longToBytes(reto);
        byte[] encryptedReto = Encryption.encryptWithPrivateKey(retoByteArray, publicKeyServer);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedReto);
    }

    /**
     * Encrypts a package status using the symmetric key LS
     * @param status the status of the searched package
     * @return String corresponding to the bytes of the encrypted status
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public String encryptPackageStatusWithSymmetricKey(String status) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String calculateHMACofDigest(String digest) throws NoSuchAlgorithmException, InvalidKeyException {
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
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
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
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    private int decryptPackageIdWithSymmetricKey(String encryptedPackageId) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted package id string is converted to a byte array
        byte[] encryptedPackageIdWithSymmetricKey = ByteUtils.str2byte(encryptedPackageId);

        //Decrypts the package id with decrypt method and returns byte array
        byte[] decryptedPackageId = Decryption.decryptWithSymmetricKey(encryptedPackageIdWithSymmetricKey,sharedSecretKey);

        //Converts decrypted byte array to string and then to integer(because package id is an int)
        return Integer.parseInt(ByteUtils.byte2str(decryptedPackageId));
    }

    /**
     * Decrypts the encrypted username using the servers private key
     * @param encryptedUsername the encrypted username bytes in a string format
     * @return String with the unencrypted username
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
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

    public void run(){
        //TODO: Completar el run con todos los protocolos
        //TODO: Cambiar los numero si refleja estado de docs documentandolo
        //TODO: Creo que va a tocar meter un while se este reciviendo input del cliente o algo por el estilo

        //TODO: Iba a implementar la cosa con un while de un tutorial(https://www.baeldung.com/a-guide-to-java-sockets ) pero se ve super sucio entonces usare yieldya que en teoria estariamos manejando multiples conexiones y no me gustaria que se quede bloqueado en un thread mientras que otros esperan. Me toca verificar con harold o geovanny si tengo la idea correcta
        //Como la veo con esto tocaria poner muchos whiles internos
        //Voy a meter un try catch por que se queja con los read lines
        try{
            //The latest message that the server has read from the client.
            String currentReceivedMessage;

            /*
            PROTOCOL BEGINS
             */

            //WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)
            //TODO: Preguntar si se tiene que como hacer algo especial si alguien rompe el protoclo (digamos aqui se le olvido andar inicio al cliente por x o y razon, como que hago solo paro todo?
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("INICIO")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient();
                return;
            }

            //ACKNOWLEDGE CLIENTS INICIO WITH "ACK"
            acknowledgeClient();

            //WAIT FOR CLIENT TO GENERATE THE reto
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient();
                return;
            }

            //RECEIVE THE RETO AND SAVE IT

            //Stores the reto in its unencrypted form in the corresponding attribute (long)
            reto = Long.parseLong(currentReceivedMessage);

            //ENCRYPT THE reto USING SERVER PRIVATE KEY AND SEND IT -> reto' = C(K_S-,reto)
            sendMessage(encryptRetoWithPrivateKey(reto));

            //WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient();
                return;
            }

            //RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')
            sharedSecretKey = decryptSharedSymmetricKeyWithPrivateKey(currentReceivedMessage);

            //ACKNOWLEDGE CLIENTS LS WITH "ACK"
            acknowledgeClient();

            //WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
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
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
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

            //ENCRYPT AND SEND PACKAGE STATUS  -> es' = C(LS,es)
            sendMessage(encryptPackageStatusWithSymmetricKey(status));

            //WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS AND RECEIVE ACK (from client)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient();
                return;
            }

            //23) GENERATE DIGEST WITH HMAC -> HMAC(LS,digest)
            //TODO: SEGUN SANDRA DIGEST ES LO MISMO QUE ESTATUS. NOT REALLY DIGEST ES


            //24) SEND HMAC DIGEST TO CLIENT
            String authCodeHMAC = calculateHMACofDigest(status);

            //25) WAIT FOR CLIENT TO READ DIGEST INFORMATION

            //26) WAIT UNTIL CLIENT SENDS "TERMINAL" AND CULMINATE THE THREAD
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("TERMINAR")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
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
