package Client.UDP;

import util.Message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Class for Listening client-side, is threaded.
 * @see java.lang.Thread
 */
public class ClientListeningThread extends Thread {

    private final MulticastSocket multicastSocket;
    private ChatFrame chatFrame;

    /**
     * Constructor of the Listening client-side given a socket and a chat frame
     * @param s Socket
     * @param chatFrame Chat Frame
     */
    ClientListeningThread(MulticastSocket s, ChatFrame chatFrame) {
        this.multicastSocket = s;
        this.chatFrame = chatFrame;
    }

    /**
     * Runnable aspect of the thread.
     * @see java.lang.Runnable
     **/
    public void run() {
        try {
            Message message;
            byte[] data = new byte[1000];
            ByteArrayInputStream byteArrayInputStream;
            ObjectInputStream objectInputStream;

            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                multicastSocket.receive(datagramPacket);

                byteArrayInputStream = new ByteArrayInputStream(data);
                objectInputStream = new ObjectInputStream(byteArrayInputStream);

                try {
                    message = (Message) objectInputStream.readObject();
                    chatFrame.displayText(message);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in ClientListeningThread:" + e);
            e.printStackTrace();
        }
    }
}
