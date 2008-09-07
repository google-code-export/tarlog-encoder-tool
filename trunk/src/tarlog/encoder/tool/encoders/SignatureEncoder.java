package tarlog.encoder.tool.encoders;

import java.security.PrivateKey;
import java.security.Signature;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.api.KeyStoreAwareEncoder;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputTextField;

public class SignatureEncoder extends KeyStoreAwareEncoder {

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Private Key Alias")
    private String              alias;

    @InputField(name = "Private Key Password")
    @InputTextField(password = true)
    private String              password;

    @Override
    public String isValid() {
        String valid = super.isValid();
        if (valid != null) {
            return valid;
        }
        if (alias == null || alias.equals("")) {
            return "Private Key cannot be empty";
        }
        if (password == null || password.equals("")) {
            return "Private Key Password cannot be empty";
        }
        return null;
    }

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
