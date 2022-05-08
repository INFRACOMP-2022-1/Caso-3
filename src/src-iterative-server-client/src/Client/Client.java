package Client;

import StatusRequests.PackageStatusRequests;
import Utils.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static Utils.ByteUtils.byte2str;

/**
 * Client class is responsible for initiating and managing all the client requests.
 * It's an iterative implementation.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Client {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The port the client threads will connect to.It's the logical endpoint of the network connection that is used to exchange information between a server and a client. This is what the server socket(Socket) will be attached to
     */
    public static final int PORT = 3333;

    /*
    The host that the client socket is going to be attached to
     */
    public static final String HOST = "127.0.0.1";

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the servers public key. K_S+
     */
    private static PublicKey serverPublicKey;

    /*
    The number of active client threads to be generated to make petitions to the server.
     */
    private static int clientRequestsNumber;

    /*
    The file name for the file where the server public key has been stored
     */
    private static String publicKeyStorageFileName;

    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * The client constructor. It manages the creation of client threads.
     * Each client makes a request to the server, which will dispatch a thread in order to deal with his petition.
     * @param publicKeyStorageFileName the file where the servers public key is stored
     * @param clientRequestsNumber the number of clients that are going to be making a petition to the server. It will also correspond to the number of created server threads to deal with the petitions.
     * @param packageStatusRequestList the list that contains the information for making the client requests that each thread will be doing (username, packageId)
     */
    public Client(String publicKeyStorageFileName, int clientRequestsNumber, ArrayList<PackageStatusRequests> packageStatusRequestList,Socket serverSocket) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        System.out.println("Im the client");

        //Stores the file name where the public key is going to be retreived from
        Client.publicKeyStorageFileName = publicKeyStorageFileName;

        //The number of clients that need to be created to fullfil all the requests
        Client.clientRequestsNumber = clientRequestsNumber;

        //Read the servers public key from the storage file
        serverPublicKey = readPrivateKeyFromFile();

        //Creates the socket that the client is going to be attached to
        Socket socketToServer = new Socket(HOST, PORT);

        //ResponseArray
        ArrayList<String> responseList = new ArrayList<>();

        //As this is the iterative version we will do the requests one by one
        for(int i = 0; i< clientRequestsNumber;i++){
            PackageStatusRequests currentPackageStatusRequest = packageStatusRequestList.get(i);

            //In this form the requests are done iteratively and each request is fulfilled in the order that they are in the list
            String status = clientProtocol(serverSocket,serverPublicKey,currentPackageStatusRequest);

            //Put response in responseList
            String response = String.format("Package request with username %s and package id %d had status %s" ,currentPackageStatusRequest.getUsername(),currentPackageStatusRequest.getPackageId(),status );
            responseList.add(response);
        }

        //Prints out the response list
        System.out.println(responseList);
    }

    //----------------------------------------------------------------------
    // CLIENT PROTOCOL
    //----------------------------------------------------------------------

    /**
     *
     * @param serverSocket It represents the connection to the server, it has all his info in such a way we can send information to it.
     * @param publicKeyServer The public key of the server (K_S+)
     * @param packageStatusRequest The username and package id that are going to be searched
     * @return String containing the package status if the package exists. A string containing "ERROR" if the package doesn't exists or there was an error in the procedure.
     */
    public String clientProtocol(Socket serverSocket , PublicKey publicKeyServer, PackageStatusRequests packageStatusRequest){

        //The secret key for asymmetric encryption between the client and the server (LS)
        SecretKey secretKey;

        //The reto is a 24-digit number that is sent to the server
        Long reto;

        //The username associated to the package that is going to be searched
        String username = packageStatusRequest.getUsername();

        //The package id associated to the package that is going to be searched
        int packageId = packageStatusRequest.getPackageId();

        //The status of the searched package
        String status;

        //The digest of the status(response) sent by the server
        String digest;

        //The chanel where the clientThread will be writing the messages that it sends to the server.
        PrintWriter outgoingMessageChanel;

        //The chanel where the clientThread will be receiving the messages that the server sends to it.
        BufferedReader incomingMessageChanel;

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
            sendMessage("INICIO",outgoingMessageChanel);

            //WAITS TO RECEIVE ACK FROM SERVER
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //GENERATES THE RETO (24-digit random number) AND SENDS IT TO THE SERVER IN PLAIN TEXT
            String retoStr = generateReto();
            reto = Long.parseLong(retoStr);
            sendMessage(retoStr,outgoingMessageChanel);

            //WAIT FOR THE SERVER TO ENCRYPT THE RETO AND SEND IT
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //DECRYPT SENT RETO
            Long serverReto = decryptServerRetoWithPublicKey(currentReceivedMessage,serverPublicKey);

            //VALIDATE IF SERVER_RETO CORRESPONDS TO THE ORIGINALLY CALCULATED RETO
            if(!Objects.equals(serverReto, reto)){
                //If the decrypted server reto isn't the same as the original reto the communication to the server should end
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //GENERATE THE SECRET KEY (SYMMETRIC KEY, LS)
            secretKey = KeyGenerators.generateSecretKeyLS();

            //ENCRYPT THE SECRET KEY/LS WITH THE SERVERS PUBLIC KEY -> LS'=C(K_S+,LS)
            sendMessage(encryptSecretKeyWithPublicKey(secretKey,serverPublicKey),outgoingMessageChanel);

            //WAIT FOR SERVER TO EXTRACT SECRET KEY AND SEND ACK MESSAGE
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //ENCRYPT THE USERNAME ASSOCIATED TO THE SEARCHED PACKAGE AND SENT IT TO THE SERVER
            sendMessage(encryptUsernameWithPublicKey(username,serverPublicKey),outgoingMessageChanel);

            //WAIT FOR THE SERVER TO SEARCH IN THE RECORD TABLE FOR THE USERNAME

            //Note: This will cover for the case that the server sends error because it couldn't find that username
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //ENCRYPT THE PACKAGE ID ASSOCIATED TO THE SEARCHED PACKAGE AND SEND IT TO THE SERVER
            sendMessage(encryptPackageIdWithSymmetricKey(packageId,secretKey),outgoingMessageChanel);

            //WAIT FOR THE SERVER TO SEARCH FOR THE PACKAGE ID
            if((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ERROR")){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //WAIT FOR THE SERVER TO SEARCH FOR THE PACKAGE AND SENDING IT ENCRYPTED
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //DECRYPT STATUS(response) ASSOCIATED TO THE SEARCHED PACKAGE
            status = decryptPackageStatusWithSymmetricKey(currentReceivedMessage,secretKey);

            //SEND ACK
            sendMessage("ACK",outgoingMessageChanel);

            //WAIT FOR SERVER TO GENERATE THE DIGEST AND SEND IT IN AN HMAC
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //RECEIVE HMAC, CALCULATE DIGEST USING STATUS (response)
            //Note: currentReceivedMessage is the HMAC in string format at this point

            //Gets byte array of currentReceivedMessage , that in this case is the HMAC sent by the server
            byte[] hmacDigestByteArrayServer = ByteUtils.str2byte(currentReceivedMessage);

            //Calculate the digest using status
            byte[] hmacDigestByteArrayLocal = HashingAndAuthCodes.getMessageDigest(ByteUtils.str2byte(status));

            //COMPARE SERVER DIGEST TO LOCALLY GENERATED DIGEST
            if(hmacDigestByteArrayLocal != hmacDigestByteArrayServer){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }

            //SEND "TERMINAR" TO END PROTOCOL
            sendMessage("TERMINAR",outgoingMessageChanel);

            //CLOSES ALL CONNECTIONS TO SERVER
            closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);

            //PRINT MESSAGE IN CONSOLE
            System.out.println(status);
            return status;


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "ERROR"; //If the protocol gets here there was an error
    }

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    /**
     * Encrypts the secret key (LS) using the servers public key.
     * @return String corresponding to the encrypted bytes of the secret key
     */
    public String encryptSecretKeyWithPublicKey(SecretKey secretKey,PublicKey publicKeyServer){
        //Encrypts byte[] version of the parameter
        byte[] secretKeyByteArray = secretKey.getEncoded();
        byte[] encryptedSecretKey = Encryption.encryptWithPublicKey(secretKeyByteArray, publicKeyServer);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedSecretKey);
    }

    /**
     * Encrypts the username using the servers public key.
     * @param username username of the package that is being searched
     * @return String corresponding to the encrypted bytes of the username
     */
    public String encryptUsernameWithPublicKey(String username, PublicKey publicKeyServer ){
        //Encrypts byte[] version of the parameter
        byte[] usernameByteArray = ByteUtils.str2byte(username);
        byte[] encryptedUsername = Encryption.encryptWithPublicKey(usernameByteArray, publicKeyServer);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedUsername);
    }

    /**
     * Encrypts the package id using the secret key LS
     * @param packageId package id of the package that is being searched
     * @return String corresponding to the encrypted bytes of the package id
     */
    public String encryptPackageIdWithSymmetricKey(int packageId, SecretKey secretKey){
        //Gets string version of the package id
        String packageIdStr = String.valueOf(packageId);

        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] packageIdByteArray = ByteUtils.str2byte(packageIdStr);
        byte[] encryptedPackageId = Encryption.encryptWithSymmetricKey(packageIdByteArray,secretKey);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedPackageId);
    }

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    /**
     * Decrypts the reto sent by the server using the servers public key.
     * @param encryptedServerReto the reto sent by the server (It's in string format)
     * @return Long with the 24-digit number that corresponds to the decrypted reto.
     */
    public Long decryptServerRetoWithPublicKey(String encryptedServerReto,PublicKey publicKeyServer) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedRetoWithPublicKeyByteArray = ByteUtils.str2byte(encryptedServerReto);

        //Decrypts the reto with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithPublicKey(encryptedRetoWithPublicKeyByteArray,publicKeyServer);

        //Converts decrypted byte array to Long
        return Long.parseLong(ByteUtils.byte2str(decryptedReto));
    }

    /**
     * Decrypts the package status sent by the server using the secret key (LS)
     * @param encryptedPackageStatus the package status sent by the server
     * @return String with the package status (corresponding to the Status enums)
     */
    public String decryptPackageStatusWithSymmetricKey(String encryptedPackageStatus, SecretKey secretKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedPackageStatusByteArray = ByteUtils.str2byte(encryptedPackageStatus);

        //Decrypts the package status with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithSymmetricKey(encryptedPackageStatusByteArray,secretKey);

        //Converts decrypted byte array to Long
        return ByteUtils.byte2str(decryptedReto);
    }
    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Closes all the connections to the server
     */
    public void closeAllConnectionsToServer(BufferedReader incomingMessageChanel, PrintWriter outgoingMessageChanel, Socket serverSocket ) throws IOException {
        incomingMessageChanel.close();
        outgoingMessageChanel.close();
        serverSocket.close();
    }

    /**
     * Sends a message string to the server. It's a generic method.
     * @param message String containing the message to be sent to the server.
     */
    public void sendMessage(String message, PrintWriter outgoingMessageChanel){
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

        //This is in charge returning the 24 numeric string
        return retoStr;
    }

    /**
     * Reads the private key from the file where the server stored the serialized object.
     * @return The server public key
     */
    public PublicKey readPrivateKeyFromFile() throws IOException, ClassNotFoundException {
        //Creates file input stream for the file with the public key
        FileInputStream file = new FileInputStream(publicKeyStorageFileName);

        //Creates a object input stream to be able to read from the file
        ObjectInputStream objectInputStream = new ObjectInputStream(file);

        //Retrieves the public key stored in the file and returns it
        return (PublicKey) objectInputStream.readObject();
    }

}