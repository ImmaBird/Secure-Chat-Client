import java.lang.*;
import java.io.*;
import java.net.*;

import java.math.BigInteger;

import javax.crypto.*;
import javax.crypto.spec.*;

public class Server extends Thread {
	private ServerSocket servSock = null;

	private void printInfo(Socket s) {
		InetAddress ia;
		System.out.println("\tLocal Port : " + s.getLocalPort());
		System.out.println("\tRemote Port: " + s.getPort());

		ia = s.getInetAddress(); // REMOTE
		System.out.println("\t==> Remote IP: " + ia.getHostAddress());
		System.out.println("\t==> Remote Name: " + ia.getHostName());
		System.out.println("\t==> Remote DNS Name: " + ia.getCanonicalHostName());

		ia = s.getLocalAddress(); // LOCAL
		System.out.println("\t==> Local IP: " + ia.getHostAddress());
		System.out.println("\t==> Local Name: " + ia.getHostName());
		System.out.println("\t==> Local DNS Name: " + ia.getCanonicalHostName());
	}

	public Server(int port) {
		try {
			servSock = new ServerSocket(port, 5);
			System.out.println("Listening on port " + port);
		} catch (Exception e) {
			System.err.println("[ERROR] + " + e);
		}
		this.start();
	}

	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for connections......");
				Socket s = servSock.accept();
				System.out.println("Server accepted connection from: " + s.getInetAddress().getHostAddress());
				// printInfo(s);

				new ClientHandler(s).start();
			} catch (Exception e) {
				System.err.println("[ERROR] + " + e);
			}
		}
		// servSock.close(); // At some point we need to close this (when we shutdown
		// the server), for now let's put it here
	}

	public static void main(String args[]) {
		new Server(1234);
	}

}

/**
 *
 */
class ClientHandler extends Thread {
	private Socket s = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;

	public ClientHandler(Socket s) {
		this.s = s;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (Exception e) {
			System.err.println("Exception: " + e);
		}
	}

	public void run() {
		try {
			/*
			 * (1) SEND a BigInteger, which is Serializable
			 */
			oos.writeObject(new BigInteger("123456788"));

			/*
			 * (2) RECEIVE a BigInteger, which is Serializable
			 */
			BigInteger bi = (BigInteger) ois.readObject();
			System.out.println("Client sent to me: " + bi);

			/*
			 * (3) SEND a custom object (** must be Serializable **)
			 */
			INFO info = new INFO("LEON", new BigInteger("112233445566778899"));
			for (int i = 0; i < info.getArray().length; i++) {
				info.getArray()[i] = i;
			}
			oos.writeObject(info);

			/*
			 * (4) SEND a String, which is Serializable
			 */
			oos.writeObject(new String("This is a String"));

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
			 * SEND a String (encrypted)
			 */
			String str = new String("Encrypted String :)");
			SealedObject sealedObj = new SealedObject(str, desCipher_encr);
			oos.writeObject(sealedObj);

			/*
			 * RECEIVE a custom Object (encrypted)
			 */
			SealedObject sobj = (SealedObject) ois.readObject();
			INFO info_en = (INFO) sobj.getObject(desCipher_decr);
			System.out.println("INFO object (encrypted) sent by client: ");
			info_en.printInfo();
			for (int i = 0; i < info_en.getArray().length; i++) {
				System.out.println("\t[" + i + "] " + info_en.getArray()[i]);
			}

			/*
			 * SEND IMAGE
			 */
			IMAGE_OBJ im = new IMAGE_OBJ("./IMAGE_TO_SEND.png");
			SealedObject si = new SealedObject(im, desCipher_encr);
			oos.writeObject(si);

			/*
			 * Close stream
			 */
			oos.close();
			ois.close();

			/*
			 * Close connection
			 */
			s.close();
		} catch (Exception e) {
			System.err.println("Exception: " + e);
		}
	}

}
