import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Node{

    // Table of Nodes [Name,IP,Port] with max of 3 Nodes
    public ArrayList<NodeEntry> knownNodes = new ArrayList<>(3);

    public String name;
    public String ip;
    public int port;

    ServerSocket server;
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

        Node node = new Node(ip, port, "N " + port);
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

        if (!ipOfOtherNode.equals("")) {

            System.out.println("Enter the Port of another Node: ");
            int portOfOtherNode = 8080;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                portOfOtherNode = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

            node.initializeNode(ipOfOtherNode, portOfOtherNode, "N " + portOfOtherNode + "");
        } else {
            System.out.println("** No Node was added to the table of Node N " + port + " **");
        }

        System.out.println("Press any Key to activate this Node");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("** Node is now active\n **");

        // Wait for other nodes that want to establish a connection
        Thread receiveTableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    node.waitForNodeConnection();
                }
            }
        });
        receiveTableThread.start();

        // Periodically every 3 seconds try to establish a connection with a random node
        Thread requestTableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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

        while(true) {

            System.out.println("Enter a new  Name of the initialized Node: \n");
            String newName = "";
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                newName = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            node.name = newName;
            node.propagateNodeChange(new NodeEntry(ip,port,newName));
            System.out.println("** MainThread [" + Thread.currentThread().getId() + "] started Propagation of Name Update **");
        }
    }

    public void sendMessage(String message, Socket node){

        try {
            System.out.println("** Thread [" + Thread.currentThread().getId() + "] sent message (" + message + ") **");
            DataOutputStream outToRandomNode = new DataOutputStream(new BufferedOutputStream(node.getOutputStream()));
            outToRandomNode.writeUTF(message);
            outToRandomNode.flush();
        } catch(IOException e) {
            System.err.println("Error: Connection to the randomly selected node can't be established or closed.");
        }
    }

    public void connectToRandomNode(){
        Random rnd = new Random();
        if(knownNodes.size() < 1)
            return;
        int randomNodeRequestIndex = rnd.nextInt(knownNodes.size());
        try {
            randomNodeRequest = new Socket(knownNodes.get(randomNodeRequestIndex).ip,knownNodes.get(randomNodeRequestIndex).port);
            updateNodeTable();
        } catch (IOException e) {
            // System.err.println("An error occurred while connecting to the randomly chosen Node [" + knownNodes.get(randomNodeRequestIndex).port +"]. Thus this node wil be removed from the table.");
            // e.printStackTrace();
            removeNode(randomNodeRequestIndex);
        }
    }

    public void waitForNodeConnection(){
        Socket randomNodeReceive = null;
        try {
            randomNodeReceive = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            DataInputStream inMessageFromServer = new DataInputStream(new BufferedInputStream(randomNodeReceive.getInputStream()));
            String message = inMessageFromServer.readUTF();
            //Read message of what to do
            if(message.equals("Table")) {
                // Return table
                // System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] received message (" + message + ") from Node [" + randomNodeReceive.getLocalPort() + "] **");
                ObjectOutputStream outToServer = new ObjectOutputStream(randomNodeReceive.getOutputStream());
                outToServer.writeObject(knownNodes);
                outToServer.flush();
                outToServer.close();
                // System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] sent Table " + formatTable(knownNodes) + "to Node [" + randomNodeReceive.getLocalPort() + "] **");
            } else {
                NodeEntry updatedEntry = new NodeEntry(message);
                // Update Table with propagated Update and continue Propagation
                System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] received an Entry for Propagation **");
                updateTableAndContinuePropagation(updatedEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNodeTable(){

        // Send message so client knows what to do
        sendMessage("Table", randomNodeRequest);
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
        //System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] received Table " + formatTable(tableOfRandomNode) + "from randomly chosen Node **");
        addNode(tableOfRandomNode);
    }

    public void removeNode(int index){
        //System.out.println("**Removed Node [" + knownNodes.get(index).port + "] from the table **");
        knownNodes.remove(index);
    }

    public void addNode(ArrayList<NodeEntry> tableOfRandomNode){

        //System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] is merging its own Table " + formatTable(knownNodes) +"with the received Table " + formatTable(tableOfRandomNode) + "from randomly chosen Node **");

        knownNodes.addAll(tableOfRandomNode);
        // Remove myself from the table
        NodeEntry selfNode = new NodeEntry(ip,port,"N " + name + "");
        for (int i= 0; i < knownNodes.size();i++){
            if(knownNodes.get(i).equals(selfNode)){
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
            //System.out.println("** RequestTableThread [" + Thread.currentThread().getId() + "] merged Tables into " + formatTable(knownNodes) + "**");
        }
    }

    public void updateTableAndContinuePropagation(NodeEntry updatedEntry){
        updateTableEntry(updatedEntry);
        System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] updated table with received Entry**");
        // 20% chance, that node will continue propagation
        Random rnd = new Random();
        //if(rnd.nextInt(101) > 50) {
            propagateNodeChange(updatedEntry);
            System.out.println("** ReceiveTableThread [" + Thread.currentThread().getId() + "] continued Propagation **");
        //}

    }

    public void propagateNodeChange(NodeEntry updatedEntry){
        Socket randomNodeToPropagate = null;
        Random rnd = new Random();
        if(knownNodes.size() < 1)
            return;
        int randomNodeToPropagateIndex = rnd.nextInt(knownNodes.size());
        try {
            randomNodeToPropagate = new Socket(knownNodes.get(randomNodeToPropagateIndex).ip,knownNodes.get(randomNodeToPropagateIndex).port);
            sendMessage(updatedEntry.toString(), randomNodeToPropagate);
        } catch (IOException e) {
            System.err.println("An error occurred while connecting to the randomly chosen Node [" + knownNodes.get(randomNodeToPropagateIndex).port +"] for Propagation.");
        }
    }

    public void updateTableEntry(NodeEntry updatedEntry) {
        for (NodeEntry e : knownNodes) {
            if (e.equals(updatedEntry)) {
                e.setName(updatedEntry.name);
                return;
            }
        }
    }

}
