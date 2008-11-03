package tarlog.encoder.tool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class InputTextEditor extends Composite {

    private final Text text;

    public InputTextEditor(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));
        text = new Text(this, style | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
        getText().setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite bottomComposite = new Composite(this, SWT.NONE);
        bottomComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
        final Button showBytesButton = new Button(bottomComposite, SWT.CHECK);
        showBytesButton.setData(getText());
        getText().setData(showBytesButton);
        showBytesButton.setText("Show bytes");
        showBytesButton.addSelectionListener(new ShowBytesListener());
        Link link = new Link(bottomComposite, SWT.NONE);
        link.setText("<a>Clean</a>");
        link.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                getText().setText("");
            }
        });
    }

    public Text getText() {
        return text;
    }

}
