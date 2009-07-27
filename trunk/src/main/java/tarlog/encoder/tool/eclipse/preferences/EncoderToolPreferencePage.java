/*******************************************************************************
 *     Copyright 2009 Michael Elman (http://tarlogonjava.blogspot.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *     
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
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
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import tarlog.encoder.tool.eclipse.Activator;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncodersGroup;
import tarlog.ui.swt.ddialog.api.fields.InputTextField;
import tarlog.ui.swt.ddialog.impl.DynamicInputDialog;
import tarlog.ui.swt.ddialog.utils.AbstractSelectionListener;
import tarlog.ui.swt.ddialog.utils.Utils;

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

    public static final Pattern WORD_PATTERN = Pattern.compile(InputTextField.WORD_PATTERN);

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

            @Override
            public void widgetSelected(SelectionEvent e) {
                edit();
            }
        });
        return button;
    }

    private void edit() {
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
            | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        TreeColumn mainColumn = new TreeColumn(tree, SWT.NONE);
        mainColumn.setWidth(200);
        mainColumn.setText("Name");
        TreeColumn enabledColumn = new TreeColumn(tree, SWT.NONE);
        enabledColumn.setWidth(200);
        enabledColumn.setText("Enabled");
        tree.setMenu(createTreeMenu(tree));
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tree.setLayout(new FillLayout());

        tree.setVisible(true);
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                edit();
            }
        });

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

    private Menu createTreeMenu(Tree tree) {
        Menu menu = new Menu(tree);

        final MenuItem enabledMenuItem = new MenuItem(menu, SWT.CHECK);
        enabledMenuItem.setText("Enabled");
        enabledMenuItem.addSelectionListener(new AbstractSelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean enabledMenuSelection = ((MenuItem) e.widget).getSelection();
                TreeSelection selection = (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    group.setEnabled(enabledMenuSelection);
                } else if (firstElement instanceof EncoderDef) {
                    EncoderDef def = (EncoderDef) firstElement;
                    def.setEnabled(enabledMenuSelection);
                }
                treeViewer.refresh();
            }
        });

        menu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {
                TreeSelection selection = (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();

                if (firstElement instanceof EncodersGroup) {
                    EncodersGroup group = (EncodersGroup) firstElement;
                    enabledMenuItem.setEnabled(true);
                    enabledMenuItem.setSelection(group.isEnabled());
                } else if (firstElement instanceof EncoderDef) {
                    EncoderDef def = (EncoderDef) firstElement;
                    enabledMenuItem.setEnabled(true);
                    enabledMenuItem.setSelection(def.isEnabled());
                } else {
                    enabledMenuItem.setEnabled(false);
                }
                treeViewer.refresh();
                treeViewer.setSelection(treeViewer.getSelection());
            }
        });
        return menu;
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