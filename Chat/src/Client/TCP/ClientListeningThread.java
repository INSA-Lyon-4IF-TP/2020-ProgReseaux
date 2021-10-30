package Client.TCP;

import util.Message;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Class for Listening client-side, is threaded.
 * @see java.lang.Thread
 */
public class ClientListeningThread extends Thread {
    private Socket serverSocket;
    private ChatFrame chatFrame;

    /**
     * Constructor of the Listening client-side given a socket and a chat frame
     * @param s Socket
     * @param chatFrame Chat Frame
     */
    ClientListeningThread(Socket s, ChatFrame chatFrame) {
        this.serverSocket = s;
        this.chatFrame = chatFrame;
    }

    /**
     * Runnable aspect of the thread.
     * @see java.lang.Runnable
     **/
    public void run() {
        try {
            ObjectInputStream socIn = new ObjectInputStream (serverSocket.getInputStream());
            while (true) {
                Message message = (Message) socIn.readObject();
                chatFrame.displayText(message);
            }
        } catch (Exception e) {
            System.err.println("Error in ClientListeningThread:" + e);
            e.printStackTrace();
        }
    }
}
