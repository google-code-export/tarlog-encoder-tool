package tarlog.encodertool.encoders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tarlog.encodertool.AbstractEncoder;
import tarlog.encodertool.Utils;


public class MyUrlEncoder extends AbstractEncoder {

    @Override
    public String getName() {
        return "URL Encoder";
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
            return URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

}
