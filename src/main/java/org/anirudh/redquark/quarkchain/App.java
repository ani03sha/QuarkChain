package org.anirudh.redquark.quarkchain;

import java.util.ArrayList;

import org.anirudh.redquark.quarkchain.model.Block;
import org.anirudh.redquark.quarkchain.util.StringUtil;

import com.google.gson.GsonBuilder;

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

	public static void main(String[] args) {

		for (int i = 0; i < 5; i++) {
			Block block = new Block(i + "", "Block-" + i);
			blockchain.add(block);
			System.out.println("Trying to mine block: " + i);
			blockchain.get(i).mineBlock(difficulty);
		}

		System.out.println("\nBlockchain valid: " + StringUtil.isBlockchainValid(blockchain));

		String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe blockchain: ");
		System.out.println(blockChainJson);
	}
}
