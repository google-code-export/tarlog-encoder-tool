package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;

public class CreateSpinner extends CreateField {

    public CreateSpinner(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    Control createDialog(Font font, Composite parent,
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

}
