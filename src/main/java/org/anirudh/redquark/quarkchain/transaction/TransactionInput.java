package org.anirudh.redquark.quarkchain.transaction;

/**
 * For you to own 1 bitcoin, you have to receive 1 Bitcoin. The ledger doesn’t
 * really add one bitcoin to you and minus one bitcoin from the sender, the
 * sender referenced that he/she previously received one bitcoin, then a
 * transaction output was created showing that 1 Bitcoin was sent to your
 * address. (Transaction inputs are references to previous transaction outputs).
 * 
 * This class will be used to reference TransactionOutputs that have not yet
 * been spent. The transactionOutputId will be used to find the relevant
 * TransactionOutput, allowing miners to check your ownership.
 */
public class TransactionInput {

	/**
	 * Reference to TransactionOutputs - transactionId
	 */
	public String transactionOutputId;

	/**
	 * Contains the Unspent Transaction Output
	 */
	public TransactionOutput UTXO;

	/**
	 * @param transactionOutputId
	 */
	public TransactionInput(String transactionOutputId) {
		
		this.transactionOutputId = transactionOutputId;
	}

}
