import java.rmi.Remote;
import java.rmi.RemoteException;

/** Author: Martin Pfeifhofer © SS 2018 **/

public interface Services extends Remote {

    String compute(int time) throws RemoteException;
    String sort(String inputString) throws RemoteException;

}