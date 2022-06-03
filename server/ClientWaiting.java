import interfaces.*;

public class ClientWaiting {
  public ClientInterface client;
  public int callbackId;

  public ClientWaiting(ClientInterface client, int callbackId) {
    this.client = client;
    this.callbackId = callbackId;
  }
}
