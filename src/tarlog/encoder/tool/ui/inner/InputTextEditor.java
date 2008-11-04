package tarlog.encoder.tool.ui.inner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.GridComposite;

public class InputTextEditor extends GridComposite {

    private final Text          text;
    private final GridComposite bottomComposite;

    public GridComposite getBottomComposite() {
        return bottomComposite;
    }

    public InputTextEditor(Composite parent, int style) {
        super(parent, style);
//        removeMargins();
        setLayoutData(new GridData(GridData.FILL_BOTH));
        text = new Text(this, style | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
        getText().setLayoutData(new GridData(GridData.FILL_BOTH));
        bottomComposite = new GridComposite(this, SWT.NONE, 2);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
            false));
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

    public final Text getText() {
        return text;
    }

}
