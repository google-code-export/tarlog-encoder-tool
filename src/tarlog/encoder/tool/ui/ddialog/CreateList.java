package tarlog.encoder.tool.ui.ddialog;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
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
import tarlog.encoder.tool.api.fields.InputListField;
import tarlog.encoder.tool.api.fields.ListConverter;
import tarlog.encoder.tool.api.fields.InputListField.InputType;
import tarlog.encoder.tool.ui.AbstractSelectionListener;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreateList extends CreateField {

    private static final ListConverter<String> stringConverter = new StringConverter();
    private static final ListConverter<URL>    urlConverter    = new URLConverter();

    @SuppressWarnings("unchecked")
    private ListConverter                      converter       = null;
    private List                               list;

    public CreateList(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    @SuppressWarnings("unchecked")
    public List createField(Font font, Composite parent,
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
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        composite.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        composite.setLayout(layout);

        list = new List(composite,
            (fieldWrapper.inputField.readonly() ? SWT.READ_ONLY : SWT.NONE)
                | SWT.MULTI | SWT.BORDER);

        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = inputDialog.convertHorizontalDLUsToPixels(30);
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

        addButtons(composite, inputListFieldAnnotation);

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return converter.fromList(list.getItems());
            }
        });
        return list;
    }

    private void addButtons(Composite composite,
        InputListField inputListFieldAnnotation) {
        if (inputListFieldAnnotation == null) {
            createStringInputButton(composite);
        } else {
            for (InputType inputType : inputListFieldAnnotation.inputType()) {
                if (inputType == InputType.FILE) {
                    createFileInputButton(composite);
                } else if (inputType == InputType.FOLDER) {
                    createFolderInputButton(composite);
                } else if (inputType == InputType.STRING) {
                    createStringInputButton(composite);
                }
            }
        }
    }

    private void createFileInputButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        button.setText("Add File");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                String file = null;
                FileDialog fileDialog = new FileDialog(shell);
                file = fileDialog.open();
                if (file != null) {
                    list.add(file);
                }
            }
        });
    }

    private void createFolderInputButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        button.setText("Add Folder");
        button.addSelectionListener(new AbstractSelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                String file = null;
                DirectoryDialog directoryDialog = new DirectoryDialog(shell);
                file = directoryDialog.open();
                if (file != null) {
                    list.add(file);
                }
            }
        });
    }

    private void createStringInputButton(Composite composite) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
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
            return null;
        }
    }
}
