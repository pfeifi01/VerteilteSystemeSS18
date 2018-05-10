import java.util.Random;
import java.util.UUID;

public class Application extends Thread{

	private Node node;
	
	public Application(Node node){
		this.node = node;
	}
	
	public void getMessage(String str){
		System.out.println("Received Message from Middleware: ");
		System.out.println(str);
	}
	
	
	public void sendMessage(){
		String str = generateString();
		node.getMiddleware().getMessageFromApplicationAndSendMessageToAll(str);
	}
	
    public String generateString() {
    	String allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * allChars.length());
            str.append(allChars.charAt(index));
        }
        String saltStr = str.toString();
        return saltStr;
    }

	public void run(){
		sendMessage();
		while(true){}
	}
	
	
}
