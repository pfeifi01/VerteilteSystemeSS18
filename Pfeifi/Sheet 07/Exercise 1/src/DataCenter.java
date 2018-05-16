import javax.naming.Name;
import java.util.HashMap;

public class DataCenter {
    // DataCenter Instance + Latency to DataCenter
    HashMap<DataCenter,Integer> connectionsToDataCenters = new HashMap<>();

    int amountOfClients;
    int amountOfRequests;
    int amountOfReplicas;
    int amountOfLatency;
    String name;

    public DataCenter(String name, int amountOfClients){
        this.name = name;
        this.amountOfClients = amountOfClients;
        System.out.println("** "+ name + " has been created with an amount of clients of [" + amountOfClients + "] **");
    }

    public void initDataCenter (HashMap<DataCenter,Integer> connections, int amountOfRequests) {
        this.connectionsToDataCenters = connections;
        this.amountOfRequests = amountOfRequests;
        System.out.println("** "+ name + " has been initialized an amount of request of [" + amountOfRequests + "] and " + connections.size() +" connections to other Data Centers**");
    }


    // Find with best Data Center Connection considering Latency and if it can handle another Request

    public DataCenter findOptimalDataCenter(){
        // TODO: Implement
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

    // TODO: Why Replica and not Client and why amountOfRequests at the receiver not the sender?
    public void addClientToDataCenter() {
        if(!canHandleNewRequests()) {
            amountOfReplicas ++;
            System.out.println("** " + name + " received a new Replica **");
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
