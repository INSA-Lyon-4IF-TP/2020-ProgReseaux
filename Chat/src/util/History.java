package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents an history of all messages. Serializable
 * @see java.io.Serializable
 */
public class History implements Serializable {
    private List<Message> messages;

    /**
     * Default constructor.
     */
    public History() {
        this.messages = new ArrayList<>();
    }

    /**
     * Constructor given an History object.
     * @param history History.
     */
    public History(History history){
        this.messages = new ArrayList<>(history.getMessages());
    }

    /**
     * Getter of a list of messages.
     * @return List of messages of the history.
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Function to get the latest message to be read.
     * @param nbNewMessages number of new messages
     * @return Message.
     */
    public Message getLastMessage(int nbNewMessages) {
        try {
            return messages.get(messages.size()-nbNewMessages);
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function to add a message to history.
      * @param message Message to be added.
     */
    public void addMessage(Message message) {
        this.messages.add(message);
    }

    /**
     * Saves the history to a file.
     * @param filename String of where to save the history.
     */
    public void saveToFile(String filename) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filename));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(this);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor of the history with a file.
     * @param filename String of where the history is stored.
     */
    public History(String filename) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(filename));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            History history_temp = (History) objectInputStream.readObject();
            this.messages = new ArrayList<>(history_temp.getMessages());

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            this.messages = new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
