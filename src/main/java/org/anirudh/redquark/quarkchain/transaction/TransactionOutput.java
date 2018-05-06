package org.anirudh.redquark.quarkchain.transaction;

import java.security.PublicKey;

import org.anirudh.redquark.quarkchain.util.StringUtil;

/**
 * Transaction outputs will show the final amount sent to each party from the
 * transaction. These, when referenced as inputs in new transactions, act as
 * proof that you have coins to send.
 */
public class TransactionOutput {

	/**
	 * Unique id
	 */
	public String id;

	/**
	 * Owner of the coins
	 */
	public PublicKey receiver;

	/**
	 * Amount of coins they own
	 */
	public float value;

	/**
	 * The id of the transaction this output was created in.
	 */
	public String parentTransactionId;

	/**
	 * @param receiver
	 * @param value
	 * @param parentTransactionId
	 */
	public TransactionOutput(PublicKey receiver, float value, String parentTransactionId) {

		this.receiver = receiver;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(receiver) + Float.toString(value));
	}

	/**
	 * Check if the coin belongs to you
	 */
	public boolean isMine(PublicKey publicKey) {
		
		return (publicKey == receiver);
	}

}
