package tarlog.encoder.tool.ui.ddialog;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;

public abstract class CreateField {

    protected final Shell              shell;
    protected final List<FieldControl> fieldControls;
    protected final Object             object;
    protected final DynamicInputDialog inputDialog;
    
    public CreateField(DynamicInputDialog inputDialog) {
        super();
        this.inputDialog = inputDialog;
        this.shell = inputDialog.getShell();
        this.fieldControls = inputDialog.fieldControls;
        this.object = inputDialog.object;
    }



    protected void createLabel(Font font, Composite composite, String fieldName) {
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

}
