package amz.kit.test;

import amz.kit.crypto.AmzCrypto;
import amz.kit.entity.AmzEncryptedMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class AmzCryptoTest { // TODO more unit tests
    @Test
    public void TestEncryptTextMessage() {
        String message = "Test message";

        byte[] myPrivateKey = AmzCrypto.getInstance().getPrivateKey("example1");
        byte[] myPublicKey = AmzCrypto.getInstance().getPublicKey(myPrivateKey);
        byte[] theirPrivateKey = AmzCrypto.getInstance().getPrivateKey("example2");
        byte[] theirPublicKey = AmzCrypto.getInstance().getPublicKey(theirPrivateKey);

        AmzEncryptedMessage amzEncryptedMessage = AmzCrypto.getInstance().encryptTextMessage(message, myPrivateKey, theirPublicKey);

        String result1 = new String(AmzCrypto.getInstance().decryptMessage(amzEncryptedMessage, myPrivateKey, theirPublicKey));
        String result2 = new String(AmzCrypto.getInstance().decryptMessage(amzEncryptedMessage, theirPrivateKey, myPublicKey));

        Assert.assertEquals(message, result1);
        Assert.assertEquals(message, result2);
    }

    @Test
    public void TestSignAndVerify() {
        byte[] myMessage = "A Message".getBytes(StandardCharsets.UTF_8);
        byte[] myPrivateKey = AmzCrypto.getInstance().getPrivateKey("example1");
        byte[] myPublic = AmzCrypto.getInstance().getPublicKey(myPrivateKey);
        byte[] signature = AmzCrypto.getInstance().sign(myMessage, myPrivateKey);
        Assert.assertTrue(AmzCrypto.getInstance().verify(signature, myMessage, myPublic, true));
    }
}
