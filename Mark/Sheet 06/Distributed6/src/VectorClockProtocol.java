import java.io.Serializable;

public class VectorClockProtocol implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private int portIndex;
	private int[] vector;
	private String message;
	
	public VectorClockProtocol(int portIndex, int[] vector, String message){
		this.portIndex = portIndex;
		this.vector = vector;
		this.message = message;
	}

	
	public int getPortIndex() {
		return portIndex;
	}

	public void setPortIndex(int portIndex) {
		this.portIndex = portIndex;
	}

	public int[] getVector() {
		return vector;
	}

	public void setVector(int[] vector) {
		this.vector = vector;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
