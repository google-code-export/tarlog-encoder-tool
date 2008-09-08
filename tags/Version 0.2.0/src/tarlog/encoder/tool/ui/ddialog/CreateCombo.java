package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import tarlog.encoder.tool.api.fields.InputTextField;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreateCombo extends CreateField {

    public CreateCombo(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    public Combo createComboFieldOfEnum(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName,
        final Class<?> fieldType) {
        createLabel(font, parent, fieldName);
        final Combo combo = new Combo(parent, SWT.DROP_DOWN
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        Enum<?>[] enumConstants = (Enum[]) fieldType.getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            combo.add(enumConstant.name());
        }
        Object value = fieldWrapper.initialValue;
        if (value != null) {
            combo.setText(((Enum<?>) value).name());
        }
        if (inputDialog.validator != null) {
            combo.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    inputDialog.setFields();
                    inputDialog.validateInput();
                }
            });
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

    public Combo createComboField(Font font, Composite parent,
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
        Object value = fieldWrapper.initialValue;
        if (value != null) {
            combo.setText((String) value);
        }
        if (inputDialog.validator != null) {
            combo.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    inputDialog.setFields();
                    inputDialog.validateInput();
                }
            });
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

}
