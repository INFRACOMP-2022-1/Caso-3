package Client;

import SecurityUtils.*;
import StatusRequests.PackageStatusRequests;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static SecurityUtils.ByteUtils.byte2str;

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
    If debug mode is turned on
     */
    public boolean debug;

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

    /*
    The current number of requests that have been done
     */
    private static int numberOfRequestsDone = 0;



    //----------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------

    /**
     * The client constructor. It manages the creation of client threads.
     * Each client makes a request to the server, which will dispatch a thread in order to deal with his petition.
     * @param publicKeyStorageFileName the file where the servers public key is stored
     * @param clientRequestsNumber the number of clients that are going to be making a petition to the server. It will also correspond to the number of created server threads to deal with the petitions.
     * @param packageStatusRequestList the list that contains the information for making the client requests that each thread will be doing (username, packageId)
     * @param debug if the debug mode is turned on
     */
    public Client(String publicKeyStorageFileName, int clientRequestsNumber, ArrayList<PackageStatusRequests> packageStatusRequestList,boolean debug) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        System.out.println("Im the client");

        //Sets if debug mode is on
        this.debug = debug;

        //Stores the file name where the public key is going to be retreived from
        Client.publicKeyStorageFileName = publicKeyStorageFileName;

        //The number of clients that need to be created to fullfil all the requests
        Client.clientRequestsNumber = clientRequestsNumber;

        //Read the servers public key from the storage file
        serverPublicKey = readServerPublicKeyFromFile();



        //ResponseArray
        ArrayList<String> responseList = new ArrayList<>();

        //As this is the iterative version we will do the requests one by one
        for(int i = 0; i< clientRequestsNumber;i++){
            //Creates the socket that the client is going to be attached to
            Socket socketToServer = new Socket(HOST, PORT);

            PackageStatusRequests currentPackageStatusRequest = packageStatusRequestList.get(i);

            //Gets the thread colour that is visible when debug mode is turned on
            String threadColour = getColour(i);

            //In this form the requests are done iteratively and each request is fulfilled in the order that they are in the list
            String status = clientProtocol(socketToServer,currentPackageStatusRequest,threadColour);

            //Put response in responseList
            String response = String.format("Package request with username %s and package id %d had status %s" ,currentPackageStatusRequest.getUsername(),currentPackageStatusRequest.getPackageId(),status );
            responseList.add(response);
        }

        //Prints out the response list
        System.out.println(responseList);
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
     * Reads the public key from the file where the server stored the serialized object.
     * @return The server public key
     */
    public PublicKey readServerPublicKeyFromFile() throws IOException, ClassNotFoundException {
        //Creates file input stream for the file with the public key
        FileInputStream file = new FileInputStream(publicKeyStorageFileName);

        //Creates a object input stream to be able to read from the file
        ObjectInputStream objectInputStream = new ObjectInputStream(file);

        //Retrieves the public key stored in the file and returns it
        return (PublicKey) objectInputStream.readObject();
    }

    /**
     * Gets a colour for the thread based on its number in the for loop
     * @param i the number of the request being sent
     * @return String with the colour for the thread
     */
    public String getColour(int i){
        //Colour array
        ArrayList<String> colourArray = new ArrayList<>();

        //String colours
        String TEXT_RED = "\u001B[31m";
        colourArray.add(TEXT_RED);
        String TEXT_GREEN = "\u001B[32m";
        colourArray.add(TEXT_GREEN);
        String TEXT_YELLOW = "\u001B[33m";
        colourArray.add(TEXT_YELLOW);
        String TEXT_BLUE = "\u001B[34m";
        colourArray.add(TEXT_BLUE);
        String TEXT_PURPLE = "\u001B[35m";
        colourArray.add(TEXT_PURPLE);
        String TEXT_CYAN = "\u001B[36m";
        colourArray.add(TEXT_CYAN);

        //Returns colour for string based on the parameter i modulo 6
        return colourArray.get(i%6);
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
        byte[] usernameByteArray = username.getBytes();
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
        byte[] packageIdByteArray = packageIdStr.getBytes(StandardCharsets.UTF_8);
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
     * @return String with the 24-digit number that corresponds to the decrypted reto.
     */
    public String decryptServerRetoWithPublicKey(String encryptedServerReto, PublicKey publicKeyServer) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedRetoWithPublicKeyByteArray = ByteUtils.str2byte(encryptedServerReto);

        //Decrypts the reto with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithPublicKey(encryptedRetoWithPublicKeyByteArray,publicKeyServer);

        //Converts decrypted byte array to Long
        return new String(decryptedReto, StandardCharsets.UTF_8);
    }

    /**
     * Decrypts the reto sent by the server using the shared secret key.
     * @param encryptedServerReto the reto sent by the server (It's in string format)
     * @return String with the 24-digit number that corresponds to the decrypted reto.
     */
    public String decryptServerRetoWithSymmetricKey(String encryptedServerReto, SecretKey secretKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedRetoWithPublicKeyByteArray = ByteUtils.str2byte(encryptedServerReto);

        //Decrypts the reto with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithSymmetricKey(encryptedRetoWithPublicKeyByteArray,secretKey);

        //Converts decrypted byte array to Long
        return new String(decryptedReto, StandardCharsets.UTF_8);
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
        return new String(decryptedReto, StandardCharsets.UTF_8);
    }

    //----------------------------------------------------------------------
    // HASHING AND AUTHENTICATION CODES
    //----------------------------------------------------------------------

    /**
     * Calculates the HMAC (Authentication code) for a given digest
     * @return String containing the bytes for the HMAC (hash) of the digest
     */
    public String calculateHMACofDigest(SecretKey secretKey,String status){
        //Calculate the digest
        String digest = createDigest(status);

        //Converts to byte array
        byte[] digestByteArray = digest.getBytes(StandardCharsets.UTF_8);

        //Gets HMAC of digest
        byte[] authenticationCodeHMAC = HashingAndAuthCodes.signWithHMAC(digestByteArray,secretKey);

        //Since there are problems with byte transmission through sockets the encrypted authentication code byte array is converted to a string
        return byte2str(authenticationCodeHMAC);
    }

    /**
     * Creates a message digest using the status response on a package
     * @return a String containing the bytes of the message digest
     */
    public String createDigest(String status){
        //Converts to byte array
        byte[] responseByteArray = status.getBytes(StandardCharsets.UTF_8);

        //Gets Message Digest
        byte[] messageDigest = HashingAndAuthCodes.getMessageDigest(responseByteArray);

        //Since there are problems with byte transmission through sockets the encrypted authentication code byte array is converted to a string
        return byte2str(messageDigest);
    }


    //----------------------------------------------------------------------
    // CLIENT PROTOCOL
    //----------------------------------------------------------------------

    /**
     *
     * @param serverSocket It represents the connection to the server, it has all his info in such a way we can send information to it.
     * @param packageStatusRequest The username and package id that are going to be searched
     * @return String containing the package status if the package exists. A string containing "ERROR" if the package doesn't exists or there was an error in the procedure.
     */
    public String clientProtocol(Socket serverSocket , PackageStatusRequests packageStatusRequest,String threadColour){

        //The secret key for asymmetric encryption between the client and the server (LS)
        SecretKey secretKey;

        //The reto is a 24-digit number that is sent to the server
        String reto;

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
            if(debug){
                System.out.println(threadColour+"SENT INICIO");
            }

            //WAITS TO RECEIVE ACK FROM SERVER
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RECEIVED ACK");
            }

            //GENERATES THE RETO (24-digit random number) AND SENDS IT TO THE SERVER IN PLAIN TEXT
            reto = generateReto();
            sendMessage(reto,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT RETO " + reto);
            }

            //WAIT FOR THE SERVER TO ENCRYPT THE RETO AND SEND IT
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }

            //DECRYPT SENT RETO
            String serverReto = decryptServerRetoWithPublicKey(currentReceivedMessage,serverPublicKey);
            if(debug){
                System.out.println(threadColour+"RECEIVED ENCRYPTED RETO " + currentReceivedMessage);
            }
            if(debug){
                System.out.println(threadColour+"DECRYPTED RECEIVED RETO IS " + serverReto);
            }

            //VALIDATE IF SERVER_RETO CORRESPONDS TO THE ORIGINALLY CALCULATED RETO
            if(!serverReto.equals(reto)){
                //If the decrypted server reto isn't the same as the original reto the communication to the server should end
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RETO VALIDATION RETURNED " + Objects.equals(serverReto, reto));
            }

            //GENERATE THE SECRET KEY (SYMMETRIC KEY, LS)
            secretKey = KeyGenerators.generateSecretKeyLS();
            if(debug){
                System.out.println(threadColour+"GENERATED SECRET KEY IS " + secretKey);
            }

            //ENCRYPT THE SECRET KEY/LS WITH THE SERVERS PUBLIC KEY -> LS'=C(K_S+,LS)
            String encryptedKey = encryptSecretKeyWithPublicKey(secretKey,serverPublicKey);
            sendMessage(encryptedKey,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"ENCRYPTED SECRET KEY IS " + encryptedKey);
            }

            //WAIT FOR SERVER TO EXTRACT SECRET KEY AND SEND ACK MESSAGE
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RECEIVE ACK");
            }

            //ENCRYPT THE USERNAME ASSOCIATED TO THE SEARCHED PACKAGE AND SENT IT TO THE SERVER
            String encryptedUsername =encryptUsernameWithPublicKey(username,serverPublicKey);
            sendMessage(encryptedUsername,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ENCRYPTED USERNAME " + encryptedUsername);
            }

            //WAIT FOR THE SERVER TO SEARCH IN THE RECORD TABLE FOR THE USERNAME

            //Note: This will cover for the case that the server sends error because it couldn't find that username
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RECEIVED ACK");
            }

            //ENCRYPT THE PACKAGE ID ASSOCIATED TO THE SEARCHED PACKAGE AND SEND IT TO THE SERVER
            String encryptedPackageId =encryptPackageIdWithSymmetricKey(packageId,secretKey);
            sendMessage(encryptedPackageId,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ENCRYPTED PACKAGE ID " + encryptedPackageId);
            }

            //WAIT FOR THE SERVER TO SEARCH FOR THE PACKAGE ID
            if((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ERROR")){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }


            //DECRYPT STATUS(response) ASSOCIATED TO THE SEARCHED PACKAGE
            status = decryptPackageStatusWithSymmetricKey(currentReceivedMessage,secretKey);
            if(debug){
                System.out.println(threadColour+"RECEIVED ENCRYPTED STATUS MESSAGE " + currentReceivedMessage);
                System.out.println(threadColour+"UNENCRYPTED STATUS MESSAGE AS " + status);
            }

            //SEND ACK
            sendMessage("ACK",outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ACK ");
            }

            //WAIT FOR SERVER TO GENERATE THE DIGEST AND SEND IT IN AN HMAC
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                if(debug){
                    System.out.println(threadColour+"ERROR SOMETHING WENT WRONG");
                }
                return "ERROR";
            }

            //RECEIVE HMAC, CALCULATE DIGEST USING STATUS (response)
            //Note: currentReceivedMessage is the HMAC in string format at this point

            //Gets byte array of currentReceivedMessage , that in this case is the HMAC sent by the server
            String hmacDigestByteArrayServer = currentReceivedMessage;
            if(debug){
                System.out.println(threadColour+"RECEIVED HMAC DIGEST AS" + hmacDigestByteArrayServer);
            }

            //Calculate the digest using status
            String hmacDigestByteArrayLocal = calculateHMACofDigest(secretKey,status);
            if(debug){
                System.out.println(threadColour+"CALCULATED LOCAL HMAC DIGEST AS" + hmacDigestByteArrayLocal);
            }

            //COMPARE SERVER DIGEST TO LOCALLY GENERATED DIGEST
            if(!hmacDigestByteArrayLocal.equals(hmacDigestByteArrayServer)){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"HMAC COMPARISON RESULTS ARE " + (hmacDigestByteArrayLocal.equals(hmacDigestByteArrayServer)));
            }

            //SEND "TERMINAR" TO END PROTOCOL
            sendMessage("TERMINAR",outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT TERMINAR");
            }

            //PRINT MESSAGE IN CONSOLE
            System.out.println(status);

            //Updates the number of requests done
            /*
            numberOfRequestsDone+=1;
             */
            //Check if all the requests have been made and close the socket
            /*
            if(numberOfRequestsDone==clientRequestsNumber){
                closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);
            }
            */

            closeAllConnectionsToServer(incomingMessageChanel,outgoingMessageChanel,serverSocket);

            return status;


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "ERROR"; //If the protocol gets here there was an error
    }


}
