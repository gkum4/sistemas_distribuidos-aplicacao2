import java.util.Timer;
import java.util.TimerTask;

import interfaces.*;

public class Resource2TimeoutManager extends TimerTask {
  Timer timer;
  ClientWaiting client;
  ServerInterface server;

  public Resource2TimeoutManager(Timer timer, ClientWaiting client, ServerInterface server) {
    this.timer = timer;
    this.client = client;
    this.server = server;
  }

  public void run() {
    System.out.println("Tempo limite de liberação do recurso 2 atingido.\n");
    try {
      client.client.notifyResource2Timeout();
      server.deregisterResource2(client.callbackId);
    } catch (Exception e) {
      System.out.println("Error in Resource2TimeoutManager, " + e.getMessage());
    }

    timer.cancel();
  }
}
