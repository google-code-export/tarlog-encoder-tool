package tarlog.encodertool.ui;

import org.apache.commons.codec.DecoderException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;

public class ShowBytesListener extends AbstractSelectionListener {

    public void widgetSelected(SelectionEvent e) {

        Button showBytesButton = (Button) e.getSource();
        try {
            Text targetText = (Text) showBytesButton.getData();
            boolean selection = showBytesButton.getSelection();
            if (selection) {
                targetText.setText(Utils.bytesToHex(targetText.getText()));
            } else {
                targetText.setText(new String(
                    Utils.bytesFromHex(targetText.getText())));
            }
        } catch (DecoderException e1) {
            Utils.showException(showBytesButton.getShell(), e1);
        }
    }
}