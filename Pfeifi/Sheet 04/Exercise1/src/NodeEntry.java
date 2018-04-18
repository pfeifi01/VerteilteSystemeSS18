
public class NodeEntry {

    public String name;
    public String ip;
    public int port;

    public NodeEntry(String ip, int port, String name){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public NodeEntry(String ip, int port){
        this.ip = ip;
        this.port = port;
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
}
