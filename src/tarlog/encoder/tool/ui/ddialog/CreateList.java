package tarlog.encoder.tool.ui.ddialog;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.osgi.framework.adaptor.FilePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;

import tarlog.encoder.tool.Utils;
import tarlog.encoder.tool.api.fields.InputDirectoryField;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.api.fields.InputListField;
import tarlog.encoder.tool.api.fields.ListConverter;
import tarlog.encoder.tool.api.fields.InputListField.InputType;
import tarlog.encoder.tool.ui.AbstractSelectionListener;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreateList extends CreateField {

    private static final ListConverter<String> stringConverter = new StringConverter();
    private static final ListConverter<URL>    urlConverter    = new URLConverter();
    private static final String                absolutePath    = new File(".").getAbsolutePath();

    @SuppressWarnings("unchecked")
    private ListConverter                      converter       = null;
    private List                               list;

    public CreateList(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    @SuppressWarnings("unchecked")
    public Composite createField(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        createLabel(font, parent, fieldName);

        InputListField inputListFieldAnnotation = fieldWrapper.field.getAnnotation(InputListField.class);
        if (inputListFieldAnnotation != null) {
            Class<? extends ListConverter> converterClass = inputListFieldAnnotation.converter();
            if (converterClass != ListConverter.class) {
                try {
                    converter = converterClass.newInstance();
                } catch (InstantiationException e) {
                    Utils.showException(shell, e);
                } catch (IllegalAccessException e) {
                    Utils.showException(shell, e);
                }
            }
        }
        if (converter == null) {
            Class<?> type = fieldWrapper.field.getType().getComponentType();
            if (type == String.class) {
                converter = stringConverter;
            } else if (type == URL.class) {
                converter = urlConverter;
            } else {
                throw new RuntimeException(
                    "Converter must be defined for this array type: "
                        + type.getName());
            }
        }

        Composite composite = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        composite.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        composite.setLayout(layout);

        list = new List(composite,
            (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE)
                | SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = inputDialog.convertVerticalDLUsToPixels(50);
        layoutData.widthHint = inputDialog.convertHorizontalDLUsToPixels(90);
        list.setLayoutData(layoutData);

        Object[] value = (Object[]) fieldWrapper.initialValue;
        if (value != null) {
            list.setItems(converter.toList(value));
        }

        //        if (inputDialog.validator != null) {
        //            list.addModifyListener(new ModifyListener() {
        //
        //                public void modifyText(ModifyEvent e) {
        //                    inputDialog.setFields();
        //                    inputDialog.validateInput();
        //                }
        //            });
        //        }

        addButtons(composite, fieldWrapper);

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return converter.fromList(list.getItems());
            }
        });
        return composite;
    }

    private void addButtons(Composite parent, FieldWrapper fieldWrapper) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, false, true);
        composite.setLayoutData(layoutData);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        InputListField inputListField = fieldWrapper.field.getAnnotation(InputListField.class);
        InputDirectoryField inputDirectoryField = fieldWrapper.field.getAnnotation(InputDirectoryField.class);
        InputFileField inputFileField = fieldWrapper.field.getAnnotation(InputFileField.class);

        if (inputDirectoryField == null && inputListField == null
            && inputFileField == null) {
            createStringInputButton(composite);
        } else {
            if (inputDirectoryField != null) {
                createFolderInputButton(composite, inputDirectoryField);
            }
            if (inputFileField != null) {
                createFileInputButton(composite, inputFileField);
            }
            if (inputListField != null) {
                for (InputType inputType : inputListField.inputType()) {
                    if (inputType == InputType.UP) {
                        createUpButton(composite);
                    } else if (inputType == InputType.DOWN) {
                        createDownButton(composite);
                    } else if (inputType == InputType.STRING) {
                        createStringInputButton(composite);
                    }
                }
            }
        }
        createRemoveButton(composite);
    }

    private void createUpButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Up");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = list.getSelectionIndex();
                if (selectionIndex > 0) {
                    String item = list.getItem(selectionIndex);
                    list.setItem(selectionIndex,
                        list.getItem(selectionIndex - 1));
                    list.setItem(selectionIndex - 1, item);
                    list.setSelection(selectionIndex - 1);
                }
            }
        });
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private void createDownButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Down");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = list.getSelectionIndex();
                if (selectionIndex != -1
                    && selectionIndex < list.getItemCount() - 1) {
                    String item = list.getItem(selectionIndex);
                    list.setItem(selectionIndex,
                        list.getItem(selectionIndex + 1));
                    list.setItem(selectionIndex + 1, item);
                    list.setSelection(selectionIndex + 1);
                }
            }
        });
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private void createRemoveButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Remove");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                int selectionIndex;
                while ((selectionIndex = list.getSelectionIndex()) != -1) {
                    list.remove(selectionIndex);
                }
            }
        });
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private void createFileInputButton(Composite composite,
        final InputFileField inputFileField) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText(inputFileField.buttonText());
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                String file = null;
                FileDialog fileDialog = new FileDialog(shell, SWT.OPEN
                    | SWT.MULTI);
                String[] filterExtensions = inputFileField.filterExtensions().length > 0 ? inputFileField.filterExtensions()
                    : null;
                String[] filterNames = inputFileField.filterNames().length > 0 ? inputFileField.filterNames()
                    : null;
                String filterPath = inputFileField.filterPath().equals("") ? null
                    : inputFileField.filterPath();
                fileDialog.setFilterExtensions(filterExtensions);
                fileDialog.setFilterNames(filterNames);
                fileDialog.setFilterPath(filterPath == null ? absolutePath
                    : filterPath);

                file = fileDialog.open();
                if (file != null) {
                    String[] fileNames = fileDialog.getFileNames();
                    String path = inputFileField.relative() ? new FilePath(
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
                    for (String fileName : fileNames) {
                        fileName = path + File.separator + fileName;
                        list.add(fileName);
                    }
                }
            }
        });
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private void createFolderInputButton(Composite composite,
        final InputDirectoryField inputDirectoryField) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText(inputDirectoryField.buttonText());
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                String file = null;
                DirectoryDialog directoryDialog = new DirectoryDialog(shell);
                String filterPath = inputDirectoryField.filterPath().equals("") ? null
                    : inputDirectoryField.filterPath();
                directoryDialog.setFilterPath(filterPath == null ? absolutePath
                    : filterPath);
                file = directoryDialog.open();
                if (file != null) {
                    list.add(file);
                }
            }
        });
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private void createStringInputButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText("Add");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                InputDialog inputDialog = new InputDialog(shell, "Add string",
                    "Enter string", "", new IInputValidator() {

                        public String isValid(String newText) {
                            if (newText.trim().equals("")) {
                                return "String cannot be empty";
                            }
                            //                            Matcher matcher = WORD_PATTERN.matcher(newText);
                            //                            if (!matcher.matches()) {
                            //                                return "Group name must be a word";
                            //                            }
                            //                            if (encodersStore.getGroup(newText) != null) {
                            //                                return "Group name must be unique";
                            //                            }
                            return null;
                        }
                    });
                int rc = inputDialog.open();
                if (rc == Dialog.OK) {
                    list.add(inputDialog.getValue());
                }
            }
        });
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private static class StringConverter implements ListConverter<String> {

        public String[] fromList(String[] list) {
            return list;
        }

        public String[] toList(String[] list) {
            return (String[]) list;
        }
    }

    private static class URLConverter implements ListConverter<URL> {

        public URL[] fromList(String[] list) {
            if (list == null) {
                return null;
            }
            URL[] array = new URL[list.length];
            for (int i = 0; i < list.length; i++) {
                try {
                    File file = new File(list[i]);
                    if (file.isFile() || file.isDirectory()) {
                        array[i] = file.toURL();
                    } else {
                        array[i] = new URL(list[i]);
                    }
                } catch (MalformedURLException e) {

                    throw new RuntimeException(e);
                }
            }
            return array;
        }

        public String[] toList(URL[] list) {
            if (list == null) {
                return null;
            }
            String[] array = new String[list.length];
            for (int i = 0; i < list.length; i++) {
                array[i] = list[i].toString();
            }
            return array;
        }
    }
}
