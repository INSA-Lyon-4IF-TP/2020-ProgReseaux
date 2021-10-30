package Client.TCP;

import util.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Frame for displaying the chat
 * @see java.awt.Frame
 */
public class ChatFrame extends Frame {
    Button validationButton = new Button();
    TextField messageField = new TextField();
    TextArea displayArea = new TextArea();
    Socket serverSocket;

    /**
     * Constructor for the chat frame given a socket.
     * @param serverSocket Socket.
     */
    public ChatFrame(Socket serverSocket) {
        this.serverSocket = serverSocket;
        initialize();
    }

    /**
     * Initialization of the chat frame
     */
    public void initialize() {
        this.setResizable(false);
        this.setLayout(null);
        this.setBackground(Color.white);
        this.setBounds(50, 50, 420, 600);
        this.setTitle("Chat GUI");

        validationButton.setLabel("Envoyer");
        validationButton.setBounds(350, 550, 50, 30);

        messageField.setBounds(20, 550, 330, 30);
        messageField.setBackground(Color.white);
        // messageField.setText("Votre message");

        displayArea.setBounds(20, 40, 380, 500);
        displayArea.setEditable(false);

        this.add(validationButton, null);
        this.add(messageField, null);
        this.add(displayArea, null);


        validationButton.addActionListener(
                // lambda-function for action on validation button
                e -> {
//                    displayText();
                    //Envoyer le message au serveur
                    try {
                        PrintStream socOut = new PrintStream(serverSocket.getOutputStream());
                        String line = messageField.getText();
                        messageField.setText("");
                        socOut.println(line);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
    }

    /**
     * Function to display text.
     * @param message Message to display.
     */
    public void displayText(Message message) {
        displayArea.append(message + "\n");
    }
}
