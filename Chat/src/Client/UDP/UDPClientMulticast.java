package Client.UDP;

import util.Message;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.List;

import static java.lang.System.exit;

/**
 * Main class for Multicast UDP
 */
public class UDPClientMulticast {

    private static MulticastSocket receiveSocket;
    private static DatagramSocket sendSocket;
    private static InetAddress groupAddress;
    private static int groupPort;

    /**
     * Main method of the main class.
     * accepts a connection, receives a message from client then sends an echo to the client
     * @param args Arguments (String[])
     * @throws IOException Exception thrown.
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: java UDPClient <Server host> <Server port>");
            exit(1);
        }

        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        List<Message> messagesHistory = null;

        try {
            sendSocket = new DatagramSocket();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            Message message = new Message("[MULTICAST REQUEST]", "anonymous");
            objectOutputStream.writeObject(message);

            byte[] dataSent = byteArrayOutputStream.toByteArray();

            DatagramPacket multicastRequest = new DatagramPacket(dataSent, dataSent.length, host, port);

            sendSocket.send(multicastRequest);

            byte[] dataReceived = new byte[1000];
            DatagramPacket multicastResponse = new DatagramPacket(dataReceived, dataReceived.length);

            sendSocket.receive(multicastResponse);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataReceived);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            try {
                message = (Message) objectInputStream.readObject();
                String[] response = message.getContent().split(" ");
                groupAddress = InetAddress.getByName(response[0]);
                groupPort = Integer.parseInt(response[1]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                exit(1);
            }

            dataReceived = new byte[1000];
            DatagramPacket history = new DatagramPacket(dataReceived, dataReceived.length);
            sendSocket.receive(history);

            byteArrayInputStream = new ByteArrayInputStream(dataReceived);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);

            try {
                messagesHistory = (List<Message>) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            exit(1);
        }

        try {
            receiveSocket = new MulticastSocket(groupPort);
            receiveSocket.joinGroup(groupAddress);
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }
        //System.out.println(multicastSocket);

        ChatFrame mainFrame = new ChatFrame(sendSocket, host, port);
        mainFrame.setVisible(true);

        mainFrame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        mainFrame.dispose();
                    }
                }
        );

        assert messagesHistory != null;
        for (Message m : messagesHistory) {
            mainFrame.displayText(m);
        }

//        ClientWritingThread clientWritingThread = new ClientWritingThread(sendSocket, host, port);
//        clientWritingThread.start();
        ClientListeningThread clientListeningThread = new ClientListeningThread(receiveSocket, mainFrame);
        clientListeningThread.start();

        //multicastSocket.leaveGroup(groupAddr);
    }
}