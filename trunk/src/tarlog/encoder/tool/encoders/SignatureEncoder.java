package tarlog.encoder.tool.encoders;

import java.security.PrivateKey;
import java.security.Signature;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.api.KeyStoreAwareEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;
import tarlog.ui.swt.ddialog.api.fields.InputTextField;

public class SignatureEncoder extends KeyStoreAwareEncoder {

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Private Key Alias", required = true)
    @InputTextField(validateNotEmpty = true)
    private String              alias;

    @InputField(name = "Private Key Password", required = true)
    @InputTextField(password = true, validateNotEmpty = true)
    private String              password;

    @Override
    public Object encode(byte[] source) {
        try {
            Signature sig = Signature.getInstance(algorithm.name());
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias,
                password.toCharArray());
            sig.initSign(privateKey);
            sig.update(source);
            return sig.sign();
        } catch (Exception e) {
            showException(e);
            return null;
        }
    }
}
