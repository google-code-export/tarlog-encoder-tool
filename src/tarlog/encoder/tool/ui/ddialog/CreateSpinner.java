package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreateSpinner extends CreateField {

    public CreateSpinner(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    Control createDialog(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        createLabel(font, parent, fieldName);
        final Spinner spinner = new Spinner(parent, SWT.BORDER
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        Object value = fieldWrapper.initialValue;
        if (value != null) {
            spinner.setSelection(((Integer) value).intValue());
        }

        if (inputDialog.toValidateInput()) {
            spinner.addModifyListener(new ModifyListener() {

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
                return spinner.getSelection();
            }
        });
        return spinner;
    }

}
