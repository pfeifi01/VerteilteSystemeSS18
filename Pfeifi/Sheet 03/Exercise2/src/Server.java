import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Server implements Services {

    public int serverPort;
    public  AccessRights right;
    Services stub = null;

    public Server(int port, AccessRights right){
        this.serverPort = port;
        this.right = right;
    }

    private void initializeServer(){
        try {
            stub = (Services) UnicastRemoteObject.exportObject(this, 0);
            Registry registry;
            LocateRegistry.createRegistry(this.serverPort);
            registry = LocateRegistry.getRegistry(serverPort);
            registry.bind("Services", stub);
            System.out.println("**" + this.right + "-Server is Ready**");
        } catch (Exception e) {
            System.err.println("An error occurred while starting the "+ this.right +"-Server: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Create two new instances of endpoint-servers, on for each function of the services
        Server sortServer = new Server(9000,AccessRights.SORT);
        Server computeServer = new Server(9100, AccessRights.COMPUTE);
        sortServer.initializeServer();
        computeServer.initializeServer();

    }

    @Override
    public String compute(int time) throws RemoteException {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Server computed for " + time + " seconds<";
    }

    @Override
    public String sort(String input) throws RemoteException {
        char[] chars = input.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}