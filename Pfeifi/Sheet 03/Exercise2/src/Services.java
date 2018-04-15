import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Services extends Remote {
    String sort(String inputString, AccessRight right) throws RemoteException;
    String compute(int time, AccessRight right) throws RemoteException;
}