package tarlog.encoder.tool.encoders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tarlog.encoder.tool.api.AbstractEncoder;

public class MyUrlEncoder extends AbstractEncoder {

    @Override
    public Object encode(String source) {
        try {
            return URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            showException(e);
            return null;
        }
    }

}
