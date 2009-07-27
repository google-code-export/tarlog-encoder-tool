/*******************************************************************************
 *     Copyright 2009 Michael Elman (http://tarlogonjava.blogspot.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *     
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
package tarlog.encoder.tool.encoders;

import java.security.Signature;
import java.security.cert.Certificate;

import tarlog.encoder.tool.SignatureAlgorithms;
import tarlog.encoder.tool.api.KeyStoreAwareEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;
import tarlog.ui.swt.ddialog.api.fields.InputTextField;
import tarlog.ui.swt.ddialog.utils.Utils;

public class VerifySignature extends KeyStoreAwareEncoder {

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Certificate Alias", required = true)
    @InputTextField(validateNotEmpty = true)
    private String              alias;

    @InputField(name = "Signature", required = true)
    @InputTextField(multiline = true, validateNotEmpty = true)
    private String              signature;

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
