package tarlog.encoder.tool.ui.ddialog;

import java.io.File;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.api.fields.TextField;

public class DynamicInputDialog extends Dialog {

    final Object             object;
    final List<FieldWrapper> fields;
    final String             title;
    final List<FieldControl> fieldControls;

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
                    control = new CreateCombo(this).createComboField(parent.getFont(),
                        composite, fieldWrapper, fieldName, fieldType);
                } else {
                    control = new CreateText(this).createField(parent.getFont(),
                        composite, fieldWrapper, fieldName);
                }
            } else if (fieldType.equals(Boolean.class)
                || fieldType.equals(boolean.class)) {
                control = new CreateCheckButton(this).createDialog(parent.getFont(),
                    composite, fieldWrapper, fieldName);
            } else if (fieldType.isEnum()) {
                control = new CreateCombo(this).createComboFieldOfEnum(
                    parent.getFont(), composite, fieldWrapper, fieldName,
                    fieldType);
            } else if (fieldType.equals(Integer.class)) {
                control = new CreateSpinner(this).createDialog(parent.getFont(),
                    composite, fieldWrapper, fieldName);
            } else if (fieldType.equals(File.class)) {
                control = new CreateFileDialog(this).createDialog(parent.getFont(),
                    composite, fieldWrapper, fieldName);
            } else if (fieldType.equals(Properties.class)) {
                control = new CreatePropertiesDialog(this).createDialog(parent.getFont(),
                    composite, fieldWrapper, fieldName);
            } else {
                throw new RuntimeException("Unsupported type : "
                    + fieldType.getName());
            }

            control.setEnabled(fieldWrapper.inputField.enabled());
        }
        applyDialogFont(composite);
        return composite;
    }
    
    @Override
    public int convertHorizontalDLUsToPixels(int dlus) {
        return super.convertHorizontalDLUsToPixels(dlus);
    }
    
    @Override
    public int convertVerticalDLUsToPixels(int dlus) {
        return super.convertVerticalDLUsToPixels(dlus);
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
