import java.io.Serializable;
import java.math.BigInteger;
import javax.crypto.SealedObject;

public class Crypto {

    public static enum Algorithm {
        AES, DES
    };

    public static Algorithm algorithm;
    private AES aes;
    private DES des;

    public Crypto(BigInteger key) {
        Crypto.algorithm = Algorithm.AES;
        this.aes = new AES(key);
        this.des = new DES(key);
    }

    public SealedObject encrypt(Serializable data) throws Exception {
        switch (algorithm) {
        case AES:
            return aes.encrypt(data);
        case DES:
            return des.encrypt(data);
        }
        throw new Exception();
    }

    public Serializable decrypt(SealedObject sealedObj) throws Exception {
        switch (algorithm) {
        case AES:
            return aes.decrypt(sealedObj);
        case DES:
            return des.decrypt(sealedObj);
        }
        throw new Exception();
    }
}