import java.util.ArrayList;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class DataCenter {

    ArrayList<DataCenter> connectionsToDataCenters;
    int[][] latenciesToDataCenters;

    private int amountOfClients;    // Amount of Clients that a Data Center can serve
    private int amountOfRequests;   // Amount of Requests that a Data Center can handle
    private int amountOfReplicas;   // Amount of Replicas that a Data Center can handle
    private int amountOfLatency;    // Amount of Latency that a Data Center has waited
    private int id;
    String name;

    public DataCenter(String name, int amountOfClients, int amountOfRequests){
        this.name = name;
        this.amountOfClients = amountOfClients;
        this.amountOfRequests = amountOfRequests;
        System.out.println("** Data Center ["+ name + "] has been created with an amount of Clients of {" + amountOfClients + "} and an amount of Request of {" + amountOfRequests + "} **");
    }

    // Initialize Data Center with connections and latencies to the other Data Centers
    public void initDataCenterConnections( ArrayList<DataCenter> listOfDataCenters, int[][] latenciesToDataCenters){
        this.connectionsToDataCenters = listOfDataCenters;
        this.latenciesToDataCenters = latenciesToDataCenters;
        this.id = connectionsToDataCenters.indexOf(this);
    }

    // If the Data Center can't handle anymore requests, it creates replicas and forwards the requests to another Data Center
    public void sendRequestsToDataCenter(){
        if(!this.canHandleHisRequests()) {
            int unhandled = this.getAmountOfUnhandledRequests();
            System.out.println("** --> Data Center [" + name +"] has {" + this.getAmountOfUnhandledRequests() + "} unhandled Requests **");
            for(int i = 0; i < unhandled; i++) {
                this.findOptimalDataCenter();
            }
            System.out.println();
        }
    }

    public int findConnectionWithLowestLatency(ArrayList<Integer> ignoreIndices){

        int index = 0;
        for (int i=0; i < latenciesToDataCenters[id].length; i++){
            if(ignoreIndices.contains(i)){
                continue;
            }
            index = i;
            break;
        }
        int min = latenciesToDataCenters[id][index];

        for(int i = 0; i < latenciesToDataCenters[id].length;i++){
            if(ignoreIndices.contains(i)){
                continue;
            }
            if(latenciesToDataCenters[id][i] < min){
                min =latenciesToDataCenters[id][i];
                index = i;
            }
        }
        return index ;
    }

    // Find best Data Center Connection considering Latency, cost of replicas and how many requests it can handle
    public void findOptimalDataCenter(){
        DataCenter optimalDataCenter;
        ArrayList<Integer> ignoreIndices = new ArrayList<>();
        ignoreIndices.add(id);
        int indexOfLowestLatencyDataCenter = 0;
        for(int i=0; i < latenciesToDataCenters[id].length;i++){
            // Choose Data Center with lowest Latency
            indexOfLowestLatencyDataCenter = findConnectionWithLowestLatency(ignoreIndices);
            ignoreIndices.add(indexOfLowestLatencyDataCenter);
            optimalDataCenter = connectionsToDataCenters.get(indexOfLowestLatencyDataCenter);
            // Ask if Data Center can handle any more requests
            // Choose best, considering replica cost and latency cost
            if(optimalDataCenter.canHandleNewRequests()) {
                amountOfLatency += latenciesToDataCenters[id][indexOfLowestLatencyDataCenter];
                optimalDataCenter.amountOfRequests++;
                this.amountOfRequests--;
                System.out.println("** Optimal Data Center [" + optimalDataCenter.name + "] can handle another request and received a new Replica **");
                return;
            }
        }
        // If other Data Centers can't handle any more requests
        System.out.println("** Data Center [" + name + "]: Other Data Centers can't handle anymore Requests, so it adds a Replica to itself **");
        this.amountOfReplicas ++;
    }

    // Request Handling Functions

    public boolean canHandleHisRequests() {
        if(amountOfClients >= amountOfRequests)
            return true;
        else
            return false;
    }

    public boolean canHandleNewRequests() {
        if(amountOfClients > amountOfRequests)
            return true;
        else
            return false;
    }

    // Getters

    public int getAmountOfUnhandledRequests() {
        if(amountOfRequests - amountOfClients > 0)
            return amountOfRequests - amountOfClients;
        else
            return 0;
    }

    public int getAmountOfReplicas() {
        return amountOfReplicas;
    }

    public int getAmountOfLatency() {
        return amountOfLatency;
    }
}
