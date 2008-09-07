package tarlog.encoder.tool.encoders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.eclipse.jface.dialogs.Dialog;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputTextField;
import tarlog.encoder.tool.api.fields.Validator;

public class X509CertificateEncoder extends AbstractEncoder implements
    Validator {

    private X509Certificate     cert;

    @InputField(name = "Certificate File", order = -300)
    private File                file;

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Signature")
    @InputTextField(multiline = true)
    private String              signature;

    public String isValid() {
        if (file == null || file.equals("")) {
            return "Certificate File cannot be empty";
        }
        if (signature == null || signature.equals("")) {
            return "Signature cannot be empty";
        }
        return null;
    }

    @Override
    protected int beforeEncode() {
        int inputStatus = super.beforeEncode();
        if (inputStatus != Dialog.OK) {
            return inputStatus;
        }
        try {
            InputStream inStream = new FileInputStream(file);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();
            return Dialog.OK;
        } catch (Exception e) {
            showException(e);
            return Dialog.CANCEL;
        }
    }

    @Override
    public Object encode(byte[] source) {
        try {
            PublicKey publicKey = cert.getPublicKey();
            Signature sig = Signature.getInstance(algorithm.name());
            sig.initVerify(publicKey);
            sig.update(source);
            return String.valueOf(sig.verify(Utils.bytesFromHex(signature)));
        } catch (Exception e) {
            showException(e);
            return null;
        }
    }

}
