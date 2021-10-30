package util;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that represents a message. Is serializable for I/O aspects.
 * @see java.io.Serializable
 */
public class Message implements Serializable {

    private String content;
    private String sender;
    private Date date;

    /**
     * Constructor given content and sender.
     * @param content String : content of the message.
     * @param sender String : sender of the message.
     */
    public Message(String content, String sender) {
        this.content = content;
        this.sender = sender;
        this.date = new Date();
    }

    /**
     * Getter content.
     * @return String : content
     */
    public String getContent() {
        return content;
    }

    /**
     * Getter sender.
     * @return String : sender.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Getter date.
     * @return Date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Overrided method to convert to string.
     * @return String.
     */
    @Override
    public String toString() {
        return "[" + date + "] " + sender + "> " + content;
    }
}
