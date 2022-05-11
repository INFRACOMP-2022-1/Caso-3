package Server;

import TestSuite.ProcessTestData;
import SecurityUtils.*;
import Records.RecordList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Random;

import static SecurityUtils.ByteUtils.byte2str;

/**
 * Server. It's responsible for dispatching the answering the clients requests.
 * It's an iterative implementation.
 *
 * @author Veronica Escobar
 * @author Santiago Vela
 */
public class Server {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    /*
    The port the server will connect to . Its the logical endpoint of the network connection that is used to exchange information between a server and a client. This is what the client socket will be attached to
     */
    public static final int PORT = 3333;

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    /*
    This is the server socket. It's responsible for listening for any client that wants to establish a connection to the server. It will return a socket with the connection to the client.
     */
    private static ServerSocket serverSocket;

    /*
    This is the servers private key. K_S-
     */
    private static PrivateKey privateKey;

    /*
    This is the servers public key. K_S+
     */
    private static PublicKey publicKey;

    /*
    This contains the list to access all the records
     */
    private static RecordList recordList;

    /*
    This contains the info of the file where the servers public key is writen to
     */
    private static String publicKeyStorageFileName;

    /*
    Boolean that determines if Symmetric Encryption or Asymmetric Encryption is being used for encrypting the reto.
    If True, then Symmetric encryption is being used. If false, then Asymmetric Encryption is being used.
     */
    boolean symmetricRetoCypherMode;


    //----------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------

