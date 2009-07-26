/*******************************************************************************
 *     Licensed to the Apache Software Foundation (ASF) under one
 *     or more contributor license agreements.  See the NOTICE file
 *     distributed with this work for additional information
 *     regarding copyright ownership.  The ASF licenses this file
 *     to you under the Apache License, Version 2.0 (the
 *     "License"); you may not use this file except in compliance
 *     with the License.  You may obtain a copy of the License at
 *     
 *      http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
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
import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;
import tarlog.ui.swt.ddialog.api.fields.InputTextField;
import tarlog.ui.swt.ddialog.utils.Utils;

public class X509CertificateEncoder extends AbstractEncoder {

    private X509Certificate     cert;

    @InputField(name = "Certificate File", required = true, order = -300)
    private File                file;

    @InputField(name = "Algorithm", readonly = true)
    private SignatureAlgorithms algorithm = SignatureAlgorithms.SHA1withDSA;

    @InputField(name = "Signature", required = true)
    @InputTextField(multiline = true, validateNotEmpty = true)
    private String              signature;

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
