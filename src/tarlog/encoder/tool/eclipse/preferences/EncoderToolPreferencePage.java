package tarlog.encoder.tool.eclipse.preferences;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.eclipse.Activator;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncodersGroup;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class EncoderToolPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage {

    public static final Pattern WORD_PATTERN = Pattern.compile("\\w.*\\w");

    private PropertiesStore     store;
    private TreeViewer          treeViewer;

    private Button              addEncoderButton;
    private Button              editButton;
    private Button              upButton;
    private Button              downButton;
    private Button              removeButton;

    private Button              addGroupButton;

    private Composite           contentsComposite;

    public EncoderToolPreferencePage() {
        this(Activator.getDefault().getPreferenceStore());
    }

    public EncoderToolPreferencePage(String string) throws IOException {
        this(new PreferenceStore(string));
    }

    private EncoderToolPreferencePage(IPreferenceStore preferencePage) {
        setPreferenceStore(preferencePage);
        //        setDescription("A demonstration of a preference page implementation");
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public boolean performOk() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        store.store(preferenceStore);
        if (preferenceStore instanceof PreferenceStore) {
            try {
                ((PreferenceStore) preferenceStore).save();
            } catch (IOException e) {
                Utils.showException(getShell(), e);
                return false;
            }
        }
        return super.performOk();
    }

    @Override
    protected Control createContents(Composite parent) {
        initialize();

        contentsComposite = new Composite(parent, SWT.NONE);
        contentsComposite.setLayout(new GridLayout(2, false));
        contentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
            true));
        fillContents();
        return contentsComposite;
    }

    private void fillContents() {
        createTree(contentsComposite);
        createButtons(contentsComposite);
    }

    private void reloadContents() {
        for (Control control : contentsComposite.getChildren()) {
            if (!control.isDisposed()) {
                control.dispose();
            }
        }
        fillContents();
        contentsComposite.layout();
    }

    private void createButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));

        addGroupButton = createAddGroupButton(composite);
        addEncoderButton = createAddEncoderButton(composite);
        editButton = createEditButton(composite);
        removeButton = createRemoveButton(composite);
        upButton = createUpButton(composite);
        downButton = createDownButton(composite);
    }

    private Button createAddGroupButton(Composite composite) {
        Button addGroupButton = new Button(composite, SWT.PUSH);
        addGroupButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
            false));
        addGroupButton.setText("Add Group");

        addGroupButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                editGroupName("");
            }
        });
        return addGroupButton;
    }

    private Button createEditButton(Composite composite) {
        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Edit");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                TreeSelection selection = (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    String name = editGroupName(group.getGroupName());
                    if (name != null) {
                        group.setGroupName(name);
                        treeViewer.refresh();
                    }
                } else if (firstElement instanceof EncoderDef) {
                    DynamicInputDialog dynamicInputDialog = new DynamicInputDialog(
                        getShell(), "Edit Encoder", firstElement);
                    int rc = dynamicInputDialog.open();
                    if (rc == Dialog.OK) {
                        treeViewer.refresh();
                    }
                }
            }
        });
        return button;
    }

    private Button createAddEncoderButton(Composite composite) {
        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Add Encoder");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                Object firstElement = ((TreeSelection) treeViewer.getSelection()).getFirstElement();
                EncodersGroup encodersGroup = (EncodersGroup) firstElement;

                EncoderDef encoderDef = new EncoderDef();
                DynamicInputDialog dynamicInputDialog = new DynamicInputDialog(
                    getShell(), "Add Encoder", encoderDef);
                int rc = dynamicInputDialog.open();

                if (rc == Dialog.OK) {
                    encodersGroup.getList().add(encoderDef);
                    treeViewer.refresh();
                }

            }
        });
        return button;
    }

    private Button createUpButton(Composite composite) {
        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Up");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                TreeSelection selection = (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();

                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    store.moveUp(group);
                } else if (firstElement instanceof EncoderDef) {
                    EncodersGroup group = (EncodersGroup) selection.getPaths()[0].getFirstSegment();
                    EncoderDef encoderDef = (EncoderDef) firstElement;
                    group.moveUp(encoderDef);
                }
                treeViewer.refresh();
                treeViewer.setSelection(treeViewer.getSelection());

            }
        });
        return button;
    }

    private Button createDownButton(Composite composite) {
        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Down");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                TreeSelection selection = (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();

                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    store.moveDown(group);
                } else if (firstElement instanceof EncoderDef) {
                    EncodersGroup group = (EncodersGroup) selection.getPaths()[0].getFirstSegment();
                    EncoderDef encoderDef = (EncoderDef) firstElement;
                    group.moveDown(encoderDef);
                }
                treeViewer.refresh();
                treeViewer.setSelection(treeViewer.getSelection());
            }
        });
        return button;
    }

    private Button createRemoveButton(Composite composite) {
        Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Remove");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                TreeSelection selection = (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();

                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    store.remove(group);
                } else if (firstElement instanceof EncoderDef) {
                    EncodersGroup group = (EncodersGroup) selection.getPaths()[0].getFirstSegment();
                    EncoderDef encoderDef = (EncoderDef) firstElement;
                    group.remove(encoderDef);
                }
                treeViewer.refresh();
                treeViewer.setSelection(treeViewer.getSelection());
            }
        });
        return button;
    }

    private void createTree(Composite parent) {
        treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
            | SWT.V_SCROLL | SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tree.setLayout(new FillLayout());
        tree.setVisible(true);
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.setAutoExpandLevel(2);
        treeViewer.setLabelProvider(new LabelProvider());
        treeViewer.setInput(store);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                TreeSelection selection = (TreeSelection) event.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    addGroupButton.setEnabled(true);
                    addEncoderButton.setEnabled(true);
                    editButton.setEnabled(true);
                    removeButton.setEnabled(true);
                    downButton.setEnabled(store.canMoveDown(group));
                    upButton.setEnabled(store.canMoveUp(group));
                } else if (firstElement instanceof EncoderDef) {
                    EncodersGroup group = (EncodersGroup) selection.getPaths()[0].getFirstSegment();
                    EncoderDef encoderDef = (EncoderDef) firstElement;
                    addGroupButton.setEnabled(false);
                    addEncoderButton.setEnabled(false);
                    removeButton.setEnabled(true);
                    editButton.setEnabled(true);
                    downButton.setEnabled(group.canMoveDown(encoderDef));
                    upButton.setEnabled(group.canMoveUp(encoderDef));
                } else {
                    addGroupButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    addEncoderButton.setEnabled(false);
                    editButton.setEnabled(false);
                    downButton.setEnabled(false);
                    upButton.setEnabled(false);
                }
            }
        });
    }

    private void initialize() {
        try {
            IPreferenceStore preferenceStore = getPreferenceStore();
            if (preferenceStore instanceof PreferenceStore) {
                // stand-alone
                ((PreferenceStore) preferenceStore).load();
                PreferenceInitializer.loadDefault(preferenceStore);
            }
            store = new PropertiesStore(preferenceStore, false);
        } catch (IOException e) {
            Utils.showException(getShell(), e);
        }
    }

    private String editGroupName(String string) {
        InputDialog inputDialog = new InputDialog(getShell(),
            "Enter group name", "Enter group name", string,
            new IInputValidator() {

                public String isValid(String newText) {
                    if (newText.trim().equals("")) {
                        return "Group name cannot be empty";
                    }
                    Matcher matcher = WORD_PATTERN.matcher(newText);
                    if (!matcher.matches()) {
                        return "Group name must be a word";
                    }
                    if (store.getGroup(newText) != null) {
                        return "Group name must be unique";
                    }
                    return null;
                }
            });
        int rc = inputDialog.open();
        if (rc == Dialog.OK) {
            if ("".equals(string)) {
                // new group
                store.newGroup(inputDialog.getValue());
                treeViewer.refresh();
                return inputDialog.getValue();
            } else {
                return inputDialog.getValue();
            }
        }
        return null;
    }

    @Override
    protected void performDefaults() {
        try {
            store = new PropertiesStore(getPreferenceStore(), true);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        super.performDefaults();
        reloadContents();
    }
}