    /*
    DEFAULT CONFIGURATION
     */
    /**
     * The server constructor. It manages the creation of server threads according to user demand.
     * @param publicKeyStorageFileName the file where the public key is stored so the client has acces to it
     * @param debug if debug mode is turned on or not
     * @throws NoSuchAlgorithmException
     */
    public Server(String publicKeyStorageFileName, boolean debug) throws NoSuchAlgorithmException, IOException {
        System.out.println("Im the server");

        //Generates the private and public key
        KeyPair kp = KeyGenerators.generateKeyPair();
        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();

        //Writes the public key storage file name
        Server.publicKeyStorageFileName = publicKeyStorageFileName;

        //Writes the servers public key to a file accesible by the client
        writePublicKeyToFile();

        //Creates a record list with all the usernames, package ids and statuses
        recordList = new RecordList();
        recordList.load();

        //This socket will hold the endpoint of the network connection with the client. It holds the clients direction and port that the server will be sending information to.
        Socket clientSocket = null;

        //The server socket is created and attached to the given port
        try{
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException e){
            e.printStackTrace();
        }


        //The server will be permanently listening for any incoming connection until its shut off.
        while(true){
            try{
                //Listens for a connection and if there is one it accepts it. This creates a socket that its tied to the client in such a way that the server can communicate with him.
                clientSocket = serverSocket.accept();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            //It sets a thread colour for easier visualization during debug and testing
            Random random = new Random();
            int num = random.ints(0,7).findFirst().getAsInt();
            String threadColour = getColour(num);

            //Iteratively answers each client request
            serverProtocolDefault(clientSocket,privateKey,recordList,debug,threadColour);
        }
    }

    /*
    TEST CONFIGURATION
     */

    /**
     * The server constructor for tests. It manages the creation of server threads according to user demand.
     * @param publicKeyStorageFileName the file where the public key is stored so the client has acces to it
     * @param debug if debug mode is turned on or not
     * @throws NoSuchAlgorithmException
     */
    public Server(String publicKeyStorageFileName,ArrayList<Long> retoCypherTimeList,boolean symmetricRetoCypherMode, boolean debug) throws NoSuchAlgorithmException, IOException {
        System.out.println("Im the server");

        //Generates the private and public key
        KeyPair kp = KeyGenerators.generateKeyPair();
        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();

        //Checks if symmetric reto encryption is to be used
        this.symmetricRetoCypherMode = symmetricRetoCypherMode;

        //Writes the public key storage file name
        Server.publicKeyStorageFileName = publicKeyStorageFileName;

        //Writes the servers public key to a file accesible by the client
        writePublicKeyToFile();

        //Creates a record list with all the usernames, package ids and statuses
        recordList = new RecordList();
        recordList.load();

        //This socket will hold the endpoint of the network connection with the client. It holds the clients direction and port that the server will be sending information to.
        Socket clientSocket = null;

        //The server socket is created and attached to the given port
        try{
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException e){
            e.printStackTrace();
        }


        //The server will be permanently listening for any incoming connection until its shut off.
        while(true){
            try{
                //Listens for a connection and if there is one it accepts it. This creates a socket that its tied to the client in such a way that the server can communicate with him.
                clientSocket = serverSocket.accept();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            //It sets a thread colour for easier visualization during debug and testing
            Random random = new Random();
            int num = random.ints(0,7).findFirst().getAsInt();
            String threadColour = getColour(num);

            //Iteratively answers each client request

            //Runs Symmetric Reto protocol
            if(symmetricRetoCypherMode){
                Long timeElapsed = serverProtocolSymmetricTest(clientSocket,privateKey,recordList,debug,threadColour);
                retoCypherTimeList.add(timeElapsed);
                collectDataFromTests(retoCypherTimeList,symmetricRetoCypherMode);
            }
            //Runs Asymmetric Reto protocol
            else{
                Long timeElapsed = serverProtocolAsymmetricTest(clientSocket,privateKey,recordList,debug,threadColour);
                retoCypherTimeList.add(timeElapsed);
                collectDataFromTests(retoCypherTimeList,symmetricRetoCypherMode);
            }


        }
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    /**
     * Collects data about the cypher times.
     * @param retoCypherTimeList
     */
    public static void collectDataFromTests(ArrayList<Long> retoCypherTimeList,boolean retoSymmetric) throws IOException {
        //Saves information for reto times acording to algorithm
        ProcessTestData.writeToIndividualTestCsv(retoCypherTimeList,retoSymmetric);
        //ProcessTestData.writeToAccumulatedTestCsv(retoCypherTimeList,retoSymmetric);
    }

    /**
     * Writes the serialized public key to a file within the main package for it to be latter accessed by the client.
     */
    public void writePublicKeyToFile() throws IOException {
        //Creates a file output stream with the given file name
        FileOutputStream file = new FileOutputStream(publicKeyStorageFileName);

        //Creates an object output stream anchored to the given file, in such a way that an object can be written.
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(file);

        //Writes the public key to the file
        objectOutputStream.writeObject(publicKey);

        //Closes the object output stream
        objectOutputStream.close();
    }

    /**
     * Sends a message to the client indicating that it has acknowledged ("ACK" in the protocol) the previous received message
     */
    public void acknowledgeClient(PrintWriter outgoingMessageChanel){
        outgoingMessageChanel.println("ACK");
    }

    /**
     * This closes all the connections. It closes the clientSocket, the incomingMessageChannel(BufferedReader) and the outgoingMessageChannel(PrintWriter).
     * This method will be called when the connection is to be terminated, either because of an error in the protocol (like the username or id haven't been found in the table) or when the protocol ends with "TERMINAR"
     * @throws IOException Exception in closing the incomingMessageChannel (BufferedReader)
     */
    public void closeAllConnectionsToClient(PrintWriter outgoingMessageChanel,BufferedReader incomingMessageChanel, Socket clientSocket) throws IOException {
        incomingMessageChanel.close();
        outgoingMessageChanel.close();
        clientSocket.close();
    }

    /**
     * Send message is a general purpose method that is responsible for sending to the client a message given by parameter
     * @param message The string that is to be sent to the client.
     */
    public void sendMessage(String message,PrintWriter outgoingMessageChanel){
        outgoingMessageChanel.println(message);
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
     * The encryption of a reto using the servers private key
     * @param reto 24-digit number originally sent by the client
     * @return String corresponding to the encrypted bytes of the reto
     */
    public String encryptRetoWithPrivateKey(String reto, PrivateKey privateKeyServer){
        //Encrypts byte[] version of the parameter
        byte[] retoByteArray = reto.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedReto = Encryption.encryptWithPrivateKey(retoByteArray, privateKeyServer);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedReto);
    }

    /**
     * The encryption of a reto using the servers private key
     * @param reto 24-digit number originally sent by the client
     * @return String corresponding to the encrypted bytes of the reto
     */
    public String encryptRetoWithSymmetricKey(String reto, SecretKey secretKey){
        //Encrypts byte[] version of the parameter
        byte[] retoByteArray = reto.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedReto = Encryption.encryptWithSymmetricKey(retoByteArray, secretKey);

        //Since there are problems with byte transmission through sockets the encrypted reto byte array is converted to a string
        return byte2str(encryptedReto);
    }

    /**
     * Encrypts a package status using the symmetric key LS
     * @param status the status of the searched package
     * @return String corresponding to the bytes of the encrypted status
     */
    public String encryptPackageStatusWithSymmetricKey(String status, SecretKey sharedSecretKey){
        //Encrypts byte[] version of the parameter
        byte[] statusByteArray = status.getBytes(StandardCharsets.UTF_8);
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
    public String calculateHMACofDigest(String digest,SecretKey sharedSecretKey){
        //Converts to byte array
        byte[] digestByteArray = digest.getBytes(StandardCharsets.UTF_8);

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
        byte[] responseByteArray = response.getBytes(StandardCharsets.UTF_8);

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
    public SecretKey decryptSharedSymmetricKeyWithPrivateKey(String encryptedSharedSymmetricKey, PrivateKey privateKeyServer) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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
    private int decryptPackageIdWithSymmetricKey(String encryptedPackageId, SecretKey sharedSecretKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted package id string is converted to a byte array
        byte[] encryptedPackageIdWithSymmetricKey = ByteUtils.str2byte(encryptedPackageId);

        //Decrypts the package id with decrypt method and returns byte array
        byte[] decryptedPackageId = Decryption.decryptWithSymmetricKey(encryptedPackageIdWithSymmetricKey,sharedSecretKey);

        //Converts decrypted byte array to int
        return Integer.parseInt(new String(decryptedPackageId, StandardCharsets.UTF_8) );
    }

    /**
     * Decrypts the encrypted username using the servers private key
     * @param encryptedUsername the encrypted username bytes in a string format
     * @return String with the unencrypted username
     */
    public String decryptUsernameWithPrivateKey(String encryptedUsername, PrivateKey privateKeyServer) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedUsernameWithSymmetricKey = ByteUtils.str2byte(encryptedUsername);

        //Decrypts the username with decrypt method and returns byte array
        byte[] decryptedUsername = Decryption.decryptWithPrivateKey(encryptedUsernameWithSymmetricKey,privateKeyServer);

        //Converts decrypted byte array to string
        return new String(decryptedUsername, StandardCharsets.UTF_8);
    }

    //----------------------------------------------------------------------
    // SERVER PROTOCOLS
    //----------------------------------------------------------------------

    /**
     * The server side of the communication protocol
     * @param clientSocket This socket will hold the endpoint of the network connection with the client. It holds the clients direction and port that the server will be sending information to.
     * @param privateKeyServer This is the servers private key. K_S-
     * @param debug if debug mode is turned on
     * @param threadColour the colour the debug mode comments are showed in
     * @return
     */
    public String serverProtocolDefault(Socket clientSocket,PrivateKey privateKeyServer,RecordList recordList,boolean debug,String threadColour){

        //The "reto" sent by the client
        String reto;

        //The LS secret key shared by the client and the server
        SecretKey sharedSecretKey;

        //The username given by the client to be searched in the recordList to then be able to ask for package status
        String username;

        //The package id given by the client to be searched in the recordList to then be able to ask for package status
        int packageId;

        //The status of the searched package corresponding to the username and package id
        String status;

        //The digest is a MessageDigest created using the status(response)
        String digest;

        //The chanel where the serverThread will be writing the messages that it sends to the client.
        PrintWriter outgoingMessageChanel;

        //The chanel where the serverThread will be receiving the messages that the client sends to it.
        BufferedReader incomingMessageChanel;

        try{
            outgoingMessageChanel = new PrintWriter(clientSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //The latest message that the server has read from the client.
            String currentReceivedMessage;

            /*
            PROTOCOL BEGINS
             */

            //WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("INICIO")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                return "ERROR";
            }

            //ACKNOWLEDGE CLIENTS INICIO WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ACK");
            }

            //WAIT FOR CLIENT TO GENERATE THE reto
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(threadColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }
                return "ERROR";
            }

            //RECEIVE THE RETO AND SAVE IT

            //Stores the reto in its unencrypted form in the corresponding attribute (long)
            reto = currentReceivedMessage;
            if(debug){
                System.out.println(threadColour+"RECEIVED RETO " + reto);
            }

            //ENCRYPT THE reto USING SERVER PRIVATE KEY AND SEND IT -> reto' = C(K_S-,reto)
            String encryptedReto = encryptRetoWithPrivateKey(reto,privateKeyServer);
            sendMessage(encryptedReto,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ENCRYPTED RETO " + encryptedReto);
            }


            //WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(threadColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }
                return "ERROR";
            }

            //RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')
            sharedSecretKey = decryptSharedSymmetricKeyWithPrivateKey(currentReceivedMessage,privateKeyServer);
            if(debug){
                System.out.println(threadColour+"RECEIVED SECRET KEY " + sharedSecretKey);
            }

            //ACKNOWLEDGE CLIENTS LS WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ACK");
            }

            //WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(threadColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }
                return "ERROR";
            }

            //DECRYPT RECEIVED USERNAME -> username = D(K_S-,username'). SEARCH IF USERNAME IN DATABASE, ACT ACCORDINGLY.
            username = decryptUsernameWithPrivateKey(currentReceivedMessage,privateKeyServer).toString();
            if(!recordList.searchForUsername(username)){
                sendMessage("ERROR",outgoingMessageChanel);
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RECEIVED USERNAME " + username);
            }

            //ACKNOWLEDGE CLIENTS EXISTING USERNAME WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ACK");
            }

