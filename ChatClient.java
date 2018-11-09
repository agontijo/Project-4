import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client for the chat program.
 * @author Alice Gontijo Goulart Leite, Tae Yoon Kim
 * @version 2018-11-09
 */

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    //Constructors
    //-----------------------------------------------------------------------------
    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    private ChatClient() {
        this("localHost", 1500, "Anonymous");
    }

    private ChatClient(String username) {
        this("localHost", 1500, username);
    }

    private ChatClient(int port, String username) {
        this("localHost", port, username);
    }
    //-----------------------------------------------------------------------------


    /*
     * This starts the Chat Client
     */
    private boolean start() {

        try {
            //create a socket to connect to the server and its port
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        String server;
        String username;
        String port;

        // Get proper arguments and override defaults
        Scanner scanner = new Scanner(System.in);

        // Get Username, Port number, and Server Address
        while (true) {
            System.out.println("Input username: ");
            username = scanner.nextLine();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty!");
            } else {
                break;
            }
        } //while

        System.out.println("Input port number [default is 1500]: ");
        port = scanner.nextLine();
        System.out.println("Input server address [default is \"localhost\"]: ");
        server = scanner.nextLine();
        if (port.isEmpty()) {
            port = "1500";
        }
        if (server.isEmpty()) {
            server = "localhost";
        }

        // Create your client with the given input
        ChatClient client = new ChatClient(server, Integer.parseInt(port), username);

        // start the thread that will start listening from the server
        client.start();

        // Send an empty message to the server
        client.sendMessage(new ChatMessage(0, "HelloWorld"));
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) { //TODO: add a condition to end the loop (end the connection)
                try {
                    String msg = (String) sInput.readObject();
                    System.out.print(msg);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } //while
        } //run
    }
}
