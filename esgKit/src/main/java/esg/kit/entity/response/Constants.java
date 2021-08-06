package esg.kit.entity.response;

import esg.kit.entity.EsgAddress;
import esg.kit.entity.EsgID;
import esg.kit.entity.response.http.ConstantsResponse;
import esg.kit.entity.response.http.TransactionSubtypeResponse;
import esg.kit.entity.response.http.TransactionTypeResponse;
import esg.kit.service.impl.grpc.BrsApi;

import java.util.Arrays;

public class Constants {
    private final int maxBlockPayloadLength;
    private final int maxArbitraryMessageLength;
    private final EsgID genesisBlockId;
    private final EsgAddress genesisAccountId;
    private final TransactionType[] transactionTypes;

    public Constants(int maxBlockPayloadLength, int maxArbitraryMessageLength, EsgAddress genesisAccountId, EsgID genesisBlockId, TransactionType[] transactionTypes) {
        this.maxBlockPayloadLength = maxBlockPayloadLength;
        this.maxArbitraryMessageLength = maxArbitraryMessageLength;
        this.genesisAccountId = genesisAccountId;
        this.genesisBlockId = genesisBlockId;
        this.transactionTypes = transactionTypes;
    }

    public Constants(ConstantsResponse constantsResponse) {
        this.maxBlockPayloadLength = constantsResponse.getMaxBlockPayloadLength();
        this.maxArbitraryMessageLength = constantsResponse.getMaxArbitraryMessageLength();
        this.genesisBlockId = EsgID.fromLong(constantsResponse.getGenesisBlockId());
        this.genesisAccountId = EsgAddress.fromEither(constantsResponse.getGenesisAccountId());
        this.transactionTypes = Arrays.stream(constantsResponse.getTransactionTypes())
                .map(TransactionType::new)
                .toArray(TransactionType[]::new);
    }

    public Constants(BrsApi.Constants constants) {
        this.maxBlockPayloadLength = constants.getMaxBlockPayloadLength();
        this.maxArbitraryMessageLength = constants.getMaxArbitraryMessageLength();
        this.genesisBlockId = EsgID.fromLong(constants.getGenesisBlock());
        this.genesisAccountId = EsgAddress.fromId(constants.getGenesisAccount());
        this.transactionTypes = constants.getTransactionTypesList()
                .stream()
                .map(TransactionType::new)
                .toArray(TransactionType[]::new);
    }

    public int getMaxBlockPayloadLength() {
        return maxBlockPayloadLength;
    }

    public int getMaxArbitraryMessageLength() {
        return maxArbitraryMessageLength;
    }

    public EsgAddress getGenesisAccountId() {
        return genesisAccountId;
    }

    public EsgID getGenesisBlockId() {
        return genesisBlockId;
    }

    public TransactionType[] getTransactionTypes() {
        return transactionTypes;
    }

    public static class TransactionType {
        private final String description;
        private final int type;
        private final Subtype[] subtypes;

        public TransactionType(String description, int type, Subtype[] subtypes) {
            this.description = description;
            this.type = type;
            this.subtypes = subtypes;
        }

        public TransactionType(TransactionTypeResponse transactionTypeResponse) {
            this.description = transactionTypeResponse.getDescription();
            this.type = transactionTypeResponse.getValue();
            this.subtypes = Arrays.stream(transactionTypeResponse.getSubtypes())
                    .map(Subtype::new)
                    .toArray(Subtype[]::new);
        }

        public TransactionType(BrsApi.Constants.TransactionType transactionType) {
            this.description = transactionType.getDescription();
            this.type = transactionType.getType();
            this.subtypes = transactionType.getSubtypesList()
                    .stream()
                    .map(Subtype::new)
                    .toArray(Subtype[]::new);
        }

        public String getDescription() {
            return description;
        }

        public int getType() {
            return type;
        }

        public Subtype[] getSubtypes() {
            return subtypes;
        }

        public static class Subtype {
            private final String description;
            private final int subtype;

            public Subtype(String description, int subtype) {
                this.description = description;
                this.subtype = subtype;
            }

            public Subtype(TransactionSubtypeResponse transactionSubtypeResponse) {
                this.description = transactionSubtypeResponse.getDescription();
                this.subtype = transactionSubtypeResponse.getValue();
            }

            public Subtype(BrsApi.Constants.TransactionType.TransactionSubtype transactionSubtype) {
                this.description = transactionSubtype.getDescription();
                this.subtype = transactionSubtype.getSubtype();
            }

            public String getDescription() {
                return description;
            }

            public int getSubtype() {
                return subtype;
            }
        }
    }
}
