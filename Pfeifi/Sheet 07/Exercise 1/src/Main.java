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

        // Maximum Number Of Requests executed by a Data Center
        int maximumAmountOfRequests = 16;

        // Random Number Of Requests executed by each Data Center (between 0 and 15)
        Random random = new Random();
        int amountOfRequestsDataCenterA = random.nextInt(maximumAmountOfRequests);
        int amountOfRequestsDataCenterB = random.nextInt(maximumAmountOfRequests);
        int amountOfRequestsDataCenterC = random.nextInt(maximumAmountOfRequests);
        int amountOfRequestsDataCenterD = random.nextInt(maximumAmountOfRequests);

        // Create Data Centers
        DataCenter a = new DataCenter("A",numberOfClientsDataCenterA, amountOfRequestsDataCenterA);
        DataCenter b = new DataCenter("B",numberOfClientsDataCenterB, amountOfRequestsDataCenterB);
        DataCenter c = new DataCenter("C",numberOfClientsDataCenterC, amountOfRequestsDataCenterC);
        DataCenter d = new DataCenter("D",numberOfClientsDataCenterD, amountOfRequestsDataCenterD);
        System.out.println();

        // Store Latencies between Data Centers, where the indices are the following 0 = A, 1 = B, 2 = C, 3 = D
        int[][] latenciesToDataCenters = {{0,1,4,1},{1,0,6,2},{4,6,0,10},{1,2,10,0}}; // Latency between Data Centers computed from the Digits of the Matriculation Number [1416200]

        // Use the Distance Vector Algorithm to find fastest path between two Pairs of Data Centers by minimizing the connection latency matrix
        latenciesToDataCenters = sortConnectionsByLatency(latenciesToDataCenters);

        // Store Data Centers in a List
        ArrayList<DataCenter> listOfDataCenters = new ArrayList<>();

        // Add initialized Data Centers to List of Data Centers
        listOfDataCenters.add(a);
        listOfDataCenters.add(b);
        listOfDataCenters.add(c);
        listOfDataCenters.add(d);


        for(DataCenter center : listOfDataCenters) {
            center.initDataCenterConnections(listOfDataCenters, latenciesToDataCenters);
        }

        // Initialize Weight Parameters (w1, w2)
        Pair<Double,Double> preferStorageCost = new Pair<>(0.75,0.25);
        Pair<Double,Double> preferLatencyCost = new Pair<>(0.25,0.75);
        Pair<Double,Double> preferEquallyStorageAndLatencyCost = new Pair<>(0.5,0.5);

        // Send requests to Data Centers
        for(DataCenter center : listOfDataCenters) {
           center.sendRequestsToDataCenter();
        }

        // First we add the number of clients times the latency to the Data Center which is 1
        int latencyCosts = numberOfClientsDataCenterA + numberOfClientsDataCenterB +numberOfClientsDataCenterC +numberOfClientsDataCenterD;
        int storageCosts = 0;

        // Then we sum the latency and storage costs of each Data Center up, considering that each replica costs 30
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

    // Calculate the total cost
    public static double calculateTotalCost(double storageCost, double latencyCost, Pair<Double, Double> weights) {
        return storageCost * weights.getKey() + latencyCost * weights.getValue();
    }

    // Minimize the latency of the connections between Data Centers
    public static int[][] sortConnectionsByLatency(int[][] latenciesToDataCenters){

        // Distance Vector Algorithm by Warshall
        for(int k = 0; k < latenciesToDataCenters.length; k++){
            for(int i = 0; i < latenciesToDataCenters.length; i++){
                for(int j = 0; j < latenciesToDataCenters.length; j++){
                    //TODO: Continue Implementing
                }
            }
        }
        return new int[][]{};
    }
}

