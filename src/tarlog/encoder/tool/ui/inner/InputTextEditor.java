package tarlog.encoder.tool.ui.inner;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.GridComposite;
import tarlog.ui.swt.ddialog.utils.Utils;

public class InputTextEditor extends GridComposite {

    private final Text          text;
    private final GridComposite bottomComposite;

    public GridComposite getBottomComposite() {
        return bottomComposite;
    }

    public InputTextEditor(final Composite parent, int style) {
        super(parent, style);
        boolean readonly = (style & SWT.READ_ONLY) != 0;
        setLayoutData(new GridData(GridData.FILL_BOTH));
        text = new Text(this, style | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);

        text.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if ((e.stateMask & SWT.CTRL) != 0) {
                    switch (e.keyCode) {
                        case 'a':
                        case 'A':
                            text.selectAll();
                            break;
                    }
                }
            }
        });

        text.setLayoutData(new GridData(GridData.FILL_BOTH));

        bottomComposite = new GridComposite(this, SWT.NONE, readonly ? 3 : 4);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Show bytes
        final Button showBytesButton = new Button(bottomComposite, SWT.CHECK);
        showBytesButton.setData(getText());
        text.setData(showBytesButton);
        showBytesButton.setText("Show bytes");
        showBytesButton.addSelectionListener(new ShowBytesListener());

        // Charset
        Combo combo = new Combo(bottomComposite, SWT.DROP_DOWN);
        combo.add("UTF-8");
        combo.add("UTF-16");
        combo.add("US-ASCII");
        combo.add("ISO-8859-1");
        combo.select(0);
        text.setData(Charset.class.getName(), combo);

        // Clean Link
        Link cleanLink = new Link(bottomComposite, SWT.NONE);
        cleanLink.setText("<a>Clean</a>");
        cleanLink.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                text.setText("");
            }
        });

        // Import Link
        if (!readonly) {
            Link importLink = new Link(bottomComposite, SWT.NONE);
            importLink.setText("<a>Import</a>");
            importLink.addListener(SWT.Selection, new Listener() {

                public void handleEvent(Event event) {
                    FileInputStream is = null;
                    try {
                        FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                        String name = fileDialog.open();
                        if (name != null) {
                            is = new FileInputStream(name);
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            copyStream(is, os);
                            String bytes = Utils.bytesToHex(os.toByteArray());
                            ((Button) text.getData()).setSelection(true);
                            text.setText(bytes);
                        }
                    } catch (IOException e) {
                        Utils.showException(getShell(), e);
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    private static void copyStream(InputStream src, OutputStream dst) throws IOException {
        byte[] bytes = new byte[1024];
        int read = 0;
        while ((read = src.read(bytes)) != -1) {
            dst.write(bytes, 0, read);
        }
    }

    public final Text getText() {
        return text;
    }

}
