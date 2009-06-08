package tarlog.encoder.tool.ui.inner;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import tarlog.ui.swt.ddialog.utils.AbstractSelectionListener;
import tarlog.ui.swt.ddialog.utils.Utils;

public class ShowBytesListener extends AbstractSelectionListener {

    public void widgetSelected(SelectionEvent e) {

        Button showBytesButton = (Button) e.getSource();
        String charsetText = null;
        try {
            Text targetText = (Text) showBytesButton.getData();
            Combo charsetCombo = (Combo) targetText.getData(Charset.class.getName());
            charsetText = charsetCombo.getText();
            Charset charset = Charset.forName(charsetText);
            boolean selection = showBytesButton.getSelection();
            if (selection) {
                targetText.setText(Utils.bytesToHex(targetText.getText().getBytes(charset)));
            } else {
                targetText.setText(new String(Utils.bytesFromHex(targetText.getText()), charset));
            }
        } catch (UnsupportedCharsetException e1) {
            Utils.showErrorMessage(showBytesButton.getShell(), "Unsupported charset",
                "Unsupported charset: " + charsetText);
        } catch (Exception e1) {
            Utils.showException(showBytesButton.getShell(), e1);
        }
    }
}