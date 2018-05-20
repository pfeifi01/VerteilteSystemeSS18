import java.util.Scanner;

public class Node {

	private Application application;
	private Middleware middleware;
	private int[] PORTS = {8080,9000,9090};
	private int portIndex;
	private String IP = "localhost";
	
	public Node(int portIndex){
		this.portIndex = portIndex;
	}
	
	
	public void init(){
		application = new Application(this);		
		middleware = new Middleware(this,this.portIndex);
		
		application.start();
		middleware.start();
	}
	
	public static void main(String[] args){
		
	   	System.out.println("Enter index for port for Client {8080,9000,9090}: ");
    	Scanner in = new Scanner(System.in);
        Node client = new Node(in.nextInt());    
        client.init();
        in.close();
        
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

	public void setMiddleware(Middleware middleware) {
		this.middleware = middleware;
	}
	
	
}
