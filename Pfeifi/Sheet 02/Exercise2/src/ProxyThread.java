import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ProxyThread implements Runnable{

    private Socket client = null;
    private Socket server = null;

    public ProxyThread(Socket client, Socket server) {
        this.client = client;
        this.server = server;
    }

    public void run(){
        System.out.println("**ProxyThread " +Thread.currentThread().getId() + " accepted Client " + client + "**");

        // Forward request from client to server

        try {

            DataInputStream inputFromClient = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            String clientMessage = inputFromClient.readUTF();

            System.out.println("**ProxyThread " +Thread.currentThread().getId() + " forwarded message from Client to Server[" + server.getPort() + "]**");

            String serverResponse = Protocol.request(server, clientMessage);
            DataOutputStream outputToClient = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            outputToClient.writeUTF(serverResponse);

            System.out.println("**ProxyThread " +Thread.currentThread().getId() + " forwarded message from Server["+ server.getPort() + "]to Client**");

            outputToClient.close();
            inputFromClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}