import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Main {
	
	public static void main(String[] args){
		
		//Connections to all other nodes
		int[][] connections = {{0,1,5,1},{1,0,8,8},{5,8,0,4},{1,8,5,0}};
		
		//with distancevectoralg. update all nodes.
		
		int[][] temp = connections;
		for(int k = 0; k < 4; k++){
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					temp[i][j] = Math.min(temp[i][j],temp[i][k] + temp[k][j]);
				}
			}
		}
		connections = temp;
		
		System.out.println("Connections after distancevectoralg.:");
		for (int[] row : connections){
			System.out.println(Arrays.toString(row));
		}
		
		System.out.println("-----------------------");

		
		Random r = new Random();
	
		/** Number of clients **/
		
		int clientsA = 10;
		int clientsB = 12;
		int clientsC = 10;
		int clientsD = 4;
		
		/** Number of requests **/
		
		int requestsA = r.nextInt(16);
		int requestsB = r.nextInt(16);
		int requestsC = r.nextInt(16);
		int requestsD = r.nextInt(16);
		
		/** Initialize DataCenters **/
		
		DataCenter a = new DataCenter("A", clientsA, requestsA);
		DataCenter b = new DataCenter("B", clientsB, requestsB);
		DataCenter c = new DataCenter("C", clientsC, requestsC);
		DataCenter d = new DataCenter("D", clientsD, requestsD);


		/** Used to loop over all centers **/
		
		ArrayList<DataCenter> centers = new ArrayList<>();
		centers.add(a);
		centers.add(b);
		centers.add(c);
		centers.add(d);
		
		
		System.out.println("A: " + a.getNumberOfClients() + " clients, " + a.getNumberOfRequests() + " requests.");
		System.out.println("B: " + b.getNumberOfClients() + " clients, " + b.getNumberOfRequests() + " requests.");
		System.out.println("C: " + c.getNumberOfClients() + " clients, " + c.getNumberOfRequests() + " requests.");
		System.out.println("D: " + d.getNumberOfClients() + " clients, " + d.getNumberOfRequests() + " requests.");

		
		
		
		for(DataCenter center : centers){
			center.init(centers,connections);
		}
		
		
		//weights
		double[] preferStorage = {0.75,0.25};
		double[] preferLatency = {0.25,0.75};
		double[] equal = {0.5,0.5};

		
		for(DataCenter center : centers) {
			System.out.println("+++" + center.getName() + " sends requests+++");
			center.sendRequests();
		}
		
		System.out.println("-----------------------");
		for(DataCenter center : centers){
			System.out.println(center.getName() + " has " + center.getNumberOfReplicas() + " replicas,  " + center.getNumberOfRequests() + " requests, and a latency of: " + center.getLatencyCost());
		}
		
		int storageCosts = 0;
		//latency to datacenter
		int latencyCosts = clientsA + clientsB + clientsC + clientsD;
	
		
		for(DataCenter center : centers) {
			latencyCosts += center.getLatencyCost();
			storageCosts += center.getNumberOfReplicas() * 30;
		}
		

		System.out.println("Prefer storage cost " + totalCost(storageCosts, latencyCosts, preferStorage));
		System.out.println("Prefer latency cost " + totalCost(storageCosts, latencyCosts, preferLatency));
		System.out.println("Prefer equally storage cost and latency cost " + totalCost(storageCosts, latencyCosts, equal));
	}
		
	
	
	public static double totalCost(double StorageCost, double LatencyCost, double[] weights) {
		return StorageCost * weights[0] + LatencyCost * weights[1];
	}
}
