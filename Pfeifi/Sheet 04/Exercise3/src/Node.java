import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class Node{

    // Table of Nodes [Name,IP,Port] with max of 3 Nodes
    public ArrayList<String> knownNodes = new ArrayList<>(3);
    //TODO: Create second table with deleted nodes for the case we can't connect anymore to anything in our table

    public final String networkHandlerIp = "localhost";
    public final int networkHandlerPort = 8000;
    Socket networkHandler;

    public String name;
    public String ip;
    public int port;

    ServerSocket server;
    Socket randomNodeReceive;
    Socket randomNodeRequest;

    public Node (String ip, int port, String name){
        this.name = name;
        this.ip = ip;
        this.port = port;

        try {
            server = new ServerSocket(port);
            networkHandler = new Socket(networkHandlerIp,networkHandlerPort);
            ObjectOutputStream outToNetworkHandler =  new ObjectOutputStream(networkHandler.getOutputStream());
            outToNetworkHandler.writeObject(new NodeEntry(ip,port,name));
            ObjectInputStream inFromNetworkHandler = null;
            inFromNetworkHandler = new ObjectInputStream(networkHandler.getInputStream());
            System.out.println("** Node registered itself at the Network Handler **");
            outToNetworkHandler.close();
            inFromNetworkHandler.close();
            networkHandler.close();
        } catch (IOException e) {
            System.err.println("An error occurred while registering the ServerSocket of Node " + name + ", or while connecting to Network Handler.");
            e.printStackTrace();
        }
    }

    public void initializeNode(String nameOfOtherNode){
        knownNodes.add(nameOfOtherNode);
        System.out.println("** Added Node [" + nameOfOtherNode + "] to the Table of Node " + name + " **\n");
    }

    // Format Table to Pretty Printing
    public String formatTable(ArrayList<String> nodeTable){
        String formattedTable = "";
        for(int i = 0; i < nodeTable.size(); i++){
            formattedTable += "{" + nodeTable.get(i)+ "} ";
        }
        return formattedTable;
    }

    public static void main(String args[]) {

        // Read IP and Port of the node from User Input

//        System.out.println("Enter the IP for the Node: ");
        String ip = "localhost";
        int port = 8080;
        String name ="";
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            ip = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("Enter the Port for the Node: ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           port = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Enter the Name for the Node: ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            name = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node node = new Node(ip,port,name);
        System.out.println("** Initialized Node N[" + node.port + "] with IP [" + node.ip + "] , Port [" + node.port + "] and Name " + node.name + " **\n");

        // Read message from user input
//        System.out.println("Enter the IP of another Node. If you don't want to add another Node, just press Enter.");
        String ipOfOtherNode = "localhost";
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            ipOfOtherNode = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



            System.out.println("Enter the Name of another Node: ");
            String nameOfOtherNode = "";
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                nameOfOtherNode = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        if(!nameOfOtherNode.equals("")){
            node.initializeNode(nameOfOtherNode);
        } else {
            System.out.println("** No Node was added to the table of Node  " + name + " **");
        }

        System.out.println("Press any Key to activate this Node");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input= reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("** Node is now active**\n");

        // Wait for other nodes that want to establish a connection
            Thread receiveTableThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        node.waitForNodeConnection();
                    }
                }
            });
        receiveTableThread.start();

        // Periodically every 3 seconds try to establish a connection with a random node
        Thread requestTableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    node.connectToRandomNode();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        requestTableThread.start();
    }


    public void connectToRandomNode(){
        Random rnd = new Random();
        if(knownNodes.size() < 1)
            return;
        int randomNodeRequestIndex = rnd.nextInt(knownNodes.size());
        try {
            System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] requested Table of Node [" + knownNodes.get(randomNodeRequestIndex) + "] from Network Handler **");
            networkHandler = new Socket(networkHandlerIp,networkHandlerPort);
            ObjectOutputStream outToNetworkHandler =  new ObjectOutputStream(networkHandler.getOutputStream());
            outToNetworkHandler.writeObject(new NodeEntry(null,0,knownNodes.get(randomNodeRequestIndex)));
            ObjectInputStream inFromNetworkHandler = null;
            inFromNetworkHandler = new ObjectInputStream(networkHandler.getInputStream());
            NodeEntry randomNode = (NodeEntry) inFromNetworkHandler.readObject();
            outToNetworkHandler.flush();
            outToNetworkHandler.close();
            inFromNetworkHandler.close();
            networkHandler.close();
            System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] received IP [" + randomNode.ip +"] and Port[" + randomNode.port + "] of Node [" + randomNode.name + "] from Network Handler **");
            randomNodeRequest = new Socket(randomNode.ip,randomNode.port);
            updateNodeTable();
        } catch (IOException e) {
            System.err.println("An error occurred while connecting to the randomly chosen Node . Thus this node wil be removed from the table.");
            // e.printStackTrace();
            removeNode(randomNodeRequestIndex);
        }
        catch (ClassNotFoundException e ) {
        System.err.println("Error: Problem occurred while casting NodeTable from Object");
        e.printStackTrace();
        }
    }

    public void waitForNodeConnection(){
        try {
            randomNodeReceive = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream outToServer =  new ObjectOutputStream(randomNodeReceive.getOutputStream());
            outToServer.writeObject(knownNodes);
            System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] sent Table " + formatTable(knownNodes) + "to Node with Port ["+ randomNodeReceive.getLocalPort() +"] **");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNodeTable(){

        ObjectInputStream inFromRandomNode = null;
        ArrayList<String> tableOfRandomNode = new ArrayList<>();
        try {
            inFromRandomNode = new ObjectInputStream(randomNodeRequest.getInputStream());
            // TODO: FixAnnotation for unchecked typecast
            // @SuppressWarnings('unchecked')
            tableOfRandomNode = (ArrayList<String>) inFromRandomNode.readObject();
            randomNodeRequest.close();
        } catch (IOException  e ) {
            System.err.println("Error: Connection to the randomly selected node can't be closed.");
            e.printStackTrace();
        } catch (ClassNotFoundException e ) {
            System.err.println("Error: Problem occurred while casting NodeTable from Object");
            e.printStackTrace();
        }
        System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] received Table " + formatTable(tableOfRandomNode) + "from randomly chosen Node **");
        addNode(tableOfRandomNode);
    }

    public void removeNode(int index){
        System.out.println("**Removed Node [" + knownNodes.get(index) + "] from the table **");
        knownNodes.remove(index);
    }

    public void addNode(ArrayList<String> tableOfRandomNode){

        System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] is merging its own Table " + formatTable(knownNodes) +"with the received Table " + formatTable(tableOfRandomNode) + "from randomly chosen Node **");

        knownNodes.addAll(tableOfRandomNode);
        // Remove myself from the table
        String selfNode = name;
        for (int i= 0; i < knownNodes.size();i++){
            if(knownNodes.get(i).equals(selfNode)){
                knownNodes.remove(i);
            }
        }

        Set<String> mergedTable = new LinkedHashSet<>(knownNodes);
        knownNodes.clear();
        knownNodes.addAll(mergedTable);
        for(int i = 0; i < knownNodes.size();i++){
            String temp = knownNodes.get(i); {
                for (int j = i+1; j < knownNodes.size();j++){
                    if(temp.equals(knownNodes.get(j)))
                        knownNodes.remove(j);
                }
            }
            System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] merged Tables into " + formatTable(knownNodes) + "**");
        }
    }
}
