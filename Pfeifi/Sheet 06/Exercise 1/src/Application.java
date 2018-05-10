import java.util.Random;

public class Application extends Thread{

    private Node node;

    public Application(Node node){
        this.node = node;
    }

    @Override
    public void run() {
        while(true){
            sendMessage();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(){
        String message = generateMessage();
        System.out.println("Node [" + (this.node.getIndexOfPort()+1) +"]: Application sends message [" + message +"] to Middleware");
        this.node.getMiddleware().sendMessageFromApplicationToOtherNode(message);
    }

    public void receiveMessage(String message){
        System.out.println("Node [" + (this.node.getIndexOfPort()+1) +"]: Application received message [" + message +"] from Middleware");
    }

    private String generateMessage() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder message = new StringBuilder();
        Random rnd = new Random();
        while (message.length() < 11) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            message.append(chars.charAt(index));
        }
        return message.toString();
    }
}
