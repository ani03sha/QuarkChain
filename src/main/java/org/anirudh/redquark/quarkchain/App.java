package org.anirudh.redquark.quarkchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import org.anirudh.redquark.quarkchain.model.Block;
import org.anirudh.redquark.quarkchain.model.Transaction;
import org.anirudh.redquark.quarkchain.model.Wallet;
import org.anirudh.redquark.quarkchain.transaction.TransactionOutput;
import org.anirudh.redquark.quarkchain.util.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Main class
 */
public class App {

	/**
	 * This ArrayList contains all the blocks in the blockchain, hence ultimately
	 * creates the blockchain itself
	 */
	public static ArrayList<Block> blockchain = new ArrayList<>();

	/**
	 * Difficulty
	 */
	public static int difficulty = 1;

	/**
	 * Instances of Wallet
	 */
	public static Wallet walletA, walletB;

	/**
	 * All unspent transactions
	 */
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

	public static void main(String[] args) {

		/*
		 * for (int i = 0; i < 5; i++) { Block block = new Block(i + "", "Block-" + i);
		 * blockchain.add(block); System.out.println("Trying to mine block: " + i);
		 * blockchain.get(i).mineBlock(difficulty); }
		 * 
		 * System.out.println("\nBlockchain valid: " +
		 * StringUtil.isBlockchainValid(blockchain));
		 * 
		 * String blockChainJson = new
		 * GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		 * System.out.println("\nThe blockchain: "); System.out.println(blockChainJson);
		 */

		/**
		 * Setup bouncy castle as a security provider
		 */
		Security.addProvider(new BouncyCastleProvider());

		/**
		 * Create new wallets
		 */
		walletA = new Wallet();
		walletB = new Wallet();

		/**
		 * Test public and private keys
		 */
		System.out.println("Public and Private keys: ");

		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		System.out.println(StringUtil.getStringFromKey(walletB.privateKey));

		/**
		 * Create a test transaction from walletA to walletB
		 */
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);

		/**
		 * Verify the signature works and verify it from the public key
		 */
		System.out.println("Is signature verified?");
		System.out.println(transaction.verifySignature());
	}
}
