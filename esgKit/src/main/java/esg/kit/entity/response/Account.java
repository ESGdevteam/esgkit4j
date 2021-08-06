package esg.kit.entity.response;

import esg.kit.entity.EsgAddress;
import esg.kit.entity.EsgID;
import esg.kit.entity.EsgValue;
import esg.kit.entity.response.http.AccountResponse;
import esg.kit.service.impl.grpc.BrsApi;
import org.bouncycastle.util.encoders.Hex;

public class Account {
    private final EsgAddress id;
    private final EsgValue balance;
    private final EsgValue forgedBalance;
    private final EsgValue unconfirmedBalance;
    private final byte[] publicKey;
    private final String description;
    private final String name;

    public Account(EsgAddress id, EsgValue balance, EsgValue forgedBalance, EsgValue unconfirmedBalance, byte[] publicKey, String description, String name) {
        this.id = id;
        this.balance = balance;
        this.forgedBalance = forgedBalance;
        this.unconfirmedBalance = unconfirmedBalance;
        this.publicKey = publicKey;
        this.description = description;
        this.name = name;
    }

    public Account(AccountResponse accountResponse) {
        this.id = EsgAddress.fromEither(accountResponse.getAccount());
        this.balance = EsgValue.fromPlanck(accountResponse.getBalanceNQT());
        this.forgedBalance = EsgValue.fromPlanck(accountResponse.getForgedBalanceNQT());
        this.unconfirmedBalance = EsgValue.fromPlanck(accountResponse.getUnconfirmedBalanceNQT());
        this.publicKey = accountResponse.getPublicKey() == null ? new byte[32] : Hex.decode(accountResponse.getPublicKey());
        this.description = accountResponse.getDescription();
        this.name = accountResponse.getName();
    }

    public Account(BrsApi.Account account) {
        this.id = EsgAddress.fromId(account.getId());
        this.balance = EsgValue.fromPlanck(account.getBalance());
        this.forgedBalance = EsgValue.fromEsg(account.getForgedBalance());
        this.unconfirmedBalance = EsgValue.fromEsg(account.getUnconfirmedBalance());
        this.publicKey = account.getPublicKey().toByteArray();
        this.description = account.getDescription();
        this.name = account.getName();
    }

    public EsgAddress getId() {
        return id;
    }

    public EsgValue getBalance() {
        return balance;
    }

    public EsgValue getForgedBalance() {
        return forgedBalance;
    }

    public EsgValue getUnconfirmedBalance() {
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
