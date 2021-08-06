package esg.kit.test;

import esg.kit.crypto.EsgCrypto;
import esg.kit.entity.EsgEncryptedMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class EsgCryptoTest { // TODO more unit tests
    @Test
    public void TestEncryptTextMessage() {
        String message = "Test message";

        byte[] myPrivateKey = EsgCrypto.getInstance().getPrivateKey("example1");
        byte[] myPublicKey = EsgCrypto.getInstance().getPublicKey(myPrivateKey);
        byte[] theirPrivateKey = EsgCrypto.getInstance().getPrivateKey("example2");
        byte[] theirPublicKey = EsgCrypto.getInstance().getPublicKey(theirPrivateKey);

        EsgEncryptedMessage esgEncryptedMessage = EsgCrypto.getInstance().encryptTextMessage(message, myPrivateKey, theirPublicKey);

        String result1 = new String(EsgCrypto.getInstance().decryptMessage(esgEncryptedMessage, myPrivateKey, theirPublicKey));
        String result2 = new String(EsgCrypto.getInstance().decryptMessage(esgEncryptedMessage, theirPrivateKey, myPublicKey));

        Assert.assertEquals(message, result1);
        Assert.assertEquals(message, result2);
    }

    @Test
    public void TestSignAndVerify() {
        byte[] myMessage = "A Message".getBytes(StandardCharsets.UTF_8);
        byte[] myPrivateKey = EsgCrypto.getInstance().getPrivateKey("example1");
        byte[] myPublic = EsgCrypto.getInstance().getPublicKey(myPrivateKey);
        byte[] signature = EsgCrypto.getInstance().sign(myMessage, myPrivateKey);
        Assert.assertTrue(EsgCrypto.getInstance().verify(signature, myMessage, myPublic, true));
    }
}
