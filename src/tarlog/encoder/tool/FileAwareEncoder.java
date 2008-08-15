package tarlog.encoder.tool;

import java.io.File;


public abstract class FileAwareEncoder extends AbstractEncoder implements
    FileAware {

    private File file;

    @Override
    final public String getName() {
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
