package tarlog.encodertool.encoders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import tarlog.encodertool.AbstractEncoder;
import tarlog.encodertool.Utils;

public class InflaterEncoder extends AbstractEncoder {

    private boolean nowrap = true;

    @Override
    public String getName() {
        return "Inflater";
    }

    @Override
    public Object encode(byte[] source) {
        try {
            Boolean input = Utils.getBooleanInput(shell, "Select compression",
                "GZIP compatible compression", String.valueOf(nowrap));

            if (input != null) {
                nowrap = input;
            }
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(source);
            InflaterInputStream inflater = new InflaterInputStream(bytesIn,
                new Inflater(nowrap));

            byte[] bytes = new byte[4096];
            int read = inflater.read(bytes);
            byte[] result = new byte[read];
            System.arraycopy(bytes, 0, result, 0, read);
            return result;
        } catch (IOException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    @Override
    public Object encode(String source) {
        return encode(source.getBytes());
    }
}
