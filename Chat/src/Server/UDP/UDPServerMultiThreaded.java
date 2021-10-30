/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package Server.UDP;

import util.History;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Main class for the TCP server-side.
 */
public class UDPServerMultiThreaded {

    //    private static List<ServerWritingThread> serverWritingThreadList = new Vector<>();
    private static History history;

    /**
     * Main method of the class.
     *
     * @param args arguments (String[])
     **/
    public static synchronized void main(String args[]) {
        history = new History("historyUDP.ser");

        if (args.length != 3) {
            System.out.println("Usage: java UDPServerMultiThreaded <Server port> <Multicast address> <Multicast port>");
            System.exit(1);
        }

        try {
            int serverPort = Integer.parseInt(args[0]);
            InetAddress multicastAddress = InetAddress.getByName(args[1]);
            int multicastPort = Integer.parseInt(args[2]);

            MulticastSocket multicastSocket = new MulticastSocket();
            ServerWritingThread serverWritingThread = new ServerWritingThread(multicastSocket, multicastAddress, multicastPort, history);
            serverWritingThread.start();

            DatagramSocket listeningSocket = new DatagramSocket(serverPort);
            ServerListeningThread serverListeningThread = new ServerListeningThread(listeningSocket,serverWritingThread, multicastAddress, multicastPort);
            serverListeningThread.start();

            // Saving history at "save" in stdin
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while(true) {
                try {
                    line = stdin.readLine();
                    if(line.equals("save")) {
                        history.saveToFile("historyUDP.ser");
                    }
                    else {
                        System.out.println("Type 'save' in order to save the history of messages");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter of history.
     * @return History.
     */
    public static History getHistorique() {
        return history;
    }
}

  
