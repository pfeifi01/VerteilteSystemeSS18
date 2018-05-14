import java.util.Random;

public class Application extends Thread{

	private Node node;
	
	public Application(Node node){
		this.node = node;
	}
	
	public void getMessage(String str){
		System.err.println("APPLICATION: Received Message from Middleware: " + str + "\n");
		sendMessage();
	}
	
	
	public void sendMessage(){
		String str = generateString();
		System.out.println("APPLICATION: Sent Message to Middleware: " + str);

		node.getMiddleware().getMessageFromApplicationAndSendMessageToAll(str);
	}
	
    public String generateString() {
    	String allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 10) {
            int index = (int) (rnd.nextFloat() * allChars.length());
            str.append(allChars.charAt(index));
        }
        String saltStr = str.toString();
        return saltStr;
    }

	public void run(){
		if(this.node.getMiddleware().getPortIndex() == 0){
			sendMessage();
		}
		while(true){
		}
	}
	
	
}
