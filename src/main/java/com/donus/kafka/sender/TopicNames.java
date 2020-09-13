package com.donus.kafka.sender;

public class TopicNames {

    private TopicNames() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public final static String DEPOSIT_TOPIC = "DEPOSIT_TRANSACTION_DONUS";
    public final static String WITHDRAW_TOPIC = "WITHDRAW_TRANSACTION_DONUS";
    public final static String TRANSFER_TOPIC = "TRANSFER_TRANSACTION_DONUS";
    public final static String NEW_ACCOUNT_TOPIC = "NEW_ACCOUNT_TOPIC";

}
