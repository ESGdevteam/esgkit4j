package amz.kit.test;

import amz.kit.crypto.AmzCrypto;
import amz.kit.entity.AmzAddress;
import amz.kit.entity.AmzID;
import amz.kit.entity.AmzValue;
import amz.kit.entity.response.*;
import amz.kit.entity.response.attachment.ATCreationAttachment;
import amz.kit.entity.response.attachment.MultiOutAttachment;
import amz.kit.entity.response.attachment.MultiOutSameAttachment;
import amz.kit.service.AmzNodeService;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AmzNodeServiceTest {

    private final AmzNodeService amzNodeService = getAmzNodeService();
    private final AmzCrypto amzCrypto = AmzCrypto.getInstance();

    protected abstract AmzNodeService getAmzNodeService();

    @Test
    public void testAmzServiceGetBlock() {
        Block blockIDResponse = RxTestUtils.testSingle(amzNodeService.getBlock(TestVariables.EXAMPLE_BLOCK_ID));
        Block blockHeightResponse = RxTestUtils.testSingle(amzNodeService.getBlock(TestVariables.EXAMPLE_BLOCK_HEIGHT));
        Block blockTimestampResponse = RxTestUtils.testSingle(amzNodeService.getBlock(TestVariables.EXAMPLE_TIMESTAMP));
    }

    @Test
    public void testAmzServiceGetBlockID() {
        AmzID blockIDResponse = RxTestUtils.testSingle(amzNodeService.getBlockId(TestVariables.EXAMPLE_BLOCK_HEIGHT));
    }

    @Test
    public void testAmzServiceGetBlocks() {
        Block[] blocksResponse = RxTestUtils.testSingle(amzNodeService.getBlocks(0, 99)); // BRS caps this call at 99 blocks.
        //assertEquals(100, blocksResponse.getBlocks().length);
    }

    @Test
    public void testAmzServiceGetConstants() {
        Constants constantsResponse = RxTestUtils.testSingle(amzNodeService.getConstants());
    }

    @Test
    public void testAmzServiceGetAccount() {
        Account accountResponse = RxTestUtils.testSingle(amzNodeService.getAccount(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAccountATs() {
        AT[] accountATsResponse = RxTestUtils.testSingle(amzNodeService.getAccountATs(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAccountBlockIDs() {
        AmzID[] accountBlockIDsResponse = RxTestUtils.testSingle(amzNodeService.getAccountBlockIDs(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAccountBlocks() {
        Block[] accountBlocksResponse = RxTestUtils.testSingle(amzNodeService.getAccountBlocks(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAccountTransactionIDs() {
        AmzID[] accountTransactionIDsResponse = RxTestUtils.testSingle(amzNodeService.getAccountTransactionIDs(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAccountTransactions() {
        Transaction[] accountTransactionsResponse = RxTestUtils.testSingle(amzNodeService.getAccountTransactions(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAccountWithRewardRecipient() {
        AmzAddress[] accountsWithRewardRecipientResponse = RxTestUtils.testSingle(amzNodeService.getAccountsWithRewardRecipient(TestVariables.EXAMPLE_POOL_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceGetAT() {
        AT accountATsResponse = RxTestUtils.testSingle(amzNodeService.getAt(TestVariables.EXAMPLE_AT_ID));
    }

    @Test
    public void testAmzServiceGetAtIDs() {
        AmzAddress[] atIDsResponse = RxTestUtils.testSingle(amzNodeService.getAtIds());
    }

    @Test
    public void testAmzServiceGetTransaction() {
        Transaction transactionIdTransactionResponse = RxTestUtils.testSingle(amzNodeService.getTransaction(TestVariables.EXAMPLE_TRANSACTION_ID));
        Transaction fullHashTransactionResponse = RxTestUtils.testSingle(amzNodeService.getTransaction(TestVariables.EXAMPLE_TRANSACTION_FULL_HASH));

        Transaction multiOutTransactionResponse = RxTestUtils.testSingle(amzNodeService.getTransaction(TestVariables.EXAMPLE_MULTI_OUT_TRANSACTION_ID));
        assertEquals(MultiOutAttachment.class, multiOutTransactionResponse.getAttachment().getClass());
        assertEquals(22, ((MultiOutAttachment) multiOutTransactionResponse.getAttachment()).getOutputs().size());

        Transaction multiOutSameTransactionResponse = RxTestUtils.testSingle(amzNodeService.getTransaction(TestVariables.EXAMPLE_MULTI_OUT_SAME_TRANSACTION_ID));
        assertEquals(MultiOutSameAttachment.class, multiOutSameTransactionResponse.getAttachment().getClass());
        assertEquals(128, ((MultiOutSameAttachment) multiOutSameTransactionResponse.getAttachment()).getRecipients().length);

        Transaction atCreationTransactionResponse = RxTestUtils.testSingle(amzNodeService.getTransaction(TestVariables.EXAMPLE_AT_CREATION_TRANSACTION_ID));
        assertEquals(ATCreationAttachment.class, atCreationTransactionResponse.getAttachment().getClass());
    }

    @Test
    public void testAmzServiceGetTransactionBytes() {
        byte[] transactionBytesResponse = RxTestUtils.testSingle(amzNodeService.getTransactionBytes(TestVariables.EXAMPLE_TRANSACTION_ID));
    }

    @Test
    public void testAmzServiceGenerateTransaction() {
        // TODO test with zero amounts
        byte[] withoutMessageAmount = RxTestUtils.testSingle(amzNodeService.generateTransaction(TestVariables.EXAMPLE_ACCOUNT_ID, TestVariables.EXAMPLE_ACCOUNT_PUBKEY, AmzValue.fromAmz(1), AmzValue.fromAmz(1), 1440));
        byte[] withStringMessage = RxTestUtils.testSingle(amzNodeService.generateTransactionWithMessage(TestVariables.EXAMPLE_ACCOUNT_ID, TestVariables.EXAMPLE_ACCOUNT_PUBKEY, AmzValue.fromAmz(1), AmzValue.fromAmz(1), 1440, "Test Transaction"));
        byte[] withBytesMessage = RxTestUtils.testSingle(amzNodeService.generateTransactionWithMessage(TestVariables.EXAMPLE_ACCOUNT_ID, TestVariables.EXAMPLE_ACCOUNT_PUBKEY, AmzValue.fromAmz(1), AmzValue.fromAmz(1), 1440, TestVariables.EXAMPLE_ACCOUNT_PUBKEY));
    }

    @Test
    public void testAmzServiceSuggestFee() {
        FeeSuggestion suggestFeeResponse = RxTestUtils.testSingle(amzNodeService.suggestFee());
        assertTrue(suggestFeeResponse.getPriorityFee().compareTo(suggestFeeResponse.getStandardFee()) >= 0);
        assertTrue(suggestFeeResponse.getStandardFee().compareTo(suggestFeeResponse.getCheapFee()) >= 0);
    }

    @Test
    public void testAmzServiceGetMiningInfo() {
        MiningInfo miningInfoResponse = RxTestUtils.testObservable(amzNodeService.getMiningInfo(), 1).get(0);
    }

    @Test
    public void testAmzServiceGetRewardRecipient() {
        AmzAddress rewardRecipientResponse = RxTestUtils.testSingle(amzNodeService.getRewardRecipient(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testAmzServiceSubmitNonce() {
        Long submitNonceResponse = RxTestUtils.testSingle(amzNodeService.submitNonce("example", "0", null));
    }

    @Test
    public void testAmzServiceGenerateMultiOut() {
        Map<AmzAddress, AmzValue> recipients = new HashMap<>();
        recipients.put(amzCrypto.getAmzAddressFromPassphrase("example1"), AmzValue.fromAmz(1));
        recipients.put(amzCrypto.getAmzAddressFromPassphrase("example2"), AmzValue.fromAmz(2));
        byte[] multiOutResponse = RxTestUtils.testSingle(amzNodeService.generateMultiOutTransaction(TestVariables.EXAMPLE_ACCOUNT_PUBKEY, AmzValue.fromPlanck(753000), 1440, recipients));
    }

    @Test
    public void testAmzServiceGenerateMultiOutSame() {
        Set<AmzAddress> recipients = new HashSet<>();
        recipients.add(amzCrypto.getAmzAddressFromPassphrase("example1"));
        recipients.add(amzCrypto.getAmzAddressFromPassphrase("example2"));
        byte[] multiOutSameResponse = RxTestUtils.testSingle(amzNodeService.generateMultiOutSameTransaction(TestVariables.EXAMPLE_ACCOUNT_PUBKEY, AmzValue.fromAmz(1), AmzValue.fromPlanck(753000), 1440, recipients));
    }

    @Test
    public void testAmzServiceGenerateCreateATTransaction() {
        byte[] lotteryAtCode = Hex.decode("1e000000003901090000006400000000000000351400000000000201000000000000000104000000803a0900000000000601000000040000003615000200000000000000260200000036160003000000020000001f030000000100000072361b0008000000020000002308000000090000000f1af3000000361c0004000000020000001e0400000035361700040000000200000026040000007f2004000000050000001e02050000000400000036180006000000020000000200000000030000001a39000000352000070000001b07000000181b0500000012332100060000001a310100000200000000030000001a1a0000003618000a0000000200000020080000000900000023070800000009000000341f00080000000a0000001a78000000341f00080000000a0000001ab800000002000000000400000003050000001a1a000000");
        byte[] lotteryAtCreationBytes = AmzCrypto.getInstance().getATCreationBytes((short) 1, lotteryAtCode, new byte[0], 1, 1, 1, AmzValue.fromAmz(2));
        assertEquals("01000000020001000100010000c2eb0b0000000044011e000000003901090000006400000000000000351400000000000201000000000000000104000000803a0900000000000601000000040000003615000200000000000000260200000036160003000000020000001f030000000100000072361b0008000000020000002308000000090000000f1af3000000361c0004000000020000001e0400000035361700040000000200000026040000007f2004000000050000001e02050000000400000036180006000000020000000200000000030000001a39000000352000070000001b07000000181b0500000012332100060000001a310100000200000000030000001a1a0000003618000a0000000200000020080000000900000023070800000009000000341f00080000000a0000001a78000000341f00080000000a0000001ab800000002000000000400000003050000001a1a00000000", Hex.toHexString(lotteryAtCreationBytes));
        byte[] createATResponse = RxTestUtils.testSingle(amzNodeService.generateCreateATTransaction(TestVariables.EXAMPLE_ACCOUNT_PUBKEY, AmzValue.fromAmz(5), 1440, "TestAT", "An AT For Testing", lotteryAtCreationBytes));
    }
}
