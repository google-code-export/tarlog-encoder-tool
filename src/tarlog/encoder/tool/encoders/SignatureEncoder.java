package tarlog.encoder.tool.encoders;

import java.security.PrivateKey;
import java.security.Signature;

import tarlog.encoder.tool.KeyStoreAwareEncoder;
import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.ui.InputField;

public class SignatureEncoder extends KeyStoreAwareEncoder {

    @Override
    public String getEncoderName() {
        return "Create Signature";
    }

    @Override
    public Object encode(byte[] source) {
        if (keystore == null) {
            Utils.showErrorMessage(shell, "Error",
                "Key store should be initialized");
            return null;
        }
//        SigDetailsDialog inputDialog = new SigDetailsDialog(shell);
//        int rc = inputDialog.open();
//        if (rc != Dialog.OK) {
//            return null;
//        }
        try {
            Signature sig = Signature.getInstance(algorithm);
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias,
                password.toCharArray());
            sig.initSign(privateKey);
            sig.update(source);
            return sig.sign();
        } catch (Exception e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    @InputField
    private String algorithm;
    @InputField
    private String alias;
    @InputField
    private String password;

    //    public class SigDetailsDialog extends Dialog {
    //
    //        private Combo uiAlgorithm;
    //        private Text  uiAlias;
    //        private Text  uiPassword;
    //
    //        public SigDetailsDialog(Shell parent) {
    //            super(parent);
    //        }
    //
    //        protected void configureShell(Shell shell) {
    //            super.configureShell(shell);
    //            shell.setText("Select Signature Details");
    //        }
    //
    //        @Override
    //        protected Control createDialogArea(Composite parent) {
    //            Composite composite = new Composite(
    //                (Composite) super.createDialogArea(parent), SWT.NONE);
    //            composite.setLayout(new GridLayout(2, false));
    //            Label label = new Label(composite, SWT.NONE);
    //            label.setText("Algorithm: ");
    //            uiAlgorithm = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
    //            uiAlgorithm.add("SHA1withDSA");
    //            uiAlgorithm.add("SHA1withRSA");
    //            if (algorithm != null) {
    //                uiAlgorithm.setText(algorithm);
    //            } else {
    //                uiAlgorithm.setText("SHA1withDSA");
    //            }
    //            label = new Label(composite, SWT.NONE);
    //            label.setText("Private Key Alias: ");
    //            uiAlias = new Text(composite, SWT.SINGLE | SWT.BORDER);
    //            if (alias != null) {
    //                uiAlias.setText(alias);
    //            }
    //            label = new Label(composite, SWT.NONE);
    //            label.setText("Private Key Password: ");
    //            uiPassword = new Text(composite, SWT.SINGLE | SWT.BORDER);
    //            if (password != null) {
    //                uiPassword.setText(password);
    //            }
    //            return composite;
    //        }
    //
    //        protected void buttonPressed(int buttonId) {
    //            if (buttonId == IDialogConstants.OK_ID) {
    //                algorithm = uiAlgorithm.getText();
    //                alias = uiAlias.getText();
    //                password = uiPassword.getText();
    //            }
    //            super.buttonPressed(buttonId);
    //        }
    //
    //    }
}
