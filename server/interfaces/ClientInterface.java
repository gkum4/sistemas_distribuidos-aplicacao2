package interfaces;
import java.rmi.*;
import java.security.PublicKey;

public interface ClientInterface extends Remote {
  public void notifyResource1Available(
          String message,
          PublicKey serverPubKey,
          byte[] signature
  ) throws RemoteException;
  public void notifyResource2Available(
          String message,
          PublicKey serverPubKey,
          byte[] signature
  ) throws RemoteException;
  public void notifyResource1Timeout() throws RemoteException;
  public void notifyResource2Timeout() throws RemoteException;
  public int getId() throws RemoteException;
}
