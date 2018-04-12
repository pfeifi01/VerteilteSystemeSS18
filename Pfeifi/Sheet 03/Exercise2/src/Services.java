import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Services extends Remote {

    String sort(String inputString) throws RemoteException;
    String compute(int time) throws RemoteException;

}