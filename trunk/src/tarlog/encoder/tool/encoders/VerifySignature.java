package tarlog.encoder.tool.encoders;

import java.security.Signature;
import java.security.cert.Certificate;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.KeyStoreAwareEncoder;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputTextField;

public class VerifySignature extends KeyStoreAwareEncoder {

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Certificate Alias")
    private String              alias;

    @InputField(name = "Signature")
    @InputTextField(multiline = true)
    private String              signature;

    @Override
    public String isValid() {
        String valid = super.isValid();
        if (valid != null) {
            return valid;
        }
        if (alias == null || alias.equals("")) {
            return "Certificate alias cannot be empty";
        }
        if (signature == null || signature.equals("")) {
            return "Signature cannot be empty";
        }
        return null;
    }
    
    @Override
    public Object encode(byte[] source) {
        try {
            Signature sig = Signature.getInstance(algorithm.name());
            Certificate certificate = keystore.getCertificate(alias);
            sig.initVerify(certificate.getPublicKey());
            sig.update(source);
            return String.valueOf(sig.verify(Utils.bytesFromHex(signature)));
        } catch (Exception e) {
            showException(e);
            return null;
        }
    }

}
