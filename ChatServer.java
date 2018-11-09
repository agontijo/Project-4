import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Server for the chat program
 * @author Alice Gontijo Goulart Leite, Tae Yoon Kim
 * @version 2018-11-09
 */

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;


    private ChatServer(int port) {
        this.port = port;
    }

    private ChatServer() {
        this(1500);
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            //setting up a server socket inside the server class with the given port number
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) { //making a loop so that it will continuously wait for input
                //waiting until there is a client to serve (accept the client)
                Socket socket = serverSocket.accept();

                //Creating a new runnable object for the connected client and give the client a uniqueId
                Runnable r = new ClientThread(socket, uniqueId++);

                //Create a new thread for the client
                Thread t = new Thread(r);

                //add the client to the collection
                clients.add((ClientThread) r);

                //start the thread of the client
                t.start();
            }
        } catch (IOException e) {
            System.out.println("ChatServer.java: Could not connect to port: " + port + ". Check Again.");
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        String port;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input port number for the Server Socket [default is 1500]: ");
        port = scanner.nextLine();

        if (port.isEmpty()) {
            port = "1500";
        }

        ChatServer server = new ChatServer(Integer.parseInt(port));
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;                      //
        ObjectInputStream sInput;           //
        ObjectOutputStream sOutput;         //
        int id;                             //client unique ID
        String username;                    //
        ChatMessage cm;                     //the chatMessage which the client will input

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                //After creating the I/O stream, the very first line will be the username
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            try {
                cm = (ChatMessage) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(username + ": Ping");


            // Send message back to the client
            try {
                sOutput.writeObject("Pong");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
