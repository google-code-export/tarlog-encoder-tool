package tarlog.encoder.tool.ui.ddialog;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
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

import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.api.fields.InputFileField.FileFieldType;
import tarlog.encoder.tool.ui.AbstractSelectionListener;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;

public class CreateFileDialog extends CreateField {



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
        Object value = getValue(fieldWrapper.field);
        if (value != null) {
            String fileName = ((File) value).getAbsolutePath();
            text.setText(fileName);
        }

        final InputFileField fileFieldAnnotation = fieldWrapper.field.getAnnotation(InputFileField.class);

        final Button button = new Button(composite, SWT.PUSH);
        button.setEnabled(!fieldWrapper.inputField.readonly());
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        button.setText("...");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                FileFieldType fileFieldType = FileFieldType.file;
                String[] filterExtensions = null;
                String[] filterNames = null;
                String filterPath = null;
                if (fileFieldAnnotation != null) {
                    fileFieldType = fileFieldAnnotation.fileFieldType();
                    filterExtensions = fileFieldAnnotation.filterExtensions().length > 0 ? fileFieldAnnotation.filterExtensions()
                        : null;
                    filterNames = fileFieldAnnotation.filterNames().length > 0 ? fileFieldAnnotation.filterNames()
                        : null;
                    filterPath = fileFieldAnnotation.filterPath().equals("") ? null
                        : fileFieldAnnotation.filterPath();
                }
                String file = null;
                if (fileFieldType == FileFieldType.file) {
                    FileDialog fileDialog = new FileDialog(shell);
                    fileDialog.setFileName(text.getText());
                    fileDialog.setFilterExtensions(filterExtensions);
                    fileDialog.setFilterNames(filterNames);
                    fileDialog.setFilterPath(filterPath);
                    file = fileDialog.open();
                } else {
                    DirectoryDialog directoryDialog = new DirectoryDialog(shell);
                    directoryDialog.setText(text.getText());
                    directoryDialog.setFilterPath(filterPath);
                    file = directoryDialog.open();
                }
                if (file != null) {
                    text.setText(file);
                }
            }
        });

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
