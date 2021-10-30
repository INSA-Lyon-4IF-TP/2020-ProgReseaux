/***
 * TCPClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package Client.TCP;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

/**
 * Main class for the TCP part, client-side.
 */
public class TCPClient {
    /**
     * Main method of the main class.
     * Accepts a connection, receives a message from client then sends an echo to the client
     * @param args Aguments (String[])
     * @throws IOException Exception thrown for I/O.
     */
    public static void main(String[] args) throws IOException {

        Socket serverSocket = null;

        if (args.length != 2) {
            System.out.println("Usage: java TCPClient <Server host> <Server port>");
            System.exit(1);
        }

        try {
            // creation socket ==> connexion
            serverSocket = new Socket(args[0], Integer.parseInt(args[1]));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:" + args[0]);
            System.exit(1);
        }

        System.out.println("Connected to " + serverSocket.getLocalAddress().toString());

        ChatFrame mainFrame = new ChatFrame(serverSocket);
        mainFrame.setVisible(true);

        mainFrame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        mainFrame.dispose();
                    }
                }
        );

        ClientListeningThread listeningThread = new ClientListeningThread(serverSocket, mainFrame);
        listeningThread.start();
        //ClientWritingThread writingThread = new ClientWritingThread(serverSocket);
        //writingThread.start();

//        echoSocket.close();
    }
}


