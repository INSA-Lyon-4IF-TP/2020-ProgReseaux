/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package Server.UDP;

import util.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for Listening server-side, is threaded.
 * @see java.lang.Thread
 */
public class ServerListeningThread extends Thread {

    private final DatagramSocket listeningSocket;
    private String senderName = "anonymous";
    private final ServerWritingThread writingThread;
    private final InetAddress multicastAddress;
    private final int multicastPort;

    /**
     * Constructor given a datagram socket, a writing thread, an adress and a port to connect.
     * @param datagramSocket DatagramSocket
     * @param writingThread ServerWritingThread
     * @param multicastAddress InetAdress (adress)
     * @param multicastPort int (port)
     */
    ServerListeningThread(DatagramSocket datagramSocket, ServerWritingThread writingThread, InetAddress multicastAddress, int multicastPort) {
        this.listeningSocket = datagramSocket;
        this.writingThread = writingThread;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
    }

    /**
     * Runnable aspect of the class.
     * receives a request from client then sends an echo to the client
     * @see java.lang.Runnable
     */
    public synchronized void run() {
        try {
            Message message;
            byte[] data;
            ByteArrayInputStream byteArrayInputStream;
            ObjectInputStream objectInputStream;
            ByteArrayOutputStream byteArrayOutputStream;
            ObjectOutputStream objectOutputStream;
            while (true) {
                try {
                    data = new byte[1000];
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                    listeningSocket.receive(datagramPacket);
                    byteArrayInputStream = new ByteArrayInputStream(data);
                    objectInputStream = new ObjectInputStream(byteArrayInputStream);

                    try {
                        message = (Message) objectInputStream.readObject();
                        System.out.println(message);
                        String content = message.getContent();

                        if(content.equals("[MULTICAST REQUEST]")){
                            Message response = new Message(multicastAddress.getHostAddress() + " " + multicastPort, "[SERVER]");

                            byteArrayOutputStream = new ByteArrayOutputStream();
                            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(response);
                            byte[] responseData = byteArrayOutputStream.toByteArray();

                            listeningSocket.send(new DatagramPacket(responseData, responseData.length, datagramPacket.getAddress(), datagramPacket.getPort()));

                            //Sending history

                            List<Message> history = UDPServerMultiThreaded.getHistorique().getMessages();

                            byteArrayOutputStream = new ByteArrayOutputStream();
                            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(history);
                            byte[] historyData = byteArrayOutputStream.toByteArray();

                            listeningSocket.send(new DatagramPacket(historyData, historyData.length, datagramPacket.getAddress(), datagramPacket.getPort()));

                        }
                        else {
                            writingThread.addMessage(message);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.err.println("Error in EchoServer:" + e);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}

  
