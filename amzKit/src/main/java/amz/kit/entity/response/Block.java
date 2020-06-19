package amz.kit.entity.response;

import amz.kit.crypto.AmzCrypto;
import amz.kit.entity.AmzAddress;
import amz.kit.entity.AmzID;
import amz.kit.entity.AmzTimestamp;
import amz.kit.entity.AmzValue;
import amz.kit.entity.response.http.BlockResponse;
import amz.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Block {
    private final BigInteger nonce;
    private final AmzAddress generator;
    private final AmzID id;
    private final AmzID nextBlock;
    private final AmzID previousBlock;
    private final AmzID[] transactions;
    private final AmzTimestamp timestamp;
    private final AmzValue blockReward;
    private final AmzValue totalAmount;
    private final AmzValue totalFee;
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

    public Block(BigInteger nonce, AmzAddress generator, AmzID id, AmzID nextBlock, AmzID previousBlock, AmzID[] transactions, AmzTimestamp timestamp, AmzValue blockReward, AmzValue totalAmount, AmzValue totalFee, byte[] generationSignature, byte[] generatorPublicKey, byte[] payloadHash, byte[] previousBlockHash, byte[] signature, int height, int payloadLength, int scoopNum, int version, long baseTarget) {
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
        this.generator = AmzAddress.fromEither(blockResponse.getGenerator());
        this.id = AmzID.fromLong(blockResponse.getBlock());
        this.nextBlock = AmzID.fromLong(blockResponse.getNextBlock());
        this.previousBlock = AmzID.fromLong(blockResponse.getPreviousBlock());
        this.transactions = Arrays.stream(blockResponse.getTransactions())
                .map(AmzID::fromLong)
                .toArray(AmzID[]::new);
        this.timestamp = new AmzTimestamp(blockResponse.getTimestamp());
        this.blockReward = AmzValue.fromAmz(blockResponse.getBlockReward());
        this.totalAmount = AmzValue.fromPlanck(blockResponse.getTotalAmountNQT());
        this.totalFee = AmzValue.fromPlanck(blockResponse.getTotalFeeNQT());
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
        AmzCrypto amzCrypto = AmzCrypto.getInstance();
        this.nonce = new BigInteger(Long.toUnsignedString(block.getNonce()));
        this.generator = amzCrypto.getAmzAddressFromPublic(block.getGeneratorPublicKey().toByteArray());
        this.id = AmzID.fromLong(block.getId());
        this.nextBlock = AmzID.fromLong(block.getNextBlockId());
        this.previousBlock = block.getPreviousBlockHash().size() == 0 ? AmzID.fromLong(0) : amzCrypto.hashToId(block.getPreviousBlockHash().toByteArray());
        this.transactions = block.getTransactionIdsList()
                .stream()
                .map(AmzID::fromLong)
                .toArray(AmzID[]::new);
        this.timestamp = new AmzTimestamp(block.getTimestamp());
        this.blockReward = AmzValue.fromPlanck(block.getBlockReward());
        this.totalAmount = AmzValue.fromPlanck(block.getTotalAmount());
        this.totalFee = AmzValue.fromPlanck(block.getTotalFee());
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

    public AmzAddress getGenerator() {
        return generator;
    }

    public AmzID getId() {
        return id;
    }

    public AmzID getNextBlock() {
        return nextBlock;
    }

    public AmzID getPreviousBlock() {
        return previousBlock;
    }

    public AmzID[] getTransactions() {
        return transactions;
    }

    public AmzTimestamp getTimestamp() {
        return timestamp;
    }

    public AmzValue getBlockReward() {
        return blockReward;
    }

    public AmzValue getTotalAmount() {
        return totalAmount;
    }

    public AmzValue getTotalFee() {
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
