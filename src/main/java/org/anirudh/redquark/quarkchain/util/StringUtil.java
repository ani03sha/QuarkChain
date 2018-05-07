package org.anirudh.redquark.quarkchain.util;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

import org.anirudh.redquark.quarkchain.App;
import org.anirudh.redquark.quarkchain.block.Block;
import org.anirudh.redquark.quarkchain.transaction.Transaction;

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

	/**
	 * This method encodes the key and returns
	 * 
	 * @param key
	 * @return encoded key
	 */
	public static String getStringFromKey(Key key) {

		/**
		 * Encoded using the basic Base64 algorithm
		 */
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * Applies ECDSA Signature and returns the result ( as bytes ).
	 * 
	 * @param key
	 * @param input
	 * @return byte[]
	 */
	public static byte[] applyECDSASignature(PrivateKey key, String input) {

		Signature dsa;

		byte[] output = new byte[0];

		try {

			dsa = Signature.getInstance("ECDSA", "BC");

			dsa.initSign(key);

			byte[] strByte = input.getBytes();

			dsa.update(strByte);

			byte[] realSignature = dsa.sign();

			output = realSignature;

		} catch (Exception e) {

			throw new RuntimeException();
		}

		return output;
	}

	/**
	 * Verifies a String signature
	 * 
	 * @param key
	 * @param data
	 * @param signature
	 * @return boolean
	 */
	public static boolean verifyECDSASignatrue(PublicKey key, String data, byte[] signature) {

		try {

			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");

			ecdsaVerify.initVerify(key);

			ecdsaVerify.verify(data.getBytes());

			return ecdsaVerify.verify(signature);

		} catch (Exception e) {

			throw new RuntimeException();
		}
	}

	/**
	 * Tacks in array of transactions and returns a merkle root.
	 * 
	 * @param transactions
	 * @return String
	 */
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {

		int count = transactions.size();

		ArrayList<String> previousTreeLayer = new ArrayList<>();

		for (Transaction transaction : transactions) {

			previousTreeLayer.add(transaction.getTransactionId());
		}

		ArrayList<String> treeLayer = previousTreeLayer;

		while (count > 1) {

			treeLayer = new ArrayList<>();

			for (int i = 1; i < previousTreeLayer.size(); i++) {

				treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}

			count = treeLayer.size();

			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size()) == 1 ? treeLayer.get(0) : "";

		return merkleRoot;
	}

	/**
	 * Returns difficulty string target, to compare to hash. eg difficulty of 5 will
	 * return "00000"
	 * 
	 * @param difficulty
	 * @return String
	 */
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
}
