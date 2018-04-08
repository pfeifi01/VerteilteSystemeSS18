import java.io.IOException;
import java.net.ServerSocket;

public class Proxy {

    public static void main(String[] args) {

        int port = Protocol.FIXED_PORT;
        boolean active = true;
        ServerSocket proxyServer = null;

        try {
            proxyServer = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("An error occurred while registering server socket");
            e.printStackTrace();
        }

        System.out.println("**ProxyServer is ready for Clients**");

        while(active){
            // All communication is done via the ProxyProtocol
            String output = Protocol.reply(proxyServer);
            if(output.equals(Protocol.SHUTDOWN)){
                active = false;
            }
        }
    }



}
