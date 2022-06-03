import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import interfaces.*;

public class Server {
  public static void main(String[] args) {
    System.out.println("Servidor inicializado!\n");

    try {
      Registry nameServiceReference = LocateRegistry.createRegistry(1099);

      ServerInterface serverReference = new ServerImpl();

      nameServiceReference.rebind("aplicacao2", serverReference);
    } catch (Exception e) {
      System.out.println("Error running Server, " + e.getMessage());
    }
  }
}