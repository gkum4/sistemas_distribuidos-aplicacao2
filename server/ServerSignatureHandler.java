import java.security.*;

public class ServerSignatureHandler {
  public PublicKey publicKey;
  public PrivateKey privateKey;

  public ServerSignatureHandler() throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");

    SecureRandom secureRand = new SecureRandom();
    kpg.initialize(512, secureRand);
    KeyPair keyPair = kpg.generateKeyPair();

    this.publicKey = keyPair.getPublic();
    privateKey = keyPair.getPrivate();
  }

  public byte[] getSignature(String message) throws
          NoSuchAlgorithmException,
          SignatureException,
          InvalidKeyException
  {
    Signature signature = Signature.getInstance("DSA");
    signature.initSign(privateKey);
    signature.update(message.getBytes());

    return signature.sign();
  }
}
