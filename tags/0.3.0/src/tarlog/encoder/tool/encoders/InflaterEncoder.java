package tarlog.encoder.tool.encoders;

import java.util.zip.Inflater;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.fields.InputField;

public class InflaterEncoder extends AbstractEncoder {

    @InputField(name = "GZIP compatible compression")
    private boolean nowrap = true;

    @Override
    public Object encode(byte[] source) {
        try {
            Inflater decompresser = new Inflater(nowrap);
            decompresser.setInput(source, 0, source.length);
            byte[] tmpresult = new byte[4096];
            int resultLength = decompresser.inflate(tmpresult);
            decompresser.end();
            byte[] result = new byte[resultLength];
            System.arraycopy(tmpresult, 0, result, 0, resultLength);
            return result;
        } catch (Exception e) {
            showException(e);
            return null;
        }
    }

}
