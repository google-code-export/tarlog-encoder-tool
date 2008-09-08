package tarlog.encoder.tool.encoders;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import tarlog.encoder.tool.api.AbstractEncoder;

public class MyUrlDecoder extends AbstractEncoder {

    @Override
    public Object encode(String source) {
        try {
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            showException(e);
            return null;
        }
    }

}
