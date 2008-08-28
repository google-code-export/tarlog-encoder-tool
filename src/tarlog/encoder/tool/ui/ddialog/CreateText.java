package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.api.fields.TextField;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;

public class CreateText extends CreateField {

    public CreateText(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    public Text createField(Font font, Composite parent,
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
            layoutData.heightHint = inputDialog.convertHorizontalDLUsToPixels(30);
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

}
