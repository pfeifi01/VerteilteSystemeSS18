import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private int numberOfThreads = 2;
    private String unsortedStrings[] = {"test","hello","world","glass","dog","phone","bird","mouse","key","check"};
    private AccessRights right = null; // TODO: Refactor into array

    public Client (AccessRights right){
        this.right = right;
    }

    public static void main(String args[]) {

        Client sortClient = new Client(AccessRights.SORT);
        Client computeClient = new Client(AccessRights.COMPUTE);
        sortClient.initializeClient();
        computeClient.initializeClient();
    }

    public void initializeClient(){
        try {
            Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            Services stub = (Services) registry.lookup("Services");
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            for(int i = 0; i < numberOfThreads; i++){
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Random rnd = new Random();
                        int timeToCompute = rnd.nextInt(10);
                        String unsortedString = unsortedStrings[timeToCompute];

                        // Make request to the server
                        System.out.println("**Client Thread (" + Thread.currentThread().getId() + ") with Access Right["+right+"] sends unsorted String [" + unsortedString +"] to Server**");
                        try {
                            System.out.println("**Client Thread (" + Thread.currentThread().getId() + ") with Access Right["+right+"] got Server Response: " + stub.compute(timeToCompute));
                            System.out.println("**Client Thread (" + Thread.currentThread().getId() + ") with Access Right["+right+"] got Server Response: " + stub.sort(unsortedString));
                        } catch (RemoteException e) {
                            System.err.println("An error occurred while connecting to the server: " + e.toString());
                        }
                    }
                });
            } executor.shutdown();
        } catch (Exception e) {
            System.err.println("An error occurred while starting the registry or connecting to the server: " + e.toString());
            e.printStackTrace();
        }
    }
}