import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Node {

    // Table of Nodes [Name,IP,Port] with max of 3 Nodes
    public ArrayList<NodeEntry> knownNodes = new ArrayList<>(3);

    public String name;
    public String ip;
    public int port;

    ServerSocket server;

    public Node (String ip, int port, String name){
        this.name = name;
        this.ip = ip;
        this.port = port;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("An error occurred while registering the ServerSocket of Node[" + name + "]");
            e.printStackTrace();
        }
    }

    public Node (String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void initializeNode(String ipOfOtherNode, int portOfOtherNode ){
        knownNodes.add(new NodeEntry(ipOfOtherNode,portOfOtherNode)); // Initialize without name, read name when connecting
    }

    public static void main(String args[]) {

        int numberOfNodes = 10;
        Node[] nodes = new Node[numberOfNodes];
        int port;

        for(int i = 0; i < numberOfNodes; i++){
            port = 8000 + (i*100);
            nodes[i] = new Node("localhost",port,"N" + (i+1));
            System.out.println("** Initialized Node [" + nodes[i].name + "] with IP [" + nodes[i].ip + "] and Port [" + nodes[i].port + "] **");
            if(i>0) {
                nodes[i].initializeNode(nodes[i - 1].ip, nodes[i - 1].port);
                System.out.println("** Added Node [" + nodes[i-1].name + "] to the Table of Node [" + nodes[i].name + "] **");
            } else {
                System.out.println("** Added no Node to the Table of Node [" + nodes[i].name + "] **");
            }
        }
    }

    public void removeNode(){

    }

    public void addNode(){

    }

    public void updateNodeTable(){

    }
}
