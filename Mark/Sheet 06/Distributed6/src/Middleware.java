import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Middleware extends Thread{

	
	private Node node;
	private int portIndex;
	private int[] PORTS = {8080,9000,9090};
	private int[][] delays = {{0,1,20},{1,0,3},{20,3,0}};
	private int[] clockVector = {0,0,0};
	private List<VectorClockProtocol> buffer = new ArrayList<>();
	
	ServerSocket server = null;
	
	public Middleware(Node node, int portIndex){
		this.node = node;
		this.portIndex = portIndex;

		try{
			  server = new ServerSocket(PORTS[portIndex]);
		}catch(IOException e){
				System.err.println("Error creating server");
		}
	}
	
	//Connection between Middlewares
	
	public VectorClockProtocol getMessageFromOtherNode(){
		
		VectorClockProtocol protocol = null;
		
		try {
			Socket other = server.accept();
			
			ObjectInputStream input = new ObjectInputStream(other.getInputStream());
			Object obj = input.readObject();
			protocol = (VectorClockProtocol)obj;
		} catch (IOException e) {
			System.out.println("Error connecting to other client");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return protocol;
	}
	
	public void getMessageFromApplicationAndSendMessageToAll(String str){
		

		
		System.out.println("Middleware received Message from application: ");
		System.out.println(str);
		System.out.println("Middleware ready to send it");

		
		ArrayList<Integer> otherPortIndices = new ArrayList<>();
		
		for(int i = 0; i < PORTS.length; i++){
			if(i == this.portIndex){
				i++;
			}
			otherPortIndices.add(i);
		}
		
		//Update Vector
		this.clockVector[this.portIndex]++;
		VectorClockProtocol protocol = new VectorClockProtocol(this.portIndex,this.clockVector,str);
		
		
		try {
			Socket socket1 = new Socket("localhost", PORTS[otherPortIndices.get(0)]);
			Socket socket2 = new Socket("localhost", PORTS[otherPortIndices.get(1)]);
			
			int delay1 = this.delays[this.portIndex][otherPortIndices.get(0)];
			int delay2 = this.delays[this.portIndex][otherPortIndices.get(1)];
			
			ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
			
			//Wait delay time to send message
			try {
				Thread.sleep(delay1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			out1.writeObject(protocol);
			out1.flush();
			socket1.close();
			
			ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
			
			try {
				Thread.sleep(delay2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			out2.writeObject(protocol);
			out2.flush();

			
			socket2.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		//sendToAllOthers();
		//OutputStream
		
	}
	
	
	//Connection from Middleware to Application
	
	
	public void sendMessageToApplication(String str){
		node.getApplication().getMessage(str);
	}
	
	public  void getMessageFromOtherAndSendToApplication(){
		
		VectorClockProtocol protocol = getMessageFromOtherNode();
		
		if(checkVectorClock(protocol.getPortIndex(),protocol.getVector())){
			this.clockVector[protocol.getPortIndex()]++;
			sendMessageToApplication(protocol.getMessage());
			
			//Loop through buffer, maybe another one got ready
			for(int i = 0; i < buffer.size(); i++){
				if(checkVectorClock(buffer.get(i).getPortIndex(),buffer.get(i).getVector())){
					this.clockVector[protocol.getPortIndex()]++;
					sendMessageToApplication(protocol.getMessage());
					i=0;
				}
			}
		}else{
			//Add protocol to List, because its not ready to be executed
			buffer.add(protocol);
		}
	}
	
	public boolean checkVectorClock(int otherIndex, int[] otherVector){
		
		//Check right message
		if(otherVector[otherIndex] != (this.clockVector[otherIndex]+1)){
			return false;
		}
		
		//Check right time
		for(int i = 0; i < clockVector.length; i++){
			if(i == otherIndex){
				i++;
			}
			if(otherVector[i] != this.clockVector[i]){
				return false;
			}
		}
		return true;
	}
	
	
	public void run(){

		System.out.println("Middleware sends message to its application");
		sendMessageToApplication("middle");
		while(true){
		}
		
	}
}
