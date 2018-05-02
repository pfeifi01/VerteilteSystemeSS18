import javafx.util.Pair;

public class FingerTableEntry {

    private int start;
    private Pair<Integer,Integer> interval;
    private int successor;

    public FingerTableEntry(int start, Pair<Integer,Integer> interval, int successor){

        this.start = start;
        this.interval = interval;
        this.successor = successor;
    }
}