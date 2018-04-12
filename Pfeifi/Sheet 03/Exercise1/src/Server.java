import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Server implements Services{

    public static void main(String[] args) {

        // Create a new instance of the server
        Server server = new Server();

        try {
            Services stub = (Services) UnicastRemoteObject.exportObject(server, Registry.REGISTRY_PORT);
            // Create + locate the registry and bind the remote object's stub in the registry
            Registry registry;
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
            registry = LocateRegistry.getRegistry();
            registry.bind("Services", stub);
            System.out.println("**Server is ready for Clients**");
        } catch (Exception e) {
            System.err.println("An error occurred while starting the registry or the server: " + e.toString());
            e.printStackTrace();
        }
    }

    // Expand by starting a new Thread for every request
    @Override
    public String compute(int time) throws RemoteException {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "**Server computed for " + time + " seconds**";
    }

    // Expand by starting a new Thread for every request
    @Override
    public String sort(String input) throws RemoteException {
        char[] chars = input.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}