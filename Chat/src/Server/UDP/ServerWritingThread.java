package Server.UDP;

import util.History;
import util.Message;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Class for Writing client-side, is threaded.
 * @see java.lang.Thread
 */
public class ServerWritingThread extends Thread {

    private final MulticastSocket multicastSocket;
    private final InetAddress multicastAddress;
    private final int multicastPort;
    private History history;
    private int newMessages = 0;

    /**
     * Function that adds a message to the history and notifies it.
     * @param message Message to be added.
     */
    public synchronized void addMessage(Message message){
        this.history.addMessage(message);
        ++newMessages;
        this.notify();
    }

    /**
     * Getter for the last message.
     * @return Message
     */
    private synchronized Message getMessage(){
        while(newMessages == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.history.getLastMessage(newMessages--);
    }

    /**
     * Constructor of the class, given a multicast socket, an adress, a port, and an history.
     * @param multicastSocket Multicast socket
     * @param multicastAddress InetAdress (adress)
     * @param multicastPort int (port)
     * @param history History
     */
    ServerWritingThread(MulticastSocket multicastSocket, InetAddress multicastAddress, int multicastPort, History history) {
        this.multicastSocket = multicastSocket;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.history = history;
    }

    /**
     * Runnable aspect of the class.
     * @see java.lang.Runnable
     */
    public synchronized void run() {
        try {
            while (true) {
                Message message = this.getMessage();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(message);
                byte[] data = byteArrayOutputStream.toByteArray();
                multicastSocket.send(new DatagramPacket(data, data.length, multicastAddress, multicastPort));
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}