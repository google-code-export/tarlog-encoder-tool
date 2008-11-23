package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.api.fields.InputTextField;
import tarlog.encoder.tool.api.fields.Validator;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreateText extends CreateField {

    public CreateText(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    public Text createField(Font font, Composite parent,
        final FieldWrapper fieldWrapper, final String fieldName) {
        createLabel(font, parent, fieldName);
        final InputTextField textField = fieldWrapper.field.getAnnotation(InputTextField.class);
        boolean isMultiline = false;
        boolean isPassword = false;
        if (textField != null) {
            isMultiline = textField.multiline();
            isPassword = textField.password();

            if (textField.validateNotEmpty()) {
                inputDialog.validators.add(inputDialog.validators.isEmpty() ? 0
                    : inputDialog.validators.size() - 1, new Validator() {

                    public String isValid() {
                        Object value = fieldWrapper.getValue(object,
                            fieldWrapper.field);
                        if (value == null || ((String) value).equals("")) {
                            return fieldName + " cannot be empty.";
                        }
                        return null;
                    }
                });
            }

            if (!textField.validationPattern().equals("")) {
                inputDialog.validators.add(inputDialog.validators.isEmpty() ? 0
                    : inputDialog.validators.size() - 1, new Validator() {

                    final Pattern pattern = Pattern.compile(textField.validationPattern());

                    public String isValid() {
                        Object value = fieldWrapper.getValue(object,
                            fieldWrapper.field);
                        Matcher matcher = pattern.matcher((String) value);
                        if (!matcher.matches()) {
                            return textField.validationMessage();
                        }
                        return null;
                    }
                });
            }
        }
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
            layoutData.heightHint = inputDialog.convertHorizontalDLUsToPixels(30);
        }
        text.setLayoutData(layoutData);
        String value = (String) fieldWrapper.initialValue;
        if (value != null) {
            text.setText(value);
        }

        if (inputDialog.toValidateInput()) {
            text.addModifyListener(new ModifyListener() {

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
                return text.getText();
            }
        });
        return text;
    }

}
