package tarlog.encoder.tool.eclipse.preferences;

import java.io.IOException;
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
import tarlog.encoder.tool.eclipse.preferences.EncodersStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.EncodersStore.EncodersGroup;
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

    public static final Pattern WORD_PATTERN = Pattern.compile("\\w*");

    private EncodersStore       encodersStore;
    private TreeViewer          treeViewer;

    private Button              addEncoderButton;

    private Button              editButton;

    public EncoderToolPreferencePage() {
        this(Activator.getDefault().getPreferenceStore());
    }

    public EncoderToolPreferencePage(String string) throws IOException {
        this(new PreferenceStore(string));
    }

    private EncoderToolPreferencePage(IPreferenceStore preferencePage) {
        setPreferenceStore(preferencePage);
        setDescription("A demonstration of a preference page implementation");
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public boolean performOk() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        encodersStore.store(preferenceStore);
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

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createTree(composite);
        createButtons(composite);
        return composite;
    }

    private void createButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));

        createAddGroupButton(composite);
        addEncoderButton = createAddEncoderButton(composite);
        editButton = createEditButton(composite);
    }

    private void createAddGroupButton(Composite composite) {
        Button addGroupButton = new Button(composite, SWT.PUSH);
        addGroupButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
            false));
        addGroupButton.setText("Add Group");

        addGroupButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                editGroupName("");
            }
        });
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
                    String name = editGroupName(group.groupName);
                    if (name != null) {
                        group.groupName = name;
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
                    encodersGroup.list.add(encoderDef);
                    treeViewer.refresh();
                }

            }
        });
        return button;
    }

    private void createTree(Composite parent) {
        treeViewer = new TreeViewer(parent);
        Tree tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tree.setLayout(new FillLayout());
        tree.setVisible(true);
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.setLabelProvider(new LabelProvider());
        treeViewer.setInput(encodersStore);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                Object firstElement = ((TreeSelection) event.getSelection()).getFirstElement();
                if (firstElement instanceof EncodersGroup) {
                    addEncoderButton.setEnabled(true);
                    editButton.setEnabled(true);
                } else if (firstElement instanceof EncoderDef) {
                    addEncoderButton.setEnabled(false);
                    editButton.setEnabled(true);
                } else {
                    addEncoderButton.setEnabled(false);
                    editButton.setEnabled(false);
                }
            }
        });
    }

    private void initialize() {
        try {
            IPreferenceStore preferenceStore = getPreferenceStore();
            if (preferenceStore instanceof PreferenceStore) {
                ((PreferenceStore) preferenceStore).load();
            }
            encodersStore = new EncodersStore(preferenceStore);
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
                    if (encodersStore.getGroup(newText) != null) {
                        return "Group name must be unique";
                    }
                    return null;
                }
            });
        int rc = inputDialog.open();
        if (rc == Dialog.OK) {
            if ("".equals(string)) {
                // new group
                encodersStore.newGroup(inputDialog.getValue());
                treeViewer.refresh();
                return inputDialog.getValue();
            } else {
                return inputDialog.getValue();
            }
        }
        return null;
    }

    
}