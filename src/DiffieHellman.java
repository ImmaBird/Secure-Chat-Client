import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {

    private DiffieHellman() {
    }

    public static BigInteger serverTransaction(ObjectInputStream in, ObjectOutputStream out) {
        // compute parameters
        BigInteger q = getPrimeNumber(1024); // big prime
        BigInteger a = getPrimitiveRoot(q); // primitive root
        BigInteger xa = getPrivateComponent(q); // private component of user a
        BigInteger ya = getPublicComponent(q, a, xa); // public component of user a

        try {
            // send parameters
            out.writeObject(q);
            out.writeObject(a);
            out.writeObject(ya);

            // get yb
            BigInteger yb = (BigInteger) in.readObject(); // public component of user b

            // compute key and return
            return getKey(q, xa, yb);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static BigInteger clientTransaction(ObjectInputStream in, ObjectOutputStream out) {
        try {
            // get parameters
            BigInteger q = (BigInteger) in.readObject();
            BigInteger a = (BigInteger) in.readObject();
            BigInteger yb = (BigInteger) in.readObject();

            // compute parameters
            BigInteger xa = getPrivateComponent(q); // private component of user a
            BigInteger ya = getPublicComponent(q, a, xa); // public component of user a

            // send ya
            out.writeObject(ya); // public component of user b

            // compute key and return
            return getKey(q, xa, yb);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // q
    private static BigInteger getPrimeNumber(int numBits) {
        return BigInteger.probablePrime(numBits, new SecureRandom());
    }

    // a
    private static BigInteger getPrimitiveRoot(BigInteger q) {
        return BigInteger.valueOf(3);
    }

    // XA
    private static BigInteger getPrivateComponent(BigInteger q) {
        BigDecimal randomDecimal = new BigDecimal(new SecureRandom().nextDouble());
        return new BigDecimal(q).multiply(randomDecimal).toBigInteger();
    }

    // YA
    private static BigInteger getPublicComponent(BigInteger q, BigInteger a, BigInteger xa) {
        return a.modPow(xa, q);
    }

    // K
    private static BigInteger getKey(BigInteger q, BigInteger xa, BigInteger yb) {
        return yb.modPow(xa, q);
    }

}