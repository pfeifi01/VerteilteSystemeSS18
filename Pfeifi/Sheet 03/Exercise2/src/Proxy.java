import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Proxy implements Services {

    Services serverStub = null;

    public Proxy(){
        try {
            // Create + locate the registry and bind the remote object's stub in the registry
            Registry registryToServer = LocateRegistry.createRegistry(9000); //RMI-Port 1099
            serverStub = (Services) registryToServer.lookup("Services");
            registryToServer.bind("Services", serverStub);
            System.out.println("**ProxyServer is ready for Server**");

            // Create + locate the registry and bind the remote object's stub in the registry
            Registry registryForClient;
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
            registryForClient = LocateRegistry.getRegistry();
            Services proxyStub = (Services) UnicastRemoteObject.exportObject(this, Registry.REGISTRY_PORT);
            registryForClient.bind("Services", proxyStub);
            System.out.println("**Server is ready for Clients**");
        } catch (Exception e) {
            System.err.println("An error occurred while starting the registry or the proxyServer: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Proxy proxyServer = new Proxy();

    }

    @Override
    // Forwards function to Server, if client has the access right "COMPUTE"
    public String compute(int time) throws RemoteException {

        // TODO: Check Access Rights & forward to Server
        return serverStub.compute(time);
    }

    @Override
    // Forwards function to Server, if client has the access right "SORT"
    public String sort(String inputString) throws RemoteException {

        // TODO: Check Access Rights & forward to Server
        return serverStub.sort(inputString);
    }
}