package tarlog.encodertool;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Utils {

    public static void showErrorMessage(Shell shell, String title, String text) {
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        messageBox.setMessage(String.valueOf(text));
        messageBox.setText(String.valueOf(title));
        messageBox.open();
    }

    public static void showInformationMessage(Shell shell, String title, String text) {
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION
                | SWT.OK);
        messageBox.setMessage(text);
        messageBox.setText(title);
        messageBox.open();
    }
    
    public static void showException(Shell shell, Throwable t) {
        t.printStackTrace();
        StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        Utils.showErrorMessage(shell, "Exception occured", out.toString());
    }

    public static String bytesToHex(String string) {
        byte[] textAsBytes = string.getBytes();
        return bytesToHex(textAsBytes);
    }

    public static String bytesToHex(byte[] textAsBytes) {
        char[] encodeHex = Hex.encodeHex(textAsBytes);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < encodeHex.length; ++i) {
            builder.append(encodeHex[i]);
            if (i % 2 != 0) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    public static byte[] bytesFromHex(String hex) throws DecoderException {
        String text = hex.replaceAll(" ", "");
        return Hex.decodeHex(text.toCharArray());
    }
}
