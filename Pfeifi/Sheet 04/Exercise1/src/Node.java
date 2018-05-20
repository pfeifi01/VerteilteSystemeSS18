import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Node{

    // Table of Nodes [Name,IP,Port] with max of 3 Nodes
    public ArrayList<NodeEntry> knownNodes = new ArrayList<>(3);
    // Save all nodes that were ever in our Node Table to prevent a possible segregation of the network
    public ArrayList<NodeEntry> allEverKnownNodes = new ArrayList<>();

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
        } catch (IOException e) {
            System.err.println("An error occurred while registering the ServerSocket of Node " + name + ".");
            e.printStackTrace();
        }
    }

    public void initializeNode(String ipOfOtherNode, int portOfOtherNode, String nameOfOtherNode){
        knownNodes.add(new NodeEntry(ipOfOtherNode,portOfOtherNode,nameOfOtherNode));
        allEverKnownNodes.add(new NodeEntry(ipOfOtherNode,portOfOtherNode,nameOfOtherNode));
        System.out.println("** Added Node N[" + portOfOtherNode + "] to the Table of Node " + name + " **\n");
    }

    // Format Table to Pretty Printing
    public String formatTable(ArrayList<NodeEntry> nodeTable){
        String formattedTable = "";
        for(int i = 0; i < nodeTable.size(); i++){
            formattedTable += "{" + nodeTable.get(i).ip + "|" + nodeTable.get(i).port + "|" + nodeTable.get(i).name + "} ";
        }
        return formattedTable;
    }

    public static void main(String args[]) {

        // Read IP and Port of the node from User Input

//        System.out.println("Enter the IP for the Node: ");
        String ip = "localhost";
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            ip = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("Enter the Port for the Node: ");
        int port = 8080;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           port = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node node = new Node(ip,port,"N "+ port);
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

        if(!ipOfOtherNode.equals("")){

            System.out.println("Enter the Port of another Node: ");
            int portOfOtherNode = 8080;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                portOfOtherNode = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

            node.initializeNode(ipOfOtherNode,portOfOtherNode,"N "+portOfOtherNode+"");
        } else {
            System.out.println("** No Node was added to the table of Node N " + port + " **");
        }

        System.out.println("Press any Key to activate this Node");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input= reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Node is now active");

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
        boolean useBackupTable = false;
        int randomNodeRequestIndex = -1;
        try {
            if(knownNodes.size() < 1 || false) { // If Node Table is empty, use Backup Table TODO: Replace false with a variable that checks if there was never a table update after connecting always to the same nodes..
                if (allEverKnownNodes.size() < 1)
                    return;
                else {
                    useBackupTable = true;
                    randomNodeRequestIndex = rnd.nextInt(allEverKnownNodes.size());
                    randomNodeRequest = new Socket(allEverKnownNodes.get(randomNodeRequestIndex).ip,allEverKnownNodes.get(randomNodeRequestIndex).port);
                }
            } else {
                randomNodeRequestIndex = rnd.nextInt(knownNodes.size());
                randomNodeRequest = new Socket(knownNodes.get(randomNodeRequestIndex).ip,knownNodes.get(randomNodeRequestIndex).port);
            }
            updateNodeTable();
        } catch (IOException e) {
            // e.printStackTrace();
            if(!useBackupTable) {
                System.err.println("An error occurred while connecting to the randomly chosen Node . Thus this node wil be removed from the table.");
                removeNode(randomNodeRequestIndex);
            } else {
                System.err.println("An error occurred while connecting to the randomly chosen Node from Backup Table");
            }
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
            System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] sent Table " + formatTable(knownNodes) + "to Node ["+ randomNodeReceive.getLocalPort() +"] **");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNodeTable(){

        ObjectInputStream inFromRandomNode = null;
        ArrayList<NodeEntry> tableOfRandomNode = new ArrayList<>();
        try {
            inFromRandomNode = new ObjectInputStream(randomNodeRequest.getInputStream());
            // TODO: FixAnnotation for unchecked typecast
            // @SuppressWarnings('unchecked')
            tableOfRandomNode = (ArrayList<NodeEntry>) inFromRandomNode.readObject();
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
        allEverKnownNodes.add(knownNodes.get(index));
        System.out.println("**Removed Node [" + knownNodes.get(index).port + "] from the table **");
        knownNodes.remove(index);
    }

    public void addNode(ArrayList<NodeEntry> tableOfRandomNode){

        System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] is merging its own Table " + formatTable(knownNodes) +"with the received Table " + formatTable(tableOfRandomNode) + "from randomly chosen Node **");

        knownNodes.addAll(tableOfRandomNode);
        // Remove myself from the table
        NodeEntry selfNode = new NodeEntry(ip,port,"N " + name + "");
        for (int i= 0; i < knownNodes.size();i++) {
            if (knownNodes.get(i).equals(selfNode)) {
                knownNodes.remove(i);
            }
        }

        Set<NodeEntry> mergedTable = new LinkedHashSet<>(knownNodes);
        knownNodes.clear();
        knownNodes.addAll(mergedTable);
        for(int i = 0; i < knownNodes.size();i++){
            NodeEntry temp = knownNodes.get(i); {
                for (int j = i+1; j < knownNodes.size();j++){
                    if(temp.equals(knownNodes.get(j)))
                        knownNodes.remove(j);
                }
            }
            System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] merged Tables into " + formatTable(knownNodes) + "**");
        }

        // Remove random nodes, until just there are just three nodes left
        Random random = new Random();
        while(knownNodes.size() > 3){
            knownNodes.remove(random.nextInt(knownNodes.size()));
        }
    }
}
