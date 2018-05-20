import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class NetworkHandler {

    public ArrayList<NodeEntry> nodeNetwork = new ArrayList<>();

    ServerSocket server;
    public final int networkHandlerPort = 8000;

    public NetworkHandler (){
        try {
            server = new ServerSocket(networkHandlerPort);
        } catch (IOException e) {
            System.err.println("An error occurred while registering the ServerSocket the Network Handler.");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        NetworkHandler networkHandler = new NetworkHandler();

        System.out.println("** Network Handler is now active **\n");

        while(true) {
            Socket node = null;
            try {
                node = networkHandler.server.accept();
                System.out.println("** Network Handler accepted Node **");
                networkHandler.handleMultipleRequests(node);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public NodeEntry registerNodeEntry(NodeEntry nodeEntry){

            for (int i = 0; i < nodeNetwork.size();i++){
                if (nodeEntry.name.equals(nodeNetwork.get(i).name))
                    return nodeEntry;
            }
            nodeNetwork.add(nodeEntry);

            return nodeEntry;
    }

    public NodeEntry returnNodeEntry(NodeEntry nodeEntry){

        for (int i = 0; i < nodeNetwork.size();i++){
            if (nodeEntry.name.equals(nodeNetwork.get(i).name)){
                nodeEntry.ip = nodeNetwork.get(i).ip;
                nodeEntry.port = nodeNetwork.get(i).port;
                return nodeEntry;
            }
        }
        return nodeEntry;
    }

    public synchronized void handleMultipleRequests(Socket node){

        try {
            ObjectInputStream inFromNode = null;
            inFromNode = new ObjectInputStream(node.getInputStream());
            NodeEntry nodeEntry = (NodeEntry) inFromNode.readObject();
            ObjectOutputStream outToNode =  new ObjectOutputStream(node.getOutputStream());

            if(nodeEntry.port == 0){
                NodeEntry entry = returnNodeEntry(nodeEntry);
                outToNode.writeObject(entry);
                System.out.println("** NetworkHandler returned the requested NodeEntry " + formatEntry(entry) + "**");
            } else {
                NodeEntry entry = registerNodeEntry(nodeEntry);
                outToNode.writeObject(entry);
                System.out.println("** NetworkHandler added the requesting Node " + formatEntry(entry) + "to the Table " + formatTable() + "**");
                System.out.println("** NetworkTable: " + formatTable() + "**");
            }
            outToNode.flush();
            outToNode.close();
            inFromNode.close();
            node.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e ) {
            System.err.println("Error: Problem occurred while casting NodeTable from Object");
            e.printStackTrace();
        }
    }

    // Format Entry to Pretty Printing
    public String formatEntry(NodeEntry nodeEntry){
        return "{" + nodeEntry.ip + "|" + nodeEntry.port + "|" + nodeEntry.name + "} ";

    }

    // Format Table to Pretty Printing
    public String formatTable(){
        String formattedTable = "";
        for(int i = 0; i < nodeNetwork.size(); i++){
            formattedTable += "{" +nodeNetwork.get(i).ip + "|" + nodeNetwork.get(i).port + "|" + nodeNetwork.get(i).name + "} ";
        }
        return formattedTable;
    }

}
