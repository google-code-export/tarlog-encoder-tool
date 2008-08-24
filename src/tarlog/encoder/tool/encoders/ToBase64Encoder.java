// (C) Copyright 2003-2008 Hewlett-Packard Development Company, L.P.
package tarlog.encoder.tool.encoders;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import tarlog.encoder.tool.AbstractEncoder;
import tarlog.encoder.tool.Utils;


/**
 *
 */
public class ToBase64Encoder extends AbstractEncoder {

    @Override
    public String getGroup() {
        return FromBase64Encoder.BASE64;
    }
    
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
