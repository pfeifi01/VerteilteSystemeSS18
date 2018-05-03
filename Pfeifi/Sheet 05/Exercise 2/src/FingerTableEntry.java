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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public Pair<Integer, Integer> getInterval() {
        return interval;
    }

    public void setInterval(Pair<Integer, Integer> interval) {
        this.interval = interval;
    }

    public int getSuccessor() {
        return successor;
    }

    public void setSuccessor(int successor) {
        this.successor = successor;
    }
}