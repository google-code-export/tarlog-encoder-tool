package tarlog.encodertool.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import tarlog.encoder.tool.AbstractEncoder;
import tarlog.encoder.tool.Utils;

public class DeflateEncoder extends AbstractEncoder {

    boolean nowrap = true;

    @Override
    public String getName() {
        return "Deflate";
    }

    @Override
    public Object encode(byte[] source) {

        try {
            Boolean input = Utils.getBooleanInput(shell, "Select compression",
                "GZIP compatible compression", String.valueOf(nowrap));

            if (input != null) {
                nowrap = input;
            }
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFLATED, nowrap);
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(
                bytesOut, deflater);
            deflaterStream.write(source);
            deflaterStream.finish();
            return bytesOut.toByteArray();
        } catch (IOException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

}
