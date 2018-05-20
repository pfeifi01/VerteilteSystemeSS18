import java.io.Serializable;

p/** Author: Martin Pfeifhofer © SS 2018 **/

ublic class VectorClockProtocol implements Serializable {

    private int[] clockVector;
    private String message;
    private int indexOfNode;

    public VectorClockProtocol(int[] clockVector, String message, int indexOfNode){

        this.clockVector = clockVector;
        this.message = message;
        this.indexOfNode = indexOfNode;
    }

    public int[] getClockVector() {
        return clockVector;
    }

    public void setClockVector(int[] clockVector) {
        this.clockVector = clockVector;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIndexOfNode() {
        return indexOfNode;
    }

    public void setIndexOfNode(int indexOfNode) {
        this.indexOfNode = indexOfNode;
    }
}
