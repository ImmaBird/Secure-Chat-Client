import java.io.Serializable;
import java.math.BigInteger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import javax.crypto.Cipher;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	// Initialization Vector
	private final byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
			0x0d, 0x0e, 0x0f };

	private byte[] salt = "AGE jmemoaAGrgaJHFGfRTY{25'234'234;5l;'54'ppw [pE,MK".getBytes();
	private Cipher cipher_encr = null;
	private Cipher cipher_decr = null;

	/**
	 * AES constructor. Using 256 bits
	 * 
	 * @param passphrase the password to use to encrypt or decrypt.
	 */
	public AES(BigInteger key) {
		char[] charArrayKey = new String(key.toByteArray()).toCharArray();

		try {
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec pbeKeySpec = new PBEKeySpec(charArrayKey, salt, 65536, 256); // 256 bits
			SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			cipher_encr = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher_encr.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));

			cipher_decr = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher_decr.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		} catch (Exception ex) {
			System.err.println("AES failed to initialize.");
			ex.printStackTrace();
		}
	}

	/**
	 * AES encrypton of Serializable objects.
	 * 
	 * @param data Serializable Object to Encrypt.
	 * @return an encrypted SealedObject Object.
	 * @throws Exception
	 */
	public SealedObject encrypt(Serializable data) throws Exception {
		return new SealedObject(data, cipher_encr);
	}

	/**
	 * AES decrypton of SealedObject objects.
	 * 
	 * @param sealedObj is the encrypted Object
	 * @return a Serializable Object
	 * @throws Exception
	 */
	public Serializable decrypt(SealedObject sealedObj) throws Exception {
		return (Serializable) sealedObj.getObject(cipher_decr);
	}
}
