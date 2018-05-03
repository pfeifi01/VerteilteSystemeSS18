import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {

        int amountOfAdresses = 16; // 2^4
        int[] indicesOfNodes = {2, 4, 6, 10, 13};
        Node[] globalTable = new Node[amountOfAdresses];

        System.out.println("** Chord Initialized **\n");

        for (int i = 0; i < indicesOfNodes.length; i++) {
            globalTable[indicesOfNodes[i]] = new Node(indicesOfNodes[i], globalTable);
        }

        for (int i = 0; i < indicesOfNodes.length; i++) {
            globalTable[indicesOfNodes[i]].initFingerTable(indicesOfNodes);
        }

        while (true) {

            System.out.println("** Enter 'add' or 'remove' to change the structure of the Chord. Enter 'exit' to quit **");
            String input = "";
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.equals("add") || input.equals("remove")) {
                if (input.equals("add")) {
                    System.out.println("** Chord: " + formatChord(indicesOfNodes) +" **");
                    System.out.println("** Enter the value of the node you want to add **");
                    int addInput = 0;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        addInput = Integer.parseInt(reader.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("** Adding Node " + addInput +" to the Chord**\n");
                    // TODO: Add a node to the network

                }else if (input.equals("remove")) {
                    System.out.println("** Chord: " + formatChord(indicesOfNodes) +" **");
                    System.out.println("** Enter the value of the node you want to remove **");
                    int removeInput = 0;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        removeInput = Integer.parseInt(reader.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("** Removing Node" + removeInput +" from the Chord**\n");
                    // TODO: Remove a node from the network
                }
                // Let Nodes update their Tables by comparing with Updated Table
                for (int i = 0; i < indicesOfNodes.length; i++) {
                    globalTable[indicesOfNodes[i]].updateGlobalTable(globalTable);
                }
                System.out.println();
            } else {
                if(input.equals("exit")) {
                    System.out.println("\n**Chord Shutdown **");
                    break;
                }
                System.out.println("** Wrong Input - Try Again **");
            }
        }
    }

    public static String formatChord(int[] indicesOfNodes){
        String chord = "[";
        for (int i = 0; i < indicesOfNodes.length; i++) {
            if(!(i == indicesOfNodes.length-1))
                chord += indicesOfNodes[i] + ", ";
            else
                chord += indicesOfNodes[i];
        }
        return chord + "]";
    }
}
