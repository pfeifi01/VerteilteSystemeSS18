import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javafx.util.Pair;

public class Client{

	public final int DNSPORT = 1000;
	public final String DNSIP = "localhost";
	public List<String> table = new ArrayList<>();
	
    public String name;
    public String ip;
    public int port;

    ServerSocket server;
    Socket dns;

    public Client (String name, String ip, int port){
        this.ip = ip;
        this.port = port;
        this.name = name;

        try {
            server = new ServerSocket(port);
            dns = new Socket(DNSIP,DNSPORT);
            
            //Send DNS info, that I am in the network
            ObjectOutputStream out = new ObjectOutputStream(dns.getOutputStream());		
			ObjectInputStream in = new ObjectInputStream(dns.getInputStream());
			out.writeObject(new TableEntry(this.name, this.ip,this.port));
			in.readObject();
			out.flush();
            out.close();
            dns.close();
        } catch (IOException e) {
            System.err.println("Error while registering the ServerSocket of Client [" + port + "] or while connecting to DNS");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 
    
    public void sendingTable(){
    	ObjectOutputStream out = null;
    	Socket client = null;
		try {
			client = server.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
    
    
    public TableEntry request(String id){
    	TableEntry received = null;
    	try {
    		dns = new Socket(DNSIP, DNSPORT);
			ObjectInputStream in = new ObjectInputStream(dns.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(dns.getOutputStream());				
			Object object = null;
			
			//Send TableEntry back to client
			out.writeObject(new TableEntry(id,"useless",0));

			received = (TableEntry) in.readObject();
			out.flush();
			out.close();
			in.close();
			dns.close();
		} catch (ClassNotFoundException e) {
			System.err.println("Error ClassNotFoundException");
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return received;
    }
    
    public Socket connectToClient(int id) throws IOException{
    	Socket socket = null;
    	TableEntry chosen = request(table.get(id));
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
    	
			try {
				in = new ObjectInputStream(client.getInputStream());
				if ((object = in.readObject()) != null) {
					@SuppressWarnings("unchecked")
					ArrayList<String> received = (ArrayList<String>) object;
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
    
    public void remove(int id){

    	System.out.println("Removed Client["+table.get(id)+"].");
    	table.remove(id);
    }

    public void mergeTables(ArrayList<String> received){
    	
    	//Combine both
    	table.addAll(received);
    	
    	//Remove myself
    	String temp = this.name;
    	for(int i = 0; i < table.size(); i++){
    		if(table.get(i).equals(temp)){
    			table.remove(i);
    		}
    	}
    	
    	removeDuplicates();
    }
    
    public void removeDuplicates(){
    	String temp = null;
    	for(int i = 0; i < table.size();i++){
    		temp = table.get(i);
    		for(int j = i+1; j < table.size();j++){
    			if(table.get(j).equals(temp)){
        			table.remove(j);
        		}
    		}
    	}
    }
    
    public void add(String entry){
    	table.add(entry);
    }
    
    public void printTable(){
    	System.out.println("-----------");
    	for(String e : table){
    		System.out.println(e);
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
        
        System.out.println("Enter another clients name for table (enter to skip this):");
        in = new Scanner(System.in);
        String line = in.nextLine();
        if(!line.equals("")){ 
	    	client.add(line);
        }
    	
    	System.out.println("Client started with " + client.table.size() + " entries.");
        Thread t1 = new Thread(new Runnable(){
        	public void run(){
        		while(true){
        			client.sendingTable();
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
        in.close();
    }  
}
