import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Proxy implements Services {

    private Services sortServerStub = null;
    private Services computeServerStub = null;

    private void initializeProxyServer(){
        try {
            // Connect to the Sort-Server
            Registry registryToSortServer = LocateRegistry.getRegistry(9000);
            sortServerStub = (Services) registryToSortServer.lookup("Services");
            System.out.println("**Proxy-Server has successfully connected to the Sort-Server**");

            // Connect to Compute-Server
            Registry registryToComputeServer = LocateRegistry.getRegistry(9100);
            computeServerStub = (Services) registryToComputeServer.lookup("Services");
            System.out.println("**Proxy-Server has successfully connected to the Compute-Server**");

            // Create ProxyServer for Clients to access
            Registry registryForClient;
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
            registryForClient = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            Services proxyStub = (Services) UnicastRemoteObject.exportObject(this, Registry.REGISTRY_PORT);
            registryForClient.bind("Services", proxyStub);
            System.out.println("**Proxy-Server is ready for Clients**\n");
        } catch (Exception e) {
            System.err.println("An error occurred while connecting to the Endpoint-Servers or creating the Proxy-Server: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Proxy proxyServer = new Proxy();
        proxyServer.initializeProxyServer();
    }

    @Override
    // Forwards function to Server, if client has the access right "SORT"
    public String sort(String inputString) throws RemoteException {
        if(true) { // TODO: Check Access Rights & forward to SortServer [If Client.AccesRights == Server.Accesrights]
            System.out.println("**Function 'sort' called by Client Thread (" + Thread.currentThread().getId() + ") was forwarded by the ProxyServer to Server**");
            return sortServerStub.sort(inputString);
        } else {
            System.out.println("**Function 'sort' called by Client Thread (" + Thread.currentThread().getId() + ") was denied by the ProxyServer**");
            return "Access Denied";
        }
    }

    @Override
    // Forwards function to Server, if client has the access right "COMPUTE"
    public String compute(int time) throws RemoteException {
        if(true){ // TODO: Check Access Rights & forward to ComputeServer [If Client.AccesRights == Server.Accesrights]
            System.out.println("**Function 'compute' called by Client Thread (" + Thread.currentThread().getId() + ") was forwarded by the ProxyServer to Server**");
            return computeServerStub.compute(time);
        } else {
            System.out.println("**Function 'compute' called by Client Thread (" + Thread.currentThread().getId() + ") was denied by the ProxyServer**");
            return "Access Denied";
        }
    }
}