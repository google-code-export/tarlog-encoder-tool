package tarlog.encoder.tool.ui.inner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.AbstractSelectionListener;
import tarlog.encoder.tool.ui.EncoderTool;

public class SwapButton {

    public SwapButton(final Composite rightComposite,
        final EncoderTool encoderTool) {
        Button exchange = new Button(rightComposite, SWT.PUSH);
        GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gridData.verticalIndent = 50;
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
