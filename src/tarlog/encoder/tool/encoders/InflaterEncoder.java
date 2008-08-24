package tarlog.encoder.tool.encoders;

import java.util.zip.Inflater;

import tarlog.encoder.tool.AbstractEncoder;
import tarlog.encoder.tool.Utils;

public class InflaterEncoder extends AbstractEncoder {

    private boolean nowrap = true;

    @Override
    public String getName() {
        return "Inflate";
    }

    @Override
    public Object encode(byte[] source) {
        try {
            Boolean input = Utils.getBooleanInput(shell, "Select compression",
                "GZIP compatible compression", String.valueOf(nowrap));

            if (input != null) {
                nowrap = input;
            }

            Inflater decompresser = new Inflater();
            decompresser.setInput(source, 0, source.length);
            byte[] tmpresult = new byte[4096];
            int resultLength = decompresser.inflate(tmpresult);
            decompresser.end();
            byte[] result = new byte[resultLength];
            System.arraycopy(tmpresult, 0, result, 0, resultLength);

            //            ByteArrayInputStream bytesIn = new ByteArrayInputStream(source);
            //            InflaterInputStream inflater = new InflaterInputStream(bytesIn,
            //                inflater);
            //
            //            byte[] bytes = new byte[4096];
            //            int read = inflater.read(bytes);
            //            byte[] result = new byte[read];
            //            System.arraycopy(bytes, 0, result, 0, read);
            return tmpresult;
        } catch (Exception e) {
            Utils.showException(shell, e);
            return null;
        }
    }

}
