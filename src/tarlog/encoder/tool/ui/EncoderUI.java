package tarlog.encoder.tool.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.Initiable;
import tarlog.encoder.tool.eclipse.Activator;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncodersGroup;
import tarlog.encoder.tool.ui.inner.HistoryManager;
import tarlog.encoder.tool.ui.inner.InputTextEditor;
import tarlog.encoder.tool.ui.inner.SwapButton;

public class EncoderUI extends SashForm {

    private Text            targetText;
    private Text            sourceText;
    private Composite       leftComposite;
    private PropertiesStore propertiesStore;
    private HistoryManager  historyManager;
    private Shell           shell;
    private boolean         standalone = false;

    public EncoderUI(Composite parent, boolean standalone) {
        super(parent, SWT.VERTICAL);
        this.shell = parent.getShell();
        this.standalone = standalone;
        createTopPart(this);
        createBottomPart(this);
        this.setWeights(new int[] { 1, 2 });

        initEncodersStore();
        load();
    }

    private void createTopPart(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        Composite leftComposite = new GridComposite(sashForm);
        InputTextEditor textEditor = new InputTextEditor(leftComposite,
            SWT.MULTI | SWT.BORDER);
        sourceText = textEditor.getText();
        Composite textEditorBottomComposite = textEditor.getBottomComposite();
        ((GridLayout) textEditorBottomComposite.getLayout()).numColumns++;
        new SwapButton(textEditorBottomComposite, this);
        Composite rightComposite = new GridComposite(sashForm);
        GridData gridData = new GridData(SWT.CENTER, SWT.FILL, false, true);
        rightComposite.setLayoutData(gridData);
        historyManager = new HistoryManager(rightComposite, this);
        sashForm.setWeights(new int[] { 5, 1 });
    }

    private void createBottomPart(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        createLeftPart(sashForm);
        createRightPart(sashForm);
        sashForm.setWeights(new int[] { 1, 3 });
    }

    private void createLeftPart(Composite parent) {
        ScrolledComposite scrolledComposite = null;
        scrolledComposite = new ScrolledComposite(parent, SWT.BORDER
            | SWT.V_SCROLL | SWT.H_SCROLL);
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
            true));
        parent = scrolledComposite;

        leftComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        leftComposite.setLayout(layout);
        leftComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        scrolledComposite.setContent(leftComposite);
    }

    private void initEncodersStore() {
        try {
            IPreferenceStore preferenceStore;
            if (standalone) {
                PreferenceStore store = new PreferenceStore(
                    EncoderTool.ENCODER_PROPERTIES);
                store.load();
                preferenceStore = store;
            } else {
                preferenceStore = Activator.getDefault().getPreferenceStore();
            }
            propertiesStore = new PropertiesStore(preferenceStore, false);
        } catch (MalformedURLException e) {
            Utils.showException(shell, e);
        } catch (IOException e) {
            Utils.showException(shell, e);
        }
    }

    void reload() throws IOException {
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

        LOOP_GROUP: for (EncodersGroup encodersGroup : propertiesStore.getStore()) {
            if (!encodersGroup.isEnabled()) {
                continue LOOP_GROUP;
            }
            Composite grouping;

            Group group = new Group(leftComposite, SWT.NONE);
            group.setText(encodersGroup.getGroupName());
            grouping = group;
            grouping.setLayout(new GridLayout());
            GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
            grouping.setLayoutData(layoutData);
            LOOP_ENCODER: for (EncoderDef encoderDef : encodersGroup.getList()) {
                if (!encoderDef.isEnabled()) {
                    continue LOOP_ENCODER;
                }
                try {
                    @SuppressWarnings("unchecked")
                    Class<AbstractEncoder> clazz = (Class<AbstractEncoder>) encoderDef.getEncoderClass();
                    final AbstractEncoder encoder = clazz.newInstance();
                    encoder.setSource(sourceText);
                    encoder.setTarget(targetText);
                    encoder.setName(encoderDef.getName());
                    encoder.setEncodingMethod(encoderDef.getEncodingMethod());
                    encoder.setShell(shell);
                    encoder.setHistoryManager(historyManager);
                    final Composite composite = new Composite(grouping,
                        SWT.NONE);
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
        InputTextEditor textEditor = new InputTextEditor(parent, SWT.NONE);
        return textEditor.getText();
    }

    public Text getTargetText() {
        return targetText;
    }

    public Text getSourceText() {
        return sourceText;
    }

}
