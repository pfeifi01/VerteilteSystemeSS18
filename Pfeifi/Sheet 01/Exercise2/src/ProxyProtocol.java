import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProxyProtocol {

    public static final int FIXED_PORT = 9000;
    public static final int FIXED_PORT_SERVER = 9090;

    public static String SHUTDOWN = "Shutdown";
    private static boolean running = true;

    public static String reply(ServerSocket server){

        return "";
    }

    public static String request(Socket proxyServer, String clientMessage) {

        return "";
    }

    public static void close(){
        running = false;
    }

}
