package tarlog.encoder.tool.encoders;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tarlog.encoder.tool.api.AbstractEncoder;


public class MD5Encoder extends AbstractEncoder {
    
    @Override
    public Object encode(byte[] source) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
