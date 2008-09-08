package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tarlog.encoder.tool.ui.AbstractTableLabelProvider;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldWrapper;

public class CreatePropertiesDialog extends CreateField {

    private static final String   VALUE       = "Value";
    private static final String   NAME        = "Name";
    private static final String[] columnNames = { NAME, VALUE };

    private boolean               enabled     = true;

    public CreatePropertiesDialog(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }

    public Control createDialog(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {

        enabled = !(fieldWrapper.inputField.readonly() || !fieldWrapper.inputField.enabled());

        Group group = new Group(parent, SWT.NONE);
        group.setText(fieldName);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        group.setLayout(new GridLayout(2, false));

        final Table table = createTable(group);
        table.setEnabled(enabled);

        final List<Pair> tableInput = new ArrayList<Pair>();
        Object value = fieldWrapper.initialValue;

        if (value instanceof Properties) {
            Properties props = (Properties) value;
            for (Entry<Object, Object> entry : props.entrySet()) {
                tableInput.add(new Pair(entry));
            }
        }
        table.setData(tableInput);

        final TableViewer tableViewer = createTableViewer(table);

        createButtonsArea(group, tableViewer);

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                Properties properties = new Properties();
                for (Pair pair : tableInput) {
                    properties.setProperty(pair.key, pair.value);
                }
                return properties;
            }
        });
        return table;
    }

    private Table createTable(Group group) {
        final Table table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION
            | SWT.MULTI);

        TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText(columnNames[0]);
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText(columnNames[1]);
        tableColumn.setWidth(150);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = inputDialog.convertVerticalDLUsToPixels(60);
        table.setLayoutData(layoutData);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        return table;
    }

    private void createButtonsArea(Group group, final TableViewer tableViewer) {
        Composite buttonsComposite = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        buttonsComposite.setLayout(gridLayout);
        buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
            false));
        Button addButton = new Button(buttonsComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addButton.setText("Add");
        addButton.setEnabled(enabled);
        final Table table = tableViewer.getTable();
        @SuppressWarnings("unchecked")
        final List<Pair> tableInput = (List<Pair>) table.getData();
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                tableInput.add(new Pair());
                tableViewer.refresh();
                table.setSelection(tableInput.size() - 1);
            }
        });
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
        //        addButton.setSize(convertHorizontalDLUsToPixels(6));
        Button removeButton = new Button(buttonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setEnabled(enabled);
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
            false));
        // make the buttons equal size
        addButton.setSize(removeButton.getSize());
        removeButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                int selectionIndex = table.getSelectionIndex();
                if (selectionIndex >= 0 && selectionIndex < tableInput.size()) {
                    tableInput.remove(selectionIndex);
                    tableViewer.refresh();
                    table.setSelection(selectionIndex);
                }
            }
        });
        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                inputDialog.setFields();
                inputDialog.validateInput();
            }
        });
    }

    private TableViewer createTableViewer(final Table table) {
        final TableViewer tableViewer = new TableViewer(table);

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setLabelProvider(new AbstractTableLabelProvider() {

            @Override
            public String getColumnText(Object element, int columnIndex) {
                Pair entry = (Pair) element;
                return columnIndex == 0 ? entry.key : entry.value;
            }

        });
        @SuppressWarnings("unchecked")
        final List<Pair> tableInput = (List<Pair>) table.getData();
        tableViewer.setInput(tableInput);
        tableViewer.setColumnProperties(columnNames);

        tableViewer.setCellEditors(new TextCellEditor[] {
            new TextCellEditor(table), new TextCellEditor(table) });

        tableViewer.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                return true;
            }

            public Object getValue(Object element, String property) {
                Pair entry = (Pair) element;
                if (property.equals(NAME)) {
                    return entry.key;
                }
                return entry.value;
            }

            public void modify(Object element, String property, Object value) {
                TableItem item = (TableItem) element;
                Pair pair = (Pair) item.getData();
                if (property.equals(NAME)) {
                    pair.key = (String) value;
                } else {
                    pair.value = ((String) value);
                }
                tableViewer.refresh();
            }
        });
        return tableViewer;
    }

    class Pair {

        Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Pair(Entry<Object, Object> entry) {
            this((String) entry.getKey(), (String) entry.getValue());

        }

        public Pair() {
            this("", "");
        }
        String key;
        String value;
    }

}
