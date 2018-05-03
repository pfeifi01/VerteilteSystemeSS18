import javafx.util.Pair;

import java.util.ArrayList;

public class Node{

    private int id;
    private int successor;
    private int predecessor;
    private Node[]  globalTable;
    private ArrayList<FingerTableEntry> fingerTable;

    private int m;
    private int n;

    public Node(int id,  Node[] globalTable){
        this.id = id;
        this.globalTable = globalTable;
        this.fingerTable = new ArrayList<>();
        this.m = (int) (Math.log(globalTable.length)/Math.log(2)); // n = 2^m ---> m = log2(len)
        this.n = globalTable.length;
    }

    public void initFingerTable(int[] indicesOfNodes){

        int start,successor;
        Pair<Integer,Integer> interval;
        for (int i = 1; i <= m; i++){
            start = (id + (int) Math.pow(2,i-1)) % n;
            if(!(i == m))
                interval = new Pair<>(start,(id + (int) Math.pow(2,i)) % n);
            else
                interval = new Pair<>(start,start);
            successor = findSuccessorForFingerTableInit(indicesOfNodes, start);
            fingerTable.add(new FingerTableEntry(start,interval,successor));
            System.out.println("** Node [" + this.id + "] added new Entry {" + start + ",(" + interval.getKey() + "|" + interval.getValue() + ")," + successor +"} to its Fingertable**");
        }
        // TODO: Fix
        //this.predecessor = findPredecessor(this.id);
        //this.successor = findSuccessor(this.id);
        System.out.println();
    }

    public int findSuccessorForFingerTableInit(int[] indicesOfNodes, int index){
        int successor = index + 1;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < indicesOfNodes.length; j++) {
                if (successor == indicesOfNodes[j])
                    return successor;
            }
            successor = (successor + 1) % n;
        }
        return successor;
    }

    public int findSuccessor (int id){
        int temp = findPredecessor(id);
        return globalTable[temp].successor;
    }

    public int findPredecessor(int id){

        int temp = this.id;
        while(!(temp <= id && id <= globalTable[temp].successor)){
            temp = globalTable[temp].closestPrecedingFinger(id);
        }
        return id;
    }

    public int closestPrecedingFinger(int id){

        for(int i = m; i >= 1; i--){
            if(this. id <= fingerTable.get(i).getSuccessor() && fingerTable.get(i).getSuccessor() >= id){
                return fingerTable.get(i).getSuccessor();
            }
        }
        return this.id;
    }

    public void updateGlobalTable(Node[] globalTable){
        //TODO: Add or Remove Node from Global Table by comparing with updated Global Table
        System.out.println("** Node [" + this.id + "] is updating its Fingertable**");
    }
}
