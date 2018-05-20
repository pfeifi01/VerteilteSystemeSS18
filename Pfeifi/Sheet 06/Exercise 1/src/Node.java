import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Author: Martin Pfeifhofer © SS 2018 **/

public class Node {

    private Application application;
    private Middleware middleware;

    private int[] listOfPorts = {8900,9000,9100};
    private int indexOfPort;
    private String ip;

    public Node(int indexOfPort){

        this.ip = "localhost";
        this.indexOfPort=indexOfPort;
    }

    public static void main(String[] args) {

        System.out.println("Enter the PortIndex for the Node (1,2 or 3) ");

        int portIndex = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            portIndex = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        portIndex--;

        Node n = new Node(portIndex);
        n.initNode();
    }

    public void initNode(){

        application = new Application(this);
        middleware = new Middleware(this, indexOfPort);
        application.start();
        middleware.start();
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Middleware getMiddleware() {
        return middleware;
    }

    public void setMiddleware(Middleware middlewarew) {
        this.middleware = middlewarew;
    }

    public int getIndexOfPort() {
        return indexOfPort;
    }

}
