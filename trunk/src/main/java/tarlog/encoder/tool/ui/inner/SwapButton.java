package tarlog.encoder.tool.ui.inner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.EncoderUI;
import tarlog.encoder.tool.ui.GridComposite;
import tarlog.ui.swt.ddialog.utils.AbstractSelectionListener;

public class SwapButton extends GridComposite{

    public SwapButton(final Composite parent,
        final EncoderUI encoderTool) {
        super(parent);
        removeMargins();
        setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        Button exchange = new Button(this, SWT.PUSH);
        GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        exchange.setLayoutData(gridData);
        exchange.setText("<>");
        exchange.setToolTipText("Swap input");
        exchange.addSelectionListener(new AbstractSelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Text sourceText = encoderTool.getSourceText();
                Text targetText = encoderTool.getTargetText();

                Button sourceBytesButton = (Button) sourceText.getData();
                Button targetBytesButton = (Button) targetText.getData();

                String oldText = sourceText.getText();
                boolean oldBytesButtonStatus = sourceBytesButton.getSelection();

                sourceText.setText(targetText.getText());
                sourceBytesButton.setSelection(targetBytesButton.getSelection());
                targetText.setText(oldText);
                targetBytesButton.setSelection(oldBytesButtonStatus);
            }
        });
    }
}
