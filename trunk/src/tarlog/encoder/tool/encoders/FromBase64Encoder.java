// (C) Copyright 2003-2008 Hewlett-Packard Development Company, L.P.
package tarlog.encoder.tool.encoders;

import org.apache.commons.codec.binary.Base64;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder;


/**
 *
 */
public class FromBase64Encoder extends AbstractEncoder {

    public static final String BASE64 = "Base64";

    @Override
    public String getGroup() {
        return BASE64;
    }
    
    @Override
    public Object encode(String source) {
        try {
            return Base64.decodeBase64(source.getBytes("UTF-8"));
        } catch (Exception e) {
            Utils.showException(shell, e);
            return null;
        }
    }
    
    @Override
    public Object encode(byte[] source) {
        return Base64.decodeBase64(source);
    }

    @Override
    public String getName() {
        return "From Base64";
    }

}
