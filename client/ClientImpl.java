import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.security.Signature;

import interfaces.*;

public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
  private int id;
  ServerInterface serverRef;
  int callbackResource1Id = -1;
  int callbackResource2Id = -1;
  Boolean isHoldingResource1 = false;
  Boolean isHoldingResource2 = false;
  PublicKey serverPublicKey = null;

  public ClientImpl(int clientId) throws Exception {
    super();
    this.id = clientId;

    Registry nameRegistryRef = LocateRegistry.getRegistry();

    serverRef = (ServerInterface) nameRegistryRef.lookup("aplicacao2");
  }

  public void askToAccessResource1() {
    if (callbackResource1Id != -1) {
      System.out.println("Você já está em posse do recurso 1 ou já registrou interesse.\n");
      return;
    }

    try {
      callbackResource1Id = serverRef.registerResource1(this);
    } catch (Exception e) {
      System.out.println("Error askToAccessResource1, " + e.getMessage());
      return;
    }

    System.out.println("Foi registrado com sucesso o interesse no recurso 1.\n");
  }

  public void askToAccessResource2() {
    if (callbackResource2Id != -1) {
      System.out.println("Você já está em posse do recurso 2 ou já registrou interesse.\n");
      return;
    }

    try {
      callbackResource2Id = serverRef.registerResource2(this);
    } catch (Exception e) {
      System.out.println("Error askToAccessResource2, " + e.getMessage());
      return;
    }

    System.out.println("Foi registrado com sucesso o interesse no recurso 2.\n");
  }

  public void releaseResource1() {
    if (!isHoldingResource1) {
      System.out.println("Você não está em posse do recurso 1.\n");
      return;
    }

    try {
      serverRef.deregisterResource1(callbackResource1Id);
    } catch (Exception e) {
      System.out.println("Error releaseResource1, " + e.getMessage());
      return;
    }

    callbackResource1Id = -1;
    isHoldingResource1 = false;

    System.out.println("Recurso 1 foi liberado com sucesso.\n");
  }

  public void releaseResource2() {
    if (!isHoldingResource2) {
      System.out.println("Você não está em posse do recurso 2.\n");
      return;
    }

    try {
      serverRef.deregisterResource2(callbackResource2Id);
    } catch (Exception e) {
      System.out.println("Error releaseResource2, " + e.getMessage());
      return;
    }

    callbackResource1Id = -1;
    isHoldingResource2 = false;

    System.out.println("Recurso 2 foi liberado com sucesso.\n");
  }

  public void notifyResource1Available(
          String message,
          PublicKey serverPubKey,
          byte[] signature
  ) throws RemoteException {
    if (serverPublicKey == null) {
      System.out.println("Recebida a chave pública do servidor.\n");
      serverPublicKey = serverPubKey;
    }

    try {
      Signature clientSignature = Signature.getInstance("DSA");
      clientSignature.initVerify(serverPublicKey);
      clientSignature.update(message.getBytes());

      if (clientSignature.verify(signature)) {
        System.out.println("Mensagem autenticada, servidor permitiu acesso ao recurso 1!\n");
        isHoldingResource1 = true;
      } else {
        System.out.println("A Mensagem recebida NÃO pode ser validada.\n");
      }
    } catch (Exception e) {
      System.out.println("Error validating resource1 message, " + e.getMessage());
    }
  }

  public void notifyResource2Available(
          String message,
          PublicKey serverPubKey,
          byte[] signature
  ) throws RemoteException {
    if (serverPublicKey == null) {
      System.out.println("Recebida a chave pública do servidor.\n");
      serverPublicKey = serverPubKey;
    }

    try {
      Signature clientSignature = Signature.getInstance("DSA");
      clientSignature.initVerify(serverPublicKey);
      clientSignature.update(message.getBytes());

      if (clientSignature.verify(signature)) {
        System.out.println("Mensagem autenticada, servidor permitiu acesso ao recurso 2!\n");
        isHoldingResource2 = true;
      } else {
        System.out.println("A Mensagem recebida com relação ao recurso 1 NÃO pode ser validada.\n");
      }
    } catch (Exception e) {
      System.out.println("Error validating resource2 message, " + e.getMessage());
    }
  }

  public void notifyResource1Timeout() throws RemoteException {
    System.out.println("Servidor indicou que foi atingido o tempo limite, você não está mais em posse do recurso 1.\n");

    isHoldingResource1 = false;
    callbackResource1Id = -1;
  }

  public void notifyResource2Timeout() throws RemoteException {
    System.out.println("Servidor indicou que foi atingido o tempo limite, você não está mais em posse do recurso 2.\n");

    isHoldingResource2 = false;
    callbackResource2Id = -1;
  }

  public int getId() throws RemoteException {
    return this.id;
  }
}
