package tarlog.encoder.tool.ui;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.api.fields.InputTextField;
import tarlog.encoder.tool.api.fields.InputFileField.FileFieldType;

public class DynamicInputDialog extends Dialog {

    private Object             object;
    private List<FieldWrapper> fields;
    private String             title;
    private List<FieldControl> fieldControls;

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
                InputTextField textField = fieldWrapper.field.getAnnotation(InputTextField.class);
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
        InputTextField textField = fieldWrapper.field.getAnnotation(InputTextField.class);
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
        InputTextField textField = fieldWrapper.field.getAnnotation(InputTextField.class);
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
