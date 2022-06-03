package interfaces;
import java.rmi.*;


public interface ServerInterface extends Remote {
  public int registerResource1(ClientInterface clientReference) throws RemoteException;
  public int registerResource2(ClientInterface clientReference) throws RemoteException;
  public void deregisterResource1(int callbackId) throws RemoteException;
  public void deregisterResource2(int callbackId) throws RemoteException;
}