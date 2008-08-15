package tarlog.encodertool;

import java.io.UnsupportedEncodingException;

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
        try {
            Class<? extends AbstractEncoder> realClass = getClass();
            if (realClass.getMethod("encode", String.class).getDeclaringClass() != realClass
                && realClass.getMethod("encode", byte[].class).getDeclaringClass() != realClass) {
                throw new RuntimeException(String.format("The encoder class %s must override at least one 'encode' method", realClass.getName()));
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
        try {
            return encode(source.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    public Object encode(byte[] source) {
        try {
            return encode(new String(source, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
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
