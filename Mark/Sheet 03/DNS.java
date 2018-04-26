import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DNS {
	
	public List<TableEntry> list = new ArrayList<>();
	
	public final int DNSPORT = 1000;
	public final String DNSIP = "localhost";
	
	public ServerSocket server;

	
	public DNS(){
		try {
			server = new ServerSocket(DNSPORT);
		} catch (IOException e) {
			System.err.println("Error creating ServerSocket");
			e.printStackTrace();
		}
	}
	
	
	public TableEntry createResponse(TableEntry entry){

		for (TableEntry tmp : list){
			if(entry.equals(tmp)){
				return tmp;
			}
		}
		list.add(entry);
		return entry;
	}
	
	
	public synchronized void handleRequests(Socket client){
		
		try {
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());				
			Object object = null;
			
			if ((object = in.readObject()) != null) {
				TableEntry received = (TableEntry) object;
				
				TableEntry response = createResponse(received);
				
				
				//Send TableEntry back to client
				out.writeObject(response);
				out.flush();
				out.close();
				in.close();
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Error ClassNotFoundException");
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args){
		 
		DNS dns = new DNS();
		System.out.println("DNS started.");
		 while(true){
			 try {
				Socket client = dns.server.accept();
				System.out.println("Client accepted");
				dns.handleRequests(client);
				dns.printTable();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
	}
	
	
    public void printTable(){
    	System.out.println("-----------");
    	for(TableEntry e : list){
    		System.out.println(e.getName() + " - " + e.getPort());
    	}
    	System.out.println("-----------");

    }
}
