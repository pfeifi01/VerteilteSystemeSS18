import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Proxy implements Services {

    private Services proxyServerStub = null;
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
            proxyServerStub = (Services) UnicastRemoteObject.exportObject(this, Registry.REGISTRY_PORT);
            registryForClient.bind("Services", proxyServerStub);
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
    public String sort(String inputString, AccessRight right) throws RemoteException {
        if(right.equals(AccessRight.SORT)) {
            System.out.println("**Function [SORT] called by a Client Thread was forwarded by the ProxyServer to Server**");
            return sortServerStub.sort(inputString, right);
        } else {
            System.out.println("**Function [SORT] called by a Client Thread was denied by the ProxyServer**");
            return "ACCESS DENIED: No rights for [SORT]";
        }
    }

    @Override
    // Forwards function to Server, if client has the access right "COMPUTE"
    public String compute(int time,AccessRight right) throws RemoteException {
        if(right.equals(AccessRight.COMPUTE)){
            System.out.println("**Function [COMPUTE] called by a Client Thread was forwarded by the ProxyServer to Server**");
            return computeServerStub.compute(time, right);
        } else {
            System.out.println("**Function [COMPUTE] called by a Client Thread was denied by the ProxyServer**");
            return "ACCESS DENIED: No rights for [COMPUTE]";
        }
    }
}