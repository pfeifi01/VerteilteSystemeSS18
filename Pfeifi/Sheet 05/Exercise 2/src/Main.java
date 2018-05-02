import java.util.ArrayList;

public class Main {

    public static int amountOfNodes = 16;
    public static Node[] nodes = new Node[amountOfNodes];
    public static ArrayList<Node> globalTable;

    public static void main(String[] args) {

        // TODO: Init Global Table

        // TODO: Assign FingerTables with FingerTableEntries to Nodes

        for (int i = 0; i < amountOfNodes; i++){
            if(i == 0)
                nodes[i] = new Node(i, i +1,amountOfNodes - 1,globalTable);
            else if(i == amountOfNodes - 1)
                nodes[i] = new Node(i, 0,i - 1,globalTable);
            else
                nodes[i] = new Node(i, i +1,i - 1,globalTable);
        }

        // TODO: Stuff

    }
}
