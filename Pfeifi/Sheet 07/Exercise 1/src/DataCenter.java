import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Map;

public class DataCenter {

    ArrayList<DataCenter> connectionsToDataCenters;
    int[][] latenciesToDataCenters;

    int amountOfClients;    // Amount of Clients that a Data Center can serve
    int amountOfRequests;   // Amount of Requests that a Data Center can handle
    int amountOfReplicas;   // Amount of Replicas that a Data Center can handle
    int amountOfLatency;    // Amount of Latency that a Data Center has waited
    String name;

    public DataCenter(String name, int amountOfClients, int amountOfRequests){
        this.name = name;
        this.amountOfClients = amountOfClients;
        this.amountOfRequests = amountOfRequests;
        System.out.println("** Data Center ["+ name + "] has been created with an amount of clients of [" + amountOfClients + "] and  an amount of request of [" + amountOfRequests + "] **");
    }

    // Initialize Data Center with connections and latencies to the other Data Centers
    public void initDataCenterConnections( ArrayList<DataCenter> listOfDataCenters, int[][] latenciesToDataCenters){
        this.connectionsToDataCenters = listOfDataCenters;
        this.latenciesToDataCenters = latenciesToDataCenters;
    }

    // If the Data Center can't handle anymore requests, it creates replicas and forwards the requests to another Data Center
    public void sendRequestsToDataCenter(){
        if(!this.canHandleHisRequests()) {
            int unhandled = this.getAmountOfUnhandledRequests();
            for(int i = 0; i < unhandled; i++) {
                DataCenter data = this.findOptimalDataCenter();
                data.addReplicaToDataCenter();
            }
        }
    }

    // Find best Data Center Connection considering Latency, cost of replicas and how many requests it can handle
    public DataCenter findOptimalDataCenter(){

        // TODO: Implement

        // Choose Data Center with lowest Latency

        // Ask how many request the connection can handle

        // Choose best, considering replica cost and latency cost

        // Sum up waited latency

        amountOfLatency += 0;

        return null;
    }

    // Request Handling Functions

    public boolean canHandleHisRequests() {
        if(amountOfClients >= amountOfRequests)
            return true;
        else
            return false;
    }

    public boolean canHandleNewRequests() {
        if(amountOfClients < amountOfRequests)
            return true;
        else
            return false;
    }

    public void addReplicaToDataCenter() {
        if(!canHandleNewRequests()) {
            amountOfReplicas ++;
            System.out.println("** " + name + " created a new Replica **");
        }
        amountOfRequests++;
    }

    // Getters

    public int getAmountOfUnhandledRequests() {
        if(amountOfRequests - amountOfClients > 0)
            return 0;
        else
            return amountOfRequests-amountOfClients;
    }

    public int getAmountOfReplicas() {
        return amountOfReplicas;
    }

    public int getAmountOfLatency() {
        return amountOfLatency;
    }
}
