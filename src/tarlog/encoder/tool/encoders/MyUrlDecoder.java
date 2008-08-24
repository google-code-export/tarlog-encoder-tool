package tarlog.encoder.tool.encoders;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import tarlog.encoder.tool.AbstractEncoder;
import tarlog.encoder.tool.Utils;


public class MyUrlDecoder extends AbstractEncoder {

    @Override
    public String getGroup() {
        return MyUrlEncoder.URL_ENCODING;
    }
    
    @Override
    public String getName() {
        return "Decode";
    }
    
    
    @Override
    public Object encode(String source) {
        try {
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

}
