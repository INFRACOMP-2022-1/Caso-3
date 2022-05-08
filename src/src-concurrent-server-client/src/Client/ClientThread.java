package Client;

import StatusRequests.PackageStatusRequests;
import Utils.*;

import javax.crypto.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Objects;
import java.util.Random;

import static Utils.ByteUtils.byte2str;

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
    If debug mode is turned on
     */
    public boolean debug;

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
    public String reto;

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
     * @param debug if debug mode is turned on
     */
    public ClientThread(Socket serverSocket , PublicKey publicKeyServer, PackageStatusRequests packageStatusRequest,boolean debug){
        this.debug = debug;
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

        //This is in charge returning the 24 numeric string
        return retoStr;
    }

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    /**
     * Encrypts the secret key (LS) using the servers public key.
     * @return String corresponding to the encrypted bytes of the secret key
     */
    public String encryptSecretKeyWithPublicKey(){
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
    public String encryptUsernameWithPublicKey(String username){
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
    public String encryptPackageIdWithSymmetricKey(int packageId){
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
    public String decryptServerRetoWithPublicKey(String encryptedServerReto) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedRetoWithPublicKeyByteArray = ByteUtils.str2byte(encryptedServerReto);

        //Decrypts the reto with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithPublicKey(encryptedRetoWithPublicKeyByteArray,publicKeyServer);

        //Converts decrypted byte array to Long
        return new String(decryptedReto, StandardCharsets.UTF_8);
    }

    /**
     * Decrypts the package status sent by the server using the secret key (LS)
     * @param encryptedPackageStatus the package status sent by the server
     * @return String with the package status (corresponding to the Status enums)
     */
    public String decryptPackageStatusWithSymmetricKey(String encryptedPackageStatus) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Since there are problems with byte transmission through sockets the encrypted username string is converted to a byte array
        byte[] encryptedPackageStatusByteArray = ByteUtils.str2byte(encryptedPackageStatus);

        //Decrypts the package status with decrypt method and returns byte array
        byte[] decryptedReto = Decryption.decryptWithSymmetricKey(encryptedPackageStatusByteArray,secretKey);

        //Converts decrypted byte array to Long
        return new String(decryptedReto, StandardCharsets.UTF_8);
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
            if(debug){
                System.out.println("SENT INICIO");
            }

            //WAITS TO RECEIVE ACK FROM SERVER
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer();
                System.out.println("SENT INICIO");
                return;
            }

            //GENERATES THE RETO (24-digit random number) AND SENDS IT TO THE SERVER IN PLAIN TEXT
            String strReto = generateReto();
            reto = strReto;
            sendMessage(strReto);
            if(debug){
                System.out.println("SENT RETO " + strReto);
            }

            //WAIT FOR THE SERVER TO ENCRYPT THE RETO AND SEND IT
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer();
                return;
            }

            //DECRYPT SENT RETO
            String serverReto = decryptServerRetoWithPublicKey(currentReceivedMessage);
            if(debug){
                System.out.println("RECEIVED ENCRYPTED RETO " + currentReceivedMessage);
            }
            if(debug){
                System.out.println("DECRYPTED RECEIVED RETO IS " + serverReto);
            }

            //VALIDATE IF SERVER_RETO CORRESPONDS TO THE ORIGINALLY CALCULATED RETO
            if(!serverReto.equals(reto)){
                //If the decrypted server reto isn't the same as the original reto the communication to the server should end
                closeAllConnectionsToServer();
                return;
            }
            if(debug){
                System.out.println("RETO VALIDATION RETURNED " + Objects.equals(serverReto, reto));
            }

            //GENERATE THE SECRET KEY (SYMMETRIC KEY, LS)
            secretKey = KeyGenerators.generateSecretKeyLS();
            if(debug){
                System.out.println("GENERATED SECRET KEY IS " + secretKey);
            }

            //ENCRYPT THE SECRET KEY/LS WITH THE SERVERS PUBLIC KEY -> LS'=C(K_S+,LS)
            String encryptedSecretKey = encryptSecretKeyWithPublicKey();
            sendMessage(encryptedSecretKey);
            if(debug){
                System.out.println("ENCRYPTED SECRET KEY IS " + secretKey);
            }

            //WAIT FOR SERVER TO EXTRACT SECRET KEY AND SEND ACK MESSAGE
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer();
                return;
            }

            //ENCRYPT THE USERNAME ASSOCIATED TO THE SEARCHED PACKAGE AND SENT IT TO THE SERVER
            String encryptedUsername = encryptUsernameWithPublicKey(username);
            sendMessage(encryptedUsername);
            if(debug){
                System.out.println("SENT ENCRYPTED USERNAME " + encryptedUsername);
            }

            //WAIT FOR THE SERVER TO SEARCH IN THE RECORD TABLE FOR THE USERNAME

            //Note: This will cover for the case that the server sends error because it couldn't find that username
            if(!((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK"))){
                closeAllConnectionsToServer();
                return;
            }
            if(debug){
                System.out.println("RECEIVED ACK");
            }

            //ENCRYPT THE PACKAGE ID ASSOCIATED TO THE SEARCHED PACKAGE AND SEND IT TO THE SERVER
            String encryptedPackageId = encryptPackageIdWithSymmetricKey(packageId);
            sendMessage(encryptedPackageId);
            if(debug){
                System.out.println("SENT ENCRYPTED PACKAGE ID " + encryptedPackageId);
            }

            //WAIT FOR THE SERVER TO SEARCH FOR THE PACKAGE ID AND ENCRYPT IT
            if((currentReceivedMessage = incomingMessageChanel.readLine()).equals("ERROR")){
                closeAllConnectionsToServer();
                return;
            }
            if(debug){
                if(currentReceivedMessage.equals("ERROR")){
                    System.out.println("RECEIVED ERROR MESSAGE ");
                }
            }


            //DECRYPT STATUS(response) ASSOCIATED TO THE SEARCHED PACKAGE
            status = decryptPackageStatusWithSymmetricKey(currentReceivedMessage);

            if(debug){
                System.out.println("RECEIVED ENCRYPTED STATUS MESSAGE " + currentReceivedMessage);
                System.out.println("UNENCRYPTED STATUS MESSAGE AS" + status);
            }

            //SEND ACK
            sendMessage("ACK");
            if(debug){
                System.out.println("SENT ACK ");
            }

            //WAIT FOR SERVER TO GENERATE THE DIGEST AND SEND IT IN AN HMAC
            if((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                closeAllConnectionsToServer();
                return;
            }


            //RECEIVE HMAC, CALCULATE DIGEST USING STATUS (response)
            //Note: currentReceivedMessage is the HMAC in string format at this point

            //Gets byte array of currentReceivedMessage , that in this case is the HMAC sent by the server
            byte[] hmacDigestByteArrayServer = ByteUtils.str2byte(currentReceivedMessage);
            if(debug){
                System.out.println("RECEIVED HMAC DIGEST AS" + hmacDigestByteArrayServer);
            }

            //Calculate the digest using status
            byte[] hmacDigestByteArrayLocal = HashingAndAuthCodes.getMessageDigest(ByteUtils.str2byte(status));
            if(debug){
                System.out.println("CALCULATED LOCAL HMAC DIGEST AS" + hmacDigestByteArrayLocal);
            }

            //COMPARE SERVER DIGEST TO LOCALLY GENERATED DIGEST
            if(hmacDigestByteArrayLocal != hmacDigestByteArrayServer){
                closeAllConnectionsToServer();
                return;
            }
            if(debug){
                System.out.println("HMAC COMPARISON RESULTS ARE " + (hmacDigestByteArrayLocal == hmacDigestByteArrayServer));
            }

            //SEND "TERMINAR" TO END PROTOCOL
            sendMessage("TERMINAR");
            if(debug){
                System.out.println("SENT TERMINAR");
            }


            //PRINT MESSAGE IN CONSOLE
            System.out.println(status);

            //CLOSES ALL CONNECTIONS TO SERVER
            closeAllConnectionsToServer();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
