import java.io.Serializable;

public class NodeEntry implements Serializable {

    public String name;
    public String ip;
    public int port;

    public NodeEntry(String ip, int port, String name){
        this.name =  name;
        this.ip = ip;
        this.port = port;
    }

    public NodeEntry (String message){
        String[] messageSplit = message.split(" ");
        this.port =  Integer.parseInt(messageSplit[0]);
        this.name =  messageSplit[2];
        this.ip =  messageSplit[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return (port + " " + ip + " " + name);
    }

    public boolean equals(NodeEntry entry) {
        if(this.port == entry.port){
            return true;
        }
        return false;
    }
}
