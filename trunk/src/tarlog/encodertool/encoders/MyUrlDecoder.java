package tarlog.encodertool.encoders;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import tarlog.encodertool.AbstractEncoder;
import tarlog.encodertool.Utils;


public class MyUrlDecoder extends AbstractEncoder {

    @Override
    public String getName() {
        return "URL Decoder";
    }
    
    @Override
    public Object encode(byte[] source) {
        try {
            return encode(new String(source, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
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
