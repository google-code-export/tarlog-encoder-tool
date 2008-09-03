package tarlog.encoder.tool.ui.ddialog;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.TextField;
import tarlog.encoder.tool.api.fields.Validator;

public class DynamicInputDialog extends Dialog {

    final Object             object;
    final List<FieldWrapper> fields;
    final String             title;
    final List<FieldControl> fieldControls;
    final Validator          validator;
    private Text             errorMessageText;
    private String           errorMessage;

    public DynamicInputDialog(Shell parentShell, String title, Object object) {
        this(parentShell, title, object, getInputFields(object));
    }

    public static List<FieldWrapper> getInputFields(Object object) {
        List<FieldWrapper> fields = new ArrayList<FieldWrapper>();
        Class<?> clazz = object.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                InputField inputField = field.getAnnotation(InputField.class);
                if (inputField != null) {
                    fields.add(new FieldWrapper(object, field, inputField));
                }
            }
            clazz = clazz.getSuperclass();
        }
        Collections.sort(fields, new Comparator<FieldWrapper>() {

            public int compare(FieldWrapper o1, FieldWrapper o2) {
                return o1.inputField.order() - o2.inputField.order();
            }
        });
        return fields;
    }

    public DynamicInputDialog(Shell parentShell, String title, Object object,
        List<FieldWrapper> fields) {
        super(parentShell);
        this.object = object;
        this.fields = fields;
        this.title = title;
        this.fieldControls = new ArrayList<FieldControl>(fields.size());
        this.validator = object instanceof Validator ? (Validator) object
            : null;
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
                    control = new CreateCombo(this).createComboField(
                        parent.getFont(), composite, fieldWrapper, fieldName,
                        fieldType);
                } else {
                    control = new CreateText(this).createField(
                        parent.getFont(), composite, fieldWrapper, fieldName);
                }
            } else if (fieldType.equals(Boolean.class)
                || fieldType.equals(boolean.class)) {
                control = new CreateCheckButton(this).createDialog(
                    parent.getFont(), composite, fieldWrapper, fieldName);
            } else if (fieldType.isEnum()) {
                control = new CreateCombo(this).createComboFieldOfEnum(
                    parent.getFont(), composite, fieldWrapper, fieldName,
                    fieldType);
            } else if (fieldType.equals(Integer.class)) {
                control = new CreateSpinner(this).createDialog(
                    parent.getFont(), composite, fieldWrapper, fieldName);
            } else if (fieldType.equals(File.class)) {
                control = new CreateFileDialog(this).createDialog(
                    parent.getFont(), composite, fieldWrapper, fieldName);
            } else if (fieldType.equals(Properties.class)) {
                control = new CreatePropertiesDialog(this).createDialog(
                    parent.getFont(), composite, fieldWrapper, fieldName);
            } else {
                throw new RuntimeException("Unsupported type : "
                    + fieldType.getName());
            }

            control.setEnabled(fieldWrapper.inputField.enabled());
        }
        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
            | GridData.HORIZONTAL_ALIGN_FILL));
        errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(
            SWT.COLOR_WIDGET_BACKGROUND));
        // Set the error message text
        // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
        setErrorMessage(errorMessage);

        applyDialogFont(composite);
        return composite;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        validateInput();
        return contents;
    }
    
    protected void validateInput() {
        String errorMessage = null;
        if (validator != null) {
            errorMessage = validator.isValid();
        }
        // Bug 16256: important not to treat "" (blank error) the same as null
        // (no error)
        setErrorMessage(errorMessage);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        if (errorMessageText != null && !errorMessageText.isDisposed()) {
            errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
            // Disable the error message text control if there is no error, or
            // no error text (empty or whitespace only).  Hide it also to avoid
            // color change.
            boolean hasError = errorMessage != null
                && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
            errorMessageText.setEnabled(hasError);
            errorMessageText.setVisible(hasError);
            errorMessageText.getParent().update();

            Control button = getButton(IDialogConstants.OK_ID);
            if (button != null) {
                button.setEnabled(errorMessage == null);
            }
        }
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
            setFields();
        } else {
            restoreFields();
        }
        super.buttonPressed(buttonId);
    }

    private void restoreFields() {
        for (FieldWrapper fieldWrapper : fields) {
            Object value = fieldWrapper.initialValue;
            try {
                final Field f = fieldWrapper.field;
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

    protected void setFields() {
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

    interface FieldControl {

        Field getField();

        Object getValue();
    }

    public static class FieldWrapper {

        public InputField inputField;
        public Field      field;
        public Object     initialValue;

        private FieldWrapper(Object obj, Field field, InputField inputField) {
            this.field = field;
            this.inputField = inputField;
            this.initialValue = getValue(obj, field);
        }

        protected Object getValue(Object object, Field field) {
            try {
                AccessibleObject.setAccessible(new Field[] { field }, true);
                //            field.setAccessible(true);
                return field.get(object);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
