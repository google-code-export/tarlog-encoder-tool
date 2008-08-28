package tarlog.encoder.tool.ui.ddialog;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Map.Entry;

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

import tarlog.encoder.tool.api.AbstractEncoder.FieldWrapper;
import tarlog.encoder.tool.ui.AbstractStructuredContentProvider;
import tarlog.encoder.tool.ui.AbstractTableLabelProvider;
import tarlog.encoder.tool.ui.ddialog.DynamicInputDialog.FieldControl;

public class CreatePropertiesDialog extends CreateField {




    private static final String VALUE = "Value";
    private static final String NAME  = "Name";

    public CreatePropertiesDialog(DynamicInputDialog inputDialog) {
        super(inputDialog);
    }


    public Control createDialog(Font font, Composite parent,
        final FieldWrapper fieldWrapper, String fieldName) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(fieldName);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        group.setLayout(new GridLayout(2, false));

        final Table table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION
            | SWT.MULTI);
        final String[] columnNames = { NAME, VALUE };

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

        final TableViewer tableViewer = new TableViewer(table);

        tableViewer.setContentProvider(new AbstractStructuredContentProvider() {

            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof Properties) {
                    return ((Properties) inputElement).entrySet().toArray();
                }
                return new Object[0];
            }
        });

        tableViewer.setLabelProvider(new AbstractTableLabelProvider() {

            @Override
            public String getColumnText(Object element, int columnIndex) {
                @SuppressWarnings("unchecked")
                Entry<String, String> entry = (Entry<String, String>) element;
                return columnIndex == 0 ? entry.getKey() : entry.getValue();
            }

        });

        Object value = getValue(fieldWrapper.field);
        final Properties tableInput = value == null ? new Properties()
            : (Properties) value;

        table.setData(tableInput);
        tableViewer.setInput(tableInput);
        tableViewer.setColumnProperties(columnNames);

        tableViewer.setCellEditors(new TextCellEditor[] {
            new TextCellEditor(table), new TextCellEditor(table) });

        tableViewer.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                return true;
            }

            public Object getValue(Object element, String property) {
                @SuppressWarnings("unchecked")
                Entry<String, String> entry = (Entry<String, String>) element;
                if (property.equals(NAME)) {
                    return entry.getKey();
                }
                return entry.getValue();
            }

            public void modify(Object element, String property, Object value) {
                TableItem item = (TableItem) element;
                @SuppressWarnings("unchecked")
                Entry<String, String> entry = (Entry<String, String>) item.getData();
                if (property.equals(NAME)) {
                    Object keyValue = tableInput.remove(entry.getKey());
                    tableInput.setProperty((String) value, (String) keyValue);
                } else {
                    entry.setValue((String) value);
                }
                tableViewer.refresh();
            }
        });

        Composite buttonsComposite = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        buttonsComposite.setLayout(gridLayout);
        buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.UP, false,
            false));
        Button addButton = new Button(buttonsComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                tableInput.setProperty("", "");
                table.setSelection(tableInput.size() - 1);
                tableViewer.refresh();
            }
        });

        //        addButton.setSize(convertHorizontalDLUsToPixels(6));
        Button removeButton = new Button(buttonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        // make the buttons equal size
        addButton.setSize(removeButton.getSize());
        removeButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                System.out.println(".widgetSelected()");
            }
        });

        fieldControls.add(new FieldControl() {

            public Field getField() {
                return fieldWrapper.field;
            }

            public Object getValue() {
                return table.getData();
            }
        });
        return table;
    }

}
