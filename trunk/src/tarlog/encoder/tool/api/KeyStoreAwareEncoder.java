package tarlog.encoder.tool.api;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

import org.eclipse.jface.dialogs.Dialog;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputTextField;

public abstract class KeyStoreAwareEncoder extends AbstractEncoder {

    protected KeyStore keystore;

    @InputField(name = "Key Store File", order = -300)
    private File       file;

    @InputField(name = "Key Store Type", order = -200)
    @InputTextField(values = { "JKS", "PKCS12" })
    private String     type = "JKS";

    @InputField(name = "Key Store Password", order = -100)
    @InputTextField(password = true)
    private String     password;

    @Override
    protected int beforeEncode() {
        int inputStatus = super.beforeEncode();
        if (inputStatus != Dialog.OK) {
            return inputStatus;
        }
        try {
            keystore = KeyStore.getInstance(type);
        } catch (KeyStoreException e) {
            Utils.showException(shell, e);
            return Dialog.CANCEL;
        }

        try {
            keystore.load(new FileInputStream(file), password.toCharArray());
        } catch (Exception e) {
            Utils.showException(shell, e);
            return Dialog.CANCEL;
        }
        return inputStatus;
    }
}
