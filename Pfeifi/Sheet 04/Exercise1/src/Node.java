import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Node{

    // Table of Nodes [Name,IP,Port] with max of 3 Nodes
    public ArrayList<NodeEntry> knownNodes = new ArrayList<>(3);

    //public String name;
    public String ip;
    public int port;

    ServerSocket server;
    Socket randomNodeReceive;
    Socket randomNodeRequest;

    public Node (String ip, int port, String name){
       // this.name = name;
        this.ip = ip;
        this.port = port;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("An error occurred while registering the ServerSocket of Node[" + name + "]");
            e.printStackTrace();
        }
    }


    public void initializeNode(String ipOfOtherNode, int portOfOtherNode ){
        knownNodes.add(new NodeEntry(ipOfOtherNode,portOfOtherNode));
        System.out.println("** Added Node N[" + portOfOtherNode + "] to the Table of Node N[" + port + "] **");
    }

    public static void main(String args[]) {

        // Read IP and Port of the node from User Input

        System.out.println("Enter the IP for the Node: ");
        String ip = "localhost";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            ip = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Enter the Port for the Node: ");
        int port = 8080;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           port = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node node = new Node(ip,port,"N " + port);
        System.out.println("** Initialized Node N[" + node.port + "] with IP [" + node.ip + "] and Port [" + node.port + "] **\n");

        // Read message from user input
        System.out.println("Enter the IP of another Node. If you don't want to add another Node, just press Enter.");
        String ipOfOtherNode = "localhost";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            ipOfOtherNode = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!ipOfOtherNode.equals("")){

            System.out.println("Enter the Port of another Node: ");
            int portOfOtherNode = 8080;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                portOfOtherNode = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }

            node.initializeNode(ipOfOtherNode,portOfOtherNode);
        } else {
            System.out.println("** No Node was added to the table of Node N[" + port + "] **");
        }

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
            randomNodeRequest = new Socket(knownNodes.get(randomNodeRequestIndex).ip,knownNodes.get(randomNodeRequestIndex).port);
        } catch (IOException e) {
            System.err.println("An error occurred while connecting to the randomly selected node. Thus this node wil be removed from the table.");
            // e.printStackTrace();
            removeNode(randomNodeRequestIndex);
        }
        updateNodeTable();
    }

    public void waitForNodeConnection(){
        try {
            randomNodeReceive = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OutputStream outToServer = null;
            outToServer = randomNodeReceive.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            //TODO: Format table
            // table.split at ',' and create new NodeEntry
            out.writeUTF("table");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNodeTable(){
        try {
            InputStream inFromRandomNode = randomNodeRequest.getInputStream();
            DataInputStream in = new DataInputStream(inFromRandomNode);
            String tableOfRandomNode = in.readUTF();
            //TODO: Format table
            // table.split at ',' and create new NodeEntry
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            randomNodeRequest.close();
        } catch (IOException e) {
            System.err.println("Error: Connection to the randomly selected node can't be closed.");
        }


        // TODO: add node we get from randomNode
        addNode(new ArrayList<NodeEntry>());
    }

    public void removeNode(int index){
        System.out.println("**Removed Node [" + knownNodes.get(index).name + "] from the table **");
        knownNodes.remove(index);
    }
    //TODO: make table clear threadsafe

    public void addNode(ArrayList<NodeEntry> tableOfRandomNode){

        // Remove me from the table
        knownNodes.addAll(tableOfRandomNode);
        knownNodes.remove(new NodeEntry(ip,port));

        // Remove Duplicates
        Set<NodeEntry> mergedTable = new HashSet<>();
        mergedTable.addAll(knownNodes);
        knownNodes.clear();
        knownNodes.addAll(mergedTable);
    }
}
