package tarlog.encoder.tool.api;

import java.io.File;

import tarlog.encoder.tool.api.fields.InputField;

public abstract class FileAwareEncoder extends AbstractEncoder {

    @InputField(name = "File", order = -300)
    private File file;

    protected File getFile() {
        return file;
    }
}
