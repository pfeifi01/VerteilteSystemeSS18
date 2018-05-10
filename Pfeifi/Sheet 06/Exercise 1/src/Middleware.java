import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Middleware extends Thread {

    private Node node;

    ServerSocket server;
    Socket inFromOtherNode;
    Socket outToOtherNode1;
    Socket outToOtherNode2;

    private int[] listOfPorts = {8900, 9000, 9100};
    private int indexOfPort;
    private int[][] sendDelays = {{0, 1, 20}, {1, 0, 3}, {20, 3, 0}};
    private int[] clockVector = {0, 0, 0};
    private ArrayList<VectorClockProtocol> receivedMessages = new ArrayList<>();

    public Middleware(Node node, int indexOfPort) {
        this.node = node;
        this.indexOfPort = indexOfPort;
        try {
            server = new ServerSocket(listOfPorts[indexOfPort]);

        } catch (IOException e) {
            System.err.println("An error occurred while registering the ServerSocket of Node [" + indexOfPort + "] .");
            e.printStackTrace();
        }
    }

    public void sendMessageFromOtherNodeToApplication(String message) {
        node.getApplication().receiveMessage(message);
    }

    public void sendMessageFromApplicationToOtherNode(String message) {

        updateVectorClock(this.indexOfPort);
        VectorClockProtocol vectorClock = new VectorClockProtocol(this.clockVector, message, this.indexOfPort);

        ArrayList<Integer> indicesOfOtherPorts = new ArrayList<>();
        ArrayList<Integer> portsOfReceivingNodes = new ArrayList<>();

        for (int i = 0; i < listOfPorts.length; i++){
            if(!(i == this.indexOfPort)){
                indicesOfOtherPorts.add(i);
                portsOfReceivingNodes.add(listOfPorts[i]);
            }
        }

        try {
            outToOtherNode1 = new Socket("localhost", portsOfReceivingNodes.get(0));
            outToOtherNode2 = new Socket("localhost", portsOfReceivingNodes.get(1));

            ObjectOutputStream output1 = new ObjectOutputStream(outToOtherNode1.getOutputStream());
            ObjectOutputStream output2 = new ObjectOutputStream(outToOtherNode2.getOutputStream());

            int firstDelay = sendDelays[indexOfPort][indicesOfOtherPorts.get(0)];
            int secondDelay =  sendDelays[indexOfPort][indicesOfOtherPorts.get(1)];

            // Determine which message is sent first, by comparing the delays
            if(firstDelay > secondDelay){
                Thread.sleep(secondDelay * 1000);
                output2.writeObject(vectorClock);
                System.out.println("Node [" + (this.node.getIndexOfPort() + 1) + "]: Middleware forwarded message [" + message + "] to Node with Port [" + portsOfReceivingNodes.get(1) +"] with a delay of " + secondDelay + " seconds." );

                Thread.sleep((firstDelay - secondDelay) * 1000);
                output1.writeObject(vectorClock);
                System.out.println("Node [" + (this.node.getIndexOfPort() + 1) + "]: Middleware forwarded message [" + message + "] to Node with Port [" + portsOfReceivingNodes.get(0) +"] with a delay of " + firstDelay + " seconds." );
            } else {
                Thread.sleep(firstDelay * 1000);
                output1.writeObject(vectorClock);
                System.out.println("Node [" + (this.node.getIndexOfPort() + 1) + "]: Middleware forwarded message [" + message + "] to Node with Port [" + portsOfReceivingNodes.get(0) + "] with a delay of " + firstDelay + " seconds.");

                Thread.sleep((secondDelay - firstDelay) * 1000);
                output2.writeObject(vectorClock);
                System.out.println("Node [" + (this.node.getIndexOfPort() + 1) + "]: Middleware forwarded message [" + message + "] to Node with Port [" + portsOfReceivingNodes.get(1) + "] with a delay of " + secondDelay + " seconds.");
            }
            output1.close();
            outToOtherNode1.close();
            output2.close();
            outToOtherNode2.close();

        } catch (IOException | InterruptedException  e) {
            e.printStackTrace();
        }
    }

    public void receiveAndCheckMessageFromOtherNode() {

        ObjectInputStream inFromRandomNode = null;
        VectorClockProtocol input = null;
        try {
            inFromOtherNode = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inFromRandomNode = new ObjectInputStream(inFromOtherNode.getInputStream());
            input = (VectorClockProtocol) inFromRandomNode.readObject();
            System.out.println("Node [" + (this.node.getIndexOfPort() + 1) + "]: Middleware received message [" + input.getMessage() + "] from another Node" );
            inFromRandomNode.close();
        } catch (IOException  e ) {
            System.err.println("Error: Connection to the node can't be closed.");
            e.printStackTrace();
        } catch (ClassNotFoundException e ) {
            System.err.println("Error: Problem occurred while casting VectorClockProtocol from Object");
            e.printStackTrace();
        }
        if(inFromRandomNode == null ||input == null)
            return;

        if (checkIncomingVectorClockProtocol(input)) {
            updateVectorClock(input.getIndexOfNode());
            sendMessageFromOtherNodeToApplication(input.getMessage());
            // Check if buffer contains message which should be executed next
            for (int i = 0; i < receivedMessages.size(); i++){
                if (checkIncomingVectorClockProtocol(receivedMessages.get(i))){
                    updateVectorClock(input.getIndexOfNode());
                    sendMessageFromOtherNodeToApplication(receivedMessages.get(i).getMessage());
                    i = 0;
                }
            }
        } else {
            // Store message in buffer to be executed later
            this.receivedMessages.add(input);
        }
    }

    public boolean checkIncomingVectorClockProtocol(VectorClockProtocol input) {

        int vectorValueOfSendingNode = input.getClockVector()[input.getIndexOfNode()];

        // Check right message
        if (!(vectorValueOfSendingNode == (this.clockVector[input.getIndexOfNode()] + 1)))
            return false;
        else {
            // Check right time
            for (int i = 0; i < this.clockVector.length; i++){
                if(i == vectorValueOfSendingNode)
                    i++;
                if (!(this.clockVector[i] == input.getClockVector()[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateVectorClock(int index){
        this.clockVector[index]++;
    }

    @Override
    public void run() {
        while (true) {
            receiveAndCheckMessageFromOtherNode();
        }
    }
}