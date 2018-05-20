import java.io.IOException;
import java.net.ServerSocket;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Server {
    

    public static void main(String[] args) {

        int port = Protocol.FIXED_PORT;
        boolean active = true;
        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("An error occurred while registering server socket");
            e.printStackTrace();
        }

        System.out.println("**Server is ready for Clients**");

        while(active){
            // All communication is done via the protocol
            String output = Protocol.reply(server);
            if(output.equals(Protocol.SHUTDOWN)){
                active = false;
            }
        }
    }
}