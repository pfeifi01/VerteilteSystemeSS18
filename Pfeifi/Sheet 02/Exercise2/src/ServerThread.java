import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ServerThread implements Runnable{

    private Socket proxyClient = null;
    private ServerSocket server = null;

    public ServerThread(Socket proxyClient, ServerSocket server) {
        this.proxyClient = proxyClient;
        this.server = server;
    }

    public void run(){
        System.out.println("**ServerThread " +Thread.currentThread().getId() + " connected to Proxy**");
        try {
            DataInputStream inputFromClient = new DataInputStream(new BufferedInputStream(proxyClient.getInputStream()));
            DataOutputStream outputToClient = new DataOutputStream(new BufferedOutputStream(proxyClient.getOutputStream()));
            String input = inputFromClient.readUTF();

            // Shutdown the protocol + server and close the streams when shutdown message is received
            if(input.equals(Protocol.SHUTDOWN)){
                System.out.println("**ServerThread " +Thread.currentThread().getId() + " received Shutdown Message**");
                outputToClient.writeUTF("**Server will shutdown now**");
                // Attention when interrupting server
                Protocol.close();
                server.close();
            } else {
                String output = sortString(input);
                outputToClient.writeUTF(output);
                System.out.println("**ServerThread " +Thread.currentThread().getId() + " successfully sorted Input**");
            }

            outputToClient.close();
            inputFromClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sort the user input
    public String sortString(String input){
        char[] chars = input.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}