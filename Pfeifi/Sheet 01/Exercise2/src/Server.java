import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {

        System.out.println("Specify the Port of your Server: ");
        Scanner in = new Scanner(System.in);
        int port = in.nextInt();
        in.close();

        boolean active = true;
        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("An error occurred while registering server socket from server");
            e.printStackTrace();
        }

        System.out.println("**Server is ready for Clients**");

        while(active){
            // All communication is done via the protocol
            String output = Protocol.replyServerProxy(server);
            if(output.equals(Protocol.SHUTDOWN)){
                active = false;
            }
        }
    }
}