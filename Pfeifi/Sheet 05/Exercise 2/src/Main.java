import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        int amountOfAdresses = 16; // 2^4
        ArrayList<Integer> indicesOfNodes = new ArrayList(Arrays.asList(new Integer[] {2,4,6,10,13}));
        Node[] globalTable = new Node[amountOfAdresses];

        System.out.println("** Chord Initialized **\n");

        for (int i = 0; i < indicesOfNodes.size(); i++) {
            globalTable[indicesOfNodes.get(i)] = new Node(indicesOfNodes.get(i), globalTable, indicesOfNodes);
        }

        for (int i = 0; i < indicesOfNodes.size(); i++) {
            globalTable[indicesOfNodes.get(i)].initSuccessorAndPredecessor(indicesOfNodes);
        }
        System.out.println();



        while (true) {

            System.out.println("** Enter 'add' to add a node the Chord. Enter 'exit' to quit **");
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
                    int newIndex = 0;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        newIndex = Integer.parseInt(reader.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    indicesOfNodes.add(newIndex);
                    globalTable[newIndex] = new Node(newIndex, globalTable);

                    System.out.println("** Choose the predecessor**");
                    int predecessorInput = 0;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        predecessorInput = Integer.parseInt(reader.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    globalTable[newIndex].setPredecessor(predecessorInput);
                    System.out.println("** Added new Node [" + newIndex +"] to the Chord**\n");
                    System.out.println("** New Node [" + newIndex + "] is asking its Predecessor Node [" + globalTable[newIndex].getPredecessor() + "] for its Successor**");
                    globalTable[newIndex].setSuccessor(globalTable[globalTable[newIndex].getPredecessor()].getSuccessor());
                    System.out.println("** Node [" + predecessorInput +"] is updating its Successor to be the new Node [" + newIndex + "]**");
                    System.out.println("** Old Successor of Node ["+ predecessorInput +"] is updating its predecessor as the new Node [" + newIndex + "] **");
                    globalTable[globalTable[newIndex].getPredecessor()].setSuccessor(newIndex);
                    globalTable[globalTable[newIndex].getSuccessor()].setPredecessor(newIndex);
                    System.out.println("** New Node [" + newIndex +"] creates its Finger Table **\n");
                    globalTable[newIndex].initFingerTable(indicesOfNodes);
                    Collections.sort(indicesOfNodes);
                    // Let Nodes update their Tables by comparing with Updated Table, that a node was added
                    int start = indicesOfNodes.indexOf(newIndex);
                    int c = start;
                    while(true){
                        c%= indicesOfNodes.size();
                        globalTable[indicesOfNodes.get(c)].updateFingerTable(indicesOfNodes);
                        if(c == start -1)
                            break;
                        c++;

                    }
                }
                System.out.println();

                for (int i = 0; i < indicesOfNodes.size(); i++) {
                    System.out.println("**Printing Table of Node [" + indicesOfNodes.get(i) + "] **\n" + globalTable[indicesOfNodes.get(i)].printFingerTable());
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

    public static String formatChord(ArrayList<Integer> indicesOfNodes){

        String chord = "[";
        for (int i = 0; i < indicesOfNodes.size(); i++) {
            if(!(i == indicesOfNodes.size()-1))
                chord += indicesOfNodes.get(i) + ", ";
            else
                chord += indicesOfNodes.get(i);
        }
        return chord + "]";
    }
}
