package org.anirudh.redquark.quarkchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import org.anirudh.redquark.quarkchain.block.Block;
import org.anirudh.redquark.quarkchain.transaction.Transaction;
import org.anirudh.redquark.quarkchain.transaction.TransactionInput;
import org.anirudh.redquark.quarkchain.transaction.TransactionOutput;
import org.anirudh.redquark.quarkchain.wallet.Wallet;
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
	 * Difficulty - Increase or decrease the value of this parameter to see the
	 * effect of computation power required to solve the transaction
	 */
	public static int difficulty = 5;

	/**
	 * Instances of Wallet
	 */
	public static Wallet walletA, walletB;

	/**
	 * All unspent transactions
	 */
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

	/**
	 * Minimum transaction value
	 */
	public static float minimumTransaction = 0.1f;

	/**
	 * Genesis Transaction
	 */
	public static Transaction genesisTransaction;

	public static void main(String[] args) {

		/**
		 * Setup bouncy castle as a security provider
		 */
		Security.addProvider(new BouncyCastleProvider());

		/**
		 * Create new wallets
		 */
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinBase = new Wallet();

		/**
		 * Create genesis transaction, which sends 100 QuarkCoin to walletA;
		 */
		genesisTransaction = new Transaction(coinBase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinBase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.getReceiver(),
				genesisTransaction.getValue(), genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		/**
		 * Testing by creating some blocks
		 */
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		/**
		 * Checking if the blockchain is valid
		 */
		isChainValid();

	}

	/**
	 * This method checks if the blockchain is valid in order to make sure someone
	 * has not tampered it
	 * 
	 * @return {@link Boolean}
	 */
	public static boolean isChainValid() {

		/**
		 * Current block
		 */
		Block currentBlock;

		/**
		 * Previous block
		 */
		Block previousBlock;

		/**
		 * Getting the target string based on difficulty parameter
		 */
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		/**
		 * A temporary working list of unspent transactions at a given block state.
		 */
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();

		/**
		 * Adding the unspent transaction to the HashMap
		 */
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		/**
		 * Loop through blockchain to check hashes:
		 */
		for (int i = 1; i < blockchain.size(); i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			/**
			 * Compare registered hash and calculated hash:
			 */
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal");
				return false;
			}

			/**
			 * Compare previous hash and registered previous hash
			 */
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}

			/**
			 * Check if hash is solved
			 */
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			/**
			 * Loop through blockchains transactions:
			 */
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).receiver != currentTransaction.getReceiver()) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).receiver != currentTransaction.getSender()) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;

	}

	/**
	 * This method adds a block to the blockchain
	 * 
	 * @param newBlock
	 */
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}
