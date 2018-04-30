import java.io.Serializable;

public class TableEntry implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private int port;
	private String ip;
	
	
	public TableEntry(String name, String ip, int port) {
		super();
		this.port = port;
		this.ip = ip;
		this.name = name;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(TableEntry other){
		if(this.getName().equals(other.getName())){
			return true;
		}
		return false;
	}
	
	
}
