package server;

import Utils.ByteUtils;
import Utils.Decryption;
import Utils.Encryption;
import records.RecordList;
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

    //TODO: Description
    public String digest;

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



    //TODO: EN GENERAL CREAR PUROS METODOS PARA MANEJAR CADA PARTE DEL PROTOCOLO

    //TODO: Document. This is a general purpose method. As all messages are sent as strings (even if encrypted) then this is a general message sender
    public void sendMessage(String message){
        outgoingMessageChanel.println(message);
    }

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    //TODO: ENCRYPT RETOO USING PRIVATE KEY
    public String encryptRetoWithPrivateKey(Long reto) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Encrypts byte[] version of the parameter
        byte[] retoByteArray = ByteUtils.longToBytes(reto);
        byte[] encryptedReto =Encryption.encryptWithPrivateKey(retoByteArray, publicKeyServer);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedReto);
    }

    //TODO: ENCRYPT STATUS WITH SYMMETRIC KEY
    public String encryptPackageStatusWithSymmetricKey(String status) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Encrypts byte[] version of the parameter
        byte[] statusByteArray = ByteUtils.str2byte(status);
        byte[] encryptedStatus = Encryption.encryptWithSymmetricKey(statusByteArray,sharedSecretKey);

        //Since there are problems with byte transmission through sockets the encrypted status byte array is converted to a string
        return byte2str(encryptedStatus);
    }

    //TODO: ENCRYPT DIGEST WITH HMAC
    public String encryptDigestWithHMAC(String digest) throws NoSuchAlgorithmException, InvalidKeyException {
        //Encrypts byte[] version of the parameter
        byte[] digestByteArray = ByteUtils.str2byte(digest);
        byte[] authenticationCodeHMAC = Encryption.signWithHMAC(digestByteArray,sharedSecretKey);

        //Since there are problems with byte transmission through sockets the encrypted authentication code byte array is converted to a string
        return byte2str(authenticationCodeHMAC);
    }

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    //TODO: TOCA PROCESAR EN TODOS LADOS LO DE BYTES

    //TODO: DECRYPT SYMMETRIC KEY USING PRIVATE KEY
    public SecretKey decryptSharedSymmetricKeyWithPrivateKey(String encryptedSharedSymmetricKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted sharedSymmetricKey string is converted to a byte array
        byte[] encryptedSharedSymmetricKeyByteArray = ByteUtils.str2byte(encryptedSharedSymmetricKey);

        //Decrypts the key with decrypt method and returns byte array
        byte[] decryptedSharedSymmetricKeyByteArray = Decryption.decryptWithPrivateKey(encryptedSharedSymmetricKeyByteArray,privateKeyServer);

        //Converts decrypted byte array to a secret key
        //TODO: "REPLACE AES WITH REFERENCE TO KEY GENERATORS
        //TODO: Could use SecretKeySpec(byte[] key,String algorithm) but I found the example with parameters public SecretKeySpec(byte[] key,int offset,int len,String algorithm)
        return new SecretKeySpec(decryptedSharedSymmetricKeyByteArray,0,decryptedSharedSymmetricKeyByteArray.length, "AES");
    }

    //TODO: DECRYPT SYMMETRIC KEY USING SYMMETRIC KEY
    private int decryptPackageIdWithSymmetricKey(String encryptedPackageId) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted package id string is converted to a byte array
        byte[] encryptedPackageIdWithSymmetricKey = ByteUtils.str2byte(encryptedPackageId);

        //Decrypts the package id with decrypt method and returns byte array
        byte[] decryptedPackageId = Decryption.decryptWithSymmetricKey(encryptedPackageIdWithSymmetricKey,sharedSecretKey);

        //Converts decrypted byte array to string and then to integer(because package id is an int)
        return Integer.parseInt(ByteUtils.byte2str(decryptedPackageId));
    }

    //TODO: DECRYPT USERNAME WITH PRIVATE KEY
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
            String currentReceivedMessage;//The latest message that the server has read from the client.

            //1) SEND THE SERVERS PUBLIC KEY TO THE CLIENT
            //TODO: Check con Geovanny si da problema mandar un tipo PublicKey (no string)
            sendServerPrivateKey();

            //2) WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)
            while(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("INICIO")){
                Thread.yield();
            }

            //3) ACKNOWLEDGE CLIENTS INICIO WITH "ACK"
            acknowledgeClient();

            //4&5&6) WAIT FOR CLIENT TO GENERATE THE reto AND SEND IT. SAVE THE reto
            while((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                Thread.yield();
            }

            //Stores the reto in its unencrypted form in the corresponding attribute (long)
            reto = Long.parseLong(currentReceivedMessage);

            //7) ENCRYPT THE reto USING SERVER PRIVATE KEY AND SEND IT -> reto' = C(K_S-,reto)
            String encryptedRetoStr = encryptRetoWithPrivateKey(reto);
            sendMessage(encryptedRetoStr);

            //8&9) WAIT FOR CLIENT TO DECRYPT AND CHECK PREVIOUSLY SENT ENCRYPTED reto

            //10&11&12) WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)
            while((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                Thread.yield();
            }

            //13) RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')
            sharedSecretKey = decryptSharedSymmetricKeyWithPrivateKey(currentReceivedMessage);

            //14) ACKNOWLEDGE CLIENTS LS WITH "ACK"
            acknowledgeClient();

            //15) WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)
            while((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                Thread.yield();
            }

            //16) DECRYPT RECEIVED USERNAME -> username = D(K_S-,username'). SEARCH IF USERNAME IN DATABASE, ACT ACCORDINGLY.
            username = decryptUsernameWithPrivateKey(currentReceivedMessage).toString();
            //TODO: Revisar que toca hacer en este caso en el protocolo
            if(!recordList.searchForUsername(username)){
                closeAllConnectionsToClient();
            }

            //17) ACKNOWLEDGE CLIENTS EXISTING USERNAME WITH "ACK"
            acknowledgeClient();

            //18) WAIT FOR CLIENT TO SEND ENCRYPTED PACKAGE ID -> id_pkg' = C(LS,id_pkg)
            while((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                Thread.yield();
            }

            //19) DECRYPT RECEIVED PACKAGE ID -> id = D(LS,id_pkg')
            //SEARCH FOR PACKAGE ASSOCIATED TO USERNAME, ACT ACCORDINGLY.
            //ENCRYPT PACKAGE STATUS  -> es' = C(LS,es)

            packageId = decryptPackageIdWithSymmetricKey(currentReceivedMessage);
            //TODO: Revisar que toca hacer en los casos en esta parte del protocolo
            status = recordList.searchForPackage(username,packageId);

            //20) SEND ENCRYPTED es TO CLIENT
            String packageStatusStr = encryptPackageStatusWithSymmetricKey(status);
            sendMessage(packageStatusStr);

            //21&22) WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS AND RECEIVE ACK (from client)
            while(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                Thread.yield();
            }

            //23) GENERATE DIGEST WITH HMAC -> HMAC(LS,digest)
            digest = generateDigest();

            //24) SEND HMAC DIGEST TO CLIENT
            String authCodeHMAC = encryptDigestWithHMAC(digest);
            //TODO: Need to send both the digest and the authCodeHMAC

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
