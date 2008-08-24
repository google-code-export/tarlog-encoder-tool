package tarlog.encoder.tool.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.AbstractEncoder;
import tarlog.encoder.tool.FileAware;
import tarlog.encoder.tool.Utils;

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

    private Image getImage() {
        try {
            InputStream img = new FileInputStream("icons/encoders.jpg");
            ImageLoader imageLoader = new ImageLoader();
            ImageData[] load = imageLoader.load(img);
            return new Image(shell.getDisplay(), load[0]);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        shell = getShell();
        shell.setText("Encoder Tool");
        shell.setSize(1000, 700);
        Image image = getImage();
        if (image != null) {
            shell.setImage(image);
        }
        //        shell.setMaximized(true);

        SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
        createTopPart(sashForm);
        createBottomPart(sashForm);
        sashForm.setWeights(new int[] { 1, 2 });

        load();
        return shell;
    }

    private void createTopPart(Composite parent) {
        Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayout(new GridLayout(2, false));
        sourceText = createTextEditor(composite, SWT.MULTI | SWT.BORDER);
        Button exchange = new Button(composite, SWT.PUSH);
        exchange.setText("<>");
        exchange.addSelectionListener(new AbstractSelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Button sourceBytesButton = (Button) sourceText.getData();
                Button targetBytesButton = (Button) targetText.getData();

                String oldText = sourceText.getText();
                boolean oldBytesButtonStatus = sourceBytesButton.getSelection();

                sourceText.setText(targetText.getText());
                sourceBytesButton.setSelection(targetBytesButton.getSelection());
                targetText.setText(oldText);
                targetBytesButton.setSelection(oldBytesButtonStatus);
            }
        });
    }

    private void createBottomPart(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        createLeftPart(sashForm);
        createRightPart(sashForm);
        sashForm.setWeights(new int[] { 1, 3 });
    }

    private void createLeftPart(Composite parent) {
        final ScrolledComposite scrolledComposite = new ScrolledComposite(
            parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        leftComposite = new Composite(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(leftComposite);
        GridLayout layout = new GridLayout();
        leftComposite.setLayout(layout);
        leftComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

    }

    private void load() {
        @SuppressWarnings("unchecked")
        TreeSet<String> names = new TreeSet(properties.keySet());
        Map<String, Group> groups = new HashMap<String, Group>();
        for (Iterator<String> itr = names.iterator(); itr.hasNext();) {
            try {
                @SuppressWarnings("unchecked")
                Class<AbstractEncoder> clazz = (Class<AbstractEncoder>) Class.forName(properties.getProperty(itr.next()));
                final AbstractEncoder encoder = clazz.newInstance();
                encoder.setSource(sourceText);
                encoder.setTarget(targetText);
                encoder.setShell(shell);
                String groupName = encoder.getGroup();
                Group group = groups.get(groupName);
                if (group == null) {
                    group = new Group(leftComposite, SWT.NONE);
                    group.setText(groupName);
                    group.setLayout(new GridLayout());
                    group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                        true));
                    groups.put(groupName, group);
                }
                final Composite composite = new Composite(group, SWT.NULL);
                RowLayout layout = new RowLayout();
                layout.type = SWT.HORIZONTAL;
                composite.setLayout(layout);
                final Button button = new Button(composite, SWT.RADIO);
                button.setText(encoder.getName());
                button.addSelectionListener(new AbstractSelectionListener() {

                    public void widgetSelected(SelectionEvent e) {
                        for (Control child : leftComposite.getChildren()) {
                            Composite groupChild = (Composite) child;
                            for (Control groupChildControl : groupChild.getChildren()) {
                                Composite groupChildComposite = (Composite) groupChildControl;
                                for (Control lbutton : groupChildComposite.getChildren()) {
                                    if (e.getSource() != lbutton
                                        && lbutton instanceof Button
                                        && (lbutton.getStyle() & SWT.RADIO) != 0) {
                                        ((Button) lbutton).setSelection(false);
                                    }
                                }
                            }
                        }

                    }
                });
                button.addSelectionListener(encoder);
                if (encoder instanceof FileAware) {
                    Button fileButton = new Button(composite, SWT.ARROW);
                    fileButton.setText("...");
                    fileButton.addSelectionListener(new AbstractSelectionListener() {

                        public void widgetSelected(SelectionEvent e) {
                            FileDialog fileDialog = new FileDialog(shell);
                            String file = fileDialog.open();
                            ((FileAware) encoder).setFileName(file);
                            button.setText(encoder.getName());
                            leftComposite.setSize(leftComposite.computeSize(
                                SWT.DEFAULT, SWT.DEFAULT));
                        }
                    });
                }
            } catch (Exception e) {
                Utils.showException(shell, e);
            }
        }
        leftComposite.setSize(leftComposite.computeSize(SWT.DEFAULT,
            SWT.DEFAULT));
    }

    private void createRightPart(Composite parent) {
        Composite rightComposite = new Composite(parent, SWT.BORDER);
        rightComposite.setLayout(new GridLayout());
        targetText = createTextEditor(rightComposite, SWT.MULTI | SWT.READ_ONLY
            | SWT.BORDER);
    }

    private Text createTextEditor(Composite parent, int style) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Text text = new Text(composite, style | SWT.WRAP);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        final Button showBytesButton = new Button(composite, SWT.CHECK);
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
