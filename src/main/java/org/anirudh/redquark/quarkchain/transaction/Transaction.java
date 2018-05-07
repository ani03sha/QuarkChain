package org.anirudh.redquark.quarkchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import org.anirudh.redquark.quarkchain.App;
import org.anirudh.redquark.quarkchain.util.StringUtil;

/**
 * Each transaction will carry a certain amount of data:
 * 
 * - The public key(address) of the sender of funds. 
 * - The public key(address) of the receiver of funds. 
 * - The value/amount of funds to be transferred. 
 * - Inputs, which are references to previous transactions that prove the sender has funds to send. 
 * - Outputs, which shows the amount relevant addresses received in the transaction. (These outputs are referenced as inputs in new transactions) 
 * - A cryptographic signature, that proves the owner of the address is the one sending this transaction and that the data hasn’t been changed. (for
 * example: preventing a third party from changing the amount sent)
 */
public class Transaction {

	/**
	 * This is also the hash of the transaction
	 */
	private String transactionId;
	
	/**
	 * Sender's address/public key
	 */
	private PublicKey sender;
	
	/**
	 * Receiver's address/sender
	 */
	private PublicKey receiver;
	
	/**
	 * Value to be transferred
	 */
	private float value;
	
	/**
	 * This is to prevent anybody else from spending funds in our wallet.
	 */
	private byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<>();
	
	public ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	/**
	 * A rough count of how many transactions have been generated
	 */
	private static int sequence = 0;
	
	/**
	 * Parameterized constructor
	 */
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		
		this.sender = from;
		this.receiver = to;
		this.value = value;
		this.inputs = inputs;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @return the sender
	 */
	public PublicKey getSender() {
		return sender;
	}

	/**
	 * @return the receiver
	 */
	public PublicKey getReceiver() {
		return receiver;
	}

	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @return the signature
	 */
	public byte[] getSignature() {
		return signature;
	}

	/**
	 * @return the inputs
	 */
	public ArrayList<TransactionInput> getInputs() {
		return inputs;
	}

	/**
	 * @return the outputs
	 */
	public ArrayList<TransactionOutput> getOutputs() {
		return outputs;
	}
	
	/**
	 * Calculate the transaction hash which will be used as its id
	 */
	private String calculateHash() {
		
		/**
		 * Increase the sequence to avoid 2 identical transactions having the same hash
		 */
		sequence++;
		
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver)
				+ Float.toString(value) + sequence);
	}
	
	/**
	 * Signs all the data we don't wish to be tampered with.
	 * 
	 * @param privateKey
	 */
	public void generateSignature(PrivateKey privateKey) {

		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver)
				+ Float.toString(value);

		signature = StringUtil.applyECDSASignature(privateKey, data);
	}
	
	/**
	 * Verifies the data we signed hasn't been tampered with
	 */
	public boolean verifySignature() {

		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver)
				+ Float.toString(value);

		return StringUtil.verifyECDSASignatrue(sender, data, signature);
	}
	
	public boolean processTransaction() {
		
		if(!verifySignature()) {
			
			System.out.println("Transaction Signature failed to verify");
			
			return false;
		}
		
		/**
		 * Gather transaction inputs (Make sure they are unspent)
		 */
		for(TransactionInput i : inputs) {
			
			i.UTXO = App.UTXOs.get(i.transactionOutputId);
		}
		
		/**
		 * Check if the transaction is valid
		 */
		if(getInputsValue() < App.minimumTransaction) {
			
			System.out.println("Transaction Inputs too small: " + getInputsValue());
			
			return false;
		}
		
		/**
		 * Generate transaction outputs
		 */
		
		/**
		 * Get value of inputs then the left over change
		 */
		float leftOver = getInputsValue() - value;
		
		/**
		 * Calculate the transaction id
		 */
		transactionId = calculateHash();
		
		/**
		 * Sends value to the receiver
		 */
		outputs.add(new TransactionOutput(this.receiver, value, transactionId));
		
		/**
		 * Send the left over change back to the sender
		 */
		outputs.add(new TransactionOutput(sender, leftOver, transactionId));
		
		/**
		 * Add outputs to the unspent list
		 */
		for(TransactionOutput o : outputs) {
			
			App.UTXOs.put(o.id, o);
		}
		
		/**
		 * Remove transaction inputs from UTXO list as spent
		 */
		for(TransactionInput i : inputs) {
			
			/**
			 * If transaction cannot be found, skip it
			 */
			if(i.UTXO == null) {
				
				continue;
			}
			
			App.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	/**
	 * Returns sum of inputs(UTXOs) values
	 * 
	 * @return float
	 */
	public float getInputsValue() {
		
		float total = 0;
		
		for(TransactionInput i : inputs) {
			
			/**
			 * If transaction cannot be found, skip it
			 */
			if(i.UTXO == null) {
				continue;
			}
			
			total =total + i.UTXO.value;
		}
		
		return total;
	}
	
	/**
	 * Returns sum of outputs
	 * 
	 * @return float
	 */
	public float getOutputsValue() {
		
		float total = 0;
		
		for(TransactionOutput o : outputs) {
			total = total + o.value;
		}
		
		return total;
	}
}
