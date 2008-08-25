package tarlog.encoder.tool.encoders;

import java.security.Signature;
import java.security.cert.Certificate;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.KeyStoreAwareEncoder;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.TextField;

public class VerifySignature extends KeyStoreAwareEncoder {

    public static final String SIGNATURES = "Digital Signatures";

    @InputField(name = "Algorithm", readonly=true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;
    
    @InputField(name = "Certificate Alias")
    private String              alias;
    
    @InputField(name = "Signature")
    @TextField(multiline = true)
    private String              signature;
    
    @Override
    public String getName() {
        return "Verify Signature";
    }

    @Override
    public String getGroup() {
        return SIGNATURES;
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
            Signature sig = Signature.getInstance(algorithm.name());
            Certificate certificate = keystore.getCertificate(alias);
            sig.initVerify(certificate.getPublicKey());
            sig.update(source);
            return String.valueOf(sig.verify(Utils.bytesFromHex(signature)));
        } catch (Exception e) {
            Utils.showException(shell, e);
            return null;
        }
    }



    //    public class SigDetailsDialog extends Dialog {
    //
    //        private Combo uiAlgorithm;
    //        private Text  uiAlias;
    //        private Text  uiSignature;
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
    //            label.setText("Certificate: ");
    //            uiAlias = new Text(composite, SWT.SINGLE | SWT.BORDER);
    //            if (alias != null) {
    //                uiAlias.setText(alias);
    //            }
    //
    //            label = new Label(composite, SWT.NONE);
    //            label.setText("Signature (as bytes): ");
    //            uiSignature = new Text(composite, SWT.MULTI | SWT.BORDER);
    //            GridData layoutData = new GridData(GridData.FILL_BOTH);
    //            layoutData.verticalSpan = 3;
    //            uiSignature.setLayoutData(layoutData);
    //            if (signature != null) {
    //                uiSignature.setText(signature);
    //            }
    //            return composite;
    //        }
    //
    //        protected void buttonPressed(int buttonId) {
    //            if (buttonId == IDialogConstants.OK_ID) {
    //                algorithm = uiAlgorithm.getText();
    //                alias = uiAlias.getText();
    //                signature = uiSignature.getText();
    //            }
    //            super.buttonPressed(buttonId);
    //        }
    //
    //    }

}
