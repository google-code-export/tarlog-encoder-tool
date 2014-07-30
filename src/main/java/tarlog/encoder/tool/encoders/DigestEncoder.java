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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;

public class DigestEncoder extends AbstractEncoder {

	private enum DigestAlgorithm {
		MD2, MD5, SHA_1("SHA-1"), SHA_256("SHA-256"), SHA_384("SHA-384"), SHA_512("SHA-512");
		
		private String algorithmName;
		
		private DigestAlgorithm() {
		}

		private DigestAlgorithm(String an) {
			this.algorithmName = an;
		}

		public String getAlgorithmName() {
			if (algorithmName == null) {
				return name();
			}
			return algorithmName;
		}
	}

    @InputField(name = "Algorithm", readonly = true, required = true)
    private DigestAlgorithm algorithm;
	
	@Override
	public Object encode(byte[] source) {

		try {
			MessageDigest md = MessageDigest.getInstance(algorithm.getAlgorithmName());
			md.update(source);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

    @Override
    protected String historyInfo() {
        String historyInfo = super.historyInfo();
        return String.format("%s (%s)", historyInfo, String.valueOf(algorithm));
    }
}
