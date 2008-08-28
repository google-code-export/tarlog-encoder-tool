package tarlog.encoder.tool.ui;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.api.fields.TextField;
import tarlog.encoder.tool.api.fields.InputFileField.FileFieldType;

public class DynamicInputDialog extends Dialog {

    private static final String VALUE = "Value";
    private static final String NAME  = "Name";
    private Object              object;
    private List<FieldWrapper>  fields;
    private String              title;
    private List<FieldControl>  fieldControls;

    public DynamicInputDialog(Shell parentShell, String title, Object object,
        List<FieldWrapper> fields) {
        super(parentShell);
        this.object = object;
        this.fields = fields;
        this.title = title;
        this.fieldControls = new ArrayList<FieldControl>(fields.size());
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }

    private Object getValue(Field field) {
        try {
            AccessibleObject.setAccessible(new Field[] { field }, true);
            //            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalArgumentException e) {
            Utils.showException(getShell(), e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        composite.setLayoutData(data);
        //        Composite composite = (Composite) super.createDialogArea(parent);

        for (final FieldWrapper fieldWrapper : fields) {

            String fieldName = fieldWrapper.inputField.name().equals("") ? fieldWrapper.field.getName()
                : fieldWrapper.inputField.name();

            // create input field
            final Class<?> fieldType = fieldWrapper.field.getType();
            Control control;
            if (fieldType.equals(String.class)) {
                TextField textField = fieldWrapper.field.getAnnotation(TextField.class);
                if (textField != null && textField.values().length > 0) {
                    control = createComboField(parent.getFont(), composite,
                        fieldWrapper, fieldName, fieldType);
                } else {
                    control = createStringField(parent.getFont(), composite,
                        fieldWrapper, fieldName);
                }
            } else if (fieldType.equals(Boolean.class)
                || fieldType.equals(boolean.class)) {
                control = createCheckButtonField(composite, fieldWrapper,
                    fieldName);
            } else if (fieldType.isEnum()) {
                control = createComboFieldOfEnum(parent.getFont(), composite,
                    fieldWrapper, fieldName, fieldType);
            } else if (fieldType.equals(Integer.class)) {
                control = createSpinner(parent.getFont(), composite,
                    fieldWrapper, fieldName);
            } else if (fieldType.equals(File.class)) {
                control = createFileDialog(parent.getFont(), composite,
                    fieldWrapper, fieldName);
            } else if (fieldType.equals(Properties.class)) {
                control = createProperties(parent.getFont(), composite,
                    fieldWrapper, fieldName);
            } else {
                throw new RuntimeException("Unsupported type : "
                    + fieldType.getName());
            }

            control.setEnabled(fieldWrapper.inputField.enabled());
        }
        applyDialogFont(composite);
        return composite;
    }

    private Control createFileDialog(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        createLabel(font, parent, fieldName);

        Composite composite = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        composite.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        composite.setLayout(layout);
        final Text text = new Text(composite, SWT.BORDER
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        text.setLayoutData(layoutData);
        Object value = getValue(fieldWrapper.field);
        if (value != null) {
            String fileName = ((File) value).getAbsolutePath();
            text.setText(fileName);
        }

        final InputFileField fileFieldAnnotation = fieldWrapper.field.getAnnotation(InputFileField.class);

        final Button button = new Button(composite, SWT.PUSH);
        button.setEnabled(!fieldWrapper.inputField.readonly());
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        button.setText("...");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                FileFieldType fileFieldType = FileFieldType.file;
                String[] filterExtensions = null;
                String[] filterNames = null;
                String filterPath = null;
                if (fileFieldAnnotation != null) {
                    fileFieldType = fileFieldAnnotation.fileFieldType();
                    filterExtensions = fileFieldAnnotation.filterExtensions().length > 0 ? fileFieldAnnotation.filterExtensions()
                        : null;
                    filterNames = fileFieldAnnotation.filterNames().length > 0 ? fileFieldAnnotation.filterNames()
                        : null;
                    filterPath = fileFieldAnnotation.filterPath().equals("") ? null
                        : fileFieldAnnotation.filterPath();
                }
                String file = null;
                if (fileFieldType == FileFieldType.file) {
                    FileDialog fileDialog = new FileDialog(getShell());
                    fileDialog.setFileName(text.getText());
                    fileDialog.setFilterExtensions(filterExtensions);
                    fileDialog.setFilterNames(filterNames);
                    fileDialog.setFilterPath(filterPath);
                    file = fileDialog.open();
                } else {
                    DirectoryDialog directoryDialog = new DirectoryDialog(
                        getShell());
                    directoryDialog.setText(text.getText());
                    directoryDialog.setFilterPath(filterPath);
                    file = directoryDialog.open();
                }
                if (file != null) {
                    text.setText(file);
                }
            }
        });

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                if (!text.getText().equals("")) {
                    return new File(text.getText());
                }
                return null;
            }
        });
        return composite;
    }

    private Control createProperties(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {

        Group group = new Group(parent, SWT.NONE);
        group.setText(fieldName);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        group.setLayout(new GridLayout(2, false));

        final Table table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION
            | SWT.MULTI);
        final String[] columnNames = { NAME, VALUE };

        TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText(columnNames[0]);
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText(columnNames[1]);
        tableColumn.setWidth(150);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = convertVerticalDLUsToPixels(60);
        table.setLayoutData(layoutData);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableViewer tableViewer = new TableViewer(table);

        tableViewer.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
                // do nothing
            }

            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
                // do nothing
            }

            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof Properties) {
                    return ((Properties) inputElement).entrySet().toArray();
                }
                return new Object[0];
            }
        });

        tableViewer.setLabelProvider(new ITableLabelProvider() {

            public Image getColumnImage(Object element, int columnIndex) {
                return null;
            }

            public String getColumnText(Object element, int columnIndex) {
                @SuppressWarnings("unchecked")
                Entry<String, String> entry = (Entry<String, String>) element;
                return columnIndex == 0 ? entry.getKey() : entry.getValue();
            }

            public void addListener(ILabelProviderListener listener) {

            }

            public void dispose() {

            }

            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            public void removeListener(ILabelProviderListener listener) {

            }

        });

        Object value = getValue(fieldWrapper.field);
        final Properties tableInput = value == null ? new Properties()
            : (Properties) value;

        table.setData(tableInput);
        tableViewer.setInput(tableInput);
        tableViewer.setColumnProperties(columnNames);

        tableViewer.setCellEditors(new TextCellEditor[] {
            new TextCellEditor(table), new TextCellEditor(table) });

        tableViewer.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                return true;
            }

            public Object getValue(Object element, String property) {
                @SuppressWarnings("unchecked")
                Entry<String, String> entry = (Entry<String, String>) element;
                if (property.equals(NAME)) {
                    return entry.getKey();
                }
                return entry.getValue();
            }

            public void modify(Object element, String property, Object value) {
                TableItem item = (TableItem) element;
                @SuppressWarnings("unchecked")
                Entry<String, String> entry = (Entry<String, String>) item.getData();
                if (property.equals(NAME)) {
                    Object keyValue = tableInput.remove(entry.getKey());
                    tableInput.setProperty((String) value, (String) keyValue);
                } else {
                    entry.setValue((String) value);
                }
                tableViewer.refresh();
            }
        });

        Composite buttonsComposite = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        buttonsComposite.setLayout(gridLayout);
        buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.UP, false,
            false));
        Button addButton = new Button(buttonsComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                tableInput.setProperty("", "");
                tableViewer.refresh();
            }
        });

        //        addButton.setSize(convertHorizontalDLUsToPixels(6));
        Button removeButton = new Button(buttonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        // make the buttons equal size
        addButton.setSize(removeButton.getSize());
        removeButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                System.out.println(".widgetSelected()");
            }
        });

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return table.getData();
            }
        });
        return table;
    }

    private Control createSpinner(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        createLabel(font, parent, fieldName);
        final Spinner spinner = new Spinner(parent, SWT.BORDER
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        Object value = getValue(fieldWrapper.field);
        if (value != null) {
            spinner.setSelection(((Integer) value).intValue());
        }

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return spinner.getSelection();
            }
        });
        return spinner;
    }

    private Combo createComboFieldOfEnum(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName,
        final Class<?> fieldType) {
        createLabel(font, parent, fieldName);
        final Combo combo = new Combo(parent, SWT.DROP_DOWN
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        Enum<?>[] enumConstants = (Enum[]) fieldType.getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            combo.add(enumConstant.name());
        }
        Object value = getValue(fieldWrapper.field);
        if (value != null) {
            combo.setText(((Enum<?>) value).name());
        }
        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            @SuppressWarnings("unchecked")
            public Object getValue() {
                String text = combo.getText();
                return "".equals(text) ? null : Enum.valueOf((Class) fieldType,
                    text);
            }
        });
        return combo;
    }

    private Combo createComboField(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName,
        final Class<?> fieldType) {
        createLabel(font, parent, fieldName);
        final Combo combo = new Combo(parent, SWT.DROP_DOWN
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        TextField textField = fieldWrapper.field.getAnnotation(TextField.class);
        if (textField != null) {
            for (String val : textField.values()) {
                combo.add(val);
            }
        }
        Object value = getValue(fieldWrapper.field);
        if (value != null) {
            combo.setText((String) value);
        }
        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return combo.getText();
            }
        });
        return combo;
    }

    private Text createStringField(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        createLabel(font, parent, fieldName);
        TextField textField = fieldWrapper.field.getAnnotation(TextField.class);
        boolean isMultiline = textField != null ? textField.multiline() : false;
        boolean isPassword = textField != null ? textField.password() : false;
        final Text text = new Text(parent, (isMultiline ? SWT.MULTI
            : SWT.SINGLE)
            | SWT.BORDER
            | SWT.V_SCROLL
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        if (isPassword) {
            text.setEchoChar('*');
        }
        GridData layoutData = new GridData(SWT.FILL, (isMultiline ? SWT.FILL
            : SWT.CENTER), true, (isMultiline ? true : false));
        if (isMultiline) {
            layoutData.heightHint = convertHorizontalDLUsToPixels(30);
        }
        text.setLayoutData(layoutData);
        String value = (String) getValue(fieldWrapper.field);
        if (value != null) {
            text.setText(value);
        }
        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return text.getText();
            }
        });
        return text;
    }

    private Button createCheckButtonField(Composite composite,
        final FieldWrapper fieldWrapper, String fieldName) {
        final Button button = new Button(composite, SWT.CHECK
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        GridData data = new GridData(GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
            | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        data.horizontalSpan = 2;
        button.setLayoutData(data);

        button.setText(fieldName);
        Boolean value = (Boolean) getValue(fieldWrapper.field);
        if (value != null) {
            button.setSelection(value.booleanValue());
        }
        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return button.getSelection();
            }
        });
        return button;
    }

    private void createLabel(Font font, Composite composite, String fieldName) {
        if (!fieldName.trim().endsWith(":")) {
            fieldName = fieldName + ":";
        }
        Label label = new Label(composite, SWT.WRAP);
        label.setText(fieldName);
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        //                data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);
        label.setFont(font);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            for (FieldControl fieldWrapper : fieldControls) {
                Object value = fieldWrapper.getValue();
                try {
                    final Field f = fieldWrapper.getField();
                    if (!f.isAccessible()) {
                        AccessController.doPrivileged(new PrivilegedAction<Object>() {

                            public Object run() {
                                f.setAccessible(true);
                                return null;
                            }
                        });
                    }
                    //                    AccessibleObject.setAccessible(new Field[] {f}, true);
                    f.set(object, value);
                } catch (Exception e) {
                    Utils.showException(getShell(), e);
                }
            }
        }
        super.buttonPressed(buttonId);
    }

    interface FieldControl {

        Field getField();

        Object getValue();
    }

}
