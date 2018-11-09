
import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    // type: 0 = normal message, 1 = logout message
    private int type;
    private String message;

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    } //constructor

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }


}
