package esg.kit.test;

import esg.kit.crypto.EsgCrypto;
import esg.kit.entity.EsgAddress;
import esg.kit.entity.EsgID;
import esg.kit.entity.EsgValue;
import esg.kit.entity.response.*;
import esg.kit.entity.response.attachment.ATCreationAttachment;
import esg.kit.entity.response.attachment.MultiOutAttachment;
import esg.kit.entity.response.attachment.MultiOutSameAttachment;
import esg.kit.service.EsgNodeService;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class EsgNodeServiceTest {

    private final EsgNodeService esgNodeService = getEsgNodeService();
    private final EsgCrypto esgCrypto = EsgCrypto.getInstance();

    protected abstract EsgNodeService getEsgNodeService();

    @Test
    public void testEsgServiceGetBlock() {
        Block blockIDResponse = RxTestUtils.testSingle(esgNodeService.getBlock(TestVariables.EXAMPLE_BLOCK_ID));
        Block blockHeightResponse = RxTestUtils.testSingle(esgNodeService.getBlock(TestVariables.EXAMPLE_BLOCK_HEIGHT));
        Block blockTimestampResponse = RxTestUtils.testSingle(esgNodeService.getBlock(TestVariables.EXAMPLE_TIMESTAMP));
    }

    @Test
    public void testEsgServiceGetBlockID() {
        EsgID blockIDResponse = RxTestUtils.testSingle(esgNodeService.getBlockId(TestVariables.EXAMPLE_BLOCK_HEIGHT));
    }

    @Test
    public void testEsgServiceGetBlocks() {
        Block[] blocksResponse = RxTestUtils.testSingle(esgNodeService.getBlocks(0, 99)); // BRS caps this call at 99 blocks.
        //assertEquals(100, blocksResponse.getBlocks().length);
    }

    @Test
    public void testEsgServiceGetConstants() {
        Constants constantsResponse = RxTestUtils.testSingle(esgNodeService.getConstants());
    }

    @Test
    public void testEsgServiceGetAccount() {
        Account accountResponse = RxTestUtils.testSingle(esgNodeService.getAccount(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAccountATs() {
        AT[] accountATsResponse = RxTestUtils.testSingle(esgNodeService.getAccountATs(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAccountBlockIDs() {
        EsgID[] accountBlockIDsResponse = RxTestUtils.testSingle(esgNodeService.getAccountBlockIDs(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAccountBlocks() {
        Block[] accountBlocksResponse = RxTestUtils.testSingle(esgNodeService.getAccountBlocks(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAccountTransactionIDs() {
        EsgID[] accountTransactionIDsResponse = RxTestUtils.testSingle(esgNodeService.getAccountTransactionIDs(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAccountTransactions() {
        Transaction[] accountTransactionsResponse = RxTestUtils.testSingle(esgNodeService.getAccountTransactions(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAccountWithRewardRecipient() {
        EsgAddress[] accountsWithRewardRecipientResponse = RxTestUtils.testSingle(esgNodeService.getAccountsWithRewardRecipient(TestVariables.EXAMPLE_POOL_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceGetAT() {
        AT accountATsResponse = RxTestUtils.testSingle(esgNodeService.getAt(TestVariables.EXAMPLE_AT_ID));
    }

    @Test
    public void testEsgServiceGetAtIDs() {
        EsgAddress[] atIDsResponse = RxTestUtils.testSingle(esgNodeService.getAtIds());
    }

    @Test
    public void testEsgServiceGetTransaction() {
        Transaction transactionIdTransactionResponse = RxTestUtils.testSingle(esgNodeService.getTransaction(TestVariables.EXAMPLE_TRANSACTION_ID));
        Transaction fullHashTransactionResponse = RxTestUtils.testSingle(esgNodeService.getTransaction(TestVariables.EXAMPLE_TRANSACTION_FULL_HASH));

        Transaction multiOutTransactionResponse = RxTestUtils.testSingle(esgNodeService.getTransaction(TestVariables.EXAMPLE_MULTI_OUT_TRANSACTION_ID));
        assertEquals(MultiOutAttachment.class, multiOutTransactionResponse.getAttachment().getClass());
        assertEquals(22, ((MultiOutAttachment) multiOutTransactionResponse.getAttachment()).getOutputs().size());

        Transaction multiOutSameTransactionResponse = RxTestUtils.testSingle(esgNodeService.getTransaction(TestVariables.EXAMPLE_MULTI_OUT_SAME_TRANSACTION_ID));
        assertEquals(MultiOutSameAttachment.class, multiOutSameTransactionResponse.getAttachment().getClass());
        assertEquals(128, ((MultiOutSameAttachment) multiOutSameTransactionResponse.getAttachment()).getRecipients().length);

        Transaction atCreationTransactionResponse = RxTestUtils.testSingle(esgNodeService.getTransaction(TestVariables.EXAMPLE_AT_CREATION_TRANSACTION_ID));
        assertEquals(ATCreationAttachment.class, atCreationTransactionResponse.getAttachment().getClass());
    }

    @Test
    public void testEsgServiceGetTransactionBytes() {
        byte[] transactionBytesResponse = RxTestUtils.testSingle(esgNodeService.getTransactionBytes(TestVariables.EXAMPLE_TRANSACTION_ID));
    }

    @Test
    public void testEsgServiceGenerateTransaction() {
        // TODO test with zero amounts
        byte[] withoutMessageAmount = RxTestUtils.testSingle(esgNodeService.generateTransaction(TestVariables.EXAMPLE_ACCOUNT_ID, TestVariables.EXAMPLE_ACCOUNT_PUBKEY, EsgValue.fromEsg(1), EsgValue.fromEsg(1), 1440));
        byte[] withStringMessage = RxTestUtils.testSingle(esgNodeService.generateTransactionWithMessage(TestVariables.EXAMPLE_ACCOUNT_ID, TestVariables.EXAMPLE_ACCOUNT_PUBKEY, EsgValue.fromEsg(1), EsgValue.fromEsg(1), 1440, "Test Transaction"));
        byte[] withBytesMessage = RxTestUtils.testSingle(esgNodeService.generateTransactionWithMessage(TestVariables.EXAMPLE_ACCOUNT_ID, TestVariables.EXAMPLE_ACCOUNT_PUBKEY, EsgValue.fromEsg(1), EsgValue.fromEsg(1), 1440, TestVariables.EXAMPLE_ACCOUNT_PUBKEY));
    }

    @Test
    public void testEsgServiceSuggestFee() {
        FeeSuggestion suggestFeeResponse = RxTestUtils.testSingle(esgNodeService.suggestFee());
        assertTrue(suggestFeeResponse.getPriorityFee().compareTo(suggestFeeResponse.getStandardFee()) >= 0);
        assertTrue(suggestFeeResponse.getStandardFee().compareTo(suggestFeeResponse.getCheapFee()) >= 0);
    }

    @Test
    public void testEsgServiceGetMiningInfo() {
        MiningInfo miningInfoResponse = RxTestUtils.testObservable(esgNodeService.getMiningInfo(), 1).get(0);
    }

    @Test
    public void testEsgServiceGetRewardRecipient() {
        EsgAddress rewardRecipientResponse = RxTestUtils.testSingle(esgNodeService.getRewardRecipient(TestVariables.EXAMPLE_ACCOUNT_ID));
    }

    @Test
    public void testEsgServiceSubmitNonce() {
        Long submitNonceResponse = RxTestUtils.testSingle(esgNodeService.submitNonce("example", "0", null));
    }

    @Test
    public void testEsgServiceGenerateMultiOut() {
        Map<EsgAddress, EsgValue> recipients = new HashMap<>();
        recipients.put(esgCrypto.getEsgAddressFromPassphrase("example1"), EsgValue.fromEsg(1));
        recipients.put(esgCrypto.getEsgAddressFromPassphrase("example2"), EsgValue.fromEsg(2));
        byte[] multiOutResponse = RxTestUtils.testSingle(esgNodeService.generateMultiOutTransaction(TestVariables.EXAMPLE_ACCOUNT_PUBKEY, EsgValue.fromPlanck(753000), 1440, recipients));
    }

    @Test
    public void testEsgServiceGenerateMultiOutSame() {
        Set<EsgAddress> recipients = new HashSet<>();
        recipients.add(esgCrypto.getEsgAddressFromPassphrase("example1"));
        recipients.add(esgCrypto.getEsgAddressFromPassphrase("example2"));
        byte[] multiOutSameResponse = RxTestUtils.testSingle(esgNodeService.generateMultiOutSameTransaction(TestVariables.EXAMPLE_ACCOUNT_PUBKEY, EsgValue.fromEsg(1), EsgValue.fromPlanck(753000), 1440, recipients));
    }

    @Test
    public void testEsgServiceGenerateCreateATTransaction() {
        byte[] lotteryAtCode = Hex.decode("1e000000003901090000006400000000000000351400000000000201000000000000000104000000803a0900000000000601000000040000003615000200000000000000260200000036160003000000020000001f030000000100000072361b0008000000020000002308000000090000000f1af3000000361c0004000000020000001e0400000035361700040000000200000026040000007f2004000000050000001e02050000000400000036180006000000020000000200000000030000001a39000000352000070000001b07000000181b0500000012332100060000001a310100000200000000030000001a1a0000003618000a0000000200000020080000000900000023070800000009000000341f00080000000a0000001a78000000341f00080000000a0000001ab800000002000000000400000003050000001a1a000000");
        byte[] lotteryAtCreationBytes = EsgCrypto.getInstance().getATCreationBytes((short) 1, lotteryAtCode, new byte[0], 1, 1, 1, EsgValue.fromEsg(2));
        assertEquals("01000000020001000100010000c2eb0b0000000044011e000000003901090000006400000000000000351400000000000201000000000000000104000000803a0900000000000601000000040000003615000200000000000000260200000036160003000000020000001f030000000100000072361b0008000000020000002308000000090000000f1af3000000361c0004000000020000001e0400000035361700040000000200000026040000007f2004000000050000001e02050000000400000036180006000000020000000200000000030000001a39000000352000070000001b07000000181b0500000012332100060000001a310100000200000000030000001a1a0000003618000a0000000200000020080000000900000023070800000009000000341f00080000000a0000001a78000000341f00080000000a0000001ab800000002000000000400000003050000001a1a00000000", Hex.toHexString(lotteryAtCreationBytes));
        byte[] createATResponse = RxTestUtils.testSingle(esgNodeService.generateCreateATTransaction(TestVariables.EXAMPLE_ACCOUNT_PUBKEY, EsgValue.fromEsg(5), 1440, "TestAT", "An AT For Testing", lotteryAtCreationBytes));
    }
}
