package tarlog.encodertool.encoders;

import java.security.PrivateKey;
import java.security.Signature;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encodertool.Utils;

public class SignatureEncoder extends KeyStoreAwareEncoder {

    @Override
    public String getEncoderName() {
        return "Create Signature";
    }

    @Override
    public Object encode(String source) {
        return encode(source.getBytes());
    }

    @Override
    public Object encode(byte[] source) {
        if (keystore == null) {
            Utils.showErrorMessage(shell, "Error",
                "Key store should be initialized");
            return null;
        }
        InputDialog inputDialog = new InputDialog(shell);
        int rc = inputDialog.open();
        if (rc != Dialog.OK) {
            return null;
        }
        try {
            Signature sig = Signature.getInstance(algorithm);
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias,
                password.toCharArray());
            sig.initSign(privateKey);
            sig.update(source);
            Utils.showInformationMessage(shell, "Signing", new String(source));
            return sig.sign();
        } catch (Exception e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    private String algorithm;
    private String alias;
    private String password;

    public class InputDialog extends Dialog {

        private Combo uiAlgorithm;
        private Text  uiAlias;
        private Text  uiPassword;

        public InputDialog(Shell parent) {
            super(parent);
        }

        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            shell.setText("Select Signature Details");
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            Label label = new Label(composite, SWT.NONE);
            label.setText("Algorithm: ");
            uiAlgorithm = new Combo(composite, SWT.DROP_DOWN);
            uiAlgorithm.add("SHA1withDSA");
            uiAlgorithm.add("SHA1withRSA");
            if (algorithm != null) {
                uiAlgorithm.setText(algorithm);
            } else {
                uiAlgorithm.setText("SHA1withDSA");
            }
            label = new Label(composite, SWT.NONE);
            label.setText("Private Key: ");
            uiAlias = new Text(composite, SWT.SINGLE | SWT.BORDER);
            if (alias != null) {
                uiAlias.setText(alias);
            }
            label = new Label(composite, SWT.NONE);
            label.setText("Private Key Password: ");
            uiPassword = new Text(composite, SWT.SINGLE | SWT.BORDER);
            if (password != null) {
                uiPassword.setText(password);
            }
            return composite;
        }

        protected void buttonPressed(int buttonId) {
            if (buttonId == IDialogConstants.OK_ID) {
                algorithm = uiAlgorithm.getText();
                alias = uiAlias.getText();
                password = uiPassword.getText();
            }
            super.buttonPressed(buttonId);
        }

    }
}
