import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {

    // assume this is user a
    private BigInteger q; // big prime
    private BigInteger a; // primitive root
    private BigInteger xa; // private component of user a
    private BigInteger ya; // public component of user a
    private BigInteger yb; // public component of user b
    
    public DiffieHellman(){
        q = getPrimeNumber(1024);
        a = getPrimitiveRoot();
        xa = getPrivateComponent();
        ya = getPublicComponent();
    }

    // q
    private BigInteger getPrimeNumber(int numBits) {
        return BigInteger.probablePrime(numBits, new SecureRandom());
    }

    // a
    private BigInteger getPrimitiveRoot(){
        for(BigInteger i = BigInteger.ZERO; i.compareTo(q) == -1; i.add(BigInteger.ONE)){
            if(q.gcd(i).equals(BigInteger.ONE)){
                return i;
            }
        }
        return null;
    }

    // XA
    private BigInteger getPrivateComponent() {
        BigDecimal randomDecimal = new BigDecimal(new SecureRandom().nextDouble());
        return new BigDecimal(q).multiply(randomDecimal).toBigInteger();
    }

    // YA
    private BigInteger getPublicComponent() {
        return a.modPow(xa, q);
    }

    // K
    public BigInteger getKey() {
        return yb.modPow(xa, q);
    }
}