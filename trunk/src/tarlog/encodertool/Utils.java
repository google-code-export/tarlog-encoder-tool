package tarlog.encodertool;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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

    public static void showInformationMessage(Shell shell, String title,
        String text) {
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION
            | SWT.OK);
        messageBox.setMessage(text);
        messageBox.setText(title);
        messageBox.open();
    }

    public static void showException(Shell shell, Throwable t) {
        t.printStackTrace();
        MultiStatus info = new MultiStatus("OK", IStatus.ERROR, t.getMessage(), t);
        for (StackTraceElement stackTraceElement : t.getStackTrace()) {
            info.add(new Status(IStatus.ERROR, "OK", stackTraceElement.toString()));
        }
        ErrorDialog.openError(shell, "Exception occured", null, info);
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
        String text = hex.replaceAll("\\s", "");
        return Hex.decodeHex(text.toCharArray());
    }

    public static Boolean getBooleanInput(Shell parentShell,
        String dialogTitle, String dialogMessage, String initialValue) {
        InputDialog inputDialog = new InputDialog(parentShell, dialogTitle,
            dialogMessage, initialValue, new IInputValidator() {

                public String isValid(String newText) {
                    if (newText.equals("true") || newText.equals("false")) {
                        return null;
                    }
                    return "Should be true or false";
                }
            });
        int rc = inputDialog.open();
        if (rc == Dialog.OK) {
            return Boolean.parseBoolean(inputDialog.getValue());
        }
        return null;
    }
}
