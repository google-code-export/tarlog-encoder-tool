package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreateCheckButton extends CreateField {

    public CreateCheckButton(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    Control createDialog(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        final Button button = new Button(parent, SWT.CHECK
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        GridData data = new GridData(GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
            | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = inputDialog.convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        data.horizontalSpan = 2;
        button.setLayoutData(data);

        button.setText(fieldName);
        Boolean value = (Boolean) fieldWrapper.initialValue;
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

}
