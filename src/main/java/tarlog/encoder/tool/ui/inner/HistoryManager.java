/*******************************************************************************
 *     Copyright 2009 Michael Elman (http://tarlogonjava.blogspot.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *     
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
package tarlog.encoder.tool.ui.inner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.ui.EncoderUI;
import tarlog.ui.swt.ddialog.utils.AbstractSelectionListener;

public class HistoryManager {

    private List<Step>                         history          = new ArrayList<Step>();
    private final org.eclipse.swt.widgets.List historyList;
    private final int                          MAX_HISTORY_SIZE = 50;

    public HistoryManager(final Composite rightComposite,
        final EncoderUI encoderTool) {
        Label label = new Label(rightComposite, SWT.NONE);
        GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
        label.setLayoutData(gridData);
        label.setText("History:");
        historyList = new org.eclipse.swt.widgets.List(rightComposite,
            SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        historyList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create menu
        Menu menu = new Menu(historyList);
        historyList.setMenu(menu);
        final MenuItem removeMenuItem = new MenuItem(menu, SWT.NONE);
        removeMenuItem.setText("Remove");
        removeMenuItem.addSelectionListener(new AbstractSelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = historyList.getSelectionIndex();
                remove(selectionIndex);
                historyList.setTopIndex(historyList.getItemCount() > 0 ? historyList.getItemCount() - 1 : 0);
            }
        });

        final MenuItem clearMenuItem = new MenuItem(menu, SWT.NONE);
        clearMenuItem.setText("Clear");
        clearMenuItem.addSelectionListener(new AbstractSelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                history.clear();
                historyList.removeAll();
            }
        });

        menu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {
                int selectionIndex = historyList.getSelectionIndex();
                if (selectionIndex != -1) {
                    removeMenuItem.setEnabled(true);
                } else {
                    removeMenuItem.setEnabled(false);
                }
                if (historyList.getItemCount() > 0) {
                    clearMenuItem.setEnabled(true);
                } else {
                    clearMenuItem.setEnabled(false);
                }
            }
        });

        historyList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
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
        if (history.size() >= MAX_HISTORY_SIZE) {
            remove(0);
        }
        Button sourceBytesButton = (Button) sourceText.getData();
        Button targetBytesButton = (Button) targetText.getData();
        boolean sourceBytesButtonSelection = sourceBytesButton.getSelection();
        boolean targetBytesButtonSelection = targetBytesButton.getSelection();

        history.add(new Step(name, sourceText.getText(), targetText.getText(),
            sourceBytesButtonSelection, targetBytesButtonSelection));
        addToHistoryList(history.size() - 1, name);
        historyList.setTopIndex(historyList.getItemCount() > 0 ? historyList.getItemCount() - 1 : 0);
    }

    private void addToHistoryList(int index, String name) {
        historyList.add(String.format("%d. %s", index + 1, name), index);
    }

    private void remove(int selectionIndex) {
        if (selectionIndex != -1) {
            history.remove(selectionIndex);
            historyList.removeAll();
            for (int i = 0; i < history.size(); ++i) {
                addToHistoryList(i, history.get(i).name);
            }
        }
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
