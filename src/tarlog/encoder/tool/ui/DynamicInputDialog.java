package tarlog.encoder.tool.ui;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.AbstractEncoder.FieldWrapper;

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
                TextField textField = fieldWrapper.field.getAnnotation(TextField.class);
                if (textField != null && textField.values().length > 0) {
                    control = createComboField(parent.getFont(), composite, fieldWrapper,
                        fieldName, fieldType);
                } else {
                    control = createStringField(parent.getFont(), composite,
                        fieldWrapper, fieldName);
                }
            } else if (fieldType.equals(Boolean.class)
                || fieldType.equals(boolean.class)) {
                control = createCheckButtonField(composite, fieldWrapper, fieldName);
            } else if (fieldType.isEnum()) {
                control = createComboFieldOfEnum(parent.getFont(), composite,
                    fieldWrapper, fieldName, fieldType);

            } else {
                throw new RuntimeException("Unsupported type : "
                    + fieldType.getName());
            }
            
            control.setEnabled(fieldWrapper.inputField.enabled());
        }
        applyDialogFont(composite);
        return composite;
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
