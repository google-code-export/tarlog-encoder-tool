package tarlog.encoder.tool;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.AbstractSelectionListener;

/**
 * Basic class for all encoders. The implementing class should extend from this class
 * and override at least one encode method.
 */
public abstract class AbstractEncoder extends AbstractSelectionListener {

    private Text    targetText;
    private Text    sourceText;
    protected Shell shell;

    public AbstractEncoder() {
        try {
            // verify that at least one encode method was overriden
            Class<? extends AbstractEncoder> realClass = getClass();
            if (realClass.getMethod("encode", String.class).getDeclaringClass() != realClass
                && realClass.getMethod("encode", byte[].class).getDeclaringClass() != realClass) {
                throw new RuntimeException(
                    String.format(
                        "The encoder class %s must override at least one 'encode' method",
                        realClass.getName()));
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    final public void widgetSelected(SelectionEvent e) {
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

    /**
     * <p>
     * Method is invoked to convert text source
     * <p>
     * Override this method to add a specific behavior. By default
     * <tt>encode(byte[] source)</tt> is invoked.
     * 
     * @param source
     * @return the result of the encoding. Usually should be either String or
     *         byte[]. Otherwise toString() of the result will be invoked. In
     *         case of null, the method invocation will be canceled and the
     *         result will remain unchanged.
     */
    public Object encode(String source) {
        try {
            return encode(source.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    /**
     * <p>
     * Method is invoked to convert bytes source
     * <p>
     * Override this method to add a specific behavior. By default
     * <tt>encode(String source)</tt> is invoked.
     * 
     * @param source
     * @return the result of the encoding. Usually should be either String or
     *         byte[]. Otherwise toString() of the result will be invoked. In
     *         case of null, the method invocation will be canceled and the
     *         result will remain unchanged.
     */
    public Object encode(byte[] source) {
        try {
            return encode(new String(source, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    /**
     * @return the name of the encoder
     */
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
