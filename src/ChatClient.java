import java.io.*;
import java.net.*;
import java.math.BigInteger;
import javax.crypto.*;
import javax.crypto.spec.*;

class ChatClient {

	public static void main(String args[]) {
		try {
			Socket s = new Socket("localhost", 1234);
			System.out.println("Local Port: " + s.getLocalPort());
			System.out.println("Server Port: " + s.getPort());

			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			/*
			 * (1) RECEIVE a BigInteger
			 */
			BigInteger bi = (BigInteger) ois.readObject();
			System.out.println("Server sent to me: " + bi);

			/*
			 * (2) SEND a BigInteger
			 */
			oos.writeObject(bi.add(BigInteger.ONE));

			/*
			 * (3) RECEIVE a custom object, which is Serializable
			 */
			INFO info = (INFO) ois.readObject();
			System.out.println("INFO object sent by server: ");
			info.printInfo();
			for (int i = 0; i < info.getArray().length; i++) {
				System.out.println("\t[" + i + "] " + info.getArray()[i]);
			}

			/*
			 * (4) RECEIVE a String
			 */
			System.out.println("Server says: " + (String) ois.readObject());

			System.out.println("--------- ENCRYPTED SEND/RECEIVE -----------");

			/*
			 * Create Key
			 */
			String password = "12345678";
			byte key[] = password.getBytes();
			DESKeySpec desKeySpec = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

			/*
			 * Create Cipher
			 */
			Cipher desCipher_encr = Cipher.getInstance("DES/ECB/PKCS5Padding");
			Cipher desCipher_decr = Cipher.getInstance("DES/ECB/PKCS5Padding");

			/*
			 * Initialize Cipher
			 */
			desCipher_encr.init(Cipher.ENCRYPT_MODE, secretKey);
			desCipher_decr.init(Cipher.DECRYPT_MODE, secretKey);

			/*
			 * RECEIVE a String (encrypted)
			 */
			SealedObject sobj = (SealedObject) ois.readObject();
			String ss = (String) sobj.getObject(desCipher_decr);
			System.out.println("Server sent String: " + ss);

			/*
			 * SEND a custom object (encrypted)
			 */
			INFO info_en = new INFO("CLIENT", new BigInteger("999999999"));
			SealedObject info_sealed_obj = new SealedObject(info_en, desCipher_encr);
			oos.writeObject(info_sealed_obj);

			/*
			 * RECEIVE IMAGE
			 */
			SealedObject si = (SealedObject) ois.readObject();
			System.out.println("(1)");
			IMAGE_OBJ iobj = (IMAGE_OBJ) si.getObject(desCipher_decr);
			System.out.println("(2) ");
			iobj.SaveImage("./IMAGE_RECEIVED.png");
			System.out.println("(3)");

			// close streams
			oos.close();
			ois.close();

			/*
			 * Close connection
			 */
			s.close();
		} catch (Exception e) {
			System.err.print("[ERROR] ::" + e);
		}
	}
}
