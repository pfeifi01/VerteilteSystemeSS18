import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Client{


	public List<TableEntry> table = new ArrayList<>();
	
    public String name;
    public String ip;
    public int port;

    ServerSocket server;

    public Client (String name, String ip, int port){
        this.ip = ip;
        this.port = port;
        this.name = name;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error while registering the ServerSocket of Client [" + port + "]");
            e.printStackTrace();
        }
    } 
   
    
    public void handleIncomingStream(){
    	
    	Socket client = null;
    	try {
    		client = server.accept();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	DataInputStream in = null;    	
    	String response = null;
    
		try {
			System.out.println(client.getPort());
			in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			response = in.readUTF();
		} catch(IOException e) {
			System.err.println("Error getting client message");
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				System.err.println("Error while stream closing");
			}
		}

    	if(response.equals("table")){
    		sendingTable(client);
    	}
    }
    
    public void sendingTable(Socket client){
    	
    	ObjectOutputStream out = null;
		
		try {
			System.out.println("Client["+port+"] is sending its table.");
			out = new ObjectOutputStream(client.getOutputStream());				
			out.writeObject(table);
			out.flush();
		} catch(IOException e) {
			System.err.println("Error while writing to the Client");
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public Socket connectToClient(int id) throws IOException{
    	
    	Socket socket = null;
    	TableEntry chosen = table.get(id);
    	
    	try {
    		socket = new Socket(chosen.getIp(),chosen.getPort());	
    	} catch (IOException e) {
    		System.out.println("Error connecting to random Client.");
    		throw new IOException();
    	}
    	return socket;
    }
       
    public void updateTable(){
    	
    	ObjectInputStream in = null; 
    	Object object = null;
    	
    	//Connect to random client
    	Random rnd = new Random();
    	int id = rnd.nextInt(table.size());
    	
    	try{
    	
    		Socket client = connectToClient(id);
    		
    		sendMessage(client,"table");
    	
			try {
				in = new ObjectInputStream(client.getInputStream());
				if ((object = in.readObject()) != null) {
					@SuppressWarnings("unchecked")
					ArrayList<TableEntry> received = (ArrayList<TableEntry>) object;
					mergeTables(received);
				}
					
			} catch (ClassNotFoundException e) {
				System.err.println("Error ClassNotFoundException");
			} catch(IOException e) {
				System.err.println("Error getting Client response");
			} finally {
				try {
					in.close();
					client.close();
				} catch (IOException e) {
					System.err.println("Error while stream closing");
				}
			}
			
    	}catch(IOException e){
    		remove(id);
    	}
		
    }
    
    public void sendMessage(Socket client, String message){
    	
    	DataOutputStream out = null;
		try {
			System.out.println("Request sent to server: " + client.getPort());
			out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));				
			out.writeUTF(message);
			out.flush();
		} catch(IOException e) {
			System.err.println("Error while writing to the client");
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				System.err.println("Error while stream closing");
			}
		}
    }
    
    public void remove(int id){

    	System.out.println("Removed Client["+table.get(id).getPort()+"].");
    	table.remove(id);
    }

    public void mergeTables(ArrayList<TableEntry> received){
    	
    	//Combine both
    	table.addAll(received);
    	
    	//Remove myself
    	TableEntry temp = new TableEntry(this.ip,this.port,this.name);
    	for(int i = 0; i < table.size(); i++){
    		if(table.get(i).equals(temp)){
    			table.remove(i);
    		}
    	}
    	
    	removeDuplicates();
    }
    
    public void removeDuplicates(){
    	TableEntry temp = null;
    	for(int i = 0; i < table.size();i++){
    		temp = table.get(i);
    		for(int j = i+1; j < table.size();j++){
    			if(table.get(j).equals(temp)){
        			table.remove(j);
        		}
    		}
    	}
    }
    
    public void add(TableEntry entry){
    	table.add(entry);
    }
    
    public void printTable(){
    	System.out.println("-----------");
    	for(TableEntry e : table){
    		System.out.println(e.getPort());
    	}
    	System.out.println("-----------");

    }
    
    @SuppressWarnings("resource")
	public static void main(String args[]) {

    	
    	System.out.println("Enter port for Client:");
    	Scanner in = new Scanner(System.in);
    	int port = in.nextInt();
    	System.out.println("Enter name for Client:");
    	in = new Scanner(System.in);
    	String name = in.nextLine();
        Client client = new Client(name, "localhost",port);
        
        System.out.println("Enter another port for table (enter to skip this):");
        in = new Scanner(System.in);
        String line = in.nextLine();
        if(!line.equals("")){ 
	    	client.add(new TableEntry("localhost",Integer.parseInt(line),"blala"));
        }
    	
    	System.out.println("Client started with " + client.table.size() + " entries.");
        Thread t1 = new Thread(new Runnable(){
        	public void run(){
        		while(true){
        			client.handleIncomingStream();
        		}
        	}
        });
        t1.start();

        System.out.println("Press enter to start clients job:");
        in = new Scanner(System.in);
        String ads = in.nextLine();
        Thread t2 = new Thread(new Runnable(){
        	public void run(){
	        	for(int i = 0; i < 3; i++){
	        		try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        		System.out.println("Old table: ");
	        		client.printTable();
	        		client.updateTable();
	        		System.out.println("New table: ");
	        		client.printTable();

	        	}
        	}
        });
        t2.start();
        
        System.out.println("Press enter to change name of client job:");
        in = new Scanner(System.in);
        String addadss = in.nextLine();
        Thread t3 = new Thread(new Runnable(){
        	public void run(){
        		
        	}
        });
        t3.start();
        in.close();
    }  
}
