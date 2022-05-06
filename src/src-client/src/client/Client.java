package client;

import Utils.ByteUtils;
import Utils.Decryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Random;

/**
 * Client represents a client that wants to request information on the status of a package through a connection with the server.
 * The client will deal with the client side of the established protocol.
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
    protected static Socket serverSocket;

    //TODO: Documentar
    protected static SecretKey secretKey;

    //TODO: Documentar
    protected static PublicKey publicKeyServer;

    //TODO: Documentar
    public static Long reto;

    //TODO: Documentar
    public String username;

    //TODO: Documentar
    public int packageId;

    //TODO: Documentar
    public String status;

    //TODO: Documentar
    public String digest;

    //TODO: Documentar
    public static PrintWriter outgoingMessageChanel;

    public static BufferedReader incomingMessageChanel;

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    //TODO: Document
    public static void closeAllConnectionsToServer() throws IOException {
        //incomingMessageChanel.close();
        //outgoingMessageChanel.close();
        //clientSocket.close();
    }

    //TODO: Generate Secret Key
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        String CIPHER_AES = "AES";
        int SECRET_KEY_SIZE = 256;

        KeyGenerator kg = KeyGenerator.getInstance(CIPHER_AES);
        kg.init(SECRET_KEY_SIZE);
        return kg.generateKey();
    }

    //TODO: Document
    public static void saveServerPublicKey(String serverPublicKey){
        //byte[] serverPublicKeyByteArray = ByteUtils.
    }



    //TODO: DOCUMENT
    public static void sendMessage(String message){
        outgoingMessageChanel.println(message);
    }

    //TODO:Document
    public static String generateReto(){

        //This is in charge of generating a 24 character string composed of numbers
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 24; i++) {
            str.append(random.nextInt(10));
        }

        //Gets the string format and the long format of the reto
        String retoStr = str.toString();
        reto = Long.parseLong(retoStr);//stores long reto in its corresponding atribute

        //This is in charge returning the 24 numeric string
        return retoStr;
    }

    //----------------------------------------------------------------------
    // ENCRYPTION
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // DECRYPTION
    //----------------------------------------------------------------------

    public Long decryptRetoWithServerPublicKey(String encryptedReto){
        //TODO: REVISAR ESTO POR QUE ME TOCA DESENCRIPTAR CON LA LLAVE PUBLICA Y NO CREO QUE TENGA ESE METODO EN DECRYPTION
        return Long.parseLong("0");
    }

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Im the client");

        try {

            //TODO: Document
            outgoingMessageChanel = new PrintWriter(serverSocket.getOutputStream(),true);
            incomingMessageChanel = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            //TODO: Document
            String currentReceivedMessage;

            //1) Guardar la clave publica del servidor
            while((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                Thread.yield();
            }

            saveServerPublicKey(currentReceivedMessage);

            //2) Manda mensaje de incio
            sendMessage("INICIO");

            //3) Espera mensaje de ACK de parte del servidor
            while(!(currentReceivedMessage = incomingMessageChanel.readLine()).equals("ACK")){
                Thread.yield();
            }

            //4) Generar reto
            String retoStr = generateReto();//By default, the method also saves the long format version of the reto

            //5) Mandar reto
            sendMessage(retoStr);

            //6) Esperar a que el servidor cifre el reto
            //7) Esperar a que el servidro mande el reto cifrado
            while((currentReceivedMessage = incomingMessageChanel.readLine()) == null){
                Thread.yield();
            }

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
