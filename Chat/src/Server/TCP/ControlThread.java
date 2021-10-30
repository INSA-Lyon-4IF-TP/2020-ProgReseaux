package Server.TCP;

import util.History;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class for a control thread.
 * @see java.lang.Thread
 */
public class ControlThread extends Thread {
    private History history;

    /**
     * Constructor given an history.
     * @param history History
     */
    public ControlThread(History history) {
        this.history = history;
    }

    /**
     * Runnable aspect for the class.
     * @see java.lang.Runnable
     */
    public synchronized void run() {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while(true) {
            try {
                line = stdin.readLine();
                if(line.equals("save")) {
                    history.saveToFile("historyTCP.ser");
                }
                else {
                    System.out.println("Type 'save' in order to save the history of messages");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
