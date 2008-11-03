package tarlog.encoder.tool.ui.inner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.AbstractSelectionListener;
import tarlog.encoder.tool.ui.EncoderTool;

public class HistoryManager {

    private List<Step>                         history = new ArrayList<Step>();
    private final org.eclipse.swt.widgets.List historyList;

    public HistoryManager(final Composite rightComposite,
        final EncoderTool encoderTool) {
        Label label = new Label(rightComposite, SWT.NONE);
        GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
        gridData.verticalIndent = 15;
        label.setLayoutData(gridData);
        label.setText("History:");
        historyList = new org.eclipse.swt.widgets.List(rightComposite,
            SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        historyList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        historyList.addSelectionListener(new AbstractSelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = historyList.getSelectionIndex();
                if (selectionIndex != -1) {
                    Step step = history.get(selectionIndex);
                    Text sourceText = encoderTool.getSourceText();
                    Text targetText = encoderTool.getTargetText();

                    Button sourceBytesButton = (Button) sourceText.getData();
                    Button targetBytesButton = (Button) targetText.getData();

                    sourceText.setText(step.sourceText);
                    sourceBytesButton.setSelection(step.sourceBytesButtonSelection);
                    targetText.setText(step.targetText);
                    targetBytesButton.setSelection(step.targetBytesButtonSelection);
                }
            }
        });
    }

    public void addStep(String name, Text sourceText, Text targetText) {
        Button sourceBytesButton = (Button) sourceText.getData();
        Button targetBytesButton = (Button) targetText.getData();
        boolean sourceBytesButtonSelection = sourceBytesButton.getSelection();
        boolean targetBytesButtonSelection = targetBytesButton.getSelection();

        history.add(new Step(name, sourceText.getText(), targetText.getText(),
            sourceBytesButtonSelection, targetBytesButtonSelection));
        historyList.add(name);
    }

    private class Step {

        String  name;
        String  sourceText;
        String  targetText;
        boolean sourceBytesButtonSelection;
        boolean targetBytesButtonSelection;

        Step(String name, String sourceText, String targetText,
            boolean sourceBytesButtonSelection,
            boolean targetBytesButtonSelection) {
            super();
            this.name = name;
            this.sourceText = sourceText;
            this.targetText = targetText;
            this.sourceBytesButtonSelection = sourceBytesButtonSelection;
            this.targetBytesButtonSelection = targetBytesButtonSelection;
        }
    }
}
