// (C) Copyright 2003-2008 Hewlett-Packard Development Company, L.P.
package tarlog.encoder.tool.encoders;

import org.apache.commons.codec.binary.Base64;

import tarlog.encoder.tool.api.AbstractEncoder;

/**
 *
 */
public class ToBase64Encoder extends AbstractEncoder {

    @Override
    public Object encode(byte[] source) {
        return new String(Base64.encodeBase64(source));
    }

}
