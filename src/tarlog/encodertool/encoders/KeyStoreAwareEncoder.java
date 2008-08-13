package tarlog.encodertool.encoders;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;

import tarlog.encodertool.Utils;

public abstract class KeyStoreAwareEncoder extends FileAwareEncoder {

    protected KeyStore keystore;

    @Override
    public void setFileName(String fileName) {
        super.setFileName(fileName);
        InputDialog inputDialog = new InputDialog(shell, "Keystore type",
            "Please, enter the keystore type", "JKS",null /* new IInputValidator() {

                public String isValid(String newText) {
                    if (newText.equals("JKS") || newText.equals("PKCS12")) {
                        return null;
                    }
                    return "Currently only JKS or PKCS12 are supported";
                }
            }*/);
        int rc = inputDialog.open();
        String type;
        if (rc != InputDialog.OK) {
            type = "JKS";
        } else {
            type = inputDialog.getValue();
        }
        try {
            keystore = KeyStore.getInstance(type);
        } catch (KeyStoreException e) {
            Utils.showException(shell, e);
        }

        inputDialog = new InputDialog(shell, "Keystore password",
            "Please, enter the keystore password", null, null);
        rc = inputDialog.open();
        char[] password = null;
        if (rc == InputDialog.OK) {
            password = inputDialog.getValue().toCharArray();
        }
        try {
            keystore.load(new FileInputStream(getFile()), password);
        } catch (Exception e) {
            Utils.showException(shell, e);
        }
    }
}
