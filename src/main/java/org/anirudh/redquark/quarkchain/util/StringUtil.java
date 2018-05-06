package org.anirudh.redquark.quarkchain.util;

import java.security.MessageDigest;
import java.util.ArrayList;

import org.anirudh.redquark.quarkchain.App;
import org.anirudh.redquark.quarkchain.model.Block;

/**
 * Helper class
 */
public class StringUtil {

	/**
	 * Apply SHA256 to a string and returns the result
	 * 
	 * @param input
	 * @return {@value}
	 */
	public static String applySha256(String input) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			/**
			 * Applying SHA-256 on our input
			 */
			byte[] hash = messageDigest.digest(input.getBytes("UTF-8"));

			/**
			 * This will contain hash as a hexadecimal
			 */
			StringBuffer hexHash = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {

				String hex = Integer.toHexString(0xff & hash[i]);

				if (hex.length() == 1) {
					hexHash.append('0');
				}
				hexHash.append(hex);
			}

			return hexHash.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method loops through all blocks in the chain and compares the hashes.
	 * This method will need to check the hash variable is actually equal to the
	 * calculated hash, and the previous block’s hash is equal to the previousHash
	 * variable.
	 * 
	 * Any change to the blockchain will cause this method to return false
	 */
	public static Boolean isBlockchainValid(ArrayList<Block> blockchain) {

		Block currentBlock;
		Block previousBlock;

		/**
		 * Loop through blockchain to compare hashes
		 */
		for (int i = 1; i < blockchain.size(); i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			String hashTarget = new String(new char[App.difficulty]).replace('\0', '0');

			/**
			 * Compare registered hash and the calculated hash
			 */
			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {

				System.out.println("Current Hashes are not equal");

				return false;
			}

			/**
			 * Compare previous hash and the registered previous hash
			 */
			if (!previousBlock.getHash().equals(previousBlock.calculateHash())) {

				System.out.println("Previous hashes are not equal");

				return false;
			}
			/**
			 * Check if hash is solved
			 */
			if (!currentBlock.getHash().substring(0, App.difficulty).equals(hashTarget)) {
				
				System.out.println("This block hasn't been mined");
				
				return false;
			}
		}

		return true;
	}
}
