import java.util.*;

import interfaces.*;

public class Client {
  // args give process Id (e.g. "1")
  public static void main(String[] args) {
    System.out.println("Seja bem vindo à Aplicação 2 - Middleware!");
    printInstructions();

    Scanner scanner = new Scanner(System.in);

    ClientImpl clientImpl;
    int clientId = Integer.parseInt(args[0]);

    try {
      clientImpl = new ClientImpl(clientId);
    } catch (Exception e) {
      System.out.println("Error initializing ClientImpl, " + e.getMessage());
      return;
    }

    while (true) {
      String userInput = scanner.nextLine();
      System.out.println("");

      if (userInput.equals("1")) {
        clientImpl.askToAccessResource1();
        continue;
      }

      if (userInput.equals("2")) {
        clientImpl.askToAccessResource2();
        continue;
      }

      if (userInput.equals("3")) {
        clientImpl.releaseResource1();
        continue;
      }

      if (userInput.equals("4")) {
        clientImpl.releaseResource2();
        continue;
      }

      System.out.println("Comando inválido.");
      printInstructions();
    }
  }

  private static void printInstructions() {
    System.out.println("Digite:");
    System.out.println("\"1\" para ACESSAR recurso 1.");
    System.out.println("\"2\" para ACESSAR recurso 2.");
    System.out.println("\"3\" para LIBERAR recurso 1 (caso esteja em posse dele).");
    System.out.println("\"4\" para LIBERAR recurso 2 (caso esteja em posse dele).");
  }
}
