package tarlog.encodertool;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractEncoder implements SelectionListener {

    private Text    targetText;
    private Text    sourceText;
    protected Shell shell;

    public AbstractEncoder() {

    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        try {
            boolean sourceBytes = ((Button) sourceText.getData()).getSelection();
            String inText = sourceText.getText();
            Object out;
            if (sourceBytes) {
                out = encode(Utils.bytesFromHex(inText));
            } else {
                out = encode(inText);
            }
            if (out == null) {
                return;
            }
            if (out instanceof byte[]) {
                String bytes = Utils.bytesToHex((byte[]) out);
                ((Button) targetText.getData()).setSelection(true);
                targetText.setText(bytes);
            } else {
                ((Button) targetText.getData()).setSelection(false);
                targetText.setText(String.valueOf(out));
            }
        } catch (Exception e1) {
            Utils.showException(shell, e1);
        }
    }

    public Object encode(String source) {
        return source;
    }

    public Object encode(byte[] source) {
        return source;
    }

    public abstract String getName();

    public void setTarget(Text targetText) {
        this.targetText = targetText;
    }

    public void setSource(Text sourceText) {
        this.sourceText = sourceText;
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

}
