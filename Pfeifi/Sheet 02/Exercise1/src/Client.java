import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Client {

    public static void main(String args[]) {

        Socket server = null;

        try {
            server = new Socket("localhost", Protocol.FIXED_PORT);
        } catch (IOException ex) {
            System.err.println("An error occurred while connecting to the server");
        }

        // Read message from user input
        System.out.println("Enter your Input: ");
        String message = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            message = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make request to server
        if(server!=null)
            System.out.println("Server Response: " +Protocol.request(server,message));

        try {
            server.close();
        } catch (IOException e) {
            System.err.println("Error: Server can't be closed");
        }
    }
}
