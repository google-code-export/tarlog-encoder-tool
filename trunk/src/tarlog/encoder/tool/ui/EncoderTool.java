package tarlog.encoder.tool.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.Initiable;
import tarlog.encoder.tool.eclipse.Activator;
import tarlog.encoder.tool.eclipse.preferences.EncoderToolPreferencePage;
import tarlog.encoder.tool.eclipse.preferences.EncodersStore;
import tarlog.encoder.tool.eclipse.preferences.EncodersStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.EncodersStore.EncodersGroup;

/**
 *
 */
public class EncoderTool extends ApplicationWindow {

    //    private static final String ENCODERS_FILE      = "encoders.properties";
    private static final String ENCODER_PROPERTIES = "encoder.properties";
    //    private static final String ENCODERS_PROPERTY  = "encoders";

    private static final String VERSION            = "0.2.0";

    //    private Properties          properties;
    private Text                targetText;
    private Text                sourceText;
    private Composite           leftComposite;
    private Shell               shell;
    private boolean             standalone         = false;
    private EncodersStore       encodersStore;

    public EncoderTool() {
        super(null);
    }

    public void init() throws IOException {

        //        properties = new Properties();
        //
        //        String propertiesFile = System.getProperty(ENCODERS_PROPERTY,
        //            ENCODERS_FILE);
        //
        //        properties.load(getClass().getClassLoader().getResourceAsStream(
        //            propertiesFile));

        setBlockOnOpen(true);
        if (standalone) {
            addMenuBar();
        }
        initEncodersStore();
        open();
    }

    private void initEncodersStore() throws IOException {
        IPreferenceStore preferenceStore;
        if (standalone) {
            PreferenceStore store = new PreferenceStore(ENCODER_PROPERTIES);
            store.load();
            preferenceStore = store;
        } else {
            preferenceStore = Activator.getDefault().getPreferenceStore();
        }
        encodersStore = new EncodersStore(preferenceStore, false);

    }

    private Image getImage() {
        try {
            InputStream img = getClass().getClassLoader().getResourceAsStream(
                "icons/encoders.jpg");
            if (img == null) {
                img = new FileInputStream("icons/encoders.jpg");
            }
            ImageLoader imageLoader = new ImageLoader();
            ImageData[] load = imageLoader.load(img);
            return new Image(shell.getDisplay(), load[0]);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    protected MenuManager createMenuManager() {
        MenuManager menuManager = super.createMenuManager();

        // create menu only when stand alone
        MenuManager fileMenuManager = new MenuManager("&File");
        fileMenuManager.add(new OpenPreferencesAction());
        menuManager.add(fileMenuManager);

        //        SaveAction saveAction = new SaveAction();
        //        saveAction.setAccelerator(SWT.CTRL | 'S');
        //        fileMenuManager.add(saveAction);
        //        LoadAction loadAction = new LoadAction();
        //        loadAction.setAccelerator(SWT.CTRL | 'O');
        //        fileMenuManager.add(loadAction);
        //        menuManager.add(fileMenuManager);

        return menuManager;
    }

    private class OpenPreferencesAction extends Action {

        public OpenPreferencesAction() {
            super("Preferences");
        }

        @Override
        public void run() {
            try {
                EncoderToolPreferencePage page = new EncoderToolPreferencePage(
                    ENCODER_PROPERTIES);
                page.setTitle("Encoder Tool");
                PreferenceManager mgr = new PreferenceManager();
                IPreferenceNode node = new PreferenceNode("1", page);
                mgr.addToRoot(node);
                PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
                dialog.create();
                dialog.setMessage(page.getTitle());
                int rc = dialog.open();
                if (rc == Dialog.OK) {
                    reload();
                }
            } catch (IOException e) {
                Utils.showException(shell, e);
            }
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        shell = getShell();
        shell.setText("Encoder Tool " + VERSION);
        shell.setSize(1000, 700);
        Image image = getImage();
        if (image != null) {
            shell.setImage(image);
        }
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
        exchange.setToolTipText("Swap input");
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

    private void reload() throws IOException {
        for (Control control : leftComposite.getChildren()) {
            if (!control.isDisposed()) {
                control.dispose();
            }
        }
        initEncodersStore();
        load();
        leftComposite.layout(true, true);
    }

    private void load() {
        final List<Button> radioButtons = new ArrayList<Button>();

        for (EncodersGroup encodersGroup : encodersStore.getStore()) {
            Group group = new Group(leftComposite, SWT.NONE);
            group.setText(encodersGroup.getGroupName());
            group.setLayout(new GridLayout());
            group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            for (EncoderDef encoderDef : encodersGroup.getList()) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<AbstractEncoder> clazz = (Class<AbstractEncoder>) encoderDef.getEncoderClass();
                    final AbstractEncoder encoder = clazz.newInstance();
                    encoder.setSource(sourceText);
                    encoder.setTarget(targetText);
                    encoder.setName(encoderDef.getName());
                    encoder.setShell(shell);
                    final Composite composite = new Composite(group, SWT.NULL);
                    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                        true, true));
                    RowLayout layout = new RowLayout();
                    layout.type = SWT.HORIZONTAL;
                    composite.setLayout(layout);
                    final Button button = new Button(composite, SWT.RADIO);
                    radioButtons.add(button);
                    button.setText(encoder.getName());
                    button.addSelectionListener(new AbstractSelectionListener() {

                        public void widgetSelected(SelectionEvent e) {
                            for (Button radioButton : radioButtons) {
                                if (e.getSource() != radioButton) {
                                    radioButton.setSelection(false);
                                }
                            }
                        }
                    });
                    button.addSelectionListener(encoder);
                    if (encoder instanceof Initiable) {
                        Button fileButton = new Button(composite, SWT.ARROW);
                        fileButton.addSelectionListener(new AbstractSelectionListener() {

                            public void widgetSelected(SelectionEvent e) {
                                ((Initiable) encoder).init();
                                leftComposite.setSize(leftComposite.computeSize(
                                    SWT.DEFAULT, SWT.DEFAULT));
                            }
                        });
                    }
                } catch (Exception e) {
                    Utils.showException(shell, e);
                }
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
        final Text text = new Text(composite, style | SWT.WRAP | SWT.V_SCROLL);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite bottomComposite = new Composite(composite, SWT.NONE);
        bottomComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
        final Button showBytesButton = new Button(bottomComposite, SWT.CHECK);
        showBytesButton.setData(text);
        text.setData(showBytesButton);
        showBytesButton.setText("Show bytes");
        showBytesButton.addSelectionListener(new ShowBytesListener());
        Link link = new Link(bottomComposite, SWT.NONE);
        link.setText("<a>Clean</a>");
        link.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                text.setText("");
            }
        });

        return text;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        EncoderTool encoderTool = new EncoderTool();
        encoderTool.standalone = true;
        encoderTool.init();
    }
}
