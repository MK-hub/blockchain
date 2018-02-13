package utils;
import block.Block;
import main.BlockChain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutputs> outputs = new ArrayList<TransactionOutputs>();


    private static int sequence =0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput>inputs){
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash(){
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender)+
                        StringUtil.getStringFromKey(reciepient)+
                        Float.toString(value)+ sequence);

    }

    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender)+ StringUtil.getStringFromKey(reciepient)+ Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey,data);
    }
    public boolean verifiySignature(){
        String data = StringUtil.getStringFromKey(sender)+ StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender,data,signature);
    }

    public boolean processTransaction(){
        if(verifiySignature()==false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        for (TransactionInput i: inputs){
            i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
        }

        if (getInputsValue()< BlockChain.minimumTransaction){
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutputs(this.reciepient, value,transactionId));
        outputs.add(new TransactionOutputs( this.sender, leftOver,transactionId));

        for (TransactionOutputs o : outputs){
            BlockChain.UTXOs.put(o.id,o);
        }


        for (TransactionInput i : inputs){
            if (i.UTXO ==null)continue;
            BlockChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    public float getInputsValue(){
        float total =0;
        for (TransactionInput i :inputs){
            if(i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutputs o : outputs) {
            total += o.value;
        }
        return total;
    }
}
