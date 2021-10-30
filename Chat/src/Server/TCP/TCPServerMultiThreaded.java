/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package Server.TCP;

import util.History;
import util.Message;

import java.net.*;
import java.util.List;
import java.util.Vector;

/**
 * Main class for the TCP server-side.
 */
public class TCPServerMultiThreaded {

	private static List<ServerWritingThread> serverWritingThreadList = new Vector<>();
	private static History history;

    /**
     * Main method of the class.
     *
     * @param args arguments (String[])
     **/
    public static synchronized void main(String args[]) {
        ServerSocket listenSocket;
        history = new History("historyTCP.ser");

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }

        ControlThread controlThread = new ControlThread(history);
        controlThread.start();

        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ServerWritingThread writingThread = new ServerWritingThread(clientSocket, history);
                writingThread.start();
                serverWritingThreadList.add(writingThread);
                ServerListeningThread listeningThread = new ServerListeningThread(clientSocket, writingThread);
                listeningThread.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    /**
     * Getter for the thread list.
     * @return List of ServerWritingThread.
     */
    public static List<ServerWritingThread> getServerWritingThreads() {
        return serverWritingThreadList;
    }

    /**
     * Function to add a message to history.
     * @param message Message to be added.
     */
    public static void addMessageToHistory(Message message) {
        history.addMessage(message);
        //System.out.println(historique.getMessages());
    }
}

  
