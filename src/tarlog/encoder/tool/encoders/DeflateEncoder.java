package tarlog.encoder.tool.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.fields.InputField;

public class DeflateEncoder extends AbstractEncoder {

    @InputField(name = "GZIP compatible compression")
    private boolean nowrap = true;


    @Override
    public Object encode(byte[] source) {

        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFLATED, nowrap);
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(
                bytesOut, deflater);
            deflaterStream.write(source);
            deflaterStream.finish();
            return bytesOut.toByteArray();
        } catch (IOException e) {
            showException(e);
            return null;
        }
    }

}
