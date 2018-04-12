import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Services extends Remote {

    String compute(int time) throws RemoteException;
    String sort(String inputString) throws RemoteException;

}