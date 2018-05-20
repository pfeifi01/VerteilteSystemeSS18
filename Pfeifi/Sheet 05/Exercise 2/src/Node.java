import javafx.util.Pair;

import java.util.ArrayList;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Node{

    private int id;
    private int successor;
    private int predecessor;
    private Node[]  globalTable;
    private ArrayList<FingerTableEntry> fingerTable;

    private int m;
    private int n;

    public Node(int id,  Node[] globalTable, ArrayList<Integer> indicesOfNodes){
        this.id = id;
        this.globalTable = globalTable;
        this.fingerTable = new ArrayList<>();
        this.m = (int) (Math.log(globalTable.length)/Math.log(2)); // n = 2^m ---> m = log2(len)
        this.n = globalTable.length;

        initFingerTable(indicesOfNodes);
    }

    public Node(int id,  Node[] globalTable){
        this.id = id;
        this.globalTable = globalTable;
        this.fingerTable = new ArrayList<>();
        this.m = (int) (Math.log(globalTable.length)/Math.log(2)); // n = 2^m ---> m = log2(len)
        this.n = globalTable.length;
    }

    public void initFingerTable(ArrayList<Integer> indicesOfNodes){

        int start,successor;
        Pair<Integer,Integer> interval;
        for (int i = 1; i <= m; i++){
            start = (id + (int) Math.pow(2,i-1)) % n;
            if(!(i == m))
                interval = new Pair<>(start,(id + (int) Math.pow(2,i)) % n);
            else
                interval = new Pair<>(start,this.id);
            successor = findSuccessor(indicesOfNodes, start);
            fingerTable.add(new FingerTableEntry(start,interval,successor));
            System.out.println("** Node [" + this.id + "] added new Entry {" + start + ",(" + interval.getKey() + "|" + interval.getValue() + ")," + successor +"} to its FingerTable **");
        }
        System.out.println();
    }

    public void initSuccessorAndPredecessor(ArrayList<Integer> indicesOfNodes){
        this.predecessor = findPredecessor(indicesOfNodes, this.id);
        this.successor = findSuccessor(indicesOfNodes, this.id);
        System.out.println("** Node [" + this.id + "] has Predecessor Node [" + predecessor + "] and Successor Node [" + successor +"] **");
    }

    public int findPredecessor(ArrayList<Integer> indicesOfNodes, int index){
        int predecessor = (n + index - 1) % n;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < indicesOfNodes.size(); j++) {
                if (predecessor == indicesOfNodes.get(j))
                    return predecessor;
            }
            predecessor = (n + predecessor - 1) % n;
        }
        return predecessor;
    }

    public int findSuccessor(ArrayList<Integer> indicesOfNodes, int index){
        int successor = index + 1;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < indicesOfNodes.size(); j++) {
                if (successor == indicesOfNodes.get(j))
                    return successor;
            }
            successor = (successor + 1) % n;
        }
        return successor;
    }

    public void updateFingerTable(ArrayList<Integer> indicesOfNodes){
        //TODO: Add or Remove Node from Global Table by comparing with updated Global Table
        n = globalTable.length;
        for (int i = 0; i < m; i++){
            Node p = globalTable[findPredecessor(indicesOfNodes,this.id - (int) Math.pow(2,i))];
            while (p.fingerTable.get(i).getSuccessor() > this.id) {
                p.fingerTable.get(i).setSuccessor(this.id);
                p = globalTable[p.getPredecessor()];
            }
        }
        System.out.println("** Node [" + this.id + "] updated its Finger Table **");
    }

    public String printFingerTable(){

        String fingerTableAsString = "\n";
        for (int i = 0; i < fingerTable.size(); i++) {
            fingerTableAsString += "{" + fingerTable.get(i).getStart() + ",(" + fingerTable.get(i).getInterval().getKey() + "|" + fingerTable.get(i).getInterval().getValue() + ")," + fingerTable.get(i).getSuccessor() +"}\n";
        }
        return fingerTableAsString;
    }


    /*  Functions implemented from Paper

    public int findSuccessor (int id){
        int temp = findPredecessor(id);
        return globalTable[temp].successor;
    }



    public int findPredecessor(int id){

        int temp = this.id;
        while(!(checkIfBetween(temp, globalTable[temp].successor,id,true))){
            temp = globalTable[temp].closestPrecedingFinger(id);
        }
        return id;
    }

    public int closestPrecedingFinger(int id){

        for(int i = m-1; i >= 0; i--){
            if(checkIfBetween(this.id,id,fingerTable.get(i).getSuccessor(),false))
                return fingerTable.get(i).getSuccessor();
        }
        return this.id;
    }


    public boolean checkIfBetween(int lower, int upper, int searched, boolean mode){
        int upperCounter = upper;
        if (mode){
            upperCounter++;
        }

        for (int i = lower + 1; i < upperCounter; i++){
            if (searched == (i % n)){
                return true;
            }
            i = (i % n);
        }
        return false;
    } */

    // Getter & Setter

    public int getSuccessor() {
        return successor;
    }

    public void setSuccessor(int successor) {
        this.successor = successor;
    }

    public int getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(int predecessor) {
        this.predecessor = predecessor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
