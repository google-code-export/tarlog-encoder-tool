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
package tarlog.encoder.tool.ui.inner;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import tarlog.ui.swt.ddialog.utils.AbstractSelectionListener;
import tarlog.ui.swt.ddialog.utils.Utils;

public class ShowBytesListener extends AbstractSelectionListener {

    public void widgetSelected(SelectionEvent e) {

        Button showBytesButton = (Button)e.getSource();
        String charsetText = null;
        try {
            Text targetText = (Text)showBytesButton.getData();
            Combo charsetCombo = (Combo)targetText.getData(Charset.class.getName());
            charsetText = charsetCombo.getText();
            Charset charset = Charset.forName(charsetText);
            boolean selection = showBytesButton.getSelection();
            if (selection) {
                targetText.setText(Utils.bytesToHex(targetText.getText().getBytes(charset)));
            } else {
                byte[] origBytes = Utils.bytesFromHex(targetText.getText());
                targetText.setText(new String(origBytes, charset));
                byte[] resultBytes = targetText.getText().getBytes(charset);
                if (!Arrays.equals(origBytes, resultBytes)) {
                    if (!Utils
                        .askYesNoQuestion(Display.getCurrent().getActiveShell(),
                                          "Unreversible conversion",
                                          "The conversion is not reversible. " + "Do you want to continue?")) {
                        showBytesButton.setSelection(true);
                        targetText.setText(Utils.bytesToHex(origBytes));
                    }
                }
            }
        } catch (UnsupportedCharsetException e1) {
            Utils.showErrorMessage(showBytesButton.getShell(),
                                   "Unsupported charset",
                                   "Unsupported charset: " + charsetText);
        } catch (Exception e1) {
            Utils.showException(showBytesButton.getShell(), e1);
        }
    }
}
