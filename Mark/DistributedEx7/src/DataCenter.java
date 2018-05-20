import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.util.Pair;

public class DataCenter {
	
	
	private int numberOfClients;
	private int numberOfReplicas;
	private int requests;
	private int latencyCost;
	private String name;
	private int id;
	ArrayList<DataCenter> centers;
	int[][] connections;


	public DataCenter(String name, int numberOfClients, int requests){
		this.name = name;
		this.numberOfClients = numberOfClients;
		this.requests = requests;
	}
	
	public void init(ArrayList<DataCenter> centers, int[][] connections){
		this.centers = centers;
		this.connections = connections;
		this.id = centers.indexOf(this);
		
	}
	

	public void addReplica() {
		if(this.requests >= this.numberOfClients) {
			this.numberOfReplicas ++;
			System.err.println("Replica added on " + this.name);
		}
		this.requests++;		
	}
	
	public int unHandledRequests() {
		int unhandled = requests - numberOfClients - numberOfReplicas;
		return unhandled;

	}

	
	public void sendRequests(){
		if(this.getNumberOfClients() < this.getNumberOfRequests()) {
			int unhandled = this.unHandledRequests();
			for(int i = 0; i < unhandled; i++) {
				sendToCheapestDatacenter();
			}
		}
	}
	
	
	
	public void sendToCheapestDatacenter() {
		
		DataCenter cheapest = null;
		ArrayList<Integer> toIgnore = new ArrayList<>();
		int minIndex = 0;
		toIgnore.add(this.id);
		for(int i = 0; i < connections[this.id].length; i++) {
			minIndex = getIndexOfBestConnection(toIgnore);
			toIgnore.add(minIndex);
			cheapest = centers.get(minIndex); 
			if(cheapest.unHandledRequests() < 0){
				this.latencyCost += connections[this.id][minIndex];
				cheapest.requests += 1;
				this.requests--;
				System.out.println(this.name + " sends request to " + cheapest.name);
				return;
			}
		}
		System.out.println("Added Replica on " + this.name);
		this.numberOfReplicas++;
		
	}
	
	public int getIndexOfBestConnection(ArrayList<Integer> toIgnore){
		int minIndex = 0;
	    for (int i = 0; i < connections[this.id].length; i++){
	    	if(toIgnore.contains((Integer)i)){
	    		continue;
	    	}
	    	minIndex = i;
	    	break;
	    }
	    int min = connections[this.id][minIndex];
		
		
	    for (int i = 0; i < connections[this.id].length; i++){
	    	if(toIgnore.contains((Integer)i)){
	    		continue;
	    	}
	        if (connections[this.id][i] < min ){
	            min = connections[this.id][i];
	            minIndex = i;
	        }
       }
	    return minIndex;
	}
	
	public int getNumberOfReplicas() {
		return numberOfReplicas;
	}
	public int getNumberOfRequests(){
		return requests;
	}
	public int getNumberOfClients(){
		return numberOfClients;
	}	
	public int getLatencyCost(){
		return latencyCost;
	}
	public int[][] getConnections() {
		return connections;
	}
	public String getName(){
		return name;
	}
}

