package amz.kit.entity.response;

import amz.kit.entity.AmzAddress;
import amz.kit.entity.AmzID;
import amz.kit.entity.AmzValue;
import amz.kit.entity.response.http.AccountResponse;
import amz.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

public class Account {
    private final AmzAddress id;
    private final AmzValue balance;
    private final AmzValue forgedBalance;
    private final AmzValue unconfirmedBalance;
    private final byte[] publicKey;
    private final String description;
    private final String name;

    public Account(AmzAddress id, AmzValue balance, AmzValue forgedBalance, AmzValue unconfirmedBalance, byte[] publicKey, String description, String name) {
        this.id = id;
        this.balance = balance;
        this.forgedBalance = forgedBalance;
        this.unconfirmedBalance = unconfirmedBalance;
        this.publicKey = publicKey;
        this.description = description;
        this.name = name;
    }

    public Account(AccountResponse accountResponse) {
        this.id = AmzAddress.fromEither(accountResponse.getAccount());
        this.balance = AmzValue.fromPlanck(accountResponse.getBalanceNQT());
        this.forgedBalance = AmzValue.fromPlanck(accountResponse.getForgedBalanceNQT());
        this.unconfirmedBalance = AmzValue.fromPlanck(accountResponse.getUnconfirmedBalanceNQT());
        this.publicKey = accountResponse.getPublicKey() == null ? new byte[32] : Hex.decode(accountResponse.getPublicKey());
        this.description = accountResponse.getDescription();
        this.name = accountResponse.getName();
    }

    public Account(BrsApi.Account account) {
        this.id = AmzAddress.fromId(account.getId());
        this.balance = AmzValue.fromPlanck(account.getBalance());
        this.forgedBalance = AmzValue.fromAmz(account.getForgedBalance());
        this.unconfirmedBalance = AmzValue.fromAmz(account.getUnconfirmedBalance());
        this.publicKey = account.getPublicKey().toByteArray();
        this.description = account.getDescription();
        this.name = account.getName();
    }

    public AmzAddress getId() {
        return id;
    }

    public AmzValue getBalance() {
        return balance;
    }

    public AmzValue getForgedBalance() {
        return forgedBalance;
    }

    public AmzValue getUnconfirmedBalance() {
        return unconfirmedBalance;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
