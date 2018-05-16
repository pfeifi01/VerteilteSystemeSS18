import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        // Number of Clients that a Data Center can handle
        int numberOfClientsDataCenterA = 10;
        int numberOfClientsDataCenterB = 12;
        int numberOfClientsDataCenterC = 10;
        int numberOfClientsDataCenterD = 4;

        // Create Data Centers
        DataCenter a = new DataCenter("Data Center A",numberOfClientsDataCenterA);
        DataCenter b = new DataCenter("Data Center B",numberOfClientsDataCenterB);
        DataCenter c = new DataCenter("Data Center C",numberOfClientsDataCenterC);
        DataCenter d = new DataCenter("Data Center D",numberOfClientsDataCenterD);

        System.out.println();

        // Latency between Data Centers computed from the Digits of the Matriculation Number
        String matriculationNumber = "1416200";
        int LatencyBetweenA_B = Integer.parseInt(matriculationNumber.substring(0, 1));
        int LatencyBetweenA_C = Integer.parseInt(matriculationNumber.substring(1, 2));
        int LatencyBetweenA_D = Integer.parseInt(matriculationNumber.substring(2, 3));
        int LatencyBetweenB_C = Integer.parseInt(matriculationNumber.substring(3, 4));
        int LatencyBetweenB_D = Integer.parseInt(matriculationNumber.substring(4, 5));
        int LatencyBetweenC_D = Integer.parseInt(matriculationNumber.substring(5, 6));

        // Connections between Data Centers containing the Instance of the Data Center and the Latency of the Connection
        HashMap<DataCenter, Integer> connectionsOfDataCenterA = new HashMap<>();
        connectionsOfDataCenterA.put(b, LatencyBetweenA_B);
        connectionsOfDataCenterA.put(c, LatencyBetweenA_C);
        connectionsOfDataCenterA.put(d,LatencyBetweenA_D);
        HashMap<DataCenter, Integer> connectionsOfDataCenterB = new HashMap<>();
        connectionsOfDataCenterB.put(a, LatencyBetweenA_B);
        connectionsOfDataCenterB.put(c, LatencyBetweenB_C);
        connectionsOfDataCenterB.put(d, LatencyBetweenB_D);
        HashMap<DataCenter, Integer> connectionsOfDataCenterC = new HashMap<>();
        connectionsOfDataCenterC.put(a, LatencyBetweenA_C);
        connectionsOfDataCenterC.put(b,LatencyBetweenB_C);
        connectionsOfDataCenterC.put(d, LatencyBetweenC_D);
        HashMap<DataCenter, Integer> connectionsOfDataCenterD = new HashMap<>();
        connectionsOfDataCenterD.put(a, LatencyBetweenA_D);
        connectionsOfDataCenterD.put(b, LatencyBetweenB_D);
        connectionsOfDataCenterD.put(c, LatencyBetweenC_D);

        // Maximum Number Of Requests executed by a Data Center
        int maximumAmountOfRequests = 16;

        // Random Number Of Requests executed by each Data Center (between 0 and 15)
        Random random = new Random();
        int amountOfRequestsDataCenterA = random.nextInt(maximumAmountOfRequests);
        int amountOfRequestsDataCenterB = random.nextInt(maximumAmountOfRequests);
        int amountOfRequestsDataCenterC = random.nextInt(maximumAmountOfRequests);
        int amountOfRequestsDataCenterD = random.nextInt(maximumAmountOfRequests);

        // Initialize Data Centers
        a.initDataCenter(connectionsOfDataCenterA,amountOfRequestsDataCenterA);
        b.initDataCenter(connectionsOfDataCenterB,amountOfRequestsDataCenterB);
        c.initDataCenter(connectionsOfDataCenterC,amountOfRequestsDataCenterC);
        d.initDataCenter(connectionsOfDataCenterD,amountOfRequestsDataCenterD);

        System.out.println();

        // Store Data Centers in a List
        ArrayList<DataCenter> listOfDataCenters = new ArrayList<>();

        // Add initialized Data Centers to List of Data Centers
        listOfDataCenters.add(a);
        listOfDataCenters.add(b);
        listOfDataCenters.add(c);
        listOfDataCenters.add(d);

        // Initialize Weight Parameters (w1, w2)
        Pair<Double,Double> preferStorageCost = new Pair<>(0.75,0.25);
        Pair<Double,Double> preferLatencyCost = new Pair<>(0.25,0.75);
        Pair<Double,Double> preferEquallyStorageAndLatencyCost = new Pair<>(0.5,0.5);

        //Add Clients to Data Centers TODO: understand what is happening here
        for(DataCenter center : listOfDataCenters) {
            if(!center.canHandleHisRequests()) {
                int unhandled = center.getAmountOfUnhandledRequests();
                for(int i = 0; i < unhandled; i++) {
                    DataCenter data = center.findOptimalDataCenter();
                    data.addClientToDataCenter();
                }
            }
        }

        //TODO: understand why we are doing this
        int latencyCosts = numberOfClientsDataCenterA + numberOfClientsDataCenterB +numberOfClientsDataCenterC +numberOfClientsDataCenterD;
        int storageCosts = 0;

        for(DataCenter center : listOfDataCenters) {
            latencyCosts += center.getAmountOfLatency();
            storageCosts += center.getAmountOfReplicas() * 30;
        }

        // Print Results
        System.out.println();
        System.out.println("** Results: ");
        System.out.println("** Prefer Storage Cost " + calculateTotalCost(storageCosts, latencyCosts, preferStorageCost));
        System.out.println("** Prefer Latency Cost " +calculateTotalCost(storageCosts, latencyCosts, preferLatencyCost));
        System.out.println("** Prefer Equally Storage Cost and Latency Cost " + calculateTotalCost(storageCosts, latencyCosts, preferEquallyStorageAndLatencyCost));


    }
    // Calculate the total cost TODO: understand what is happening here
    public static double calculateTotalCost(double storageCost, double latencyCost, Pair<Double, Double> weights) {
        return storageCost * weights.getKey() + latencyCost * weights.getValue();
    }
}
