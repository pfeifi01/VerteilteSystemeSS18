import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String args[]) {

        int numberOfThreads = 10;
        String unsortedStrings[] = {"test","hello","world","glass","dog","phone","bird","mouse","key","check"};

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
                        System.out.println("**Client Thread (" + Thread.currentThread().getId() + ") sends unsorted String [" + unsortedString +"] to Server**");
                        try {
                            System.out.println("**Client Thread (" + Thread.currentThread().getId() + ") got Server Response: " + stub.compute(timeToCompute));
                            System.out.println("**Client Thread (" + Thread.currentThread().getId() + ") got Server Response: " + stub.sort(unsortedString));
                        } catch (RemoteException e) {
                            System.err.println("An error occurred while connecting to the server: " + e.toString());
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("An error occurred while starting the registry or connecting to the server: " + e.toString());
            e.printStackTrace();
        }
    }
}