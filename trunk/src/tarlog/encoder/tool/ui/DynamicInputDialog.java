package tarlog.encoder.tool.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
    private List<FieldControl> fieldWrappers;

    public DynamicInputDialog(Shell parentShell, String title, Object object,
        List<FieldWrapper> fields) {
        super(parentShell);
        this.object = object;
        this.fields = fields;
        this.title = title;
        this.fieldWrappers = new ArrayList<FieldControl>(fields.size());
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }

    private Object getValue(Field field) {
        try {
            field.setAccessible(true);
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
        //        Composite composite = new Composite(parent, SWT.NONE);
        //        GridLayout layout = new GridLayout();
        //        composite.setLayout(layout);
        //        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite composite = (Composite) super.createDialogArea(parent);

        for (final FieldWrapper fieldWrapper : fields) {

            String fieldName = fieldWrapper.inputField.name().equals("") ? fieldWrapper.field.getName()
                : fieldWrapper.inputField.name();
            // create label
            Label label = new Label(composite, SWT.WRAP);
            label.setText(fieldName);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());

            // create input field
            Class<?> fieldType = fieldWrapper.field.getType();
            Control control = null;
            if (fieldType.equals(String.class)) {
                final Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
                String value = (String) getValue(fieldWrapper.field);
                if (value != null) {
                    text.setText(value);
                }
                fieldWrappers.add(new FieldControl() {

                    public Field getField() {
                        return fieldWrapper.field;
                    }

                    public Object getValue() {
                        return text.getText();
                    }
                });
                control = text;
            } else if (fieldType.equals(Boolean.class)
                || fieldType.equals(boolean.class)) {
                final Button button = new Button(composite, SWT.CHECK);
                Boolean value = (Boolean) getValue(fieldWrapper.field);
                if (value != null) {
                    button.setSelection(value.booleanValue());
                }
                fieldWrappers.add(new FieldControl() {

                    public Field getField() {
                        return fieldWrapper.field;
                    }

                    public Object getValue() {
                        return button.getSelection();
                    }
                });
                control = button;
            } else if (fieldType.isEnum()) {
                final Combo combo = new Combo(composite, SWT.DROP_DOWN
                    | SWT.READ_ONLY);
                String value = (String) getValue(fieldWrapper.field);
                if (value != null) {
                    combo.setText(value);
                }
                Enum<?>[] enumConstants = (Enum[]) fieldType.getEnumConstants();
                for (Enum<?> enumConstant : enumConstants) {
                    combo.add(enumConstant.name());
                }
                fieldWrappers.add(new FieldControl() {

                    public Field getField() {
                        return fieldWrapper.field;
                    }

                    public Object getValue() {
                        return combo.getText();
                    }
                });
                control = combo;
            } else {
                throw new RuntimeException("Unsupported type : "
                    + fieldType.getName());
            }

            control.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));

        }
        applyDialogFont(composite);
        return composite;
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            for (FieldControl fieldWrapper : fieldWrappers) {
                Object value = fieldWrapper.getValue();
                try {
                    fieldWrapper.getField().setAccessible(true);
                    fieldWrapper.getField().set(object, value);
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
