/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package Server.TCP;

import java.io.*;
import java.net.*;

/**
 * Class respresenting an echo from the server to the client.
 */
public class EchoServer {

    /**
     * Receives a request from client then sends an echo to the client
     * @param clientSocket Client socket
     **/
    static void doService(Socket clientSocket) {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                socOut.println(line);
                System.out.println(clientSocket.getLocalAddress().toString() + " : " + line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    /**
     * Main method for the class.
     * @param args Arguments (String[])
     */
    public static void main(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("connexion from:" + clientSocket.getInetAddress());
                doService(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }
}

  