            //WAIT FOR CLIENT TO SEND ENCRYPTED PACKAGE ID -> id_pkg' = C(LS,id_pkg)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                return "ERROR";
            }

            //DECRYPT RECEIVED PACKAGE ID -> id = D(LS,id_pkg')
            packageId = decryptPackageIdWithSymmetricKey(currentReceivedMessage,sharedSecretKey);
            if(debug){
                System.out.println(threadColour+"RECEIVED PACKAGE ID " + packageId);
            }

            //SEARCH FOR PACKAGE ASSOCIATED TO USERNAME, ACT ACCORDINGLY.
            if(!recordList.searchForPackageId(packageId)){
                sendMessage("ERROR",outgoingMessageChanel);
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                return "ERROR";
            }

            status = recordList.searchForPackage(username,packageId);
            if(debug){
                System.out.println(threadColour+"FOUND PACKAGE STATUS " + status);
            }

            //ENCRYPT AND SEND PACKAGE STATUS-(response)  -> es' = C(LS,es)
            String encryptedStatus = encryptPackageStatusWithSymmetricKey(status,sharedSecretKey);
            sendMessage(encryptedStatus,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT ENCRYPTED STATUS " + encryptedStatus);
            }

            //WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS(response) AND RECEIVE ACK (from client)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RECEIVED ACK ");
            }

            //GENERATE DIGEST USING THE STATUS(RESPONSE)
            digest = createDigest(status);
            if(debug){
                System.out.println(threadColour+"GENERATED DIGEST " + digest);
            }

            //GET HMAC OF THE DIGEST AND SEND IT TO CLIENT -> HMAC(LS,digest)
            String digestHmac = calculateHMACofDigest(digest,sharedSecretKey);
            sendMessage(digestHmac,outgoingMessageChanel);
            if(debug){
                System.out.println(threadColour+"SENT HMAC OF DIGEST " + digestHmac);
            }

            //WAIT FOR CLIENT TO READ DIGEST INFORMATION AND UNTIL THE CLIENT SENDS "TERMINAL" AND CULMINATE THE THREAD
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("TERMINAR")){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                return "ERROR";
            }
            if(debug){
                System.out.println(threadColour+"RECEIVED TERMINAR ");
            }

            //Return the status
            return status;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "ERROR";//This should never happen
    }


    /**
     * Initiates the server side of the protocol using symmetric encryption for the reto.
     * It modifies the order of the original protocol in such a way that the client has to send the Secret key after Inicio and Ack have been done, the server decrypts and stores the key.
     * Then the client sends the reto and the server decrypts it using the secret key.
     * @param clientSocket the client socket containing the connection to the client
     * @param privateKeyServer the private key of the server
     * @param recordList the list with all the existing records of packages
     * @param debug if debug mode is turned on
     * @param requestColour the colour the output for the request is going to be used
     * @return Long with the time in nanoseconds that it took to encrypt the reto with the symmetric key.
     */
    public Long serverProtocolSymmetricTest(Socket clientSocket,PrivateKey privateKeyServer,RecordList recordList,boolean debug,String requestColour){
        //The "reto" sent by the client
        String reto;

        //The start time at which the reto started to be encrypted
        Long retoEncryptStartTime;

        //The end time at which the reto finished being encrypted
        Long retoEncryptEndTime;

        //The LS secret key shared by the client and the server
        SecretKey sharedSecretKey;

        //The username given by the client to be searched in the recordList to then be able to ask for package status
        String username;

        //The package id given by the client to be searched in the recordList to then be able to ask for package status
        int packageId;

        //The status of the searched package corresponding to the username and package id
        String status;

        //The digest is a MessageDigest created using the status(response)
        String digest;

        //The chanel where the serverThread will be writing the messages that it sends to the client.
        PrintWriter outgoingMessageChanel;

        //The chanel where the serverThread will be receiving the messages that the client sends to it.
        BufferedReader incomingMessageChanel;

        try{
            outgoingMessageChanel = new PrintWriter(clientSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //The latest message that the server has read from the client.
            String currentReceivedMessage;

            /*
            PROTOCOL BEGINS
             */

            //WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("INICIO")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }

            //ACKNOWLEDGE CLIENTS INICIO WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ACK");
            }

            //-----------------------------------SYMMETRIC PROTOCOL-------------------------------------------------------

            //----------------------------
            //RECEIVE THE LS AND SAVE IT
            //----------------------------

            //WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(requestColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }

            }

            //RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')
            sharedSecretKey = decryptSharedSymmetricKeyWithPrivateKey(currentReceivedMessage,privateKeyServer);
            if(debug){
                System.out.println(requestColour+"RECEIVED SECRET KEY " + sharedSecretKey);
            }

            //ACKNOWLEDGE CLIENTS LS WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ACK");
            }

            //----------------------------
            //RECEIVE THE RETO AND SAVE IT
            //----------------------------

            //WAIT FOR CLIENT TO GENERATE THE reto
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(requestColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }
            }

            //Stores the reto in its unencrypted form in the corresponding attribute
            reto = currentReceivedMessage;
            if(debug){
                System.out.println(requestColour+"RECEIVED RETO " + reto);
            }

            //ENCRYPT THE reto USING SYMMETRIC KEY AND SEND IT -> reto' = C(LS,reto)
            retoEncryptStartTime = System.nanoTime();
            String encryptedReto = encryptRetoWithSymmetricKey(reto,sharedSecretKey);
            retoEncryptEndTime = System.nanoTime();
            sendMessage(encryptedReto,outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ENCRYPTED RETO " + encryptedReto);
            }

            //-------------------------------------------------------------------------------------------------------------


            //WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(requestColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }

            }

            //DECRYPT RECEIVED USERNAME -> username = D(K_S-,username'). SEARCH IF USERNAME IN DATABASE, ACT ACCORDINGLY.
            username = decryptUsernameWithPrivateKey(currentReceivedMessage,privateKeyServer).toString();
            if(!recordList.searchForUsername(username)){
                sendMessage("ERROR",outgoingMessageChanel);
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }
            if(debug){
                System.out.println(requestColour+"RECEIVED USERNAME " + username);
            }

            //ACKNOWLEDGE CLIENTS EXISTING USERNAME WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ACK");
            }

            //WAIT FOR CLIENT TO SEND ENCRYPTED PACKAGE ID -> id_pkg' = C(LS,id_pkg)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }

            //DECRYPT RECEIVED PACKAGE ID -> id = D(LS,id_pkg')
            packageId = decryptPackageIdWithSymmetricKey(currentReceivedMessage,sharedSecretKey);
            if(debug){
                System.out.println(requestColour+"RECEIVED PACKAGE ID " + packageId);
            }

            //SEARCH FOR PACKAGE ASSOCIATED TO USERNAME, ACT ACCORDINGLY.
            if(!recordList.searchForPackageId(packageId)){
                sendMessage("ERROR",outgoingMessageChanel);
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }

            status = recordList.searchForPackage(username,packageId);
            if(debug){
                System.out.println(requestColour+"FOUND PACKAGE STATUS " + status);
            }

            //ENCRYPT AND SEND PACKAGE STATUS-(response)  -> es' = C(LS,es)
            String encryptedStatus = encryptPackageStatusWithSymmetricKey(status,sharedSecretKey);
            sendMessage(encryptedStatus,outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ENCRYPTED STATUS " + encryptedStatus);
            }

            //WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS(response) AND RECEIVE ACK (from client)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }
            if(debug){
                System.out.println(requestColour+"RECEIVED ACK ");
            }

            //GENERATE DIGEST USING THE STATUS(RESPONSE)
            digest = createDigest(status);
            if(debug){
                System.out.println(requestColour+"GENERATED DIGEST " + digest);
            }

            //GET HMAC OF THE DIGEST AND SEND IT TO CLIENT -> HMAC(LS,digest)
            String digestHmac = calculateHMACofDigest(digest,sharedSecretKey);
            sendMessage(digestHmac,outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT HMAC OF DIGEST " + digestHmac);
            }

            //WAIT FOR CLIENT TO READ DIGEST INFORMATION AND UNTIL THE CLIENT SENDS "TERMINAL" AND CULMINATE THE THREAD
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("TERMINAR")){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }
            if(debug){
                System.out.println(requestColour+"RECEIVED TERMINAR ");
            }

            //Returns the time it took to encrypt the reto by the server
            return retoEncryptEndTime - retoEncryptStartTime;

        }
        catch (Exception e){
            e.printStackTrace();
        }


        return (long) -1;
    }

    /**
     *
     * @param clientSocket
     * @param privateKeyServer
     * @param recordList
     * @param debug
     * @param requestColour
     * @return
     */
    public Long serverProtocolAsymmetricTest(Socket clientSocket,PrivateKey privateKeyServer,RecordList recordList,boolean debug,String requestColour){
        //The "reto" sent by the client
        String reto;

        //The start time at which the reto started to be encrypted
        Long retoEncryptStartTime;

        //The end time at which the reto finished being encrypted
        Long retoEncryptEndTime;

        //The LS secret key shared by the client and the server
        SecretKey sharedSecretKey;

        //The username given by the client to be searched in the recordList to then be able to ask for package status
        String username;

        //The package id given by the client to be searched in the recordList to then be able to ask for package status
        int packageId;

        //The status of the searched package corresponding to the username and package id
        String status;

        //The digest is a MessageDigest created using the status(response)
        String digest;

        //The chanel where the serverThread will be writing the messages that it sends to the client.
        PrintWriter outgoingMessageChanel;

        //The chanel where the serverThread will be receiving the messages that the client sends to it.
        BufferedReader incomingMessageChanel;

        try{
            outgoingMessageChanel = new PrintWriter(clientSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //The latest message that the server has read from the client.
            String currentReceivedMessage;

            /*
            PROTOCOL BEGINS
             */

            //WAIT FOR CLIENT TO SEND "INICIO" MESSAGE (UNENCRYPTED)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("INICIO")){
                //If the received message is anything different from INICIO then the connection to the client is closed(protocol has not been followed) and the protocol of communication is immediately terminated
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }

            //ACKNOWLEDGE CLIENTS INICIO WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ACK");
            }

            //WAIT FOR CLIENT TO GENERATE THE reto
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(requestColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }

            }

            //RECEIVE THE RETO AND SAVE IT

            //Stores the reto in its unencrypted form in the corresponding attribute (long)
            reto = currentReceivedMessage;
            if(debug){
                System.out.println(requestColour+"RECEIVED RETO " + reto);
            }

            //symmetricRetoCypher

            //ENCRYPT THE reto USING SERVER PRIVATE KEY AND SEND IT -> reto' = C(K_S-,reto)
            retoEncryptStartTime = System.nanoTime();
            String encryptedReto = encryptRetoWithPrivateKey(reto,privateKeyServer);
            retoEncryptEndTime = System.nanoTime();
            sendMessage(encryptedReto,outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ENCRYPTED RETO " + encryptedReto);
            }


            //WAIT FOR CLIENT TO GENERATE SHARED SECRET (LS) AND SEND IT ENCRYPTED WITH THE SERVERS PUBLIC KEY-> LS'=C(K_S+,LS)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(requestColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }

            }

            //RECEIVE ENCRYPTED SHARED SECRET (LS') AND DECRYPT IT -> LS = D(K_S-,LS')
            sharedSecretKey = decryptSharedSymmetricKeyWithPrivateKey(currentReceivedMessage,privateKeyServer);
            if(debug){
                System.out.println(requestColour+"RECEIVED SECRET KEY " + sharedSecretKey);
            }

            //ACKNOWLEDGE CLIENTS LS WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ACK");
            }

            //WAIT FOR USER TO SEND THE ENCRYPTED USERNAME TO BE SEARCHED -> username'=C(K_S+,username)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);
                if(debug){
                    System.out.println(requestColour+"SOMETHING WENT WRONG ERROR MESSAGE RECEIVED!");
                }

            }

            //DECRYPT RECEIVED USERNAME -> username = D(K_S-,username'). SEARCH IF USERNAME IN DATABASE, ACT ACCORDINGLY.
            username = decryptUsernameWithPrivateKey(currentReceivedMessage,privateKeyServer).toString();
            if(!recordList.searchForUsername(username)){
                sendMessage("ERROR",outgoingMessageChanel);
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }
            if(debug){
                System.out.println(requestColour+"RECEIVED USERNAME " + username);
            }

            //ACKNOWLEDGE CLIENTS EXISTING USERNAME WITH "ACK"
            acknowledgeClient(outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ACK");
            }

            //WAIT FOR CLIENT TO SEND ENCRYPTED PACKAGE ID -> id_pkg' = C(LS,id_pkg)
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }

            //DECRYPT RECEIVED PACKAGE ID -> id = D(LS,id_pkg')
            packageId = decryptPackageIdWithSymmetricKey(currentReceivedMessage,sharedSecretKey);
            if(debug){
                System.out.println(requestColour+"RECEIVED PACKAGE ID " + packageId);
            }

            //SEARCH FOR PACKAGE ASSOCIATED TO USERNAME, ACT ACCORDINGLY.
            if(!recordList.searchForPackageId(packageId)){
                sendMessage("ERROR",outgoingMessageChanel);
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }

            status = recordList.searchForPackage(username,packageId);
            if(debug){
                System.out.println(requestColour+"FOUND PACKAGE STATUS " + status);
            }

            //ENCRYPT AND SEND PACKAGE STATUS-(response)  -> es' = C(LS,es)
            String encryptedStatus = encryptPackageStatusWithSymmetricKey(status,sharedSecretKey);
            sendMessage(encryptedStatus,outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT ENCRYPTED STATUS " + encryptedStatus);
            }

            //WAIT FOR CLIENT TO EXTRACT PACKAGE STATUS(response) AND RECEIVE ACK (from client)
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }
            if(debug){
                System.out.println(requestColour+"RECEIVED ACK ");
            }

            //GENERATE DIGEST USING THE STATUS(RESPONSE)
            digest = createDigest(status);
            if(debug){
                System.out.println(requestColour+"GENERATED DIGEST " + digest);
            }

            //GET HMAC OF THE DIGEST AND SEND IT TO CLIENT -> HMAC(LS,digest)
            String digestHmac = calculateHMACofDigest(digest,sharedSecretKey);
            sendMessage(digestHmac,outgoingMessageChanel);
            if(debug){
                System.out.println(requestColour+"SENT HMAC OF DIGEST " + digestHmac);
            }

            //WAIT FOR CLIENT TO READ DIGEST INFORMATION AND UNTIL THE CLIENT SENDS "TERMINAL" AND CULMINATE THE THREAD
            if(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("TERMINAR")){
                closeAllConnectionsToClient(outgoingMessageChanel,incomingMessageChanel,clientSocket);

            }
            if(debug){
                System.out.println(requestColour+"RECEIVED TERMINAR ");
            }

            //Return the elapsed time to encrypt the reto
            return retoEncryptEndTime -retoEncryptStartTime;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //This should never happen
        return (long) -1;
    }



}
