package tarlog.encoder.tool.ui.ddialog;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.osgi.framework.adaptor.FilePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.api.fields.InputDirectoryField;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.ui.AbstractSelectionListener;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

@SuppressWarnings("restriction")
public class CreateFileDialog extends CreateField {

    private static final String absolutePath = new File(".").getAbsolutePath();

    public CreateFileDialog(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    Control createDialog(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        createLabel(font, parent, fieldName);

        Composite composite = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        composite.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        composite.setLayout(layout);
        final Text text = new Text(composite, SWT.BORDER
            | (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE));
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        text.setLayoutData(layoutData);
        Object value = fieldWrapper.initialValue;
        if (value != null) {
            String fileName = ((File) value).getAbsolutePath();
            text.setText(fileName);
        }

        final InputFileField fileFieldAnnotation = fieldWrapper.field.getAnnotation(InputFileField.class);
        final InputDirectoryField directoryField = fieldWrapper.field.getAnnotation(InputDirectoryField.class);

        if (fileFieldAnnotation != null && directoryField != null) {
            throw new RuntimeException(String.format(
                "Only one of %s or %s is permitted.",
                InputFileField.class.getName(),
                InputDirectoryField.class.getName()));
        }

        final Button button = new Button(composite, SWT.PUSH);
        button.setEnabled(!fieldWrapper.inputField.readonly());
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        if (fileFieldAnnotation != null) {
            button.setText(fileFieldAnnotation.buttonText());
        } else if (directoryField != null) {
            button.setText(directoryField.buttonText());
        } else {
            button.setText("...");
        }
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                String filterPath = null;
                String file = null;
                if (fileFieldAnnotation != null) {
                    String[] filterExtensions = fileFieldAnnotation.filterExtensions().length > 0 ? fileFieldAnnotation.filterExtensions()
                        : null;
                    String[] filterNames = fileFieldAnnotation.filterNames().length > 0 ? fileFieldAnnotation.filterNames()
                        : null;
                    filterPath = fileFieldAnnotation.filterPath().equals("") ? null
                        : fileFieldAnnotation.filterPath();

                    FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
                    fileDialog.setFileName(text.getText());
                    fileDialog.setFilterExtensions(filterExtensions);
                    fileDialog.setFilterNames(filterNames);
                    fileDialog.setFilterPath(filterPath);
                    file = fileDialog.open();
                    if (file != null) {
                        String fileName = fileDialog.getFileName();
                        String path = fileFieldAnnotation.relative() ? new FilePath(
                            absolutePath).makeRelative(new FilePath(
                            fileDialog.getFilterPath()))
                            : fileDialog.getFilterPath();
                        String sep = File.separator;
                        if (path.contains("/")) {
                            if (File.separatorChar == '\\') {
                                sep = "\\\\";
                            }
                            path = path.replaceAll("/", sep);
                        }
                        file = path + File.separator + fileName;
                    }
                } else if (directoryField != null) {
                    filterPath = directoryField.filterPath().equals("") ? null
                        : directoryField.filterPath();
                    DirectoryDialog directoryDialog = new DirectoryDialog(shell);
                    directoryDialog.setText(text.getText());
                    directoryDialog.setFilterPath(filterPath);
                    file = directoryDialog.open();
                } else {
                    FileDialog fileDialog = new FileDialog(shell);
                    fileDialog.setFileName(text.getText());
                    file = fileDialog.open();
                }
                if (file != null) {
                    text.setText(file);
                }
            }
        });
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
                if (!text.getText().equals("")) {
                    return new File(text.getText());
                }
                return null;
            }
        });
        return composite;
    }

}
