import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.*;

import interfaces.*;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
  private ServerSignatureHandler signatureHandler;
  private List<ClientWaiting> resource1ClientWaitingList = new Vector<>();
  private List<ClientWaiting> resource2ClientWaitingList = new Vector<>();
  private List<Integer> clientsIdsWithPubKey = new Vector<>();
  private int resource1CallbackId = 0;
  private int resource2CallbackId = 0;
  private Boolean resource1InUse = false;
  private Boolean resource2InUse = false;
  private Timer resource1Timer;
  private Timer resource2Timer;

  public ServerImpl() throws Exception {
    super();

    this.signatureHandler = new ServerSignatureHandler();

    new Thread(resource1NotifyHandler).start();
    new Thread(resource2NotifyHandler).start();
  }

  public int registerResource1(ClientInterface clientReference) throws RemoteException {
    System.out.println("Cliente " + clientReference.getId() + " registrou interesse no recurso 1.\n");

    resource1CallbackId += 1;

    resource1ClientWaitingList.add(new ClientWaiting(clientReference, resource1CallbackId));

    return resource1CallbackId;
  }

  public int registerResource2(ClientInterface clientReference) throws RemoteException {
    System.out.println("Cliente " + clientReference.getId() + " registrou interesse no recurso 2.\n");

    resource2CallbackId += 1;

    resource2ClientWaitingList.add(new ClientWaiting(clientReference, resource2CallbackId));

    return resource2CallbackId;
  }

  public void deregisterResource1(int callbackId) throws RemoteException {
    ClientWaiting clientWaiting = resource1ClientWaitingList
      .stream()
      .filter(c -> c.callbackId == callbackId)
      .findAny()
      .orElse(null);

    if (clientWaiting == null) {
      throw new RemoteException();
    }

    cancelTimer(resource1Timer);

    resource1ClientWaitingList.remove(clientWaiting);
    resource1InUse = false;

    System.out.println("Recurso 1 foi liberado.\n");
  }

  public void deregisterResource2(int callbackId) throws RemoteException {
    ClientWaiting clientWaiting = resource2ClientWaitingList
            .stream()
            .filter(c -> c.callbackId == callbackId)
            .findAny()
            .orElse(null);

    if (clientWaiting == null) {
      throw new RemoteException();
    }

    cancelTimer(resource2Timer);

    resource2ClientWaitingList.remove(clientWaiting);
    resource2InUse = false;

    System.out.println("Recurso 2 foi liberado.\n");
  }

  private Runnable resource1NotifyHandler = () -> {
    while (true) {
      if (resource1InUse) {
        continue;
      }

      if (resource1ClientWaitingList.size() == 0) {
        continue;
      }

      resource1InUse = true;
      ClientWaiting clientWaiting = resource1ClientWaitingList.get(0);

      String message = "Recurso1";
      Boolean alreadySentPubKey = checkIfAlreadySentPubKey(clientWaiting.client);
      PublicKey publicKey = null;

      try {
        if (!alreadySentPubKey) {
          publicKey = signatureHandler.publicKey;
          clientsIdsWithPubKey.add(clientWaiting.client.getId());
        }

        byte[] signature = signatureHandler.getSignature(message);
        clientWaiting.client.notifyResource1Available(message, publicKey, signature);
        scheduleResource1Timeout(clientWaiting);

        System.out.println("Cliente " + clientWaiting.client.getId() + " foi notificado sobre a posse do recurso 1.\n");
      } catch (Exception e) {
        System.out.println("Error notifying client about resource1, " + e.getMessage());
      }
    }
  };

  private Runnable resource2NotifyHandler = () -> {
    while (true) {
      if (resource2InUse) {
        continue;
      }

      if (resource2ClientWaitingList.size() == 0) {
        continue;
      }

      resource2InUse = true;
      ClientWaiting clientWaiting = resource2ClientWaitingList.get(0);

      String message = "Recurso2";
      Boolean alreadySentPubKey = checkIfAlreadySentPubKey(clientWaiting.client);
      PublicKey publicKey = null;

      try {
        if (!alreadySentPubKey) {
          publicKey = signatureHandler.publicKey;
          clientsIdsWithPubKey.add(clientWaiting.client.getId());
        }

        byte[] signature = signatureHandler.getSignature(message);
        clientWaiting.client.notifyResource2Available(message, publicKey, signature);
        scheduleResource2Timeout(clientWaiting);

        System.out.println("Cliente " + clientWaiting.client.getId() + " foi notificado sobre a posse do recurso 2.\n");
      } catch (Exception e) {
        System.out.println("Error notifying client about resource2, " + e.getMessage());
      }
    }
  };

  private void scheduleResource1Timeout(ClientWaiting client) {
    resource1Timer = new Timer();
    resource1Timer.schedule(new Resource1TimeoutManager(resource1Timer, client, this), 10000);
  }

  private void scheduleResource2Timeout(ClientWaiting client) {
    resource2Timer = new Timer();
    resource2Timer.schedule(new Resource2TimeoutManager(resource2Timer, client, this), 10000);
  }

  private void cancelTimer(Timer timer) {
    if (timer == null) {
      return;
    }

    timer.cancel();
  }

  private Boolean checkIfAlreadySentPubKey(ClientInterface client) {
    int clientId;

    try {
      clientId = client.getId();
    } catch (Exception e) {
      System.out.println("Error getting clientId, " + e.getMessage());
      return false;
    }

    Integer checkClient = clientsIdsWithPubKey
            .stream()
            .filter(c -> c == clientId)
            .findAny()
            .orElse(null);

    if (checkClient == null) {
      return false;
    }

    return true;
  }
}
