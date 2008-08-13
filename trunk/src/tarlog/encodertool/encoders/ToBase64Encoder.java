// (C) Copyright 2003-2008 Hewlett-Packard Development Company, L.P.
package tarlog.encodertool.encoders;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import tarlog.encodertool.AbstractEncoder;
import tarlog.encodertool.Utils;


/**
 *
 */
public class ToBase64Encoder extends AbstractEncoder {

    @Override
    public String encode(String source) {
        try {
            return new String(Base64.encodeBase64(source.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    @Override
    public Object encode(byte[] source) {
        return new String(Base64.encodeBase64(source));
    }
    
    @Override
    public String getName() {
        return "To Base64";
    }

}
