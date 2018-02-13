package utils;

import java.security.PublicKey;

public class TransactionOutputs {

    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionId;

    public TransactionOutputs(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }



}
