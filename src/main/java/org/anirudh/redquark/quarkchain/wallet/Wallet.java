package org.anirudh.redquark.quarkchain.wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

/**
 * This is our wallet class which holds all the data related to a wallet, such
 * as private and public keys.
 * 
 * In their basic form wallets can just store these addresses, most wallets
 * however, are also software able to make new transactions on the Blockchain.
 */
public class Wallet {

	/**
	 * Private key is used to sign our transactions, so that nobody can spend our
	 * QuarkCoins other than the owner of private key. Users will have to keep their
	 * private key Secret !
	 */
	public PrivateKey privateKey;

	/**
	 * The public key acts as our address and this can be shared with others for
	 * transactions. We also send our public key along with the transaction and it
	 * can be used to verify that our signature is valid and data has not been
	 * tampered with.
	 */
	public PublicKey publicKey;

	/**
	 * We generate our private and public keys in a KeyPair. We will use
	 * Elliptic-curve cryptography to Generate our KeyPairs.
	 */
	public Wallet() {

		generateKeyPair();
	}

	private void generateKeyPair() {

		try {

			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");

			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

			ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");

			/**
			 * Initialize a key generator and generate a KeyPair
			 */
			keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);

			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			/**
			 * Set the private key from KeyPair
			 */
			privateKey = keyPair.getPrivate();

			/**
			 * Set the public key from the KeyPair
			 */
			publicKey = keyPair.getPublic();
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

}
