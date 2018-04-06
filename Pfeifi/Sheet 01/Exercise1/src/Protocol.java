import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Protocol {

    public static final int FIXED_PORT = 9000;
    public static String SHUTDOWN = "Shutdown";
    private static boolean running = true;

    public static String reply(ServerSocket server){

        Socket client = null;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Accept clients and assign a ServerThread to each of them
        while (running) {
            try {
                client = server.accept();
            } catch(SocketException ex) {
                System.err.println("**Server Shutdown**");
            } catch (IOException e) {
                System.err.println("An error occurred while connecting to the client");
                e.printStackTrace();
            }
            if (running)
                executor.execute(new ServerThread(client,server));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.err.println("Error: ExecutorService was interrupted");
        }
        return SHUTDOWN;
    }

    public static String request(Socket server, String clientMessage) {
        String serverResponse = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
            out.writeUTF(clientMessage);
            out.flush();
        } catch(Exception ex) {
            System.err.println("Error while writing to the server");
        }

        try {
            in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            serverResponse = in.readUTF();
        } catch(Exception ex) {
            System.err.println("Error while getting server response");
        } finally {
            try {
                in.close();
                out.close();
                server.close();
            } catch (IOException e) {
                System.err.println("Error while closing the streams");
            }
        }
        return serverResponse;
    }

    public static void close(){
        running = false;
    }

}
