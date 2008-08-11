package tarlog.encodertool.encoders;

import java.io.File;

import tarlog.encodertool.AbstractEncoder;
import tarlog.encodertool.FileAware;

public abstract class FileAwareEncoder extends AbstractEncoder implements
    FileAware {

    private File file;

    @Override
    public String getName() {
        return getEncoderName() + " (" + (file == null ? "null" : file.getName())
            + ")";
    }

    public abstract String getEncoderName();

    public void setFileName(String fileName) {
        file = new File(fileName);
    }

    protected File getFile() {
        return file;
    }
}
