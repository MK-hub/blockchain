package utils;

public class TransactionInput {

    public String transactionOutputId;
    public TransactionOutputs UTXO;

    public TransactionInput (String transactionOutputId){
        this.transactionOutputId = transactionOutputId;
    }
}
