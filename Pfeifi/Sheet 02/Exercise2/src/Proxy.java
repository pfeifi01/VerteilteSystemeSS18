import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Proxy {

    public static void main(String[] args) {

        int port = Protocol.FIXED_PORT;
        boolean active = true;
        ServerSocket proxyServer = null;
        Socket[] servers = new Socket[Protocol.ports.length];

        // Find all Servers and choose one to connect
        for(int i = 0; i < Protocol.ports.length; i++){
            try {
                servers[i] = new Socket("localhost", Protocol.ports[i]);
                System.out.println("**Server "+ servers[i] +" available**");
            } catch (IOException ex) {
                System.err.println("An error occurred while connecting from proxy to the server with port "+ Protocol.ports[i]);
            }
        }

        try {
            proxyServer = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("An error occurred while registering server socket of proxyServer");
            e.printStackTrace();
        }

        System.out.println("**ProxyServer is ready for Clients**");

        Random rn = new Random();

        while(active){
            // All communication is done via the ProxyProtocol

            // Choose a random server
            Socket randomServer = servers[rn.nextInt(servers.length)];

            String output = Protocol.replyProxyClient(proxyServer,randomServer);
            if(output.equals(Protocol.SHUTDOWN)){
                active = false;
            }
        }
    }
}
