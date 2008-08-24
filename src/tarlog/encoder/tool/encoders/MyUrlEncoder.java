package tarlog.encoder.tool.encoders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tarlog.encoder.tool.AbstractEncoder;
import tarlog.encoder.tool.Utils;


public class MyUrlEncoder extends AbstractEncoder {

    public static final String URL_ENCODING = "URL Encoding";


    @Override
    public String getGroup() {
        return URL_ENCODING;
    }
    
    @Override
    public String getName() {
        return "Encode";
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
