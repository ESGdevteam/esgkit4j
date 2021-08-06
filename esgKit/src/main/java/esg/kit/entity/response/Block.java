package esg.kit.entity.response;

import esg.kit.crypto.EsgCrypto;
import esg.kit.entity.EsgAddress;
import esg.kit.entity.EsgID;
import esg.kit.entity.EsgTimestamp;
import esg.kit.entity.EsgValue;
import esg.kit.entity.response.http.BlockResponse;
import esg.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Block {
    private final BigInteger nonce;
    private final EsgAddress generator;
    private final EsgID id;
    private final EsgID nextBlock;
    private final EsgID previousBlock;
    private final EsgID[] transactions;
    private final EsgTimestamp timestamp;
    private final EsgValue blockReward;
    private final EsgValue totalAmount;
    private final EsgValue totalFee;
    private final byte[] generationSignature;
    private final byte[] generatorPublicKey;
    private final byte[] payloadHash;
    private final byte[] previousBlockHash;
    private final byte[] signature;
    private final int height;
    private final int payloadLength;
    private final int scoopNum;
    private final int version;
    private final long baseTarget;

    public Block(BigInteger nonce, EsgAddress generator, EsgID id, EsgID nextBlock, EsgID previousBlock, EsgID[] transactions, EsgTimestamp timestamp, EsgValue blockReward, EsgValue totalAmount, EsgValue totalFee, byte[] generationSignature, byte[] generatorPublicKey, byte[] payloadHash, byte[] previousBlockHash, byte[] signature, int height, int payloadLength, int scoopNum, int version, long baseTarget) {
        this.nonce = nonce;
        this.generator = generator;
        this.id = id;
        this.nextBlock = nextBlock;
        this.previousBlock = previousBlock;
        this.transactions = transactions;
        this.timestamp = timestamp;
        this.blockReward = blockReward;
        this.totalAmount = totalAmount;
        this.totalFee = totalFee;
        this.generationSignature = generationSignature;
        this.generatorPublicKey = generatorPublicKey;
        this.payloadHash = payloadHash;
        this.previousBlockHash = previousBlockHash;
        this.signature = signature;
        this.height = height;
        this.payloadLength = payloadLength;
        this.scoopNum = scoopNum;
        this.version = version;
        this.baseTarget = baseTarget;
    }

    public Block(BlockResponse blockResponse) {
        this.nonce = new BigInteger(blockResponse.getNonce());
        this.generator = EsgAddress.fromEither(blockResponse.getGenerator());
        this.id = EsgID.fromLong(blockResponse.getBlock());
        this.nextBlock = EsgID.fromLong(blockResponse.getNextBlock());
        this.previousBlock = EsgID.fromLong(blockResponse.getPreviousBlock());
        this.transactions = Arrays.stream(blockResponse.getTransactions())
                .map(EsgID::fromLong)
                .toArray(EsgID[]::new);
        this.timestamp = new EsgTimestamp(blockResponse.getTimestamp());
        this.blockReward = EsgValue.fromEsg(blockResponse.getBlockReward());
        this.totalAmount = EsgValue.fromPlanck(blockResponse.getTotalAmountNQT());
        this.totalFee = EsgValue.fromPlanck(blockResponse.getTotalFeeNQT());
        this.generationSignature = Hex.decode(blockResponse.getGenerationSignature());
        this.generatorPublicKey = Hex.decode(blockResponse.getGeneratorPublicKey());
        this.payloadHash = Hex.decode(blockResponse.getPayloadHash());
        this.previousBlockHash = Hex.decode(blockResponse.getPreviousBlockHash());
        this.signature = Hex.decode(blockResponse.getBlockSignature());
        this.height = blockResponse.getHeight();
        this.payloadLength = blockResponse.getPayloadLength();
        this.scoopNum = blockResponse.getScoopNum();
        this.version = blockResponse.getVersion();
        this.baseTarget = blockResponse.getBaseTarget();
    }

    public Block(BrsApi.Block block) {
        EsgCrypto esgCrypto = EsgCrypto.getInstance();
        this.nonce = new BigInteger(Long.toUnsignedString(block.getNonce()));
        this.generator = esgCrypto.getEsgAddressFromPublic(block.getGeneratorPublicKey().toByteArray());
        this.id = EsgID.fromLong(block.getId());
        this.nextBlock = EsgID.fromLong(block.getNextBlockId());
        this.previousBlock = block.getPreviousBlockHash().size() == 0 ? EsgID.fromLong(0) : esgCrypto.hashToId(block.getPreviousBlockHash().toByteArray());
        this.transactions = block.getTransactionIdsList()
                .stream()
                .map(EsgID::fromLong)
                .toArray(EsgID[]::new);
        this.timestamp = new EsgTimestamp(block.getTimestamp());
        this.blockReward = EsgValue.fromPlanck(block.getBlockReward());
        this.totalAmount = EsgValue.fromPlanck(block.getTotalAmount());
        this.totalFee = EsgValue.fromPlanck(block.getTotalFee());
        this.generationSignature = block.getGenerationSignature().toByteArray();
        this.generatorPublicKey = block.getGeneratorPublicKey().toByteArray();
        this.payloadHash = block.getPayloadHash().toByteArray();
        this.previousBlockHash = block.getPreviousBlockHash().toByteArray();
        this.signature = block.getBlockSignature().toByteArray();
        this.height = block.getHeight();
        this.payloadLength = block.getPayloadLength();
        this.scoopNum = block.getScoop();
        this.version = block.getVersion();
        this.baseTarget = block.getBaseTarget();
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public EsgAddress getGenerator() {
        return generator;
    }

    public EsgID getId() {
        return id;
    }

    public EsgID getNextBlock() {
        return nextBlock;
    }

    public EsgID getPreviousBlock() {
        return previousBlock;
    }

    public EsgID[] getTransactions() {
        return transactions;
    }

    public EsgTimestamp getTimestamp() {
        return timestamp;
    }

    public EsgValue getBlockReward() {
        return blockReward;
    }

    public EsgValue getTotalAmount() {
        return totalAmount;
    }

    public EsgValue getTotalFee() {
        return totalFee;
    }

    public byte[] getGenerationSignature() {
        return generationSignature;
    }

    public byte[] getGeneratorPublicKey() {
        return generatorPublicKey;
    }

    public byte[] getPayloadHash() {
        return payloadHash;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public int getHeight() {
        return height;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public int getScoopNum() {
        return scoopNum;
    }

    public int getVersion() {
        return version;
    }

    public long getBaseTarget() {
        return baseTarget;
    }
}
