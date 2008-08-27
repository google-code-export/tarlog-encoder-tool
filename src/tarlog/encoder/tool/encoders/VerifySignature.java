package tarlog.encoder.tool.encoders;

import java.security.Signature;
import java.security.cert.Certificate;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.KeyStoreAwareEncoder;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.TextField;

public class VerifySignature extends KeyStoreAwareEncoder {

    public static final String  SIGNATURES = "Digital Signatures";

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm  = SignatureAlgorithms.SHA1withDSA;

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

}
