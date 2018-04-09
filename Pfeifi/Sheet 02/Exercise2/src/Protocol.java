import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Protocol {

    public static final int FIXED_PORT = 8080; // Port for ProxyServer
    public static int[] ports = {9000,9100,9200}; // Port for Endpoint Servers

    public static String SHUTDOWN = "Shutdown";
    private static boolean running = true;

    public static String replyServerProxy(ServerSocket server){

        Socket clientProxy = null;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Accept clients and assign a ServerThread to each of them
        while (running) {
            try {
                clientProxy = server.accept();
            } catch(SocketException ex) {
                System.out.println("**Server Shutdown**");
            } catch (IOException e) {
                System.err.println("An error occurred while connecting to the client");
                e.printStackTrace();
            }
            if (running)
                executor.execute(new ServerThread(clientProxy,server));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.err.println("Error: ExecutorService was interrupted");
        }
        return SHUTDOWN;
    }

    public static String replyProxyClient(ServerSocket serverProxy, Socket server){


        Socket client = null;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Accept clients and assign a ServerThread to each of them
        while (running) {
            try {
                client = serverProxy.accept();
            } catch(SocketException ex) {
                System.err.println("**Proxy Shutdown**");
            } catch (IOException e) {
                System.err.println("An error occurred while connecting to the client");
                e.printStackTrace();
            }
            if (running)
                executor.execute(new ProxyThread(client,server));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.err.println("Error: ExecutorService was interrupted");
        }
        return SHUTDOWN;
    }

    // The request from the client will now be made to the proxy server
    // The request from the proxy(Thread) are made to the actual server

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
