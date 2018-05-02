import java.util.ArrayList;

public class Node{

    private int id;
    private int successor;
    private int predecessor;
    private ArrayList<Node> globalTable;
    private FingerTable fingerTable;

    public Node(int id, int successor, int predecessor, ArrayList<Node> globalTable){
        this.id = id;
        this.successor = successor;
        this.predecessor = predecessor;
        this.globalTable = globalTable;
    }
}
