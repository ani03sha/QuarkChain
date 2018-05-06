package org.anirudh.redquark.quarkchain.model;

import java.util.Date;

import org.anirudh.redquark.quarkchain.util.StringUtil;

/**
 * This is the basic block that makes a blockchain
 */
public class Block {

	/**
	 * Hash of the current block
	 */
	private String hash;

	/**
	 * Hash of the previous block
	 */
	private String previousHash;

	/**
	 * Data that is stored in a block, generally transactions
	 */
	private String data;

	/**
	 * Time stamp to show when the block is generated
	 */
	private long timeStamp;

	private int nonce;

	/**
	 * Parameterized constructor
	 * 
	 * @param hash
	 * @param previousHash
	 * @param data
	 * @param timeStamp
	 */
	public Block(String previousHash, String data) {
		this.previousHash = previousHash;
		this.data = data;
		this.timeStamp = new Date().getTime();

		/**
		 * Make sure we calculate this after we set other values as it depends on it
		 */
		this.hash = calculateHash();
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @return the previousHash
	 */
	public String getPreviousHash() {
		return previousHash;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * We must calculate the hash from all parts of the block we don’t want to be
	 * tampered with. So for our block we will include the previousHash, the data
	 * and timeStamp.
	 * 
	 * @return calculatedHash
	 */
	public String calculateHash() {

		/**
		 * Pass all the three known parameters to a block so that they can contribute to
		 * its individual hash calculation
		 */
		String calculatedHash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + data);

		return calculatedHash;
	}

	/**
	 * We will require miners to do proof-of-work by trying different variable
	 * values in the block until its hash starts with a certain number of 0’s.
	 * 
	 * This method takes in an integer called difficulty, this is the number of 0’s
	 * miners must solve for. Low difficulty like 1 or 2 can be solved nearly
	 * instantly on most computers, I’d suggest something around 4–6 for testing. At
	 * the time of writing Litecoin’s difficulty is around 442,592.
	 * 
	 * @param difficulty
	 */
	public void mineBlock(int difficulty) {

		/**
		 * Create a string with difficulty * "0"
		 */
		String target = new String(new char[difficulty]).replace('\0', '0');

		while (!getHash().substring(0, difficulty).equals(target)) {

			nonce++;

			hash = calculateHash();
		}

		System.out.println("Block Mined!! : " + hash);
	}
}
