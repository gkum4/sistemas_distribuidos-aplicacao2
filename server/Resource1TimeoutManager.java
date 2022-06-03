import java.util.Timer;
import java.util.TimerTask;

import interfaces.*;

public class Resource1TimeoutManager extends TimerTask {
  Timer timer;
  ClientWaiting client;
  ServerInterface server;

  public Resource1TimeoutManager(Timer timer, ClientWaiting client, ServerInterface server) {
    this.timer = timer;
    this.client = client;
    this.server = server;
  }

  public void run() {
    System.out.println("Tempo limite de liberação do recurso 1 atingido.\n");
    try {
      client.client.notifyResource1Timeout();
      server.deregisterResource1(client.callbackId);
    } catch (Exception e) {
      System.out.println("Error in Resource1TimeoutManager, " + e.getMessage());
    }

    timer.cancel();
  }
}
