package tarlog.encoder.tool.encoders;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import tarlog.encoder.tool.FileAwareEncoder;
import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.ui.InputField;
import tarlog.encoder.tool.ui.TextField;

public class X509CertificateEncoder extends FileAwareEncoder {

    private X509Certificate     cert;

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Signature")
    @TextField(multiline = true)
    private String              signature;

    @Override
    public String getEncoderName() {
        return "X509 Verifier";
    }

    @Override
    public String getGroup() {
        return VerifySignature.SIGNATURES;
    }
    
    @Override
    public void setFileName(String fileName) {
        try {
            super.setFileName(fileName);
            InputStream inStream = new FileInputStream(fileName);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();
        } catch (Exception e) {
            Utils.showException(shell, e);
            super.setFileName(null);
        }
    }

    @Override
    public Object encode(byte[] source) {
        //        SigDetailsDialog inputDialog = new SigDetailsDialog(shell);
        //        int rc = inputDialog.open();
        //        if (rc != Dialog.OK) {
        //            return null;
        //        }
        if (cert == null) {
            Utils.showErrorMessage(shell, "Error",
                "Certificate must be initialized.");
            return null;
        }

        try {
            PublicKey publicKey = cert.getPublicKey();
            System.out.println("Using public key: " + publicKey.toString());
            Signature sig = Signature.getInstance(algorithm.name());
            sig.initVerify(publicKey);
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
    //        //        private Text  uiAlias;
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
    //            //            label = new Label(composite, SWT.NONE);
    //            //            label.setText("Certificate: ");
    //            //            uiAlias = new Text(composite, SWT.SINGLE | SWT.BORDER);
    //            //            if (alias != null) {
    //            //                uiAlias.setText(alias);
    //            //            }
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
    //                //                alias = uiAlias.getText();
    //                signature = uiSignature.getText();
    //            }
    //            super.buttonPressed(buttonId);
    //        }
    //
    //    }
}
