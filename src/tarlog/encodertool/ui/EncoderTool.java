package tarlog.encodertool.ui;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encodertool.AbstractEncoder;
import tarlog.encodertool.FileAware;
import tarlog.encodertool.Utils;

/**
 *
 */
public class EncoderTool extends ApplicationWindow {

    private static final String ENCODERS_FILE = "encoders.properties";
    private Properties          properties;
    private Text                targetText;
    private Text                sourceText;
    private Composite           leftComposite;
    private Shell               shell;

    public EncoderTool() {
        super(null);
    }

    public void init() throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(
            ENCODERS_FILE));
        setBlockOnOpen(true);
        open();
    }

    @Override
    protected Control createContents(Composite parent) {
        shell = getShell();
        shell.setText("Encoder Tool");
        shell.setMaximized(true);

        SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
        createTopPart(sashForm);
        createBottomPart(sashForm);
        sashForm.setWeights(new int[] { 1, 2 });

        load();
        return shell;
    }

    private void createTopPart(Composite parent) {
        Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayout(new GridLayout());
        sourceText = createTextEditor(composite, SWT.MULTI | SWT.BORDER);
    }

    private void createBottomPart(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        createLeftPart(sashForm);
        createRightPart(sashForm);
        sashForm.setWeights(new int[] { 1, 3 });
    }

    private void createLeftPart(Composite parent) {
        leftComposite = new Composite(parent, SWT.BORDER);
        RowLayout layout = new RowLayout();
        layout.type = SWT.VERTICAL;
        leftComposite.setLayout(layout);
    }

    private void load() {
        for (Object property : properties.values()) {
            try {
                @SuppressWarnings("unchecked")
                Class<AbstractEncoder> clazz = (Class<AbstractEncoder>) Class.forName(property.toString());
                final AbstractEncoder encoder = clazz.newInstance();
                encoder.setSource(sourceText);
                encoder.setTarget(targetText);
                encoder.setShell(shell);
                final Composite composite = new Composite(leftComposite, SWT.NULL);
                RowLayout layout = new RowLayout();
                layout.type = SWT.HORIZONTAL;
                composite.setLayout(layout);
                final Button button = new Button(composite, SWT.RADIO);
                button.setText(encoder.getName());
                button.addSelectionListener(encoder);
                button.addSelectionListener(new AbstractSelectionListener() {

                    public void widgetSelected(SelectionEvent e) {
                        for (Control child : leftComposite.getChildren()) {
                            Composite compositeChild = (Composite) child;
                            for (Control lbutton : compositeChild.getChildren()) {
                                if (e.getSource() != lbutton
                                    && lbutton instanceof Button
                                    && (lbutton.getStyle() & SWT.RADIO) != 0) {
                                    ((Button) lbutton).setSelection(false);
                                }
                            }
                        }

                    }
                });
                if (encoder instanceof FileAware) {
                    Button fileButton = new Button(composite, SWT.PUSH);
                    fileButton.setText("...");
                    fileButton.addSelectionListener(new AbstractSelectionListener() {

                        public void widgetSelected(SelectionEvent e) {
                            FileDialog fileDialog = new FileDialog(shell);
                            String file = fileDialog.open();
                            ((FileAware) encoder).setFileName(file);
                            button.setText(encoder.getName());
                            leftComposite.layout();
                        }
                    });
                }
            } catch (Exception e) {
                Utils.showException(shell, e);
            }
        }
    }

    private void createRightPart(Composite parent) {
        Composite rightComposite = new Composite(parent, SWT.BORDER);
        rightComposite.setLayout(new GridLayout());
        targetText = createTextEditor(rightComposite, SWT.MULTI | SWT.READ_ONLY
            | SWT.BORDER);
    }

    private Text createTextEditor(Composite parent, int style) {
        Text text = new Text(parent, style | SWT.WRAP);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        final Button showBytesButton = new Button(parent, SWT.CHECK);
        showBytesButton.setData(text);
        text.setData(showBytesButton);
        showBytesButton.setText("Show bytes");
        showBytesButton.addSelectionListener(new ShowBytesListener());
        return text;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        EncoderTool encoderTool = new EncoderTool();
        encoderTool.init();
    }
}
